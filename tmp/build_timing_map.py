#!/usr/bin/env python3
"""Build a sentence-accurate timing map for the RockScout tutorial video.

Outputs /tmp/rockscout_video/timing.json with:
  - chapters: start/duration in the final timeline
  - cues:     sentence-level subtitle cues (absolute seconds)
  - beats:    render instructions (screenshot, camera move, tap, transition)

Sentence timings come from ElevenLabs Scribe word timestamps where available
(/tmp/scribe/chNN.json), otherwise from a weighted estimate calibrated on the
measured speech rate of the transcribed chapters.
"""
from __future__ import annotations

import json
import os
import re
import subprocess

ROOT = "/home/user/rork-app"
NARR = "/tmp/narration_final"
SCRIBE = "/tmp/scribe"
OUT_DIR = "/tmp/rockscout_video"
SCRIPT = os.path.join(ROOT, "web", "rockscout_tutorial_script.md")

CHAPTER_TITLES = [
    "Welcome",
    "AI Rock ID",
    "Your Collection",
    "Field Tools",
    "Dig Sites & Gem Shows",
    "Trip Planning",
    "Trading & Community",
    "Social",
    "Aurora & Night Sky",
    "Your Profile",
    "Reference Library",
    "Artifacts & Wonders",
    "Field Kit",
    "Learn & Explore",
    "Premium & Free Tier",
    "Outro",
]

# ---------------------------------------------------------------- focus presets
# (cx, cy, zoom) in normalized screenshot coordinates. Screens are 828x1792.
F = {
    "wide":      (0.50, 0.45, 1.10),
    "header":    (0.50, 0.07, 1.75),
    "hero":      (0.50, 0.21, 1.85),
    "hero_btn":  (0.50, 0.30, 2.10),
    "card2":     (0.50, 0.42, 1.80),
    "card3":     (0.50, 0.62, 1.80),
    "card4":     (0.50, 0.82, 1.80),
    "upper":     (0.50, 0.22, 1.45),
    "mid":       (0.50, 0.50, 1.45),
    "lower":     (0.50, 0.78, 1.45),
    "nav":       (0.50, 0.965, 2.30),
    "search":    (0.50, 0.115, 2.00),
    "chips":     (0.50, 0.175, 2.00),
    "post_btn":  (0.86, 0.055, 2.40),
    "filter_btn":(0.86, 0.045, 2.40),
    "tabs":      (0.50, 0.145, 2.10),
    "map":       (0.50, 0.33, 1.55),
    "list1":     (0.50, 0.52, 1.70),
    "list2":     (0.50, 0.70, 1.70),
    "grid":      (0.50, 0.40, 1.50),
    "grid_low":  (0.50, 0.72, 1.50),
    "price":     (0.50, 0.20, 1.90),
    "bullets":   (0.50, 0.45, 1.55),
}

