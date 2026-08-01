#!/usr/bin/env python3
"""Pass 5: fill in life ("alive") artwork for the entries that still have none.

Earlier passes required an explicit palaeoart marker in the filename, which
dropped 38 entries. This pass widens the candidate pool (Commons categories,
several search phrasings, the Wikipedia lead image) while keeping a strict
bones/diagram/photo-of-a-mount reject list. Results are visually audited after
the run, so anything wrong here is replaced by hand.
"""

import importlib.util
import json
import os
import time
import urllib.parse

spec = importlib.util.spec_from_file_location("dl", "tmp/download_dino_life_images.py")
dl = importlib.util.module_from_spec(spec)
spec.loader.exec_module(dl)

# id -> (search phrase, filename tokens that must appear)
TARGETS = {
    "eoraptor": ("Eoraptor", ["eoraptor"]),
    "plateosaurus": ("Plateosaurus", ["plateosaurus"]),
    "lesothosaurus": ("Lesothosaurus", ["lesothosaurus"]),
    "camarasaurus": ("Camarasaurus", ["camarasaurus"]),
    "nodosaurus": ("Nodosaurus", ["nodosaurus"]),
    "citipati": ("Citipati", ["citipati"]),
    "saichania": ("Saichania", ["saichania"]),
    "dreadnoughtus": ("Dreadnoughtus", ["dreadnoughtus"]),
    "puertasaurus": ("Puertasaurus", ["puertasaurus"]),
    "dakotaraptor": ("Dakotaraptor", ["dakotaraptor"]),
    "muttaburrasaurus": ("Muttaburrasaurus", ["muttaburrasaurus"]),
    "Epidexipteryx": ("Epidexipteryx", ["epidexipteryx"]),
    "monoclonius": ("Monoclonius", ["monoclonius", "centrosaurus"]),
    "megaraptor": ("Megaraptor", ["megaraptor"]),
    "noasaurus": ("Noasaurus", ["noasaurus"]),
    "pteranodon": ("Pteranodon", ["pteranodon"]),
    "dimorphodon": ("Dimorphodon", ["dimorphodon"]),
    "tapejara": ("Tapejara", ["tapejara"]),
    "liopleurodon": ("Liopleurodon", ["liopleurodon"]),
    "paraceratherium": ("Paraceratherium", ["paraceratherium", "indricotherium"]),
    "kelenken": ("Kelenken", ["kelenken"]),
    "argentavis": ("Argentavis", ["argentavis"]),
    "deinotherium": ("Deinotherium", ["deinotherium"]),
    "chalicotherium": ("Chalicotherium", ["chalicotherium"]),
    "pelagornis": ("Pelagornis", ["pelagornis"]),
    "woolly-rhino": ("Coelodonta antiquitatis", ["coelodonta"]),
    "cave-lion": ("Panthera spelaea", ["spelaea", "cave lion"]),
    "giant-beaver": ("Castoroides", ["castoroides"]),
    "megaloceros": ("Megaloceros giganteus", ["megaloceros"]),
    "steppe-bison": ("Bison priscus", ["priscus", "steppe bison"]),
    "bison-antiquus": ("Bison antiquus", ["antiquus"]),
    "camelops": ("Camelops", ["camelops"]),
    "hagerman-horse": ("Equus simplicidens", ["simplicidens"]),
    "giant-horse": ("Equus giganteus", ["giganteus", "equus"]),
    "toxodon": ("Toxodon", ["toxodon"]),
    "eremotherium": ("Eremotherium", ["eremotherium"]),
    "thylacine": ("Thylacinus cynocephalus", ["thylacin"]),
    "great-auk": ("Pinguinus impennis great auk", ["pinguinus", "auk"]),
}

