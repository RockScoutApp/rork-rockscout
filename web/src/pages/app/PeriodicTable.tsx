const ELEMENTS: { symbol: string; name: string; number: number; category: string }[] = [
  { symbol: "H", name: "Hydrogen", number: 1, category: "nonmetal" },
  { symbol: "He", name: "Helium", number: 2, category: "noble" },
  { symbol: "Li", name: "Lithium", number: 3, category: "alkali" },
  { symbol: "Be", name: "Beryllium", number: 4, category: "alkaline" },
  { symbol: "B", name: "Boron", number: 5, category: "metalloid" },
  { symbol: "C", name: "Carbon", number: 6, category: "nonmetal" },
  { symbol: "N", name: "Nitrogen", number: 7, category: "nonmetal" },
  { symbol: "O", name: "Oxygen", number: 8, category: "nonmetal" },
  { symbol: "F", name: "Fluorine", number: 9, category: "halogen" },
  { symbol: "Ne", name: "Neon", number: 10, category: "noble" },
  { symbol: "Na", name: "Sodium", number: 11, category: "alkali" },
  { symbol: "Mg", name: "Magnesium", number: 12, category: "alkaline" },
  { symbol: "Al", name: "Aluminum", number: 13, category: "post-metal" },
  { symbol: "Si", name: "Silicon", number: 14, category: "metalloid" },
  { symbol: "P", name: "Phosphorus", number: 15, category: "nonmetal" },
  { symbol: "S", name: "Sulfur", number: 16, category: "nonmetal" },
  { symbol: "Cl", name: "Chlorine", number: 17, category: "halogen" },
  { symbol: "Ar", name: "Argon", number: 18, category: "noble" },
  { symbol: "K", name: "Potassium", number: 19, category: "alkali" },
  { symbol: "Ca", name: "Calcium", number: 20, category: "alkaline" },
  { symbol: "Fe", name: "Iron", number: 26, category: "transition" },
  { symbol: "Cu", name: "Copper", number: 29, category: "transition" },
  { symbol: "Zn", name: "Zinc", number: 30, category: "transition" },
  { symbol: "Ag", name: "Silver", number: 47, category: "transition" },
  { symbol: "Au", name: "Gold", number: 79, category: "transition" },
  { symbol: "Pb", name: "Lead", number: 82, category: "post-metal" },
  { symbol: "U", name: "Uranium", number: 92, category: "actinide" },
];

const CATEGORY_COLORS: Record<string, string> = {
  nonmetal: "#5CC98C",
  noble: "#9B7BD8",
  alkali: "#E5683C",
  alkaline: "#D9B26A",
  metalloid: "#6FA8C7",
  halogen: "#E8A33D",
  "post-metal": "#B87333",
  transition: "#DC9A6E",
  actinide: "#E2574C",
};

const CATEGORY_LABELS: Record<string, string> = {
  nonmetal: "Nonmetal",
  noble: "Noble Gas",
  alkali: "Alkali Metal",
  alkaline: "Alkaline Earth",
  metalloid: "Metalloid",
  halogen: "Halogen",
  "post-metal": "Post-Transition Metal",
  transition: "Transition Metal",
  actinide: "Actinide",
};

export default function PeriodicTable() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Periodic Table
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Elements and their role in rocks and minerals
        </p>
      </div>

      <div className="flex flex-wrap gap-2">
        {Object.entries(CATEGORY_LABELS).map(([key, label]) => (
          <div key={key} className="flex items-center gap-1.5 text-xs text-muted-foreground">
            <span className="h-3 w-3 rounded" style={{ backgroundColor: CATEGORY_COLORS[key] }} />
            {label}
          </div>
        ))}
      </div>

      <div className="grid grid-cols-4 gap-2 sm:grid-cols-6 md:grid-cols-9 lg:grid-cols-13">
        {ELEMENTS.map((el) => (
          <div
            key={el.number}
            className="flex aspect-square flex-col items-center justify-center rounded-lg border p-1 text-center transition-all hover:scale-105"
            style={{
              borderColor: `${CATEGORY_COLORS[el.category]}40`,
              backgroundColor: `${CATEGORY_COLORS[el.category]}15`,
            }}
          >
            <span className="text-[10px] text-muted-foreground">{el.number}</span>
            <span className="font-display text-sm font-bold text-foreground">{el.symbol}</span>
            <span className="truncate text-[8px] text-muted-foreground">{el.name}</span>
          </div>
        ))}
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <h3 className="text-sm font-semibold text-foreground">Key Rock-Forming Elements</h3>
        <ul className="mt-2 space-y-1.5 text-sm text-muted-foreground">
          <li><strong className="text-foreground">Si (Silicon)</strong> — the backbone of silicate minerals, the most common rock-forming minerals</li>
          <li><strong className="text-foreground">O (Oxygen)</strong> — the most abundant element in the Earth's crust, bonded to nearly every mineral</li>
          <li><strong className="text-foreground">Fe (Iron)</strong> — gives many rocks their red/brown color; key in ignous rocks like basalt</li>
          <li><strong className="text-foreground">Ca (Calcium)</strong> — found in calcite, gypsum, and many fossils</li>
          <li><strong className="text-foreground">Mg (Magnesium)</strong> — key in dolomite and mafic minerals like olivine</li>
          <li><strong className="text-foreground">K (Potassium)</strong> — found in feldspar, giving pink colors to many granites</li>
        </ul>
      </div>
    </div>
  );
}
