import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import { Toaster } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";

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

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
        <Routes>
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
          {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
