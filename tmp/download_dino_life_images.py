#!/usr/bin/env python3
"""Download life-reconstruction ("alive") images for every dictionary entry.

The existing assets/dino_images/ folder holds fossil/skeleton photos. This
script builds a parallel assets/dino_life/ folder with palaeoart life
restorations so the dictionary shows living animals instead of bones.

Strategy per entry:
  1. Resolve the best Wikipedia article title.
  2. List every image on that article (prop=images).
  3. Score filenames: reward palaeoart markers (artist suffixes such as NT /
     DB / BW, plus words like restoration, reconstruction, life), reject
     skeleton/skull/fossil/map/scale/diagram files.
  4. Fall back to a Commons file search for "<genus> restoration".
  5. Download the best candidate at 900px wide.
"""

import json
import os
import re
import sys
import time
import urllib.parse
import urllib.request

OUT_DIR = "android-rockhound/app/src/main/assets/dino_life"
IDS_FILE = "/tmp/dino_ids.json"
RESULT_FILE = "/tmp/dino_life_mapping.json"
UA = "RockScoutDinoDict/2.0 (educational app; hello@rork.com)"

os.makedirs(OUT_DIR, exist_ok=True)

# Filenames containing these are never life reconstructions.
REJECT = (
    "skeleton", "skeletal", "skull", "fossil", "bone", "teeth", "tooth",
    "vertebra", "femur", "claw_", "footprint", "track", "map", "distribution",
    "range", "scale", "size", "comparison", "diagram", "chart", "cladogram",
    "phylogeny", "tree", "logo", "commons", "wiki", "icon", "stub", "edit",
    "question", "disambig", "mount", "museum", "specimen", "holotype",
    "quarry", "excavation", "cast_", "replica", "timeline", "stratigraph",
    "locator", "flag", "ambox", "portal", "symbol", "arrow", "clock",
    "speaker", "audio", "sound", "graph", "plot", "photo_of_a_skull",
)

# Strong palaeoart signals (artist initials on Commons + descriptive words).
STRONG = (
    "_nt.", "_nt_", "nt.jpg", "nt.png", "_db.", "db.jpg", "_bw.", "bw.jpg",
    "restoration", "reconstruction", "life_", "_life", "paleoart",
    "palaeoart", "mmartyniuk", "steveoc", "dibgd", "arthurweasley",
    "durbed", "nobu_tamura", "bogdanov", "hartman", "artist",
    "_in_the_wild", "illustration", "render", "model", "artwork",
)

GOOD_EXT = (".jpg", ".jpeg", ".png")

# Extra genus tokens used for filename matching when the article title does
# not contain the scientific genus (mostly Ice Age mammals).
EXTRA_TOKENS = {
    "woolly-mammoth": ["mammuthus", "mammoth"],
    "columbian-mammoth": ["mammuthus", "mammoth"],
    "steppe-mammoth": ["mammuthus", "mammoth"],
    "woolly-rhino": ["coelodonta"],
    "dire-wolf": ["aenocyon", "canis", "wolf"],
    "cave-bear": ["ursus", "spelaeus"],
    "cave-lion": ["panthera", "spelaea"],
    "american-lion": ["panthera", "atrox"],
    "great-auk": ["pinguinus", "auk"],
    "irish-elk": ["megaloceros"],
    "terror-bird": ["phorusrhacos", "phorusrhacid", "titanis"],
    "giant-ground-sloth": ["megatherium"],
    "short-faced-bear": ["arctodus"],
    "american-cheetah": ["miracinonyx"],
    "giant-beaver": ["castoroides"],
    "hagerman-horse": ["equus"],
    "giant-horse": ["equus"],
    "bison-antiquus": ["bison"],
    "smilodon-populator": ["smilodon"],
    "dodo": ["raphus", "dodo"],
    "entelodont": ["entelodon", "daeodon"],
    "glyptodont": ["glyptodon"],
    "eurypterid": ["eurypter", "pterygotus", "jaekelopterus"],
    "giant-tortoise": ["megalochelys", "colossochelys"],
    "sabertooth-salmon": ["oncorhynchus"],
}

SPECIALS = {
    "tyrannosaurus": "Tyrannosaurus",
    "giant-horse": "Equus giganteus",
    "dire-wolf": "Dire wolf",
    "woolly-mammoth": "Woolly mammoth",
    "columbian-mammoth": "Columbian mammoth",
    "woolly-rhino": "Woolly rhinoceros",
    "cave-bear": "Cave bear",
    "cave-lion": "Cave lion",
    "american-lion": "American lion",
    "american-cheetah": "Miracinonyx",
    "giant-beaver": "Castoroides",
    "great-auk": "Great auk",
    "hagerman-horse": "Equus simplicidens",
    "bison-antiquus": "Bison antiquus",
    "smilodon-populator": "Smilodon",
    "minmi": "Minmi paravertebra",
    "zanabazar": "Zanabazar junior",
    "dodo": "Dodo",
    "eurypterid": "Eurypterid",
    "entelodont": "Entelodon",
    "glyptodont": "Glyptodon",
    "giant-ground-sloth": "Megatherium",
    "short-faced-bear": "Arctodus",
    "steppe-mammoth": "Mammuthus trogontherii",
    "irish-elk": "Irish elk",
    "giant-tortoise": "Megalochelys",
    "terror-bird": "Phorusrhacidae",
    "sabertooth-salmon": "Oncorhynchus rastrosus",
    "megalodon": "Megalodon",
    "helicoprion": "Helicoprion",
}


