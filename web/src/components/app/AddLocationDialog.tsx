import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { X, MapPin, Upload, Loader2, CheckCircle, Image as ImageIcon } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedButton, TagChip } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";

const LOCATION_TYPES = [
  "Public Dig Site",
  "Mine",
  "Quarry",
  "Beach / Shore",
  "River / Creek",
  "Desert / Field",
  "Rock Shop",
  "Metaphysical Shop",
  "Lapidary Club",
];

interface AddLocationDialogProps {
  open: boolean;
  onDismiss: () => void;
  submissionMode?: string;
}

/**
 * Full-screen location submission form with photo upload, type dropdown,
 * and coordinate entry. Submits to Supabase for community review.
 */
export default function AddLocationDialog({
  open,
  onDismiss,
  submissionMode = "dig_site",
}: AddLocationDialogProps) {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [type, setType] = useState(LOCATION_TYPES[0]);
  const [address, setAddress] = useState("");
  const [comments, setComments] = useState("");
  const [latitude, setLatitude] = useState("");
  const [longitude, setLongitude] = useState("");
  const [photos, setPhotos] = useState<string[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!open) return null;

  const dialogTitle =
    submissionMode === "campground" ? "Upload New Campground" :
    submissionMode === "trailhead" ? "Upload New Trailhead" :
    submissionMode === "park" ? "Upload New Park" :
    "Upload New Location";

  const dialogSubtitle =
    submissionMode === "campground" ? "Upload a campground for the community trip planner." :
    submissionMode === "trailhead" ? "Upload a trailhead or access point for the community trip planner." :
    submissionMode === "park" ? "Add a state or national park for the community to explore." :
    "Upload a dig site, mine, quarry, or rock shop for the community.";

  const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files) return;
    Array.from(files).slice(0, 10 - photos.length).forEach((file) => {
      const reader = new FileReader();
      reader.onload = () => {
        setPhotos((prev) => [...prev, reader.result as string]);
      };
      reader.readAsDataURL(file);
    });
  };

  const handleSubmit = async () => {
    if (!user) {
      toast.error("Sign in to submit a location");
      navigate("/app/signin");
      return;
    }
    if (!name.trim()) {
      toast.error("Please enter a location name");
      return;
    }
    const lat = parseFloat(latitude);
    const lng = parseFloat(longitude);
    if (isNaN(lat) || isNaN(lng) || (lat === 0 && lng === 0)) {
      toast.error("Please enter valid coordinates");
      return;
    }

    setIsSubmitting(true);
    try {
      const { error } = await supabase.from("rockscout_location_submissions").insert({
        submitter_id: user.id,
        submitter_name: "RockScout Hunter",
        name: name.trim(),
        type,
        address: address.trim(),
        comments: comments.trim(),
        latitude: lat,
        longitude: lng,
        photo_urls: photos,
        status: "pending",
        location_category: submissionMode,
      });
      if (error) throw error;
      toast.success("Location submitted for review!");
      onDismiss();
    } catch (err) {
      console.error("Location submission error:", err);
      toast.error("Failed to submit location. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-[90] flex flex-col bg-background">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-border px-4 py-3">
        <h2 className="font-display text-lg font-bold text-foreground">{dialogTitle}</h2>
        <button
          onClick={() => !isSubmitting && onDismiss()}
          className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground hover:bg-muted/50"
        >
          <X className="h-5 w-5" />
        </button>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-y-auto px-4 py-4">
        <div className="space-y-4">
          <p className="text-sm text-muted-foreground">{dialogSubtitle}</p>

          {/* Name */}
          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">
              Location name *
            </label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="e.g. Crystal Peak Quartz Mine"
              className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
            />
          </div>

          {/* Type */}
          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">
              Location type
            </label>
            <select
              value={type}
              onChange={(e) => setType(e.target.value)}
              className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground focus:border-primary focus:outline-none"
            >
              {LOCATION_TYPES.map((t) => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </div>

          {/* Address */}
          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">
              Address or approximate location
            </label>
            <input
              type="text"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              placeholder="e.g. Near Quartzsite, Arizona"
              className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
            />
          </div>

          {/* Coordinates */}
          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">
              Coordinates (latitude, longitude)
            </label>
            <div className="flex gap-2">
              <input
                type="number"
                step="any"
                value={latitude}
                onChange={(e) => setLatitude(e.target.value)}
                placeholder="34.2150"
                className="w-1/2 rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
              />
              <input
                type="number"
                step="any"
                value={longitude}
                onChange={(e) => setLongitude(e.target.value)}
                placeholder="-114.4680"
                className="w-1/2 rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
              />
            </div>
            <p className="mt-1 text-xs text-muted-foreground">
              <MapPin className="mr-1 inline h-3 w-3" />
              Find coordinates from Google Maps by right-clicking a location.
            </p>
          </div>

          {/* Comments */}
          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">
              Comments / additional info
            </label>
            <textarea
              value={comments}
              onChange={(e) => setComments(e.target.value)}
              placeholder="What can people find here? Any tips for visitors?"
              rows={3}
              className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
            />
          </div>

          {/* Photos */}
          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">
              Photos (up to 10)
            </label>
            <div className="flex flex-wrap gap-2">
              {photos.map((photo, i) => (
                <div key={i} className="relative h-20 w-20 overflow-hidden rounded-lg">
                  <img src={photo} alt="" className="h-full w-full object-cover" onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }} />
                  <button
                    onClick={() => setPhotos((prev) => prev.filter((_, idx) => idx !== i))}
                    className="absolute right-0 top-0 flex h-5 w-5 items-center justify-center rounded-bl-lg bg-black/60 text-white"
                  >
                    <X className="h-3 w-3" />
                  </button>
                </div>
              ))}
              {photos.length < 10 && (
                <label className="flex h-20 w-20 cursor-pointer flex-col items-center justify-center rounded-lg border border-dashed border-primary/40 bg-muted/20">
                  <ImageIcon className="h-6 w-6 text-muted-foreground" />
                  <span className="mt-1 text-[10px] text-muted-foreground">Add</span>
                  <input type="file" accept="image/*" multiple className="hidden" onChange={handlePhotoUpload} />
                </label>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Footer actions */}
      <div className="flex gap-3 border-t border-border px-4 py-3">
        <SculptedButton
          accent="citrine"
          size="sm"
          className="flex-1"
          onClick={() => !isSubmitting && onDismiss()}
        >
          Cancel
        </SculptedButton>
        <SculptedButton
          accent="citrine"
          size="sm"
          className="flex-[1.5]"
          disabled={isSubmitting || !name.trim()}
          onClick={handleSubmit}
        >
          {isSubmitting ? (
            <>
              <Loader2 className="h-4 w-4 animate-spin" />
              Uploading…
            </>
          ) : (
            <>
              <Upload className="h-4 w-4" />
              Upload Location
            </>
          )}
        </SculptedButton>
      </div>
    </div>
  );
}
