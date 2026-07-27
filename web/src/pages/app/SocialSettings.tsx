import { useNavigate } from "react-router-dom";
import { Users, MessageCircle, MapPin, UserPlus, ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";

export default function SocialSettings() {
  const navigate = useNavigate();
  const { user } = useAuth();

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Users className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to manage social settings</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">Social Settings</h1>
        <p className="mt-0.5 text-sm text-muted-foreground">Manage your connections and visibility</p>
      </div>

      <div className="space-y-3">
        <button onClick={() => navigate("/app/friends")} className="group flex w-full items-center gap-3 rounded-xl border border-border bg-card p-4 text-left transition-all hover:border-primary/40">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/15">
            <Users className="h-5 w-5 text-primary" />
          </div>
          <div className="min-w-0 flex-1">
            <h3 className="text-sm font-semibold text-foreground">Friends</h3>
            <p className="text-xs text-muted-foreground">View and manage your friend connections</p>
          </div>
          <ArrowRight className="h-4 w-4 text-muted-foreground group-hover:text-primary" />
        </button>

        <button onClick={() => navigate("/app/discover-hunters")} className="group flex w-full items-center gap-3 rounded-xl border border-border bg-card p-4 text-left transition-all hover:border-primary/40">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/15">
            <UserPlus className="h-5 w-5 text-primary" />
          </div>
          <div className="min-w-0 flex-1">
            <h3 className="text-sm font-semibold text-foreground">Discover Hunters</h3>
            <p className="text-xs text-muted-foreground">Find and connect with rockhounds near you</p>
          </div>
          <ArrowRight className="h-4 w-4 text-muted-foreground group-hover:text-primary" />
        </button>

        <button onClick={() => navigate("/app/rockscouts-map")} className="group flex w-full items-center gap-3 rounded-xl border border-border bg-card p-4 text-left transition-all hover:border-primary/40">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/15">
            <MapPin className="h-5 w-5 text-primary" />
          </div>
          <div className="min-w-0 flex-1">
            <h3 className="text-sm font-semibold text-foreground">RockScouts Map</h3>
            <p className="text-xs text-muted-foreground">See nearby RockScout users on a map</p>
          </div>
          <ArrowRight className="h-4 w-4 text-muted-foreground group-hover:text-primary" />
        </button>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <h3 className="text-sm font-semibold text-foreground">Privacy</h3>
        <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">
          Your exact location is never shared with other users. The RockScouts Map shows
          approximate positions only. You can control your visibility from the profile screen.
        </p>
      </div>
    </div>
  );
}
