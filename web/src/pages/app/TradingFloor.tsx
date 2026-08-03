import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  ArrowRightLeft,
  Loader2,
  MapPin,
  Plus,
  Search,
  Trash2,
  X,
  Tag,
  Sparkles,
  Heart,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold, TagChip } from "@/components/sculpted";
import { OptimizedImage } from "@/components/OptimizedImage";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const CYAN_HEX = "174 100% 45%";
const AMETHYST_HEX = "265 47% 67%";
const DANGER_HEX = "4 70% 55%";

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
  owner_profile?: {
    display_name: string;
    avatar_emoji: string;
  } | null;
}

const CATEGORIES = [
  "Mineral", "Crystal", "Fossil", "Gemstone", "Igneous", "Sedimentary",
  "Metamorphic", "Meteorite", "Artifact", "Other",
];

const CONDITIONS = ["Mint", "Excellent", "Good", "Fair", "Rough"];

export default function TradingFloor() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [search, setSearch] = useState("");
  const [filterType, setFilterType] = useState<"all" | "HAVE" | "WANT">("all");
  const [showCreateForm, setShowCreateForm] = useState(false);

  // Create form state
  const [formName, setFormName] = useState("");
  const [formType, setFormType] = useState<"HAVE" | "WANT">("HAVE");
  const [formCondition, setFormCondition] = useState("Good");
  const [formDesc, setFormDesc] = useState("");
  const [formWant, setFormWant] = useState("");
  const [formTags, setFormTags] = useState("");

  const { data: listings, isLoading } = useQuery<TradeListing[]>({
    queryKey: ["trading-floor"],
    queryFn: async () => {
      const { data, error } = await supabase
        .from("rockscout_trade_listings")
        .select("*")
        .eq("status", "active")
        .order("created_at", { ascending: false })
        .limit(50);
      if (error) throw error;
      const rows = (data ?? []) as TradeListing[];

      // Fetch owner profiles in parallel
      const ownerIds = [...new Set(rows.map((r) => r.owner_user_id))];
      if (ownerIds.length === 0) return [];

      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji")
        .in("id", ownerIds);

      const profileMap = new Map(
        (profiles ?? []).map((p) => [p.id, p]),
      );

      return rows.map((r) => ({
        ...r,
        owner_profile: profileMap.get(r.owner_user_id) ?? null,
      }));
    },
  });

  const createListing = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to create a listing");
      const tags = formTags
        .split(",")
        .map((t) => t.trim())
        .filter(Boolean);
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
      queryClient.invalidateQueries({ queryKey: ["trading-floor"] });
      queryClient.invalidateQueries({ queryKey: ["my-trades"] });
      setShowCreateForm(false);
      setFormName("");
      setFormDesc("");
      setFormWant("");
      setFormTags("");
      toast.success("Listing posted!");
    },
    onError: (err: Error) => toast.error(err.message),
  });

  const expressInterest = useMutation({
    mutationFn: async (listing: TradeListing) => {
      if (!user) throw new Error("Sign in first");
      const { error } = await supabase
        .from("rockscout_trade_interests")
        .insert({
          listing_id: listing.id,
          listing_owner_id: listing.owner_user_id,
          interested_user_id: user.id,
          message: `I'm interested in your ${listing.specimen_name}!`,
        });
      if (error) {
        if (error.code === "23505") {
          throw new Error("You've already expressed interest in this listing");
        }
        throw error;
      }
    },
    onSuccess: () => toast.success("Interest sent! The owner will be notified."),
    onError: (err: Error) => toast.error(err.message),
  });

  const filtered = (listings ?? []).filter((l) => {
    if (filterType !== "all" && l.type !== filterType) return false;
    if (search) {
      const q = search.toLowerCase();
      return (
        l.specimen_name.toLowerCase().includes(q) ||
        l.description.toLowerCase().includes(q) ||
        l.tags?.some((t) => t.toLowerCase().includes(q))
      );
    }
    return true;
  });

  return (
    <ScreenScaffold title="Trading Floor">
      <div className="space-y-5 px-4 pb-8">
        {/* Search + Create */}
        <div className="flex gap-3">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search specimens, tags…"
              className="pl-9"
            />
          </div>
          <SculptedButton
            accent="citrine"
            size="md"
            glowing
            onClick={() => {
              if (!user) {
                toast.error("Sign in to create a listing");
                return;
              }
              setShowCreateForm(true);
            }}
          >
            <Plus className="h-4 w-4" />
            <span className="hidden sm:inline">New Listing</span>
          </SculptedButton>
        </div>

        {/* Type filter tabs */}
        <div className="flex gap-2">
          {(["all", "HAVE", "WANT"] as const).map((t) => (
            <button
              key={t}
              onClick={() => setFilterType(t)}
              className={`rounded-full px-4 py-1.5 text-xs font-bold transition-all ${
                filterType === t
                  ? "bg-primary/15 text-primary ring-1 ring-primary/40"
                  : "border border-border text-muted-foreground hover:text-foreground"
              }`}
            >
              {t === "all" ? "All" : t === "HAVE" ? "🤝 Have" : "🔍 Want"}
            </button>
          ))}
        </div>

        {/* Listings grid */}
        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        ) : filtered.length > 0 ? (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {filtered.map((listing) => (
              <SculptedCard
                key={listing.id}
                accent={listing.type === "HAVE" ? "aqua" : "amethyst"}
                interactive
                className="overflow-hidden"
              >
                {/* Image or placeholder */}
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
                      <Sparkles
                        className="h-10 w-10"
                        style={{ color: `hsl(${listing.type === "HAVE" ? AQUA_HEX : AMETHYST_HEX} / 0.4)` }}
                      />
                    </div>
                  )}
                  {/* Type badge */}
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

                {/* Content */}
                <div className="p-3.5">
                  <h3 className="truncate text-sm font-bold text-foreground">
                    {listing.specimen_name}
                  </h3>
                  <div className="mt-1 flex flex-wrap gap-1">
                    <TagChip accent={`hsl(${CYAN_HEX})`}>
                      {listing.condition}
                    </TagChip>
                    {listing.tags?.slice(0, 2).map((tag) => (
                      <TagChip key={tag} accent={`hsl(${CITRINE_HEX})`}>
                        {tag}
                      </TagChip>
                    ))}
                  </div>
                  {listing.description && (
                    <p className="mt-2 line-clamp-2 text-xs text-muted-foreground">
                      {listing.description}
                    </p>
                  )}
                  {listing.want_in_return && (
                    <p className="mt-1.5 text-xs font-medium" style={{ color: `hsl(${AQUA_HEX})` }}>
                      Wants: {listing.want_in_return}
                    </p>
                  )}

                  {/* Owner */}
                  <div className="mt-3 flex items-center gap-2 border-t border-border pt-2.5">
                    <span className="text-base">
                      {listing.owner_profile?.avatar_emoji ?? "🧗"}
                    </span>
                    <span className="flex-1 truncate text-xs text-muted-foreground">
                      {listing.owner_profile?.display_name ?? "Unknown hunter"}
                    </span>
                    {listing.owner_user_id !== user?.id && (
                      <button
                        onClick={() => expressInterest.mutate(listing)}
                        disabled={expressInterest.isPending}
                        className="sculpted-button sculpted-raised dark-card flex items-center gap-1 rounded-lg px-2.5 py-1.5 text-xs font-bold"
                        style={{
                          ["--sculpted-accent" as string]: CITRINE_HEX,
                          color: `hsl(${CITRINE_HEX})`,
                        }}
                      >
                        <Heart className="h-3 w-3" />
                        Interested
                      </button>
                    )}
                  </div>
                </div>
              </SculptedCard>
            ))}
          </div>
        ) : (
          <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <ArrowRightLeft className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">
              No trade listings yet. Be the first to post a specimen!
            </p>
            {user && (
              <SculptedButton
                accent="citrine"
                onClick={() => setShowCreateForm(true)}
              >
                <Plus className="h-4 w-4" />
                Post a Specimen
              </SculptedButton>
            )}
          </SculptedCard>
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
              {/* Type selector */}
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">Type</label>
                <div className="flex gap-2">
                  {(["HAVE", "WANT"] as const).map((t) => (
                    <button
                      key={t}
                      onClick={() => setFormType(t)}
                      className={`flex-1 rounded-lg px-4 py-2.5 text-sm font-bold transition-all ${
                        formType === t
                          ? "ring-2 ring-primary bg-primary/10"
                          : "border border-border text-muted-foreground"
                      }`}
                    >
                      {t === "HAVE" ? "🤝 I have this" : "🔍 I want this"}
                    </button>
                  ))}
                </div>
              </div>

              {/* Specimen name */}
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                  Specimen Name
                </label>
                <Input
                  value={formName}
                  onChange={(e) => setFormName(e.target.value)}
                  placeholder="e.g. Polished Agate Slice"
                  maxLength={80}
                />
              </div>

              {/* Condition */}
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">Condition</label>
                <div className="flex flex-wrap gap-2">
                  {CONDITIONS.map((c) => (
                    <button
                      key={c}
                      onClick={() => setFormCondition(c)}
                      className={`rounded-full px-3 py-1.5 text-xs font-medium transition-all ${
                        formCondition === c
                          ? "ring-2 ring-primary bg-primary/10"
                          : "border border-border text-muted-foreground"
                      }`}
                    >
                      {c}
                    </button>
                  ))}
                </div>
              </div>

              {/* Description */}
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                  Description
                </label>
                <textarea
                  value={formDesc}
                  onChange={(e) => setFormDesc(e.target.value)}
                  placeholder="Size, origin, special features…"
                  maxLength={500}
                  rows={3}
                  className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
                />
              </div>

              {/* Want in return */}
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                  Want in Return
                </label>
                <Input
                  value={formWant}
                  onChange={(e) => setFormWant(e.target.value)}
                  placeholder="What are you looking for?"
                  maxLength={200}
                />
              </div>

              {/* Tags */}
              <div>
                <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                  Tags (comma-separated)
                </label>
                <Input
                  value={formTags}
                  onChange={(e) => setFormTags(e.target.value)}
                  placeholder="quartz, crystal, self-collected"
                  maxLength={100}
                />
              </div>
            </div>

            <div className="mt-5 flex gap-3">
              <SculptedButton
                accent="aqua"
                className="flex-1"
                onClick={() => setShowCreateForm(false)}
              >
                Cancel
              </SculptedButton>
              <SculptedButton
                accent="citrine"
                glowing
                className="flex-1"
                disabled={!formName.trim() || createListing.isPending}
                onClick={() => createListing.mutate()}
              >
                {createListing.isPending ? "Posting…" : "Post Listing"}
              </SculptedButton>
            </div>
          </div>
        </div>
      )}
    </ScreenScaffold>
  );
}
