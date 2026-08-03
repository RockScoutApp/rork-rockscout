import { useState, useMemo } from "react";
import { Search, X, FlaskConical } from "lucide-react";
import { Input } from "@/components/ui/input";
import {
  ELEMENTS,
  CATEGORY_COLORS,
  CATEGORY_LABELS,
  type Element,
  type ElementCategory,
} from "@/data/elements";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";

/**
 * Map an element to its grid position (row = period, column = group).
 * Lanthanides and actinides are shown in separate rows below.
 */
function gridPosition(el: Element): { row: number; col: number } | null {
  // Lanthanides (57-71) and actinides (89-103) go in the f-block rows
  if (el.number >= 57 && el.number <= 71) {
    return { row: 9, col: el.number - 57 + 3 };
  }
  if (el.number >= 89 && el.number <= 103) {
    return { row: 10, col: el.number - 89 + 3 };
  }
  // Period 6: La (57) is in group 3 on the main table, rest in f-block
  // Period 7: Ac (89) is in group 3 on the main table, rest in f-block
  if (el.number === 57 || el.number === 89) {
    return null; // handled by placeholder marker
  }
  return { row: el.period, col: el.group };
}

export default function PeriodicTable() {
  const [search, setSearch] = useState("");
  const [selectedElement, setSelectedElement] = useState<Element | null>(null);
  const [filterCategory, setFilterCategory] = useState<ElementCategory | "all">("all");

  const filtered = useMemo(() => {
    return ELEMENTS.filter((el) => {
      if (filterCategory !== "all" && el.category !== filterCategory) return false;
      if (search) {
        const q = search.toLowerCase();
        return (
          el.name.toLowerCase().includes(q) ||
          el.symbol.toLowerCase().includes(q) ||
          String(el.number).includes(q)
        );
      }
      return true;
    });
  }, [search, filterCategory]);

  const highlightedIds = new Set(filtered.map((e) => e.number));
  const isFiltering = search || filterCategory !== "all";

  return (
    <ScreenScaffold title="Periodic Table">
      <div className="space-y-5 px-4 pb-8">
        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by name, symbol, or number…"
            className="pl-9"
          />
        </div>

        {/* Category legend / filters */}
        <div className="flex flex-wrap gap-1.5">
          <button
            onClick={() => setFilterCategory("all")}
            className={`rounded-full px-2.5 py-1 text-[10px] font-bold transition-all ${
              filterCategory === "all"
                ? "bg-primary/15 text-primary ring-1 ring-primary/40"
                : "border border-border text-muted-foreground hover:text-foreground"
            }`}
          >
            All
          </button>
          {(Object.keys(CATEGORY_LABELS) as ElementCategory[]).map((cat) => (
            <button
              key={cat}
              onClick={() => setFilterCategory(cat)}
              className="flex items-center gap-1 rounded-full px-2.5 py-1 text-[10px] font-bold transition-all"
              style={{
                backgroundColor: filterCategory === cat ? `${CATEGORY_COLORS[cat]}25` : undefined,
                color: filterCategory === cat ? CATEGORY_COLORS[cat] : undefined,
                border: `1px solid ${filterCategory === cat ? `${CATEGORY_COLORS[cat]}60` : "hsl(var(--border))"}`,
              }}
            >
              <span className="h-2 w-2 rounded-sm" style={{ backgroundColor: CATEGORY_COLORS[cat] }} />
              {CATEGORY_LABELS[cat]}
            </button>
          ))}
        </div>

        {/* Periodic table grid */}
        <SculptedCard accent="cyan" className="overflow-x-auto p-4">
          <div className="min-w-[760px]">
            {/* Main table: 7 rows × 18 columns */}
            <div
              className="grid gap-1"
              style={{ gridTemplateColumns: "repeat(18, minmax(0, 1fr))", gridTemplateRows: "repeat(7, minmax(0, 1fr))" }}
            >
              {Array.from({ length: 7 * 18 }, (_, i) => {
                const row = Math.floor(i / 18) + 1;
                const col = (i % 18) + 1;
                const el = ELEMENTS.find((e) => {
                  const pos = gridPosition(e);
                  return pos && pos.row === row && pos.col === col;
                });
                if (el) {
                  const dimmed = isFiltering && !highlightedIds.has(el.number);
                  return (
                    <ElementCell
                      key={el.number}
                      element={el}
                      dimmed={dimmed}
                      onClick={() => setSelectedElement(el)}
                    />
                  );
                }
                // Check if this is a placeholder for La/Ac
                const isLa = row === 6 && col === 3;
                const isAc = row === 7 && col === 3;
                if (isLa || isAc) {
                  const placeholderEl = isLa
                    ? ELEMENTS.find((e) => e.number === 57)
                    : ELEMENTS.find((e) => e.number === 89);
                  if (placeholderEl) {
                    return (
                      <ElementCell
                        key={placeholderEl.number}
                        element={placeholderEl}
                        dimmed={isFiltering && !highlightedIds.has(placeholderEl.number)}
                        onClick={() => setSelectedElement(placeholderEl)}
                        placeholder
                      />
                    );
                  }
                }
                return <div key={i} />;
              })}
            </div>

            {/* f-block (lanthanides + actinides) */}
            <div className="mt-2 grid gap-1" style={{ gridTemplateColumns: "repeat(15, minmax(0, 1fr))" }}>
              {/* Lanthanide label cell */}
              <div className="flex items-center justify-center rounded text-[8px] font-bold text-muted-foreground">
                57-71
              </div>
              {ELEMENTS.filter((e) => e.number >= 58 && e.number <= 71).map((el) => (
                <ElementCell
                  key={el.number}
                  element={el}
                  dimmed={isFiltering && !highlightedIds.has(el.number)}
                  onClick={() => setSelectedElement(el)}
                />
              ))}
              {/* Actinide label cell */}
              <div className="flex items-center justify-center rounded text-[8px] font-bold text-muted-foreground">
                89-103
              </div>
              {ELEMENTS.filter((e) => e.number >= 90 && e.number <= 103).map((el) => (
                <ElementCell
                  key={el.number}
                  element={el}
                  dimmed={isFiltering && !highlightedIds.has(el.number)}
                  onClick={() => setSelectedElement(el)}
                />
              ))}
            </div>
          </div>
        </SculptedCard>

        {/* Key rock-forming elements */}
        <SculptedCard accent="citrine" className="p-5">
          <div className="mb-3 flex items-center gap-2">
            <FlaskConical className="h-5 w-5" style={{ color: `hsl(${CITRINE_HEX})` }} />
            <h3 className="font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
              Key Rock-Forming Elements
            </h3>
          </div>
          <div className="space-y-2">
            {[
              { sym: "O", desc: "Most abundant element in Earth's crust — bonded to nearly every mineral" },
              { sym: "Si", desc: "Backbone of silicate minerals — the most common rock-forming minerals" },
              { sym: "Al", desc: "Found in feldspar, bauxite, and corundum — third most abundant crustal element" },
              { sym: "Fe", desc: "Gives rocks red/brown color; key in igneous rocks like basalt and hematite" },
              { sym: "Ca", desc: "Found in calcite, gypsum, fluorite, and many fossils" },
              { sym: "Mg", desc: "Key in dolomite, olivine, and mafic minerals" },
              { sym: "K", desc: "Found in feldspar — gives pink colors to many granites" },
              { sym: "Na", desc: "Found in halite (salt), sodalite, and albite feldspar" },
            ].map((item) => {
              const el = ELEMENTS.find((e) => e.symbol === item.sym);
              if (!el) return null;
              return (
                <button
                  key={item.sym}
                  onClick={() => setSelectedElement(el)}
                  className="flex items-center gap-3 text-left transition-all hover:translate-x-1"
                >
                  <span
                    className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg font-display text-sm font-bold"
                    style={{
                      backgroundColor: `${CATEGORY_COLORS[el.category]}20`,
                      color: CATEGORY_COLORS[el.category],
                      border: `1px solid ${CATEGORY_COLORS[el.category]}40`,
                    }}
                  >
                    {el.symbol}
                  </span>
                  <span className="text-sm text-[hsl(var(--text-mid))]">{item.desc}</span>
                </button>
              );
            })}
          </div>
        </SculptedCard>

        {/* Element detail modal */}
        {selectedElement && (
          <div
            className="fixed inset-0 z-[80] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4"
            onClick={() => setSelectedElement(null)}
          >
            <div
              className="dark-card sculpted-raised w-full max-w-sm rounded-2xl p-6"
              style={{ ["--sculpted-accent" as string]: CITRINE_HEX }}
              onClick={(e) => e.stopPropagation()}
            >
              <div className="mb-4 flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div
                    className="glowing-border flex h-16 w-16 flex-col items-center justify-center rounded-xl"
                    style={{
                      backgroundColor: `${CATEGORY_COLORS[selectedElement.category]}20`,
                      ["--glow-color" as string]: CITRINE_HEX,
                    }}
                  >
                    <span className="text-[10px] text-muted-foreground">{selectedElement.number}</span>
                    <span className="font-display text-2xl font-bold" style={{ color: CATEGORY_COLORS[selectedElement.category] }}>
                      {selectedElement.symbol}
                    </span>
                  </div>
                  <div>
                    <h3 className="font-display text-xl font-bold text-foreground">{selectedElement.name}</h3>
                    <p className="text-xs text-muted-foreground">
                      Period {selectedElement.period} · Group {selectedElement.group}
                    </p>
                  </div>
                </div>
                <button onClick={() => setSelectedElement(null)} className="text-muted-foreground">
                  <X className="h-5 w-5" />
                </button>
              </div>

              <div className="space-y-3">
                <div>
                  <p className="text-xs font-semibold text-muted-foreground">Category</p>
                  <TagChip accent={CATEGORY_COLORS[selectedElement.category]}>
                    {CATEGORY_LABELS[selectedElement.category]}
                  </TagChip>
                </div>

                {selectedElement.rockRelevance && (
                  <div>
                    <p className="mb-1 text-xs font-semibold" style={{ color: `hsl(${CITRINE_HEX})` }}>
                      Rock & Mineral Relevance
                    </p>
                    <p className="text-sm text-[hsl(var(--text-mid))]">
                      {selectedElement.rockRelevance}
                    </p>
                  </div>
                )}

                {!selectedElement.rockRelevance && (
                  <p className="text-sm text-muted-foreground">
                    This element is not commonly found in rock-forming minerals.
                  </p>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </ScreenScaffold>
  );
}

function ElementCell({
  element,
  dimmed,
  onClick,
  placeholder,
}: {
  element: Element;
  dimmed: boolean;
  onClick: () => void;
  placeholder?: boolean;
}) {
  const color = CATEGORY_COLORS[element.category];
  return (
    <button
      onClick={onClick}
      className={`group relative flex aspect-square flex-col items-center justify-center rounded-md border p-0.5 transition-all hover:scale-110 hover:z-10 ${
        dimmed ? "opacity-20" : ""
      }`}
      style={{
        borderColor: `${color}50`,
        backgroundColor: `${color}15`,
      }}
      title={`${element.name} (${element.symbol})`}
    >
      <span className="text-[7px] leading-none text-muted-foreground">{element.number}</span>
      <span className="font-display text-[10px] font-bold leading-tight sm:text-xs" style={{ color }}>
        {element.symbol}
      </span>
      <span className="hidden truncate text-[6px] leading-none text-muted-foreground sm:block">
        {element.name}
      </span>
      {placeholder && (
        <span className="absolute inset-0 flex items-center justify-center text-[7px] text-muted-foreground">
          *
        </span>
      )}
    </button>
  );
}
