import { SculptedCard, ScreenScaffold } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";

const STEPS = [
  { title: "Streak Test", desc: "Rub the mineral across an unglazed porcelain plate (streak plate). The color of the powdered residue is the streak, which is often different from the mineral's surface color. For example, hematite is silver-gray but leaves a red-brown streak." },
  { title: "Hardness Test", desc: "Use the Mohs scale (1–10) to determine hardness by scratching the mineral with reference objects: fingernail (2.5), copper penny (3.5), steel nail (5.5), glass (5.5), quartz (7). A mineral can scratch anything with a lower hardness number." },
  { title: "Luster", desc: "Observe how light reflects off the surface. Metallic luster looks like metal. Non-metallic lusters include vitreous (glassy), pearly, silky, earthy, and resinous. Luster is observed on a fresh, unweathered surface." },
  { title: "Cleavage", desc: "Break the mineral and observe how it splits. Cleavage is the tendency to break along flat planes of weak atomic bonding. Mica has perfect cleavage in one direction (peels in sheets), halite cleaves in three directions at 90° (cubes)." },
  { title: "Crystal Form", desc: "If the mineral has grown without interference, observe its natural crystal shape. Quartz forms hexagonal prisms, pyrite forms cubes, calcite forms rhombohedrons. Even when not perfect, crystal habits help with identification." },
  { title: "Acid Test", desc: "Place a drop of dilute hydrochloric acid (HCl) on the mineral. Calcite and other carbonates will fizz (effervesce) as CO₂ is released. This is a definitive test for calcite vs. quartz, which look similar." },
  { title: "Magnetism", desc: "Test with a magnet. Magnetite is strongly magnetic and will attract a magnet. Lodestone is a natural magnet that will attract iron. Most minerals are non-magnetic, so this is a quick elimination test." },
  { title: "Special Properties", desc: "Some minerals have unique properties: fluorite fluoresces under UV light, ulexite transmits images through its fibers ('TV rock'), sodalite glows orange under UV, and some minerals are radioactive (autunite)." },
];

export default function MineralId() {
  return (
    <ScreenScaffold title="Mineral Identification">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Field tests for identifying minerals without lab equipment
        </p>

        <SculptedCard accent="aqua" className="p-4">
          <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">
            Identifying minerals in the field is a process of elimination. No single test
            is definitive — combine several tests to narrow down the possibilities. Always
            test on a fresh surface, as weathering can mask the true properties.
          </p>
        </SculptedCard>

        <div className="space-y-3">
          {STEPS.map((step, i) => (
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
                  <p className="mt-1.5 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{step.desc}</p>
                </div>
              </div>
            </SculptedCard>
          ))}
        </div>

        <SculptedCard accent="citrine" glowing className="p-4">
          <h3 className="text-sm font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>Pro Tip</h3>
          <p className="mt-1.5 text-xs leading-relaxed text-[hsl(var(--text-mid))]">
            Always carry a streak plate, a small magnet, a steel nail, and a hand lens (10x)
            in your field kit. With these four tools plus the acid test, you can identify
            most common minerals in the field.
          </p>
        </SculptedCard>
      </div>
    </ScreenScaffold>
  );
}
