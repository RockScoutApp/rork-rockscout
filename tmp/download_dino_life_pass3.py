#!/usr/bin/env python3
"""Third pass: replace pass-2 picks that turned out to be fossils/skeletons.

For each listed taxon we search Wikimedia Commons in the File namespace and
only accept results that carry an explicit palaeoart marker (artist initials,
"restoration", "reconstruction", "life", etc.) while rejecting anything that
looks like a bone, skull, mount, cave painting or museum specimen photo.
"""

import importlib.util
import json
import os
import time

spec = importlib.util.spec_from_file_location("dl", "tmp/download_dino_life_images.py")
dl = importlib.util.module_from_spec(spec)
spec.loader.exec_module(dl)

# id -> (search genus, filename keys)
REDO = {
    "eoraptor": ("Eoraptor", ["eoraptor"]),
    "nodosaurus": ("Nodosaurus", ["nodosaurus"]),
    "troodon": ("Troodon", ["troodon", "stenonychosaurus"]),
    "saichania": ("Saichania", ["saichania"]),
    "puertasaurus": ("Puertasaurus", ["puertasaurus"]),
    "dakotaraptor": ("Dakotaraptor", ["dakotaraptor"]),
    "mei": ("Mei long", ["mei"]),
    "monoclonius": ("Monoclonius", ["monoclonius", "centrosaurus"]),
    "megaraptor": ("Megaraptor", ["megaraptor"]),
    "noasaurus": ("Noasaurus", ["noasaurus"]),
    "zanabazar": ("Zanabazar junior", ["zanabazar", "saurornithoides"]),
    "byronosaurus": ("Byronosaurus", ["byronosaurus"]),
    "kelenken": ("Kelenken", ["kelenken", "phorusrhac"]),
    "argentavis": ("Argentavis", ["argentavis"]),
    "deinotherium": ("Deinotherium", ["deinotherium"]),
    "chalicotherium": ("Chalicotherium", ["chalicotherium"]),
    "megatherium": ("Megatherium", ["megatherium"]),
    "pygmy-mammoth": ("Mammuthus exilis", ["mammuthus", "mammoth"]),
    "woolly-rhino": ("Coelodonta", ["coelodonta"]),
    "cave-lion": ("Panthera spelaea", ["panthera", "spelaea"]),
    "giant-beaver": ("Castoroides", ["castoroides"]),
    "megaloceros": ("Megaloceros", ["megaloceros"]),
    "steppe-bison": ("Bison priscus", ["bison"]),
    "bison-antiquus": ("Bison antiquus", ["bison"]),
    "camelops": ("Camelops", ["camelops"]),
    "hagerman-horse": ("Equus simplicidens", ["equus", "simplicidens"]),
    "toxodon": ("Toxodon", ["toxodon"]),
    "eremotherium": ("Eremotherium", ["eremotherium"]),
    "thylacine": ("Thylacinus cynocephalus", ["thylacin"]),
    "moa": ("Dinornis", ["dinornis", "moa"]),
    "eurypterid": ("Pterygotus", ["pterygotus", "jaekelopterus", "eurypter"]),
    "plotosaurus": ("Plotosaurus", ["plotosaurus", "mosasaur"]),
    "giant-horse": ("Equus giganteus", ["equus"]),
    "liopleurodon": ("Liopleurodon", ["liopleurodon"]),
    "stellers-sea-cow": ("Hydrodamalis gigas", ["hydrodamalis"]),
}

HARD_REJECT = (
    "skeleton", "squelette", "esqueleto", "skull", "crani", "cranium", "maxilla",
    "mandible", "antler", "scute", "tusk", "tooth", "teeth", "bone", "vertebra",
    "fossil", "mhnt", "museum", "mount", "specimen", "cast", "compared",
    "comparison", "cave", "painting", "panneau", "hunt", "kill", "quarry",
    "la brea", "plate", "figure", "diagram", "scale", "size", "map", "shell",
    "footprint", "claw", "mor full", "holotype", "taxidermy", "stuffed",
    "photograph of", "zoo", "1842", "1812",
)


def rank(candidates: list[str], keys: list[str]) -> list[str]:
    out: list[tuple[int, str]] = []
    for cand in candidates:
        base = cand.split(":", 1)[-1]
        low = base.lower()
        if not low.endswith(dl.GOOD_EXT):
            continue
        norm = low.replace("_", " ")
        if any(bad in norm for bad in HARD_REJECT):
            continue
        if not any(k in norm for k in keys):
            continue
        pts = 0
        for marker in dl.STRONG:
            if marker in low:
                pts += 40
        if pts == 0:
            continue
        out.append((pts, cand))
    out.sort(key=lambda p: -p[0])
    return [c for _, c in out]


def main() -> None:
    mapping = json.load(open(dl.RESULT_FILE)) if os.path.exists(dl.RESULT_FILE) else {}
    fixed: list[str] = []
    failed: list[str] = []
    for dino_id, (genus, keys) in REDO.items():
        cands: list[str] = []
        for phrase in (f"{genus} restoration", f"{genus} reconstruction", f"{genus} life", genus):
            try:
                res = dl.api("commons.wikimedia.org", {
                    "action": "query", "list": "search", "srsearch": phrase,
                    "srnamespace": "6", "srlimit": "40",
                })
                cands += [r["title"] for r in res.get("query", {}).get("search", []) or []]
            except Exception:
                pass
            time.sleep(0.4)
        cands += dl.list_article_images(genus)
        ranked = rank(list(dict.fromkeys(cands)), keys)
        saved = False
        for cand in ranked[:5]:
            url = dl.file_url(cand)
            if not url:
                continue
            ext = ".png" if url.lower().split("?")[0].endswith(".png") else ".jpg"
            # Remove any previous file for this id so only one extension exists.
            for old in (".jpg", ".png"):
                p = os.path.join(dl.OUT_DIR, dino_id + old)
                if os.path.exists(p) and old != ext:
                    os.remove(p)
            path = os.path.join(dl.OUT_DIR, dino_id + ext)
            if dl.download(url, path):
                mapping[dino_id] = dino_id + ext
                print(f"FIX  {dino_id} <- {cand}", flush=True)
                fixed.append(dino_id)
                saved = True
                break
        if not saved:
            failed.append(dino_id)
            print(f"KEEP {dino_id} (no palaeoart found)", flush=True)
        time.sleep(0.8)

    json.dump(mapping, open(dl.RESULT_FILE, "w"), indent=1)
    print(f"\nfixed {len(fixed)}, unresolved {len(failed)}: {failed}")


if __name__ == "__main__":
    main()
