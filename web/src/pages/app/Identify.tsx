import { useState, useRef, useCallback, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { useTier } from "@/hooks/useTier";
import {
  Camera,
  Upload,
  Loader2,
  Sparkles,
  AlertCircle,
  CheckCircle2,
  ArrowRight,
  X,
  ScanLine,
  Maximize2,
  Mail,
  Landmark as MuseumIcon,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import { supabase } from "@/lib/supabase";
import { FUNCTIONS_URL as BACKEND_URL, APP_KEY } from "@/lib/config";
import { MuseumFinderSheet, type Museum } from "@/components/app/MuseumFinderSheet";
import { ReplyEmailDialog } from "@/components/app/ReplyEmailDialog";

interface Match {
  id: string;
  name: string;
  confidence: number;
  reasoning: string;
}

interface IdentifyResponse {
  matches: Match[];
  summary: string;
  needsClarification: boolean;
  modelsUsed?: string[];
  visualReferenceUsed?: boolean;
  error?: string;
}

interface SpecimenRef {
  id: string;
  name: string;
  image_url: string;
  image_urls?: string[];
}

export default function Identify() {
  const navigate = useNavigate();
  const { isPremium } = useTier();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const streamRef = useRef<MediaStream | null>(null);

  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [imageBase64, setImageBase64] = useState<string | null>(null);
  const [mimeType, setMimeType] = useState<string>("image/jpeg");
  const [isIdentifying, setIsIdentifying] = useState(false);
  const [result, setResult] = useState<IdentifyResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [cameraActive, setCameraActive] = useState(false);
  const [cameraError, setCameraError] = useState<string | null>(null);
  const [compareOpen, setCompareOpen] = useState(false);
  const [lightboxUrl, setLightboxUrl] = useState<string | null>(null);
  const [showMuseumFinder, setShowMuseumFinder] = useState(false);
  const [emailTargetMuseum, setEmailTargetMuseum] = useState<Museum | null>(null);
  const [emailTargetMuseums, setEmailTargetMuseums] = useState<Museum[]>([]);
  const [showReplyEmail, setShowReplyEmail] = useState(false);

  const stopCamera = useCallback(() => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((t) => t.stop());
      streamRef.current = null;
    }
    setCameraActive(false);
  }, []);

  // Clean up camera stream on unmount so the camera LED doesn't stay on.
  useEffect(() => () => stopCamera(), [stopCamera]);

  const startCamera = useCallback(async () => {
    setCameraError(null);
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: "environment" },
        audio: false,
      });
      streamRef.current = stream;
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        await videoRef.current.play();
      }
      setCameraActive(true);
    } catch (err) {
      setCameraError(
        err instanceof Error ? err.message : "Could not access camera",
      );
    }
  }, []);

  const capturePhoto = useCallback(() => {
    if (!videoRef.current || !canvasRef.current) return;
    const video = videoRef.current;
    const canvas = canvasRef.current;
    canvas.width = video.videoWidth || 1080;
    canvas.height = video.videoHeight || 1080;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
    const dataUrl = canvas.toDataURL("image/jpeg", 0.85);
    setImagePreview(dataUrl);
    setImageBase64(dataUrl);
    setMimeType("image/jpeg");
    stopCamera();
    setResult(null);
    setError(null);
  }, [stopCamera]);

  const handleFileUpload = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (!file) return;
      if (file.size > 5 * 1024 * 1024) {
        setError(
          "That image is over 5 MB. Please choose a smaller photo.",
        );
        e.target.value = "";
        return;
      }
      const reader = new FileReader();
      reader.onload = () => {
        const dataUrl = reader.result as string;
        setImagePreview(dataUrl);
        setImageBase64(dataUrl);
        setMimeType(file.type || "image/jpeg");
        setResult(null);
        setError(null);
      };
      reader.readAsDataURL(file);
    },
    [],
  );

  const topMatchIds = result?.matches.slice(0, 5).map((m) => m.id) ?? [];

  const { data: specimenRefs, isLoading: refsLoading } = useQuery<SpecimenRef[]>({
    queryKey: ["identify-specimen-refs", topMatchIds.join(",")],
    queryFn: async () => {
      if (topMatchIds.length === 0) return [];
      const { data, error } = await supabase
        .from("specimen_catalog")
        .select("id, name, image_url, image_urls")
        .in("id", topMatchIds);
      if (error) throw error;
      const rows = (data ?? []) as SpecimenRef[];
      const byId = new Map(rows.map((r) => [r.id, r]));
      return topMatchIds.map((id) => byId.get(id)).filter((r): r is SpecimenRef => r != null);
    },
    enabled: topMatchIds.length > 0,
  });

  const handleIdentify = useCallback(async () => {
    if (!imageBase64) return;
    setIsIdentifying(true);
    setError(null);
    setResult(null);
    try {
      const response = await fetch(`${BACKEND_URL}/identify`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-App-Key": APP_KEY,
        },
        body: JSON.stringify({
          imageBase64,
          mimeType,
          entitlement: isPremium ? "premium" : "free",
        }),
      });
      if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        throw new Error(body.error || `Identification failed (${response.status})`);
      }
      const data = (await response.json()) as IdentifyResponse;
      setResult(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Identification failed");
    } finally {
      setIsIdentifying(false);
    }
  }, [imageBase64, mimeType, isPremium]);

  const resetImage = useCallback(() => {
    setImagePreview(null);
    setImageBase64(null);
    setResult(null);
    setError(null);
  }, []);

  const confidenceColor = (conf: number) => {
    if (conf >= 85) return "text-emerald-400";
    if (conf >= 60) return "text-primary";
    return "text-muted-foreground";
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Identify a Rock
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Snap photos or upload images — AI will identify your specimen from
          900+ known rocks, minerals, gems, and fossils.
        </p>
      </div>

      {/* Camera viewfinder */}
      {cameraActive && (
        <div className="relative overflow-hidden rounded-xl border border-border bg-black">
          <video
            ref={videoRef}
            className="h-auto w-full"
            playsInline
            muted
          />
          <div className="absolute inset-x-0 bottom-0 flex items-center justify-center gap-3 bg-gradient-to-t from-black/70 to-transparent p-4">
            <Button onClick={capturePhoto} size="lg" className="gap-2">
              <Camera className="h-5 w-5" />
              Capture
            </Button>
            <Button
              onClick={stopCamera}
              variant="outline"
              size="lg"
              className="gap-2"
            >
              <X className="h-5 w-5" />
              Cancel
            </Button>
          </div>
        </div>
      )}

      {/* Image preview or upload zone */}
      {!cameraActive && (
        <div className="space-y-4">
          {imagePreview ? (
            <div className="relative overflow-hidden rounded-xl border border-border">
              <img
                src={imagePreview}
                alt="Specimen to identify"
                className="max-h-[400px] w-full object-contain bg-black/30"
              />
              <button
                onClick={resetImage}
                className="absolute right-3 top-3 rounded-full bg-black/60 p-2 text-white backdrop-blur transition-colors hover:bg-black/80"
                aria-label="Remove image"
              >
                <X className="h-5 w-5" />
              </button>
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center gap-4 rounded-xl border-2 border-dashed border-border bg-card/30 p-8 text-center">
              <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
                <Sparkles className="h-8 w-8 text-primary" />
              </div>
              <div>
                <p className="font-medium text-foreground">
                  Take or upload photos
                </p>
                <p className="mt-1 text-sm text-muted-foreground">
                  Get the best results in natural light, filling the frame with
                  your specimen.
                </p>
              </div>
              <div className="flex flex-wrap items-center justify-center gap-3">
                <Button onClick={startCamera} className="gap-2">
                  <Camera className="h-4 w-4" />
                  Take Photo
                </Button>
                <Button
                  onClick={() => fileInputRef.current?.click()}
                  variant="outline"
                  className="gap-2"
                >
                  <Upload className="h-4 w-4" />
                  Upload Image
                </Button>
              </div>
              {cameraError && (
                <p className="flex items-center gap-2 text-sm text-destructive">
                  <AlertCircle className="h-4 w-4" />
                  {cameraError}
                </p>
              )}
            </div>
          )}

          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            onChange={handleFileUpload}
            className="hidden"
          />
          <canvas ref={canvasRef} className="hidden" />
        </div>
      )}

      {/* Identify button */}
      {imageBase64 && !cameraActive && (
        <Button
          onClick={handleIdentify}
          disabled={isIdentifying}
          size="lg"
          className="w-full gap-2"
        >
          {isIdentifying ? (
            <>
              <Loader2 className="h-4 w-4 animate-spin" />
              Identifying...
            </>
          ) : (
            <>
              <Sparkles className="h-4 w-4" />
              Identify This Rock
            </>
          )}
        </Button>
      )}

      {/* Error */}
      {error && (
        <div className="flex items-start gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
          <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Results */}
      {result && (
        <div className="space-y-4">
          {result.matches.length === 0 ? (
            <div className="dark-card sculpted-raised rounded-lg p-6 text-center text-muted-foreground">
              No matches found. Try a clearer photo or different angle.
            </div>
          ) : (
            <>
              <div className="dark-card sculpted-raised rounded-lg p-4">
                <div className="mb-2 flex items-center gap-2">
                  <CheckCircle2 className="h-4 w-4 text-emerald-400" />
                  <span className="text-sm font-medium text-foreground">
                    Top Match
                  </span>
                  {result.modelsUsed && result.modelsUsed.length > 0 && (
                    <div className="ml-auto flex flex-wrap gap-1">
                      {result.modelsUsed.map((model: string) => {
                        const label = model.charAt(0).toUpperCase() + model.slice(1);
                        const color = model.toLowerCase().includes("haiku")
                          ? "border-orange-400/40 bg-orange-400/10 text-orange-300"
                          : model.toLowerCase().includes("sonnet")
                            ? "border-violet-400/40 bg-violet-400/10 text-violet-300"
                            : model.toLowerCase().includes("gemini")
                              ? "border-sky-400/40 bg-sky-400/10 text-sky-300"
                              : model.toLowerCase().includes("web")
                                ? "border-emerald-400/40 bg-emerald-400/10 text-emerald-300"
                                : model.toLowerCase().includes("database") || model.toLowerCase().includes("embedding")
                                  ? "border-amber-400/40 bg-amber-400/10 text-amber-300"
                                  : "border-muted-foreground/30 bg-muted/30 text-muted-foreground";
                        return (
                          <span
                            key={model}
                            className={cn(
                              "inline-flex items-center rounded-full border px-2 py-0.5 text-xs font-medium",
                              color,
                            )}
                          >
                            {label}
                          </span>
                        );
                      })}
                    </div>
                  )}
                </div>
                <h3 className="font-display text-xl font-bold text-foreground">
                  {result.matches[0].name}
                </h3>
                <div className="mt-1 flex items-center gap-2">
                  <div className="h-2 flex-1 overflow-hidden rounded-full bg-muted">
                    <div
                      className={cn(
                        "h-full rounded-full bg-primary transition-all",
                        confidenceColor(result.matches[0].confidence),
                      )}
                      style={{ width: `${result.matches[0].confidence}%` }}
                    />
                  </div>
                  <span
                    className={cn(
                      "text-sm font-semibold",
                      confidenceColor(result.matches[0].confidence),
                    )}
                  >
                    {result.matches[0].confidence}%
                  </span>
                </div>
                <p className="mt-2 text-sm text-muted-foreground">
                  {result.matches[0].reasoning}
                </p>
                <div className="mt-3 flex flex-wrap gap-2">
                  <Button
                    onClick={() => navigate(`/app/specimens/${result.matches[0].id}`)}
                    variant="outline"
                    size="sm"
                    className="gap-2"
                  >
                    View specimen details
                    <ArrowRight className="h-4 w-4" />
                  </Button>
                  <Button
                    onClick={() => setCompareOpen(true)}
                    size="sm"
                    className="gap-2"
                  >
                    <ScanLine className="h-4 w-4" />
                    Compare
                  </Button>
                </div>
              </div>

              <p className="text-sm text-muted-foreground">
                {result.summary}
              </p>

              {/* Agate uncertainty disclaimer */}
              {result.matches[0].name.toLowerCase().includes("agate") &&
                result.matches[0].confidence < 85 && (
                  <div className="dark-card sculpted-raised border-l-4 border-l-warning rounded-lg p-4">
                    <div className="mb-1 flex items-center gap-2">
                      <AlertCircle className="h-4 w-4 text-warning" />
                      <span className="text-sm font-semibold text-foreground">
                        Agate identification
                      </span>
                    </div>
                    <p className="text-sm text-muted-foreground">
                      Agates are among the hardest minerals to identify down to their specific
                      variety — many share similar banding patterns and colors. The database
                      images and your local gem & mineral resources can help you confirm the exact
                      type. Tap any match below to compare your specimen side-by-side with reference
                      photos.
                    </p>
                    <Button
                      onClick={() => setShowMuseumFinder(true)}
                      variant="default"
                      size="sm"
                      className="mt-3 w-full gap-2"
                    >
                      <AlertCircle className="h-4 w-4" />
                      Ask an Expert
                    </Button>
                  </div>
                )}

              {/* Ask an Expert — confident results card */}
              {result.matches[0].confidence >= 60 && !(
                result.matches[0].name.toLowerCase().includes("agate") &&
                result.matches[0].confidence < 85
              ) && (
                <div className="dark-card sculpted-raised rounded-lg p-4">
                  <div className="flex items-start gap-3">
                    <MuseumIcon className="mt-0.5 h-5 w-5 shrink-0 text-primary" />
                    <div>
                      <p className="text-sm font-semibold text-foreground">
                        Want to confirm with a museum?
                      </p>
                      <p className="mt-1 text-sm text-muted-foreground">
                        If you think you may have something rare or historically
                        significant, reach out to a nearby museum or cultural
                        center. They can help verify your find with expert eyes.
                      </p>
                      <Button
                        onClick={() => setShowMuseumFinder(true)}
                        variant="outline"
                        size="sm"
                        className="mt-3 gap-2"
                      >
                        <MuseumIcon className="h-4 w-4" />
                        Ask an Expert
                      </Button>
                    </div>
                  </div>
                </div>
              )}

              {result.matches.length > 1 && (
                <div>
                  <h4 className="mb-2 text-sm font-medium text-foreground">
                    Other Possibilities
                  </h4>
                  <div className="space-y-2">
                    {result.matches.slice(1).map((match) => (
                      <div
                        key={match.id}
                        className="flex w-full items-center justify-between dark-card sculpted-raised rounded-lg p-3"
                      >
                        <button
                          onClick={() => navigate(`/app/specimens/${match.id}`)}
                          className="min-w-0 flex-1 text-left transition-colors hover:text-primary"
                        >
                          <p className="truncate text-sm font-medium text-foreground">
                            {match.name}
                          </p>
                          <p className="truncate text-xs text-muted-foreground">
                            {match.reasoning}
                          </p>
                        </button>
                        <div className="ml-3 flex shrink-0 items-center gap-2">
                          <span
                            className={cn(
                              "text-sm font-semibold",
                              confidenceColor(match.confidence),
                            )}
                          >
                            {match.confidence}%
                          </span>
                          <Button
                            onClick={() => setCompareOpen(true)}
                            variant="ghost"
                            size="icon"
                            className="h-7 w-7"
                            aria-label={`Compare with ${match.name}`}
                          >
                            <ScanLine className="h-4 w-4" />
                          </Button>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {compareOpen && imagePreview && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/90 p-4">
                  <div className="relative flex max-h-[90vh] w-full max-w-2xl flex-col rounded-xl bg-card p-4 shadow-2xl">
                    <button
                      onClick={() => setCompareOpen(false)}
                      className="absolute right-3 top-3 rounded-full bg-black/60 p-2 text-white backdrop-blur transition-colors hover:bg-black/80"
                      aria-label="Close comparison"
                    >
                      <X className="h-5 w-5" />
                    </button>
                    <h3 className="mb-4 pr-10 text-lg font-semibold text-foreground">
                      Side-by-side comparison
                    </h3>
                    <div className="scrollbar-thin overflow-y-auto pr-1">
                      <div className="space-y-4">
                        {result.matches.slice(0, 5).map((match) => {
                          const refSpec = specimenRefs?.find((r) => r.id === match.id);
                          const refUrl = refSpec?.image_urls?.[0] ?? refSpec?.image_url;
                          return (
                            <div key={match.id} className="dark-card rounded-lg p-3">
                              <div className="grid grid-cols-2 gap-2">
                                <button
                                  onClick={() => setLightboxUrl(imagePreview)}
                                  className="relative aspect-square overflow-hidden rounded-lg bg-black/30"
                                >
                                  <img
                                    src={imagePreview}
                                    alt="Your photo"
                                    className="h-full w-full object-contain"
                                  />
                                  <div className="absolute right-2 top-2 rounded-full bg-black/50 p-1 text-white">
                                    <Maximize2 className="h-3 w-3" />
                                  </div>
                                  <span className="absolute bottom-2 left-2 rounded bg-black/60 px-1.5 py-0.5 text-[10px] text-white">
                                    Your photo
                                  </span>
                                </button>
                                <button
                                  onClick={() => refUrl && setLightboxUrl(refUrl)}
                                  className="relative aspect-square overflow-hidden rounded-lg bg-black/30"
                                >
                                  {refsLoading || !refUrl ? (
                                    <div className="flex h-full w-full items-center justify-center">
                                      <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
                                    </div>
                                  ) : (
                                    <>
                                      <img
                                        src={refUrl}
                                        alt={match.name}
                                        className="h-full w-full object-contain"
                                      />
                                      <div className="absolute right-2 top-2 rounded-full bg-black/50 p-1 text-white">
                                        <Maximize2 className="h-3 w-3" />
                                      </div>
                                      <span className="absolute bottom-2 left-2 rounded bg-black/60 px-1.5 py-0.5 text-[10px] text-white">
                                        Reference
                                      </span>
                                    </>
                                  )}
                                </button>
                              </div>
                              <div className="mt-2 flex items-center justify-between">
                                <div>
                                  <p className="text-sm font-medium text-foreground">{match.name}</p>
                                  <span className={cn("text-xs font-semibold", confidenceColor(match.confidence))}>
                                    {match.confidence}%
                                  </span>
                                </div>
                                <Button
                                  onClick={() => {
                                    setCompareOpen(false);
                                    navigate(`/app/specimens/${match.id}`);
                                  }}
                                  variant="outline"
                                  size="sm"
                                  className="gap-1"
                                >
                                  View details
                                  <ArrowRight className="h-3 w-3" />
                                </Button>
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {lightboxUrl && (
                <div
                  className="fixed inset-0 z-[60] flex items-center justify-center bg-black/95 p-4"
                  onClick={() => setLightboxUrl(null)}
                >
                  <button
                    onClick={() => setLightboxUrl(null)}
                    className="absolute right-4 top-4 rounded-full bg-black/60 p-2 text-white backdrop-blur transition-colors hover:bg-black/80"
                    aria-label="Close lightbox"
                  >
                    <X className="h-5 w-5" />
                  </button>
                  <img
                    src={lightboxUrl}
                    alt="Expanded view"
                    className="max-h-[90vh] max-w-full rounded-lg object-contain"
                  />
                </div>
              )}
            </>
          )}
        </div>
      )}

      {/* Ask an Expert — Museum Finder */}
      <MuseumFinderSheet
        open={showMuseumFinder}
        onDismiss={() => setShowMuseumFinder(false)}
        onEmailExpert={(museum) => {
          setEmailTargetMuseum(museum);
          setEmailTargetMuseums([museum]);
          setShowMuseumFinder(false);
          setShowReplyEmail(true);
        }}
        onEmailExperts={(museums) => {
          setEmailTargetMuseums(museums);
          setEmailTargetMuseum(museums[0] ?? null);
          setShowMuseumFinder(false);
          setShowReplyEmail(true);
        }}
        matchNames={result?.matches.map((m) => m.name) ?? []}
        matchConfidences={result?.matches.map((m) => m.confidence) ?? []}
        aiSummary={result?.summary ?? ""}
      />

      {/* Reply Email Dialog */}
      <ReplyEmailDialog
        museum={emailTargetMuseum}
        museums={emailTargetMuseums}
        open={showReplyEmail}
        onDismiss={() => {
          setShowReplyEmail(false);
          setEmailTargetMuseum(null);
        }}
        matchNames={result?.matches.map((m) => m.name) ?? []}
        matchConfidences={result?.matches.map((m) => m.confidence) ?? []}
        aiSummary={result?.summary ?? ""}
        capturedImage={imagePreview}
      />
    </div>
  );
}
