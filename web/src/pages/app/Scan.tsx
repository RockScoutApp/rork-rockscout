import { QrCode, Camera } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";

export default function Scan() {
  const { user } = useAuth();

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <QrCode className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to use the scanner</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          QR Scanner
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Scan a RockScout QR code to open a shared spot
        </p>
      </div>

      <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
        <Camera className="h-8 w-8 text-muted-foreground" />
        <p className="max-w-sm text-sm text-muted-foreground">
          QR scanning requires camera access. Point your camera at a RockScout QR code
          to automatically open the shared location. This feature works best on mobile
          devices.
        </p>
      </div>
    </div>
  );
}
