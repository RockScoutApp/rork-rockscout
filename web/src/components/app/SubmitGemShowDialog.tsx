import { useState } from "react";
import { X, Upload, Loader2, Calendar, MapPin, Image as ImageIcon } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedButton } from "@/components/sculpted";

interface SubmitGemShowDialogProps {
  open: boolean;
  onDismiss: () => void;
}

/**
 * Submit a gem show for community review.
 */
export default function SubmitGemShowDialog({ open, onDismiss }: SubmitGemShowDialogProps) {
  const { user } = useAuth();
  const [name, setName] = useState("");
  const [city, setCity] = useState("");
  const [state, setState] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [website, setWebsite] = useState("");
  const [description, setDescription] = useState("");
  const [photos, setPhotos] = useState<string[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!open) return null;

  const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files) return;
    Array.from(files).slice(0, 5 - photos.length).forEach((file) => {
      const reader = new FileReader();
      reader.onload = () => setPhotos((prev) => [...prev, reader.result as string]);
      reader.readAsDataURL(file);
    });
  };

  const handleSubmit = async () => {
    if (!user) {
      toast.error("Sign in to submit a gem show");
      return;
    }
    if (!name.trim() || !city.trim()) {
      toast.error("Please enter a show name and city");
      return;
    }
    setIsSubmitting(true);
    try {
      const { error } = await supabase.from("rockscout_gem_show_submissions").insert({
        submitter_id: user.id,
        name: name.trim(),
        city: city.trim(),
        state: state.trim(),
        start_date: startDate || null,
        end_date: endDate || null,
        website: website.trim() || null,
        description: description.trim(),
        photo_urls: photos,
        status: "pending",
      });
      if (error) throw error;
      toast.success("Gem show submitted for review!");
      onDismiss();
    } catch {
      toast.error("Failed to submit gem show");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-[90] flex flex-col bg-background">
      <div className="flex items-center justify-between border-b border-border px-4 py-3">
        <h2 className="font-display text-lg font-bold text-foreground">Submit Gem Show</h2>
        <button onClick={() => !isSubmitting && onDismiss()} className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground hover:bg-muted/50">
          <X className="h-5 w-5" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto px-4 py-4">
        <div className="space-y-4">
          <p className="text-sm text-muted-foreground">
            Know about a gem, mineral, or fossil show? Submit it here for the community.
          </p>

          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">Show name *</label>
            <input value={name} onChange={(e) => setName(e.target.value)} placeholder="e.g. Tucson Gem & Mineral Show" className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none" />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">City *</label>
              <input value={city} onChange={(e) => setCity(e.target.value)} placeholder="Tucson" className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none" />
            </div>
            <div>
              <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">State</label>
              <input value={state} onChange={(e) => setState(e.target.value)} placeholder="AZ" className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">Start date</label>
              <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground focus:border-primary focus:outline-none" />
            </div>
            <div>
              <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">End date</label>
              <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground focus:border-primary focus:outline-none" />
            </div>
          </div>

          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">Website</label>
            <input value={website} onChange={(e) => setWebsite(e.target.value)} placeholder="https://..." className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none" />
          </div>

          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">Description</label>
            <textarea value={description} onChange={(e) => setDescription(e.target.value)} placeholder="What can visitors expect? Vendors, exhibits, activities..." rows={3} className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none" />
          </div>

          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">Photos (up to 5)</label>
            <div className="flex flex-wrap gap-2">
              {photos.map((photo, i) => (
                <div key={i} className="relative h-20 w-20 overflow-hidden rounded-lg">
                  <img src={photo} alt="" className="h-full w-full object-cover" />
                  <button onClick={() => setPhotos((prev) => prev.filter((_, idx) => idx !== i))} className="absolute right-0 top-0 flex h-5 w-5 items-center justify-center rounded-bl-lg bg-black/60 text-white">
                    <X className="h-3 w-3" />
                  </button>
                </div>
              ))}
              {photos.length < 5 && (
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

      <div className="flex gap-3 border-t border-border px-4 py-3">
        <SculptedButton accent="citrine" size="sm" className="flex-1" onClick={() => !isSubmitting && onDismiss()}>Cancel</SculptedButton>
        <SculptedButton accent="citrine" size="sm" className="flex-[1.5]" disabled={isSubmitting || !name.trim() || !city.trim()} onClick={handleSubmit}>
          {isSubmitting ? <><Loader2 className="h-4 w-4 animate-spin" /> Submitting…</> : <><Upload className="h-4 w-4" /> Submit Show</>}
        </SculptedButton>
      </div>
    </div>
  );
}
