import { useState } from "react";
import { Mail, MessageSquare, Send, Loader2 } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

export default function ContactUs() {
  const { user } = useAuth();
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");
  const [sending, setSending] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!subject.trim() || !message.trim()) {
      toast.error("Please fill in the subject and message");
      return;
    }
    setSending(true);
    try {
      const body = encodeURIComponent(
        `${message}\n\n---\nFrom: ${user?.email ?? "Unknown"}\nAccount ID: ${user?.id ?? "N/A"}`,
      );
      const subjectEnc = encodeURIComponent(`RockScout Support: ${subject}`);
      window.location.href = `mailto:support@rockscout.app?subject=${subjectEnc}&body=${body}`;
      toast.success("Opening your email app...");
    } catch {
      toast.error("Could not open email app. Email support@rockscout.app directly.");
    } finally {
      setSending(false);
    }
  };

  return (
    <ScreenScaffold title="Contact Us">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Questions, feedback, or need help? We're here.
        </p>

        <div className="grid gap-4 sm:grid-cols-2">
          <SculptedCard accent="citrine" className="p-4">
            <div className="flex items-center gap-3">
              <div
                className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
                style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
              >
                <Mail className="h-5 w-5" />
              </div>
              <div>
                <p className="text-sm font-bold text-foreground">Email</p>
                <p className="text-sm text-[hsl(var(--text-mid))]">support@rockscout.app</p>
              </div>
            </div>
          </SculptedCard>
          <SculptedCard accent="aqua" className="p-4">
            <div className="flex items-center gap-3">
              <div
                className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
                style={{ ["--badge-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
              >
                <MessageSquare className="h-5 w-5" />
              </div>
              <div>
                <p className="text-sm font-bold text-foreground">Response Time</p>
                <p className="text-sm text-[hsl(var(--text-mid))]">Usually within 24–48 hours</p>
              </div>
            </div>
          </SculptedCard>
        </div>

        <SculptedCard accent="citrine" className="space-y-4 p-5">
          <div className="space-y-1.5">
            <Label htmlFor="subject">Subject</Label>
            <Input
              id="subject"
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
              placeholder="What do you need help with?"
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="message">Message</Label>
            <Textarea
              id="message"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Describe your issue or feedback..."
              rows={6}
            />
          </div>
          <SculptedButton
            accent="citrine"
            glowing
            className="w-full"
            disabled={sending}
            onClick={(e) => handleSubmit(e as unknown as React.FormEvent)}
          >
            {sending ? (
              <>
                <Loader2 className="h-4 w-4 animate-spin" />
                Opening email...
              </>
            ) : (
              <>
                <Send className="h-4 w-4" />
                Send Message
              </>
            )}
          </SculptedButton>
        </SculptedCard>
      </div>
    </ScreenScaffold>
  );
}
