/**
 * Offline sync engine for the PWA.
 *
 * Drains the pending sync queue by:
 *  1. Uploading local photo blobs to Supabase Storage (`user-photos` bucket)
 *  2. Upserting/deleting database rows in the corresponding Supabase table
 *  3. Removing queue entries on success
 *
 * Triggers:
 *  - Online event (navigator.onLine changes to true)
 *  - Background Sync API (if supported by the browser)
 *  - Nightly 4 AM timer (in the user's timezone)
 *  - Manual trigger via useOfflineSync().drainNow()
 */

import { useCallback, useEffect, useState } from "react";
import { supabase, supabaseUrl, supabaseAnonKey } from "@/lib/supabase";
import {
  type PendingSyncEntry,
  type SyncTable,
  getPendingSync,
  removeSyncEntry,
  incrementSyncAttempts,
  getOfflineRecords,
  deleteOfflineRecord,
  getPhotoBlob,
  removePhotoBlob,
  tableToStore,
  SupabaseTables,
} from "@/lib/offline-store";
import { useAuth } from "@/hooks/useAuth";

const STORAGE_BUCKET = "user-photos";
const MAX_ATTEMPTS = 10;

/** Upload a photo blob to Supabase Storage and return the public URL. */
async function uploadPhoto(
  blob: Blob,
  userId: string,
  ext: string = "jpg",
): Promise<string | null> {
  try {
    const filename = `${userId}/${crypto.randomUUID()}.${ext}`;
    const { error } = await supabase.storage
      .from(STORAGE_BUCKET)
      .upload(filename, blob, {
        contentType: blob.type || "image/jpeg",
        upsert: false,
      });

    if (error) {
      console.warn("[offline-sync] Photo upload failed:", error.message);
      return null;
    }

    const { data: urlData } = supabase.storage
      .from(STORAGE_BUCKET)
      .getPublicUrl(filename);

    return urlData.publicUrl;
  } catch (e) {
    console.warn("[offline-sync] Photo upload exception:", e);
    return null;
  }
}

/** Push a single record to Supabase (upsert by id). */
async function pushRecord(
  table: SyncTable,
  record: Record<string, unknown>,
  userId: string,
): Promise<boolean> {
  try {
    const payload = { ...record, user_id: userId };
    const { error } = await supabase
      .from(table)
      .upsert(payload, { onConflict: "id" });

    if (error) {
      console.warn(`[offline-sync] Push to ${table} failed:`, error.message);
      return false;
    }
    return true;
  } catch (e) {
    console.warn(`[offline-sync] Push to ${table} exception:`, e);
    return false;
  }
}

/** Delete a single record from Supabase by id. */
async function deleteRecord(
  table: SyncTable,
  recordId: string,
): Promise<boolean> {
  try {
    const { error } = await supabase
      .from(table)
      .delete()
      .eq("id", recordId);

    if (error) {
      console.warn(`[offline-sync] Delete from ${table} failed:`, error.message);
      return false;
    }
    return true;
  } catch (e) {
    console.warn(`[offline-sync] Delete from ${table} exception:`, e);
    return false;
  }
}

/**
 * Process a single sync queue entry.
 * Returns true on success, false on failure.
 */
async function processEntry(
  entry: PendingSyncEntry,
  userId: string,
): Promise<boolean> {
  const storeName = tableToStore(entry.table);

  if (entry.op === "delete") {
    const success = await deleteRecord(entry.table, entry.recordId);
    if (success) {
      await deleteOfflineRecord(storeName, entry.recordId);
    }
    return success;
  }

  // Upsert: fetch the offline record, upload any local photos, then push
  const records = await getOfflineRecords<Record<string, unknown>>(storeName);
  const record = records.find((r) => r.id === entry.recordId);
  if (!record) {
    // Record was deleted locally before sync — nothing to push
    return true;
  }

  // Upload any local photo blobs referenced in the record
  const photoFields: Record<string, string> = {
    [SupabaseTables.CAPTURES]: "image_urls",
    [SupabaseTables.SAVED_IMAGES]: "image_url",
    [SupabaseTables.FIELD_JOURNAL]: "photo_urls",
  };

  const photoField = photoFields[entry.table];
  if (photoField && record[photoField]) {
    if (entry.table === SupabaseTables.SAVED_IMAGES) {
      // Single image_url field
      const url = record[photoField] as string;
      if (url.startsWith("blob:") || url.startsWith("local:")) {
        const blob = await getPhotoBlob(url);
        if (blob) {
          const remoteUrl = await uploadPhoto(blob, userId);
          if (remoteUrl) {
            record[photoField] = remoteUrl;
            await removePhotoBlob(url);
          }
        }
      }
    } else {
      // Array field (image_urls / photo_urls)
      const urls = record[photoField] as string[];
      const uploadedUrls: string[] = [];
      for (const url of urls) {
        if (url.startsWith("blob:") || url.startsWith("local:")) {
          const blob = await getPhotoBlob(url);
          if (blob) {
            const remoteUrl = await uploadPhoto(blob, userId);
            uploadedUrls.push(remoteUrl || url);
            if (remoteUrl) await removePhotoBlob(url);
          } else {
            uploadedUrls.push(url);
          }
        } else {
          uploadedUrls.push(url);
        }
      }
      record[photoField] = uploadedUrls;
    }
  }

  // Push the record to Supabase
  const success = await pushRecord(entry.table, record, userId);
  if (success) {
    // Remove the offline record (it's now in Supabase)
    await deleteOfflineRecord(storeName, entry.recordId);
  }
  return success;
}

