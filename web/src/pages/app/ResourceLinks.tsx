import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { ExternalLink, Landmark, Plus, Loader2, Trash2 } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { CompactSearchPill } from "@/components/CompactSearchPill";
import { filterProfanity } from "@/lib/profanity-filter";
import { useProfanityLevel } from "@/hooks/useProfanityLevel";

type Tab = "websites" | "museums";

interface Museum {
  id: string;
  name: string;
  state: string;
  city: string | null;
  description: string | null;
  website_url: string | null;
  submitted_by: string | null;
  approved: boolean;
  created_at: string;
}

const CATEGORIES: {
  title: string;
  emoji: string;
  links: { name: string; description: string; url: string }[];
}[] = [
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
  {
    title: "War Relics",
    emoji: "🎖️",
    links: [
      { name: "American Civil War Museum", description: "Online collections from the premier museum of Civil War artifacts, uniforms, and ordnance.", url: "https://www.acwm.org" },
      { name: "Encyclopedia of Civil War Artillery", description: "Comprehensive reference for Civil War artillery projectiles, fuses, and ordnance.", url: "https://www.civilwarartillery.com" },
      { name: "Ridgeway Reference Library", description: "Ridgeway's extensive photo reference for Civil War plates, buckles, and buttons.", url: "https://www.relicman.com" },
      { name: "Museum of the American Revolution", description: "Online exhibits and artifact collections from the American Revolutionary War.", url: "https://www.amrevmuseum.org" },
      { name: "NPS Archaeology", description: "National Park Service archaeology articles on Civil War and Revolutionary War sites.", url: "https://www.nps.gov/subjects/archeology/index.htm" },
      { name: "Colonial Williamsburg Archaeology", description: "Archaeological research and collections from Colonial Williamsburg, Virginia.", url: "https://www.colonialwilliamsburg.org" },
      { name: "Historic Jamestowne Collections", description: "Artifact collections and archaeological findings from Historic Jamestowne.", url: "https://www.historicjamestowne.org" },
      { name: "Smithsonian National Museum of American History", description: "The Smithsonian's collections of Civil War and Revolutionary War military artifacts.", url: "https://americanhistory.si.edu" },
      { name: "American Revolution Institute", description: "Reference library and collections on Revolutionary War artifacts and military material culture.", url: "https://www.americanrevolutioninstitute.org" },
      { name: "Civil War Bullet Forum", description: "Community forum dedicated to the identification and study of Civil War bullets and projectiles.", url: "https://www.cwbullet.org" },
      { name: "Columbia River Arsenal", description: "Detailed photo reference for Civil War small arms ammunition and cartridge types.", url: "https://www.columbiariverarsenal.com" },
      { name: "Mount Vernon Archaeology", description: "Archaeological findings and artifact collections from George Washington's Mount Vernon estate.", url: "https://www.mountvernon.org" },
    ],
  },
];

const US_STATES = [
  "AL","AK","AZ","AR","CA","CO","CT","DE","FL","GA","HI","ID","IL","IN","IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT","NE","NV","NH","NJ","NM","NY","NC","ND","OH","OK","OR","PA","RI","SC","SD","TN","TX","UT","VT","VA","WA","WV","WI","WY","DC",
];

