import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import { Toaster } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { AuthProvider, useAuth } from "@/hooks/useAuth";

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
import type { ReactNode } from "react";

const queryClient = new QueryClient();

/** Auth gate — wraps the app layout. Redirects to the sign-in screen when
 *  the user is not authenticated. */
function RequireAuth({ children }: { children: ReactNode }) {
  const { user, isLoading } = useAuth();
  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    );
  }
  if (!user) return <SignIn />;
  return <>{children}</>;
}

const App = () => (
  <QueryClientProvider client={queryClient}>
    <AuthProvider>
      <TooltipProvider>
        <Toaster />
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

            {/* PWA app routes — gated by auth */}
            <Route
              path="/app"
              element={
                <RequireAuth>
                  <AppLayout />
                </RequireAuth>
              }
            >
              <Route index element={<Home />} />
              <Route path="identify" element={<Identify />} />
              <Route path="specimens" element={<Specimens />} />
              <Route path="specimens/:id" element={<SpecimenDetail />} />
              <Route path="collection" element={<Collection />} />
              <Route path="map" element={<MapPage />} />
              <Route path="locations/:id" element={<LocationDetail />} />
              <Route path="favorites" element={<FavoriteSpots />} />
              <Route path="journal" element={<FieldJournal />} />
              <Route path="trips" element={<TripPlanner />} />
              <Route path="trade" element={<TradeBoard />} />
              <Route path="community" element={<Community />} />
              <Route path="friends" element={<Friends />} />
              <Route path="profile/:id" element={<UserProfile />} />
              <Route path="achievements" element={<Achievements />} />
              <Route path="reference" element={<ReferenceLibrary />} />
              <Route path="gear" element={<GearGuide />} />
              <Route path="gem-shows" element={<GemShows />} />
              <Route path="paywall" element={<Paywall />} />
              <Route path="notifications" element={<Notifications />} />
              <Route path="referral" element={<Referral />} />
              <Route path="glossary" element={<Glossary />} />
              <Route path="mohs-scale" element={<MohsScale />} />
              <Route path="crystal-systems" element={<CrystalSystems />} />
              <Route path="geology" element={<GeologyReference />} />
              <Route path="fluorescence" element={<Fluorescence />} />
              <Route path="mineral-care" element={<MineralCare />} />
              <Route path="lapidary" element={<LapidaryBasics />} />
              <Route path="meteorite-hunting" element={<MeteoriteHunting />} />
              <Route path="paleontology" element={<Paleontology />} />
              <Route path="aurora" element={<AuroraTracker />} />
              <Route path="stars" element={<StarsConstellations />} />
              <Route path="severe-weather" element={<SevereWeather />} />
              <Route path="natural-wonders" element={<NaturalWonders />} />
              <Route path="blm-guide" element={<BlmGuide />} />
              <Route path="profile" element={<Profile />} />
              <Route path="offline" element={<OfflineDownloads />} />
              <Route path="settings" element={<Settings />} />
              <Route path="captures" element={<FieldCaptures />} />
              <Route path="saved-images" element={<SavedImages />} />
              <Route path="wishlist" element={<Wishlist />} />
              <Route path="artifacts" element={<Artifacts />} />
              <Route path="artifacts/:id" element={<ArtifactDetail />} />
              <Route path="locations" element={<Locations />} />
              <Route path="gem-shows/:id" element={<GemShowDetail />} />
              <Route path="all-achievements" element={<AllAchievements />} />
              <Route path="how-to-use" element={<InAppHowToUse />} />
              <Route path="contact" element={<ContactUs />} />
              <Route path="token-info" element={<TokenInfo />} />
              <Route path="thank-you/:tokens/:days" element={<ThankYou />} />
              <Route path="search" element={<Search />} />
              <Route path="community/:postId" element={<CommunityPostDetail />} />
              <Route path="prehistoric-organisms" element={<PrehistoricOrganisms />} />
              <Route path="periodic-table" element={<PeriodicTable />} />
              <Route path="tectonic-volcanic" element={<TectonicVolcanic />} />
              <Route path="resource-links" element={<ResourceLinks />} />
              <Route path="rocks-are-amazing" element={<RocksAreAmazing />} />
              <Route path="rock-types" element={<RockTypes />} />
              <Route path="mineral-id" element={<MineralId />} />
              <Route path="crystal-hardness" element={<CrystalHardness />} />
              <Route path="rock-cycle" element={<RockCycleTools />} />
              <Route path="geo-time-scale" element={<GeoTimeScale />} />
              <Route path="mass-extinctions" element={<MassExtinctions />} />
              <Route path="fossil-types" element={<FossilTypes />} />
              <Route path="geologic-periods" element={<GeologicPeriods />} />
              <Route path="period/:id" element={<PeriodDetail />} />
              <Route path="state-parks" element={<StateParks />} />
              <Route path="state-park/:id" element={<StateParkDetail />} />
              <Route path="trading-floor" element={<TradingFloor />} />
              <Route path="my-trades" element={<MyTrades />} />
              <Route path="social-settings" element={<SocialSettings />} />
              <Route path="discover-hunters" element={<DiscoverHunters />} />
              <Route path="rockscouts-map" element={<RockScoutsMap />} />
              <Route path="archived-trips" element={<ArchivedTrips />} />
              <Route path="profile/friends" element={<ProfileFriends />} />
              <Route path="shared-spot/:lat/:lng" element={<SharedSpot />} />
              <Route path="disclaimer" element={<Disclaimer />} />
              <Route path="dev-console" element={<DeveloperConsole />} />
              <Route path="scan" element={<Scan />} />
              <Route path="messenger" element={<Messenger />} />
            </Route>

            {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </TooltipProvider>
    </AuthProvider>
  </QueryClientProvider>
);

export default App;
