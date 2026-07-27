const EONS = [
  { name: "Hadean", range: "4.6–4.0 Bya", color: "#3F3930", desc: "The Earth formed from a molten ball of rock. No rocks survived from this time — the surface was repeatedly bombarded by asteroids and remelted." },
  { name: "Archean", range: "4.0–2.5 Bya", color: "#7A5C3A", desc: "The first solid crust formed. Life appeared — single-celled organisms without oxygen. Stromatolites (layered bacterial mats) are the oldest fossils." },
  { name: "Proterozoic", range: "2.5 Bya–538 Mya", color: "#9C6B2E", desc: "Oxygen filled the atmosphere (Great Oxidation Event). Complex single-celled life evolved, and by the end, the first multicellular organisms appeared." },
  { name: "Phanerozoic", range: "538 Mya–Now", color: "#E8A33D", desc: "The age of visible life. Divided into Paleozoic, Mesozoic, and Cenozoic eras. Most of the fossils we find come from this eon." },
];

const ERAS = [
  { name: "Paleozoic", range: "538–252 Mya", desc: "Cambrian explosion of life. Fish, amphibians, reptiles, and the first forests. Ended with the largest mass extinction ever (Permian-Triassic)." },
  { name: "Mesozoic", range: "252–66 Mya", desc: "The Age of Reptiles. Dinosaurs dominated the land, ichthyosaurs and plesiosaurs ruled the seas, pterosaurs took to the air. Ended with the K-Pg asteroid impact." },
  { name: "Cenozoic", range: "66 Mya–Now", desc: "The Age of Mammals. After the dinosaurs went extinct, mammals diversified and filled every ecological niche. Humans appeared in the last 2 million years." },
];

export default function GeoTimeScale() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Geologic Time Scale
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          4.6 billion years of Earth history, divided into eons and eras
        </p>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <p className="text-sm leading-relaxed text-muted-foreground">
          Geologists divide Earth's 4.6-billion-year history into eons, eras, periods,
          and epochs based on major geological and biological events. Mass extinctions
          mark the boundaries between many of these divisions.
        </p>
      </div>

      <h2 className="font-display text-lg font-bold text-foreground">Eons</h2>
      <div className="space-y-2">
        {EONS.map((eon) => (
          <div
            key={eon.name}
            className="rounded-xl border border-border bg-card p-4"
            style={{ borderLeftColor: eon.color, borderLeftWidth: 4 }}
          >
            <div className="flex items-center justify-between">
              <h3 className="font-display text-sm font-semibold text-foreground">{eon.name}</h3>
              <span className="text-xs font-medium text-primary">{eon.range}</span>
            </div>
            <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">{eon.desc}</p>
          </div>
        ))}
      </div>

      <h2 className="font-display text-lg font-bold text-foreground">Eras of the Phanerozoic</h2>
      <div className="grid gap-3 sm:grid-cols-3">
        {ERAS.map((era) => (
          <div key={era.name} className="rounded-xl border border-border bg-card p-4">
            <h3 className="font-display text-sm font-semibold text-primary">{era.name}</h3>
            <p className="text-xs font-medium text-muted-foreground">{era.range}</p>
            <p className="mt-2 text-xs leading-relaxed text-muted-foreground">{era.desc}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
