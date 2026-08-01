#!/usr/bin/env python3
"""Second pass for entries with no life reconstruction yet.

Relaxes the "strong palaeoart marker" requirement: any image on the taxon's
Wikipedia article that names the taxon and is not obviously a fossil/skeleton/
diagram is accepted. Also tries a Commons file search on several phrasings.
"""

import importlib.util
import json
import os
import time

spec = importlib.util.spec_from_file_location("dl", "tmp/download_dino_life_images.py")
dl = importlib.util.module_from_spec(spec)
spec.loader.exec_module(dl)

MANUAL_TITLES = {
    "eoraptor": "Eoraptor",
    "nodosaurus": "Nodosaurus",
    "troodon": "Troodon",
    "saichania": "Saichania",
    "tarchia": "Tarchia",
    "incisivosaurus": "Incisivosaurus",
    "puertasaurus": "Puertasaurus",
    "dakotaraptor": "Dakotaraptor",
    "mei": "Mei (dinosaur)",
    "monoclonius": "Monoclonius",
    "megaraptor": "Megaraptor",
    "noasaurus": "Noasaurus",
    "zanabazar": "Zanabazar (dinosaur)",
    "byronosaurus": "Byronosaurus",
    "liopleurodon": "Liopleurodon",
    "plotosaurus": "Plotosaurus",
    "kelenken": "Kelenken",
    "argentavis": "Argentavis",
    "deinotherium": "Deinotherium",
    "chalicotherium": "Chalicotherium",
    "megatherium": "Megatherium",
    "pygmy-mammoth": "Pygmy mammoth",
    "woolly-rhino": "Woolly rhinoceros",
    "cave-lion": "Panthera spelaea",
    "american-cheetah": "Miracinonyx",
    "giant-beaver": "Castoroides",
    "megaloceros": "Megaloceros",
    "steppe-bison": "Steppe bison",
    "bison-antiquus": "Bison antiquus",
    "camelops": "Camelops",
    "hagerman-horse": "Equus simplicidens",
    "giant-horse": "Equus giganteus",
    "toxodon": "Toxodon",
    "eremotherium": "Eremotherium",
    "thylacine": "Thylacine",
    "moa": "Moa",
    "stellers-sea-cow": "Steller's sea cow",
    "eurypterid": "Eurypterid",
}

EXTRA = {
    "pygmy-mammoth": ["mammuthus", "mammoth"],
    "woolly-rhino": ["coelodonta", "rhinoceros"],
    "cave-lion": ["panthera", "spelaea", "lion"],
    "american-cheetah": ["miracinonyx", "cheetah"],
    "giant-beaver": ["castoroides", "beaver"],
    "steppe-bison": ["bison"],
    "bison-antiquus": ["bison"],
    "hagerman-horse": ["equus", "simplicidens", "horse"],
    "giant-horse": ["equus", "horse"],
    "thylacine": ["thylacinus", "thylacine"],
    "moa": ["dinornis", "moa", "emeus", "pachyornis"],
    "stellers-sea-cow": ["hydrodamalis", "sea cow"],
    "eurypterid": ["eurypter", "pterygotus", "jaekelopterus", "megalograptus"],
    "mei": ["mei long", "mei-long"],
    "zanabazar": ["zanabazar"],
}


def relaxed_rank(candidates: list[str], keys: list[str]) -> list[str]:
    ranked: list[tuple[int, str]] = []
    for cand in candidates:
        base = cand.split(":", 1)[-1]
        low = base.lower()
        if not low.endswith(dl.GOOD_EXT):
            continue
        if any(bad in low for bad in dl.REJECT):
            continue
        norm = low.replace("_", " ")
        if not any(k in norm for k in keys):
            continue
        pts = 10
        for marker in dl.STRONG:
            if marker in low:
                pts += 40
        ranked.append((pts, cand))
    ranked.sort(key=lambda p: -p[0])
    return [c for _, c in ranked]


def main() -> None:
    mapping = json.load(open(dl.RESULT_FILE)) if os.path.exists(dl.RESULT_FILE) else {}
    still: list[str] = []
    for dino_id, title in MANUAL_TITLES.items():
        if any(os.path.exists(os.path.join(dl.OUT_DIR, dino_id + e)) for e in (".jpg", ".png")):
            continue
        keys = EXTRA.get(dino_id) or [w.lower() for w in title.replace("(dinosaur)", "").split() if len(w) > 3]
        cands = dl.list_article_images(title)
        ranked = relaxed_rank(cands, keys)
        if not ranked:
            for phrase in (f"{title} restoration", f"{title} life", title):
                ranked = relaxed_rank(dl.commons_search(phrase), keys)
                if ranked:
                    break
        saved = False
        for cand in ranked[:5]:
            url = dl.file_url(cand)
            if not url:
                continue
            ext = ".png" if url.lower().split("?")[0].endswith(".png") else ".jpg"
            path = os.path.join(dl.OUT_DIR, dino_id + ext)
            if dl.download(url, path):
                mapping[dino_id] = dino_id + ext
                print(f"OK   {dino_id} <- {cand}", flush=True)
                saved = True
                break
        if not saved:
            still.append(dino_id)
            print(f"MISS {dino_id} ({title})", flush=True)
        time.sleep(1.2)

    json.dump(mapping, open(dl.RESULT_FILE, "w"), indent=1)
    print(f"\nstill missing ({len(still)}): {still}")


if __name__ == "__main__":
    main()
