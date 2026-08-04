-- Chat typing status table for real-time typing indicators
-- Supports both private threads (chat_id = thread ID) and group chats (chat_id = group chat ID)
CREATE TABLE IF NOT EXISTS chat_typing_status (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  chat_id TEXT NOT NULL,
  user_id TEXT NOT NULL REFERENCES rockscout_profiles(id) ON DELETE CASCADE,
  is_typing BOOLEAN DEFAULT false,
  updated_at TIMESTAMPTZ DEFAULT now(),
  UNIQUE(chat_id, user_id)
);

-- RLS: users can only see typing status for chats they're a member of
ALTER TABLE chat_typing_status ENABLE ROW LEVEL SECURITY;

-- SELECT: anyone who is a participant in the chat can see typing statuses
-- For private threads: check chat_thread_participants
-- For group chats: check group_chat_members
CREATE POLICY "users_can_read_typing_status" ON chat_typing_status
  FOR SELECT USING (
    -- Private thread: user is a participant
    EXISTS (
      SELECT 1 FROM chat_thread_participants p
      WHERE p.thread_id = chat_typing_status.chat_id
        AND p.user_id = auth.uid()
    )
    OR
    -- Group chat: user is a member
    EXISTS (
      SELECT 1 FROM group_chat_members m
      WHERE m.group_chat_id = chat_typing_status.chat_id
        AND m.user_id = auth.uid()
    )
  );

-- INSERT/UPDATE: users can only upsert their own typing status
CREATE POLICY "users_can_upsert_own_typing" ON chat_typing_status
  FOR INSERT WITH CHECK (user_id = auth.uid());

CREATE POLICY "users_can_update_own_typing" ON chat_typing_status
  FOR UPDATE USING (user_id = auth.uid());

CREATE POLICY "users_can_delete_own_typing" ON chat_typing_status
  FOR DELETE USING (user_id = auth.uid());

-- Index for fast lookups
CREATE INDEX IF NOT EXISTS idx_typing_status_chat_id ON chat_typing_status(chat_id);
CREATE INDEX IF NOT EXISTS idx_typing_status_updated_at ON chat_typing_status(updated_at DESC);

-- Grant access
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_typing_status TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_typing_status TO anon;
