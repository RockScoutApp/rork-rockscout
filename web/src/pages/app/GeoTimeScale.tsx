import { useNavigate } from "react-router-dom";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

const EONS = [
  { name: "Hadean", range: "4.6–4.0 Bya", color: "#3F3930", desc: "The Earth formed from a molten ball of rock. No rocks survived from this time — the surface was repeatedly bombarded by asteroids and remelted.", accent: "0 0% 30%" },
  { name: "Archean", range: "4.0–2.5 Bya", color: "#7A5C3A", desc: "The first solid crust formed. Life appeared — single-celled organisms without oxygen. Stromatolites (layered bacterial mats) are the oldest fossils.", accent: "30 26% 36%" },
  { name: "Proterozoic", range: "2.5 Bya–538 Mya", color: "#9C6B2E", desc: "Oxygen filled the atmosphere (Great Oxidation Event). Complex single-celled life evolved, and by the end, the first multicellular organisms appeared.", accent: "33 53% 39%" },
  { name: "Phanerozoic", range: "538 Mya–Now", color: "#E8A33D", desc: "The age of visible life. Divided into Paleozoic, Mesozoic, and Cenozoic eras. Most of the fossils we find come from this eon.", accent: "36 80% 58%" },
];

const ERAS = [
  { name: "Paleozoic", range: "538–252 Mya", desc: "Cambrian explosion of life. Fish, amphibians, reptiles, and the first forests. Ended with the largest mass extinction ever (Permian-Triassic).", accent: "265 47% 67%" },
  { name: "Mesozoic", range: "252–66 Mya", desc: "The Age of Reptiles. Dinosaurs dominated the land, ichthyosaurs and plesiosaurs ruled the seas, pterosaurs took to the air. Ended with the K-Pg asteroid impact.", accent: "147 49% 55%" },
  { name: "Cenozoic", range: "66 Mya–Now", desc: "The Age of Mammals. After the dinosaurs went extinct, mammals diversified and filled every ecological niche. Humans appeared in the last 2 million years.", accent: "36 80% 58%" },
];

export default function GeoTimeScale() {
  const navigate = useNavigate();
  return (
    <ScreenScaffold title="Geologic Time Scale" onBack={() => navigate("/app/reference")}>
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          4.6 billion years of Earth history, divided into eons and eras
        </p>

        <SculptedCard accent="aqua" className="p-4">
          <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">
            Geologists divide Earth's 4.6-billion-year history into eons, eras, periods,
            and epochs based on major geological and biological events. Mass extinctions
            mark the boundaries between many of these divisions.
          </p>
        </SculptedCard>

        <div>
          <h2 className="mb-3 font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>Eons</h2>
          <div className="space-y-3">
            {EONS.map((eon) => (
              <SculptedCard key={eon.name} accent="aqua" className="overflow-hidden">
                <div
                  className="h-1 w-full"
                  style={{ backgroundColor: eon.color }}
                />
                <div className="p-4">
                  <div className="flex items-center justify-between">
                    <h3 className="font-display text-sm font-bold text-foreground">{eon.name}</h3>
                    <TagChip accent={`hsl(${eon.accent})`}>{eon.range}</TagChip>
                  </div>
                  <p className="mt-1.5 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{eon.desc}</p>
                </div>
              </SculptedCard>
            ))}
          </div>
        </div>

        <div>
          <h2 className="mb-3 font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>Eras of the Phanerozoic</h2>
          <div className="grid gap-3 sm:grid-cols-3">
            {ERAS.map((era) => (
              <SculptedCard key={era.name} accent="citrine" interactive className="overflow-hidden"
                onClick={() => navigate("/app/geologic-periods")}>
                <div className="p-4">
                  <h3 className="font-display text-sm font-bold" style={{ color: `hsl(${era.accent})` }}>{era.name}</h3>
                  <p className="text-xs font-medium text-muted-foreground">{era.range}</p>
                  <p className="mt-2 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{era.desc}</p>
                </div>
              </SculptedCard>
            ))}
          </div>
        </div>
      </div>
    </ScreenScaffold>
  );
}