# beats: (screenshot, focus-from, focus-to, tap?, weight)
# focus-to == focus-from means a slow push instead of a drift.
CH_BEATS: dict[int, list[tuple[str, str, str, bool, float]]] = {
    # NOTE: for every captured pair, "X.png" is the scrolled-DOWN frame and
    # "X_scrolled.png" is the scrolled-back-to-top frame.
    1: [
        ("01_home_scrolled.png", "wide", "hero", False, 1.0),
        ("01_home_scrolled.png", "hero", "hero_btn", False, 1.0),
        ("01_home.png", "upper", "lower", False, 1.0),
        ("01_home_scrolled.png", "hero", "wide", False, 0.8),
    ],
    2: [
        ("01_home_scrolled.png", "wide", "hero_btn", True, 1.1),
        ("02_identify.png", "header", "mid", False, 1.0),
        ("02_identify.png", "mid", "lower", True, 1.0),
        ("03_scan.png", "wide", "mid", False, 0.9),
        ("02_identify.png", "upper", "mid", False, 1.0),
        ("02_identify.png", "mid", "mid", False, 1.0),
        ("06_specimen_detail_scrolled.png", "hero", "mid", False, 1.1),
        ("06_specimen_detail.png", "upper", "lower", False, 1.0),
        ("04_pdf_report.png", "header", "mid", True, 1.1),
        ("46_paywall.png", "price", "mid", False, 0.9),
    ],
    3: [
        ("05_collection_scrolled.png", "header", "upper", False, 1.0),
        ("05_collection_scrolled.png", "search", "list1", False, 1.0),
        ("05_collection.png", "upper", "lower", False, 1.0),
        ("06_specimen_detail_scrolled.png", "hero", "mid", True, 1.0),
        ("06_specimen_detail.png", "mid", "lower", False, 0.9),
        ("07_saved_images.png", "header", "mid", False, 1.0),
    ],
    4: [
        ("08_field_captures.png", "header", "mid", False, 1.1),
        ("08_field_captures.png", "mid", "lower", False, 1.0),
        ("11_dig_sites_map.png", "map", "list1", False, 1.0),
        ("01_home.png", "mid", "lower", True, 1.1),
        ("10_upload_pill.png", "header", "mid", False, 1.0),
    ],
    5: [
        ("01_home.png", "upper", "header", True, 0.9),
        ("11_dig_sites_map.png", "header", "map", False, 1.0),
        ("11_dig_sites_map.png", "filter_btn", "map", False, 1.0),
        ("11_dig_sites_map.png", "list1", "list2", False, 1.0),
        ("12_gem_shows.png", "header", "list1", False, 1.0),
    ],
    6: [
        ("13_trip_planner.png", "header", "hero", False, 1.0),
        ("13_trip_planner.png", "post_btn", "mid", True, 1.0),
        ("13_trip_planner.png", "mid", "lower", False, 1.0),
        ("11_dig_sites_map.png", "map", "map", False, 1.0),
        ("14_archived_trips.png", "header", "mid", False, 1.0),
        ("15_field_journal.png", "header", "mid", False, 1.0),
    ],
    7: [
        ("16_trade_board.png", "header", "upper", False, 1.0),
        ("16_trade_board.png", "post_btn", "chips", True, 1.0),
        ("17_trading_floor.png", "header", "mid", False, 1.0),
        ("18_my_trades.png", "header", "mid", False, 0.9),
        ("19_community_scrolled.png", "header", "post_btn", True, 1.0),
        ("19_community.png", "upper", "lower", False, 1.0),
    ],
    8: [
        ("20_rockscouts_map.png", "header", "mid", False, 1.0),
        ("21_friends.png", "header", "tabs", False, 1.0),
        ("22_discover_hunters.png", "header", "mid", False, 1.0),
        ("21_friends.png", "tabs", "mid", False, 1.0),
        ("21_friends.png", "mid", "lower", False, 1.0),
        ("21_friends.png", "header", "header", False, 0.8),
    ],
    9: [
        ("23_aurora.png", "header", "upper", False, 1.0),
        ("23_aurora.png", "upper", "mid", False, 1.0),
        ("23_aurora.png", "mid", "lower", False, 1.0),
        ("24_stars_landing.png", "header", "grid", True, 1.0),
        ("25_constellations.png", "header", "mid", False, 1.0),
        ("25_constellations.png", "mid", "lower", False, 0.9),
    ],
    10: [
        ("26_profile.png", "header", "hero", True, 1.0),
        ("26_profile.png", "hero", "mid", False, 1.0),
        ("27_achievements_scrolled.png", "header", "upper", False, 1.0),
        ("27_achievements.png", "upper", "lower", False, 1.0),
    ],
    11: [
        ("28_periodic_table.png", "header", "grid", False, 1.0),
        ("28_periodic_table.png", "grid", "grid_low", False, 1.0),
        ("29_specimen_database_scrolled.png", "header", "grid", False, 1.0),
        ("29_specimen_database.png", "upper", "lower", False, 1.0),
        ("30_search.png", "search", "mid", True, 1.0),
    ],
    12: [
        ("31_artifacts.png", "header", "chips", False, 1.0),
        ("31_artifacts.png", "chips", "grid", False, 1.0),
        ("32_natural_wonders.png", "header", "upper", False, 1.0),
        ("32_natural_wonders.png", "mid", "lower", False, 1.0),
    ],
    13: [
        ("33_blm_guide.png", "header", "upper", False, 1.0),
        ("33_blm_guide.png", "mid", "lower", False, 1.0),
        ("34_state_parks.png", "header", "list1", False, 1.0),
        ("35_meteorite_hunting.png", "header", "mid", False, 1.0),
        ("36_gear_guide_scrolled.png", "header", "chips", False, 1.0),
        ("36_gear_guide.png", "upper", "lower", False, 1.0),
    ],
    14: [
        ("38_reference_hub.png", "header", "upper", False, 1.0),
        ("42_rock_cycle.png", "header", "mid", False, 1.0),
        ("41_rock_types.png", "upper", "lower", False, 1.0),
        ("44_mineral_id.png", "header", "mid", False, 1.0),
        ("39_crystal_systems.png", "header", "mid", False, 1.0),
        ("40_fluorescence.png", "upper", "lower", False, 1.0),
        ("43_glossary.png", "search", "list1", False, 1.0),
        ("45_lapidary.png", "header", "mid", False, 1.0),
        ("38_reference_hub.png", "mid", "lower", False, 0.9),
    ],
    15: [
        ("46_paywall.png", "wide", "price", False, 1.0),
        ("46_paywall.png", "price", "bullets", False, 1.0),
        ("29_specimen_database_scrolled.png", "header", "grid", False, 1.0),
        ("37_resource_links.png", "upper", "lower", False, 1.0),
        ("46_paywall.png", "bullets", "bullets", False, 1.0),
        ("46_paywall.png", "price", "price", True, 1.0),
        ("46_paywall.png", "bullets", "lower", False, 0.9),
    ],
    16: [
        ("01_home_scrolled.png", "wide", "hero", False, 1.0),
        ("38_reference_hub.png", "upper", "mid", False, 0.8),
        ("01_home_scrolled.png", "hero", "wide", False, 1.0),
    ],
}


