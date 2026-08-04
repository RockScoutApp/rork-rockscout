import { useEffect, type ReactNode } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { Toaster } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { AuthProvider } from "@/hooks/useAuth";
import { TierProvider } from "@/hooks/useTier";
import { OfflineSyncProvider } from "@/hooks/useOfflineSyncContext";
import { PremiumGate } from "@/components/app/PremiumGate";
import { UpdateBanner } from "@/components/UpdateBanner";
import { initOfflineMessageQueue, setRetryFunction } from "@/lib/offline-message-queue";
import { supabase } from "@/lib/supabase";

import Landing from "./pages/Landing";
import HowToUse from "./pages/HowToUse";
import Support from "./pages/Support";
import Privacy from "./pages/Privacy";
import Terms from "./pages/Terms";
import CommunityGuidelines from "./pages/CommunityGuidelines";
import TradeDisclaimer from "./pages/TradeDisclaimer";
import Safety from "./pages/Safety";
import Cookies from "./pages/Cookies";
import Press from "./pages/Press";
import NotFound from "./pages/NotFound";
import InstallPWA from "./pages/InstallPWA";

// PWA app pages
import AppLayout from "@/components/app/AppLayout";
import SignIn from "./pages/app/SignIn";
import Home from "./pages/app/Home";
import Identify from "./pages/app/Identify";
import Specimens from "./pages/app/Specimens";
import SpecimenDetail from "./pages/app/SpecimenDetail";
import Collection from "./pages/app/Collection";
import MapPage from "./pages/app/MapPage";
import LocationDetail from "./pages/app/LocationDetail";
import FavoriteSpots from "./pages/app/FavoriteSpots";
import FieldJournal from "./pages/app/FieldJournal";
import TripPlanner from "./pages/app/TripPlanner";
import TradeBoard from "./pages/app/TradeBoard";
import Community from "./pages/app/Community";
import Friends from "./pages/app/Friends";
import UserProfile from "./pages/app/UserProfile";
import Achievements from "./pages/app/Achievements";
import ReferenceLibrary from "./pages/app/ReferenceLibrary";
import GearGuide from "./pages/app/GearGuide";
import GemShows from "./pages/app/GemShows";
import Paywall from "./pages/app/Paywall";
import Notifications from "./pages/app/Notifications";
import Referral from "./pages/app/Referral";
import Glossary from "./pages/app/Glossary";
import MohsScale from "./pages/app/MohsScale";
import CrystalSystems from "./pages/app/CrystalSystems";
import GeologyReference from "./pages/app/GeologyReference";
import Fluorescence from "./pages/app/Fluorescence";
import MineralCare from "./pages/app/MineralCare";
import LapidaryBasics from "./pages/app/LapidaryBasics";
import MeteoriteHunting from "./pages/app/MeteoriteHunting";
import Paleontology from "./pages/app/Paleontology";
import AuroraTracker from "./pages/app/AuroraTracker";
import StarsConstellations from "./pages/app/StarsConstellations";
import SevereWeather from "./pages/app/SevereWeather";
import NaturalWonders from "./pages/app/NaturalWonders";
import BlmGuide from "./pages/app/BlmGuide";
import Profile from "./pages/app/Profile";
import OfflineDownloads from "./pages/app/OfflineDownloads";
import Settings from "./pages/app/Settings";
import FieldCaptures from "./pages/app/FieldCaptures";
import SavedImages from "./pages/app/SavedImages";
import Wishlist from "./pages/app/Wishlist";
import Artifacts from "./pages/app/Artifacts";
import ArtifactDetail from "./pages/app/ArtifactDetail";
import Locations from "./pages/app/Locations";
import GemShowDetail from "./pages/app/GemShowDetail";
import AllAchievements from "./pages/app/AllAchievements";
import InAppHowToUse from "./pages/app/InAppHowToUse";
import ContactUs from "./pages/app/ContactUs";
import TokenInfo from "./pages/app/TokenInfo";
import ThankYou from "./pages/app/ThankYou";
import Search from "./pages/app/Search";
import CommunityPostDetail from "./pages/app/CommunityPostDetail";
import PrehistoricOrganisms from "./pages/app/PrehistoricOrganisms";
import PeriodicTable from "./pages/app/PeriodicTable";
import TectonicVolcanic from "./pages/app/TectonicVolcanic";
import ResourceLinks from "./pages/app/ResourceLinks";
import RocksAreAmazing from "./pages/app/RocksAreAmazing";
import RockTypes from "./pages/app/RockTypes";
import MineralId from "./pages/app/MineralId";
import CrystalHardness from "./pages/app/CrystalHardness";
import RockCycleTools from "./pages/app/RockCycleTools";
import GeoTimeScale from "./pages/app/GeoTimeScale";
import MassExtinctions from "./pages/app/MassExtinctions";
import FossilTypes from "./pages/app/FossilTypes";
import GeologicPeriods from "./pages/app/GeologicPeriods";
import PeriodDetail from "./pages/app/PeriodDetail";
import StateParks from "./pages/app/StateParks";
import StateParkDetail from "./pages/app/StateParkDetail";
import TradingFloor from "./pages/app/TradingFloor";
import MyTrades from "./pages/app/MyTrades";
import SocialSettings from "./pages/app/SocialSettings";
import DiscoverHunters from "./pages/app/DiscoverHunters";
import RockScoutsMap from "./pages/app/RockScoutsMap";
import ArchivedTrips from "./pages/app/ArchivedTrips";
import ProfileFriends from "./pages/app/ProfileFriends";
import SharedSpot from "./pages/app/SharedSpot";
import Disclaimer from "./pages/app/Disclaimer";
import DeveloperConsole from "./pages/app/DeveloperConsole";
import Scan from "./pages/app/Scan";
import Messenger from "./pages/app/Messenger";
import ManageDevices from "./pages/app/ManageDevices";
import DinosaurDictionary from "./pages/app/DinosaurDictionary";
import CampgroundsTrailheads from "./pages/app/CampgroundsTrailheads";
import Planets from "./pages/app/Planets";
import DeepSkyObjects from "./pages/app/DeepSkyObjects";
import ImportantStars from "./pages/app/ImportantStars";
import TripCalendar from "./pages/app/TripCalendar";
import TripJournal from "./pages/app/TripJournal";
import RockGuideDetail from "./pages/app/RockGuideDetail";
import RockInfo from "./pages/app/RockInfo";
import UserAchievements from "./pages/app/UserAchievements";
import UserCollection from "./pages/app/UserCollection";
import BlmDetail from "./pages/app/BlmDetail";
import AccountDeletedAppeal from "./pages/app/AccountDeletedAppeal";

