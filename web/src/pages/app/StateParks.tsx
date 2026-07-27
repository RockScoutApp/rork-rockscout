import { useNavigate } from "react-router-dom";
import { MapPin, ArrowRight } from "lucide-react";
import { stateParks } from "@/data/locations";

export default function StateParks() {
  const navigate = useNavigate();

  // Group by state
  const byState = stateParks.reduce<Record<string, typeof stateParks>>((acc, park) => {
    (acc[park.state] ??= []).push(park);
    return acc;
  }, {});

  const states = Object.keys(byState).sort();

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          State Parks
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {stateParks.length} state parks with rockhounding and geological relevance
        </p>
      </div>

      {states.map((state) => (
        <div key={state}>
          <h2 className="mb-2 font-display text-base font-semibold text-foreground">{state}</h2>
          <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
            {byState[state].map((park) => (
              <button
                key={park.id}
                onClick={() => navigate(`/app/state-park/${park.id}`)}
                className="group flex flex-col gap-2 rounded-xl border border-border bg-card p-4 text-left transition-all hover:border-primary/40"
              >
                <h3 className="font-display text-sm font-semibold text-foreground">{park.name}</h3>
                <p className="flex items-center gap-1 text-xs text-muted-foreground">
                  <MapPin className="h-3 w-3" />{park.region}
                </p>
                <p className="line-clamp-2 text-xs text-muted-foreground">{park.description}</p>
                <div className="flex items-center gap-2 text-xs">
                  {park.hasCamping && <span className="rounded bg-primary/15 px-1.5 py-0.5 font-medium text-primary">Camping</span>}
                  <span className="text-muted-foreground">{park.feeInfo}</span>
                </div>
                <ArrowRight className="h-4 w-4 text-muted-foreground group-hover:text-primary" />
              </button>
            ))}
          </div>
        </div>
      ))}

      {stateParks.length === 0 && (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <MapPin className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">No state parks available.</p>
        </div>
      )}
    </div>
  );
}
