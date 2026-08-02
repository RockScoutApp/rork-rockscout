import { useParams, useNavigate } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Heart, Plus, Loader2, AlertCircle } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { OptimizedImage } from "@/components/OptimizedImage";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

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
      <ScreenScaffold title="Specimen Not Found" onBack={() => navigate("/app/specimens")}>
        <div className="px-4 pb-8">
          <SculptedCard accent="danger" className="flex items-center gap-2 p-4 text-sm" >
            <AlertCircle className="h-4 w-4" style={{ color: "hsl(4 70% 55%)" }} />
            <span style={{ color: "hsl(4 70% 55%)" }}>Specimen not found.</span>
          </SculptedCard>
        </div>
      </ScreenScaffold>
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
    <ScreenScaffold title={specimen.name} onBack={() => navigate("/app/specimens")}>
      <div className="space-y-5 px-4 pb-8">
        {specimen.image_url && (
          <SculptedCard accent="citrine" glowing className="overflow-hidden">
            <div className="relative overflow-hidden">
              <OptimizedImage
                src={specimen.image_url}
                alt={specimen.name}
                loading="eager"
                className="max-h-[400px] w-full object-cover"
              />
            </div>
          </SculptedCard>
        )}

        <div>
          <p className="text-sm text-muted-foreground">{specimen.tagline}</p>
        </div>

        {user && (
          <div className="flex gap-3">
            <SculptedButton
              accent="citrine"
              glowing
              onClick={() => saveToCollection.mutate()}
              disabled={saveToCollection.isPending}
            >
              <Plus className="h-4 w-4" />
              {saveToCollection.isPending ? "Saving..." : "Add to Collection"}
            </SculptedButton>
            <SculptedButton
              accent="amethyst"
              onClick={() => addToWishlist.mutate()}
              disabled={addToWishlist.isPending}
            >
              <Heart className="h-4 w-4" />
              {addToWishlist.isPending ? "Saving..." : "Wishlist"}
            </SculptedButton>
          </div>
        )}

        <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <SculptedCard accent="aqua" className="p-4">
            <h3 className="mb-3 font-display text-sm font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
              Physical Properties
            </h3>
            <dl className="space-y-2">
              {properties.map((prop) => (
                <div key={prop.label} className="flex justify-between gap-4 text-sm">
                  <dt className="shrink-0 text-muted-foreground">{prop.label}</dt>
                  <dd className="text-right font-bold text-foreground">
                    {prop.value}
                  </dd>
                </div>
              ))}
            </dl>
          </SculptedCard>

          {specimen.description && (
            <SculptedCard accent="citrine" className="p-4">
              <h3 className="mb-2 font-display text-sm font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>
                Description
              </h3>
              <p className="text-sm leading-relaxed text-[hsl(var(--text-mid))]">
                {specimen.description}
              </p>
            </SculptedCard>
          )}

          {specimen.formation && (
            <SculptedCard accent="cyan" className="p-4 md:col-span-2">
              <h3 className="mb-2 font-display text-sm font-bold" style={{ color: `hsl(174 100% 45%)` }}>
                Formation
              </h3>
              <p className="text-sm leading-relaxed text-[hsl(var(--text-mid))]">
                {specimen.formation}
              </p>
            </SculptedCard>
          )}

          {specimen.where_found && (
            <SculptedCard accent="success" className="p-4 md:col-span-2">
              <h3 className="mb-2 font-display text-sm font-bold" style={{ color: "hsl(147 49% 55%)" }}>
                Where It&apos;s Found
              </h3>
              <p className="text-sm leading-relaxed text-[hsl(var(--text-mid))]">
                {specimen.where_found}
              </p>
            </SculptedCard>
          )}
        </div>
      </div>
    </ScreenScaffold>
  );
}
