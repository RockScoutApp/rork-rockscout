import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  ArrowRightLeft,
  Loader2,
  Plus,
  Trash2,
  X,
  Sparkles,
  CheckCircle,
  Clock,
  Package,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold, TagChip } from "@/components/sculpted";
import { OptimizedImage } from "@/components/OptimizedImage";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const AMETHYST_HEX = "265 47% 67%";
const DANGER_HEX = "4 70% 55%";
const SUCCESS_HEX = "147 49% 55%";

interface TradeListing {
  id: string;
  owner_user_id: string;
  type: string;
  specimen_name: string;
  condition: string;
  description: string;
  want_in_return: string;
  photo_uri: string | null;
  tags: string[];
  status: string;
  created_at: number;
  expires_at: number;
}

const CONDITIONS = ["Mint", "Excellent", "Good", "Fair", "Rough"];

export default function MyTrades() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [formName, setFormName] = useState("");
  const [formType, setFormType] = useState<"HAVE" | "WANT">("HAVE");
  const [formCondition, setFormCondition] = useState("Good");
  const [formDesc, setFormDesc] = useState("");
  const [formWant, setFormWant] = useState("");
  const [formTags, setFormTags] = useState("");

  const { data: listings, isLoading } = useQuery<TradeListing[]>({
    queryKey: ["my-trades", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_trade_listings")
        .select("*")
        .eq("owner_user_id", user.id)
        .order("created_at", { ascending: false });
      if (error) throw error;
      return (data ?? []) as TradeListing[];
    },
    enabled: !!user,
  });

  const deleteListing = useMutation({
    mutationFn: async (id: string) => {
      const { error } = await supabase
        .from("rockscout_trade_listings")
        .delete()
        .eq("id", id);
      if (error) throw error;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["my-trades"] });
      queryClient.invalidateQueries({ queryKey: ["trading-floor"] });
      toast.success("Listing deleted");
    },
    onError: (err: Error) => toast.error(err.message),
  });

  const markTraded = useMutation({
    mutationFn: async (id: string) => {
      const { error } = await supabase
        .from("rockscout_trade_listings")
        .update({ status: "traded" })
        .eq("id", id);
      if (error) throw error;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["my-trades"] });
      toast.success("Marked as traded!");
    },
    onError: (err: Error) => toast.error(err.message),
  });

  const createListing = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to create a listing");
      const tags = formTags.split(",").map((t) => t.trim()).filter(Boolean);
      const { error } = await supabase
        .from("rockscout_trade_listings")
        .insert({
          owner_user_id: user.id,
          type: formType,
          specimen_name: formName.trim(),
          condition: formCondition,
          description: formDesc.trim(),
          want_in_return: formWant.trim(),
          tags,
          status: "active",
        });
      if (error) throw error;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["my-trades"] });
      queryClient.invalidateQueries({ queryKey: ["trading-floor"] });
      setShowCreateForm(false);
      setFormName("");
      setFormDesc("");
      setFormWant("");
      setFormTags("");
      toast.success("Listing posted!");
    },
    onError: (err: Error) => toast.error(err.message),
  });

  if (!user) {
    return (
      <ScreenScaffold title="My Trades">
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <ArrowRightLeft className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to view your trades</p>
        </div>
      </ScreenScaffold>
    );
  }

  const activeListings = (listings ?? []).filter((l) => l.status === "active");
  const tradedListings = (listings ?? []).filter((l) => l.status === "traded");

  return (
    <ScreenScaffold title="My Trades">
      <div className="space-y-5 px-4 pb-8">
        {/* Create button */}
        <SculptedButton
          accent="citrine"
          glowing
          className="w-full"
          onClick={() => setShowCreateForm(true)}
        >
          <Plus className="h-4 w-4" />
          Create New Listing
        </SculptedButton>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        ) : activeListings.length > 0 ? (
          <>
            <h2 className="font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
              Active Listings ({activeListings.length})
            </h2>
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {activeListings.map((listing) => (
                <SculptedCard
                  key={listing.id}
                  accent={listing.type === "HAVE" ? "aqua" : "amethyst"}
                  className="overflow-hidden"
                >
                  <div className="relative aspect-square w-full overflow-hidden bg-muted/20">
                    {listing.photo_uri ? (
                      <OptimizedImage
                        src={listing.photo_uri}
                        alt={listing.specimen_name}
                        loading="lazy"
                        className="h-full w-full object-cover"
                      />
                    ) : (
                      <div className="flex h-full w-full items-center justify-center"
                        style={{
                          background: `radial-gradient(circle, hsl(${listing.type === "HAVE" ? AQUA_HEX : AMETHYST_HEX} / 0.1), transparent)`,
                        }}
                      >
                        <Package className="h-8 w-8 text-muted-foreground" />
                      </div>
                    )}
                    <div
                      className="absolute left-2 top-2 rounded-full px-2 py-0.5 text-xs font-bold"
                      style={{
                        backgroundColor: `hsl(${listing.type === "HAVE" ? AQUA_HEX : AMETHYST_HEX} / 0.85)`,
                        color: "hsl(30 30% 9%)",
                      }}
                    >
                      {listing.type === "HAVE" ? "🤝 HAVE" : "🔍 WANT"}
                    </div>
                  </div>
                  <div className="p-3.5">
                    <h3 className="truncate text-sm font-bold text-foreground">
                      {listing.specimen_name}
                    </h3>
                    <div className="mt-1 flex flex-wrap gap-1">
                      <TagChip accent={`hsl(174 100% 45%)`}>{listing.condition}</TagChip>
                    </div>
                    {listing.want_in_return && (
                      <p className="mt-1.5 text-xs font-medium" style={{ color: `hsl(${AQUA_HEX})` }}>
                        Wants: {listing.want_in_return}
                      </p>
                    )}
                    <div className="mt-3 flex gap-2 border-t border-border pt-2.5">
                      <button
                        onClick={() => markTraded.mutate(listing.id)}
                        disabled={markTraded.isPending}
                        className="sculpted-button sculpted-raised dark-card flex flex-1 items-center justify-center gap-1 rounded-lg px-2.5 py-1.5 text-xs font-bold"
                        style={{
                          ["--sculpted-accent" as string]: SUCCESS_HEX,
                          color: `hsl(${SUCCESS_HEX})`,
                        }}
                      >
                        <CheckCircle className="h-3 w-3" />
                        Traded
                      </button>
                      <button
                        onClick={() => {
                          if (confirm("Delete this listing?")) deleteListing.mutate(listing.id);
                        }}
                        disabled={deleteListing.isPending}
                        className="sculpted-button sculpted-raised dark-card flex flex-1 items-center justify-center gap-1 rounded-lg px-2.5 py-1.5 text-xs font-bold"
                        style={{
                          ["--sculpted-accent" as string]: DANGER_HEX,
                          color: `hsl(${DANGER_HEX})`,
                        }}
                      >
                        <Trash2 className="h-3 w-3" />
                        Delete
                      </button>
                    </div>
                  </div>
                </SculptedCard>
              ))}
            </div>
          </>
        ) : (
          <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <ArrowRightLeft className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">
              You have no active trade listings.
            </p>
            <p className="max-w-xs text-xs text-muted-foreground">
              Post a specimen you want to trade or sell, and other rockhounds can express interest.
            </p>
          </SculptedCard>
        )}

        {/* Traded listings */}
        {tradedListings.length > 0 && (
          <>
            <h2 className="font-display text-base font-bold text-muted-foreground">
              Traded ({tradedListings.length})
            </h2>
            <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
              {tradedListings.map((listing) => (
                <SculptedCard key={listing.id} accent="success" className="overflow-hidden opacity-70">
                  <div className="p-3.5">
                    <div className="flex items-center gap-2">
                      <CheckCircle className="h-4 w-4" style={{ color: `hsl(${SUCCESS_HEX})` }} />
                      <h3 className="truncate text-sm font-bold text-foreground">
                        {listing.specimen_name}
                      </h3>
                    </div>
                    <p className="mt-1 text-xs text-muted-foreground">
                      {listing.type === "HAVE" ? "Had" : "Wanted"} · {listing.condition}
                    </p>
                  </div>
                </SculptedCard>
              ))}
            </div>
          </>
        )}
      </div>

      {/* Create listing modal */}
      {showCreateForm && (
        <div
          className="fixed inset-0 z-[80] flex items-end justify-center bg-black/60 backdrop-blur-sm md:items-center"
          onClick={() => setShowCreateForm(false)}
        >
          <div
            className="dark-card sculpted-raised w-full max-w-lg rounded-t-2xl p-6 md:rounded-2xl"
            style={{ ["--sculpted-accent" as string]: CITRINE_HEX }}
            onClick={(e) => e.stopPropagation()}
          >
            <div className="mb-4 flex items-center justify-between">
              <h3 className="font-display text-lg font-bold text-foreground">
                New Trade Listing
              </h3>
              <button onClick={() => setShowCreateForm(false)} className="text-muted-foreground">
                <X className="h-5 w-5" />
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">Type</label>
                <div className="flex gap-2">
                  {(["HAVE", "WANT"] as const).map((t) => (
                    <button
                      key={t}
                      onClick={() => setFormType(t)}
                      className={`flex-1 rounded-lg px-4 py-2.5 text-sm font-bold transition-all ${
                        formType === t ? "ring-2 ring-primary bg-primary/10" : "border border-border text-muted-foreground"
                      }`}
                    >
                      {t === "HAVE" ? "🤝 I have this" : "🔍 I want this"}
                    </button>
                  ))}
                </div>
              </div>
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                  Specimen Name <span className="text-red-500">*</span>
                </label>
                <Input value={formName} onChange={(e) => setFormName(e.target.value)}
                  placeholder="e.g. Polished Agate Slice" maxLength={80} />
              </div>
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">Condition</label>
                <div className="flex flex-wrap gap-2">
                  {CONDITIONS.map((c) => (
                    <button key={c} onClick={() => setFormCondition(c)}
                      className={`rounded-full px-3 py-1.5 text-xs font-medium transition-all ${
                        formCondition === c ? "ring-2 ring-primary bg-primary/10" : "border border-border text-muted-foreground"
                      }`}
                    >
                      {c}
                    </button>
                  ))}
                </div>
              </div>
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">Description</label>
                <textarea value={formDesc} onChange={(e) => setFormDesc(e.target.value)}
                  placeholder="Size, origin, special features…" maxLength={500} rows={3}
                  className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none" />
              </div>
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">Want in Return</label>
                <Input value={formWant} onChange={(e) => setFormWant(e.target.value)}
                  placeholder="What are you looking for?" maxLength={200} />
              </div>
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                  Tags (comma-separated)
                </label>
                <Input value={formTags} onChange={(e) => setFormTags(e.target.value)}
                  placeholder="quartz, crystal, self-collected" maxLength={100} />
              </div>
            </div>

            <div className="mt-5 flex gap-3">
              <SculptedButton accent="aqua" className="flex-1" onClick={() => setShowCreateForm(false)}>
                Cancel
              </SculptedButton>
              <SculptedButton accent="citrine" glowing className="flex-1"
                disabled={!formName.trim() || createListing.isPending}
                onClick={() => createListing.mutate()}>
                {createListing.isPending ? "Posting…" : "Post Listing"}
              </SculptedButton>
            </div>
          </div>
        </div>
      )}
    </ScreenScaffold>
  );
}
