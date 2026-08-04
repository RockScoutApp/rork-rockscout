import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import {
  ArrowRightLeft,
  Plus,
  Trash2,
  X,
  Loader2,
  Search,
  Heart,
  MessageCircle,
  Tag,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { filterProfanity } from "@/lib/profanity-filter";

interface TradeListing {
  id: string;
  owner_user_id: string;
  type: "HAVE" | "WANT";
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

interface Profile {
  id: string;
  display_name: string;
  avatar_emoji: string;
}

interface ListingWithOwner extends TradeListing {
  owner?: Profile | null;
}

const formatTime = (epoch: number): string => {
  const d = new Date(epoch * 1000);
  const diff = Date.now() - d.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  if (days === 0) return "Today";
  if (days === 1) return "Yesterday";
  if (days < 7) return `${days}d ago`;
  if (days < 30) return `${Math.floor(days / 7)}w ago`;
  return d.toLocaleDateString("en-US", { month: "short", day: "numeric" });
};

const isExpired = (expiresAt: number): boolean =>
  expiresAt * 1000 < Date.now();

export default function TradeBoard() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const [tab, setTab] = useState<"browse" | "mine">("browse");
  const [search, setSearch] = useState("");
  const [filterType, setFilterType] = useState<"ALL" | "HAVE" | "WANT">("ALL");
  const [showEditor, setShowEditor] = useState(false);
  const [form, setForm] = useState({
    type: "HAVE" as "HAVE" | "WANT",
    specimen_name: "",
    condition: "",
    description: "",
    want_in_return: "",
  });

  const { data: listings, isLoading } = useQuery<ListingWithOwner[]>({
    queryKey: ["trade-listings", user?.id],
    queryFn: async () => {
      const { data, error } = await supabase
        .from("rockscout_trade_listings")
        .select("*")
        .order("created_at", { ascending: false })
        .limit(100);
      if (error) throw error;
      const rows = (data ?? []) as TradeListing[];

      // Fetch owner profiles
      const ownerIds = [...new Set(rows.map((r) => r.owner_user_id))];
      if (ownerIds.length === 0) return [];
      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji")
        .in("id", ownerIds);
      const profileMap = new Map<string, Profile>(
        (profiles ?? []).map((p) => [p.id, p as Profile]),
      );

      return rows.map((r) => ({
        ...r,
        owner: profileMap.get(r.owner_user_id) ?? null,
      })) as ListingWithOwner[];
    },
  });

  const createListing = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to post listings");
      const { error } = await supabase
        .from("rockscout_trade_listings")
        .insert({
          owner_user_id: user.id,
          type: form.type,
          specimen_name: filterProfanity(form.specimen_name).filteredText,
          condition: filterProfanity(form.condition).filteredText,
          description: filterProfanity(form.description).filteredText,
          want_in_return: filterProfanity(form.want_in_return).filteredText,
          tags: [],
        });
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Listing posted");
      queryClient.invalidateQueries({ queryKey: ["trade-listings"] });
      setShowEditor(false);
      setForm({
        type: "HAVE",
        specimen_name: "",
        condition: "",
        description: "",
        want_in_return: "",
      });
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to post listing"),
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
      toast.success("Listing removed");
      queryClient.invalidateQueries({ queryKey: ["trade-listings"] });
    },
    onError: () => toast.error("Failed to remove listing"),
  });

  const expressInterest = useMutation({
    mutationFn: async (listing: ListingWithOwner) => {
      if (!user) throw new Error("Sign in to contact traders");
      const { error } = await supabase
        .from("rockscout_trade_interests")
        .insert({
          listing_id: listing.id,
          listing_owner_id: listing.owner_user_id,
          interested_user_id: user.id,
          message: `I'm interested in your ${listing.specimen_name}!`,
        });
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Interest sent! The trader will be notified.");
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to send interest"),
  });

  const filtered = (listings ?? []).filter((listing) => {
    if (tab === "mine" && listing.owner_user_id !== user?.id) return false;
    if (tab === "browse" && listing.owner_user_id === user?.id) return false;
    if (filterType !== "ALL" && listing.type !== filterType) return false;
    if (search.trim()) {
      const q = search.toLowerCase();
      return (
        listing.specimen_name.toLowerCase().includes(q) ||
        listing.description.toLowerCase().includes(q) ||
        listing.condition.toLowerCase().includes(q)
      );
    }
    return true;
  });

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <ArrowRightLeft className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to use the trade board</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            Trade Board
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">
            Swap, sell, and trade specimens
          </p>
        </div>
        <Button size="sm" onClick={() => setShowEditor(true)} className="gap-2">
          <Plus className="h-4 w-4" />
          Post
        </Button>
      </div>

      <Tabs value={tab} onValueChange={(v) => setTab(v as typeof tab)}>
        <TabsList>
          <TabsTrigger value="browse">Browse</TabsTrigger>
          <TabsTrigger value="mine">My Listings</TabsTrigger>
        </TabsList>
      </Tabs>

      {tab === "browse" && (
        <div className="flex flex-wrap gap-2">
          {(["ALL", "HAVE", "WANT"] as const).map((t) => (
            <button
              key={t}
              onClick={() => setFilterType(t)}
              className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
                filterType === t
                  ? "bg-primary text-primary-foreground"
                  : "bg-muted text-muted-foreground hover:bg-muted/70"
              }`}
            >
              {t === "ALL" ? "All" : t === "HAVE" ? "Have" : "Want"}
            </button>
          ))}
        </div>
      )}

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search specimens, conditions, descriptions..."
          className="pl-10"
        />
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : filtered.length > 0 ? (
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">
          {filtered.map((listing) => {
            const expired = isExpired(listing.expires_at);
            const isMine = listing.owner_user_id === user?.id;
            return (
              <div
                key={listing.id}
                className="group space-y-3 dark-card sculpted-raised rounded-xl p-4"
              >
                <div className="flex items-start justify-between gap-2">
                  <div className="flex items-center gap-2">
                    <span
                      className={`rounded-full px-2 py-0.5 text-xs font-bold ${
                        listing.type === "HAVE"
                          ? "bg-primary/15 text-primary"
                          : "bg-amber-500/15 text-amber-600"
                      }`}
                    >
                      {listing.type === "HAVE" ? "HAVE" : "WANT"}
                    </span>
                    {expired && (
                      <span className="rounded-full bg-muted px-2 py-0.5 text-xs text-muted-foreground">
                        Expired
                      </span>
                    )}
                  </div>
                  {isMine && (
                    <button
                      onClick={() => deleteListing.mutate(listing.id)}
                      className="rounded-lg p-1 text-muted-foreground opacity-0 transition-opacity hover:bg-destructive/10 hover:text-destructive group-hover:opacity-100"
                      aria-label="Delete listing"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  )}
                </div>

                <div className="min-w-0">
                  <h3 className="truncate font-display text-base font-semibold text-foreground">
                    {listing.specimen_name || "Unnamed specimen"}
                  </h3>
                  {listing.condition && (
                    <p className="mt-0.5 truncate text-xs text-muted-foreground">
                      Condition: {listing.condition}
                    </p>
                  )}
                </div>

                {listing.description && (
                  <p className="line-clamp-3 text-sm leading-relaxed text-muted-foreground">
                    {listing.description}
                  </p>
                )}

                {listing.want_in_return && (
                  <p className="line-clamp-2 text-sm text-muted-foreground">
                    <span className="font-medium text-foreground">
                      Looking for:
                    </span>{" "}
                    {listing.want_in_return}
                  </p>
                )}

                <div className="flex items-center justify-between border-t border-border pt-2">
                  <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                    <span className="text-base">
                      {listing.owner?.avatar_emoji ?? "💎"}
                    </span>
                    <button
                      className="font-medium text-foreground hover:text-primary hover:underline"
                      onClick={() => navigate(`/app/profile/${listing.owner_user_id}`)}
                    >
                      {listing.owner?.display_name ?? "Unknown"}
                    </button>
                    <span>·</span>
                    {formatTime(listing.created_at)}
                  </div>
                  {!isMine && !expired && (
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => expressInterest.mutate(listing)}
                      disabled={expressInterest.isPending}
                      className="gap-1.5"
                    >
                      <Heart className="h-3.5 w-3.5" />
                      Interested
                    </Button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
          <ArrowRightLeft className="h-8 w-8 text-muted-foreground" />
          <p className="max-w-sm text-sm text-muted-foreground">
            {tab === "mine"
              ? "You haven't posted any listings yet. Post a specimen to swap, sell, or trade."
              : "No listings found. Be the first to post a specimen for trade!"}
          </p>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowEditor(true)}
            className="gap-2"
          >
            <Plus className="h-4 w-4" />
            Post a listing
          </Button>
        </div>
      )}

      {/* Listing editor */}
      <Dialog open={showEditor} onOpenChange={setShowEditor}>
        <DialogContent aria-describedby={undefined} className="max-w-md">
          <DialogHeader>
            <DialogTitle>Post a trade listing</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-1.5">
              <Label>Type</Label>
              <div className="flex gap-2">
                {(["HAVE", "WANT"] as const).map((t) => (
                  <button
                    key={t}
                    onClick={() => setForm((f) => ({ ...f, type: t }))}
                    className={`flex-1 rounded-lg border px-3 py-2 text-sm font-medium transition-colors ${
                      form.type === t
                        ? "border-primary bg-primary/10 text-primary"
                        : "border-border text-muted-foreground hover:bg-muted/50"
                    }`}
                  >
                    {t === "HAVE" ? "I have this" : "I want this"}
                  </button>
                ))}
              </div>
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="specimen-name">Specimen name</Label>
              <Input
                id="specimen-name"
                value={form.specimen_name}
                onChange={(e) =>
                  setForm((f) => ({ ...f, specimen_name: e.target.value }))
                }
                placeholder="e.g. Amethyst cluster, Polished tiger's eye"
              />
            </div>

            {form.type === "HAVE" && (
              <div className="space-y-1.5">
                <Label htmlFor="condition">Condition</Label>
                <Input
                  id="condition"
                  value={form.condition}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, condition: e.target.value }))
                  }
                  placeholder="e.g. Rough, Tumbled, Cut, Polished, Slab"
                />
              </div>
            )}

            <div className="space-y-1.5">
              <Label htmlFor="description">
                {form.type === "HAVE" ? "Description" : "What you're looking for"}
              </Label>
              <Textarea
                id="description"
                value={form.description}
                onChange={(e) =>
                  setForm((f) => ({ ...f, description: e.target.value }))
                }
                placeholder={
                  form.type === "HAVE"
                    ? "Describe the specimen — size, quality, where found..."
                    : "Describe what specimen you're hunting for..."
                }
                rows={3}
              />
            </div>

            {form.type === "HAVE" && (
              <div className="space-y-1.5">
                <Label htmlFor="want-in-return">Want in return (optional)</Label>
                <Input
                  id="want-in-return"
                  value={form.want_in_return}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, want_in_return: e.target.value }))
                  }
                  placeholder="e.g. Quartz crystals, Fluorite, open to offers"
                />
              </div>
            )}
          </div>

          <DialogFooter className="gap-2">
            <Button variant="outline" onClick={() => setShowEditor(false)}>
              Cancel
            </Button>
            <Button
              onClick={() => createListing.mutate()}
              disabled={createListing.isPending || !form.specimen_name.trim()}
            >
              {createListing.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                "Post listing"
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
