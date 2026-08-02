import { useParams, useNavigate } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { ArrowLeft, Heart, Plus, Loader2, AlertCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { OptimizedImage } from "@/components/OptimizedImage";
import { toast } from "sonner";

interface SpecimenDetail {
  id: string;
  name: string;
  category: string;
  tagline: string;
  colors: string;
  hardness: string;
  luster: string;
  crystal_system: string;
  streak: string;
  rarity: string;
  image_url: string;
  description: string | null;
  formation: string | null;
  where_found: string | null;
}

export default function SpecimenDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const { data: specimen, isLoading, error } = useQuery<SpecimenDetail>({
    queryKey: ["specimen", id],
    queryFn: async () => {
      const { data, error } = await supabase
        .from("specimen_catalog")
        .select("*")
        .eq("id", id)
        .single();
      if (error) throw error;
      return data as SpecimenDetail;
    },
    enabled: !!id,
  });

  const saveToCollection = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to save specimens");
      const { error } = await supabase
        .from("rockscout_collection")
        .insert({ user_id: user.id, specimen_id: id });
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Added to your collection");
      queryClient.invalidateQueries({ queryKey: ["collection"] });
    },
    onError: (err) => {
      toast.error(err instanceof Error ? err.message : "Failed to save");
    },
  });

  const addToWishlist = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to save specimens");
      const { error } = await supabase
        .from("rockscout_wishlist")
        .insert({ user_id: user.id, specimen_id: id });
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Added to your wishlist");
      queryClient.invalidateQueries({ queryKey: ["wishlist"] });
    },
    onError: (err) => {
      toast.error(err instanceof Error ? err.message : "Failed to save");
    },
  });

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <Loader2 className="h-6 w-6 animate-spin text-primary" />
      </div>
    );
  }

  if (error || !specimen) {
    return (
      <div className="space-y-4">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate("/app/specimens")}
          className="gap-2"
        >
          <ArrowLeft className="h-4 w-4" />
          Back
        </Button>
        <div className="flex items-center gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
          <AlertCircle className="h-4 w-4" />
          Specimen not found.
        </div>
      </div>
    );
  }

  const properties = [
    { label: "Category", value: specimen.category },
    { label: "Colors", value: specimen.colors },
    { label: "Hardness", value: specimen.hardness },
    { label: "Luster", value: specimen.luster },
    { label: "Crystal System", value: specimen.crystal_system },
    { label: "Streak", value: specimen.streak },
    { label: "Rarity", value: specimen.rarity },
  ].filter((p) => p.value && p.value !== "—");

  return (
    <div className="space-y-5">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => navigate("/app/specimens")}
        className="gap-2"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to database
      </Button>

      {specimen.image_url && (
        <div className="relative overflow-hidden rounded-xl border border-border">
          <OptimizedImage
            src={specimen.image_url}
            alt={specimen.name}
            loading="eager"
            className="max-h-[400px] w-full object-cover"
          />
        </div>
      )}

      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          {specimen.name}
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">{specimen.tagline}</p>
      </div>

      {user && (
        <div className="flex gap-3">
          <Button
            onClick={() => saveToCollection.mutate()}
            disabled={saveToCollection.isPending}
            className="gap-2"
          >
            <Plus className="h-4 w-4" />
            {saveToCollection.isPending ? "Saving..." : "Add to Collection"}
          </Button>
          <Button
            onClick={() => addToWishlist.mutate()}
            disabled={addToWishlist.isPending}
            variant="outline"
            className="gap-2"
          >
            <Heart className="h-4 w-4" />
            {addToWishlist.isPending ? "Saving..." : "Wishlist"}
          </Button>
        </div>
      )}

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
        <div className="rounded-xl border border-border bg-card p-4">
          <h3 className="mb-3 font-display text-sm font-semibold text-foreground">
            Physical Properties
          </h3>
          <dl className="space-y-2">
            {properties.map((prop) => (
              <div key={prop.label} className="flex justify-between gap-4 text-sm">
                <dt className="shrink-0 text-muted-foreground">{prop.label}</dt>
                <dd className="text-right font-medium text-foreground">
                  {prop.value}
                </dd>
              </div>
            ))}
          </dl>
        </div>

        {specimen.description && (
          <div className="rounded-xl border border-border bg-card p-4">
            <h3 className="mb-2 font-display text-sm font-semibold text-foreground">
              Description
            </h3>
            <p className="text-sm leading-relaxed text-muted-foreground">
              {specimen.description}
            </p>
          </div>
        )}

        {specimen.formation && (
          <div className="rounded-xl border border-border bg-card p-4 md:col-span-2">
            <h3 className="mb-2 font-display text-sm font-semibold text-foreground">
              Formation
            </h3>
            <p className="text-sm leading-relaxed text-muted-foreground">
              {specimen.formation}
            </p>
          </div>
        )}

        {specimen.where_found && (
          <div className="rounded-xl border border-border bg-card p-4 md:col-span-2">
            <h3 className="mb-2 font-display text-sm font-semibold text-foreground">
              Where It&apos;s Found
            </h3>
            <p className="text-sm leading-relaxed text-muted-foreground">
              {specimen.where_found}
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
