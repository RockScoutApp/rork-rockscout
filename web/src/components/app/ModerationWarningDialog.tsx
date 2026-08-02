import { Shield, X, AlertTriangle } from "lucide-react";
import { SculptedButton } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";

interface ModerationWarningDialogProps {
  open: boolean;
  onDismiss: () => void;
  reason?: string;
  warningType?: "post" | "image" | "message" | "general";
}

/**
 * Moderation warning dialog — shows a warning when user content
 * has been flagged by the moderation system.
 */
export default function ModerationWarningDialog({
  open,
  onDismiss,
  reason = "Your content was flagged by our moderation system for review.",
  warningType = "general",
}: ModerationWarningDialogProps) {
  if (!open) return null;

  const titles: Record<string, string> = {
    post: "Post Flagged for Review",
    image: "Image Flagged for Review",
    message: "Message Flagged for Review",
    general: "Content Flagged for Review",
  };

  return (
    <div
      className="fixed inset-0 z-[100] flex items-center justify-center bg-black/70 p-4 backdrop-blur-sm"
      onClick={onDismiss}
    >
      <div
        className="dark-card sculpted-raised w-full max-w-md rounded-2xl p-6"
        style={{ ["--sculpted-accent" as string]: "4 70% 55%" }}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Icon */}
        <div className="mb-4 flex justify-center">
          <div className="flex h-16 w-16 items-center justify-center rounded-full bg-red-500/15 ring-2 ring-red-500/30">
            <AlertTriangle className="h-8 w-8 text-red-500" />
          </div>
        </div>

        {/* Title */}
        <h3 className="text-center font-display text-lg font-bold text-foreground">
          {titles[warningType]}
        </h3>

        {/* Reason */}
        <p className="mt-3 text-center text-sm text-[hsl(var(--text-mid))]">
          {reason}
        </p>

        {/* Community guidelines link */}
        <div className="mt-4 rounded-xl bg-muted/30 p-3 text-center">
          <p className="text-xs text-muted-foreground">
            Please review our{" "}
            <a href="/community-guidelines" className="font-semibold underline" style={{ color: `hsl(${AQUA_HEX})` }}>
              Community Guidelines
            </a>{" "}
            before posting again.
          </p>
        </div>

        {/* Actions */}
        <div className="mt-5 flex flex-col gap-2">
          <SculptedButton accent="citrine" size="sm" className="w-full" onClick={onDismiss}>
            I Understand
          </SculptedButton>
        </div>

        {/* Close button */}
        <button
          onClick={onDismiss}
          className="absolute right-4 top-4 text-muted-foreground hover:text-foreground"
        >
          <X className="h-5 w-5" />
        </button>
      </div>
    </div>
  );
}
