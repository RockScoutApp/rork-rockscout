import { useNavigate } from "react-router-dom";
import { ArrowRight } from "lucide-react";

const ROCK_GUIDES = [
  { id: "igneous-overview", title: "Igneous Rocks", emoji: "🌋", class: "Igneous", desc: "Rocks formed from cooling magma or lava — from granite to basalt to obsidian." },
  { id: "sedimentary-overview", title: "Sedimentary Rocks", emoji: "🏖️", class: "Sedimentary", desc: "Rocks formed from accumulated sediments — sandstone, shale, limestone, and more." },
  { id: "metamorphic-overview", title: "Metamorphic Rocks", emoji: "💎", class: "Metamorphic", desc: "Rocks transformed by heat and pressure — schist, gneiss, marble, and quartzite." },
  { id: "minerals-overview", title: "Minerals", emoji: "🔷", class: "Mineral", desc: "Naturally occurring inorganic solids with a defined chemical composition and crystal structure." },
  { id: "crystals-overview", title: "Crystals & Gems", emoji: "✨", class: "Crystal", desc: "Cut and polished minerals prized for beauty — quartz, ruby, sapphire, emerald, and more." },
  { id: "fossils-overview", title: "Fossils", emoji: "🦴", class: "Fossil", desc: "Preserved remains or traces of ancient life — from trilobites to dinosaur bones." },
];

const CLASS_COLORS: Record<string, string> = {
  Igneous: "#E5683C",
  Sedimentary: "#D9B26A",
  Metamorphic: "#6FA8C7",
  Mineral: "#9B7BD8",
  Crystal: "#E8A33D",
  Fossil: "#C9A87C",
};

export default function RockTypes() {
  const navigate = useNavigate();

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Rock Types
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          The three major rock groups and the specimens within them
        </p>
      </div>

      <div className="rounded-lg border border-border bg-card p-4">
        <p className="text-sm leading-relaxed text-muted-foreground">
          Every rock falls into one of three categories based on how it formed. Igneous
          rocks solidify from molten material, sedimentary rocks form from accumulated
          sediments, and metamorphic rocks are transformed by heat and pressure. The rock
          cycle connects all three — any rock can become any other over geological time.
        </p>
      </div>

      <div className="grid gap-3 sm:grid-cols-2">
        {ROCK_GUIDES.map((guide) => (
          <button
            key={guide.id}
            onClick={() => navigate(`/app/guide/${guide.id}`)}
            className="group flex items-start gap-3 rounded-xl border border-border bg-card p-4 text-left transition-all hover:border-primary/40"
          >
            <span className="text-3xl">{guide.emoji}</span>
            <div className="min-w-0 flex-1">
              <h3 className="font-display text-sm font-semibold text-foreground">{guide.title}</h3>
              <span
                className="mt-0.5 inline-block rounded-full px-2 py-0.5 text-[10px] font-medium"
                style={{ backgroundColor: `${CLASS_COLORS[guide.class]}20`, color: CLASS_COLORS[guide.class] }}
              >
                {guide.class}
              </span>
              <p className="mt-1.5 text-xs leading-relaxed text-muted-foreground">{guide.desc}</p>
            </div>
            <ArrowRight className="h-4 w-4 shrink-0 text-muted-foreground group-hover:text-primary" />
          </button>
        ))}
      </div>
    </div>
  );
}
