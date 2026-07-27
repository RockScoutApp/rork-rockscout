const PERIODS = [
  { id: "quaternary", name: "Quaternary", era: "Cenozoic", range: "2.6 Mya–Now", emoji: "🧊", desc: "Ice ages and the rise of humans. Mammoths, saber-toothed cats, and giant ground sloths lived during the Pleistocene, then went extinct ~10,000 years ago." },
  { id: "neogene", name: "Neogene", era: "Cenozoic", range: "23–2.6 Mya", emoji: "🦣", desc: "Grasslands spread across the continents. Mammals diversified enormously — horses, elephants, and early hominids evolved during this period." },
  { id: "paleogene", name: "Paleogene", era: "Cenozoic", range: "66–23 Mya", emoji: "🐀", desc: "After the K-Pg extinction, mammals and birds diversified to fill the niches left by dinosaurs. The first whales took to the sea." },
  { id: "cretaceous", name: "Cretaceous", era: "Mesozoic", range: "145–66 Mya", emoji: "🦖", desc: "The last age of dinosaurs. T. rex, Triceratops, and Velociraptor lived here. Flowering plants appeared. Ended with the Chicxulub asteroid impact." },
  { id: "jurassic", name: "Jurassic", era: "Mesozoic", range: "201–145 Mya", emoji: "🦕", desc: "The golden age of dinosaurs. Giant sauropods like Brachiosaurus and Diplodocus browsed conifer forests. Archaeopteryx — the first bird — appeared." },
  { id: "triassic", name: "Triassic", era: "Mesozoic", range: "252–201 Mya", emoji: "🐊", desc: "Recovery from the Permian extinction. The first dinosaurs and the first mammals appeared. Ended with another mass extinction." },
  { id: "permian", name: "Permian", era: "Paleozoic", range: "298–252 Mya", emoji: "🦎", desc: "Pangaea — the supercontinent — formed. Synapsids (mammal ancestors) dominated. Ended with the Great Dying, the largest mass extinction ever." },
  { id: "carboniferous", name: "Carboniferous", era: "Paleozoic", range: "359–298 Mya", emoji: "🌳", desc: "Giant trees and ferns formed vast coal-forming forests. Giant dragonflies with 2-foot wingspans ruled the air. Amphibians were the top land predators." },
  { id: "devonian", name: "Devonian", era: "Paleozoic", range: "419–359 Mya", emoji: "🐟", desc: "The Age of Fishes. Armored placoderms and giant sharks ruled the seas. The first tetrapods crawled onto land, and the first forests appeared." },
  { id: "silurian", name: "Silurian", era: "Paleozoic", range: "443–419 Mya", emoji: "🪸", desc: "Reefs recovered from the Ordovician extinction. The first land plants and first air-breathing animals (millipedes) appeared." },
  { id: "ordovician", name: "Ordovician", era: "Paleozoic", range: "485–443 Mya", emoji: "🦀", desc: "Marine life diversified enormously. Trilobites, brachiopods, and the first jawless fish thrived. Ended with a mass extinction from a sudden ice age." },
  { id: "cambrian", name: "Cambrian", era: "Paleozoic", range: "538–485 Mya", emoji: "🦐", desc: "The Cambrian Explosion — almost every major animal group appeared in the fossil record. Trilobites ruled the seas. The first vertebrates appeared." },
];

export default function GeologicPeriods() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Geologic Periods
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          12 periods from the Cambrian Explosion to today
        </p>
      </div>

      <div className="space-y-2">
        {PERIODS.map((period) => (
          <div key={period.id} className="rounded-xl border border-border bg-card p-4">
            <div className="flex items-start gap-3">
              <span className="text-2xl">{period.emoji}</span>
              <div className="min-w-0 flex-1">
                <div className="flex items-center justify-between gap-2">
                  <h3 className="font-display text-sm font-semibold text-foreground">{period.name}</h3>
                  <span className="shrink-0 text-xs font-medium text-primary">{period.range}</span>
                </div>
                <span className="text-[10px] font-medium text-muted-foreground">{period.era}</span>
                <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">{period.desc}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
