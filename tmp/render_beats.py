#!/usr/bin/env python3
"""Render the tutorial video's visuals: one clip per camera beat, then concat.

Each beat is a slow Ken Burns move from one focus target to another on a single
screen. Beats flagged with a tap get an animated finger press with a ripple at
the target, and beats that introduce a new screen slide that screen in over the
previous frame.
"""
from __future__ import annotations

import json
import os
import subprocess
import sys
from concurrent.futures import ThreadPoolExecutor

OUT = "/tmp/rockscout_video"
BASES = os.path.join(OUT, "bases")
TAPS = os.path.join(OUT, "tap")
CLIPS = os.path.join(OUT, "clips")

FPS = 30
W, H = 1080, 1920
BASE_W, BASE_H = 3024, 5376
SHOT_W = 2484                       # screenshot width inside the padded canvas
X_OFF = (BASE_W - SHOT_W) / 2       # side fill offset
ZOOM_CAP = 1.90
TAP_SIZE = 420
TAP_LEN = 42 / FPS
SLIDE = 0.42
WORKERS = 6

VENC = ["-c:v", "libx264", "-preset", "veryfast", "-crf", "22",
        "-pix_fmt", "yuv420p", "-r", str(FPS), "-g", "60"]


def to_canvas(cx: float, cy: float) -> tuple[float, float]:
    """Screenshot-normalized point -> padded-canvas-normalized point."""
    return (X_OFF + cx * SHOT_W) / BASE_W, cy


def clamp(v: float, lo: float, hi: float) -> float:
    return max(lo, min(hi, v))


def camera_state(bx: float, by: float, z: float) -> tuple[float, float, float]:
    """Window origin (canvas px) and output scale for a static camera."""
    z = min(z, ZOOM_CAP)
    win_w = BASE_W / z
    win_h = BASE_H / z
    x = clamp(bx * BASE_W - win_w / 2, 0, BASE_W - win_w)
    y = clamp(by * BASE_H - win_h / 2, 0, BASE_H - win_h)
    return x, y, W / win_w


def ease(var: str) -> str:
    """Smoothstep easing expression on a 0..1 progress variable."""
    return f"(3*pow({var},2)-2*pow({var},3))"


def build_body(beat: dict, path: str) -> list[str]:
    dur = beat["duration"]
    frames = max(2, int(round(dur * FPS)))
    fx0, fy0, fz0 = beat["from"]
    fx1, fy1, fz1 = beat["to"]
    bx0, by0 = to_canvas(fx0, fy0)
    bx1, by1 = to_canvas(fx1, fy1)
    z0, z1 = min(fz0, ZOOM_CAP), min(fz1, ZOOM_CAP)
    if abs(z0 - z1) < 0.02 and abs(bx0 - bx1) < 0.002 and abs(by0 - by1) < 0.002:
        z1 = min(z0 * 1.08, ZOOM_CAP)   # never let a beat sit perfectly still

    e = ease(f"(on/{frames})")
    z_expr = f"{z0:.4f}+({z1 - z0:.4f})*{e}"
    cx_expr = f"{bx0:.5f}+({bx1 - bx0:.5f})*{e}"
    cy_expr = f"{by0:.5f}+({by1 - by0:.5f})*{e}"
    x_expr = f"max(0\\,min(iw-iw/zoom\\,({cx_expr})*iw-(iw/zoom)/2))"
    y_expr = f"max(0\\,min(ih-ih/zoom\\,({cy_expr})*ih-(ih/zoom)/2))"

    zoompan = (f"zoompan=z='{z_expr}':x='{x_expr}':y='{y_expr}':"
               f"d=1:s={W}x{H}:fps={FPS}")

    base = os.path.join(BASES, beat["shot"].replace(".png", ".jpg"))
    cmd = ["ffmpeg", "-y", "-framerate", str(FPS), "-loop", "1",
           "-t", f"{dur:.3f}", "-i", base]

    if beat["tap"] and dur >= 2.2:
        tap_at = max(0.55, min(dur - TAP_LEN - 0.2, dur * 0.52))
        tx, ty = to_canvas(*beat["tap_at"])
        wx, wy, scale = camera_state(tx, ty, beat["to"][2])
        px = (tx * BASE_W - wx) * scale - TAP_SIZE / 2
        py = (ty * BASE_H - wy) * scale - TAP_SIZE / 2
        px = clamp(px, -TAP_SIZE * 0.3, W - TAP_SIZE * 0.7)
        py = clamp(py, -TAP_SIZE * 0.3, H - TAP_SIZE * 0.7)
        cmd += ["-framerate", str(FPS), "-i", os.path.join(TAPS, "tap_%03d.png")]
        fc = (f"[0:v]{zoompan}[bg];"
              f"[1:v]format=rgba,setpts=PTS+{tap_at:.3f}/TB[tp];"
              f"[bg][tp]overlay=x={px:.0f}:y={py:.0f}:eof_action=pass:"
              f"format=auto[v]")
        cmd += ["-filter_complex", fc, "-map", "[v]"]
    else:
        cmd += ["-vf", zoompan]

    cmd += ["-t", f"{dur:.3f}"] + VENC + [path]
    return cmd


