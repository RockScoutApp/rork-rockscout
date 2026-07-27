import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import NotFound from "@/pages/NotFound";

const PERIODS: Record<string, { name: string; range: string; emoji: string; desc: string; rocks: string; fossils: string }> = {
  quaternary: { name: "Quaternary", range: "2.6 Mya–Now", emoji: "🧊", desc: "Ice ages and the rise of humans.", rocks: "Glacial till, loess, alluvial deposits", fossils: "Mammoth teeth, bison bones, human artifacts" },
  cretaceous: { name: "Cretaceous", range: "145–66 Mya", emoji: "🦖", desc: "The last age of dinosaurs, ended by an asteroid.", rocks: "Chalk, sandstone, shale, basalt", fossils: "T. rex, Triceratops, ammonites, amber" },
  jurassic: { name: "Jurassic", range: "201–145 Mya", emoji: "🦕", desc: "The golden age of giant dinosaurs.", rocks: "Sandstone, limestone, shale", fossils: "Diplodocus, Stegosaurus, Archaeopteryx" },
  triassic: { name: "Triassic", range: "252–201 Mya", emoji: "🐊", desc: "Recovery and the first dinosaurs.", rocks: "Red sandstone, conglomerate", fossils: "Early dinosaurs, dicynodonts, ichthyosaurs" },
  permian: { name: "Permian", range: "298–252 Mya", emoji: "🦎", desc: "Pangaea and the Great Dying.", rocks: "Red beds, evaporites, sandstone", fossils: "Dimetrodon, synapsids, plant fossils" },
  carboniferous: { name: "Carboniferous", range: "359–298 Mya", emoji: "🌳", desc: "Coal-forming forests and giant insects.", rocks: "Coal, limestone, sandstone", fossils: "Giant dragonflies, ferns, amphibians" },
};

export default function PeriodDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const period = id ? PERIODS[id] : undefined;

  if (!period) return <NotFound />;

  return (
    <div className="space-y-6">
      <Button variant="ghost" size="sm" onClick={() => navigate("/app/geologic-periods")} className="gap-2">
        <ArrowLeft className="h-4 w-4" />
        Back to Geologic Periods
      </Button>

      <div className="rounded-xl border border-border bg-card p-5">
        <div className="flex items-start gap-4">
          <span className="text-4xl">{period.emoji}</span>
          <div className="min-w-0 flex-1">
            <h1 className="font-display text-2xl font-bold text-foreground">{period.name}</h1>
            <p className="mt-1 text-sm font-medium text-primary">{period.range}</p>
            <p className="mt-2 text-sm leading-relaxed text-muted-foreground">{period.desc}</p>
          </div>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="rounded-lg border border-border bg-card p-4">
          <h3 className="text-sm font-semibold text-foreground">Typical Rocks</h3>
          <p className="mt-1.5 text-sm text-muted-foreground">{period.rocks}</p>
        </div>
        <div className="rounded-lg border border-border bg-card p-4">
          <h3 className="text-sm font-semibold text-foreground">Common Fossils</h3>
          <p className="mt-1.5 text-sm text-muted-foreground">{period.fossils}</p>
        </div>
      </div>
    </div>
  );
}
