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
  Mail,
  FileText,
  Smartphone,
  Locate,
  CloudUpload,
  RefreshCw,
  ShieldCheck,
  MessageSquare,
  Search,
  Landmark,
  Clock,
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
  {
    title: "Ask an Expert & Museum Finder",
    icon: Mail,
    body: "When an identification has low confidence, tap \"Ask an Expert\" to search for nearby museums and geological institutions. Select multiple museums and compose an email with your photo and ID results attached — drafts auto-save across app restarts.",
  },
  {
    title: "PDF Identification Reports",
    icon: FileText,
    body: "Tap the PDF icon on any match card to generate a printable 1-2 page report with your photo, all matches, AI analysis, and web references. Share it via email or save it to your files.",
  },
  {
    title: "Free & Premium PWA",
    icon: Smartphone,
    body: "Install RockScout as a PWA on desktop, laptop, or tablet. Free tier: read-only access to the full database, guides, and map. Premium tier: all features unlocked on up to 2 additional devices with email confirmation.",
  },
  {
    title: "Offline Photo Sync Queue",
    icon: CloudUpload,
    body: "When you capture photos with no signal, RockScout stores them in a local offline sync queue automatically. The queue holds your photos, form data, and location pins safely on-device until your connection is restored, then drains automatically — uploading everything to your cloud storage on Supabase without any action needed. A periodic background sync runs every 6 hours, and an optional nightly sync at 4 AM in your local time zone backs up a day's worth of captures while your device charges on WiFi.",
  },
  {
    title: "Sync Now — Manual Upload",
    icon: RefreshCw,
    body: "Open Settings and tap the \"Sync Now\" button in the Storage section to force an immediate upload of all pending local changes — field captures, saved images, field journal entries, and trip planner data. Use it after a long field day with no signal to push everything to the cloud immediately.",
  },
  {
    title: "Data Security & Row Level Security",
    icon: ShieldCheck,
    body: "Your specimen captures, field journal entries, saved images, and trip planner data are protected by Supabase Row Level Security (RLS) policies. Only you can view, edit, or delete your own data. Photos are stored in individual user-specific storage buckets, encrypted in transit via HTTPS/TLS, and your credentials are managed through Supabase Auth with secure session management.",
  },
  {
    title: "Search Near Me",
    icon: Locate,
    body: "Tap \"Search Near Me\" on the Dig Sites section to run a web search for rock-related places near your GPS location. Searches 50 miles first, then expands to 100 miles automatically. Results open in your browser.",
  },
  {
    title: "Group Chats",
    icon: Users,
    body: "Create group chats with a name, subject, member cap, header image, and profanity filter level (normal or strict). Invite friends from your connections — they get a popup to accept or decline. The creator can delete the group at any time. Group chats support image sending, reply threading, user tagging, and scroll speed controls.",
  },
  {
    title: "Reply Threading & User Tagging",
    icon: MessageSquare,
    body: "Long-press any message to reply to it — the original comment appears in a preview bar above your input, and the tagged username (@username) is inserted automatically. Your reply shows indented under the original. Type @username in any message to tag someone — their name appears in a bright Citrine pill so they know they were mentioned.",
  },
  {
    title: "Scroll Speed Controls",
    icon: Zap,
    body: "Control how the chat auto-scrolls to new messages: Normal (instant), Half (4-second delay), or Stop (no auto-scroll). When you're scrolled up reading older messages, a Current button jumps you to the latest message instantly.",
  },
  {
    title: "Drafts in Notifications",
    icon: Clock,
    body: "Unfinished email and chat drafts are automatically saved and appear as a notification row. Tap a draft to resume right where you left off — your text, recipients, and attachments are all preserved. Drafts are deleted when you send the message.",
  },
  {
    title: "Compact Search Bars",
    icon: Search,
    body: "Search bars across the app collapse into compact single-row pills that expand on tap — saving screen space while keeping search always accessible. Found on Community, Messenger, Specimens, Natural Wonders, Glossary, Dinosaur Dictionary, and more.",
  },
  {
    title: "Profanity Warning System",
    icon: ShieldCheck,
    body: "A two-tier profanity filter keeps chat family-friendly. Common profanity is silently asterisked. Explicit language is asterisked and triggers a warning popup. Three warnings auto-report the user, five trigger a second report, and six result in a ban. False positives can be reported via support@rockscout.app.",
  },
  {
    title: "Museum Directory & Add Button",
    icon: Landmark,
    body: "Browse a directory of rock, gem, and mineral museums on the Museums tab of Rock & Gem Resources. Filter by state or search by name. Found a museum that isn't listed? Tap the Add a Museum button to submit it — after review, it appears in the directory for every RockScout user.",
  },
  {
    title: "Add Users to Private Chats",
    icon: Users,
    body: "Add up to 5 users to a private chat thread. Tap the Add User icon in the chat header, select friends from your list, and each invited user gets an accept/cancel popup. Once accepted, they're part of the conversation with full messaging, image sharing, and reply threading support.",
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
            className="dark-card sculpted-raised rounded-xl p-4"
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

      <div className="dark-card sculpted-raised rounded-xl p-4">
        <h3 className="text-sm font-semibold text-foreground">
          5 MB Upload Limit
        </h3>
        <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
          All photo uploads (Identify, Field Camera, Submit Specimen) are limited to 5 MB each. If
          your photo is larger, choose a smaller one or compress it first. Specimen submissions accept up to 10 photos.
        </p>
      </div>

      <div className="dark-card sculpted-raised rounded-xl p-4">
        <h3 className="text-sm font-semibold text-foreground">
          Cross-Platform Sync
        </h3>
        <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
          Your collection, wishlist, field journal, trips, favorite spots, and
          achievements sync across the Android app and the web PWA. Sign in
          with the same account on any device — your data follows you.
        </p>
      </div>

      <div className="dark-card sculpted-raised rounded-xl p-4">
        <h3 className="text-sm font-semibold text-foreground">
          Offline-First Design
        </h3>
        <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
          RockScout is built for the field. Bulk-download all specimen photos (~3.5 GB) for offline access, cache map tiles for dig sites and trip areas, and capture photos with no signal — the offline sync queue uploads everything automatically once you're back online. Tap Sync Now in Settings to force an immediate upload.
        </p>
      </div>
    </div>
  );
}
