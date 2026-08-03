import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { SculptedCard, ScreenScaffold } from "@/components/sculpted";
import TripPlanner from "./TripPlanner";
import FieldJournal from "./FieldJournal";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";

/**
 * Merged Trip Planner & Field Journal screen — a single page with
 * a pill switcher between Trip Planner and Field Journal tabs.
 * Mirrors the Android TripJournalScreen pattern.
 */
export default function TripJournal() {
  const [activeTab, setActiveTab] = useState<"planner" | "journal">("planner");

  return (
    <ScreenScaffold
      title={activeTab === "planner" ? "Trip Planner" : "Field Journal"}
     
    >
      <div className="space-y-0 px-0 pb-0">
        {/* Pill switcher */}
        <div className="flex items-center justify-center gap-3 px-4 py-3">
          <button
            onClick={() => setActiveTab("planner")}
            className="rounded-full px-6 py-2 text-sm font-bold transition-all"
            style={{
              backgroundColor: activeTab === "planner" ? `hsl(${CITRINE_HEX} / 0.18)` : "transparent",
              color: activeTab === "planner" ? `hsl(${CITRINE_HEX})` : "hsl(var(--muted-foreground))",
              border: `1.5px solid ${activeTab === "planner" ? `hsl(${CITRINE_HEX})` : "hsl(var(--border))"}`,
              boxShadow: activeTab === "planner" ? `0 0 8px hsl(${CITRINE_HEX} / 0.3)` : "none",
            }}
          >
            Trip Planner
          </button>
          <button
            onClick={() => setActiveTab("journal")}
            className="rounded-full px-6 py-2 text-sm font-bold transition-all"
            style={{
              backgroundColor: activeTab === "journal" ? `hsl(${AQUA_HEX} / 0.18)` : "transparent",
              color: activeTab === "journal" ? `hsl(${AQUA_HEX})` : "hsl(var(--muted-foreground))",
              border: `1.5px solid ${activeTab === "journal" ? `hsl(${AQUA_HEX})` : "hsl(var(--border))"}`,
              boxShadow: activeTab === "journal" ? `0 0 8px hsl(${AQUA_HEX} / 0.3)` : "none",
            }}
          >
            Field Journal
          </button>
        </div>

        {/* Tab content */}
        {activeTab === "planner" ? <TripPlanner /> : <FieldJournal />}
      </div>
    </ScreenScaffold>
  );
}
