import { Component, type ReactNode } from "react";
import { reportError } from "@/lib/errorReporter";

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

/**
 * Top-level error boundary. Catches render crashes that would otherwise
 * produce a silent black screen (the app has a near-black body background).
 * Shows a plain-English error message with a reload button instead.
 */
export class ErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false, error: null };

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, info: React.ErrorInfo) {
    console.error("RockScout render crash:", error, info.componentStack);
    // Report to central service with the component stack as breadcrumb
    void reportError(
      window.location.pathname,
      error,
      true,  // isFatal
      false, // attemptSelfHeal
    );
  }

  render() {
    if (this.state.hasError) {
      return (
        <div
          style={{
            minHeight: "100vh",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            padding: "2rem",
            background: "hsl(30 23% 7%)",
            color: "hsl(36 40% 92%)",
            fontFamily: "Inter, ui-sans-serif, system-ui, sans-serif",
            textAlign: "center",
          }}
        >
          <div
            style={{
              fontSize: "1.5rem",
              fontWeight: 700,
              marginBottom: "0.5rem",
              fontFamily: "Fraunces, Georgia, serif",
            }}
          >
            Something went wrong
          </div>
          <p style={{ fontSize: "0.875rem", opacity: 0.7, maxWidth: "28rem" }}>
            The page hit an unexpected error. Try reloading — if it keeps
            happening, clearing your browser cache should fix it.
          </p>
          <button
            onClick={async () => {
              // Clear any stale service worker caches before reloading so the
              // next load fetches the latest build, not the cached broken one.
              try {
                if ("caches" in window) {
                  const keys = await caches.keys();
                  await Promise.all(keys.map((k) => caches.delete(k)));
                }
                if ("serviceWorker" in navigator) {
                  const regs = await navigator.serviceWorker.getRegistrations();
                  await Promise.all(regs.map((r) => r.unregister()));
                }
              } catch {
                // Ignore cleanup failures — a plain reload is still better than nothing.
              }
              window.location.reload();
            }}
            style={{
              marginTop: "1.5rem",
              padding: "0.625rem 1.5rem",
              borderRadius: "9999px",
              border: "1px solid hsl(36 80% 58%)",
              background: "hsl(36 80% 58% / 0.15)",
              color: "hsl(36 80% 58%)",
              fontSize: "0.875rem",
              fontWeight: 600,
              cursor: "pointer",
            }}
          >
            Reload page
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
