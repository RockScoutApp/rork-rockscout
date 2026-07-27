import { useState } from "react";
import { Mail, MessageSquare, Send, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";

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
      // Open the user's email client with a pre-filled message
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
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Contact Us
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Questions, feedback, or need help? We're here.
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="rounded-xl border border-border bg-card p-4">
          <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
            <Mail className="h-4 w-4 text-primary" />
            Email
          </div>
          <p className="mt-2 text-sm text-muted-foreground">
            support@rockscout.app
          </p>
        </div>
        <div className="rounded-xl border border-border bg-card p-4">
          <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
            <MessageSquare className="h-4 w-4 text-primary" />
            Response Time
          </div>
          <p className="mt-2 text-sm text-muted-foreground">
            Usually within 24–48 hours
          </p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4 rounded-xl border border-border bg-card p-5">
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
        <Button type="submit" disabled={sending} className="gap-2">
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
        </Button>
      </form>
    </div>
  );
}
