import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, Check, BookOpen } from "lucide-react";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface RockGuide {
  id: string;
  title: string;
  rockClass: "Igneous" | "Sedimentary" | "Metamorphic" | "Mineral" | "Crystal";
  emoji: string;
  intro: string;
  howItForms: string;
  keyTraits: string[];
  examples: string[];
  cycleNote: string;
}

const GUIDES: Record<string, RockGuide> = {
  "igneous-overview": {
    id: "igneous",
    title: "Igneous Rocks",
    rockClass: "Igneous",
    emoji: "🌋",
    intro: "Igneous rocks are born from fire. They form when molten rock — magma below ground or lava at the surface — cools and hardens into solid crystals.",
    howItForms: "When rock gets hot enough deep inside the Earth, it melts into magma. As that magma cools, mineral crystals lock together. Cool it slowly underground and you get large crystals (like granite). Cool it fast at the surface and you get tiny crystals or even glass (like basalt or obsidian). The slower the cooling, the bigger the crystals.",
    keyTraits: [
      "Made of interlocking crystals (no layers or fossils)",
      "Can be coarse-grained (slow cooling) or fine-grained (fast cooling)",
      "Often hard and dense",
      "Two families: intrusive (underground) and extrusive (surface)",
    ],
    examples: ["granite", "basalt", "obsidian"],
    cycleNote: "Igneous rock is usually the starting point of the rock cycle — everything begins as something that once melted.",
  },
  "sedimentary-overview": {
    id: "sedimentary",
    title: "Sedimentary Rocks",
    rockClass: "Sedimentary",
    emoji: "🏖️",
    intro: "Sedimentary rocks are the storytellers of geology. They form layer by layer from bits of older rock, sand, mud, and the remains of living things.",
    howItForms: "Wind, water, and ice break older rocks into tiny pieces called sediment. That sediment gets carried away and dropped in layers — usually in lakes, rivers, or oceans. Over time the layers are buried, squeezed, and cemented together into solid rock. Some sedimentary rocks instead form from minerals left behind by evaporating water, or from shells and skeletons piling up.",
    keyTraits: [
      "Often shows visible layers (strata)",
      "The only rocks that commonly contain fossils",
      "Usually softer than igneous or metamorphic rock",
      "Three types: clastic (fragments), chemical, and biogenic",
    ],
    examples: ["sandstone", "limestone"],
    cycleNote: "Sedimentary rock records Earth's history — each layer is a page from a different moment in time.",
  },
  "metamorphic-overview": {
    id: "metamorphic",
    title: "Metamorphic Rocks",
    rockClass: "Metamorphic",
    emoji: "🪨",
    intro: "Metamorphic rocks are transformed rocks. Heat and pressure deep in the Earth change an existing rock into something brand new — without ever fully melting it.",
    howItForms: "When rock is buried deep or caught in a mountain-building collision, intense heat and pressure rearrange its minerals. The rock stays solid but its crystals grow, align, or change entirely. Limestone becomes marble; shale becomes slate and then schist and gneiss as conditions intensify. Many show 'foliation' — banding or layering caused by minerals lining up under pressure.",
    keyTraits: [
      "Formed from a 'parent' rock that was heated and squeezed",
      "Often banded or foliated (aligned mineral layers)",
      "Usually harder and denser than the parent rock",
      "Never fully melts — that would make it igneous instead",
    ],
    examples: ["marble", "slate", "gneiss"],
    cycleNote: "Metamorphic rock is the 'in-between' stage — pushed further it melts into magma and restarts the rock cycle.",
  },
  "minerals-overview": {
    id: "minerals",
    title: "Minerals",
    rockClass: "Mineral",
    emoji: "💎",
    intro: "Minerals are the building blocks of everything solid on Earth. Each mineral has a unique chemical formula and crystal structure — like nature's own recipe book written in atoms.",
    howItForms: "Minerals crystallize from magma and lava, precipitate from ancient seas and hot springs, or grow in cracks and veins where hot water carries dissolved elements. Each mineral needs the right chemical ingredients, temperature, and pressure to form — which is why different minerals are found in different places. Pyrite crystallizes from hot hydrothermal fluids; gypsum evaporates from salt lakes; garnet grows deep under mountain-building pressure.",
    keyTraits: [
      "Each mineral has a unique, predictable chemical formula",
      "Crystals form specific shapes determined by their atomic structure",
      "Hardness, color, luster, and streak are key identification tools",
      "There are over 5,700 known minerals — with about 100 being common",
    ],
    examples: ["pyrite", "fluorite", "galena", "calcite", "azurite", "hematite"],
    cycleNote: "Minerals are the alphabet of geology — every rock is just a collection of minerals stuck together.",
  },
  "crystals-overview": {
    id: "crystals",
    title: "Crystals & Gems",
    rockClass: "Crystal",
    emoji: "✨",
    intro: "Crystals and gems are minerals that formed with exceptional clarity, color, or rarity. They are nature's most beautiful accidents — the same mineral chemistry, but with perfect conditions for beauty.",
    howItForms: "Gems are just minerals that grew in unusually good conditions. Quartz is common everywhere, but amethyst needs trace iron and natural radiation. Beryl is common, but emerald needs chromium and vanadium — elements rarely found together. Topaz and tourmaline need fluorine, diamond needs extreme pressure 100+ miles deep. The difference between a plain rock and a priceless gem is often just a few trace atoms and perfect growing conditions.",
    keyTraits: [
      "Gems are evaluated by the four Cs: Color, Clarity, Cut, and Carat",
      "The same mineral family can produce multiple gem varieties",
      "Some gems are created by rare geological events — like diamonds from deep-mantle eruptions",
      "Many gems also have industrial uses because of their extreme hardness",
    ],
    examples: ["diamond", "emerald", "corundum", "topaz", "tourmaline", "garnet"],
    cycleNote: "Gems are proof that geology can produce things of extraordinary beauty — given the right ingredients, time, and conditions.",
  },
  "fossils-overview": {
    id: "fossils",
    title: "Fossils",
    rockClass: "Metamorphic",
    emoji: "🦴",
    intro: "Fossils are preserved remains or traces of ancient life — from trilobites to dinosaur bones. They offer a window into worlds that existed millions of years ago.",
    howItForms: "Fossilization is rare. An organism must be buried quickly in sediment before it decays or is eaten. Over millions of years, minerals seep into the remains, replacing organic material bit by bit — turning bone to stone while preserving the original structure in perfect detail.",
    keyTraits: [
      "Found almost exclusively in sedimentary rock",
      "Most fossils are of hard parts: bones, shells, teeth",
      "Rare cases preserve soft tissue, feathers, or skin impressions",
      "Trace fossils (footprints, burrows) record behavior, not just anatomy",
    ],
    examples: ["trilobite", "ammonite", "dinosaur-bone", "petrified-wood"],
    cycleNote: "Fossils are the proof of life written into the rock record — the ultimate sedimentary treasure.",
  },
};