def parse_script() -> list[list[str]]:
    """Return a list of sentences per chapter, in order."""
    with open(SCRIPT, encoding="utf-8") as fh:
        raw = fh.read()
    parts = re.split(r"^## Chapter \d+[^\n]*$", raw, flags=re.M)[1:]
    chapters: list[list[str]] = []
    for part in parts:
        body = part.split("---")[0]
        text = " ".join(
            line.strip() for line in body.strip().splitlines() if line.strip()
        )
        sentences = [s.strip() for s in re.split(r"(?<=[.!?])\s+", text) if s.strip()]
        chapters.append(sentences)
    assert len(chapters) == 16, f"expected 16 chapters, got {len(chapters)}"
    return chapters


def words_of(sentence: str) -> list[str]:
    return re.findall(r"[A-Za-z0-9']+", sentence.lower())


def audio_duration(path: str) -> float:
    out = subprocess.check_output(
        ["ffprobe", "-v", "quiet", "-show_entries", "format=duration",
         "-of", "csv=p=0", path]
    )
    return float(out.decode().strip())


def scribe_words(idx: int) -> list[tuple[str, float, float]] | None:
    path = os.path.join(SCRIBE, f"ch{idx:02d}.json")
    if not os.path.exists(path):
        return None
    try:
        with open(path, encoding="utf-8") as fh:
            data = json.load(fh)
    except json.JSONDecodeError:
        return None
    words = [
        (re.sub(r"[^a-z0-9']", "", w.get("text", "").lower()),
         float(w.get("start", 0.0)), float(w.get("end", 0.0)))
        for w in data.get("words", [])
        if w.get("type") == "word"
    ]
    words = [w for w in words if w[0]]
    return words or None


def weight_of(sentence: str) -> float:
    """Estimated speaking weight: characters plus pause bonuses."""
    w = len(sentence)
    w += 9 * sentence.count(",")
    w += 14 * len(re.findall(r"[—:;]", sentence))
    w += 18  # end-of-sentence pause
    return float(w)


def sentence_times_estimated(sentences: list[str], duration: float) -> list[tuple[float, float]]:
    weights = [weight_of(s) for s in sentences]
    total = sum(weights)
    times: list[tuple[float, float]] = []
    t = 0.0
    for w in weights:
        d = duration * w / total
        times.append((t, t + d))
        t += d
    return times


def sentence_times_from_scribe(
    sentences: list[str], words: list[tuple[str, float, float]], duration: float
) -> list[tuple[float, float]]:
    """Walk transcript words sequentially, resyncing on the first word of each sentence."""
    times: list[tuple[float, float]] = []
    pos = 0
    n = len(words)
    for si, sentence in enumerate(sentences):
        sw = words_of(sentence)
        if not sw or pos >= n:
            break
        # resync: look ahead a few words for the sentence's opening word
        first = sw[0]
        for probe in range(pos, min(pos + 4, n)):
            if words[probe][0] == first:
                pos = probe
                break
        start = words[pos][1]  # start time
        end_idx = min(pos + len(sw) - 1, n - 1)
        end = words[end_idx][2]
        # next sentence begins where the following word begins
        pos = min(end_idx + 1, n)
        times.append((start, end))
    # fill any tail sentences that ran past the transcript
    while len(times) < len(sentences):
        last_end = times[-1][1] if times else 0.0
        times.append((last_end, min(last_end + 2.0, duration)))
    # monotonic + clamp
    fixed: list[tuple[float, float]] = []
    prev_end = 0.0
    for i, (s, e) in enumerate(times):
        s = max(s, prev_end)
        e = max(e, s + 0.4)
        if i == len(times) - 1:
            e = duration
        fixed.append((s, min(e, duration)))
        prev_end = fixed[-1][1]
    return fixed


