import { Component, type ReactNode } from "react";
import { useLocation } from "react-router-dom";
import { reportError } from "@/lib/errorReporter";

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
  /** pathname when the error was captured — used to reset on navigation. */
  errorPath: string | null;
}

/**
 * Per-route error boundary.
 *
 * Wraps the `<Outlet />` inside `AppLayout` so a render crash on a single
 * page (e.g. a missing import, undefined access, bad Supabase response)
 * shows a recoverable error card with a "Go Home" button instead of a
 * blank white screen that forces the user to reload the entire PWA.
 *
 * The boundary automatically resets when the route changes, so navigating
 * to a different page clears the error state.
 */
export class RouteErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false, error: null, errorPath: null };

  static getDerivedStateFromError(error: Error): Partial<State> {
    return { hasError: true, error, errorPath: window.location.pathname };
  }

  componentDidCatch(error: Error, info: React.ErrorInfo) {
    console.error("Route render crash:", error, info.componentStack);
    void reportError(
      window.location.pathname,
      error,
      true,
      false,
    );
  }

  /**
   * Reset error state when the route changes so the user can navigate
   * to other pages without being stuck on the error screen.
   */
  componentDidUpdate(_prevProps: Props, prevState: State) {
    if (
      prevState.hasError &&
      window.location.pathname !== prevState.errorPath
    ) {
      this.setState({ hasError: false, error: null, errorPath: null });
    }
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex min-h-[60vh] flex-col items-center justify-center px-6 text-center">
          <div
            className="sculpted-raised dark-card max-w-md rounded-2xl p-6"
            style={{ ["--sculpted-accent" as string]: "4 70% 55%" }}
          >
            <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-red-500/15 ring-1 ring-red-500/30">
              <svg
                className="h-7 w-7 text-red-500"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                strokeWidth={2}
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M12 9v2m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>
            <h2 className="font-display text-lg font-bold text-foreground">
              This page hit an error
            </h2>
            <p className="mt-2 text-sm text-muted-foreground">
              Try going back or returning to Home. If the problem persists,
              clearing your browser cache should fix it.
            </p>
            <div className="mt-5 flex flex-col gap-2 sm:flex-row sm:justify-center">
              <button
                onClick={() => {
                  this.setState({ hasError: false, error: null, errorPath: null });
                  window.location.href = "/app";
                }}
                className="rounded-full border border-border bg-card/60 px-5 py-2.5 text-sm font-semibold text-muted-foreground transition-colors hover:bg-muted/50 hover:text-foreground"
              >
                Go back
              </button>
              <a
                href="/app"
                className="rounded-full bg-primary px-5 py-2.5 text-sm font-semibold text-primary-foreground transition-colors hover:bg-primary/90"
              >
                Go to Home
              </a>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
