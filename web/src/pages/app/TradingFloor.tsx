import { useQuery } from "@tanstack/react-query";
import { ArrowRightLeft, Loader2, MapPin } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { OptimizedImage } from "@/components/OptimizedImage";

interface TradeListing {
  id: string;
  user_id: string;
  title: string;
  description: string;
  category: string;
  condition: string;
  image_url: string | null;
  location_text: string;
  created_at: string;
}

export default function TradingFloor() {
  const { user } = useAuth();

  const { data: listings, isLoading } = useQuery<TradeListing[]>({
    queryKey: ["trading-floor"],
    queryFn: async () => {
      const { data, error } = await supabase
        .from("rockscout_trade_listings")
        .select("*")
        .order("created_at", { ascending: false })
        .limit(50);
      if (error) throw error;
      return (data ?? []) as TradeListing[];
    },
  });

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Trading Floor
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Browse specimens available for trade from rockhounds everywhere
        </p>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : listings && listings.length > 0 ? (
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {listings.map((listing) => (
            <div key={listing.id} className="rounded-xl border border-border bg-card overflow-hidden">
              {listing.image_url && (
                <div className="relative aspect-square w-full overflow-hidden bg-muted/30">
                  <OptimizedImage src={listing.image_url} alt={listing.title} loading="lazy" className="h-full w-full object-cover" />
                </div>
              )}
              <div className="p-3">
                <h3 className="truncate text-sm font-semibold text-foreground">{listing.title}</h3>
                <span className="mt-0.5 inline-block rounded bg-primary/15 px-1.5 py-0.5 text-[10px] font-medium text-primary">{listing.category}</span>
                {listing.location_text && (
                  <p className="mt-1 flex items-center gap-1 text-xs text-muted-foreground">
                    <MapPin className="h-3 w-3" />{listing.location_text}
                  </p>
                )}
                {listing.description && (
                  <p className="mt-1 line-clamp-2 text-xs text-muted-foreground">{listing.description}</p>
                )}
                <p className="mt-1 text-[10px] text-muted-foreground">
                  {listing.user_id === user?.id ? "Your listing" : `Condition: ${listing.condition}`}
                </p>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <ArrowRightLeft className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No trade listings yet. Be the first to post a specimen for trade!
          </p>
        </div>
      )}
    </div>
  );
}
