import { Component, type ReactNode } from "react";

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
            onClick={() => {
              // Clear any stale service worker caches before reloading.
              if ("caches" in window) {
                caches.keys().then((keys) =>
                  Promise.all(keys.map((k) => caches.delete(k))),
                );
              }
              if ("serviceWorker" in navigator) {
                navigator.serviceWorker
                  .getRegistrations()
                  .then((regs) =>
                    Promise.all(regs.map((r) => r.unregister())),
                  )
                  .finally(() => window.location.reload());
              } else {
                window.location.reload();
              }
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
