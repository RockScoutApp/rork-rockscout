import { useState, useEffect, useRef } from "react";
import { Outlet, NavLink, useNavigate } from "react-router-dom";
import {
  Home,
  Camera,
  BookOpen,
  Map,
  User,
  LogOut,
  LogIn,
  Gem,
  MapPin,
  Keyboard,
  CloudCheck,
  CloudUpload,
  CloudOff,
} from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { useKeyboardShortcuts } from "@/hooks/useKeyboardShortcuts";
import { useBackGuard } from "@/hooks/useBackGuard";
import { syncEntitlement } from "@/lib/entitlement";
import IosBetaBanner from "@/components/app/IosBetaBanner";
import KeyboardHelpOverlay from "@/components/app/KeyboardHelpOverlay";
import FieldCameraDialog from "@/components/app/FieldCameraDialog";
import { cn } from "@/lib/utils";

interface NavItem {
  to: string;
  icon: typeof Home;
  label: string;
  end?: boolean;
}

const NAV_ITEMS_FREE: NavItem[] = [
  { to: "/app", icon: Home, label: "Home", end: true },
  { to: "/app/specimens", icon: BookOpen, label: "Specimens" },
  { to: "/app/collection", icon: Gem, label: "Collection" },
  { to: "/app/map", icon: Map, label: "Map" },
  { to: "/app/favorites", icon: MapPin, label: "Spots" },
  { to: "/app/profile", icon: User, label: "Profile" },
];

const NAV_ITEMS_PREMIUM: NavItem[] = [
  { to: "/app", icon: Home, label: "Home", end: true },
  { to: "/app/identify", icon: Camera, label: "Identify" },
  { to: "/app/collection", icon: Gem, label: "Collection" },
  { to: "/app/map", icon: Map, label: "Map" },
  { to: "/app/favorites", icon: MapPin, label: "Spots" },
  { to: "/app/profile", icon: User, label: "Profile" },
];

