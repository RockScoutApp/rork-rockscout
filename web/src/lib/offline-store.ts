/**
 * IndexedDB-based offline store for photo captures, saved images, field
 * journal entries, and trip planner data.
 *
 * When the user is offline, captures/journal/trips/saved-images are written
 * to IndexedDB instead of going directly to Supabase. A pending sync queue
 * tracks which records need to be pushed. When connectivity is restored
 * (or during the nightly 4 AM sync window), the queue is drained:
 *   1. Local photos (blobs) are uploaded to the `user-photos` Supabase Storage bucket
 *   2. The database row is upserted to the corresponding Supabase table
 *   3. The queue entry is removed on success
 *
 * The store also caches the last-known-good data from Supabase so the UI
 * can render offline without blank screens.
 */

const DB_NAME = "rockscout-offline";
const DB_VERSION = 1;

// Object store names
const STORE_CAPTURES = "captures";
const STORE_SAVED_IMAGES = "saved_images";
const STORE_JOURNAL = "journal";
const STORE_TRIPS = "trips";
const STORE_QUEUE = "sync_queue";
const STORE_PHOTOS = "photos"; // raw blobs keyed by local id

// Supabase table names
const TABLE_CAPTURES = "rockscout_captures";
const TABLE_SAVED_IMAGES = "rockscout_saved_images";
const TABLE_FIELD_JOURNAL = "rockscout_field_journal";
const TABLE_TRIPS = "rockscout_trips";

export type SyncTable =
  | typeof TABLE_CAPTURES
  | typeof TABLE_SAVED_IMAGES
  | typeof TABLE_FIELD_JOURNAL
  | typeof TABLE_TRIPS;

export type SyncOp = "upsert" | "delete";

export interface PendingSyncEntry {
  id: string; // unique queue entry id
  table: SyncTable;
  recordId: string;
  op: SyncOp;
  queuedAt: number;
  attempts: number;
}

let dbPromise: Promise<IDBDatabase> | null = null;

/** Open (and upgrade) the IndexedDB database. */
function openDB(): Promise<IDBDatabase> {
  if (dbPromise) return dbPromise;
  dbPromise = new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);
    request.onerror = () => reject(request.error);
    request.onsuccess = () => resolve(request.result);
    request.onupgradeneeded = () => {
      const db = request.result;
      if (!db.objectStoreNames.contains(STORE_CAPTURES)) {
        db.createObjectStore(STORE_CAPTURES, { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains(STORE_SAVED_IMAGES)) {
        db.createObjectStore(STORE_SAVED_IMAGES, { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains(STORE_JOURNAL)) {
        db.createObjectStore(STORE_JOURNAL, { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains(STORE_TRIPS)) {
        db.createObjectStore(STORE_TRIPS, { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains(STORE_QUEUE)) {
        db.createObjectStore(STORE_QUEUE, { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains(STORE_PHOTOS)) {
        db.createObjectStore(STORE_PHOTOS);
      }
    };
  });
  return dbPromise;
}

/** Run a transaction on a single store with a read-write mode. */
async function tx<T>(
  store: string,
  mode: IDBTransactionMode,
  fn: (store: IDBObjectStore) => IDBRequest<T>,
): Promise<T> {
  const db = await openDB();
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(store, mode);
    const objectStore = transaction.objectStore(store);
    const request = fn(objectStore);
    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });
}

// ─── Cache: store last-known-good records for offline rendering ──────────

export async function cacheRecords(
  store: string,
  records: Record<string, unknown>[],
): Promise<void> {
  const db = await openDB();
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(store, "readwrite");
    const os = transaction.objectStore(store);
    os.clear();
    for (const record of records) {
      os.put(record);
    }
    transaction.oncomplete = () => resolve();
    transaction.onerror = () => reject(transaction.error);
  });
}

export async function getCachedRecords<T>(
  store: string,
): Promise<T[]> {
  const db = await openDB();
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(store, "readonly");
    const os = transaction.objectStore(store);
    const request = os.getAll();
    request.onsuccess = () => resolve(request.result as T[]);
    request.onerror = () => reject(request.error);
  });
}

// ─── Photo blob storage ──────────────────────────────────────────────────

/** Store a photo blob in IndexedDB with a local id as the key. */
export async function storePhotoBlob(localId: string, blob: Blob): Promise<void> {
  await tx(STORE_PHOTOS, "readwrite", (os) => os.put(blob, localId));
}

/** Retrieve a stored photo blob by local id. */
export async function getPhotoBlob(localId: string): Promise<Blob | undefined> {
  return tx(STORE_PHOTOS, "readonly", (os) => os.get(localId));
}

/** Remove a stored photo blob. */
export async function removePhotoBlob(localId: string): Promise<void> {
  await tx(STORE_PHOTOS, "readwrite", (os) => os.delete(localId));
}

// ─── Sync queue ──────────────────────────────────────────────────────────

/** Enqueue a pending sync operation. Replaces any existing entry for the same table + recordId. */
export async function enqueueSync(
  table: SyncTable,
  recordId: string,
  op: SyncOp = "upsert",
): Promise<void> {
  const db = await openDB();
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(STORE_QUEUE, "readwrite");
    const os = transaction.objectStore(STORE_QUEUE);

    // First, check if an entry for the same table+recordId exists
    const cursorRequest = os.openCursor();
    cursorRequest.onsuccess = () => {
      const cursor = cursorRequest.result;
      if (cursor) {
        const entry = cursor.value as PendingSyncEntry;
        if (entry.table === table && entry.recordId === recordId) {
          // Replace existing entry
          cursor.delete();
        }
        cursor.continue();
      } else {
        // Add the new entry
        const newEntry: PendingSyncEntry = {
          id: `${table}_${recordId}_${Date.now()}`,
          table,
          recordId,
          op,
          queuedAt: Date.now(),
          attempts: 0,
        };
        os.add(newEntry);
      }
    };
    transaction.oncomplete = () => resolve();
    transaction.onerror = () => reject(transaction.error);
  });
}

