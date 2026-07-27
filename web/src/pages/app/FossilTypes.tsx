const FOSSILIZATION_STEPS = [
  { title: "Death", desc: "An organism dies and its remains settle in a low-energy environment — a lakebed, ocean floor, or volcanic ash layer. Scavengers and bacteria may remove soft tissue, leaving only hard parts (bones, shells, teeth)." },
  { title: "Burial", desc: "Sediment rapidly covers the remains, protecting them from scavengers and weather. The faster the burial, the better the preservation. Mudslides, volcanic ash, and river deltas are ideal." },
  { title: "Permineralization", desc: "Groundwater seeps through the sediment and deposits minerals (usually silica or calcite) into the empty spaces within the bone or shell. This can take thousands to millions of years." },
  { title: "Replacement", desc: "The original organic material is gradually replaced by minerals, molecule by molecule. In petrified wood, the cellulose is replaced by silica while the structure is preserved in perfect detail." },
  { title: "Lithification", desc: "The surrounding sediment is compressed and cemented into sedimentary rock. The fossil is now encased in stone, protected from further decay." },
  { title: "Exposure", desc: "Millions of years later, erosion or tectonic activity exposes the fossil at the surface. This is the moment a rockhound or paleontologist can find and collect it." },
];

const FOSSIL_TYPES = [
  { name: "Body Fossils", emoji: "🦴", desc: "The preserved remains of the organism itself — bones, shells, teeth, or entire animals frozen in amber or ice." },
  { name: "Trace Fossils", emoji: "👣", desc: "Evidence of activity rather than the organism — footprints, burrows, bite marks, coprolites (fossilized dung), and eggs." },
  { name: "Molds & Casts", emoji: "🐚", desc: "A mold forms when an organism decays and leaves an impression in the rock. A cast forms when minerals fill the mold, creating a replica." },
  { name: "Petrified Wood", emoji: "🌳", desc: "Wood where every cell has been replaced by silica or other minerals. Growth rings, bark, and even cell walls are preserved in stone." },
  { name: "Amber", emoji: "🟡", desc: "Fossilized tree resin that traps insects, pollen, and small animals. The oldest amber with inclusions is from the Cretaceous period." },
  { name: "Carbon Films", emoji: "🌑", desc: "Thin films of carbon left when an organism's volatile components escape, leaving only a black silhouette. Common for leaves and fish." },
];

export default function FossilTypes() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Fossilization & Types
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          How life becomes stone — and the different kinds of fossils
        </p>
      </div>

      <h2 className="font-display text-lg font-bold text-foreground">How Fossilization Works</h2>
      <div className="space-y-2">
        {FOSSILIZATION_STEPS.map((step, i) => (
          <div key={step.title} className="rounded-xl border border-border bg-card p-4">
            <div className="flex items-start gap-3">
              <span className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-primary/15 font-display text-sm font-bold text-primary">
                {i + 1}
              </span>
              <div className="min-w-0 flex-1">
                <h3 className="font-display text-sm font-semibold text-foreground">{step.title}</h3>
                <p className="mt-1 text-sm leading-relaxed text-muted-foreground">{step.desc}</p>
              </div>
            </div>
          </div>
        ))}
      </div>

      <h2 className="font-display text-lg font-bold text-foreground">Types of Fossils</h2>
      <div className="grid gap-3 sm:grid-cols-2">
        {FOSSIL_TYPES.map((type) => (
          <div key={type.name} className="rounded-xl border border-border bg-card p-4">
            <div className="flex items-start gap-3">
              <span className="text-2xl">{type.emoji}</span>
              <div className="min-w-0 flex-1">
                <h3 className="font-display text-sm font-semibold text-foreground">{type.name}</h3>
                <p className="mt-1 text-xs leading-relaxed text-muted-foreground">{type.desc}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
