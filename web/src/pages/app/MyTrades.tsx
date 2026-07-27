import { useQuery } from "@tanstack/react-query";
import { ArrowRightLeft, Loader2 } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";

interface TradeListing {
  id: string;
  title: string;
  description: string;
  category: string;
  condition: string;
  image_url: string | null;
  created_at: string;
}

export default function MyTrades() {
  const { user } = useAuth();

  const { data: listings, isLoading } = useQuery<TradeListing[]>({
    queryKey: ["my-trades", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_trade_listings")
        .select("*")
        .eq("user_id", user.id)
        .order("created_at", { ascending: false });
      if (error) throw error;
      return (data ?? []) as TradeListing[];
    },
    enabled: !!user,
  });

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <ArrowRightLeft className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to view your trades</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">My Trades</h1>
        <p className="mt-0.5 text-sm text-muted-foreground">Your active trade listings</p>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : listings && listings.length > 0 ? (
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {listings.map((listing) => (
            <div key={listing.id} className="rounded-xl border border-border bg-card overflow-hidden">
              {listing.image_url && (
                <div className="aspect-square w-full overflow-hidden bg-muted/30">
                  <img src={listing.image_url} alt={listing.title} loading="lazy" className="h-full w-full object-cover" />
                </div>
              )}
              <div className="p-3">
                <h3 className="truncate text-sm font-semibold text-foreground">{listing.title}</h3>
                <span className="mt-0.5 inline-block rounded bg-primary/15 px-1.5 py-0.5 text-[10px] font-medium text-primary">{listing.category}</span>
                {listing.description && <p className="mt-1 line-clamp-2 text-xs text-muted-foreground">{listing.description}</p>}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <ArrowRightLeft className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">You have no active trade listings.</p>
        </div>
      )}
    </div>
  );
}
