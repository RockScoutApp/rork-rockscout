#!/usr/bin/env python3
"""Final pass: replace the images a visual audit flagged as bones/diagrams.

Only files carrying an explicit palaeoart marker are accepted, and anything
that reads as a bone, mount, diagram, taxidermy or scale figure is rejected.
Entries that still cannot be resolved are deleted so the app falls back to the
drawn silhouette instead of showing a skeleton labelled as a living animal.
"""

import importlib.util
import json
import os
import time

spec = importlib.util.spec_from_file_location("dl", "tmp/download_dino_life_images.py")
dl = importlib.util.module_from_spec(spec)
spec.loader.exec_module(dl)

# id -> (search phrase, filename keys)
REDO = {
    "camarasaurus": ("Camarasaurus", ["camarasaurus"]),
    "camelops": ("Camelops", ["camelops"]),
    "citipati": ("Citipati", ["citipati"]),
    "deinotherium": ("Deinotherium", ["deinotherium"]),
    "dilophosaurus": ("Dilophosaurus", ["dilophosaurus"]),
    "dimorphodon": ("Dimorphodon", ["dimorphodon"]),
    "diplodocus": ("Diplodocus", ["diplodocus"]),
    "dodo": ("Dodo Raphus cucullatus", ["dodo", "raphus"]),
    "dreadnoughtus": ("Dreadnoughtus", ["dreadnoughtus"]),
    "edmontosaurus": ("Edmontosaurus", ["edmontosaurus"]),
    "great-auk": ("Great auk Pinguinus", ["auk", "pinguinus"]),
    "heterodontosaurus": ("Heterodontosaurus", ["heterodontosaurus"]),
    "iguanodon": ("Iguanodon", ["iguanodon"]),
    "lambeosaurus": ("Lambeosaurus", ["lambeosaurus"]),
    "lesothosaurus": ("Lesothosaurus", ["lesothosaurus"]),
    "muttaburrasaurus": ("Muttaburrasaurus", ["muttaburrasaurus"]),
    "paraceratherium": ("Paraceratherium", ["paraceratherium", "indricotherium"]),
    "pelagornis": ("Pelagornis", ["pelagornis"]),
    "plateosaurus": ("Plateosaurus", ["plateosaurus"]),
    "pteranodon": ("Pteranodon", ["pteranodon"]),
    "smilodon": ("Smilodon", ["smilodon"]),
    "smilodon-populator": ("Smilodon populator", ["smilodon"]),
    "tapejara": ("Tapejara pterosaur", ["tapejara"]),
    "woolly-mammoth": ("Mammuthus primigenius", ["mammuthus", "mammoth"]),
    "megaloceros": ("Megaloceros giganteus", ["megaloceros"]),
    "thylacine": ("Thylacinus cynocephalus", ["thylacin"]),
    "troodon": ("Stenonychosaurus Troodon", ["troodon", "stenonychosaurus"]),
    "woolly-rhino": ("Coelodonta antiquitatis", ["coelodonta"]),
    "cave-lion": ("Panthera spelaea", ["panthera", "spelaea"]),
    "eoraptor": ("Eoraptor lunensis", ["eoraptor"]),
    "nodosaurus": ("Nodosaurus", ["nodosaurus"]),
    "saichania": ("Saichania", ["saichania"]),
    "kelenken": ("Kelenken guillermoi", ["kelenken"]),
    "chalicotherium": ("Chalicotherium", ["chalicotherium"]),
    "toxodon": ("Toxodon", ["toxodon"]),
    "eremotherium": ("Eremotherium", ["eremotherium"]),
    "bison-antiquus": ("Bison antiquus", ["bison"]),
    "steppe-bison": ("Bison priscus steppe bison", ["bison", "priscus"]),
    "giant-beaver": ("Castoroides ohioensis", ["castoroides"]),
    "hagerman-horse": ("Equus simplicidens", ["simplicidens"]),
    "pygmy-mammoth": ("Mammuthus exilis pygmy mammoth", ["exilis", "mammuthus"]),
    "dakotaraptor": ("Dakotaraptor steini", ["dakotaraptor"]),
    "megaraptor": ("Megaraptor namunhuaiquii", ["megaraptor"]),
    "noasaurus": ("Noasaurus leali", ["noasaurus"]),
    "monoclonius": ("Monoclonius Centrosaurus", ["monoclonius", "centrosaurus"]),
    "puertasaurus": ("Puertasaurus reuili", ["puertasaurus"]),
    "argentavis": ("Argentavis magnificens", ["argentavis"]),
    "liopleurodon": ("Liopleurodon ferox", ["liopleurodon"]),
    "giant-horse": ("Equus giganteus", ["equus"]),
    "majungasaurus": ("Majungasaurus crenatissimus", ["majungasaurus"]),
}

