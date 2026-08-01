#!/usr/bin/env python3
"""Download Wikipedia images for all dinosaurs in the dino dictionary.

Uses the Wikipedia REST API to fetch page summaries with thumbnails,
then downloads the original/full images and saves them locally.
Handles name mismatches with fallback searches.
"""

import json
import os
import re
import subprocess
import sys
import time
import urllib.parse
import urllib.request

OUTPUT_DIR = "android-rockhound/app/src/main/assets/dino_images"
MAPPING_FILE = "tmp/dino_image_mapping.json"
FAILED_FILE = "tmp/dino_image_failed.txt"

# Ensure output dir exists
os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs("tmp", exist_ok=True)

# Read the dino list
with open("/tmp/dino_list.txt", "r") as f:
    lines = [line.strip() for line in f if line.strip()]

# Special name mappings: dino_id -> Wikipedia article title(s) to try (in order)
# These handle cases where the app name doesn't match the Wikipedia article title
SPECIAL_MAPPINGS = {
    "tyrannosaurus": ["Tyrannosaurus", "Tyrannosaurus rex"],
    "great-auk": ["Great auk"],
    "stellers-sea-cow": ["Steller's sea cow"],
    "smilodon-populator": ["Smilodon", "Smilodon populator"],
    "dire-wolf": ["Dire wolf", "Aenocyon dirus"],
    "short-faced-bear": ["Giant short-faced bear", "Arctodus"],
    "cave-lion": ["Cave lion", "Panthera spelaea"],
    "cave-bear": ["Cave bear", "Ursus spelaeus"],
    "american-lion": ["American lion", "Panthera atrox"],
    "american-cheetah": ["American cheetah", "Miracinonyx"],
    "giant-beaver": ["Giant beaver", "Castoroides"],
    "giant-horse": ["Equus giganteus"],
    "hagerman-horse": ["Hagerman horse"],
    "terror-bird-titanis": ["Titanis"],
    "thylacine": ["Thylacine", "Tasmanian tiger"],
    "moa": ["Moa"],
    "dodo": ["Dodo"],
    "sea-scorpion-eurypterid": ["Eurypterid", "Sea scorpion"],
    "eurypterid": ["Eurypterid", "Sea scorpion"],
    "mei": ["Mei long", "Mei (dinosaur)"],
    "epidexipteryx": ["Epidexipteryx"],
    "columbian-mammoth": ["Columbian mammoth"],
    "pygmy-mammoth": ["Pygmy mammoth", "Mammuthus exilis"],
    "woolly-rhino": ["Woolly rhinoceros", "Coelodonta"],
    "cave-lion": ["Cave lion", "Panthera spelaea"],
    "steppe-bison": ["Steppe bison"],
    "bison-antiquus": ["Bison antiquus"],
    "camelops": ["Camelops"],
    "toxodon": ["Toxodon"],
    "macrauchenia": ["Macrauchenia"],
    "eremotherium": ["Eremotherium"],
    "thylacoleo": ["Thylacoleo", "Marsupial lion"],
    "megaloceros": ["Megaloceros", "Irish elk"],
    "mastodon": ["Mastodon"],
    "glyptodont": ["Glyptodon"],
    "doedicurus": ["Doedicurus"],
    "diprotodon": ["Diprotodon"],
    "megatherium": ["Megatherium"],
    "megalania": ["Megalania", "Varanus priscus"],
    "andrewsarchus": ["Andrewsarchus"],
    "entelodont": ["Entelodont", "Entelodontidae"],
    "arsinoitherium": ["Arsinoitherium"],
    "titanoboa": ["Titanoboa"],
    "phorusrhacos": ["Phorusrhacos", "Terror bird"],
    "kelenken": ["Kelenken"],
    "argentavis": ["Argentavis"],
    "deinotherium": ["Deinotherium"],
    "chalicotherium": ["Chalicotherium"],
    "pelagornis": ["Pelagornis"],
    "uintatherium": ["Uintatherium"],
    "amphicyon": ["Amphicyon"],
    "merychippus": ["Merychippus"],
    "synthetoceras": ["Synthetoceras"],
    "basilosaurus": ["Basilosaurus"],
    "paraceratherium": ["Paraceratherium", "Indricotherium"],
    "dimetrodon": ["Dimetrodon"],
    "edaphosaurus": ["Edaphosaurus"],
    "diplocaulus": ["Diplocaulus"],
    "eryops": ["Eryops"],
    "anomalocaris": ["Anomalocaris"],
    "dunkleosteus": ["Dunkleosteus"],
    "tiktaalik": ["Tiktaalik"],
    "acanthostega": ["Acanthostega"],
    "meganeura": ["Meganeura"],
    "arthropleura": ["Arthropleura"],
    "pulmonoscorpius": ["Pulmonoscorpius"],
    "leedsichthys": ["Leedsichthys"],
    "megalodon": ["Megalodon", "Otodus megalodon"],
    "deinosuchus": ["Deinosuchus"],
    "sarcosuchus": ["Sarcosuchus"],
    "sloths": ["Ground sloth"],
    "sloth": ["Ground sloth"],
    "spinosaurus": ["Spinosaurus"],
    "velociraptor": ["Velociraptor"],
    "anomalocaris": ["Anomalocaris"],
}


