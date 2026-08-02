import { useState, useRef, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import {
  QrCode,
  Camera,
  ScanLine,
  Upload,
  X,
  Loader2,
  CheckCircle,
  MapPin,
  User,
  ExternalLink,
} from "lucide-react";
import jsQR from "jsqr";
import { useAuth } from "@/hooks/useAuth";
import { SculptedCard, SculptedButton, ScreenScaffold } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const CYAN_HEX = "174 100% 45%";

interface ScanResult {
  type: "spot" | "profile" | "url";
  url: string;
  label: string;
}

function parseQRData(data: string): ScanResult | null {
  // RockScout shared spot URL: /app/shared-spot/:lat/:lng
  const spotMatch = data.match(/\/app\/shared-spot\/(-?[\d.]+)\/(-?[\d.]+)/);
  if (spotMatch) {
    return {
      type: "spot",
      url: data,
      label: `Shared Spot: ${spotMatch[1]}, ${spotMatch[2]}`,
    };
  }
  // RockScout profile URL: /app/profile/:id
  const profileMatch = data.match(/\/app\/profile\/([a-f0-9-]+)/);
  if (profileMatch) {
    return {
      type: "profile",
      url: data,
      label: "Hunter Profile",
    };
  }
  // Generic URL
  if (data.startsWith("http://") || data.startsWith("https://")) {
    return { type: "url", url: data, label: data };
  }
  return null;
}

export default function Scan() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const animationRef = useRef<number>(0);

  const [scanning, setScanning] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<ScanResult | null>(null);
  const [cameraReady, setCameraReady] = useState(false);

  const stopCamera = useCallback(() => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((t) => t.stop());
      streamRef.current = null;
    }
    if (animationRef.current) {
      cancelAnimationFrame(animationRef.current);
      animationRef.current = 0;
    }
    setScanning(false);
    setCameraReady(false);
  }, []);

  const handleResult = useCallback(
    (res: ScanResult) => {
      setResult(res);
      stopCamera();
    },
    [stopCamera],
  );

  const scanFrame = useCallback(() => {
    if (!videoRef.current || !canvasRef.current) return;
    const video = videoRef.current;
    const canvas = canvasRef.current;
    if (video.readyState !== video.HAVE_ENOUGH_DATA) return;

    const w = video.videoWidth;
    const h = video.videoHeight;
    canvas.width = w;
    canvas.height = h;
    const ctx = canvas.getContext("2d", { willReadFrequently: true });
    if (!ctx) return;
    ctx.drawImage(video, 0, 0, w, h);
    const imageData = ctx.getImageData(0, 0, w, h);
    const code = jsQR(imageData.data, w, h, { inversionAttempts: "dontInvert" });

    if (code) {
      const parsed = parseQRData(code.data);
      if (parsed) {
        handleResult(parsed);
        return;
      }
    }
    animationRef.current = requestAnimationFrame(scanFrame);
  }, [handleResult]);

  const startCamera = useCallback(async () => {
    setError(null);
    setResult(null);
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: "environment" },
      });
      streamRef.current = stream;
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        videoRef.current.play();
        setScanning(true);
        setCameraReady(true);
        animationRef.current = requestAnimationFrame(scanFrame);
      }
    } catch {
      setError("Camera access denied. Check your browser permissions or use the upload option below.");
    }
  }, [scanFrame]);

  useEffect(() => {
    return () => stopCamera();
  }, [stopCamera]);

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setError(null);
    setResult(null);

    const reader = new FileReader();
    reader.onload = (ev) => {
      const img = new Image();
      img.onload = () => {
        const canvas = document.createElement("canvas");
        canvas.width = img.width;
        canvas.height = img.height;
        const ctx = canvas.getContext("2d");
        if (!ctx) return;
        ctx.drawImage(img, 0, 0);
        const imageData = ctx.getImageData(0, 0, img.width, img.height);
        const code = jsQR(imageData.data, img.width, img.height);
        if (code) {
          const parsed = parseQRData(code.data);
          if (parsed) {
            handleResult(parsed);
          } else {
            setError(`QR code detected but not a valid RockScout code: ${code.data}`);
          }
        } else {
          setError("No QR code found in the uploaded image. Try a clearer photo.");
        }
      };
      img.src = ev.target?.result as string;
    };
    reader.readAsDataURL(file);
  };

  const navigateToResult = () => {
    if (!result) return;
    if (result.type === "url" && result.url.startsWith("http")) {
      window.open(result.url, "_blank");
    } else {
      const path = result.url.startsWith("/app") ? result.url : `/app${result.url}`;
      navigate(path);
    }
  };

  if (!user) {
    return (
      <ScreenScaffold title="QR Scanner" onBack={() => window.history.back()}>
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <QrCode className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to use the scanner</p>
        </div>
      </ScreenScaffold>
    );
  }

  return (
    <ScreenScaffold title="QR Scanner" onBack={() => { stopCamera(); window.history.back(); }}>
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Scan a RockScout QR code to open a shared spot, hunter profile, or link.
        </p>

        {/* Scanner viewport */}
        <SculptedCard accent="citrine" glowing className="overflow-hidden">
          <div className="relative aspect-square w-full overflow-hidden bg-black md:aspect-video">
            {scanning ? (
              <>
                <video
                  ref={videoRef}
                  className="h-full w-full object-cover"
                  playsInline
                  muted
                />
                <canvas ref={canvasRef} className="hidden" />
                {/* Targeting overlay */}
                <div className="absolute inset-0 flex items-center justify-center">
                  <div
                    className="h-48 w-48 border-2 rounded-xl"
                    style={{ borderColor: `hsl(${CITRINE_HEX} / 0.8)` }}
                  >
                    <div
                      className="h-full w-full animate-pulse rounded-xl"
                      style={{
                        boxShadow: `inset 0 0 20px hsl(${CITRINE_HEX} / 0.3)`,
                      }}
                    />
                  </div>
                </div>
                {/* Scanning line */}
                <div
                  className="absolute left-1/4 right-1/4 h-0.5 animate-bounce"
                  style={{
                    background: `hsl(${CITRINE_HEX})`,
                    boxShadow: `0 0 8px hsl(${CITRINE_HEX})`,
                  }}
                />
              </>
            ) : (
              <div className="flex h-full w-full flex-col items-center justify-center gap-3 p-6 text-center">
                {result ? (
                  <>
                    <CheckCircle className="h-12 w-12" style={{ color: `hsl(${CITRINE_HEX})` }} />
                    <p className="text-sm font-bold text-foreground">QR Code Found!</p>
                    <p className="text-xs text-muted-foreground">{result.label}</p>
                  </>
                ) : (
                  <>
                    <ScanLine className="h-12 w-12 text-muted-foreground" />
                    <p className="text-sm text-muted-foreground">
                      {error ?? "Tap start to open your camera"}
                    </p>
                  </>
                )}
              </div>
            )}
          </div>
        </SculptedCard>

        {/* Action buttons */}
        <div className="flex gap-3">
          {!scanning ? (
            <SculptedButton
              accent="citrine"
              glowing
              className="flex-1"
              onClick={startCamera}
            >
              <Camera className="h-4 w-4" />
              {result ? "Scan Again" : "Start Camera"}
            </SculptedButton>
          ) : (
            <SculptedButton
              accent="danger"
              className="flex-1"
              onClick={stopCamera}
            >
              <X className="h-4 w-4" />
              Stop
            </SculptedButton>
          )}
          <SculptedButton
            accent="aqua"
            className="flex-1"
            onClick={() => fileInputRef.current?.click()}
          >
            <Upload className="h-4 w-4" />
            Upload QR
          </SculptedButton>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            className="hidden"
            onChange={handleFileUpload}
          />
        </div>

        {/* Result card */}
        {result && (
          <SculptedCard accent="success" glowing className="p-4">
            <div className="flex items-center gap-3">
              <div
                className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
                style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
              >
                {result.type === "spot" ? <MapPin className="h-5 w-5" /> :
                 result.type === "profile" ? <User className="h-5 w-5" /> :
                 <ExternalLink className="h-5 w-5" />}
              </div>
              <div className="flex-1">
                <p className="text-sm font-bold text-foreground">{result.label}</p>
                <p className="text-xs text-muted-foreground">
                  {result.type === "spot" ? "Shared rockhounding location" :
                   result.type === "profile" ? "Hunter profile" : "External link"}
                </p>
              </div>
              <SculptedButton accent="citrine" size="sm" glowing onClick={navigateToResult}>
                Open
              </SculptedButton>
            </div>
          </SculptedCard>
        )}

        {/* Info card */}
        <SculptedCard accent="cyan" className="p-4">
          <div className="flex items-start gap-3">
            <QrCode className="h-5 w-5 shrink-0" style={{ color: `hsl(${CYAN_HEX})` }} />
            <div>
              <p className="text-xs font-semibold text-foreground">How it works</p>
              <p className="mt-1 text-xs text-muted-foreground">
                Point your camera at a RockScout QR code, or upload a photo of one.
                Shared spots, hunter profiles, and links are all supported. Your camera
                feed is processed locally — nothing is uploaded.
              </p>
            </div>
          </div>
        </SculptedCard>
      </div>
    </ScreenScaffold>
  );
}