HARD_REJECT = (
    "skeleton", "skeletal", "squelette", "esqueleto", "skull", "crani", "cranium",
    "maxilla", "mandible", "jaw", "tusk", "tooth", "teeth", "bone", "vertebra",
    "femur", "humerus", "pelvis", "fossil", "mhnt", "museum", "mount", "specimen",
    "holotype", "cast", "comparison", "diagram", "scale", " size", "map",
    "footprint", "track", "claw", "taxidermy", "stuffed", "egg", "embryo", "nest",
    "anatomy", "chart", "engraving", "exhibit", "gallery", "display", "quarry",
    "cladogram", "phylogen", "logo", "icon", "svg", "coprolite", "locator",
    "distribution", "range", "stratigraph", "timeline", "sketch of skull",
)

SOFT_BONUS = (
    "_nt.", "_nt_", "restoration", "reconstruction", "life", "paleoart",
    "palaeoart", "nobu", "tamura", "durbed", "bogdanov", "dibgd", "hartman",
    "artwork", "illustration", "render", "model", "_db.", "_bw.", "dmitry",
    "mauricio", "anton", "wild", "zoo", "recreation", "reconstitution",
)


def category_files(category: str) -> list[str]:
    try:
        data = dl.api("commons.wikimedia.org", {
            "action": "query", "list": "categorymembers",
            "cmtitle": f"Category:{category}", "cmtype": "file", "cmlimit": "200",
        })
    except Exception:
        return []
    return [m["title"] for m in data.get("query", {}).get("categorymembers", []) or []]


def lead_image(title: str) -> list[str]:
    try:
        data = dl.api("en.wikipedia.org", {
            "action": "query", "titles": title, "prop": "pageimages",
            "piprop": "original", "redirects": "1",
        })
    except Exception:
        return []
    out = []
    for page in data.get("query", {}).get("pages", {}).values():
        original = page.get("original", {}).get("source")
        if original:
            name = urllib.parse.unquote(original.rsplit("/", 1)[-1])
            out.append("File:" + name)
    return out


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
        pts = 10 + sum(20 for marker in SOFT_BONUS if marker in norm)
        out.append((pts, cand))
    out.sort(key=lambda p: -p[0])
    return [c for _, c in dict.fromkeys(out)]


def main() -> None:
    mapping_path = "/tmp/dino_life_pass5.json"
    mapping: dict[str, str] = {}
    ok: list[str] = []
    failed: list[str] = []

    for dino_id, (phrase, keys) in TARGETS.items():
        genus = phrase.split()[0]
        cands: list[str] = []
        cands += lead_image(phrase)
        cands += category_files(genus)
        for query in (
            f"{phrase} restoration", f"{phrase} reconstruction",
            f"{phrase} life", f"{phrase} paleoart", phrase,
        ):
            try:
                res = dl.api("commons.wikimedia.org", {
                    "action": "query", "list": "search", "srsearch": query,
                    "srnamespace": "6", "srlimit": "40",
                })
                cands += [r["title"] for r in res.get("query", {}).get("search", []) or []]
            except Exception:
                pass
            time.sleep(0.25)
        cands += dl.list_article_images(phrase)

        ranked = rank(list(dict.fromkeys(cands)), keys)
        saved = False
        for cand in ranked[:8]:
            url = dl.file_url(cand)
            if not url:
                continue
            ext = ".png" if url.lower().split("?")[0].endswith(".png") else ".jpg"
            tmp_path = os.path.join(dl.OUT_DIR, dino_id + ".tmp")
            if dl.download(url, tmp_path):
                os.replace(tmp_path, os.path.join(dl.OUT_DIR, dino_id + ext))
                mapping[dino_id] = dino_id + ext
                print(f"OK   {dino_id} <- {cand}", flush=True)
                ok.append(dino_id)
                saved = True
                break
        if not saved:
            failed.append(dino_id)
            print(f"MISS {dino_id} (candidates: {len(ranked)})", flush=True)
        time.sleep(0.4)

    json.dump(mapping, open(mapping_path, "w"), indent=1)
    print(f"\nsaved {len(ok)}, missing {len(failed)}: {failed}")


if __name__ == "__main__":
    main()
