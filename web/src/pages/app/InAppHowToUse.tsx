import { useNavigate } from "react-router-dom";
import {
  Camera,
  BookOpen,
  Map,
  Gem,
  Heart,
  ClipboardList,
  Calendar,
  ArrowRightLeft,
  Users,
  Award,
  Compass,
  Bell,
  Gift,
  Zap,
  ArrowLeft,
  HelpCircle,
} from "lucide-react";
import { Button } from "@/components/ui/button";

const SECTIONS: { title: string; icon: typeof Camera; body: string }[] = [
  {
    title: "Identify a Rock",
    icon: Camera,
    body: "Tap the Identify tile on the Home screen. Take a photo or upload an image — AI will identify your specimen from 900+ known rocks, minerals, gems, and fossils. The result includes the top match, confidence percentage, and other possibilities.",
  },
  {
    title: "Field Camera",
    icon: Camera,
    body: "The Field Camera lets you capture field photos and save them to any destination: Field Captures, Saved Images, My Rocks, Wishlist, Field Journal, Share to Profile, Profile Background, or Submit a Specimen. After the trial expires, free users can only save to Saved Images.",
  },
  {
    title: "Specimen Database",
    icon: BookOpen,
    body: "Browse 900+ rocks, minerals, crystals, gems, and fossils. Filter by category, search by name, and tap any specimen for full details including hardness, crystal system, streak, and where it's found.",
  },
  {
    title: "Maps & Dig Sites",
    icon: Map,
    body: "Find nearby collecting sites, mines, parks, and shops. The map shows all locations as markers — tap any marker for details. Use the Locations list view to filter by type (mines, shops, BLM sites, state parks).",
  },
  {
    title: "My Collection & Wishlist",
    icon: Gem,
    body: "Save specimens to your collection or wishlist from any specimen detail page. Your collection syncs across devices — sign in on any browser or the Android app to see the same data.",
  },
  {
    title: "Field Journal",
    icon: ClipboardList,
    body: "Log your field trips and finds — location, weather, notes, and the story of the day. Journal entries sync across devices.",
  },
  {
    title: "Trip Planner",
    icon: Calendar,
    body: "Plan your next rockhounding adventure with multi-stop trips, target specimens, and a gear checklist.",
  },
  {
    title: "Trade Board",
    icon: ArrowRightLeft,
    body: "Swap, sell, and trade specimens with other rockhounds. Always follow safe trading practices and the Trade Disclaimer.",
  },
  {
    title: "Community & Friends",
    icon: Users,
    body: "Share finds, connect with rockhounds, and message your friends. Be respectful and follow the Community Guidelines.",
  },
  {
    title: "Achievements",
    icon: Award,
    body: "Level up and earn badges by identifying rocks, building your collection, planning trips, and more. Each achievement earns XP toward your next level.",
  },
  {
    title: "Reference Library",
    icon: Compass,
    body: "Guides, geology, paleontology, gear recommendations, gem shows, and more — all in one place.",
  },
  {
    title: "Notifications",
    icon: Bell,
    body: "Your in-app notification feed. Enable push notifications in Settings to get alerts on your device.",
  },
  {
    title: "Refer a Friend",
    icon: Gift,
    body: "Earn XP for every rockhound you bring in. Share your referral link from the Refer a Friend tile.",
  },
  {
    title: "Go Premium",
    icon: Zap,
    body: "Upgrade to Premium for unlimited IDs, ad-free experience, and pro features. Your purchase works across the Android app and the web PWA — sign in with the same account.",
  },
];

export default function InAppHowToUse() {
  const navigate = useNavigate();

  return (
    <div className="space-y-6">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => navigate("/app")}
        className="gap-2"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to Home
      </Button>

      <div>
        <div className="flex items-center gap-3">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/15">
            <HelpCircle className="h-6 w-6 text-primary" />
          </div>
          <div>
            <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
              How to Use RockScout
            </h1>
            <p className="mt-0.5 text-sm text-muted-foreground">
              Everything you need to know to get started
            </p>
          </div>
        </div>
      </div>

      <div className="space-y-3">
        {SECTIONS.map((section) => (
          <div
            key={section.title}
            className="rounded-xl border border-border bg-card p-4"
          >
            <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
              <section.icon className="h-4 w-4 text-primary" />
              {section.title}
            </div>
            <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
              {section.body}
            </p>
          </div>
        ))}
      </div>

      <div className="rounded-xl border border-border bg-card p-4">
        <h3 className="text-sm font-semibold text-foreground">
          5 MB Upload Limit
        </h3>
        <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
          All photo uploads (Identify, Field Camera) are limited to 5 MB. If
          your photo is larger, choose a smaller one or compress it first.
        </p>
      </div>

      <div className="rounded-xl border border-border bg-card p-4">
        <h3 className="text-sm font-semibold text-foreground">
          Cross-Platform Sync
        </h3>
        <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
          Your collection, wishlist, field journal, trips, favorite spots, and
          achievements sync across the Android app and the web PWA. Sign in
          with the same account on any device — your data follows you.
        </p>
      </div>
    </div>
  );
}
