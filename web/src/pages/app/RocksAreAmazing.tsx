import { SculptedCard, ScreenScaffold } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";

const FACTS = [
  { title: "The Oldest Rocks", body: "The Acasta Gneiss in Canada is about 4.03 billion years old — nearly as old as the Earth itself. Some zircon crystals from Australia are 4.4 billion years old.", emoji: "🪨" },
  { title: "Rocks from Space", body: "Meteorites are the oldest rocks you can hold — some formed 4.56 billion years ago, before the Earth even existed. Chondrites contain the primordial material of the solar system.", emoji: "☄️" },
  { title: "Living Rocks", body: "Stromatolites are rocks built by living cyanobacteria. They're among the oldest evidence of life on Earth, dating back 3.5 billion years. You can find them forming today in Shark Bay, Australia.", emoji: "🦠" },
  { title: "Color from Impurities", body: "Pure quartz is clear. A tiny amount of iron turns it purple (amethyst), while irradiation turns it smoky. The entire rainbow of agate colors comes from trace impurities.", emoji: "💎" },
  { title: "Pressure Cooker", body: "Diamonds form at depths of 90+ miles under pressure 50,000 times atmospheric. They're brought to the surface by deep volcanic pipes called kimberlites — named after Kimberley, South Africa.", emoji: "💍" },
  { title: "Time in Stone", body: "Petrified wood forms when silica-rich groundwater replaces organic material cell by cell. The process takes millions of years but preserves the tree's growth rings in perfect detail.", emoji: "🌲" },
  { title: "Magnetic Memory", body: "Magnetic minerals in basalt record the Earth's magnetic field direction when the rock cools. This 'paleomagnetism' proved plate tectonics and revealed that the magnetic poles flip periodically.", emoji: "🧲" },
  { title: "Rocks That Glow", body: "Fluorescent minerals absorb UV light and re-emit visible light. The most famous is fluorite (which gave the phenomenon its name). Yooperlite — a syenite rock with fluorescent sodalite — glows orange under UV.", emoji: "✨" },
];

export default function RocksAreAmazing() {
  return (
    <ScreenScaffold title="Rocks Are Amazing">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Fascinating facts about the rocks beneath our feet
        </p>

        <div className="grid gap-4 sm:grid-cols-2">
          {FACTS.map((fact) => (
            <SculptedCard key={fact.title} accent="citrine" className="p-4">
              <div className="flex items-start gap-3">
                <span className="text-2xl shrink-0">{fact.emoji}</span>
                <div>
                  <h3 className="font-display text-sm font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>
                    {fact.title}
                  </h3>
                  <p className="mt-1.5 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{fact.body}</p>
                </div>
              </div>
            </SculptedCard>
          ))}
        </div>

        <SculptedCard accent="citrine" glowing className="p-5 text-center">
          <p className="text-sm font-medium" style={{ color: `hsl(${CITRINE_HEX})` }}>
            Every rock tells a story — of fire, pressure, time, and the forces that shaped our planet.
          </p>
        </SculptedCard>
      </div>
    </ScreenScaffold>
  );
}
