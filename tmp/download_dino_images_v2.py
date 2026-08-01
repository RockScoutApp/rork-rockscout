#!/usr/bin/env python3
"""Download remaining dino images using thumbnail URLs from the Wikipedia API.

The previous script failed for 101 dinos because it tried to construct 800px
thumbnail URLs that don't exist. This version uses the exact thumbnail URL
returned by the API (typically 320-400px wide), which is always valid.
Also adds better fallback search queries.
"""

import json
import os
import re
import time
import urllib.parse
import urllib.request

OUTPUT_DIR = "android-rockhound/app/src/main/assets/dino_images"
MAPPING_FILE = "tmp/dino_image_mapping.json"
FAILED_FILE = "tmp/dino_image_failed.txt"

os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs("tmp", exist_ok=True)

# Load existing mapping
mapping = {}
if os.path.exists(MAPPING_FILE):
    with open(MAPPING_FILE, "r") as f:
        mapping = json.load(f)

# Load failed list
failed_ids = []
if os.path.exists(FAILED_FILE):
    with open(FAILED_FILE, "r") as f:
        failed_ids = [line.strip() for line in f if line.strip()]

# Load the full dino list
with open("/tmp/dino_list.txt", "r") as f:
    all_lines = [line.strip() for line in f if line.strip()]

# Build a lookup: dino_id -> display_name
id_to_name = {}
for line in all_lines:
    parts = line.split("|", 1)
    dino_id = parts[0].strip()
    dino_name = parts[1].strip() if len(parts) > 1 else dino_id
    id_to_name[dino_id] = dino_name

# Special Wikipedia title mappings for the failed ones
SPECIAL_TITLES = {
    "tyrannosaurus": ["Tyrannosaurus", "Tyrannosaurus rex"],
    "triceratops": ["Triceratops"],
    "spinosaurus": ["Spinosaurus"],
    "ankylosaurus": ["Ankylosaurus"],
    "stegosaurus": ["Stegosaurus"],
    "giganotosaurus": ["Giganotosaurus"],
    "carcharodontosaurus": ["Carcharodontosaurus"],
    "argentinosaurus": ["Argentinosaurus"],
    "parasaurolophus": ["Parasaurolophus"],
    "iguanodon": ["Iguanodon"],
    "baryonyx": ["Baryonyx"],
    "utahraptor": ["Utahraptor"],
    "microraptor": ["Microraptor"],
    "protoceratops": ["Protoceratops"],
    "oviraptor": ["Oviraptor"],
    "therizinosaurus": ["Therizinosaurus"],
    "carnotaurus": ["Carnotaurus"],
    "maiasaura": ["Maiasaura"],
    "edmontosaurus": ["Edmontosaurus"],
    "lambeosaurus": ["Lambeosaurus"],
    "styracosaurus": ["Styracosaurus"],
    "centrosaurus": ["Centrosaurus"],
    "euoplocephalus": ["Euoplocephalus"],
    "nodosaurus": ["Nodosaurus"],
    "troodon": ["Troodon"],
    "ornithomimus": ["Ornithomimus"],
    "gallimimus": ["Gallimimus"],
    "tarbosaurus": ["Tarbosaurus"],
    "albertosaurus": ["Albertosaurus"],
    "daspletosaurus": ["Daspletosaurus"],
    "suchomimus": ["Suchomimus"],
    "concavenator": ["Concavenator"],
    "sinoceratops": ["Sinoceratops"],
    "kosmoceratops": ["Kosmoceratops"],
    "pentaceratops": ["Pentaceratops"],
    "achelousaurus": ["Achelousaurus"],
    "einiosaurus": ["Einiosaurus"],
    "achillobator": ["Achillobator"],
    "citipati": ["Citipati"],
    "pinacosaurus": ["Pinacosaurus"],
    "saichania": ["Saichania"],
    "tarchia": ["Tarchia"],
    "shantungosaurus": ["Shantungosaurus"],
    "yutyrannus": ["Yutyrannus"],
    "beipiaosaurus": ["Beipiaosaurus"],
    "psittacosaurus": ["Psittacosaurus"],
    "pelecanimimus": ["Pelecanimimus"],
    "polacanthus": ["Polacanthus"],
    "hypsilophodon": ["Hypsilophodon"],
    "ouranosaurus": ["Ouranosaurus"],
    "rebbachisaurus": ["Rebbachisaurus"],
    "dreadnoughtus": ["Dreadnoughtus"],
    "patagotitan": ["Patagotitan"],
    "puertasaurus": ["Puertasaurus"],
    "saltasaurus": ["Saltasaurus"],
    "amargasaurus": ["Amargasaurus"],
    "dakotaraptor": ["Dakotaraptor"],
    "abelisaurus": ["Abelisaurus"],
    "rajasaurus": ["Rajasaurus"],
    "muttaburrasaurus": ["Muttaburrasaurus"],
    "minmi": ["Minmi (dinosaur)", "Minmi dinosaur"],
    "leaellynasaura": ["Leaellynasaura"],
    "monoclonius": ["Monoclonius"],
    "leptoceratops": ["Leptoceratops"],
    "austroraptor": ["Austroraptor"],
    "megaraptor": ["Megaraptor"],
    "rapetosaurus": ["Rapetosaurus"],
    "noasaurus": ["Noasaurus"],
    "masiakasaurus": ["Masiakasaurus"],
    "zanabazar": ["Zanabazar (dinosaur)"],
    "byronosaurus": ["Byronosaurus"],
    "quetzalcoatlus": ["Quetzalcoatlus"],
    "dimorphodon": ["Dimorphodon"],
    "tapejara": ["Tapejara"],
    "liopleurodon": ["Liopleurodon"],
    "tylosaurus": ["Tylosaurus"],
    "plotosaurus": ["Plotosaurus"],
    "synthetoceras": ["Synthetoceras"],
    "woolly-mammoth": ["Woolly mammoth", "Mammuthus primigenius"],
    "columbian-mammoth": ["Columbian mammoth", "Mammuthus columbi"],
    "smilodon-populator": ["Smilodon", "Smilodon populator"],
    "dire-wolf": ["Dire wolf", "Aenocyon dirus"],
    "elasmotherium": ["Elasmotherium"],
    "cave-lion": ["Cave lion", "Panthera spelaea"],
    "giant-horse": ["Equus giganteus"],
    "eremotherium": ["Eremotherium"],
    "moa": ["Moa"],
    "dimetrodon": ["Dimetrodon"],
    "edaphosaurus": ["Edaphosaurus"],
    "diplocaulus": ["Diplocaulus"],
    "eryops": ["Eryops"],
    "anomalocaris": ["Anomalocaris"],
    "dunkleosteus": ["Dunkleosteus"],
    "acanthostega": ["Acanthostega"],
    "meganeura": ["Meganeura"],
    "pulmonoscorpius": ["Pulmonoscorpius"],
    "ceratosaurus": ["Ceratosaurus"],
    "compsognathus": ["Compsognathus"],
    "archaeopteryx": ["Archaeopteryx"],
    "torvosaurus": ["Torvosaurus"],
    "saurophaganax": ["Saurophaganax"],
}


