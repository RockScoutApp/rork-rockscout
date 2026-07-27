import { Bone } from "lucide-react";

const ORGANISMS = [
  { name: "Dinosaurs", emoji: "🦖", era: "Mesozoic (252–66 Mya)", desc: "The dominant terrestrial vertebrates for over 160 million years, from the small bipedal predators of the Triassic to the colossal sauropods of the Jurassic." },
  { name: "Pterosaurs", emoji: "🦇", era: "Mesozoic (228–66 Mya)", desc: "The first vertebrates to achieve powered flight. Ranged from sparrow-sized insectivores to Quetzalcoatlus, with a 33-foot wingspan." },
  { name: "Marine Reptiles", emoji: "🐍", era: "Mesozoic (252–66 Mya)", desc: "Ichthyosaurs, plesiosaurs, and mosasaurs — air-breathing predators that ruled the Mesozoic seas." },
  { name: "Trilobites", emoji: "🦀", era: "Paleozoic (521–252 Mya)", desc: "Arthropods that thrived for over 270 million years. Over 20,000 species are known from fossils." },
  { name: "Ammonites", emoji: "🐚", era: "Devonian–Cretaceous (400–66 Mya)", desc: "Shelled cephalopods with spiral shells. Their suture patterns are so distinctive they're used as index fossils." },
  { name: "Crinoids", emoji: "🌿", era: "Ordovician–Present (485 Mya–now)", desc: "Sea lilies — echinoderms that attached to the seafloor with a stem and fed with feathery arms." },
  { name: "Bryozoans", emoji: "🌊", era: "Ordovician–Present (485 Mya–now)", desc: "Colonial 'moss animals' that built reef-like structures. Each colony is made of tiny zooids." },
  { name: "Brachiopods", emoji: "🪨", era: "Cambrian–Present (538 Mya–now)", desc: "Lamp shells — bivalve-like animals with two unequal shells. Dominant filter-feeders of the Paleozoic." },
  { name: "Corals", emoji: "🧵", era: "Cambrian–Present (538 Mya–now)", desc: "Colonial animals that build calcium carbonate skeletons. Rugose and tabulate corals are common fossils." },
  { name: "Sharks", emoji: "🦈", era: "Devonian–Present (419 Mya–now)", desc: "Cartilaginous fish whose teeth are among the most common fossils. Megalodon teeth can be 7 inches long." },
  { name: "Plants", emoji: "🌳", era: "Devonian–Present (419 Mya–now)", desc: "From giant Carboniferous clubmosses to petrified wood — plant fossils tell the story of life on land." },
];

export default function PrehistoricOrganisms() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Prehistoric Organisms
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Life through the ages — from the Cambrian explosion to the Ice Age
        </p>
      </div>
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {ORGANISMS.map((org) => (
          <div key={org.name} className="rounded-xl border border-border bg-card p-4">
            <div className="flex items-start gap-3">
              <span className="text-3xl">{org.emoji}</span>
              <div className="min-w-0 flex-1">
                <h3 className="font-display text-sm font-semibold text-foreground">{org.name}</h3>
                <p className="text-xs font-medium text-primary">{org.era}</p>
                <p className="mt-1.5 text-xs leading-relaxed text-muted-foreground">{org.desc}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
