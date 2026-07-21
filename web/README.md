# RockScout.net — marketing + legal site

Static React (Vite + Tailwind + shadcn) site that deploys free to Cloudflare Pages
at **RockScout.net**. Marketing landing page plus Privacy, Terms, Community
Guidelines, Trade Disclaimer, Safety & Meetup, Cookies, Support, and Press pages.

## What's here

- `src/pages/Landing.tsx` — hero, features, screenshot strip, Play Store CTA, JSON-LD
- `src/pages/Support.tsx` — FAQ + legal links + contact
- `src/pages/Privacy.tsx` · `Terms.tsx` · `CommunityGuidelines.tsx` · `TradeDisclaimer.tsx` · `Safety.tsx` · `Cookies.tsx` — legal content
- `src/pages/Press.tsx` — press kit (icon, screenshots, boilerplate, facts)
- `src/pages/NotFound.tsx` — branded 404
- `src/content/legal.ts` — single source of truth for legal copy (mirrors the in-app disclaimer)
- `src/components/Layout.tsx` — navbar + footer + per-page SEO meta
- `src/components/LegalPage.tsx` — shared legal page shell
- `public/_redirects` — SPA rewrites so React Router routes work on direct visits
- `public/robots.txt` · `public/sitemap.xml` — SEO
- `public/images/screenshots/` — the 6 Play Store screenshots
- `public/images/og-share-card.png` — social share image
- `public/favicon.png` · `public/icon.png` — app icon

## Run locally

```bash
cd web
bun install
bun run dev      # http://localhost:8080
bun run build    # outputs static files to web/dist
bun run preview  # preview the production build
```

## Deploy to Cloudflare Pages (free, one-time setup, ~10 minutes)

### 1. Push this repo to GitHub (or GitLab/Bitbucket)
If it's not already on a git host, push it there. Cloudflare Pages connects to
your git repo and redeploys on every push.

