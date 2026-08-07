-- storage_policies_owner_workaround.sql
-- Create the missing RLS policies on storage.objects via SQL Editor.
-- storage.objects is owned by supabase_admin, so the SQL Editor (postgres)
-- cannot CREATE/DROP POLICY directly. We temporarily change ownership to
-- postgres, create the policies, then restore ownership to supabase_admin.
--
-- Safe to re-run: all CREATE/DROP are guarded with IF [NOT] EXISTS.

DO $$
DECLARE
    original_owner text;
BEGIN
    -- Discover the current owner of storage.objects
    SELECT tableowner INTO original_owner
    FROM pg_tables
    WHERE schemaname = 'storage' AND tablename = 'objects';

    RAISE NOTICE 'storage.objects current owner: %', original_owner;

    -- Temporarily make postgres the owner so we can manage policies
    EXECUTE format('ALTER TABLE storage.objects OWNER TO postgres');

    -- Enable RLS on the table (idempotent)
    EXECUTE 'ALTER TABLE storage.objects ENABLE ROW LEVEL SECURITY';

    -- Drop/recreate policies
    EXECUTE format('DROP POLICY IF EXISTS %I ON storage.objects', 'user-photos-read-all');
    EXECUTE format('CREATE POLICY %I ON storage.objects FOR SELECT USING (bucket_id = ''user-photos'')', 'user-photos-read-all');

    EXECUTE format('DROP POLICY IF EXISTS %I ON storage.objects', 'user-photos-write-self');
    EXECUTE format(
        'CREATE POLICY %I ON storage.objects FOR INSERT WITH CHECK (bucket_id = ''user-photos'' AND (storage.foldername(name))[1] = auth.uid()::text)',
        'user-photos-write-self'
    );

    EXECUTE format('DROP POLICY IF EXISTS %I ON storage.objects', 'user-photos-update-self');
    EXECUTE format(
        'CREATE POLICY %I ON storage.objects FOR UPDATE USING (bucket_id = ''user-photos'' AND (storage.foldername(name))[1] = auth.uid()::text) WITH CHECK (bucket_id = ''user-photos'' AND (storage.foldername(name))[1] = auth.uid()::text)',
        'user-photos-update-self'
    );

    EXECUTE format('DROP POLICY IF EXISTS %I ON storage.objects', 'user-photos-delete-self');
    EXECUTE format(
        'CREATE POLICY %I ON storage.objects FOR DELETE USING (bucket_id = ''user-photos'' AND (storage.foldername(name))[1] = auth.uid()::text)',
        'user-photos-delete-self'
    );

    -- Restore original ownership
    EXECUTE format('ALTER TABLE storage.objects OWNER TO %I', original_owner);

    RAISE NOTICE 'storage.policies created and ownership restored to %', original_owner;
END
$$;
