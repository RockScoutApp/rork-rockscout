-- 0016_fatal_error_email_trigger.sql
-- PostgreSQL trigger on rockscout_error_logs that calls the /send-error-email
-- Cloudflare Worker endpoint whenever a new row with is_fatal = true is inserted.
-- The Worker sends an email alert to aaron_james_martin@yahoo.com via Resend.

-- Enable pg_http if available (Supabase ships pg_net for outbound HTTP).
-- We use pg_net's net.http_post function to call the Edge Function.
CREATE EXTENSION IF NOT EXISTS pg_net;

-- Grant access to the net schema for the trigger function's owner.
GRANT USAGE ON SCHEMA net TO postgres;

-- Function that fires AFTER INSERT on rockscout_error_logs when is_fatal = true.
-- Sends an HTTP POST to the Cloudflare Worker /send-error-email endpoint with
-- the error details so an email alert is dispatched via Resend.
CREATE OR REPLACE FUNCTION notify_fatal_error()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  worker_url text;
  app_key text;
BEGIN
  -- Only fire for fatal errors
  IF NEW.is_fatal = false THEN
    RETURN NEW;
  END IF;

  -- Read config from the rockscout_config table (set once via SQL Editor).
  SELECT value INTO worker_url FROM rockscout_config WHERE key = 'worker_url';
  SELECT value INTO app_key FROM rockscout_config WHERE key = 'app_key';

  IF worker_url IS NULL OR app_key IS NULL THEN
    -- Configuration not set yet — skip silently (error is still stored in the table)
    RETURN NEW;
  END IF;

  -- Fire-and-forget HTTP POST to the Worker endpoint.
  -- pg_net's http_post is async — the trigger doesn't block on the response.
  PERFORM net.http_post(
    url := worker_url || '/send-error-email',
    headers := jsonb_build_object(
      'Content-Type', 'application/json',
      'X-App-Key', app_key
    ),
    body := jsonb_build_object(
      'errorType', NEW.error_type,
      'errorMessage', NEW.error_message,
      'stackTrace', NEW.stack_trace,
      'platform', NEW.platform,
      'appVersion', NEW.app_version,
      'osVersion', NEW.os_version,
      'deviceModel', NEW.device_model,
      'userId', NEW.user_id::text,
      'screen', NEW.screen,
      'createdAt', NEW.created_at::text
    )
  );

  RETURN NEW;
END;
$$;

-- Drop existing trigger if it exists (idempotent re-run safe)
DROP TRIGGER IF EXISTS trigger_fatal_error_email ON rockscout_error_logs;

-- Create the trigger — fires AFTER INSERT, only for fatal errors.
-- Using a WHEN clause so the trigger only fires for is_fatal = true rows,
-- avoiding overhead on non-fatal error inserts.
CREATE TRIGGER trigger_fatal_error_email
  AFTER INSERT ON rockscout_error_logs
  FOR EACH ROW
  WHEN (NEW.is_fatal = true)
  EXECUTE FUNCTION notify_fatal_error();

-- Grant execute on the trigger function to the postgres role.
GRANT EXECUTE ON FUNCTION notify_fatal_error() TO postgres;