### 2. Create the Pages project
1. Log into your Cloudflare dashboard → **Workers & Pages** → **Create** → **Pages** → **Connect to Git**.
2. Authorize Cloudflare to access your repo and pick this project's repository.
3. In **Set up builds and deployments**:
   - **Framework preset:** `Vite` (or none — it doesn't matter, we set the commands manually)
   - **Root directory:** `web` (the website files live inside the `web/` folder at the repo root)
   - **Build command:** `bun run build` (Cloudflare will fall back to `npm install` + `npm run build` if `bun` isn't detected — that's fine, the repo includes `web/.npmrc` with `legacy-peer-deps=true` and pins TypeScript 5.9.x so npm install resolves cleanly)
   - **Build output directory:** `dist`
   - **Environment variables:** add `NODE_VERSION` = `20` (or whatever Cloudflare's current default is — Vite needs Node 18+). Optionally `BUN_VERSION` = `1.1` to force bun.
4. Click **Save and Deploy**. First build runs and the site is live within ~60 seconds at `rockscout.pages.dev` (or `<project-name>.pages.dev`).

> **If a deploy fails on an old commit:** clicking **Retry deployment** re-runs the *same commit* — it does NOT pull new code. To build from the latest commit, either push any new commit to `main` (triggers the GitHub webhook → new build), or in Cloudflare go to **Deployments → ⋮ → Retry deployment** *only after* confirming the latest commit is checked out (the commit hash appears at the top of the build log).

### 3. Connect your custom domain — RockScout.net
You already registered RockScout.net through Cloudflare Registrar, so nameservers
already point to Cloudflare — no DNS delegation step is needed.

1. In your Cloudflare dashboard → **Workers & Pages** → click your new RockScout Pages project.
2. Go to **Custom domains** → **Set up a custom domain**.
3. Enter `rockscout.net` → **Continue** → **Activate domain**.
   Cloudflare auto-provisions the DNS record and a free TLS certificate.
4. Repeat for `www.rockscout.net`. (Optional — you can also set up a redirect from `www` to the root under **Rules → Redirect Rules** if you prefer a single canonical host.)
5. Wait 1–5 minutes for DNS to propagate on Cloudflare's network. Visit `https://rockscout.net` — the site is live.

### 4. (Optional) Redirect `www` → root
If you want `rockscout.net` as the only canonical host:
1. Cloudflare dashboard → your domain → **Rules** → **Redirect Rules** → **Create rule**.
2. When: `Hostname equals www.rockscout.net`.
3. Then: **Dynamic redirect** → URL `concat("https://rockscout.net", http.request.uri.path)` → Status `301`.

## Set up `support@rockscout.net` (free email forwarding, ~3 minutes)

Cloudflare Email Routing is included free with every Cloudflare account. It forwards
incoming mail at `support@rockscout.net` (or any `*@rockscout.net` address) to an
inbox you already have — your Yahoo, Gmail, anything — so you get a branded public
address without paying for hosted email.

1. Cloudflare dashboard → **RockScout.net** → **Email** → **Email Routing**.
2. Click **Get started**. Cloudflare auto-adds the required MX and SPF DNS records
   for you (one click, no manual DNS editing).
3. Go to **Destination addresses** → **Add destination address** → enter your
   personal inbox (e.g. `Rockscoutapp2026@yahoo.com`) → click **Send verification**.
   Cloudflare sends a verification email to that inbox; open it and click the link
   to confirm.
4. Go to **Routing rules** → **Create address** → choose either:
   - `support@rockscout.net` → forward to your verified Yahoo inbox (recommended —
     matches the address used in Privacy, Terms, Support, Community Guidelines,
     and the LICENSE), **or**
   - **Catch-all** `*@rockscout.net` → forward to your Yahoo inbox (covers
     `support@`, `legal@`, `press@`, etc. in one rule).
5. Send a test email to `support@rockscout.net` from any account — it should land
   in your Yahoo inbox within a minute.

**Cost:** $0. Cloudflare Email Routing is free on every plan, including the free
tier. No per-mailbox fee, no usage limits that matter for a support inbox.

**Replies:** Email Routing is forward-only — replying *from* `support@rockscout.net`
requires either (a) a free SMTP service like Resend/Brevo with a verified sending
domain, or (b) upgrading to Cloudflare's paid email routing later. For now,
replies can go out from your Yahoo inbox; the public-facing address still reads as
`support@rockscout.net` to anyone emailing you.

## Updating the site

- Edit the files in `web/src/` and push to your main branch. Cloudflare rebuilds
  and deploys automatically in ~60 seconds. No manual deploys.
- To change legal copy, edit `src/content/legal.ts` (the in-app disclaimer
  screen mirrors the same clauses, so update both if you want them in sync).
- To swap screenshots, replace files in `public/images/screenshots/`.
- To change the social share preview, replace `public/images/og-share-card.png`.

## Troubleshooting builds

**Symptom:** `npm error ERESOLVE ... typescript@7.0.2 ... peer typescript >=4.8.4 <6.1.0 from typescript-eslint`
Cause: an old commit pinned `typescript@^7.0.2`, which `typescript-eslint` can't peer with. Fixed in commit `609f34bb` (TypeScript pinned to `~5.9.2`) and `56f22e95` (`.npmrc` with `legacy-peer-deps=true`). If a build log still shows `typescript@7.0.2`, it is building a stale commit — push any new commit to `main` to trigger a fresh build from the latest code.

> **Do NOT click "Retry deployment"** on an old failed deployment — it re-runs the *same pinned commit* and never pulls fresh code. Instead, push any new commit to `main` on GitHub; Cloudflare's GitHub webhook fires automatically and builds from the new HEAD. To confirm which commit a build is running, read the `HEAD is now at <hash>` line at the top of the build log — if it is not your latest `main` hash, the build is stale.

**Symptom:** `Cannot find cwd: /opt/buildhome/repo/web`
Cause: the `web/` folder is missing at the repo root, or **Root directory** is set to a path that doesn't exist. Fix: make sure the website files are in the `web/` folder at the repo root and set **Root directory** to `web` in Cloudflare build settings.

**Symptom:** Cloudflare runs `npm install` instead of `bun install`
This is fine — the repo includes a `.npmrc` with `legacy-peer-deps=true` and pins TypeScript 5.9.x so npm resolves cleanly. To force bun, set `BUN_VERSION` = `1.1` under **Settings → Environment variables**.

## Cost

- **Hosting:** $0 — Cloudflare Pages free tier (unlimited requests, unlimited bandwidth, 500 builds/month, free auto-HTTPS).
- **Domain:** the wholesale `.net` price through Cloudflare Registrar (~low-to-mid teens per year, flat renewals — no markup, no renewal hike).
- **No CMS, no database, no server.** Pure static files.
