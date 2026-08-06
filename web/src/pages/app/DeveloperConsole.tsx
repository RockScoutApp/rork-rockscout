import { useState, useEffect } from "react";
import { Terminal, Shield, TrendingUp, MousePointerClick, DollarSign, RotateCcw } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import {
  getAffiliateState,
  resetAffiliateStats,
  estimatedAffiliateRevenue,
  type AffiliateState,
} from "@/lib/affiliate-tracker";
import { FUNCTIONS_URL } from "@/lib/config";

export default function DeveloperConsole() {
  const { user } = useAuth();

  // Only accessible to the developer (check email)
  const isDev =
    user?.email?.endsWith("@rockscout.app") ||
    user?.email === "RockScoutApp2026@yahoo.com";

  const [affState, setAffState] = useState<AffiliateState | null>(null);

  useEffect(() => {
    if (isDev) {
      setAffState(getAffiliateState());
    }
  }, [isDev]);

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

  const handleReset = () => {
    resetAffiliateStats();
    setAffState(getAffiliateState());
  };

  const recentDays = affState?.perDay?.slice(-14) ?? [];
  const maxDailyClicks = Math.max(1, ...recentDays.map((d) => d.clicks));

  const topItems = affState?.perItem?.slice(0, 10) ?? [];
  const maxItemClicks = Math.max(1, ...topItems.map((i) => i.clicks));

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-3">
        <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/15">
          <Terminal className="h-6 w-6 text-primary" />
        </div>
        <div>
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            Developer Console
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">
            Admin tools and diagnostics
          </p>
        </div>
      </div>

      {/* Account Info */}
      <div className="dark-card sculpted-raised rounded-lg p-4">
        <h3 className="text-sm font-semibold text-foreground">Account Info</h3>
        <pre className="mt-2 rounded bg-muted/30 p-3 text-xs text-muted-foreground overflow-x-auto">
{`User ID: ${user?.id}
Email: ${user?.email}`}
        </pre>
      </div>

      {/* Backend Status */}
      <div className="dark-card sculpted-raised rounded-lg p-4">
        <h3 className="text-sm font-semibold text-foreground">Backend Status</h3>
        <p className="mt-2 text-sm text-muted-foreground">
          Backend URL:{" "}
          {FUNCTIONS_URL}
        </p>
      </div>

      {/* Affiliate Click Analytics */}
      <div className="space-y-4">
        <div className="flex items-center gap-2">
          <MousePointerClick className="h-5 w-5 text-primary" />
          <h2 className="font-display text-xl font-bold text-foreground">
            Affiliate Click Analytics
          </h2>
        </div>
        <p className="text-sm text-muted-foreground">
          Amazon gear guide click-through tracking from the web app
        </p>

        {/* Stat cards */}
        <div className="grid grid-cols-2 gap-3">
          <div className="rounded-lg border border-primary/30 bg-primary/5 p-4">
            <div className="flex items-center gap-2">
              <TrendingUp className="h-4 w-4 text-primary" />
              <span className="text-xs font-medium text-muted-foreground">
                Total Clicks
              </span>
            </div>
            <p className="mt-2 text-2xl font-bold text-primary">
              {affState?.totalClicks ?? 0}
            </p>
          </div>
          <div className="rounded-lg border border-green-500/30 bg-green-500/5 p-4">
            <div className="flex items-center gap-2">
              <DollarSign className="h-4 w-4 text-green-500" />
              <span className="text-xs font-medium text-muted-foreground">
                Est. Revenue
              </span>
            </div>
            <p className="mt-2 text-2xl font-bold text-green-500">
              {estimatedAffiliateRevenue()}
            </p>
          </div>
        </div>

        {/* Daily clicks bar graph */}
        <div className="dark-card sculpted-raised rounded-lg p-4">
          <h3 className="text-sm font-semibold text-foreground">
            Daily Click-Throughs (last 14 days)
          </h3>
          <div className="mt-4 space-y-1.5">
            {recentDays.length === 0 ? (
              <p className="py-6 text-center text-sm text-muted-foreground">
                No affiliate clicks recorded yet.
              </p>
            ) : (
              recentDays.map((day) => {
                const widthPct = (day.clicks / maxDailyClicks) * 100;
                return (
                  <div
                    key={day.date}
                    className="flex items-center gap-3"
                  >
                    <span className="w-16 shrink-0 text-xs text-muted-foreground">
                      {day.date.slice(5)}
                    </span>
                    <div className="h-5 flex-1 overflow-hidden rounded bg-muted/30">
                      <div
                        className="h-full rounded bg-gradient-to-r from-primary/60 to-primary transition-all duration-300"
                        style={{ width: `${widthPct}%` }}
                      />
                    </div>
                    <span className="w-8 shrink-0 text-right text-xs font-bold text-primary">
                      {day.clicks}
                    </span>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* Top items bar graph */}
        <div className="dark-card sculpted-raised rounded-lg p-4">
          <h3 className="text-sm font-semibold text-foreground">
            Top Gear Items by Clicks
          </h3>
          <div className="mt-4 space-y-1.5">
            {topItems.length === 0 ? (
              <p className="py-6 text-center text-sm text-muted-foreground">
                No item clicks recorded yet.
              </p>
            ) : (
              topItems.map((item) => {
                const widthPct = (item.clicks / maxItemClicks) * 100;
                return (
                  <div
                    key={item.itemId}
                    className="flex items-center gap-3"
                  >
                    <span className="w-32 shrink-0 truncate text-xs text-foreground">
                      {item.name}
                    </span>
                    <div className="h-5 flex-1 overflow-hidden rounded bg-muted/30">
                      <div
                        className="h-full rounded bg-gradient-to-r from-amber-500/60 to-amber-500 transition-all duration-300"
                        style={{ width: `${widthPct}%` }}
                      />
                    </div>
                    <span className="w-8 shrink-0 text-right text-xs font-bold text-amber-500">
                      {item.clicks}
                    </span>
                  </div>
                );
              })
            )}
          </div>
          <button
            onClick={handleReset}
            className="mt-4 inline-flex items-center gap-2 rounded-lg border border-red-500/30 bg-red-500/10 px-3 py-1.5 text-xs font-medium text-red-500 transition-colors hover:bg-red-500/20"
          >
            <RotateCcw className="h-3.5 w-3.5" />
            Reset Affiliate Stats
          </button>
        </div>
      </div>
    </div>
  );
}
