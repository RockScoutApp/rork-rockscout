import { useState, useRef, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import {
  Camera,
  Upload,
  Loader2,
  Sparkles,
  AlertCircle,
  CheckCircle2,
  ArrowRight,
  X,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";

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

const BACKEND_URL = import.meta.env.EXPO_PUBLIC_RORK_FUNCTIONS_URL as string;
const APP_KEY = import.meta.env.EXPO_PUBLIC_RORK_APP_KEY as string;

export default function Identify() {
  const navigate = useNavigate();
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

  const stopCamera = useCallback(() => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((t) => t.stop());
      streamRef.current = null;
    }
    setCameraActive(false);
  }, []);

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
          entitlement: "free",
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
  }, [imageBase64, mimeType]);

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
          Snap a photo or upload an image — AI will identify your specimen from
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
                  Take or upload a photo
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
            <div className="rounded-lg border border-border bg-card p-6 text-center text-muted-foreground">
              No matches found. Try a clearer photo or different angle.
            </div>
          ) : (
            <>
              <div className="rounded-lg border border-border bg-card p-4">
                <div className="mb-2 flex items-center gap-2">
                  <CheckCircle2 className="h-4 w-4 text-emerald-400" />
                  <span className="text-sm font-medium text-foreground">
                    Top Match
                  </span>
                  {result.modelsUsed && result.modelsUsed.length > 0 && (
                    <span className="ml-auto text-xs text-muted-foreground">
                      via {result.modelsUsed.join(" + ")}
                    </span>
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
                <Button
                  onClick={() =>
                    navigate(`/app/specimens/${result.matches[0].id}`)
                  }
                  variant="outline"
                  size="sm"
                  className="mt-3 gap-2"
                >
                  View specimen details
                  <ArrowRight className="h-4 w-4" />
                </Button>
              </div>

              <p className="text-sm text-muted-foreground">
                {result.summary}
              </p>

              {result.matches.length > 1 && (
                <div>
                  <h4 className="mb-2 text-sm font-medium text-foreground">
                    Other Possibilities
                  </h4>
                  <div className="space-y-2">
                    {result.matches.slice(1).map((match) => (
                      <button
                        key={match.id}
                        onClick={() => navigate(`/app/specimens/${match.id}`)}
                        className="flex w-full items-center justify-between rounded-lg border border-border bg-card p-3 text-left transition-colors hover:border-primary/40"
                      >
                        <div className="min-w-0 flex-1">
                          <p className="truncate text-sm font-medium text-foreground">
                            {match.name}
                          </p>
                          <p className="truncate text-xs text-muted-foreground">
                            {match.reasoning}
                          </p>
                        </div>
                        <span
                          className={cn(
                            "ml-3 shrink-0 text-sm font-semibold",
                            confidenceColor(match.confidence),
                          )}
                        >
                          {match.confidence}%
                        </span>
                      </button>
                    ))}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
}
