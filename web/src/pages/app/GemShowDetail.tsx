import { useParams, useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  Calendar,
  MapPin,
  ExternalLink,
  Phone,
  Mail,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { gemShows } from "@/data/locations";
import NotFound from "@/pages/NotFound";

export default function GemShowDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const show = id ? gemShows.find((s) => s.id === id) : undefined;

  if (!show) return <NotFound />;

  return (
    <div className="space-y-6">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => navigate("/app/gem-shows")}
        className="gap-2"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to Gem Shows
      </Button>

      <div className="rounded-xl border border-border bg-card p-5">
        <div className="flex items-start gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/15">
            <Calendar className="h-6 w-6 text-primary" />
          </div>
          <div className="min-w-0 flex-1">
            <h1 className="font-display text-2xl font-bold text-foreground">
              {show.name}
            </h1>
            <p className="mt-1 flex items-center gap-1.5 text-sm text-muted-foreground">
              <MapPin className="h-3.5 w-3.5" />
              {show.venue}, {show.city}, {show.state}
            </p>
          </div>
        </div>

        <div className="mt-4 flex flex-wrap gap-2">
          <span className="rounded-full bg-primary/15 px-3 py-1 text-xs font-medium text-primary">
            {show.monthLabel}
          </span>
          <span className="rounded-full bg-amber-500/15 px-3 py-1 text-xs font-medium text-amber-600">
            {show.dateRange}
          </span>
          {show.isAnnual && (
            <span className="rounded-full bg-muted px-3 py-1 text-xs font-medium text-muted-foreground">
              Annual Event
            </span>
          )}
        </div>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <h3 className="text-sm font-semibold text-foreground">About</h3>
        <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
          {show.description}
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="rounded-lg border border-border bg-card p-4">
          <h3 className="text-sm font-semibold text-foreground">Entry Fee</h3>
          <p className="mt-1.5 text-sm text-muted-foreground">{show.entryFee}</p>
        </div>
        <div className="rounded-lg border border-border bg-card p-4">
          <h3 className="text-sm font-semibold text-foreground">Location</h3>
          <p className="mt-1.5 text-sm text-muted-foreground">
            {show.venue}
          </p>
          <p className="text-sm text-muted-foreground">
            {show.city}, {show.state}
          </p>
        </div>
      </div>

      <div className="flex flex-wrap gap-3">
        <a href={show.website} target="_blank" rel="noopener noreferrer">
          <Button variant="outline" className="gap-2">
            <ExternalLink className="h-4 w-4" />
            Visit Website
          </Button>
        </a>
        {show.phone && (
          <a href={`tel:${show.phone}`}>
            <Button variant="outline" className="gap-2">
              <Phone className="h-4 w-4" />
              {show.phone}
            </Button>
          </a>
        )}
        {show.email && (
          <a href={`mailto:${show.email}`}>
            <Button variant="outline" className="gap-2">
              <Mail className="h-4 w-4" />
              Email
            </Button>
          </a>
        )}
      </div>
    </div>
  );
}