def get_wikipedia_thumbnail(title: str) -> dict | None:
    """Fetch the page summary from Wikipedia REST API and return thumbnail info."""
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
            if original and original.get("source"):
                return {
                    "url": original["source"],
                    "width": original.get("width", 0),
                    "height": original.get("height", 0),
                }
            elif thumb and thumb.get("source"):
                return {
                    "url": thumb["source"],
                    "width": thumb.get("width", 0),
                    "height": thumb.get("height", 0),
                }
    except Exception as e:
        # Try the action API as fallback
        return get_wikipedia_thumbnail_api(title)


def get_wikipedia_thumbnail_api(title: str) -> dict | None:
    """Fallback: use the action API with pageimages prop."""
    params = {
        "action": "query",
        "titles": title,
        "prop": "pageimages",
        "format": "json",
        "pithumbsize": "800",
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
                    return {
                        "url": thumb["source"],
                        "width": thumb.get("width", 0),
                        "height": thumb.get("height", 0),
                    }
    except Exception:
        pass
    return None


def search_wikipedia(query: str) -> str | None:
    """Search Wikipedia for a page title matching the query."""
    params = {
        "action": "query",
        "list": "search",
        "srsearch": query,
        "format": "json",
        "srlimit": "3",
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
            if results:
                return results[0]["title"]
    except Exception:
        pass
    return None


def download_image(url: str, filepath: str) -> bool:
    """Download an image from a URL to a local file."""
    req = urllib.request.Request(
        url,
        headers={"User-Agent": "RockScoutDinoDict/1.0 (educational app; contact@rork.com)"},
    )
    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            data = resp.read()
            if len(data) < 2000:  # Too small, probably an error page
                return False
            with open(filepath, "wb") as f:
                f.write(data)
            return True
    except Exception:
        return False


def process_dino(dino_id: str, dino_name: str) -> str | None:
    """Try to download an image for a dinosaur. Returns the local filename or None."""

    # Check if we already have it
    expected_file = os.path.join(OUTPUT_DIR, f"{dino_id}.jpg")
    if os.path.exists(expected_file) and os.path.getsize(expected_file) > 2000:
        return f"{dino_id}.jpg"

    # Determine the Wikipedia titles to try
    # Clean the display name - remove parentheticals and extra text
    clean_name = re.sub(r"\s*\(.*\)\s*", "", dino_name).strip()
    # Also try without "giant", "great", etc. prefixes for better matches
    alt_name = clean_name
    for prefix in ["Giant ", "Great "]:
        if clean_name.startswith(prefix):
            alt_name = clean_name[len(prefix):]

    titles_to_try = []
    if dino_id in SPECIAL_MAPPINGS:
        titles_to_try.extend(SPECIAL_MAPPINGS[dino_id])
    titles_to_try.append(clean_name)
    titles_to_try.append(alt_name)
    titles_to_try.append(dino_name)
    # Also try just the scientific genus name (first word, capitalized)
    titles_to_try.append(dino_name.split()[0])

    # Deduplicate while preserving order
    seen = set()
    unique_titles = []
    for t in titles_to_try:
        if t and t not in seen:
            seen.add(t)
            unique_titles.append(t)

    for title in unique_titles:
        result = get_wikipedia_thumbnail(title)
        if result and result["url"]:
            # Determine file extension from URL
            url_lower = result["url"].lower()
            if ".png" in url_lower:
                ext = "png"
            elif ".jpg" in url_lower or ".jpeg" in url_lower:
                ext = "jpg"
            elif ".svg" in url_lower:
                ext = "svg"
            else:
                ext = "jpg"

            filepath = os.path.join(OUTPUT_DIR, f"{dino_id}.{ext}")
            if download_image(result["url"], filepath):
                return f"{dino_id}.{ext}"

        time.sleep(0.3)  # Be polite to Wikipedia

    # Last resort: search Wikipedia
    search_query = clean_name if clean_name else dino_name
    found_title = search_wikipedia(search_query)
    if found_title:
        time.sleep(0.3)
        result = get_wikipedia_thumbnail(found_title)
        if result and result["url"]:
            url_lower = result["url"].lower()
            if ".png" in url_lower:
                ext = "png"
            elif ".jpg" in url_lower or ".jpeg" in url_lower:
                ext = "jpg"
            elif ".svg" in url_lower:
                ext = "svg"
            else:
                ext = "jpg"
            filepath = os.path.join(OUTPUT_DIR, f"{dino_id}.{ext}")
            if download_image(result["url"], filepath):
                return f"{dino_id}.{ext}"

    return None


def main():
    mapping = {}
    failed = []
    succeeded = 0
    already_had = 0

    # Load existing mapping if any
    if os.path.exists(MAPPING_FILE):
        with open(MAPPING_FILE, "r") as f:
            mapping = json.load(f)

    total = len(lines)
    for i, line in enumerate(lines):
        parts = line.split("|", 1)
        dino_id = parts[0].strip()
        dino_name = parts[1].strip() if len(parts) > 1 else dino_id

        # Skip if already mapped and file exists
        if dino_id in mapping:
            existing_file = os.path.join(OUTPUT_DIR, mapping[dino_id])
            if os.path.exists(existing_file) and os.path.getsize(existing_file) > 2000:
                already_had += 1
                continue

        result = process_dino(dino_id, dino_name)
        if result:
            mapping[dino_id] = result
            succeeded += 1
            print(f"[{i+1}/{total}] OK   {dino_id} -> {result}")
        else:
            failed.append(dino_id)
            print(f"[{i+1}/{total}] FAIL {dino_id} ({dino_name})")

        # Save mapping periodically
        if (i + 1) % 20 == 0:
            with open(MAPPING_FILE, "w") as f:
                json.dump(mapping, f, indent=2)
            with open(FAILED_FILE, "w") as f:
                for fid in failed:
                    f.write(fid + "\n")

        time.sleep(0.5)  # Be polite

    # Final save
    with open(MAPPING_FILE, "w") as f:
        json.dump(mapping, f, indent=2)
    with open(FAILED_FILE, "w") as f:
        for fid in failed:
            f.write(fid + "\n")

    print(f"\n=== SUMMARY ===")
    print(f"Total: {total}")
    print(f"Already had: {already_had}")
    print(f"Newly downloaded: {succeeded}")
    print(f"Failed: {len(failed)}")
    if failed:
        print(f"Failed IDs: {failed}")


if __name__ == "__main__":
    main()
