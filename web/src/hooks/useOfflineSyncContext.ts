import createContextHook from "@nkzw/create-context-hook";
import { useOfflineSync } from "@/hooks/useOfflineSync";

/**
 * App-wide offline sync state, shared across all PWA pages.
 *
 * Wraps the useOfflineSync hook so any component can:
 *  - Read the pending sync count (for UI badges)
 *  - Check if a sync is in progress
 *  - Manually trigger a drain
 *
 * The hook automatically:
 *  - Drains the queue when connectivity is restored (online event)
 *  - Registers a Background Sync tag for browser-managed sync
 *  - Schedules a nightly 4 AM drain timer in the user's timezone
 *  - Drains on mount if online and there are pending items
 */
function useOfflineSyncState() {
  const sync = useOfflineSync();
  return sync;
}

export const [OfflineSyncProvider, useOfflineSyncContext] =
  createContextHook(useOfflineSyncState);
