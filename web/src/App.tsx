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
