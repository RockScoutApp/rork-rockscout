const SCALE = [
  { number: 1, mineral: "Talc", desc: "Softest known mineral. Can be scratched by a fingernail." },
  { number: 2, mineral: "Gypsum", desc: "Scratched by a fingernail (2.5). Forms desert roses and selenite crystals." },
  { number: 3, mineral: "Calcite", desc: "Scratched by a copper penny (3.5). Fizzes in acid. Common in limestone." },
  { number: 4, mineral: "Fluorite", desc: "Easily scratched by a steel knife. Four perfect cleavage directions." },
  { number: 5, mineral: "Apatite", desc: "Scratched by a steel knife (5.5). The mineral in bones and teeth." },
  { number: 6, mineral: "Orthoclase (Feldspar)", desc: "Scratches glass (5.5). Can be scratched by quartz (7)." },
  { number: 7, mineral: "Quartz", desc: "Scratches glass easily. One of the most common rock-forming minerals." },
  { number: 8, mineral: "Topaz", desc: "Much harder than quartz. Only scratched by corundum and diamond." },
  { number: 9, mineral: "Corundum", desc: "Includes ruby and sapphire. Only diamond is harder." },
  { number: 10, mineral: "Diamond", desc: "Hardest known natural material. Cannot be scratched by anything." },
];

export default function CrystalHardness() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Crystal Hardness Scale
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          The Mohs scale — from talc (1) to diamond (10)
        </p>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <p className="text-sm leading-relaxed text-muted-foreground">
          The Mohs hardness scale, developed by Friedrich Mohs in 1812, ranks 10 reference
          minerals from softest (1) to hardest (10). A mineral can scratch any mineral with
          a lower number. The scale is relative, not linear — diamond (10) is about 4 times
          harder than corundum (9), but corundum is only about 2 times harder than topaz (8).
        </p>
      </div>

      <div className="space-y-2">
        {SCALE.map((item) => (
          <div
            key={item.number}
            className="flex items-center gap-4 rounded-xl border border-border bg-card p-3"
          >
            <span
              className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg font-display text-lg font-bold"
              style={{
                backgroundColor: `hsl(${(10 - item.number) * 20}, 70%, 50%)`,
                color: "white",
              }}
            >
              {item.number}
            </span>
            <div className="min-w-0 flex-1">
              <h3 className="font-display text-sm font-semibold text-foreground">{item.mineral}</h3>
              <p className="text-xs text-muted-foreground">{item.desc}</p>
            </div>
          </div>
        ))}
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <h3 className="text-sm font-semibold text-foreground">Field Reference Objects</h3>
        <ul className="mt-2 space-y-1 text-sm text-muted-foreground">
          <li>• Fingernail: ~2.5</li>
          <li>• Copper penny: ~3.5</li>
          <li>• Steel knife blade: ~5.5</li>
          <li>• Window glass: ~5.5</li>
          <li>• Streak plate (porcelain): ~7</li>
          <li>• Quartz crystal: 7</li>
        </ul>
      </div>
    </div>
  );
}