def build_slide(prev_frame: str, body: str, dur: float, path: str) -> list[str]:
    e = ease(f"(t/{SLIDE})")
    x_expr = f"if(lt(t,{SLIDE}),W*(1-{e}),0)"
    fc = (f"[0:v]scale={W}:{H}[bgs];"
          f"[bgs][1:v]overlay=x='{x_expr}':y=0:shortest=1[v]")
    return (["ffmpeg", "-y", "-framerate", str(FPS), "-loop", "1",
             "-i", prev_frame, "-i", body,
             "-filter_complex", fc, "-map", "[v]", "-t", f"{dur:.3f}"]
            + VENC + [path])


def run(cmd: list[str]) -> tuple[int, str]:
    proc = subprocess.run(cmd, capture_output=True)
    return proc.returncode, proc.stderr.decode()[-600:]


def main() -> None:
    os.makedirs(CLIPS, exist_ok=True)
    with open(os.path.join(OUT, "timing.json"), encoding="utf-8") as fh:
        data = json.load(fh)
    beats = data["beats"]

    # phase 1: bodies
    jobs = []
    for i, beat in enumerate(beats):
        body = os.path.join(CLIPS, f"body_{i:03d}.mp4")
        if os.path.exists(body) and os.path.getsize(body) > 20000:
            continue
        jobs.append((i, build_body(beat, body)))
    print(f"phase 1: rendering {len(jobs)} beat bodies", flush=True)
    failures = 0
    with ThreadPoolExecutor(max_workers=WORKERS) as pool:
        for (i, _cmd), (code, err) in zip(jobs, pool.map(lambda j: run(j[1]), jobs)):
            if code != 0:
                failures += 1
                print(f"  FAIL body {i}: {err}", flush=True)
    if failures:
        print(f"phase 1 failures: {failures}", flush=True)
        sys.exit(1)
    print("phase 1 done", flush=True)

    # phase 2: last frame of the clip preceding each slide-in
    need_frames = {i - 1 for i, b in enumerate(beats) if b["slide_in"] and i > 0}
    for i in sorted(need_frames):
        frame = os.path.join(CLIPS, f"last_{i:03d}.png")
        if os.path.exists(frame):
            continue
        body = os.path.join(CLIPS, f"body_{i:03d}.mp4")
        run(["ffmpeg", "-y", "-sseof", "-0.1", "-i", body, "-frames:v", "1",
             "-update", "1", frame])
    print(f"phase 2 done ({len(need_frames)} handoff frames)", flush=True)

    # phase 3: slide-in passes
    jobs = []
    for i, beat in enumerate(beats):
        final = os.path.join(CLIPS, f"clip_{i:03d}.mp4")
        body = os.path.join(CLIPS, f"body_{i:03d}.mp4")
        if os.path.exists(final) and os.path.getsize(final) > 20000:
            continue
        if beat["slide_in"] and i > 0:
            prev_frame = os.path.join(CLIPS, f"last_{i - 1:03d}.png")
            if os.path.exists(prev_frame):
                jobs.append((i, build_slide(prev_frame, body, beat["duration"], final)))
                continue
        os.replace(body, final)
    print(f"phase 3: {len(jobs)} slide transitions", flush=True)
    with ThreadPoolExecutor(max_workers=WORKERS) as pool:
        for (i, _cmd), (code, err) in zip(jobs, pool.map(lambda j: run(j[1]), jobs)):
            if code != 0:
                print(f"  FAIL slide {i}: {err}", flush=True)
    print("phase 3 done", flush=True)

    # phase 4: concat
    listfile = os.path.join(OUT, "clip_list.txt")
    with open(listfile, "w", encoding="utf-8") as fh:
        for i in range(len(beats)):
            fh.write(f"file '{os.path.join(CLIPS, f'clip_{i:03d}.mp4')}'\n")
    out = os.path.join(OUT, "video_full.mp4")
    code, err = run(["ffmpeg", "-y", "-f", "concat", "-safe", "0", "-i", listfile,
                     "-c", "copy", out])
    if code != 0:
        print(f"concat failed: {err}", flush=True)
        sys.exit(1)
    dur = subprocess.check_output(
        ["ffprobe", "-v", "quiet", "-show_entries", "format=duration",
         "-of", "csv=p=0", out]).decode().strip()
    print(f"video_full.mp4 duration={dur}s target={data['total_duration']}s", flush=True)


if __name__ == "__main__":
    main()