HARD_REJECT = (
    "skeleton", "skeletal", "squelette", "esqueleto", "skull", "crani", "cranium",
    "maxilla", "mandible", "jaw", "antler", "scute", "tusk", "tooth", "teeth",
    "bone", "vertebra", "femur", "humerus", "pelvis", "fossil", "mhnt", "museum",
    "mount", "specimen", "holotype", "cast", "compared", "comparison", "diagram",
    "cave", "painting", "panneau", "hunt", "kill", "quarry", "la brea", "plate",
    "figure", "fig.", "scale", "size", "map", "shell", "footprint", "claw",
    "taxidermy", "stuffed", "egg", "embryo", "nest", "anatomy", "myology",
    "musculature", "chart", "1842", "1812", "1905", "engraving", "sketch",
    "exhibit", "gallery", "display", "wing membrane", "pneumatic",
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
        pts = sum(40 for marker in dl.STRONG if marker in low)
        if pts == 0:
            continue
        out.append((pts, cand))
    out.sort(key=lambda p: -p[0])
    return [c for _, c in out]


def main() -> None:
    mapping = json.load(open(dl.RESULT_FILE)) if os.path.exists(dl.RESULT_FILE) else {}
    fixed: list[str] = []
    dropped: list[str] = []
    for dino_id, (phrase, keys) in REDO.items():
        cands: list[str] = []
        for query in (f"{phrase} restoration", f"{phrase} reconstruction", f"{phrase} life restoration"):
            try:
                res = dl.api("commons.wikimedia.org", {
                    "action": "query", "list": "search", "srsearch": query,
                    "srnamespace": "6", "srlimit": "40",
                })
                cands += [r["title"] for r in res.get("query", {}).get("search", []) or []]
            except Exception:
                pass
            time.sleep(0.3)
        cands += dl.list_article_images(phrase.split()[0])
        ranked = rank(list(dict.fromkeys(cands)), keys)

        saved = False
        for cand in ranked[:5]:
            url = dl.file_url(cand)
            if not url:
                continue
            ext = ".png" if url.lower().split("?")[0].endswith(".png") else ".jpg"
            tmp_path = os.path.join(dl.OUT_DIR, dino_id + ".tmp")
            if dl.download(url, tmp_path):
                for old in (".jpg", ".png", ".JPG"):
                    p = os.path.join(dl.OUT_DIR, dino_id + old)
                    if os.path.exists(p):
                        os.remove(p)
                os.replace(tmp_path, os.path.join(dl.OUT_DIR, dino_id + ext))
                mapping[dino_id] = dino_id + ext
                print(f"FIX  {dino_id} <- {cand}", flush=True)
                fixed.append(dino_id)
                saved = True
                break
        if not saved:
            for old in (".jpg", ".png", ".JPG"):
                p = os.path.join(dl.OUT_DIR, dino_id + old)
                if os.path.exists(p):
                    os.remove(p)
            mapping.pop(dino_id, None)
            dropped.append(dino_id)
            print(f"DROP {dino_id}", flush=True)
        time.sleep(0.6)

    json.dump(mapping, open(dl.RESULT_FILE, "w"), indent=1)
    print(f"\nfixed {len(fixed)}, dropped {len(dropped)}: {dropped}")


if __name__ == "__main__":
    main()