def fetch_page_image(title: str) -> dict | None:
    """Fetch page summary and return the thumbnail URL (always valid on Wikimedia)."""
    encoded = urllib.parse.quote(title, safe="")
    url = f"https://en.wikipedia.org/api/rest_v1/page/summary/{encoded}"
    req = urllib.request.Request(
        url,
        headers={"User-Agent": "RockScoutDinoDict/1.0 (educational app; contact@rork.com)"},
    )
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            data = json.loads(resp.read().decode("utf-8"))
            thumb = data.get("thumbnail")
            original = data.get("originalimage")
            # Prefer thumbnail — it's pre-generated and always downloadable
            if thumb and thumb.get("source"):
                return {"url": thumb["source"], "width": thumb.get("width", 0), "height": thumb.get("height", 0)}
            # Fall back to original (works but may be huge)
            if original and original.get("source"):
                return {"url": original["source"], "width": original.get("width", 0), "height": original.get("height", 0)}
    except Exception:
        pass
    return None


def fetch_via_action_api(title: str) -> dict | None:
    """Use the action API with pageimages prop as a fallback."""
    params = {
        "action": "query",
        "titles": title,
        "prop": "pageimages",
        "format": "json",
        "pithumbsize": "400",
        "pilicense": "any",
    }
    url = "https://en.wikipedia.org/w/api.php?" + urllib.parse.urlencode(params)
    req = urllib.request.Request(
        url,
        headers={"User-Agent": "RockScoutDinoDict/1.0 (educational app; contact@rork.com)"},
    )
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            data = json.loads(resp.read().decode("utf-8"))
            pages = data.get("query", {}).get("pages", {})
            for page_id, page in pages.items():
                thumb = page.get("thumbnail")
                if thumb and thumb.get("source"):
                    return {"url": thumb["source"], "width": thumb.get("width", 0), "height": thumb.get("height", 0)}
    except Exception:
        pass
    return None