/** Get all pending sync entries. */
export async function getPendingSync(): Promise<PendingSyncEntry[]> {
  return tx(STORE_QUEUE, "readonly", (os) => os.getAll() as IDBRequest<PendingSyncEntry[]>);
}

/** Remove a sync queue entry after successful sync. */
export async function removeSyncEntry(entryId: string): Promise<void> {
  await tx(STORE_QUEUE, "readwrite", (os) => os.delete(entryId));
}

/** Increment the attempt counter for a queue entry. */
export async function incrementSyncAttempts(entryId: string): Promise<void> {
  const db = await openDB();
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(STORE_QUEUE, "readwrite");
    const os = transaction.objectStore(STORE_QUEUE);
    const getRequest = os.get(entryId);
    getRequest.onsuccess = () => {
      const entry = getRequest.result as PendingSyncEntry | undefined;
      if (entry) {
        entry.attempts++;
        os.put(entry);
      }
    };
    transaction.oncomplete = () => resolve();
    transaction.onerror = () => reject(transaction.error);
  });
}

/** Clear all pending sync entries (used after a full pull-sync). */
export async function clearSyncQueue(): Promise<void> {
  await tx(STORE_QUEUE, "readwrite", (os) => os.clear());
}

/** Count pending sync entries — used by the UI badge. */
export async function getPendingCount(): Promise<number> {
  return tx(STORE_QUEUE, "readonly", (os) => os.count());
}

// ─── Offline record storage (for records created while offline) ──────────

/** Store an offline record (capture/journal/trip/saved-image) keyed by its id. */
export async function putOfflineRecord(
  store: string,
  record: Record<string, unknown>,
): Promise<void> {
  await tx(store, "readwrite", (os) => os.put(record));
}

/** Get all offline records from a store. */
export async function getOfflineRecords<T>(store: string): Promise<T[]> {
  return tx<T[]>(store, "readonly", (os) => os.getAll() as IDBRequest<T[]>);
}

/** Delete an offline record by id. */
export async function deleteOfflineRecord(
  store: string,
  id: string,
): Promise<void> {
  await tx(store, "readwrite", (os) => os.delete(id));
}

// ─── Store name helpers ──────────────────────────────────────────────────

export const OfflineStores = {
  CAPTURES: STORE_CAPTURES,
  SAVED_IMAGES: STORE_SAVED_IMAGES,
  JOURNAL: STORE_JOURNAL,
  TRIPS: STORE_TRIPS,
} as const;

export const SupabaseTables = {
  CAPTURES: TABLE_CAPTURES,
  SAVED_IMAGES: TABLE_SAVED_IMAGES,
  FIELD_JOURNAL: TABLE_FIELD_JOURNAL,
  TRIPS: TABLE_TRIPS,
} as const;

// ─── Map table → store for offline record lookup ─────────────────────────

export function tableToStore(table: SyncTable): string {
  switch (table) {
    case TABLE_CAPTURES: return STORE_CAPTURES;
    case TABLE_SAVED_IMAGES: return STORE_SAVED_IMAGES;
    case TABLE_FIELD_JOURNAL: return STORE_JOURNAL;
    case TABLE_TRIPS: return STORE_TRIPS;
    default: return STORE_CAPTURES;
  }
}
