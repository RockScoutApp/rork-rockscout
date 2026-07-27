import { ExternalLink } from "lucide-react";

const CATEGORIES: { title: string; emoji: string; links: { name: string; description: string; url: string }[] }[] = [
  {
    title: "Identification & Reference",
    emoji: "📖",
    links: [
      { name: "Mindat.org", description: "The world's largest mineral database with photos, localities, and properties.", url: "https://www.mindat.org" },
      { name: "Webmineral", description: "Mineral species database with crystallography and chemical data.", url: "https://webmineral.com" },
      { name: "Mineralogy Database", description: "Comprehensive mineral data with optical and physical properties.", url: "https://rruff.info/ima" },
      { name: "Handbook of Mineralogy", description: "Free PDF mineral reference by the Mineralogical Society of America.", url: "https://www.handbookofmineralogy.org" },
    ],
  },
  {
    title: "Maps & Locations",
    emoji: "🗺️",
    links: [
      { name: "BLM Land Status Maps", description: "Public land maps for rockhounding on BLM-managed land.", url: "https://www.blm.gov/maps" },
      { name: "USGS Topo Maps", description: "Free USGS topographic maps for finding collecting sites.", url: "https://www.usgs.gov/maps" },
      { name: "Mindat Localities", description: "Search for mineral localities by region worldwide.", url: "https://www.mindat.org/geoloc.php" },
    ],
  },
  {
    title: "Clubs & Community",
    emoji: "👥",
    links: [
      { name: "American Federation of Mineralogical Societies", description: "Find a local rockhound club near you.", url: "https://www.amfed.org" },
      { name: "Reddit r/rockhounds", description: "Active community for sharing finds and asking questions.", url: "https://www.reddit.com/r/rockhounds" },
      { name: "The Fossil Forum", description: "Community dedicated to fossil collecting and identification.", url: "https://www.thefossilforum.com" },
    ],
  },
  {
    title: "Safety & Ethics",
    emoji: "⚠️",
    links: [
      { name: "BLM Rockhounding Rules", description: "Official rules for collecting on BLM land.", url: "https://www.blm.gov/programs/recreation/hunting-and-fishing/rockhounding" },
      { name: "USFS Collecting Guidelines", description: "Forest Service rules for recreational rock collecting.", url: "https://www.fs.usda.gov" },
      { name: "Leave No Trace", description: "Outdoor ethics for responsible collecting.", url: "https://lnt.org" },
    ],
  },
  {
    title: "Learning & Education",
    emoji: "🎓",
    links: [
      { name: "USGS Education", description: "Free geology education resources from the US Geological Survey.", url: "https://www.usgs.gov/science/science-explorer" },
      { name: "Smithsonian Rock & Gem", description: "Online exhibits from the Smithsonian's mineral collection.", url: "https://www.si.edu/spotlight/minerals" },
      { name: "Geology.com", description: "Articles and guides on rocks, minerals, and geology.", url: "https://geology.com" },
    ],
  },
];

export default function ResourceLinks() {
  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Resource Links
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Trusted resources for rockhounds, mineralogists, and fossil collectors
        </p>
      </div>

      {CATEGORIES.map((cat) => (
        <div key={cat.title}>
          <h2 className="mb-2 font-display text-base font-semibold text-foreground">
            <span className="mr-2">{cat.emoji}</span>{cat.title}
          </h2>
          <div className="space-y-2">
            {cat.links.map((link) => (
              <a
                key={link.url}
                href={link.url}
                target="_blank"
                rel="noopener noreferrer"
                className="group flex items-start gap-3 rounded-lg border border-border bg-card p-3 transition-all hover:border-primary/40"
              >
                <div className="min-w-0 flex-1">
                  <p className="flex items-center gap-1.5 text-sm font-semibold text-foreground">
                    {link.name}
                    <ExternalLink className="h-3 w-3 text-muted-foreground group-hover:text-primary" />
                  </p>
                  <p className="mt-0.5 text-xs text-muted-foreground">{link.description}</p>
                </div>
              </a>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}