export default function ResourceLinks() {
  const profanityLevel = useProfanityLevel();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState<Tab>("websites");
  const [search, setSearch] = useState("");
  const [stateFilter, setStateFilter] = useState("");
  const [showAddMuseum, setShowAddMuseum] = useState(false);
  const [museumForm, setMuseumForm] = useState({
    name: "",
    state: "",
    city: "",
    description: "",
    website_url: "",
  });

  const { data: museums, isLoading: museumsLoading } = useQuery<Museum[]>({
    queryKey: ["museums"],
    queryFn: async () => {
      const { data, error } = await supabase
        .from("rockscout_museums")
        .select("*")
        .eq("approved", true)
        .order("name", { ascending: true });
      if (error) throw error;
      return (data ?? []) as Museum[];
    },
    enabled: activeTab === "museums",
  });

  const submitMuseum = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to submit a museum");
      if (!museumForm.name.trim()) throw new Error("Museum name is required");
      if (!museumForm.state) throw new Error("State is required");
      const { error } = await supabase.from("rockscout_museums").insert({
        name: filterProfanity(museumForm.name.trim(), profanityLevel).filteredText,
        state: museumForm.state,
        city: filterProfanity(museumForm.city.trim(), profanityLevel).filteredText || null,
        description: filterProfanity(museumForm.description.trim(), profanityLevel).filteredText || null,
        website_url: museumForm.website_url.trim() || null,
        submitted_by: user.id,
        approved: false,
      });
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Museum submitted! It will appear after review.");
      queryClient.invalidateQueries({ queryKey: ["museums"] });
      setShowAddMuseum(false);
      setMuseumForm({ name: "", state: "", city: "", description: "", website_url: "" });
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to submit museum"),
  });

  const filteredMuseums = (museums ?? []).filter((m) => {
    if (stateFilter && m.state !== stateFilter) return false;
    if (!search) return true;
    const q = search.toLowerCase();
    return (
      m.name?.toLowerCase().includes(q) ||
      m.city?.toLowerCase().includes(q) ||
      m.state?.toLowerCase().includes(q)
    );
  });

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Rock & Gem Resources
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Trusted resources, museums, and references for rockhounds
        </p>
      </div>

      {/* Tab switcher */}
      <div className="flex items-center gap-2">
        <button
          onClick={() => setActiveTab("websites")}
          className={`inline-flex items-center gap-2 rounded-full px-4 py-2 text-sm font-semibold transition-all ${
            activeTab === "websites"
              ? "bg-primary/15 text-primary ring-1 ring-primary/30"
              : "text-muted-foreground hover:text-foreground"
          }`}
        >
          <ExternalLink className="h-4 w-4" />
          Websites
        </button>
        <button
          onClick={() => setActiveTab("museums")}
          className={`inline-flex items-center gap-2 rounded-full px-4 py-2 text-sm font-semibold transition-all ${
            activeTab === "museums"
              ? "bg-primary/15 text-primary ring-1 ring-primary/30"
              : "text-muted-foreground hover:text-foreground"
          }`}
        >
          <Landmark className="h-4 w-4" />
          Museums
        </button>
      </div>

      {activeTab === "websites" ? (
        <>
          {CATEGORIES.map((cat) => (
            <div key={cat.title}>
              <h2 className="mb-2 font-display text-base font-semibold text-foreground">
                <span className="mr-2">{cat.emoji}</span>
                {cat.title}
              </h2>
              <div className="space-y-2">
                {cat.links.map((link) => (
                  <a
                    key={link.url}
                    href={link.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="group flex items-start gap-3 dark-card sculpted-raised rounded-lg p-3 transition-all hover:border-primary/40"
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
        </>
      ) : (
        <>
          {/* Museum directory */}
          <div className="flex flex-wrap items-center gap-3">
            <CompactSearchPill
              value={search}
              onChange={setSearch}
              placeholder="Search museums…"
            />
            <select
              value={stateFilter}
              onChange={(e) => setStateFilter(e.target.value)}
              className="rounded-full border border-border bg-card/60 px-3 py-2 text-sm text-foreground"
            >
              <option value="">All states</option>
              {US_STATES.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </select>
            <Button
              size="sm"
              onClick={() => setShowAddMuseum(true)}
              className="gap-2"
            >
              <Plus className="h-4 w-4" />
              Add a Museum
            </Button>
          </div>

          {museumsLoading ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-6 w-6 animate-spin text-primary" />
            </div>
          ) : filteredMuseums.length > 0 ? (
            <div className="space-y-2">
              {filteredMuseums.map((museum) => (
                <div
                  key={museum.id}
                  className="dark-card sculpted-raised rounded-lg p-3"
                >
                  <div className="flex items-start gap-3">
                    <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/15 text-primary ring-1 ring-primary/25">
                      <Landmark className="h-5 w-5" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="text-sm font-semibold text-foreground">
                        {museum.name}
                      </p>
                      <p className="mt-0.5 text-xs text-muted-foreground">
                        {museum.city ? `${museum.city}, ` : ""}
                        {museum.state}
                      </p>
                      {museum.description && (
                        <p className="mt-1 text-xs text-muted-foreground">
                          {museum.description}
                        </p>
                      )}
                      {museum.website_url && (
                        <a
                          href={museum.website_url}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="mt-1.5 inline-flex items-center gap-1 text-xs font-medium text-primary hover:underline"
                        >
                          Visit website
                          <ExternalLink className="h-3 w-3" />
                        </a>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
              <Landmark className="h-8 w-8 text-muted-foreground" />
              <p className="max-w-sm text-sm text-muted-foreground">
                No museums found{stateFilter ? ` in ${stateFilter}` : ""}. Know a
                great rock, gem, or mineral museum? Add it to the directory!
              </p>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setShowAddMuseum(true)}
                className="gap-2"
              >
                <Plus className="h-4 w-4" />
                Add a Museum
              </Button>
            </div>
          )}
        </>
      )}

      {/* Add museum dialog */}
      <Dialog open={showAddMuseum} onOpenChange={setShowAddMuseum}>
        <DialogContent aria-describedby={undefined} className="max-w-md">
          <DialogHeader>
            <DialogTitle>Add a Museum</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="museum-name">Museum Name *</Label>
              <Input
                id="museum-name"
                value={museumForm.name}
                onChange={(e) =>
                  setMuseumForm((f) => ({ ...f, name: e.target.value }))
                }
                placeholder="e.g. Smithsonian National Museum of Natural History"
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <Label htmlFor="museum-state">State *</Label>
                <select
                  id="museum-state"
                  value={museumForm.state}
                  onChange={(e) =>
                    setMuseumForm((f) => ({ ...f, state: e.target.value }))
                  }
                  className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm text-foreground"
                >
                  <option value="">Select state</option>
                  {US_STATES.map((s) => (
                    <option key={s} value={s}>
                      {s}
                    </option>
                  ))}
                </select>
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="museum-city">City</Label>
                <Input
                  id="museum-city"
                  value={museumForm.city}
                  onChange={(e) =>
                    setMuseumForm((f) => ({ ...f, city: e.target.value }))
                  }
                  placeholder="e.g. Washington"
                />
              </div>
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="museum-url">Website URL</Label>
              <Input
                id="museum-url"
                value={museumForm.website_url}
                onChange={(e) =>
                  setMuseumForm((f) => ({ ...f, website_url: e.target.value }))
                }
                placeholder="https://..."
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="museum-desc">Description (optional)</Label>
              <Input
                id="museum-desc"
                value={museumForm.description}
                onChange={(e) =>
                  setMuseumForm((f) => ({ ...f, description: e.target.value }))
                }
                placeholder="What makes this museum special?"
              />
            </div>
            <p className="text-xs text-muted-foreground">
              Submitted museums are reviewed before appearing in the directory.
            </p>
          </div>
          <DialogFooter className="gap-2">
            <Button variant="outline" onClick={() => setShowAddMuseum(false)}>
              Cancel
            </Button>
            <Button
              onClick={() => submitMuseum.mutate()}
              disabled={
                submitMuseum.isPending ||
                !museumForm.name.trim() ||
                !museumForm.state
              }
            >
              {submitMuseum.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                "Submit"
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