def fetch(url: str, timeout: int = 30) -> bytes:
    """GET with exponential backoff so Wikimedia rate limits do not abort a run."""
    delay = 2.0
    last: Exception | None = None
    for _ in range(6):
        req = urllib.request.Request(url, headers={"User-Agent": UA})
        try:
            with urllib.request.urlopen(req, timeout=timeout) as resp:
                return resp.read()
        except urllib.error.HTTPError as err:
            last = err
            if err.code in (429, 503):
                time.sleep(delay)
                delay *= 2
                continue
            raise
        except Exception as err:
            last = err
            time.sleep(delay)
            delay *= 2
    raise last if last else RuntimeError("fetch failed")


def api(host: str, params: dict) -> dict:
    params = dict(params)
    params["format"] = "json"
    url = f"https://{host}/w/api.php?" + urllib.parse.urlencode(params)
    return json.loads(fetch(url, timeout=25).decode("utf-8"))


def title_for(dino_id: str, name: str) -> str:
    if dino_id in SPECIALS:
        return SPECIALS[dino_id]
    clean = re.sub(r"\s*\(.*?\)\s*", "", name).strip()
    return clean


def score(filename: str) -> int:
    low = filename.lower()
    if not low.endswith(GOOD_EXT):
        return -1000
    if any(bad in low for bad in REJECT):
        return -1000
    pts = 0
    for marker in STRONG:
        if marker in low:
            pts += 40
    # Prefer files that lead with the genus name (usually the taxobox art).
    if pts == 0:
        pts = 5
    return pts


def list_article_images(title: str) -> list[str]:
    try:
        data = api("en.wikipedia.org", {
            "action": "query", "titles": title, "prop": "images",
            "imlimit": "200", "redirects": "1",
        })
    except Exception:
        return []
    pages = data.get("query", {}).get("pages", {})
    files: list[str] = []
    for page in pages.values():
        for img in page.get("images", []) or []:
            files.append(img["title"])
    return files


def commons_search(query: str) -> list[str]:
    try:
        data = api("commons.wikimedia.org", {
            "action": "query", "list": "search", "srsearch": f"{query} restoration",
            "srnamespace": "6", "srlimit": "20",
        })
    except Exception:
        return []
    return [r["title"] for r in data.get("query", {}).get("search", []) or []]


def file_url(file_title: str, width: int = 900) -> str | None:
    for host in ("commons.wikimedia.org", "en.wikipedia.org"):
        try:
            data = api(host, {
                "action": "query", "titles": file_title, "prop": "imageinfo",
                "iiprop": "url", "iiurlwidth": str(width),
            })
        except Exception:
            continue
        for page in data.get("query", {}).get("pages", {}).values():
            info = (page.get("imageinfo") or [None])[0]
            if info:
                return info.get("thumburl") or info.get("url")
    return None


def download(url: str, path: str) -> bool:
    try:
        data = fetch(url, timeout=45)
        if len(data) < 6000:
            return False
        with open(path, "wb") as fh:
            fh.write(data)
        return True
    except Exception:
        return False


def tokens_for(dino_id: str, title: str) -> list[str]:
    base = [w.lower() for w in re.split(r"[\s_-]+", title) if len(w) > 3]
    return list(dict.fromkeys(base + EXTRA_TOKENS.get(dino_id, [])))


def pick_best(candidates: list[str], dino_id: str, title: str) -> list[str]:
    """Rank candidate file titles, hard-filtering to files that name the taxon."""
    keys = tokens_for(dino_id, title)
    scored: list[tuple[int, str]] = []
    for cand in candidates:
        base = cand.split(":", 1)[-1]
        pts = score(base)
        if pts <= 0:
            continue
        norm = base.lower().replace("_", " ")
        if not any(k in norm for k in keys):
            continue
        # Only keep files that actually look like palaeoart.
        if pts < 40:
            continue
        if norm.split()[0] in keys:
            pts += 25
        scored.append((pts, cand))
    scored.sort(key=lambda pair: -pair[0])
    return [c for _, c in scored]


def main() -> None:
    ids: dict[str, str] = json.load(open(IDS_FILE))
    mapping: dict[str, str] = {}
    if os.path.exists(RESULT_FILE):
        mapping = json.load(open(RESULT_FILE))

    todo = [(i.strip(), n) for i, n in ids.items()]
    failed: list[str] = []

    for idx, (dino_id, name) in enumerate(todo, 1):
        already = [e for e in (".jpg", ".png") if os.path.exists(os.path.join(OUT_DIR, dino_id + e))]
        if already:
            mapping[dino_id] = dino_id + already[0]
            continue
        title = title_for(dino_id, name)
        ranked = pick_best(list_article_images(title), dino_id, title)
        if not ranked:
            ranked = pick_best(commons_search(title), dino_id, title)

        saved = False
        for cand in ranked[:4]:
            url = file_url(cand)
            if not url:
                continue
            ext = ".png" if url.lower().split("?")[0].endswith(".png") else ".jpg"
            path = os.path.join(OUT_DIR, dino_id + ext)
            if download(url, path):
                mapping[dino_id] = dino_id + ext
                saved = True
                print(f"[{idx}/{len(todo)}] OK   {dino_id} <- {cand}", flush=True)
                break
            time.sleep(0.2)

        if not saved:
            failed.append(dino_id)
            print(f"[{idx}/{len(todo)}] MISS {dino_id} ({title})", flush=True)

        if idx % 5 == 0:
            json.dump(mapping, open(RESULT_FILE, "w"), indent=1)
        time.sleep(1.2)

    json.dump(mapping, open(RESULT_FILE, "w"), indent=1)
    print(f"\n=== {len(mapping)} life images, {len(failed)} missing ===")
    print("missing:", failed)


if __name__ == "__main__":
    main()
