import { Sparkles } from "lucide-react";

const FACTS = [
  { title: "The Oldest Rocks", body: "The Acasta Gneiss in Canada is about 4.03 billion years old — nearly as old as the Earth itself. Some zircon crystals from Australia are 4.4 billion years old." },
  { title: "Rocks from Space", body: "Meteorites are the oldest rocks you can hold — some formed 4.56 billion years ago, before the Earth even existed. Chondrites contain the primordial material of the solar system." },
  { title: "Living Rocks", body: "Stromatolites are rocks built by living cyanobacteria. They're among the oldest evidence of life on Earth, dating back 3.5 billion years. You can find them forming today in Shark Bay, Australia." },
  { title: "Color from Impurities", body: "Pure quartz is clear. A tiny amount of iron turns it purple (amethyst), while irradiation turns it smoky. The entire rainbow of agate colors comes from trace impurities." },
  { title: "Pressure Cooker", body: "Diamonds form at depths of 150+ km under pressure 50,000 times atmospheric. They're brought to the surface by deep volcanic pipes called kimberlites — named after Kimberley, South Africa." },
  { title: "Time in Stone", body: "Petrified wood forms when silica-rich groundwater replaces organic material cell by cell. The process takes millions of years but preserves the tree's growth rings in perfect detail." },
  { title: "Magnetic Memory", body: "Magnetic minerals in basalt record the Earth's magnetic field direction when the rock cools. This 'paleomagnetism' proved plate tectonics and revealed that the magnetic poles flip periodically." },
  { title: "Rocks That Glow", body: "Fluorescent minerals absorb UV light and re-emit visible light. The most famous is fluorite (which gave the phenomenon its name). Yooperlite — a syenite rock with fluorescent sodalite — glows orange under UV." },
];

export default function RocksAreAmazing() {
  return (
    <div className="space-y-5">
      <div className="flex items-center gap-3">
        <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/15">
          <Sparkles className="h-6 w-6 text-primary" />
        </div>
        <div>
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            Rocks Are Amazing
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">
            Fascinating facts about the rocks beneath our feet
          </p>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        {FACTS.map((fact) => (
          <div key={fact.title} className="rounded-xl border border-border bg-card p-4">
            <h3 className="font-display text-sm font-semibold text-primary">{fact.title}</h3>
            <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">{fact.body}</p>
          </div>
        ))}
      </div>

      <div className="rounded-xl border border-primary/30 bg-primary/5 p-5 text-center">
        <p className="text-sm font-medium text-primary">
          Every rock tells a story — of fire, pressure, time, and the forces that shaped our planet.
        </p>
      </div>
    </div>
  );
}
