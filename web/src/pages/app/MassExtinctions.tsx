const EXTINCTIONS = [
  {
    name: "Ordovician-Silurian",
    date: "~444 Mya",
    severity: "85% of species",
    desc: "A sudden ice age caused sea levels to drop, destroying shallow marine habitats. Trilobites, brachiopods, and graptolites were hit hardest.",
  },
  {
    name: "Late Devonian",
    date: "~375 Mya",
    severity: "75% of species",
    desc: "A series of extinction pulses over 20 million years. Reef-building organisms and many fish groups disappeared. Land plants may have caused it by weathering rocks and dumping nutrients into the sea.",
  },
  {
    name: "Permian-Triassic",
    date: "~252 Mya",
    severity: "96% of species",
    desc: "The 'Great Dying' — the largest mass extinction in Earth's history. Likely caused by massive volcanic eruptions in Siberia that triggered global warming, ocean acidification, and anoxia. It took 10 million years for ecosystems to recover.",
  },
  {
    name: "Triassic-Jurassic",
    date: "~201 Mya",
    severity: "80% of species",
    desc: "Massive volcanic eruptions from the Central Atlantic Magmatic Province. Large amphibians and many reptile groups went extinct, allowing dinosaurs to dominate the Jurassic.",
  },
  {
    name: "Cretaceous-Paleogene",
    date: "~66 Mya",
    severity: "76% of species",
    desc: "The asteroid impact that killed the non-avian dinosaurs. A 6-mile asteroid struck the Yucatán Peninsula, causing global wildfires, a 'nuclear winter', and ocean acidification. The Chicxulub crater is the evidence.",
  },
  {
    name: "Holocene (Ongoing)",
    date: "Now",
    severity: " accelerating",
    desc: "The current extinction event, driven by human activity — habitat loss, climate change, pollution, and overhunting. Unlike the previous five, this one is caused by a single species.",
  },
];

export default function MassExtinctions() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Mass Extinctions
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          The 'Big Five' — and the sixth one happening now
        </p>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <p className="text-sm leading-relaxed text-muted-foreground">
          Mass extinctions are events where a large percentage of species disappear in a
          geologically short time. After each extinction, surviving species diversified
          to fill the empty niches — without the K-Pg extinction, mammals (including us)
          might never have gotten their chance.
        </p>
      </div>

      <div className="space-y-3">
        {EXTINCTIONS.map((ext) => (
          <div
            key={ext.name}
            className="rounded-xl border bg-card p-4"
            style={{ borderColor: "#E2574C30" }}
          >
            <div className="flex items-start justify-between gap-2">
              <h3 className="font-display text-sm font-semibold text-foreground">{ext.name}</h3>
              <span className="shrink-0 rounded-full bg-destructive/15 px-2 py-0.5 text-[10px] font-medium text-destructive">
                {ext.severity}
              </span>
            </div>
            <p className="mt-0.5 text-xs font-medium text-primary">{ext.date}</p>
            <p className="mt-1.5 text-sm leading-relaxed text-muted-foreground">{ext.desc}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
