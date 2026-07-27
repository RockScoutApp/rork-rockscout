import { Users, UserPlus } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";

export default function DiscoverHunters() {
  const { user } = useAuth();

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Users className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to discover hunters</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">Discover Hunters</h1>
        <p className="mt-0.5 text-sm text-muted-foreground">Find and connect with rockhounds near you</p>
      </div>

      <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
        <UserPlus className="h-8 w-8 text-muted-foreground" />
        <p className="max-w-sm text-sm text-muted-foreground">
          Discovery features are coming soon. In the meantime, check the RockScouts Map
          to see nearby users, or add friends by sharing your profile.
        </p>
      </div>
    </div>
  );
}