export default function AppLayout() {
  const { user, signOut } = useAuth();
  const { isFree, isPremium } = useTier();
  const navigate = useNavigate();
  const [helpOpen, setHelpOpen] = useState(false);
  const [fieldCameraOpen, setFieldCameraOpen] = useState(false);
  const navItems = isPremium ? NAV_ITEMS_PREMIUM : NAV_ITEMS_FREE;

  // Prevent browser back button from leaving the app shell or closing
  // the tab at the root route. Also closes dialogs/sheets on back press.
  useBackGuard();

  // ── Entitlement sync status pill ──
  type SyncState = "idle" | "syncing" | "synced" | "failed";
  const [syncState, setSyncState] = useState<SyncState>("idle");
  const [showSyncToast, setShowSyncToast] = useState(false);
  const syncToastShown = useRef(false);

  useEffect(() => {
    if (!user?.id || !isPremium) return;
    let cancelled = false;
    setSyncState("syncing");
    syncEntitlement(user.id).then((ok) => {
      if (cancelled) return;
      setSyncState(ok ? "synced" : "failed");
      if (ok && !syncToastShown.current) {
        syncToastShown.current = true;
        setShowSyncToast(true);
        setTimeout(() => setShowSyncToast(false), 4000);
      }
    });
    return () => { cancelled = true; };
  }, [user?.id, isPremium]);

  const syncConfig = {
    synced: { icon: CloudCheck, color: "text-emerald-500", label: "Synced" },
    syncing: { icon: CloudUpload, color: "text-amber-500", label: "Syncing…" },
    failed: { icon: CloudOff, color: "text-red-500", label: "Not synced" },
    idle: { icon: CloudUpload, color: "text-muted-foreground", label: "—" },
  } as const;
  const SyncIcon = syncConfig[syncState].icon;

  useKeyboardShortcuts({ onToggleHelp: () => setHelpOpen((v) => !v) });

  // Listen for the "open-field-camera" custom event dispatched by the Home tile.
  // Only enabled for premium users — free users don't have the field camera.
  useEffect(() => {
    if (isFree) return;
    const handler = () => setFieldCameraOpen(true);
    window.addEventListener("open-field-camera", handler);
    return () => window.removeEventListener("open-field-camera", handler);
  }, [isFree]);

  const handleSignOut = async () => {
    await signOut();
    navigate("/app");
  };

  const handleSignIn = () => {
    navigate("/app/signin");
  };

  return (
    <div className="flex min-h-screen flex-col bg-background md:flex-row">
      <IosBetaBanner />

      {/* Desktop sidebar */}
      <aside className="hidden w-60 shrink-0 border-r border-border bg-card/50 md:flex md:flex-col">
        <div className="flex items-center gap-2.5 px-5 py-5">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/15 ring-1 ring-primary/30">
            <Gem className="h-5 w-5 text-primary" />
          </div>
          <span className="font-display text-lg font-bold text-foreground">
            RockScout
          </span>
          {isPremium && (
            <span
              title={`Entitlement sync: ${syncConfig[syncState].label}`}
              className="ml-auto inline-flex items-center gap-1 rounded-full border border-border bg-card/40 px-2 py-0.5 text-[10px] font-semibold"
            >
              <SyncIcon className={cn("h-3 w-3", syncConfig[syncState].color)} />
              <span className={cn("hidden lg:inline", syncConfig[syncState].color)}>
                {syncConfig[syncState].label}
              </span>
            </span>
          )}
        </div>

        <nav className="flex flex-1 flex-col gap-1 px-3">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) =>
                cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors",
                  isActive
                    ? "bg-primary/15 text-primary"
                    : "text-muted-foreground hover:bg-muted/50 hover:text-foreground",
                )
              }
            >
              <item.icon className="h-5 w-5" />
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="space-y-1 border-t border-border p-3">
          <button
            onClick={() => setHelpOpen(true)}
            className="hidden w-full items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted/50 hover:text-foreground md:flex"
            aria-label="Keyboard shortcuts help"
            title="Keyboard shortcuts (?)"
          >
            <Keyboard className="h-5 w-5" />
            Shortcuts
            <span className="ml-auto flex items-center gap-1">
              <kbd className="rounded border border-border bg-muted px-1.5 py-0.5 font-sans text-[10px] font-semibold text-muted-foreground">
                ?
              </kbd>
            </span>
          </button>
          {user ? (
            <div className="mb-1 truncate px-3 text-xs text-muted-foreground">
              {user.email}
            </div>
          ) : (
            <div className="mb-1 px-3 text-xs text-muted-foreground">
              Free preview mode
            </div>
          )}
          {user ? (
            <button
              onClick={handleSignOut}
              className="flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted/50 hover:text-foreground"
            >
              <LogOut className="h-5 w-5" />
              Sign out
            </button>
          ) : (
            <button
              onClick={handleSignIn}
              className="flex w-full items-center gap-3 rounded-lg bg-primary/10 px-3 py-2.5 text-sm font-medium text-primary transition-colors hover:bg-primary/15"
            >
              <LogIn className="h-5 w-5" />
              Sign in
            </button>
          )}
        </div>
      </aside>

      {/* Main content — wider on large/desktop screens for multi-column grids */}
      <main className="flex-1 overflow-y-auto pb-20 md:pb-0">
        <div className="mx-auto w-full max-w-5xl px-4 py-6 md:px-8 md:py-8 xl:max-w-6xl 2xl:max-w-7xl">
          <Outlet />
        </div>
      </main>

      {/* Mobile bottom tab bar */}
      <nav className="fixed bottom-0 left-0 right-0 z-50 flex items-center justify-around border-t border-border bg-card/95 backdrop-blur-lg md:hidden">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) =>
              cn(
                "flex flex-1 flex-col items-center gap-1 py-2.5 text-[10px] font-medium transition-colors",
                isActive ? "text-primary" : "text-muted-foreground",
              )
            }
          >
            <item.icon className="h-5 w-5" />
            {item.label}
          </NavLink>
        ))}
      </nav>

      <KeyboardHelpOverlay open={helpOpen} onClose={() => setHelpOpen(false)} />
      <FieldCameraDialog
        open={fieldCameraOpen}
        onDismiss={() => setFieldCameraOpen(false)}
      />

      {/* Entitlement sync confirmation toast */}
      {showSyncToast && (
        <div className="fixed bottom-4 left-1/2 z-[60] -translate-x-1/2 rounded-xl border border-emerald-500/40 bg-emerald-500/10 px-4 py-2.5 text-sm font-medium text-emerald-600 shadow-lg backdrop-blur-md dark:text-emerald-400">
          <span className="flex items-center gap-2">
            <CloudCheck className="h-4 w-4" />
            Premium entitlement synced across devices
          </span>
        </div>
      )}
    </div>
  );
}
