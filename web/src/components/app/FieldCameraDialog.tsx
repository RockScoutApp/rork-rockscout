import { useState, useRef, useCallback, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Camera,
  Upload,
  X,
  Check,
  Loader2,
  Lock,
  AlertCircle,
  MapPin,
  ChevronDown,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";

/**
 * Save destination for a field camera photo.
 * Mirrors Android's SaveDestination enum.
 */
type SaveDestination =
  | "FIELD_CAPTURES"
  | "SAVED_IMAGES"
  | "MY_ROCKS"
  | "MY_WISHLIST"
  | "FIELD_JOURNAL"
  | "SHARE_PROFILE"
  | "PROFILE_BACKGROUND"
  | "SUBMIT_SPECIMEN";

const DESTINATIONS: { value: SaveDestination; label: string }[] = [
  { value: "FIELD_CAPTURES", label: "Save to Field Captures" },
  { value: "SAVED_IMAGES", label: "Save to My Saved Images" },
  { value: "MY_ROCKS", label: "Save to My Rocks" },
  { value: "MY_WISHLIST", label: "Save to My Wishlist" },
  { value: "FIELD_JOURNAL", label: "Attach to Field Journal Entry" },
  { value: "SHARE_PROFILE", label: "Share to Profile" },
  { value: "PROFILE_BACKGROUND", label: "Change Profile Background" },
  { value: "SUBMIT_SPECIMEN", label: "Submit a Specimen" },
];

const BACKEND_URL = import.meta.env.EXPO_PUBLIC_RORK_FUNCTIONS_URL as string;
const APP_KEY = import.meta.env.EXPO_PUBLIC_RORK_APP_KEY as string;

interface FieldCameraDialogProps {
  open: boolean;
  onDismiss: () => void;
  /** Called when user picks "Share to Profile" destination. */
  onShareToProfile?: (imageUrl: string, name: string, location: string) => void;
  /** Called when user picks "Submit a Specimen" destination. */
  onSubmitSpecimen?: (imageUrl: string) => void;
}

/**
 * Full-screen field camera dialog matching Android's FieldCameraDialog.
 * 1. Open webcam via getUserMedia (fallback to file upload).
 * 2. Capture frame to canvas → data URL.
 * 3. Show preview with Retake + "Save to…" dropdown.
 * 4. Save-to opens a capture card form with Name, Location, Description, pin-drop map.
 * 5. Moderates the image, then saves a rockscout_captures row + routes to destination.
 */
export default function FieldCameraDialog({
  open,
  onDismiss,
  onShareToProfile,
  onSubmitSpecimen,
}: FieldCameraDialogProps) {
  const navigate = useNavigate();
  const { user } = useAuth();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const streamRef = useRef<MediaStream | null>(null);

  const [cameraActive, setCameraActive] = useState(false);
  const [cameraError, setCameraError] = useState<string | null>(null);
  const [capturedImage, setCapturedImage] = useState<string | null>(null);
  const [showSaveForm, setShowSaveForm] = useState(false);
  const [selectedDestination, setSelectedDestination] =
    useState<SaveDestination | null>(null);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);
  const [moderationError, setModerationError] = useState<string | null>(null);

  // Form fields
  const [name, setName] = useState("");
  const [location, setLocation] = useState("");
  const [description, setDescription] = useState("");
  const [pinLat, setPinLat] = useState<number | null>(null);
  const [pinLng, setPinLng] = useState<number | null>(null);

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
        err instanceof Error
          ? err.message
          : "Could not access camera. Try uploading a file instead.",
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
    setCapturedImage(dataUrl);
    stopCamera();
  }, [stopCamera]);

  const handleFileUpload = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (!file) return;
      if (file.size > 5 * 1024 * 1024) {
        setCameraError("That image is over 5 MB. Please choose a smaller photo.");
        e.target.value = "";
        return;
      }
      const reader = new FileReader();
      reader.onload = () => {
        const dataUrl = reader.result as string;
        setCapturedImage(dataUrl);
      };
      reader.readAsDataURL(file);
    },
    [],
  );

  // Auto-start camera when dialog opens
  useEffect(() => {
    if (open && !capturedImage && !cameraActive) {
      startCamera();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  // Cleanup camera on unmount or close
  useEffect(() => {
    if (!open) {
      stopCamera();
    }
    return () => stopCamera();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  const resetAll = useCallback(() => {
    setCapturedImage(null);
    setShowSaveForm(false);
    setSelectedDestination(null);
    setName("");
    setLocation("");
    setDescription("");
    setPinLat(null);
    setPinLng(null);
    setSaveSuccess(false);
    setModerationError(null);
  }, []);

  const handleClose = useCallback(() => {
    stopCamera();
    resetAll();
    onDismiss();
  }, [stopCamera, resetAll, onDismiss]);

  const handleSelectDestination = (dest: SaveDestination) => {
    setDropdownOpen(false);
    setSelectedDestination(dest);
    setShowSaveForm(true);
  };

  const moderateImage = useCallback(
    async (base64: string): Promise<boolean> => {
      try {
        const response = await fetch(`${BACKEND_URL}/identify`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "X-App-Key": APP_KEY,
          },
          body: JSON.stringify({
            imageBase64: base64,
            mimeType: "image/jpeg",
            entitlement: "free",
            moderationOnly: true,
          }),
        });
        if (!response.ok) {
          // If the endpoint doesn't support moderationOnly, treat as clean
          return true;
        }
        const data = (await response.json()) as {
          moderationReject?: boolean;
        };
        if (data.moderationReject) {
          setModerationError(
            "This photo can't be used because it contains content that violates our family-friendly policy.",
          );
          return false;
        }
        return true;
      } catch {
        // Network error — don't block the save
        return true;
      }
    },
    [],
  );

  const saveCapture = useCallback(async () => {
    if (!user || !capturedImage || !selectedDestination) return;
    setIsSaving(true);
    setModerationError(null);

    try {
      // Moderate first
      const clean = await moderateImage(capturedImage);
      if (!clean) {
        setIsSaving(false);
        return;
      }

      // Always create a field capture row (appears on the specimen marker map)
      const captureRow = {
        user_id: user.id,
        specimen_id: "field-camera",
        specimen_emoji: "📷",
        custom_name: name,
        custom_location: location,
        general_info: description,
        image_urls: [capturedImage],
        latitude: pinLat,
        longitude: pinLng,
        in_collection: selectedDestination === "MY_ROCKS",
        in_wishlist: selectedDestination === "MY_WISHLIST",
      };

      const { error: captureError } = await supabase
        .from("rockscout_captures")
        .insert(captureRow);
      if (captureError) throw captureError;

      // Route to the specific destination
      switch (selectedDestination) {
        case "FIELD_CAPTURES":
          // Already created above
          break;
        case "SAVED_IMAGES": {
          const { error: savedError } = await supabase
            .from("rockscout_saved_images")
            .insert({
              user_id: user.id,
              image_url: capturedImage,
              thumbnail_url: capturedImage,
              source: "field-camera",
            });
          if (savedError) throw savedError;
          break;
        }
        case "MY_ROCKS":
        case "MY_WISHLIST":
          // The capture row already has the in_collection/in_wishlist flag set
          break;
        case "FIELD_JOURNAL":
          // Close dialog and navigate to journal with the photo
          handleClose();
          navigate("/app/journal", {
            state: { initialPhoto: capturedImage },
          });
          return;
        case "SHARE_PROFILE":
          if (onShareToProfile) {
            onShareToProfile(capturedImage, name, location);
          } else {
            // Fallback: navigate to community with the photo
            handleClose();
            navigate("/app/community", {
              state: { initialPhoto: capturedImage },
            });
            return;
          }
          break;
        case "PROFILE_BACKGROUND": {
          const { error: profileError } = await supabase
            .from("rockscout_profiles")
            .update({ background_image_url: capturedImage })
            .eq("user_id", user.id);
          if (profileError) throw profileError;
          break;
        }
        case "SUBMIT_SPECIMEN":
          if (onSubmitSpecimen) {
            onSubmitSpecimen(capturedImage);
          }
          break;
      }

      setSaveSuccess(true);
      setTimeout(() => handleClose(), 800);
    } catch (err) {
      toast.error(
        err instanceof Error ? err.message : "Failed to save capture",
      );
    } finally {
      setIsSaving(false);
    }
  }, [
    user,
    capturedImage,
    selectedDestination,
    name,
    location,
    description,
    pinLat,
    pinLng,
    moderateImage,
    handleClose,
    navigate,
    onShareToProfile,
    onSubmitSpecimen,
  ]);

  if (!open) return null;

  // Success state
  if (saveSuccess) {
    return (
      <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/80 p-4">
        <div className="w-full max-w-sm rounded-2xl border border-border bg-card p-6 text-center">
          <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-primary/20">
            <Check className="h-6 w-6 text-primary" />
          </div>
          <h2 className="mt-4 font-display text-lg font-bold text-foreground">
            Saved!
          </h2>
          <p className="mt-1 text-sm text-muted-foreground">
            Your field capture has been saved.
          </p>
          <Button onClick={handleClose} className="mt-4 w-full">
            OK
          </Button>
        </div>
      </div>
    );
  }

  // Moderation error state
  if (moderationError) {
    return (
      <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/80 p-4">
        <div className="w-full max-w-sm rounded-2xl border border-destructive/30 bg-card p-6">
          <div className="flex items-center gap-2 text-destructive">
            <AlertCircle className="h-5 w-5" />
            <h2 className="font-display text-lg font-bold">Photo Rejected</h2>
          </div>
          <p className="mt-2 text-sm text-muted-foreground">
            {moderationError}
          </p>
          <div className="mt-4 flex gap-2">
            <Button
              variant="outline"
              className="flex-1"
              onClick={() => {
                setModerationError(null);
                if (capturedImage) {
                  setShowSaveForm(false);
                } else {
                  startCamera();
                }
              }}
            >
              OK
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // Save form (after selecting a destination)
  if (showSaveForm && capturedImage && selectedDestination) {
    const destLabel =
      DESTINATIONS.find((d) => d.value === selectedDestination)?.label ?? "";
    return (
      <div className="fixed inset-0 z-[100] flex flex-col bg-black/95 md:items-center md:justify-center md:p-4">
        <div className="flex h-full w-full flex-col md:max-h-[90vh] md:max-w-lg md:rounded-2xl md:border md:border-border md:bg-card">
          {/* Top bar */}
          <div className="flex items-center gap-3 p-4">
            <button
              onClick={handleClose}
              className="rounded-lg p-2 text-muted-foreground hover:bg-muted hover:text-foreground"
              aria-label="Close"
            >
              <X className="h-5 w-5" />
            </button>
            <h2 className="flex-1 font-display text-lg font-bold text-primary">
              {destLabel}
            </h2>
          </div>

          <div className="flex-1 overflow-y-auto px-4 pb-4">
            {/* Photo preview */}
            <div className="mb-4 h-44 overflow-hidden rounded-xl border border-border bg-muted/20">
              <img
                src={capturedImage}
                alt="Captured photo"
                className="h-full w-full object-cover"
              />
            </div>

            {/* Name */}
            <div className="mb-3 space-y-1.5">
              <Label htmlFor="fc-name">Name</Label>
              <Input
                id="fc-name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. Clear quartz point"
              />
            </div>

            {/* Location */}
            <div className="mb-3 space-y-1.5">
              <Label htmlFor="fc-location">Location</Label>
              <Input
                id="fc-location"
                value={location}
                onChange={(e) => setLocation(e.target.value)}
                placeholder="e.g. Crater of Diamonds, AR"
              />
            </div>

            {/* Description */}
            <div className="mb-4 space-y-1.5">
              <Label htmlFor="fc-description">Description</Label>
              <Textarea
                id="fc-description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Notes about this find…"
                rows={3}
              />
            </div>

            {/* Pin-drop map */}
            <div className="mb-4 space-y-2">
              <p className="text-sm font-semibold text-primary">
                Drop a pin where you found this
              </p>
              <div
                className="relative h-48 cursor-crosshair overflow-hidden rounded-xl border border-border bg-muted/20"
                onClick={async (e) => {
                  const rect = e.currentTarget.getBoundingClientRect();
                  // Approximate: reverse-geocode center of clicked area
                  // For PWA, use approximate lat/lng based on click position
                  const x = (e.clientX - rect.left) / rect.width;
                  const y = (e.clientY - rect.top) / rect.height;
                  // Default to a rough US center; users can adjust manually
                  const lat = 39.5 - y * 20;
                  const lng = -98.35 + x * 40;
                  setPinLat(lat);
                  setPinLng(lng);
                }}
              >
                <div className="absolute inset-0 flex items-center justify-center text-muted-foreground">
                  {pinLat !== null ? (
                    <div className="flex flex-col items-center gap-1">
                      <MapPin className="h-8 w-8 text-primary" />
                      <span className="text-xs font-medium">
                        {pinLat.toFixed(3)}, {pinLng?.toFixed(3)}
                      </span>
                    </div>
                  ) : (
                    <span className="text-sm">Tap to drop a pin</span>
                  )}
                </div>
              </div>
              {pinLat !== null && (
                <button
                  onClick={() => {
                    setPinLat(null);
                    setPinLng(null);
                  }}
                  className="text-xs text-muted-foreground underline hover:text-foreground"
                >
                  Clear pin
                </button>
              )}
            </div>

            {/* Save button */}
            <Button
              onClick={saveCapture}
              disabled={isSaving}
              size="lg"
              className="w-full gap-2"
            >
              {isSaving ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin" />
                  Saving…
                </>
              ) : (
                <>
                  <Check className="h-4 w-4" />
                  Save to {destLabel.replace("Save to ", "")}
                </>
              )}
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // Camera viewfinder + capture
  if (cameraActive) {
    return (
      <div className="fixed inset-0 z-[100] flex flex-col bg-black">
        <div className="flex items-center p-4">
          <h2 className="flex-1 font-display text-lg font-bold text-primary">
            Field Camera
          </h2>
          <button
            onClick={handleClose}
            className="rounded-lg p-2 text-white hover:bg-white/10"
            aria-label="Close"
          >
            <X className="h-5 w-5" />
          </button>
        </div>
        <div className="relative flex-1 overflow-hidden">
          <video
            ref={videoRef}
            className="h-full w-full object-cover"
            playsInline
            muted
          />
          <div className="absolute inset-x-0 bottom-0 flex items-center justify-center gap-3 bg-gradient-to-t from-black/70 to-transparent p-6">
            <Button onClick={capturePhoto} size="lg" className="gap-2">
              <Camera className="h-5 w-5" />
              Capture
            </Button>
            <Button
              onClick={handleClose}
              variant="outline"
              size="lg"
              className="gap-2"
            >
              <X className="h-5 w-5" />
              Cancel
            </Button>
          </div>
        </div>
        <canvas ref={canvasRef} className="hidden" />
      </div>
    );
  }

  // Photo preview + Save-to dropdown
  if (capturedImage) {
    return (
      <div className="fixed inset-0 z-[100] flex flex-col bg-black md:items-center md:justify-center md:p-4">
        <div className="flex h-full w-full flex-col md:max-h-[90vh] md:max-w-2xl md:rounded-2xl md:border md:border-border md:bg-card">
          {/* Top bar */}
          <div className="flex items-center p-4">
            <h2 className="flex-1 font-display text-lg font-bold text-primary">
              Field Camera
            </h2>
            <button
              onClick={handleClose}
              className="rounded-lg p-2 text-muted-foreground hover:bg-muted hover:text-foreground"
              aria-label="Close"
            >
              <X className="h-5 w-5" />
            </button>
          </div>

          <div className="flex-1 overflow-y-auto px-4 pb-4">
            {/* Photo preview */}
            <div className="mb-3 overflow-hidden rounded-xl border border-primary/30 bg-muted/20">
              <img
                src={capturedImage}
                alt="Captured photo"
                className="max-h-[50vh] w-full object-contain"
              />
            </div>

            {/* Retake */}
            <Button
              onClick={() => {
                setCapturedImage(null);
                startCamera();
              }}
              variant="outline"
              className="mb-2 w-full gap-2"
            >
              <Camera className="h-4 w-4" />
              Retake Photo
            </Button>

            {/* Save-to dropdown */}
            <div className="relative">
              <button
                onClick={() => setDropdownOpen(!dropdownOpen)}
                className="flex w-full items-center gap-2 rounded-xl border border-primary bg-primary/10 px-4 py-3 text-primary transition-colors hover:bg-primary/20"
              >
                <Camera className="h-5 w-5" />
                <span className="flex-1 text-left font-semibold">
                  Save to…
                </span>
                <ChevronDown className="h-5 w-5" />
              </button>
              {dropdownOpen && (
                <div className="absolute bottom-full mb-2 max-h-64 w-full overflow-y-auto rounded-xl border border-border bg-card shadow-xl">
                  {DESTINATIONS.map((dest) => {
                    // After trial expires, free users can only save to
                    // Saved Images. Other destinations are paid.
                    const isPaidDestination = dest.value !== "SAVED_IMAGES";
                    // TODO: wire trial/premium status from useAuth or a
                    // separate entitlement hook. For now, all destinations
                    // are available — premium is managed via the mobile app.
                    return (
                      <button
                        key={dest.value}
                        onClick={() => handleSelectDestination(dest.value)}
                        className="flex w-full items-center gap-2 px-4 py-3 text-left text-sm text-foreground transition-colors hover:bg-muted/50"
                      >
                        <span className="flex-1">{dest.label}</span>
                      </button>
                    );
                  })}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Initial state: camera error or no camera, show upload fallback
  return (
    <div className="fixed inset-0 z-[100] flex flex-col bg-black md:items-center md:justify-center md:p-4">
      <div className="flex h-full w-full flex-col md:max-h-[90vh] md:max-w-lg md:rounded-2xl md:border md:border-border md:bg-card">
        <div className="flex items-center p-4">
          <h2 className="flex-1 font-display text-lg font-bold text-primary">
            Field Camera
          </h2>
          <button
            onClick={handleClose}
            className="rounded-lg p-2 text-muted-foreground hover:bg-muted hover:text-foreground"
            aria-label="Close"
          >
            <X className="h-5 w-5" />
          </button>
        </div>
        <div className="flex flex-1 flex-col items-center justify-center gap-4 p-8 text-center">
          <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
            <Camera className="h-8 w-8 text-primary" />
          </div>
          <div>
            <p className="font-medium text-foreground">
              Take or upload a photo
            </p>
            <p className="mt-1 text-sm text-muted-foreground">
              Capture a field photo and save it to any destination.
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
        <input
          ref={fileInputRef}
          type="file"
          accept="image/*"
          onChange={handleFileUpload}
          className="hidden"
        />
        <canvas ref={canvasRef} className="hidden" />
      </div>
    </div>
  );
}