const CLASS_COLORS: Record<string, string> = {
  Igneous: "14 75% 57%",
  Sedimentary: "41 53% 64%",
  Metamorphic: "200 41% 61%",
  Mineral: "265 47% 67%",
  Crystal: "36 80% 58%",
};

export default function RockGuideDetail() {
  const { guideId } = useParams<{ guideId: string }>();
  const navigate = useNavigate();

  const guide = guideId ? GUIDES[guideId] : null;

  if (!guide) {
    return (
      <ScreenScaffold title="Guide Not Found" onBack={() => navigate("/app/rock-types")}>
        <div className="flex flex-col items-center justify-center gap-4 px-4 py-16 text-center">
          <BookOpen className="h-12 w-12 text-muted-foreground" />
          <p className="text-muted-foreground">This rock guide doesn't exist.</p>
          <button
            onClick={() => navigate("/app/rock-types")}
            className="text-sm font-semibold text-primary"
          >
            Back to Rock Types
          </button>
        </div>
      </ScreenScaffold>
    );
  }

  const accent = CLASS_COLORS[guide.rockClass] ?? CITRINE_HEX;

  return (
    <ScreenScaffold title={guide.title}>
      <div className="space-y-5 px-4 pb-8">
        {/* Hero header */}
        <SculptedCard
          accent="aqua"
          className="overflow-hidden p-0"
          style={{ ["--sculpted-accent" as string]: accent }}
        >
          <div
            className="relative flex h-44 items-end p-5"
            style={{
              background: `linear-gradient(to bottom, hsl(${accent} / 0.4), hsl(30 10% 7%))`,
            }}
          >
            <div className="flex items-center gap-3">
              <span className="text-5xl">{guide.emoji}</span>
              <div>
                <h1 className="font-display text-2xl font-bold text-foreground">
                  {guide.title}
                </h1>
                <TagChip accent={`hsl(${accent})`}>{guide.rockClass}</TagChip>
              </div>
            </div>
          </div>
        </SculptedCard>

        {/* Intro */}
        <p className="px-1 text-sm leading-relaxed text-[hsl(var(--text-mid))]">
          {guide.intro}
        </p>

        {/* How it forms */}
        <SculptedCard accent="aqua" className="space-y-2 p-5">
          <div className="flex items-center gap-2">
            <span
              className="h-4 w-1.5 rounded-sm"
              style={{ backgroundColor: `hsl(${accent})` }}
            />
            <h3 className="text-xs font-bold uppercase" style={{ color: `hsl(${AQUA_HEX})` }}>
              How it forms
            </h3>
          </div>
          <p className="text-sm leading-relaxed text-foreground">{guide.howItForms}</p>
        </SculptedCard>

        {/* Key traits */}
        <SculptedCard accent="aqua" className="space-y-3 p-5">
          <div className="flex items-center gap-2">
            <span
              className="h-4 w-1.5 rounded-sm"
              style={{ backgroundColor: `hsl(${accent})` }}
            />
            <h3 className="text-xs font-bold uppercase" style={{ color: `hsl(${AQUA_HEX})` }}>
              How to spot it
            </h3>
          </div>
          <ul className="space-y-2">
            {guide.keyTraits.map((trait, i) => (
              <li key={i} className="flex items-start gap-2.5">
                <Check className="mt-0.5 h-4 w-4 shrink-0" style={{ color: `hsl(${accent})` }} />
                <span className="text-sm text-foreground">{trait}</span>
              </li>
            ))}
          </ul>
        </SculptedCard>

        {/* Examples */}
        <div className="space-y-2">
          <h3 className="px-1 text-xs font-bold uppercase" style={{ color: `hsl(${AQUA_HEX})` }}>
            Examples
          </h3>
          <div className="flex flex-wrap gap-2">
            {guide.examples.map((ex) => (
              <button
                key={ex}
                onClick={() => navigate(`/app/specimens/${ex}`)}
                className="transition-transform hover:scale-105"
              >
                <TagChip accent={`hsl(${accent})`}>{ex.replace(/-/g, " ")}</TagChip>
              </button>
            ))}
          </div>
        </div>

        {/* Rock cycle note */}
        <SculptedCard accent="citrine" className="space-y-2 p-5">
          <h3 className="text-xs font-bold uppercase" style={{ color: `hsl(${AQUA_HEX})` }}>
            Rock Cycle
          </h3>
          <p className="text-sm leading-relaxed text-foreground">{guide.cycleNote}</p>
        </SculptedCard>

        {/* Back button */}
        <div className="flex justify-center pt-2">
          <button
            onClick={() => navigate("/app/rock-types")}
            className="flex items-center gap-2 text-sm font-semibold"
            style={{ color: `hsl(${CITRINE_HEX})` }}
          >
            <ArrowLeft className="h-4 w-4" />
            Back to Rock Types
          </button>
        </div>
      </div>
    </ScreenScaffold>
  );
}
