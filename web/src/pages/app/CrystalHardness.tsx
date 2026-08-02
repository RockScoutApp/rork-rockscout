import { SculptedCard, ScreenScaffold } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";

const SCALE = [
  { number: 1, mineral: "Talc", desc: "Softest known mineral. Can be scratched by a fingernail.", accent: "150 20% 80%" },
  { number: 2, mineral: "Gypsum", desc: "Scratched by a fingernail (2.5). Forms desert roses and selenite crystals.", accent: "150 25% 72%" },
  { number: 3, mineral: "Calcite", desc: "Scratched by a copper penny (3.5). Fizzes in acid. Common in limestone.", accent: "120 30% 65%" },
  { number: 4, mineral: "Fluorite", desc: "Easily scratched by a steel knife. Four perfect cleavage directions.", accent: "200 40% 62%" },
  { number: 5, mineral: "Apatite", desc: "Scratched by a steel knife (5.5). The mineral in bones and teeth.", accent: "190 45% 55%" },
  { number: 6, mineral: "Orthoclase (Feldspar)", desc: "Scratches glass (5.5). Can be scratched by quartz (7).", accent: "170 40% 50%" },
  { number: 7, mineral: "Quartz", desc: "Scratches glass easily. One of the most common rock-forming minerals.", accent: "140 35% 45%" },
  { number: 8, mineral: "Topaz", desc: "Much harder than quartz. Only scratched by corundum and diamond.", accent: "120 35% 40%" },
  { number: 9, mineral: "Corundum", desc: "Includes ruby and sapphire. Only diamond is harder.", accent: "100 30% 35%" },
  { number: 10, mineral: "Diamond", desc: "Hardest known natural material. Cannot be scratched by anything.", accent: "80 25% 30%" },
];

const FIELD_REFERENCES = [
  "Fingernail: ~2.5",
  "Copper penny: ~3.5",
  "Steel knife blade: ~5.5",
  "Window glass: ~5.5",
  "Streak plate (porcelain): ~7",
  "Quartz crystal: 7",
];

export default function CrystalHardness() {
  return (
    <ScreenScaffold title="Crystal Hardness Scale" onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          The Mohs scale — from talc (1) to diamond (10)
        </p>

        <SculptedCard accent="aqua" className="p-4">
          <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">
            The Mohs hardness scale, developed by Friedrich Mohs in 1812, ranks 10 reference
            minerals from softest (1) to hardest (10). A mineral can scratch any mineral with
            a lower number. The scale is relative, not linear — diamond (10) is about 4 times
            harder than corundum (9), but corundum is only about 2 times harder than topaz (8).
          </p>
        </SculptedCard>

        <div className="space-y-2">
          {SCALE.map((item) => (
            <SculptedCard key={item.number} accent="citrine" className="p-3">
              <div className="flex items-center gap-4">
                <span
                  className="glowing-border flex h-10 w-10 shrink-0 items-center justify-center rounded-lg font-display text-lg font-bold"
                  style={{
                    ["--glow-color" as string]: item.accent,
                    backgroundColor: `hsl(${item.accent} / 0.2)`,
                    color: `hsl(${item.accent})`,
                  }}
                >
                  {item.number}
                </span>
                <div className="min-w-0 flex-1">
                  <h3 className="font-display text-sm font-bold text-foreground">{item.mineral}</h3>
                  <p className="text-xs text-[hsl(var(--text-mid))]">{item.desc}</p>
                </div>
              </div>
            </SculptedCard>
          ))}
        </div>

        <SculptedCard accent="citrine" glowing className="p-4">
          <h3 className="text-sm font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>Field Reference Objects</h3>
          <ul className="mt-2 space-y-1 text-xs text-[hsl(var(--text-mid))]">
            {FIELD_REFERENCES.map((ref) => (
              <li key={ref} className="flex items-center gap-2">
                <span style={{ color: `hsl(${CITRINE_HEX})` }}>•</span>
                {ref}
              </li>
            ))}
          </ul>
        </SculptedCard>
      </div>
    </ScreenScaffold>
  );
}