def main() -> None:
    os.makedirs(OUT_DIR, exist_ok=True)
    chapters = parse_script()

    chapter_meta = []
    all_cues = []
    all_beats = []
    timeline = 0.0
    scribe_used = 0

    for ci, sentences in enumerate(chapters, start=1):
        mp3 = os.path.join(NARR, f"ch{ci:02d}.mp3")
        dur = audio_duration(mp3)
        words = scribe_words(ci)
        if words:
            times = sentence_times_from_scribe(sentences, words, dur)
            scribe_used += 1
            source = "scribe"
        else:
            times = sentence_times_estimated(sentences, dur)
            source = "estimated"

        chapter_meta.append({
            "index": ci,
            "title": CHAPTER_TITLES[ci - 1],
            "start": round(timeline, 3),
            "duration": round(dur, 3),
            "timing_source": source,
            "sentences": len(sentences),
        })

        # subtitle cues (absolute)
        for (s, e), text in zip(times, sentences):
            all_cues.append({
                "start": round(timeline + s, 3),
                "end": round(timeline + e, 3),
                "text": text,
                "chapter": ci,
            })

        # distribute sentences across the chapter's beats by weight
        beats = CH_BEATS[ci]
        bw = [b[4] for b in beats]
        total_w = sum(bw)
        n_sent = len(sentences)
        # cumulative sentence index boundaries
        bounds = [0]
        acc = 0.0
        for w in bw[:-1]:
            acc += w
            bounds.append(max(bounds[-1] + 1, round(n_sent * acc / total_w)))
        bounds.append(n_sent)
        # guarantee strictly increasing, every beat gets >= 1 sentence when possible
        for i in range(1, len(bounds)):
            if bounds[i] <= bounds[i - 1]:
                bounds[i] = min(bounds[i - 1] + 1, n_sent)
        for i in range(len(bounds) - 2, 0, -1):
            if bounds[i] > bounds[i + 1]:
                bounds[i] = bounds[i + 1]

        prev_shot = None
        for bi, (shot, f_from, f_to, tap, _w) in enumerate(beats):
            s_lo, s_hi = bounds[bi], bounds[bi + 1]
            if s_hi <= s_lo:
                b_start = times[min(s_lo, n_sent - 1)][0]
                b_end = b_start + 0.8
            else:
                b_start = times[s_lo][0]
                b_end = times[s_hi - 1][1]
            if bi == 0:
                b_start = 0.0
            if bi == len(beats) - 1:
                b_end = dur
            fx0, fy0, fz0 = F[f_from]
            fx1, fy1, fz1 = F[f_to]
            all_beats.append({
                "chapter": ci,
                "shot": shot,
                "start": round(timeline + b_start, 3),
                "duration": round(max(b_end - b_start, 1.2), 3),
                "from": [fx0, fy0, fz0],
                "to": [fx1, fy1, fz1],
                "tap": bool(tap),
                "tap_at": [fx1, fy1],
                "slide_in": prev_shot is not None and prev_shot != shot,
            })
            prev_shot = shot

        timeline += dur

    # stitch beat durations so they tile the timeline exactly
    for i, beat in enumerate(all_beats[:-1]):
        nxt = all_beats[i + 1]["start"]
        beat["duration"] = round(max(nxt - beat["start"], 1.0), 3)
    all_beats[-1]["duration"] = round(timeline - all_beats[-1]["start"], 3)

    data = {
        "total_duration": round(timeline, 3),
        "chapters": chapter_meta,
        "cues": all_cues,
        "beats": all_beats,
    }
    with open(os.path.join(OUT_DIR, "timing.json"), "w", encoding="utf-8") as fh:
        json.dump(data, fh, indent=1)

    print(f"total: {timeline:.1f}s ({timeline/60:.1f} min)")
    print(f"chapters with exact word timings: {scribe_used}/16")
    print(f"cues: {len(all_cues)}  beats: {len(all_beats)}")
    shortest = min(all_beats, key=lambda b: b["duration"])
    longest = max(all_beats, key=lambda b: b["duration"])
    print(f"beat duration range: {shortest['duration']}s ({shortest['shot']}) "
          f"to {longest['duration']}s ({longest['shot']})")
    for c in chapter_meta:
        print(f"  Ch{c['index']:2d} {c['title'][:22]:22s} start={c['start']:8.2f} "
              f"dur={c['duration']:7.2f} {c['timing_source']}")


if __name__ == "__main__":
    main()
