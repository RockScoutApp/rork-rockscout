/**
 * Offline-aware mutation helpers for the PWA.
 *
 * Wraps Supabase insert/update/delete operations so they work both online
 * and offline. When offline, records are written to IndexedDB and enqueued
 * for sync. When online, they go directly to Supabase (and also cache to
 * IndexedDB for offline rendering).
 */

import { supabase } from "@/lib/supabase";
import {
  type SyncTable,
  enqueueSync,
  putOfflineRecord,
  deleteOfflineRecord,
  storePhotoBlob,
  OfflineStores,
  SupabaseTables,
} from "@/lib/offline-store";

/** Check if the browser is currently online. */
export function isOnline(): boolean {
  return typeof navigator !== "undefined" && navigator.onLine;
}

/**
 * Offline-aware upsert: inserts/updates a record in Supabase if online,
 * or stores it in IndexedDB + enqueues a sync entry if offline.
 *
 * @param table The Supabase table name.
 * @param record The record to upsert (must have an `id` field).
 * @param offlineStore The IndexedDB store name for offline caching.
 * @param userId The current user's ID.
 * @returns True on success (online upsert or offline cache), false on error.
 */
export async function offlineAwareUpsert(
  table: SyncTable,
  record: Record<string, unknown>,
  offlineStore: string,
  userId: string,
): Promise<boolean> {
  // Always cache the record locally for offline rendering
  await putOfflineRecord(offlineStore, record);

  if (isOnline()) {
    try {
      const { error } = await supabase
        .from(table)
        .upsert({ ...record, user_id: userId }, { onConflict: "id" });
      if (error) {
        console.warn(`[offline-mutation] Online upsert to ${table} failed, queuing:`, error.message);
        await enqueueSync(table, record.id as string, "upsert");
        return false;
      }
      return true;
    } catch (e) {
      console.warn(`[offline-mutation] Online upsert to ${table} exception, queuing:`, e);
      await enqueueSync(table, record.id as string, "upsert");
      return false;
    }
  } else {
    // Offline — enqueue for later sync
    await enqueueSync(table, record.id as string, "upsert");
    return true;
  }
}

/**
 * Offline-aware delete: deletes a record from Supabase if online,
 * or removes it from IndexedDB + enqueues a delete sync entry if offline.
 */
export async function offlineAwareDelete(
  table: SyncTable,
  recordId: string,
  offlineStore: string,
): Promise<boolean> {
  // Always remove from local cache
  await deleteOfflineRecord(offlineStore, recordId);

  if (isOnline()) {
    try {
      const { error } = await supabase.from(table).delete().eq("id", recordId);
      if (error) {
        console.warn(`[offline-mutation] Online delete from ${table} failed, queuing:`, error.message);
        await enqueueSync(table, recordId, "delete");
        return false;
      }
      return true;
    } catch (e) {
      console.warn(`[offline-mutation] Online delete from ${table} exception, queuing:`, e);
      await enqueueSync(table, recordId, "delete");
      return false;
    }
  } else {
    await enqueueSync(table, recordId, "delete");
    return true;
  }
}

/**
 * Stores a photo blob in IndexedDB and returns a local URL that can be
 * used in the UI until the blob is uploaded to Supabase Storage during sync.
 *
 * @param blob The photo blob to store.
 * @returns A `local:` URL that can be used as an image src, or a blob: URL.
 */
export async function storeLocalPhoto(blob: Blob): Promise<string> {
  const localId = `local:${crypto.randomUUID()}`;
  await storePhotoBlob(localId, blob);
  // Also create a blob: URL for immediate display
  return URL.createObjectURL(blob);
}

/**
 * Convert a File to a blob and store it locally for offline sync.
 * Returns both the display URL and the internal local ID.
 */
export async function storePhotoFromFile(file: File): Promise<{ displayUrl: string; localId: string }> {
  const localId = `local:${crypto.randomUUID()}`;
  await storePhotoBlob(localId, file);
  const displayUrl = URL.createObjectURL(file);
  return { displayUrl, localId };
}

// ─── Convenience helpers per table ───────────────────────────────────────

export async function upsertCapture(
  capture: Record<string, unknown>,
  userId: string,
): Promise<boolean> {
  return offlineAwareUpsert(
    SupabaseTables.CAPTURES,
    capture,
    OfflineStores.CAPTURES,
    userId,
  );
}

export async function deleteCapture(recordId: string): Promise<boolean> {
  return offlineAwareDelete(
    SupabaseTables.CAPTURES,
    recordId,
    OfflineStores.CAPTURES,
  );
}

export async function upsertSavedImage(
  image: Record<string, unknown>,
  userId: string,
): Promise<boolean> {
  return offlineAwareUpsert(
    SupabaseTables.SAVED_IMAGES,
    image,
    OfflineStores.SAVED_IMAGES,
    userId,
  );
}

export async function deleteSavedImage(recordId: string): Promise<boolean> {
  return offlineAwareDelete(
    SupabaseTables.SAVED_IMAGES,
    recordId,
    OfflineStores.SAVED_IMAGES,
  );
}

export async function upsertJournalEntry(
  entry: Record<string, unknown>,
  userId: string,
): Promise<boolean> {
  return offlineAwareUpsert(
    SupabaseTables.FIELD_JOURNAL,
    entry,
    OfflineStores.JOURNAL,
    userId,
  );
}

export async function deleteJournalEntry(recordId: string): Promise<boolean> {
  return offlineAwareDelete(
    SupabaseTables.FIELD_JOURNAL,
    recordId,
    OfflineStores.JOURNAL,
  );
}

export async function upsertTrip(
  trip: Record<string, unknown>,
  userId: string,
): Promise<boolean> {
  return offlineAwareUpsert(
    SupabaseTables.TRIPS,
    trip,
    OfflineStores.TRIPS,
    userId,
  );
}

export async function deleteTrip(recordId: string): Promise<boolean> {
  return offlineAwareDelete(
    SupabaseTables.TRIPS,
    recordId,
    OfflineStores.TRIPS,
  );
}
