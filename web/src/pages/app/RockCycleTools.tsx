import { SculptedCard, ScreenScaffold } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";

const STAGES = [
  { title: "Magma Crystallization", emoji: "🌋", desc: "Magma cools slowly underground, forming coarse-grained igneous rocks like granite. Fast-cooling lava forms fine-grained rocks like basalt. Rapid cooling creates glass like obsidian.", accent: "4 70% 55%" },
  { title: "Weathering & Erosion", emoji: "🌧️", desc: "Rocks at the surface are broken down by wind, water, ice, and chemical reactions. Quartz is resistant and survives as sand; feldspar breaks down into clay.", accent: "200 67% 57%" },
  { title: "Transport & Deposition", emoji: "🌊", desc: "Sediments are carried by rivers, wind, and ice to new locations. Heavy minerals like gold concentrate in stream beds (placers), while lighter minerals travel further.", accent: "174 100% 45%" },
  { title: "Lithification", emoji: "🪨", desc: "Sediments are compacted by the weight of overlying layers and cemented by minerals precipitating from groundwater. Sand becomes sandstone, mud becomes shale.", accent: "41 53% 64%" },
  { title: "Metamorphism", emoji: "💎", desc: "Rocks buried deep underground are transformed by heat and pressure. Limestone becomes marble, shale becomes slate then schist, sandstone becomes quartzite.", accent: "265 47% 67%" },
  { title: "Melting", emoji: "🔥", desc: "At extreme depths, rocks melt back into magma, completing the cycle. Subduction zones carry surface rocks back into the mantle to be recycled.", accent: "4 70% 55%" },
];

export default function RockCycleTools() {
  return (
    <ScreenScaffold title="The Rock Cycle">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          How rocks transform between igneous, sedimentary, and metamorphic
        </p>

        <SculptedCard accent="aqua" className="p-4">
          <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">
            The rock cycle is a continuous process with no beginning or end. Any rock can
            be transformed into any other type over geological time. Igneous rocks erode
            into sediments that become sedimentary rocks, which are buried and transformed
            into metamorphic rocks, which melt into magma that crystallizes into new
            igneous rocks. The cycle has operated for over 4 billion years.
          </p>
        </SculptedCard>

        <div className="space-y-3">
          {STAGES.map((stage, i) => (
            <div key={stage.title}>
              <SculptedCard accent="aqua" className="p-4">
                <div className="flex items-start gap-3">
                  <div
                    className="glowing-border flex h-10 w-10 shrink-0 items-center justify-center rounded-xl text-xl"
                    style={{ ["--glow-color" as string]: stage.accent }}
                  >
                    {stage.emoji}
                  </div>
                  <div className="min-w-0 flex-1">
                    <h3 className="font-display text-sm font-bold text-foreground">
                      {i + 1}. {stage.title}
                    </h3>
                    <p className="mt-1.5 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{stage.desc}</p>
                  </div>
                </div>
              </SculptedCard>
              {i < STAGES.length - 1 && (
                <div className="flex justify-center py-1" style={{ color: `hsl(${CITRINE_HEX})` }}>↓</div>
              )}
            </div>
          ))}
        </div>
      </div>
    </ScreenScaffold>
  );
}
