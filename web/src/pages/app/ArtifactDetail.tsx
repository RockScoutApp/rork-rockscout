import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, MapPin, Clock, Hammer, Lightbulb, Users } from "lucide-react";
import { Button } from "@/components/ui/button";
import { findArtifactById } from "@/data/artifacts";
import { OptimizedImage } from "@/components/OptimizedImage";
import NotFound from "@/pages/NotFound";

export default function ArtifactDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const artifact = id ? findArtifactById(id) : undefined;

  if (!artifact) return <NotFound />;

  return (
    <div className="space-y-6">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => navigate("/app/artifacts")}
        className="gap-2"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to Artifacts
      </Button>

      <div className="overflow-hidden rounded-xl border border-border bg-card">
        <div className="relative aspect-[4/3] w-full overflow-hidden bg-muted/20">
          <OptimizedImage
            src={artifact.imageUrl}
            alt={artifact.name}
            loading="eager"
            className="h-full w-full object-cover"
          />
        </div>
        <div className="p-5">
          <div className="flex items-start gap-3">
            <span className="text-3xl">{artifact.emoji}</span>
            <div className="min-w-0 flex-1">
              <h1 className="font-display text-2xl font-bold text-foreground">
                {artifact.name}
              </h1>
              <p className="mt-0.5 text-sm text-muted-foreground">
                {artifact.family} · {artifact.subFamily}
              </p>
            </div>
          </div>
          <p className="mt-3 text-sm font-medium text-primary">
            {artifact.tagline}
          </p>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="rounded-lg border border-border bg-card p-4">
          <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
            <Clock className="h-4 w-4 text-primary" />
            Time Period
          </div>
          <p className="mt-1.5 text-sm text-muted-foreground">
            {artifact.timePeriod}
          </p>
        </div>
        <div className="rounded-lg border border-border bg-card p-4">
          <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
            <Users className="h-4 w-4 text-primary" />
            Culture / Tradition
          </div>
          <p className="mt-1.5 text-sm text-muted-foreground">
            {artifact.tribe}
          </p>
        </div>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <h3 className="text-sm font-semibold text-foreground">Overview</h3>
        <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
          {artifact.description}
        </p>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
          <Hammer className="h-4 w-4 text-primary" />
          How It Was Made
        </div>
        <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
          {artifact.howMade}
        </p>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
          <MapPin className="h-4 w-4 text-primary" />
          Where Found
        </div>
        <div className="mt-2 flex flex-wrap gap-2">
          {artifact.whereFound.map((region) => (
            <span
              key={region}
              className="rounded-full bg-muted px-2.5 py-1 text-xs text-muted-foreground"
            >
              {region}
            </span>
          ))}
        </div>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
          <Lightbulb className="h-4 w-4 text-primary" />
          Fun Facts
        </div>
        <ul className="mt-2 space-y-2">
          {artifact.funFacts.map((fact, i) => (
            <li
              key={i}
              className="flex gap-2 text-sm leading-relaxed text-muted-foreground"
            >
              <span className="text-primary">•</span>
              {fact}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
