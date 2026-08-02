import { useParams, useNavigate } from "react-router-dom";
import { Mountain, Bone, ArrowLeft } from "lucide-react";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";
import NotFound from "@/pages/NotFound";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

const PERIODS: Record<string, { name: string; range: string; emoji: string; desc: string; rocks: string; fossils: string; accent: string }> = {
  quaternary: { name: "Quaternary", range: "2.6 Mya–Now", emoji: "🧊", desc: "Ice ages and the rise of humans.", rocks: "Glacial till, loess, alluvial deposits", fossils: "Mammoth teeth, bison bones, human artifacts", accent: "174 100% 45%" },
  cretaceous: { name: "Cretaceous", range: "145–66 Mya", emoji: "🦖", desc: "The last age of dinosaurs, ended by an asteroid.", rocks: "Chalk, sandstone, shale, basalt", fossils: "T. rex, Triceratops, ammonites, amber", accent: "147 49% 55%" },
  jurassic: { name: "Jurassic", range: "201–145 Mya", emoji: "🦕", desc: "The golden age of giant dinosaurs.", rocks: "Sandstone, limestone, shale", fossils: "Diplodocus, Stegosaurus, Archaeopteryx", accent: "265 47% 67%" },
  triassic: { name: "Triassic", range: "252–201 Mya", emoji: "🐊", desc: "Recovery and the first dinosaurs.", rocks: "Red sandstone, conglomerate", fossils: "Early dinosaurs, dicynodonts, ichthyosaurs", accent: "20 62% 65%" },
  permian: { name: "Permian", range: "298–252 Mya", emoji: "🦎", desc: "Pangaea and the Great Dying.", rocks: "Red beds, evaporites, sandstone", fossils: "Dimetrodon, synapsids, plant fossils", accent: "4 70% 55%" },
  carboniferous: { name: "Carboniferous", range: "359–298 Mya", emoji: "🌳", desc: "Coal-forming forests and giant insects.", rocks: "Coal, limestone, sandstone", fossils: "Giant dragonflies, ferns, amphibians", accent: "147 49% 45%" },
};

export default function PeriodDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const period = id ? PERIODS[id] : undefined;

  if (!period) return <NotFound />;

  return (
    <ScreenScaffold title={period.name} onBack={() => navigate("/app/geologic-periods")}>
      <div className="space-y-5 px-4 pb-8">
        {/* Hero card */}
        <SculptedCard accent="citrine" glowing className="p-5">
          <div className="flex items-start gap-4">
            <div
              className="glowing-border flex h-16 w-16 shrink-0 items-center justify-center rounded-2xl text-4xl"
              style={{ ["--glow-color" as string]: period.accent }}
            >
              {period.emoji}
            </div>
            <div className="min-w-0 flex-1">
              <h1 className="font-display text-2xl font-bold text-foreground">{period.name}</h1>
              <TagChip accent={`hsl(${period.accent})`}>{period.range}</TagChip>
              <p className="mt-2 text-sm leading-relaxed text-[hsl(var(--text-mid))]">{period.desc}</p>
            </div>
          </div>
        </SculptedCard>

        {/* Info cards */}
        <div className="grid gap-4 sm:grid-cols-2">
          <SculptedCard accent="aqua" className="p-4">
            <div className="flex items-start gap-3">
              <div
                className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
                style={{ ["--badge-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
              >
                <Mountain className="h-5 w-5" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-foreground">Typical Rocks</h3>
                <p className="mt-1.5 text-sm text-[hsl(var(--text-mid))]">{period.rocks}</p>
              </div>
            </div>
          </SculptedCard>

          <SculptedCard accent="citrine" className="p-4">
            <div className="flex items-start gap-3">
              <div
                className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
                style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
              >
                <Bone className="h-5 w-5" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-foreground">Common Fossils</h3>
                <p className="mt-1.5 text-sm text-[hsl(var(--text-mid))]">{period.fossils}</p>
              </div>
            </div>
          </SculptedCard>
        </div>
      </div>
    </ScreenScaffold>
  );
}