/** Drain the entire pending sync queue. Returns the number of successfully synced items. */
export async function drainSyncQueue(userId: string): Promise<number> {
  const pending = await getPendingSync();
  if (pending.length === 0) return 0;

  console.info(`[offline-sync] Draining ${pending.length} pending items`);
  let successCount = 0;

  for (const entry of pending) {
    const success = await processEntry(entry, userId);

    if (success) {
      await removeSyncEntry(entry.id);
      successCount++;
    } else {
      await incrementSyncAttempts(entry.id);
      // Drop entries that have failed too many times
      if (entry.attempts + 1 >= MAX_ATTEMPTS) {
        console.warn(
          `[offline-sync] Dropping ${entry.table}/${entry.recordId} after ${MAX_ATTEMPTS} failed attempts`,
        );
        await removeSyncEntry(entry.id);
      }
    }
  }

  console.info(
    `[offline-sync] Drain complete: ${successCount}/${pending.length} synced`,
  );
  return successCount;
}

/**
 * React hook that manages the offline sync lifecycle.
 *
 * - Listens for online/offline events and triggers a drain when connectivity is restored
 * - Schedules a nightly 4 AM drain timer (in the user's timezone)
 * - Exposes the pending count and a manual drainNow() function
 */
export function useOfflineSync() {
  const { user } = useAuth();
  const [pendingCount, setPendingCount] = useState(0);
  const [isSyncing, setIsSyncing] = useState(false);
  const [lastSyncAt, setLastSyncAt] = useState<number | null>(null);

  // Refresh the pending count
  const refreshCount = useCallback(async () => {
    try {
      const { getPendingCount } = await import("@/lib/offline-store");
      const count = await getPendingCount();
      setPendingCount(count);
    } catch {
      // IndexedDB might not be available
    }
  }, []);

  // Drain the queue now
  const drainNow = useCallback(async () => {
    if (!user || isSyncing) return;
    setIsSyncing(true);
    try {
      await drainSyncQueue(user.id);
      setLastSyncAt(Date.now());
      await refreshCount();
    } catch (e) {
      console.warn("[offline-sync] Drain failed:", e);
    } finally {
      setIsSyncing(false);
    }
  }, [user, isSyncing, refreshCount]);

  // Listen for online events — drain immediately when connectivity is restored
  useEffect(() => {
    const handleOnline = () => {
      console.info("[offline-sync] Connectivity restored — triggering sync");
      drainNow();
    };
    window.addEventListener("online", handleOnline);
    return () => window.removeEventListener("online", handleOnline);
  }, [drainNow]);

  // Listen for Background Sync messages from the service worker
  useEffect(() => {
    const handleMessage = (event: MessageEvent) => {
      if (event.data?.type === "OFFLINE_SYNC_TRIGGER") {
        console.info("[offline-sync] Background Sync triggered — draining queue");
        drainNow();
      }
    };
    navigator.serviceWorker?.addEventListener("message", handleMessage);
    return () => navigator.serviceWorker?.removeEventListener("message", handleMessage);
  }, [drainNow]);

  // Register a Background Sync tag (if supported) so the browser drains
  // the queue even if the tab is closed
  useEffect(() => {
    if (!("serviceWorker" in navigator && "SyncManager" in window)) return;
    navigator.serviceWorker.ready
      .then((registration) =>
        (registration as unknown as { sync: { register: (tag: string) => Promise<void> } })
          .sync.register("rockscout-offline-sync"),
      )
      .catch(() => {
        // Background Sync not supported — the online event + nightly timer handle it
      });
  }, []);

  // Nightly 4 AM timer (in the user's timezone)
  useEffect(() => {
    if (!user) return;

    const computeMsUntil4Am = (): number => {
      const now = new Date();
      const next = new Date();
      next.setHours(4, 0, 0, 0);
      if (next <= now) {
        next.setDate(next.getDate() + 1);
      }
      return next.getTime() - now.getTime();
    };

    let timeoutId: ReturnType<typeof setTimeout>;
    let intervalId: ReturnType<typeof setInterval>;

    const scheduleNightlyDrain = () => {
      const msUntil4Am = computeMsUntil4Am();
      timeoutId = setTimeout(() => {
        drainNow();
        // Re-schedule for the next day (24h interval)
        intervalId = setInterval(drainNow, 24 * 60 * 60 * 1000);
      }, msUntil4Am);
    };

    scheduleNightlyDrain();

    return () => {
      clearTimeout(timeoutId);
      clearInterval(intervalId);
    };
  }, [user, drainNow]);

  // Refresh pending count on mount and periodically
  useEffect(() => {
    refreshCount();
    const intervalId = setInterval(refreshCount, 30_000);
    return () => clearInterval(intervalId);
  }, [refreshCount]);

  // Drain on mount if online and there are pending items
  useEffect(() => {
    if (navigator.onLine && user) {
      drainNow();
    }
  }, [user, drainNow]);

  return {
    pendingCount,
    isSyncing,
    lastSyncAt,
    drainNow,
    refreshCount,
  };
}
