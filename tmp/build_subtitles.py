#!/usr/bin/env python3
"""Build the English subtitle track from the timing map, then translate it into
the other 14 subtitle languages. Cues are split at clause boundaries so no
caption is longer than two comfortable lines, and each split keeps its share of
the spoken line's time.
"""
from __future__ import annotations

import json
import os
import re
import time
import urllib.parse
import urllib.request
from concurrent.futures import ThreadPoolExecutor

OUT = "/tmp/rockscout_video"
SUBS = os.path.join(OUT, "subs")
CACHE = os.path.join(OUT, "translation_cache.json")

MAX_CUE_CHARS = 92
LINE_CHARS = 42

# (srt/mkv language tag, Google translate code, display name)
LANGUAGES = [
    ("eng", None, "English"),
    ("spa", "es", "Espanol"),
    ("fra", "fr", "Francais"),
    ("deu", "de", "Deutsch"),
    ("por", "pt", "Portugues"),
    ("zho", "zh-CN", "Chinese"),
    ("ara", "ar", "Arabic"),
    ("hin", "hi", "Hindi"),
    ("ind", "id", "Indonesia"),
    ("jpn", "ja", "Japanese"),
    ("fil", "tl", "Filipino"),
    ("vie", "vi", "Vietnamese"),
    ("rus", "ru", "Russian"),
    ("pol", "pl", "Polski"),
    ("ita", "it", "Italiano"),
]


def split_cue(text: str) -> list[str]:
    """Split a spoken sentence into caption-sized chunks at clause boundaries."""
    if len(text) <= MAX_CUE_CHARS:
        return [text]
    parts = re.split(r"(?<=[,;:—])\s+", text)
    chunks: list[str] = []
    cur = ""
    for part in parts:
        cand = f"{cur} {part}".strip()
        if len(cand) <= MAX_CUE_CHARS or not cur:
            cur = cand
        else:
            chunks.append(cur)
            cur = part
    if cur:
        chunks.append(cur)
    # any chunk still too long gets broken on word boundaries
    final: list[str] = []
    for chunk in chunks:
        while len(chunk) > MAX_CUE_CHARS:
            cut = chunk.rfind(" ", 0, MAX_CUE_CHARS)
            if cut <= 0:
                break
            final.append(chunk[:cut])
            chunk = chunk[cut + 1:]
        if chunk:
            final.append(chunk)
    return final


def wrap(text: str, width: int = LINE_CHARS) -> str:
    if len(text) <= width:
        return text
    words = text.split()
    lines: list[str] = []
    cur = ""
    for w in words:
        cand = f"{cur} {w}".strip()
        if len(cand) <= width or not cur:
            cur = cand
        else:
            lines.append(cur)
            cur = w
    if cur:
        lines.append(cur)
    if len(lines) > 2:  # keep captions to two lines, rebalance
        joined = " ".join(lines)
        mid = len(joined) // 2
        cut = joined.rfind(" ", 0, mid + 12)
        if cut > 0:
            lines = [joined[:cut], joined[cut + 1:]]
    return "\n".join(lines[:2])


def stamp(sec: float) -> str:
    sec = max(0.0, sec)
    h = int(sec // 3600)
    sec -= h * 3600
    m = int(sec // 60)
    sec -= m * 60
    s = int(sec)
    ms = int(round((sec - s) * 1000))
    if ms == 1000:
        s, ms = s + 1, 0
    return f"{h:02d}:{m:02d}:{s:02d},{ms:03d}"


def build_cue_list(timing: dict) -> list[dict]:
    cues: list[dict] = []
    for cue in timing["cues"]:
        chunks = split_cue(cue["text"])
        total = sum(len(c) for c in chunks) or 1
        span = max(cue["end"] - cue["start"], 0.6)
        t = cue["start"]
        for chunk in chunks:
            d = span * len(chunk) / total
            cues.append({
                "start": round(t, 3),
                "end": round(t + d, 3),
                "text": chunk,
                "chapter": cue["chapter"],
            })
            t += d
    # enforce a readable minimum and no overlap
    for i, cue in enumerate(cues):
        if cue["end"] - cue["start"] < 0.7:
            cue["end"] = cue["start"] + 0.7
        if i + 1 < len(cues) and cue["end"] > cues[i + 1]["start"]:
            cue["end"] = max(cue["start"] + 0.5, cues[i + 1]["start"] - 0.04)
    return cues


def translate(text: str, target: str, attempts: int = 4) -> str:
    params = urllib.parse.urlencode({
        "client": "gtx", "sl": "en", "tl": target, "dt": "t", "q": text,
    })
    url = f"https://translate.googleapis.com/translate_a/single?{params}"
    for attempt in range(attempts):
        try:
            req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
            with urllib.request.urlopen(req, timeout=25) as resp:
                data = json.loads(resp.read().decode("utf-8"))
            return "".join(seg[0] for seg in data[0] if seg and seg[0]).strip()
        except Exception:
            time.sleep(0.6 * (attempt + 1))
    return ""


def write_srt(path: str, cues: list[dict], texts: list[str]) -> None:
    blocks = []
    for i, (cue, text) in enumerate(zip(cues, texts), start=1):
        blocks.append(f"{i}\n{stamp(cue['start'])} --> {stamp(cue['end'])}\n{wrap(text)}")
    with open(path, "w", encoding="utf-8") as fh:
        fh.write("\n\n".join(blocks) + "\n")


def main() -> None:
    os.makedirs(SUBS, exist_ok=True)
    with open(os.path.join(OUT, "timing.json"), encoding="utf-8") as fh:
        timing = json.load(fh)
    cues = build_cue_list(timing)
    print(f"cues: {len(cues)} (from {len(timing['cues'])} spoken lines)", flush=True)

    english = [c["text"] for c in cues]
    write_srt(os.path.join(SUBS, "eng.srt"), cues, english)

    cache: dict[str, dict[str, str]] = {}
    if os.path.exists(CACHE):
        with open(CACHE, encoding="utf-8") as fh:
            cache = json.load(fh)

    for tag, code, name in LANGUAGES:
        if code is None:
            continue
        path = os.path.join(SUBS, f"{tag}.srt")
        store = cache.setdefault(code, {})
        todo = [t for t in english if t not in store]
        if todo:
            with ThreadPoolExecutor(max_workers=6) as pool:
                results = list(pool.map(lambda t: (t, translate(t, code)), todo))
            for src, dst in results:
                if dst:
                    store[src] = dst
            with open(CACHE, "w", encoding="utf-8") as fh:
                json.dump(cache, fh, ensure_ascii=False)
        translated = [store.get(t) or t for t in english]
        missing = sum(1 for t in english if not store.get(t))
        write_srt(path, cues, translated)
        print(f"  {tag} ({name}): {len(translated)} cues, {missing} untranslated", flush=True)

    print("subtitles written to", SUBS, flush=True)


if __name__ == "__main__":
    main()
