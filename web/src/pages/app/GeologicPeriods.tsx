import { useNavigate } from "react-router-dom";
import { Bone } from "lucide-react";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";

const PERIODS = [
  { id: "quaternary", name: "Quaternary", era: "Cenozoic", range: "2.6 Mya–Now", emoji: "🧊", desc: "Ice ages and the rise of humans. Mammoths, saber-toothed cats, and giant ground sloths lived during the Pleistocene, then went extinct ~10,000 years ago.", accent: "174 100% 45%" },
  { id: "neogene", name: "Neogene", era: "Cenozoic", range: "23–2.6 Mya", emoji: "🦣", desc: "Grasslands spread across the continents. Mammals diversified enormously — horses, elephants, and early hominids evolved during this period.", accent: "147 49% 55%" },
  { id: "paleogene", name: "Paleogene", era: "Cenozoic", range: "66–23 Mya", emoji: "🐀", desc: "After the K-Pg extinction, mammals and birds diversified to fill the niches left by dinosaurs. The first whales took to the sea.", accent: "36 80% 58%" },
  { id: "cretaceous", name: "Cretaceous", era: "Mesozoic", range: "145–66 Mya", emoji: "🦖", desc: "The last age of dinosaurs. T. rex, Triceratops, and Velociraptor lived here. Flowering plants appeared. Ended with the Chicxulub asteroid impact.", accent: "147 49% 55%" },
  { id: "jurassic", name: "Jurassic", era: "Mesozoic", range: "201–145 Mya", emoji: "🦕", desc: "The golden age of dinosaurs. Giant sauropods like Brachiosaurus and Diplodocus browsed conifer forests. Archaeopteryx — the first bird — appeared.", accent: "265 47% 67%" },
  { id: "triassic", name: "Triassic", era: "Mesozoic", range: "252–201 Mya", emoji: "🐊", desc: "Recovery from the Permian extinction. The first dinosaurs and the first mammals appeared. Ended with another mass extinction.", accent: "20 62% 65%" },
  { id: "permian", name: "Permian", era: "Paleozoic", range: "298–252 Mya", emoji: "🦎", desc: "Pangaea — the supercontinent — formed. Synapsids (mammal ancestors) dominated. Ended with the Great Dying, the largest mass extinction ever.", accent: "4 70% 55%" },
  { id: "carboniferous", name: "Carboniferous", era: "Paleozoic", range: "359–298 Mya", emoji: "🌳", desc: "Giant trees and ferns formed vast coal-forming forests. Giant dragonflies with 2-foot wingspans ruled the air. Amphibians were the top land predators.", accent: "147 49% 55%" },
  { id: "devonian", name: "Devonian", era: "Paleozoic", range: "419–359 Mya", emoji: "🐟", desc: "The Age of Fishes. Armored placoderms and giant sharks ruled the seas. The first tetrapods crawled onto land, and the first forests appeared.", accent: "200 67% 57%" },
  { id: "silurian", name: "Silurian", era: "Paleozoic", range: "443–419 Mya", emoji: "🪸", desc: "Reefs recovered from the Ordovician extinction. The first land plants and first air-breathing animals (millipedes) appeared.", accent: "174 100% 45%" },
  { id: "ordovician", name: "Ordovician", era: "Paleozoic", range: "485–443 Mya", emoji: "🦀", desc: "Marine life diversified enormously. Trilobites, brachiopods, and the first jawless fish thrived. Ended with a mass extinction from a sudden ice age.", accent: "265 47% 67%" },
  { id: "cambrian", name: "Cambrian", era: "Paleozoic", range: "538–485 Mya", emoji: "🦐", desc: "The Cambrian Explosion — almost every major animal group appeared in the fossil record. Trilobites ruled the seas. The first vertebrates appeared.", accent: "36 80% 58%" },
];

export default function GeologicPeriods() {
  const navigate = useNavigate();
  return (
    <ScreenScaffold title="Geologic Periods" onBack={() => navigate("/app/geo-time-scale")}>
      <div className="space-y-4 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          12 periods from the Cambrian Explosion to today
        </p>

        <div className="space-y-3">
          {PERIODS.map((period) => (
            <SculptedCard key={period.id} accent="aqua" interactive className="overflow-hidden"
              onClick={() => navigate(`/app/period/${period.id}`)}>
              <div className="flex items-start gap-3 p-4">
                <div
                  className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl"
                  style={{ ["--glow-color" as string]: period.accent }}
                >
                  {period.emoji}
                </div>
                <div className="min-w-0 flex-1">
                  <div className="flex items-center justify-between gap-2">
                    <h3 className="font-display text-sm font-bold text-foreground">{period.name}</h3>
                    <TagChip accent={`hsl(${period.accent})`}>{period.range}</TagChip>
                  </div>
                  <span className="text-[10px] font-medium text-muted-foreground">{period.era} Era</span>
                  <p className="mt-1.5 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{period.desc}</p>
                </div>
              </div>
            </SculptedCard>
          ))}
        </div>
      </div>
    </ScreenScaffold>
  );
}
