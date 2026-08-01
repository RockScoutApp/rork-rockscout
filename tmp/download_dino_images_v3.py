#!/usr/bin/env python3
"""Download remaining dino images using the batch action API.

The batch API lets us query up to 50 titles at once, which is much more
reliable than individual requests. Uses the pithumbsize=500 parameter
for good quality images that aren't too large.
"""

import json
import os
import time
import urllib.parse
import urllib.request

OUTPUT_DIR = "android-rockhound/app/src/main/assets/dino_images"
MAPPING_FILE = "tmp/dino_image_mapping.json"
FAILED_FILE = "tmp/dino_image_failed.txt"

os.makedirs(OUTPUT_DIR, exist_ok=True)

# Load mapping
with open(MAPPING_FILE, "r") as f:
    mapping = json.load(f)

# Load failed IDs
with open(FAILED_FILE, "r") as f:
    failed_ids = [line.strip() for line in f if line.strip()]

# Load dino list for names
id_to_name = {}
with open("/tmp/dino_list.txt", "r") as f:
    for line in f:
        line = line.strip()
        if not line:
            continue
        parts = line.split("|", 1)
        dino_id = parts[0].strip()
        dino_name = parts[1].strip() if len(parts) > 1 else dino_id
        id_to_name[dino_id] = dino_name

# For each failed ID, determine the best Wikipedia title to try
# Use the display name (cleaned) as the primary title
import re
title_map = {}  # dino_id -> [titles to try]
for fid in failed_ids:
    name = id_to_name.get(fid, fid)
    clean = re.sub(r"\s*\(.*\)\s*", "", name).strip()
    titles = [clean, name.split()[0]]
    # Some special cases
    specials = {
        "minmi": ["Minmi (dinosaur)", "Minmi dinosaur"],
        "zanabazar": ["Zanabazar (dinosaur)"],
        "giant-horse": ["Equus giganteus"],
        "smilodon-populator": ["Smilodon"],
        "dire-wolf": ["Dire wolf"],
        "woolly-mammoth": ["Woolly mammoth"],
        "columbian-mammoth": ["Columbian mammoth"],
    }
    if fid in specials:
        titles = specials[fid] + titles
    # Deduplicate
    seen = set()
    title_map[fid] = [t for t in titles if t and not (t in seen or seen.add(t))]


def batch_fetch_images(titles: list[str]) -> dict:
    """Fetch thumbnails for up to 50 titles via the batch action API.
    Returns {title: thumbnail_url} mapping.
    """
    results = {}
    # Process in chunks of 50
    for i in range(0, len(titles), 50):
        chunk = titles[i:i+50]
        titles_param = "|".join(chunk)
        params = {
            "action": "query",
            "titles": titles_param,
            "prop": "pageimages",
            "format": "json",
            "pithumbsize": "500",
            "pilicense": "any",
        }
        url = "https://en.wikipedia.org/w/api.php?" + urllib.parse.urlencode(params)
        req = urllib.request.Request(
            url,
            headers={"User-Agent": "RockScoutDinoDict/1.0 (educational app; contact@rork.com)"},
        )
        try:
            with urllib.request.urlopen(req, timeout=20) as resp:
                data = json.loads(resp.read().decode("utf-8"))
                pages = data.get("query", {}).get("pages", {})
                for page_id, page in pages.items():
                    title = page.get("title", "")
                    thumb = page.get("thumbnail", {})
                    if thumb and thumb.get("source"):
                        results[title] = thumb["source"]
        except Exception as e:
            print(f"  Batch error: {e}")
        time.sleep(0.5)
    return results


def download_image(url: str, filepath: str) -> bool:
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


def get_ext(url: str) -> str:
    ul = url.lower()
    if ".png" in ul:
        return "png"
    if ".svg" in ul:
        return "svg"
    return "jpg"


# Collect all unique titles to query
all_titles = set()
for fid in failed_ids:
    for t in title_map[fid]:
        all_titles.add(t)

print(f"Fetching thumbnails for {len(all_titles)} unique titles via batch API...")
title_to_url = batch_fetch_images(list(all_titles))
print(f"Got {len(title_to_url)} thumbnail URLs")

# Now download images for each failed dino
succeeded = 0
still_failed = []

for fid in failed_ids:
    name = id_to_name.get(fid, fid)
    found = False

    for title in title_map[fid]:
        # Try exact match first
        if title in title_to_url:
            url = title_to_url[title]
            ext = get_ext(url)
            filepath = os.path.join(OUTPUT_DIR, f"{fid}.{ext}")
            if download_image(url, filepath):
                mapping[fid] = f"{fid}.{ext}"
                succeeded += 1
                print(f"OK   {fid} -> {fid}.{ext}")
                found = True
                break
        # Try case-insensitive match
        for t, url in title_to_url.items():
            if t.lower() == title.lower():
                ext = get_ext(url)
                filepath = os.path.join(OUTPUT_DIR, f"{fid}.{ext}")
                if download_image(url, filepath):
                    mapping[fid] = f"{fid}.{ext}"
                    succeeded += 1
                    print(f"OK   {fid} -> {fid}.{ext} (matched '{t}')")
                    found = True
                    break
        if found:
            break
        time.sleep(0.2)

    if not found:
        still_failed.append(fid)
        print(f"FAIL {fid} ({name})")

    time.sleep(0.3)

# Save results
with open(MAPPING_FILE, "w") as f:
    json.dump(mapping, f, indent=2)
with open(FAILED_FILE, "w") as f:
    for fid in still_failed:
        f.write(fid + "\n")

print(f"\n=== RESULTS ===")
print(f"Attempted: {len(failed_ids)}")
print(f"Succeeded: {succeeded}")
print(f"Still failed: {len(still_failed)}")
print(f"Total mapped: {len(mapping)}")
if still_failed:
    print(f"Failed IDs: {still_failed}")