def search_wikipedia(query: str) -> str | None:
    """Search Wikipedia for a page title."""
    params = {
        "action": "query",
        "list": "search",
        "srsearch": query,
        "format": "json",
        "srlimit": "5",
    }
    url = "https://en.wikipedia.org/w/api.php?" + urllib.parse.urlencode(params)
    req = urllib.request.Request(
        url,
        headers={"User-Agent": "RockScoutDinoDict/1.0 (educational app; contact@rork.com)"},
    )
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            data = json.loads(resp.read().decode("utf-8"))
            results = data.get("query", {}).get("search", [])
            for result in results:
                title = result["title"]
                # Skip disambiguation pages
                if "(disambiguation)" not in title.lower():
                    return title
            if results:
                return results[0]["title"]
    except Exception:
        pass
    return None


def download_image(url: str, filepath: str) -> bool:
    """Download an image. Returns True on success."""
    req = urllib.request.Request(
        url,
        headers={"User-Agent": "RockScoutDinoDict/1.0 (educational app; contact@rork.com)"},
    )
    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            data = resp.read()
            if len(data) < 3000:
                return False
            with open(filepath, "wb") as f:
                f.write(data)
            return True
    except Exception:
        return False


def get_ext_from_url(url: str) -> str:
    url_lower = url.lower()
    if ".png" in url_lower:
        return "png"
    if ".svg" in url_lower:
        return "svg"
    return "jpg"


def process_dino(dino_id: str, dino_name: str) -> str | None:
    """Download an image for one dino. Returns filename or None."""

    # Skip if already mapped and file exists
    if dino_id in mapping:
        filepath = os.path.join(OUTPUT_DIR, mapping[dino_id])
        if os.path.exists(filepath) and os.path.getsize(filepath) > 3000:
            return mapping[dino_id]

    # Build list of Wikipedia titles to try
    clean_name = re.sub(r"\s*\(.*\)\s*", "", dino_name).strip()
    titles = []
    if dino_id in SPECIAL_TITLES:
        titles.extend(SPECIAL_TITLES[dino_id])
    titles.append(clean_name)
    titles.append(dino_name.split()[0])  # genus name

    # Deduplicate
    seen = set()
    unique_titles = [t for t in titles if t and not (t in seen or seen.add(t))]

    for title in unique_titles:
        # Try REST API first
        result = fetch_page_image(title)
        if not result:
            # Try action API
            result = fetch_via_action_api(title)

        if result and result["url"]:
            ext = get_ext_from_url(result["url"])
            filepath = os.path.join(OUTPUT_DIR, f"{dino_id}.{ext}")
            if download_image(result["url"], filepath):
                return f"{dino_id}.{ext}"

        time.sleep(0.3)

    # Last resort: search Wikipedia
    search_title = search_wikipedia(clean_name if clean_name else dino_name)
    if search_title:
        time.sleep(0.3)
        result = fetch_page_image(search_title)
        if not result:
            result = fetch_via_action_api(search_title)
        if result and result["url"]:
            ext = get_ext_from_url(result["url"])
            filepath = os.path.join(OUTPUT_DIR, f"{dino_id}.{ext}")
            if download_image(result["url"], filepath):
                return f"{dino_id}.{ext}"

    return None


def main():
    succeeded = 0
    still_failed = []

    total = len(failed_ids)
    for i, dino_id in enumerate(failed_ids):
        dino_name = id_to_name.get(dino_id, dino_id)

        # Skip if already has image
        if dino_id in mapping:
            existing = os.path.join(OUTPUT_DIR, mapping[dino_id])
            if os.path.exists(existing) and os.path.getsize(existing) > 3000:
                succeeded += 1
                continue

        result = process_dino(dino_id, dino_name)
        if result:
            mapping[dino_id] = result
            succeeded += 1
            print(f"[{i+1}/{total}] OK   {dino_id} -> {result}")
        else:
            still_failed.append(dino_id)
            print(f"[{i+1}/{total}] FAIL {dino_id} ({dino_name})")

        # Save periodically
        if (i + 1) % 10 == 0:
            with open(MAPPING_FILE, "w") as f:
                json.dump(mapping, f, indent=2)

        time.sleep(0.4)

    # Final save
    with open(MAPPING_FILE, "w") as f:
        json.dump(mapping, f, indent=2)
    with open(FAILED_FILE, "w") as f:
        for fid in still_failed:
            f.write(fid + "\n")

    print(f"\n=== RESULTS ===")
    print(f"Attempted: {total}")
    print(f"Succeeded: {succeeded}")
    print(f"Still failed: {len(still_failed)}")
    if still_failed:
        print(f"Failed: {still_failed}")
    print(f"Total mapped: {len(mapping)}")


if __name__ == "__main__":
    main()
