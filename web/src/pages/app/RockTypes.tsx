import { useNavigate } from "react-router-dom";
import { ArrowRight } from "lucide-react";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";

const ROCK_GUIDES = [
  { id: "igneous-overview", title: "Igneous Rocks", emoji: "🌋", class: "Igneous", desc: "Rocks formed from cooling magma or lava — from granite to basalt to obsidian.", accent: "14 75% 57%" },
  { id: "sedimentary-overview", title: "Sedimentary Rocks", emoji: "🏖️", class: "Sedimentary", desc: "Rocks formed from accumulated sediments — sandstone, shale, limestone, and more.", accent: "41 53% 64%" },
  { id: "metamorphic-overview", title: "Metamorphic Rocks", emoji: "💎", class: "Metamorphic", desc: "Rocks transformed by heat and pressure — schist, gneiss, marble, and quartzite.", accent: "200 41% 61%" },
  { id: "minerals-overview", title: "Minerals", emoji: "🔷", class: "Mineral", desc: "Naturally occurring inorganic solids with a defined chemical composition and crystal structure.", accent: "265 47% 67%" },
  { id: "crystals-overview", title: "Crystals & Gems", emoji: "✨", class: "Crystal", desc: "Cut and polished minerals prized for beauty — quartz, ruby, sapphire, emerald, and more.", accent: "36 80% 58%" },
  { id: "fossils-overview", title: "Fossils", emoji: "🦴", class: "Fossil", desc: "Preserved remains or traces of ancient life — from trilobites to dinosaur bones.", accent: "33 38% 64%" },
];

export default function RockTypes() {
  const navigate = useNavigate();

  return (
    <ScreenScaffold title="Rock Types">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          The three major rock groups and the specimens within them
        </p>

        <SculptedCard accent="aqua" className="p-4">
          <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">
            Every rock falls into one of three categories based on how it formed. Igneous
            rocks solidify from molten material, sedimentary rocks form from accumulated
            sediments, and metamorphic rocks are transformed by heat and pressure. The rock
            cycle connects all three — any rock can become any other over geological time.
          </p>
        </SculptedCard>

        <div className="grid gap-4 sm:grid-cols-2">
          {ROCK_GUIDES.map((guide) => (
            <SculptedCard
              key={guide.id}
              accent="aqua"
              interactive
              className="overflow-hidden"
              onClick={() => navigate(`/app/guide/${guide.id}`)}
            >
              <div className="flex items-start gap-3 p-4">
                <div
                  className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl"
                  style={{ ["--glow-color" as string]: guide.accent }}
                >
                  {guide.emoji}
                </div>
                <div className="min-w-0 flex-1">
                  <h3 className="font-display text-sm font-bold text-foreground">{guide.title}</h3>
                  <TagChip accent={`hsl(${guide.accent})`}>{guide.class}</TagChip>
                  <p className="mt-1.5 text-xs leading-relaxed text-[hsl(var(--text-mid))]">{guide.desc}</p>
                </div>
                <ArrowRight className="h-4 w-4 shrink-0 text-muted-foreground" />
              </div>
            </SculptedCard>
          ))}
        </div>
      </div>
    </ScreenScaffold>
  );
}
