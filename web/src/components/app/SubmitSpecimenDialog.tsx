import { useState } from "react";
import { X, Upload, Loader2, Image as ImageIcon } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedButton } from "@/components/sculpted";

interface SubmitSpecimenDialogProps {
  open: boolean;
  onDismiss: () => void;
}

/**
 * Submit a specimen for community review.
 */
export default function SubmitSpecimenDialog({ open, onDismiss }: SubmitSpecimenDialogProps) {
  const { user } = useAuth();
  const [name, setName] = useState("");
  const [category, setCategory] = useState("Igneous");
  const [description, setDescription] = useState("");
  const [location, setLocation] = useState("");
  const [photos, setPhotos] = useState<string[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!open) return null;

  const CATEGORIES = ["Igneous", "Sedimentary", "Metamorphic", "Mineral", "Crystal", "Fossil"];

  const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files) return;
    Array.from(files).slice(0, 10 - photos.length).forEach((file) => {
      const reader = new FileReader();
      reader.onload = () => setPhotos((prev) => [...prev, reader.result as string]);
      reader.readAsDataURL(file);
    });
  };

  const handleSubmit = async () => {
    if (!user) {
      toast.error("Sign in to submit a specimen");
      return;
    }
    if (!name.trim()) {
      toast.error("Please enter a specimen name");
      return;
    }
    setIsSubmitting(true);
    try {
      const { error } = await supabase.from("rockscout_specimen_submissions").insert({
        submitter_id: user.id,
        name: name.trim(),
        category,
        description: description.trim(),
        location: location.trim(),
        photo_urls: photos,
        status: "pending",
      });
      if (error) throw error;
      toast.success("Specimen submitted for review!");
      onDismiss();
    } catch {
      toast.error("Failed to submit specimen");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-[90] flex flex-col bg-background">
      <div className="flex items-center justify-between border-b border-border px-4 py-3">
        <h2 className="font-display text-lg font-bold text-foreground">Submit Specimen</h2>
        <button onClick={() => !isSubmitting && onDismiss()} className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground hover:bg-muted/50">
          <X className="h-5 w-5" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto px-4 py-4">
        <div className="space-y-4">
          <p className="text-sm text-muted-foreground">
            Found a specimen that's not in the database? Submit it here for review.
          </p>

          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">Specimen name *</label>
            <input value={name} onChange={(e) => setName(e.target.value)} placeholder="e.g. Blue Tourmaline" className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none" />
          </div>

          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">Category</label>
            <select value={category} onChange={(e) => setCategory(e.target.value)} className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground focus:border-primary focus:outline-none">
              {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>

          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">Location found</label>
            <input value={location} onChange={(e) => setLocation(e.target.value)} placeholder="e.g. San Diego County, California" className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none" />
          </div>

          <div>
            <label className="mb-1 block text-xs font-bold uppercase text-muted-foreground">Description</label>
            <textarea value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Describe the specimen — color, size, crystal habit, interesting features..." rows={4} className="w-full rounded-xl border border-border bg-input px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none" />
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

      <div className="flex gap-3 border-t border-border px-4 py-3">
        <SculptedButton accent="citrine" size="sm" className="flex-1" onClick={() => !isSubmitting && onDismiss()}>Cancel</SculptedButton>
        <SculptedButton accent="citrine" size="sm" className="flex-[1.5]" disabled={isSubmitting || !name.trim()} onClick={handleSubmit}>
          {isSubmitting ? <><Loader2 className="h-4 w-4 animate-spin" /> Submitting…</> : <><Upload className="h-4 w-4" /> Submit Specimen</>}
        </SculptedButton>
      </div>
    </div>
  );
}
