const TOPICS = [
  { title: "Divergent Boundaries", emoji: "↔️", desc: "Where plates pull apart, magma rises to fill the gap, creating new crust. Mid-ocean ridges and continental rift valleys form here. Iceland sits on the Mid-Atlantic Ridge — you can walk between two continents." },
  { title: "Convergent Boundaries", emoji: "→←", desc: "Where plates collide. Oceanic plates subduct under continental plates, creating volcanic arcs and earthquakes. The Andes and Cascades formed this way. When two continents collide, they create vast mountain ranges like the Himalayas." },
  { title: "Transform Boundaries", emoji: "⇅", desc: "Where plates slide past each other horizontally. No crust is created or destroyed. The San Andreas Fault is the most famous transform boundary — it produces frequent earthquakes as the Pacific and North American plates grind past each other." },
  { title: "Hotspots", emoji: "🌋", desc: "Fixed plumes of hot mantle that create volcanoes independent of plate boundaries. As a plate moves over a hotspot, a chain of volcanoes forms — the Hawaiian Islands are the classic example, with the youngest island (Hawaii) over the current hotspot." },
];

const VOLCANO_TYPES = [
  { name: "Shield Volcano", desc: "Broad, gently sloping cones built from fluid basaltic lava. Mauna Loa in Hawaii is the largest active volcano on Earth." },
  { name: "Stratovolcano", desc: "Tall, steep cones built from alternating layers of lava and ash. Mount St. Helens and Mount Fuji are examples. These produce explosive eruptions." },
  { name: "Cinder Cone", desc: "Small, steep-sided cones built from volcanic cinders. The simplest volcano type — Parícutin in Mexico grew from a cornfield in 1943." },
  { name: "Caldera", desc: "Large basin-shaped depressions formed when a volcano's magma chamber empties and the ground collapses. Yellowstone is a supervolcano caldera." },
];

export default function TectonicVolcanic() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Tectonics & Volcanoes
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          How plate tectonics shape the Earth and create the rocks we find
        </p>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <p className="text-sm leading-relaxed text-muted-foreground">
          The Earth's crust is broken into about 15 major tectonic plates that float
          on the semi-fluid mantle below. These plates move at 1–10 cm per year — about
          the speed your fingernails grow. Where plates interact, mountains rise,
          volcanoes erupt, earthquakes shake, and new rocks are born. Understanding
          plate tectonics helps rockhounds predict where specific minerals and rock
          types are likely to be found.
        </p>
      </div>

      <h2 className="font-display text-lg font-bold text-foreground">Plate Boundaries</h2>
      <div className="grid gap-4 sm:grid-cols-2">
        {TOPICS.map((topic) => (
          <div key={topic.title} className="rounded-xl border border-border bg-card p-4">
            <h3 className="font-display text-sm font-semibold text-foreground">
              <span className="mr-2">{topic.emoji}</span>{topic.title}
            </h3>
            <p className="mt-2 text-xs leading-relaxed text-muted-foreground">{topic.desc}</p>
          </div>
        ))}
      </div>

      <h2 className="font-display text-lg font-bold text-foreground">Volcano Types</h2>
      <div className="grid gap-4 sm:grid-cols-2">
        {VOLCANO_TYPES.map((v) => (
          <div key={v.name} className="rounded-xl border border-border bg-card p-4">
            <h3 className="font-display text-sm font-semibold text-primary">{v.name}</h3>
            <p className="mt-1.5 text-xs leading-relaxed text-muted-foreground">{v.desc}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
