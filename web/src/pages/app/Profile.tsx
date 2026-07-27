import { useNavigate } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { LogOut, Mail, Gem, Award, Settings } from "lucide-react";

export default function Profile() {
  const { user, signOut } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Profile
        </h1>
      </div>

      <div className="rounded-xl border border-border bg-card p-5">
        <div className="flex items-center gap-4">
          <div className="flex h-14 w-14 items-center justify-center rounded-full bg-primary/15 ring-1 ring-primary/30">
            <Gem className="h-7 w-7 text-primary" />
          </div>
          <div className="min-w-0">
            <p className="truncate font-medium text-foreground">
              {user.email}
            </p>
            <p className="mt-0.5 flex items-center gap-1 text-sm text-muted-foreground">
              <Mail className="h-3.5 w-3.5" />
              {user.email}
            </p>
          </div>
        </div>
      </div>

      <div className="space-y-2">
        <button
          onClick={() => navigate("/app/achievements")}
          className="flex w-full items-center gap-3 rounded-lg border border-border bg-card p-4 text-left transition-colors hover:border-primary/40"
        >
          <Award className="h-5 w-5 text-primary" />
          <div className="flex-1">
            <p className="text-sm font-medium text-foreground">
              Achievements
            </p>
            <p className="text-xs text-muted-foreground">
              Level up and earn badges
            </p>
          </div>
        </button>

        <button
          onClick={() => navigate("/app/settings")}
          className="flex w-full items-center gap-3 rounded-lg border border-border bg-card p-4 text-left transition-colors hover:border-primary/40"
        >
          <Settings className="h-5 w-5 text-primary" />
          <div className="flex-1">
            <p className="text-sm font-medium text-foreground">Settings</p>
            <p className="text-xs text-muted-foreground">
              Notifications, offline cache, theme
            </p>
          </div>
        </button>
      </div>

      <Button
        onClick={() => {
          signOut();
          navigate("/app");
        }}
        variant="outline"
        className="w-full gap-2"
      >
        <LogOut className="h-4 w-4" />
        Sign out
      </Button>
    </div>
  );
}