const queryClient = new QueryClient();

/**
 * Root provider stack.
 *
 * `TierProvider` lives here rather than being scoped to the `/app` subtree: the
 * marketing `Layout` footer renders `InstallAppButton`, which calls `useTier()`.
 * Scoping the provider to authenticated routes made `useTier()` return undefined
 * on `/`, `/how-to-use`, `/support`, `/press` and `404`, crashing the render and
 * producing a black screen. `useTier` degrades gracefully when signed out (the
 * profile query is disabled and the user is treated as free), so mounting it at
 * the root is safe for anonymous visitors.
 */
export function Providers({ children }: { children: ReactNode }) {
  // Initialize offline message queue — listens for online events and drains pending messages
  useEffect(() => {
    initOfflineMessageQueue();
    // Set up the retry function that sends queued messages when connectivity is restored
    setRetryFunction(async (msg) => {
      if (msg.isGroup) {
        const { error } = await supabase.from("group_messages").insert({
          group_chat_id: msg.chatId,
          sender_id: msg.senderId,
          body: msg.body,
          image_url: msg.imageUrl,
          reply_to_message_id: msg.replyToMessageId,
          tagged_user_ids: msg.taggedUserIds,
        });
        if (error) throw error;
      } else {
        const { error } = await supabase.from("chat_messages").insert({
          thread_id: msg.chatId,
          sender_id: msg.senderId,
          body: msg.body,
          image_url: msg.imageUrl,
          reply_to_message_id: msg.replyToMessageId,
          tagged_user_ids: msg.taggedUserIds,
        });
        if (error) throw error;
        await supabase
          .from("chat_threads")
          .update({ last_message_at: new Date().toISOString() })
          .eq("id", msg.chatId);
      }
    });
  }, []);

  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <TierProvider>
          <OfflineSyncProvider>
            <TooltipProvider>{children}</TooltipProvider>
          </OfflineSyncProvider>
        </TierProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}

