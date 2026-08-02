import { useNavigate } from "react-router-dom";
import {
  Layers,
  FlaskConical,
  Gem,
  Repeat,
  ArrowRight,
} from "lucide-react";
import { SculptedCard, ScreenScaffold } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";

interface LandingTileData {
  label: string;
  subtitle: string;
  icon: typeof Layers;
  accent: string;
  route: string;
}

const TILES: LandingTileData[] = [
  {
    label: "Rock Types",
    subtitle: "Igneous, sedimentary & metamorphic",
    icon: Layers,
    accent: "14 75% 57%",
    route: "/app/rock-types",
  },
  {
    label: "Mineral ID",
    subtitle: "8-step field identification process",
    icon: FlaskConical,
    accent: "41 53% 64%",
    route: "/app/mineral-id",
  },
  {
    label: "Crystal Systems",
    subtitle: "7 systems & Mohs hardness scale",
    icon: Gem,
    accent: "265 47% 67%",
    route: "/app/crystal-hardness",
  },
  {
    label: "Rock Cycle & Tools",
    subtitle: "The cycle & essential field kit",
    icon: Repeat,
    accent: "147 49% 55%",
    route: "/app/rock-cycle",
  },
];

export default function RockInfo() {
  const navigate = useNavigate();

  return (
    <ScreenScaffold title="Exploring Geology" onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        {/* Intro card */}
        <SculptedCard accent="citrine" className="p-5">
          <p className="text-sm leading-relaxed text-[hsl(var(--text-mid))]">
            From molten magma to polished gem, every rock and mineral tells a story.
            Explore the five great categories of geology below — plus the tools and
            techniques geologists use to identify what you find in the field.
          </p>
        </SculptedCard>

        {/* 2x2 grid of landing tiles */}
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {TILES.map((tile) => (
            <SculptedCard
              key={tile.label}
              accent="aqua"
              interactive
              className="overflow-hidden p-5"
              onClick={() => navigate(tile.route)}
              style={{ ["--sculpted-accent" as string]: tile.accent }}
            >
              <div className="flex items-start gap-3">
                <div
                  className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl"
                  style={{ ["--glow-color" as string]: tile.accent }}
                >
                  <tile.icon className="h-6 w-6" style={{ color: `hsl(${tile.accent})` }} />
                </div>
                <div className="min-w-0 flex-1">
                  <h3 className="font-display text-sm font-bold text-foreground">
                    {tile.label}
                  </h3>
                  <p className="mt-0.5 text-xs text-[hsl(var(--text-mid))]">
                    {tile.subtitle}
                  </p>
                </div>
                <ArrowRight className="h-4 w-4 shrink-0 text-muted-foreground" />
              </div>
            </SculptedCard>
          ))}
        </div>
      </div>
    </ScreenScaffold>
  );
}
