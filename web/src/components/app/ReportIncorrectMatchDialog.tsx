import { useState } from "react";
import { Flag, Loader2, X } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedButton } from "@/components/sculpted";

interface ReportMatchInfo {
  topMatchName: string;
  topMatchConfidence: number;
  topMatchReasoning: string;
  allMatchNames: string[];
  allMatchConfidences: number[];
  isArtifact: boolean;
}

interface ReportIncorrectMatchDialogProps {
  open: boolean;
  matchInfo: ReportMatchInfo;
  imagePreview: string | null;
  onDismiss: () => void;
}

/**
 * Dialog for reporting an incorrect ID. Shows the AI's match info (read-only),
 * an optional "I think this is actually" field, optional notes, and a thumbnail
 * of the user's uploaded photo. Submits into the existing rockscout_specimen_submissions
 * Supabase table — same table as SubmitSpecimenDialog.
 */
export default function ReportIncorrectMatchDialog({
  open,
  matchInfo,
  imagePreview,
  onDismiss,
}: ReportIncorrectMatchDialogProps) {
  const { user } = useAuth();
  const [correctedName, setCorrectedName] = useState("");
  const [notes, setNotes] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);

  if (!open) return null;

  const handleSubmit = async () => {
    if (!user) {
      toast.error("Sign in to submit a report");
      return;
    }
    setIsSubmitting(true);
    try {
      const effectiveName = correctedName.trim() || `Unknown — AI suggested ${matchInfo.topMatchName}`;
      const allMatchesStr = matchInfo.allMatchNames
        .map((n, i) => `${n} (${matchInfo.allMatchConfidences[i]}%)`)
        .join(", ");
      const description = `AI suggested: ${matchInfo.topMatchName} (${matchInfo.topMatchConfidence}%). Reasoning: ${matchInfo.topMatchReasoning}. All matches: ${allMatchesStr}.${notes.trim() ? ` User notes: ${notes.trim()}.` : ""} Report type: ${matchInfo.isArtifact ? "Artifact" : "Rock"} ID.`;

      const { error } = await supabase.from("rockscout_specimen_submissions").insert({
        submitter_id: user.id,
        name: effectiveName,
        description,
        photo_urls: imagePreview ? [imagePreview] : [],
        status: "pending",
      });

      if (error) throw error;
      setIsSubmitting(false);
      setShowSuccess(true);
    } catch {
      toast.error("Failed to submit report");
      setIsSubmitting(false);
    }
  };

  if (showSuccess) {
    return (
      <div className="fixed inset-0 z-[95] flex items-center justify-center bg-black/70">
        <div className="dark-card sculpted-raised mx-4 max-w-md rounded-2xl p-6 text-center">
          <p className="text-sm text-muted-foreground">
            We're sorry we couldn't pin down the ID, but we'll do our best to try and figure it out! Thank you for helping to expand and improve RockScout!
          </p>
          <div className="mt-5">
            <SculptedButton
              accent="citrine"
              size="md"
              className="w-full"
              onClick={() => {
                setShowSuccess(false);
                onDismiss();
              }}
            >
              Close
            </SculptedButton>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 z-[95] flex items-center justify-center bg-black/70">
      <div className="dark-card sculpted-raised mx-4 max-w-md rounded-2xl">
        <div className="flex items-center justify-between border-b border-border px-5 py-3">
          <div className="flex items-center gap-2">
            <Flag className="h-5 w-5 text-primary" />
            <h2 className="font-display text-lg font-bold text-foreground">Report incorrect ID</h2>
          </div>
          <button
            onClick={() => !isSubmitting && onDismiss()}
            className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-muted/50"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        <div className="space-y-4 px-5 py-4">
          {/* AI match info (read-only) */}
          <div className="rounded-lg border border-border bg-muted/20 p-3">
            <p className="text-sm font-bold text-foreground">
              AI suggested: {matchInfo.topMatchName} ({matchInfo.topMatchConfidence}%)
            </p>
            <p className="mt-1 text-xs text-muted-foreground">
              {matchInfo.topMatchReasoning}
            </p>
            {matchInfo.allMatchNames.length > 1 && (
              <p className="mt-1 text-xs text-muted-foreground">
                All matches: {matchInfo.allMatchNames.map((n, i) => `${n} (${matchInfo.allMatchConfidences[i]}%)`).join(", ")}
              </p>
            )}
          </div>

          {/* Photo thumbnail */}
          {imagePreview && (
            <div>
              <p className="mb-1 text-xs text-muted-foreground">Your photo (included in report):</p>
              <div className="h-16 w-16 overflow-hidden rounded-lg border border-border">
                <img src={imagePreview} alt="" className="h-full w-full object-cover" />
              </div>
            </div>
          )}

          {/* Corrected name field */}
          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">I think this is actually:</label>
            <input
              value={correctedName}
              onChange={(e) => setCorrectedName(e.target.value)}
              placeholder="e.g. Quartz crystal"
              className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
            />
            <p className="mt-1 text-xs text-muted-foreground">
              If you know what this is, tell us here! We review all submissions and may add it to the database for everyone.
            </p>
          </div>

          {/* Notes field */}
          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">Additional notes (optional)</label>
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="Any extra context about your find..."
              rows={3}
              className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
            />
          </div>

          {/* Buttons */}
          {isSubmitting ? (
            <div className="flex justify-center py-2">
              <Loader2 className="h-6 w-6 animate-spin text-primary" />
            </div>
          ) : (
            <div className="space-y-2">
              <SculptedButton
                accent="citrine"
                size="md"
                className="w-full gap-2"
                onClick={handleSubmit}
              >
                <Flag className="h-4 w-4" />
                Submit Report
              </SculptedButton>
              <SculptedButton
                accent="aqua"
                size="md"
                className="w-full"
                onClick={onDismiss}
              >
                Cancel
              </SculptedButton>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
