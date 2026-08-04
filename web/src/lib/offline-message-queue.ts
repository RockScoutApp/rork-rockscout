/**
 * Offline message queue for the web PWA messenger.
 *
 * Stores messages that failed to send due to network issues in IndexedDB.
 * When connectivity is restored, the queue is automatically drained and
 * messages are retried.
 *
 * Extends the existing offline-store.ts infrastructure with a new
 * IndexedDB object store dedicated to pending chat messages.
 */

const DB_NAME = "rockscout-offline";
const DB_VERSION = 2; // Bumped from 1 to add the message queue store
const STORE_MESSAGES = "pending_messages";

export interface PendingWebMessage {
  id: string;
  chatId: string;
  body: string;
  imageUrl: string | null;
  replyToMessageId: string | null;
  taggedUserIds: string[] | null;
  isGroup: boolean;
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
    request.onupgradeneeded = (event) => {
      const db = request.result;
      // Existing stores from offline-store.ts (version 1)
      if (!db.objectStoreNames.contains("captures")) {
        db.createObjectStore("captures", { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains("saved_images")) {
        db.createObjectStore("saved_images", { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains("journal")) {
        db.createObjectStore("journal", { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains("trips")) {
        db.createObjectStore("trips", { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains("sync_queue")) {
        db.createObjectStore("sync_queue", { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains("photos")) {
        db.createObjectStore("photos");
      }
      // New store for offline messages (version 2)
      if (!db.objectStoreNames.contains(STORE_MESSAGES)) {
        db.createObjectStore(STORE_MESSAGES, { keyPath: "id" });
      }
      // If upgrading from v1, the existing stores are already there
      void event;
    };
  });
  return dbPromise;
}

/** Run a transaction on the message store. */
async function tx<T>(
  mode: IDBTransactionMode,
  fn: (store: IDBObjectStore) => IDBRequest<T>,
): Promise<T> {
  const db = await openDB();
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(STORE_MESSAGES, mode);
    const store = transaction.objectStore(STORE_MESSAGES);
    const request = fn(store);
    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });
}

/** Enqueue a message for later sending. */
export async function enqueueMessage(msg: PendingWebMessage): Promise<void> {
  await tx("readwrite", (store) => store.put(msg));
}

/** Remove a message from the queue after successful send. */
export async function removeMessage(id: string): Promise<void> {
  await tx("readwrite", (store) => store.delete(id));
}

/** Get all pending messages. */
export async function getPendingMessages(): Promise<PendingWebMessage[]> {
  return tx<PendingWebMessage[]>("readonly", (store) =>
    store.getAll() as IDBRequest<PendingWebMessage[]>,
  );
}

/** Get pending messages for a specific chat (for UI display). */
export async function getPendingForChat(chatId: string): Promise<PendingWebMessage[]> {
  const all = await getPendingMessages();
  return all.filter((m) => m.chatId === chatId);
}

/** Increment the attempt counter for a message. */
export async function incrementAttempts(id: string): Promise<void> {
  const db = await openDB();
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(STORE_MESSAGES, "readwrite");
    const store = transaction.objectStore(STORE_MESSAGES);
    const getRequest = store.get(id);
    getRequest.onsuccess = () => {
      const msg = getRequest.result as PendingWebMessage | undefined;
      if (msg) {
        msg.attempts++;
        store.put(msg);
      }
    };
    transaction.oncomplete = () => resolve();
    transaction.onerror = () => reject(transaction.error);
  });
}

/** Count pending messages (for UI badge). */
export async function getPendingMessageCount(): Promise<number> {
  return tx<number>("readonly", (store) => store.count());
}

/** Check if there are any pending messages. */
export async function hasPendingMessages(): Promise<boolean> {
  return (await getPendingMessageCount()) > 0;
}

const MAX_ATTEMPTS = 10;

type SendFn = (msg: PendingWebMessage) => Promise<void>;

let retryFn: SendFn | null = null;
let drainInProgress = false;

/** Set the function that will be used to send pending messages. */
export function setRetryFunction(fn: SendFn): void {
  retryFn = fn;
}

/** Attempt to send all pending messages. Returns the number successfully sent. */
export async function drainMessageQueue(): Promise<number> {
  if (drainInProgress || !retryFn) return 0;
  drainInProgress = true;

  try {
    const pending = await getPendingMessages();
    if (pending.length === 0) return 0;

    console.info(`[offline-msg] Draining ${pending.length} pending messages`);
    let successCount = 0;

    for (const msg of pending) {
      try {
        await retryFn(msg);
        await removeMessage(msg.id);
        successCount++;
      } catch (err) {
        console.warn(`[offline-msg] Retry failed for ${msg.id}:`, err);
        await incrementAttempts(msg.id);
      }
    }

    // Drop messages that have failed too many times
    const remaining = await getPendingMessages();
    for (const msg of remaining) {
      if (msg.attempts >= MAX_ATTEMPTS) {
        console.warn(`[offline-msg] Dropping ${msg.id} after ${MAX_ATTEMPTS} failed attempts`);
        await removeMessage(msg.id);
      }
    }

    console.info(`[offline-msg] Drain complete: ${successCount}/${pending.length} sent`);
    return successCount;
  } finally {
    drainInProgress = false;
  }
}

/** Initialize the offline message queue — call once at app startup. */
export function initOfflineMessageQueue(): void {
  // Listen for online events to trigger a drain
  window.addEventListener("online", () => {
    console.info("[offline-msg] Connectivity restored — draining queue");
    drainMessageQueue();
  });

  // Also try draining on mount if already online
  if (navigator.onLine) {
    drainMessageQueue();
  }
}
