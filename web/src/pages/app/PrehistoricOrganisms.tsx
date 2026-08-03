import { Bone } from "lucide-react";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const CYAN_HEX = "174 100% 45%";

const ORGANISMS = [
  { name: "Dinosaurs", emoji: "🦖", era: "Mesozoic (252–66 Mya)", desc: "The dominant terrestrial vertebrates for over 160 million years, from the small bipedal predators of the Triassic to the colossal sauropods of the Jurassic.", accent: "147 49% 55%" },
  { name: "Pterosaurs", emoji: "🦇", era: "Mesozoic (228–66 Mya)", desc: "The first vertebrates to achieve powered flight. Ranged from sparrow-sized insectivores to Quetzalcoatlus, with a 33-foot wingspan.", accent: "265 47% 67%" },
  { name: "Marine Reptiles", emoji: "🐍", era: "Mesozoic (252–66 Mya)", desc: "Ichthyosaurs, plesiosaurs, and mosasaurs — air-breathing predators that ruled the Mesozoic seas.", accent: "200 67% 57%" },
  { name: "Trilobites", emoji: "🦀", era: "Paleozoic (521–252 Mya)", desc: "Arthropods that thrived for over 270 million years. Over 20,000 species are known from fossils.", accent: "36 80% 58%" },
  { name: "Ammonites", emoji: "🐚", era: "Devonian–Cretaceous (400–66 Mya)", desc: "Shelled cephalopods with spiral shells. Their suture patterns are so distinctive they're used as index fossils.", accent: "20 62% 65%" },
  { name: "Crinoids", emoji: "🌿", era: "Ordovician–Present (485 Mya–now)", desc: "Sea lilies — echinoderms that attached to the seafloor with a stem and fed with feathery arms.", accent: "147 49% 55%" },
  { name: "Bryozoans", emoji: "🌊", era: "Ordovician–Present (485 Mya–now)", desc: "Colonial 'moss animals' that built reef-like structures. Each colony is made of tiny zooids.", accent: "200 67% 57%" },
  { name: "Brachiopods", emoji: "🪨", era: "Cambrian–Present (538 Mya–now)", desc: "Lamp shells — bivalve-like animals with two unequal shells. Dominant filter-feeders of the Paleozoic.", accent: "265 47% 67%" },
  { name: "Corals", emoji: "🧵", era: "Cambrian–Present (538 Mya–now)", desc: "Colonial animals that build calcium carbonate skeletons. Rugose and tabulate corals are common fossils.", accent: "4 70% 55%" },
  { name: "Sharks", emoji: "🦈", era: "Devonian–Present (419 Mya–now)", desc: "Cartilaginous fish whose teeth are among the most common fossils. Megalodon teeth can be 7 inches long.", accent: "174 100% 45%" },
  { name: "Plants", emoji: "🌳", era: "Devonian–Present (419 Mya–now)", desc: "From giant Carboniferous clubmosses to petrified wood — plant fossils tell the story of life on land.", accent: "147 49% 55%" },
];

export default function PrehistoricOrganisms() {
  return (
    <ScreenScaffold title="Prehistoric Organisms">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Life through the ages — from the Cambrian explosion to the Ice Age
        </p>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {ORGANISMS.map((org) => (
            <SculptedCard key={org.name} accent="aqua" className="p-4">
              <div className="flex items-start gap-3">
                <div
                  className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl"
                  style={{ ["--glow-color" as string]: org.accent }}
                >
                  {org.emoji}
                </div>
                <div className="min-w-0 flex-1">
                  <h3 className="font-display text-sm font-bold text-foreground">{org.name}</h3>
                  <TagChip accent={`hsl(${org.accent})`}>{org.era}</TagChip>
                  <p className="mt-2 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{org.desc}</p>
                </div>
              </div>
            </SculptedCard>
          ))}
        </div>
      </div>
    </ScreenScaffold>
  );
}
