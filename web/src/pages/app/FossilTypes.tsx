import { SculptedCard, ScreenScaffold } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";

const FOSSILIZATION_STEPS = [
  { title: "Death", desc: "An organism dies and its remains settle in a low-energy environment — a lakebed, ocean floor, or volcanic ash layer. Scavengers and bacteria may remove soft tissue, leaving only hard parts (bones, shells, teeth)." },
  { title: "Burial", desc: "Sediment rapidly covers the remains, protecting them from scavengers and weather. The faster the burial, the better the preservation. Mudslides, volcanic ash, and river deltas are ideal." },
  { title: "Permineralization", desc: "Groundwater seeps through the sediment and deposits minerals (usually silica or calcite) into the empty spaces within the bone or shell. This can take thousands to millions of years." },
  { title: "Replacement", desc: "The original organic material is gradually replaced by minerals, molecule by molecule. In petrified wood, the cellulose is replaced by silica while the structure is preserved in perfect detail." },
  { title: "Lithification", desc: "The surrounding sediment is compressed and cemented into sedimentary rock. The fossil is now encased in stone, protected from further decay." },
  { title: "Exposure", desc: "Millions of years later, erosion or tectonic activity exposes the fossil at the surface. This is the moment a rockhound or paleontologist can find and collect it." },
];

const FOSSIL_TYPES = [
  { name: "Body Fossils", emoji: "🦴", desc: "The preserved remains of the organism itself — bones, shells, teeth, or entire animals frozen in amber or ice.", accent: "33 38% 64%" },
  { name: "Trace Fossils", emoji: "👣", desc: "Evidence of activity rather than the organism — footprints, burrows, bite marks, coprolites (fossilized dung), and eggs.", accent: "20 62% 65%" },
  { name: "Molds & Casts", emoji: "🐚", desc: "A mold forms when an organism decays and leaves an impression in the rock. A cast forms when minerals fill the mold, creating a replica.", accent: "200 67% 57%" },
  { name: "Petrified Wood", emoji: "🌳", desc: "Wood where every cell has been replaced by silica or other minerals. Growth rings, bark, and even cell walls are preserved in stone.", accent: "147 49% 55%" },
  { name: "Amber", emoji: "🟡", desc: "Fossilized tree resin that traps insects, pollen, and small animals. The oldest amber with inclusions is from the Cretaceous period.", accent: "36 80% 58%" },
  { name: "Carbon Films", emoji: "🌑", desc: "Thin films of carbon left when an organism's volatile components escape, leaving only a black silhouette. Common for leaves and fish.", accent: "0 0% 40%" },
];

export default function FossilTypes() {
  return (
    <ScreenScaffold title="Fossilization & Types">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          How life becomes stone — and the different kinds of fossils
        </p>

        <div>
          <h2 className="mb-3 font-display text-base font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>How Fossilization Works</h2>
          <div className="space-y-3">
            {FOSSILIZATION_STEPS.map((step, i) => (
              <SculptedCard key={step.title} accent="citrine" className="p-4">
                <div className="flex items-start gap-3">
                  <span
                    className="glowing-border flex h-8 w-8 shrink-0 items-center justify-center rounded-full font-display text-sm font-bold"
                    style={{ ["--glow-color" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
                  >
                    {i + 1}
                  </span>
                  <div className="min-w-0 flex-1">
                    <h3 className="font-display text-sm font-bold text-foreground">{step.title}</h3>
                    <p className="mt-1 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{step.desc}</p>
                  </div>
                </div>
              </SculptedCard>
            ))}
          </div>
        </div>

        <div>
          <h2 className="mb-3 font-display text-base font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>Types of Fossils</h2>
          <div className="grid gap-3 sm:grid-cols-2">
            {FOSSIL_TYPES.map((type) => (
              <SculptedCard key={type.name} accent="aqua" className="p-4">
                <div className="flex items-start gap-3">
                  <div
                    className="glowing-border flex h-10 w-10 shrink-0 items-center justify-center rounded-xl text-xl"
                    style={{ ["--glow-color" as string]: type.accent }}
                  >
                    {type.emoji}
                  </div>
                  <div className="min-w-0 flex-1">
                    <h3 className="font-display text-sm font-bold text-foreground">{type.name}</h3>
                    <p className="mt-1 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{type.desc}</p>
                  </div>
                </div>
              </SculptedCard>
            ))}
          </div>
        </div>
      </div>
    </ScreenScaffold>
  );
}
