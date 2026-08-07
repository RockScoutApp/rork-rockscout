/**
 * Reply Email Dialog — web equivalent of Android's ReplyEmailDialog.
 *
 * Lets the user enter/edit their reply-to email and optionally attach
 * additional photos before launching a pre-filled mailto: draft to one
 * or more museums.
 *
 * The original identification photo (if any) is included as the first
 * attachment. On web, we use mailto: with the email body pre-filled —
 * browsers can't attach files programmatically, so we note the photo
 * count and instruct the user to attach them manually in their email
 * client.
 */

import { useState, useEffect, useCallback, useRef } from "react";
import {
  Mail,
  X,
  Plus,
  Loader2,
  AlertCircle,
  Image as ImageIcon,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { cn } from "@/lib/utils";
import { supabase } from "@/lib/supabase";
import type { Museum } from "./MuseumFinderSheet";

interface ReplyEmailDialogProps {
  museum: Museum | null;
  museums: Museum[];
  open: boolean;
  onDismiss: () => void;
  matchNames?: string[];
  matchConfidences?: number[];
  aiSummary?: string;
  capturedImage?: string | null;
}

interface ExtraPhoto {
  id: string;
  dataUrl: string;
  name: string;
  size: number;
}

const MAX_PHOTOS = 4;
const MAX_TOTAL_BYTES = 18 * 1024 * 1024; // 18 MB

export function ReplyEmailDialog({
  museum,
  museums,
  open,
  onDismiss,
  matchNames = [],
  matchConfidences = [],
  aiSummary = "",
  capturedImage = null,
}: ReplyEmailDialogProps) {
  const [replyEmail, setReplyEmail] = useState("");
  const [includeCapturedPhoto, setIncludeCapturedPhoto] = useState(true);
  const [extraPhotos, setExtraPhotos] = useState<ExtraPhoto[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loadingEmail, setLoadingEmail] = useState(true);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Load user email from Supabase session
  useEffect(() => {
    if (!open) return;
    setLoadingEmail(true);
    supabase.auth.getUser().then(({ data }) => {
      const email = data.user?.email ?? "";
      const saved = localStorage.getItem("rockscout_last_reply_email");
      setReplyEmail(saved ?? email);
      setLoadingEmail(false);
    });
  }, [open]);

  // Reset state on open
  useEffect(() => {
    if (open) {
      setIncludeCapturedPhoto(true);
      setExtraPhotos([]);
      setError(null);
    }
  }, [open]);

  const capturedCount = includeCapturedPhoto && capturedImage ? 1 : 0;
  const totalPhotos = capturedCount + extraPhotos.length;
  const canAddMore = totalPhotos < MAX_PHOTOS;

  const extraBytes = extraPhotos.reduce((sum, p) => sum + p.size, 0);
  // Estimate captured photo at ~2 MB (it's a data URL but typically a JPEG)
  const capturedEstimate = includeCapturedPhoto && capturedImage ? 2_000_000 : 0;
  const totalBytes = capturedEstimate + extraBytes;

  const handleAddPhoto = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (!file) return;
      if (!canAddMore) return;

      if (file.size > 5 * 1024 * 1024) {
        setError("That image is over 5 MB. Please choose a smaller photo.");
        e.target.value = "";
        return;
      }

      const reader = new FileReader();
      reader.onload = () => {
        const dataUrl = reader.result as string;
        setExtraPhotos((prev) => [
          ...prev,
          {
            id: `photo_${Date.now()}_${Math.random().toString(36).slice(2)}`,
            dataUrl,
            name: file.name,
            size: file.size,
          },
        ]);
        setError(null);
      };
      reader.onerror = () => {
        setError("Could not load that image. Please try another.");
      };
      reader.readAsDataURL(file);
      e.target.value = "";
    },
    [canAddMore],
  );

  const removeExtraPhoto = useCallback((id: string) => {
    setExtraPhotos((prev) => prev.filter((p) => p.id !== id));
  }, []);

  const handleContinue = useCallback(() => {
    if (!replyEmail.trim()) {
      setError("Please enter your reply-to email address.");
      return;
    }
    if (totalBytes > MAX_TOTAL_BYTES) {
      setError(
        `Total attachment size exceeds the 18 MB email limit. Remove a photo and try again.`,
      );
      return;
    }

    localStorage.setItem("rockscout_last_reply_email", replyEmail.trim());

    const allMuseums = museums.length > 0 ? museums : museum ? [museum] : [];
    const recipientEmails = allMuseums
      .map((m) => m.email)
      .filter((e): e is string => !!e && e.trim().length > 0);
    const skippedCount = allMuseums.length - recipientEmails.length;

    const subject = "RockScout — Identification Assistance";
    const photoCount = totalPhotos;

    const bodyLines: string[] = [
      "Hello,",
      "",
      "I used RockScout's AI identification tool and the result was uncertain. I'm hoping you can help confirm what I've found.",
      "",
    ];

    if (matchNames.length > 0) {
      bodyLines.push("Top AI match(es):");
      matchNames.forEach((name, i) => {
        const conf = matchConfidences[i];
        bodyLines.push(
          `  • ${name}${conf != null ? ` — ${conf}% confidence` : ""}`,
        );
      });
      bodyLines.push("");
    }

    if (aiSummary.trim()) {
      bodyLines.push("AI analysis summary:");
      bodyLines.push(aiSummary.trim());
      bodyLines.push("");
    }

    if (photoCount > 1) {
      bodyLines.push(
        `I have ${photoCount} photos for your reference — different angles and detail shots of the find. Please see the attached images.`,
      );
    } else if (photoCount === 1) {
      bodyLines.push(
        "I have a photo for your reference. Please see the attached image.",
      );
    } else {
      bodyLines.push(
        "I can provide more details and photos if needed.",
      );
    }
    bodyLines.push("");
    bodyLines.push(`You can reply to me at: ${replyEmail.trim()}`);
    bodyLines.push("");
    bodyLines.push("Thank you for your time and expertise.");
    bodyLines.push("");
    bodyLines.push("— Sent from the RockScout app");

    const body = bodyLines.join("\n");

    // Build mailto: link
    const to = recipientEmails.length > 0 ? recipientEmails.join(",") : "";
    const mailtoUrl = `mailto:${to}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;

    if (skippedCount > 0) {
      // If some museums lack emails, open mailto for the ones that have it
      // and inform the user about the skipped ones
    }

    window.location.href = mailtoUrl;
    onDismiss();
  }, [
    replyEmail,
    totalBytes,
    totalPhotos,
    museums,
    museum,
    matchNames,
    matchConfidences,
    aiSummary,
    onDismiss,
  ]);

  if (!museum) return null;

  const allMuseums = museums.length > 0 ? museums : [museum];
  const recipientEmails = allMuseums
    .map((m) => m.email)
    .filter((e): e is string => !!e && e.trim().length > 0);
  const skippedCount = allMuseums.length - recipientEmails.length;

  return (
    <Dialog open={open} onOpenChange={(v) => !v && onDismiss()}>
      <DialogContent className="max-h-[85vh] max-w-md overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Mail className="h-5 w-5 text-primary" />
            Reply Email
          </DialogTitle>
          <DialogDescription>
            So the museum can reply to you. You can change this to any email
            address.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 pt-2">
          {/* Reply-to email */}
          <div className="space-y-2">
            <Input
              type="email"
              value={replyEmail}
              onChange={(e) => setReplyEmail(e.target.value)}
              placeholder="your.email@example.com"
              disabled={loadingEmail}
              aria-label="Your reply-to email"
            />
            {loadingEmail && (
              <p className="flex items-center gap-1.5 text-xs text-muted-foreground">
                <Loader2 className="h-3 w-3 animate-spin" />
                Loading your email...
              </p>
            )}
          </div>

          {/* Recipients */}
          <div className="space-y-1.5">
            <p className="text-xs font-medium text-muted-foreground">
              Recipients ({recipientEmails.length})
            </p>
            {allMuseums.map((m, i) => (
              <div
                key={`${m.id}-${i}`}
                className="flex items-center gap-2 text-sm"
              >
                <Mail className="h-3.5 w-3.5 shrink-0 text-muted-foreground" />
                <span className="truncate text-foreground">{m.name}</span>
                {m.email ? (
                  <span className="ml-auto shrink-0 text-xs text-muted-foreground">
                    {m.email}
                  </span>
                ) : (
                  <span className="ml-auto shrink-0 text-xs text-warning">
                    no email
                  </span>
                )}
              </div>
            ))}
            {skippedCount > 0 && (
              <p className="flex items-center gap-1.5 text-xs text-warning">
                <AlertCircle className="h-3 w-3" />
                {skippedCount} museum{skippedCount > 1 ? "s" : ""} without
                public email will be skipped.
              </p>
            )}
          </div>

          {/* Photo attachments */}
          <div className="space-y-2">
            <p className="text-sm font-semibold text-foreground">
              Photos to attach ({totalPhotos}/{MAX_PHOTOS})
            </p>
            <p className="text-xs text-muted-foreground">
              The original ID photo is included. Add more angles or detail
              shots — museums often need multiple views.
            </p>

            <div className="flex flex-wrap items-center gap-2">
              {/* Captured photo thumbnail */}
              {includeCapturedPhoto && capturedImage && (
                <div className="relative h-14 w-14 overflow-hidden rounded-lg border border-border">
                  <img
                    src={capturedImage}
                    alt="Original ID photo"
                    className="h-full w-full object-cover"
                  />
                  <button
                    onClick={() => setIncludeCapturedPhoto(false)}
                    className="absolute right-0 top-0 rounded-bl-md bg-black/80 p-0.5 text-white"
                    aria-label="Remove original photo"
                  >
                    <X className="h-3 w-3" />
                  </button>
                </div>
              )}

              {/* Extra photos */}
              {extraPhotos.map((photo) => (
                <div
                  key={photo.id}
                  className="relative h-14 w-14 overflow-hidden rounded-lg border border-border"
                >
                  <img
                    src={photo.dataUrl}
                    alt={photo.name}
                    className="h-full w-full object-cover"
                  />
                  <button
                    onClick={() => removeExtraPhoto(photo.id)}
                    className="absolute right-0 top-0 rounded-bl-md bg-black/80 p-0.5 text-white"
                    aria-label={`Remove ${photo.name}`}
                  >
                    <X className="h-3 w-3" />
                  </button>
                </div>
              ))}

              {/* Add photo button */}
              {canAddMore && (
                <button
                  onClick={() => fileInputRef.current?.click()}
                  className="flex h-14 w-14 items-center justify-center rounded-lg border-2 border-dashed border-sky-400/40 bg-sky-400/10 text-sky-400 transition-colors hover:bg-sky-400/20"
                  aria-label="Add photo"
                >
                  <Plus className="h-5 w-5" />
                </button>
              )}

              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleAddPhoto}
                className="hidden"
              />
            </div>

            {/* Size indicator */}
            {totalPhotos > 0 && (
              <p
                className={cn(
                  "text-xs",
                  totalBytes > MAX_TOTAL_BYTES
                    ? "text-destructive"
                    : "text-muted-foreground",
                )}
              >
                Total size: ~{(totalBytes / (1024 * 1024)).toFixed(1)} MB / 18
                MB
              </p>
            )}

            <p className="flex items-start gap-1.5 text-xs text-muted-foreground">
              <ImageIcon className="mt-0.5 h-3 w-3 shrink-0" />
              Note: Your email client will open with the message pre-filled.
              Please attach the photo(s) manually in your email app, as web
              browsers can't attach files to email drafts automatically.
            </p>
          </div>

          {error && (
            <p className="flex items-start gap-2 text-sm text-destructive">
              <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
              {error}
            </p>
          )}

          {/* Actions */}
          <div className="flex gap-3 pt-2">
            <Button onClick={handleContinue} className="flex-1 gap-2">
              <Mail className="h-4 w-4" />
              Continue
            </Button>
            <Button variant="outline" onClick={onDismiss}>
              Cancel
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
