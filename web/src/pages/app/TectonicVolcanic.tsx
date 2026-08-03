import { SculptedCard, ScreenScaffold } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

const TOPICS = [
  { title: "Divergent Boundaries", emoji: "↔️", desc: "Where plates pull apart, magma rises to fill the gap, creating new crust. Mid-ocean ridges and continental rift valleys form here. Iceland sits on the Mid-Atlantic Ridge — you can walk between two continents.", accent: "174 100% 45%" },
  { title: "Convergent Boundaries", emoji: "→←", desc: "Where plates collide. Oceanic plates subduct under continental plates, creating volcanic arcs and earthquakes. The Andes and Cascades formed this way. When two continents collide, they create vast mountain ranges like the Himalayas.", accent: "4 70% 55%" },
  { title: "Transform Boundaries", emoji: "⇅", desc: "Where plates slide past each other horizontally. No crust is created or destroyed. The San Andreas Fault is the most famous transform boundary — it produces frequent earthquakes as the Pacific and North American plates grind past each other.", accent: "36 80% 58%" },
  { title: "Hotspots", emoji: "🌋", desc: "Fixed plumes of hot mantle that create volcanoes independent of plate boundaries. As a plate moves over a hotspot, a chain of volcanoes forms — the Hawaiian Islands are the classic example, with the youngest island (Hawaii) over the current hotspot.", accent: "14 75% 57%" },
];

const VOLCANO_TYPES = [
  { name: "Shield Volcano", desc: "Broad, gently sloping cones built from fluid basaltic lava. Mauna Loa in Hawaii is the largest active volcano on Earth.", accent: "4 70% 55%" },
  { name: "Stratovolcano", desc: "Tall, steep cones built from alternating layers of lava and ash. Mount St. Helens and Mount Fuji are examples. These produce explosive eruptions.", accent: "14 75% 47%" },
  { name: "Cinder Cone", desc: "Small, steep-sided cones built from volcanic cinders. The simplest volcano type — Parícutin in Mexico grew from a cornfield in 1943.", accent: "20 62% 55%" },
  { name: "Caldera", desc: "Large basin-shaped depressions formed when a volcano's magma chamber empties and the ground collapses. Yellowstone is a supervolcano caldera.", accent: "265 47% 57%" },
];

export default function TectonicVolcanic() {
  return (
    <ScreenScaffold title="Tectonics & Volcanoes">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          How plate tectonics shape the Earth and create the rocks we find
        </p>

        <SculptedCard accent="aqua" className="p-4">
          <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">
            The Earth's crust is broken into about 15 major tectonic plates that float
            on the semi-fluid mantle below. These plates move at 1–10 cm per year — about
            the speed your fingernails grow. Where plates interact, mountains rise,
            volcanoes erupt, earthquakes shake, and new rocks are born. Understanding
            plate tectonics helps rockhounds predict where specific minerals and rock
            types are likely to be found.
          </p>
        </SculptedCard>

        <div>
          <h2 className="mb-3 font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>Plate Boundaries</h2>
          <div className="grid gap-4 sm:grid-cols-2">
            {TOPICS.map((topic) => (
              <SculptedCard key={topic.title} accent="aqua" className="p-4">
                <div className="flex items-start gap-3">
                  <div
                    className="glowing-border flex h-10 w-10 shrink-0 items-center justify-center rounded-xl text-xl"
                    style={{ ["--glow-color" as string]: topic.accent }}
                  >
                    {topic.emoji}
                  </div>
                  <div className="min-w-0 flex-1">
                    <h3 className="font-display text-sm font-bold text-foreground">{topic.title}</h3>
                    <p className="mt-1.5 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{topic.desc}</p>
                  </div>
                </div>
              </SculptedCard>
            ))}
          </div>
        </div>

        <div>
          <h2 className="mb-3 font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>Volcano Types</h2>
          <div className="grid gap-4 sm:grid-cols-2">
            {VOLCANO_TYPES.map((v) => (
              <SculptedCard key={v.name} accent="danger" className="p-4">
                <h3 className="font-display text-sm font-bold" style={{ color: `hsl(${v.accent})` }}>{v.name}</h3>
                <p className="mt-1.5 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{v.desc}</p>
              </SculptedCard>
            ))}
          </div>
        </div>
      </div>
    </ScreenScaffold>
  );
}
