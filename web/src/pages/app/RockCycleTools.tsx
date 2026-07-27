const STAGES = [
  { title: "Magma Crystallization", emoji: "🌋", desc: "Magma cools slowly underground, forming coarse-grained igneous rocks like granite. Fast-cooling lava forms fine-grained rocks like basalt. Rapid cooling creates glass like obsidian." },
  { title: "Weathering & Erosion", emoji: "🌧️", desc: "Rocks at the surface are broken down by wind, water, ice, and chemical reactions. Quartz is resistant and survives as sand; feldspar breaks down into clay." },
  { title: "Transport & Deposition", emoji: "🌊", desc: "Sediments are carried by rivers, wind, and ice to new locations. Heavy minerals like gold concentrate in stream beds (placers), while lighter minerals travel further." },
  { title: "Lithification", emoji: "🪨", desc: "Sediments are compacted by the weight of overlying layers and cemented by minerals precipitating from groundwater. Sand becomes sandstone, mud becomes shale." },
  { title: "Metamorphism", emoji: "💎", desc: "Rocks buried deep underground are transformed by heat and pressure. Limestone becomes marble, shale becomes slate then schist, sandstone becomes quartzite." },
  { title: "Melting", emoji: "🔥", desc: "At extreme depths, rocks melt back into magma, completing the cycle. Subduction zones carry surface rocks back into the mantle to be recycled." },
];

export default function RockCycleTools() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          The Rock Cycle
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          How rocks transform between igneous, sedimentary, and metamorphic
        </p>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <p className="text-sm leading-relaxed text-muted-foreground">
          The rock cycle is a continuous process with no beginning or end. Any rock can
          be transformed into any other type over geological time. Igneous rocks erode
          into sediments that become sedimentary rocks, which are buried and transformed
          into metamorphic rocks, which melt into magma that crystallizes into new
          igneous rocks. The cycle has operated for over 4 billion years.
        </p>
      </div>

      <div className="space-y-3">
        {STAGES.map((stage, i) => (
          <div key={stage.title}>
            <div className="rounded-xl border border-border bg-card p-4">
              <div className="flex items-start gap-3">
                <span className="text-2xl">{stage.emoji}</span>
                <div className="min-w-0 flex-1">
                  <h3 className="font-display text-sm font-semibold text-foreground">
                    {i + 1}. {stage.title}
                  </h3>
                  <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">{stage.desc}</p>
                </div>
              </div>
            </div>
            {i < STAGES.length - 1 && (
              <div className="flex justify-center py-1 text-primary">↓</div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
