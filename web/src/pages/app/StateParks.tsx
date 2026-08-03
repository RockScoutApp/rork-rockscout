import { useNavigate } from "react-router-dom";
import { MapPin, ArrowRight, Tent } from "lucide-react";
import { stateParks } from "@/data/locations";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

export default function StateParks() {
  const navigate = useNavigate();

  const byState = stateParks.reduce<Record<string, typeof stateParks>>((acc, park) => {
    (acc[park.state] ??= []).push(park);
    return acc;
  }, {});

  const states = Object.keys(byState).sort();

  return (
    <ScreenScaffold title="State Parks">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          {stateParks.length} state parks with rockhounding and geological relevance
        </p>

        {states.map((state) => (
          <div key={state}>
            <h2 className="mb-2 font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>{state}</h2>
            <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
              {byState[state].map((park) => (
                <SculptedCard
                  key={park.id}
                  accent="success"
                  interactive
                  className="overflow-hidden"
                  onClick={() => navigate(`/app/state-park/${park.id}`)}
                >
                  <div className="flex flex-col gap-2 p-4">
                    <h3 className="font-display text-sm font-bold text-foreground">{park.name}</h3>
                    <p className="flex items-center gap-1 text-xs" style={{ color: `hsl(${CITRINE_HEX})` }}>
                      <MapPin className="h-3 w-3" />{park.region}
                    </p>
                    <p className="line-clamp-2 text-xs text-[hsl(var(--text-mid))]">{park.description}</p>
                    <div className="flex items-center gap-2">
                      {park.hasCamping && (
                        <TagChip accent={`hsl(${AQUA_HEX})`}>
                          <Tent className="h-3 w-3" />
                          Camping
                        </TagChip>
                      )}
                      <span className="text-xs text-muted-foreground">{park.feeInfo}</span>
                    </div>
                  </div>
                </SculptedCard>
              ))}
            </div>
          </div>
        ))}

        {stateParks.length === 0 && (
          <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <MapPin className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">No state parks available.</p>
          </SculptedCard>
        )}
      </div>
    </ScreenScaffold>
  );
}