const App = () => (
  <Providers>
    <Toaster />
    {/* Surfaces a one-tap refresh whenever a newer build has been deployed,
        so installed PWAs never get stuck on an old service-worker cache. */}
    <UpdateBanner />
    <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <Routes>
        {/* Marketing site routes — unchanged */}
        <Route path="/" element={<Landing />} />
        <Route path="/how-to-use" element={<HowToUse />} />
        <Route path="/support" element={<Support />} />
        <Route path="/privacy" element={<Privacy />} />
        <Route path="/terms" element={<Terms />} />
        <Route path="/community-guidelines" element={<CommunityGuidelines />} />
        <Route path="/trade-disclaimer" element={<TradeDisclaimer />} />
        <Route path="/safety" element={<Safety />} />
        <Route path="/cookies" element={<Cookies />} />
        <Route path="/press" element={<Press />} />
        <Route path="/install" element={<Navigate to="/install/free" replace />} />
        <Route path="/install/free" element={<InstallPWA mode="free" />} />
        <Route path="/install/premium" element={<InstallPWA mode="premium" />} />

        {/* PWA app routes — free content is open; auth only triggers for
            personal-data (bookmarks) or premium features. */}
        <Route path="/app/signin" element={<SignIn />} />
        <Route path="/app" element={<AppLayout />}>
          <Route index element={<PremiumGate routePath=""><Home /></PremiumGate>} />
          <Route path="identify" element={<PremiumGate routePath="identify"><Identify /></PremiumGate>} />
          <Route path="specimens" element={<PremiumGate routePath="specimens"><Specimens /></PremiumGate>} />
          <Route path="specimens/:id" element={<PremiumGate routePath="specimens/:id"><SpecimenDetail /></PremiumGate>} />
          <Route path="collection" element={<PremiumGate routePath="collection"><Collection /></PremiumGate>} />
          <Route path="map" element={<PremiumGate routePath="map"><MapPage /></PremiumGate>} />
          <Route path="locations/:id" element={<PremiumGate routePath="locations/:id"><LocationDetail /></PremiumGate>} />
          <Route path="favorites" element={<PremiumGate routePath="favorites"><FavoriteSpots /></PremiumGate>} />
          <Route path="journal" element={<PremiumGate routePath="journal"><FieldJournal /></PremiumGate>} />
          <Route path="trips" element={<PremiumGate routePath="trips"><TripPlanner /></PremiumGate>} />
          <Route path="trade" element={<PremiumGate routePath="trade"><TradeBoard /></PremiumGate>} />
          <Route path="community" element={<PremiumGate routePath="community"><Community /></PremiumGate>} />
          <Route path="friends" element={<PremiumGate routePath="friends"><Friends /></PremiumGate>} />
          <Route path="profile/:id" element={<PremiumGate routePath="profile/:id"><UserProfile /></PremiumGate>} />
          <Route path="achievements" element={<PremiumGate routePath="achievements"><Achievements /></PremiumGate>} />
          <Route path="reference" element={<PremiumGate routePath="reference"><ReferenceLibrary /></PremiumGate>} />
          <Route path="gear" element={<PremiumGate routePath="gear"><GearGuide /></PremiumGate>} />
          <Route path="gem-shows" element={<PremiumGate routePath="gem-shows"><GemShows /></PremiumGate>} />
          <Route path="paywall" element={<PremiumGate routePath="paywall"><Paywall /></PremiumGate>} />
          <Route path="notifications" element={<PremiumGate routePath="notifications"><Notifications /></PremiumGate>} />
          <Route path="referral" element={<PremiumGate routePath="referral"><Referral /></PremiumGate>} />
          <Route path="glossary" element={<PremiumGate routePath="glossary"><Glossary /></PremiumGate>} />
          <Route path="mohs-scale" element={<PremiumGate routePath="mohs-scale"><MohsScale /></PremiumGate>} />
          <Route path="crystal-systems" element={<PremiumGate routePath="crystal-systems"><CrystalSystems /></PremiumGate>} />
          <Route path="geology" element={<PremiumGate routePath="geology"><GeologyReference /></PremiumGate>} />
          <Route path="fluorescence" element={<PremiumGate routePath="fluorescence"><Fluorescence /></PremiumGate>} />
          <Route path="mineral-care" element={<PremiumGate routePath="mineral-care"><MineralCare /></PremiumGate>} />
          <Route path="lapidary" element={<PremiumGate routePath="lapidary"><LapidaryBasics /></PremiumGate>} />
          <Route path="meteorite-hunting" element={<PremiumGate routePath="meteorite-hunting"><MeteoriteHunting /></PremiumGate>} />
          <Route path="paleontology" element={<PremiumGate routePath="paleontology"><Paleontology /></PremiumGate>} />
          <Route path="aurora" element={<PremiumGate routePath="aurora"><AuroraTracker /></PremiumGate>} />
          <Route path="stars" element={<PremiumGate routePath="stars"><StarsConstellations /></PremiumGate>} />
          <Route path="severe-weather" element={<PremiumGate routePath="severe-weather"><SevereWeather /></PremiumGate>} />
          <Route path="natural-wonders" element={<PremiumGate routePath="natural-wonders"><NaturalWonders /></PremiumGate>} />
          <Route path="blm-guide" element={<PremiumGate routePath="blm-guide"><BlmGuide /></PremiumGate>} />
          <Route path="profile" element={<PremiumGate routePath="profile"><Profile /></PremiumGate>} />
          <Route path="offline" element={<PremiumGate routePath="offline"><OfflineDownloads /></PremiumGate>} />
          <Route path="settings" element={<PremiumGate routePath="settings"><Settings /></PremiumGate>} />
          <Route path="captures" element={<PremiumGate routePath="captures"><FieldCaptures /></PremiumGate>} />
          <Route path="saved-images" element={<PremiumGate routePath="saved-images"><SavedImages /></PremiumGate>} />
          <Route path="wishlist" element={<PremiumGate routePath="wishlist"><Wishlist /></PremiumGate>} />
          <Route path="artifacts" element={<PremiumGate routePath="artifacts"><Artifacts /></PremiumGate>} />
          <Route path="artifacts/:id" element={<PremiumGate routePath="artifacts/:id"><ArtifactDetail /></PremiumGate>} />
          <Route path="locations" element={<PremiumGate routePath="locations"><Locations /></PremiumGate>} />
          <Route path="gem-shows/:id" element={<PremiumGate routePath="gem-shows/:id"><GemShowDetail /></PremiumGate>} />
          <Route path="all-achievements" element={<PremiumGate routePath="all-achievements"><AllAchievements /></PremiumGate>} />
          <Route path="how-to-use" element={<PremiumGate routePath="how-to-use"><InAppHowToUse /></PremiumGate>} />
          <Route path="contact" element={<PremiumGate routePath="contact"><ContactUs /></PremiumGate>} />
          <Route path="token-info" element={<PremiumGate routePath="token-info"><TokenInfo /></PremiumGate>} />
          <Route path="thank-you/:tokens/:days" element={<ThankYou />} />
          <Route path="search" element={<PremiumGate routePath="search"><Search /></PremiumGate>} />
          <Route path="community/:postId" element={<PremiumGate routePath="community/:postId"><CommunityPostDetail /></PremiumGate>} />
          <Route path="prehistoric-organisms" element={<PremiumGate routePath="prehistoric-organisms"><PrehistoricOrganisms /></PremiumGate>} />
          <Route path="periodic-table" element={<PremiumGate routePath="periodic-table"><PeriodicTable /></PremiumGate>} />
          <Route path="tectonic-volcanic" element={<PremiumGate routePath="tectonic-volcanic"><TectonicVolcanic /></PremiumGate>} />
          <Route path="resource-links" element={<PremiumGate routePath="resource-links"><ResourceLinks /></PremiumGate>} />
          <Route path="rocks-are-amazing" element={<PremiumGate routePath="rocks-are-amazing"><RocksAreAmazing /></PremiumGate>} />
          <Route path="rock-types" element={<PremiumGate routePath="rock-types"><RockTypes /></PremiumGate>} />
          <Route path="mineral-id" element={<PremiumGate routePath="mineral-id"><MineralId /></PremiumGate>} />
          <Route path="crystal-hardness" element={<PremiumGate routePath="crystal-hardness"><CrystalHardness /></PremiumGate>} />
          <Route path="rock-cycle" element={<PremiumGate routePath="rock-cycle"><RockCycleTools /></PremiumGate>} />
          <Route path="geo-time-scale" element={<PremiumGate routePath="geo-time-scale"><GeoTimeScale /></PremiumGate>} />
          <Route path="mass-extinctions" element={<PremiumGate routePath="mass-extinctions"><MassExtinctions /></PremiumGate>} />
          <Route path="fossil-types" element={<PremiumGate routePath="fossil-types"><FossilTypes /></PremiumGate>} />
          <Route path="geologic-periods" element={<PremiumGate routePath="geologic-periods"><GeologicPeriods /></PremiumGate>} />
          <Route path="period/:id" element={<PremiumGate routePath="period/:id"><PeriodDetail /></PremiumGate>} />
          <Route path="state-parks" element={<PremiumGate routePath="state-parks"><StateParks /></PremiumGate>} />
          <Route path="state-park/:id" element={<PremiumGate routePath="state-park/:id"><StateParkDetail /></PremiumGate>} />
          <Route path="trading-floor" element={<PremiumGate routePath="trading-floor"><TradingFloor /></PremiumGate>} />
          <Route path="my-trades" element={<PremiumGate routePath="my-trades"><MyTrades /></PremiumGate>} />
          <Route path="social-settings" element={<PremiumGate routePath="social-settings"><SocialSettings /></PremiumGate>} />
          <Route path="discover-hunters" element={<PremiumGate routePath="discover-hunters"><DiscoverHunters /></PremiumGate>} />
          <Route path="rockscouts-map" element={<PremiumGate routePath="rockscouts-map"><RockScoutsMap /></PremiumGate>} />
          <Route path="archived-trips" element={<PremiumGate routePath="archived-trips"><ArchivedTrips /></PremiumGate>} />
          <Route path="profile/friends" element={<PremiumGate routePath="profile/friends"><ProfileFriends /></PremiumGate>} />
          <Route path="shared-spot/:lat/:lng" element={<PremiumGate routePath="shared-spot/:lat/:lng"><SharedSpot /></PremiumGate>} />
          <Route path="disclaimer" element={<PremiumGate routePath="disclaimer"><Disclaimer /></PremiumGate>} />
          <Route path="dev-console" element={<PremiumGate routePath="dev-console"><DeveloperConsole /></PremiumGate>} />
          <Route path="scan" element={<PremiumGate routePath="scan"><Scan /></PremiumGate>} />
          <Route path="messenger" element={<PremiumGate routePath="messenger"><Messenger /></PremiumGate>} />
          <Route path="manage-devices" element={<PremiumGate routePath="manage-devices"><ManageDevices /></PremiumGate>} />

          {/* Route aliases for Home dashboard tiles */}
          <Route path="blm" element={<PremiumGate routePath="blm"><BlmGuide /></PremiumGate>} />
          <Route path="campgrounds" element={<PremiumGate routePath="campgrounds"><CampgroundsTrailheads /></PremiumGate>} />
          <Route path="dinosaurs" element={<PremiumGate routePath="dinosaurs"><DinosaurDictionary /></PremiumGate>} />
          <Route path="meteorites" element={<PremiumGate routePath="meteorites"><MeteoriteHunting /></PremiumGate>} />
          <Route path="prehistoric" element={<PremiumGate routePath="prehistoric"><PrehistoricOrganisms /></PremiumGate>} />
          <Route path="resources" element={<PremiumGate routePath="resources"><ResourceLinks /></PremiumGate>} />
          <Route path="tectonics" element={<PremiumGate routePath="tectonics"><TectonicVolcanic /></PremiumGate>} />
          <Route path="weather" element={<PremiumGate routePath="weather"><SevereWeather /></PremiumGate>} />
          <Route path="planets" element={<PremiumGate routePath="planets"><Planets /></PremiumGate>} />
          <Route path="deep-sky" element={<PremiumGate routePath="deep-sky"><DeepSkyObjects /></PremiumGate>} />
          <Route path="important-stars" element={<PremiumGate routePath="important-stars"><ImportantStars /></PremiumGate>} />

          {/* Phase 6: New screens */}
          <Route path="trip-calendar" element={<PremiumGate routePath="trip-calendar"><TripCalendar /></PremiumGate>} />
          <Route path="trip-journal" element={<PremiumGate routePath="trip-journal"><TripJournal /></PremiumGate>} />
          <Route path="guide/:guideId" element={<PremiumGate routePath="guide/:guideId"><RockGuideDetail /></PremiumGate>} />
          <Route path="rock-info" element={<PremiumGate routePath="rock-info"><RockInfo /></PremiumGate>} />
          <Route path="user-achievements/:id" element={<PremiumGate routePath="user-achievements/:id"><UserAchievements /></PremiumGate>} />
          <Route path="user-collection/:id/:mode" element={<PremiumGate routePath="user-collection/:id/:mode"><UserCollection /></PremiumGate>} />
          <Route path="blm/:id" element={<PremiumGate routePath="blm/:id"><BlmDetail /></PremiumGate>} />
          <Route path="account-appeal" element={<AccountDeletedAppeal />} />
        </Route>

        {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  </Providers>
);

export default App;
