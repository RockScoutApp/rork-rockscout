import { Terminal, Shield } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";

export default function DeveloperConsole() {
  const { user } = useAuth();

  // Only accessible to the developer (check email)
  const isDev = user?.email?.endsWith("@rockscout.app") || user?.email === "RockScoutApp2026@yahoo.com";

  if (!isDev) {
    return (
      <div className="space-y-5">
        <div className="flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-muted/30">
            <Shield className="h-6 w-6 text-muted-foreground" />
          </div>
          <div>
            <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
              Access Restricted
            </h1>
            <p className="mt-0.5 text-sm text-muted-foreground">
              The Developer Console is only accessible to authorized accounts.
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div className="flex items-center gap-3">
        <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/15">
          <Terminal className="h-6 w-6 text-primary" />
        </div>
        <div>
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            Developer Console
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">Admin tools and diagnostics</p>
        </div>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <h3 className="text-sm font-semibold text-foreground">Account Info</h3>
        <pre className="mt-2 rounded bg-muted/30 p-3 text-xs text-muted-foreground overflow-x-auto">
{`User ID: ${user?.id}
Email: ${user?.email}`}
        </pre>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <h3 className="text-sm font-semibold text-foreground">Backend Status</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          Backend URL: {import.meta.env.EXPO_PUBLIC_RORK_FUNCTIONS_URL ?? "not configured"}
        </p>
      </div>
    </div>
  );
}
