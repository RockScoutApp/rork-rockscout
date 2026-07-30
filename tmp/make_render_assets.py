#!/usr/bin/env python3
"""Prepare render assets for the tutorial video.

1. Base frames: each screenshot upscaled + sharpened, padded to a 9:16 canvas with
   a blurred version of itself behind it (so zoom-out never shows black bars).
2. Tap animation: a transparent PNG sequence of a finger pressing down with an
   expanding ripple and a glow ring.
"""
from __future__ import annotations

import math
import os

from PIL import Image, ImageDraw, ImageFilter, ImageEnhance

ROOT = "/home/user/rork-app"
SHOTS = os.path.join(ROOT, "web", "screenshots")
OUT = "/tmp/rockscout_video"
BASES = os.path.join(OUT, "bases")
TAPS = os.path.join(OUT, "tap")

BASE_SCALE = 3          # 828x1792 -> 2484x5376
OUT_AR = 1080 / 1920

TAP_FRAMES = 42         # 1.4s at 30fps
TAP_SIZE = 420          # canvas per tap frame
ACCENT = (245, 166, 35) # RockScout amber


def build_bases() -> int:
    os.makedirs(BASES, exist_ok=True)
    count = 0
    for name in sorted(os.listdir(SHOTS)):
        if not name.endswith(".png"):
            continue
        dst = os.path.join(BASES, name.replace(".png", ".jpg"))
        if os.path.exists(dst):
            count += 1
            continue
        img = Image.open(os.path.join(SHOTS, name)).convert("RGB")
        w, h = img.size
        bw, bh = w * BASE_SCALE, h * BASE_SCALE
        screen = img.resize((bw, bh), Image.LANCZOS)
        screen = screen.filter(ImageFilter.UnsharpMask(radius=2.4, percent=115, threshold=3))
        screen = ImageEnhance.Contrast(screen).enhance(1.04)

        canvas_w = int(round(bh * OUT_AR))
        if canvas_w <= bw:
            canvas_w = bw
        canvas = Image.new("RGB", (canvas_w, bh))
        # blurred, darkened fill behind the screen so zoom-outs still look designed
        fill = img.resize((canvas_w, bh), Image.LANCZOS).filter(ImageFilter.GaussianBox
                                                               if False else ImageFilter.GaussianBlur(90))
        fill = ImageEnhance.Brightness(fill).enhance(0.45)
        canvas.paste(fill, (0, 0))
        canvas.paste(screen, ((canvas_w - bw) // 2, 0))
        canvas.save(dst, quality=93, subsampling=1)
        count += 1
    return count


def finger(draw: ImageDraw.ImageDraw, cx: float, cy: float, press: float) -> None:
    """Draw a simple stylized fingertip pointing up-left at (cx, cy)."""
    lift = (1.0 - press) * 26
    tip_y = cy + lift
    # finger pad
    r = 34
    draw.ellipse([cx - r, tip_y - r, cx + r, tip_y + r], fill=(248, 244, 238, 235))
    # finger body angled down-right
    body = [
        (cx - r * 0.78, tip_y + r * 0.25),
        (cx + r * 0.78, tip_y + r * 0.25),
        (cx + r * 1.55, tip_y + r * 4.6),
        (cx + r * 0.15, tip_y + r * 4.6),
    ]
    draw.polygon(body, fill=(240, 234, 226, 225))
    # nail highlight
    draw.ellipse([cx - r * 0.5, tip_y - r * 0.62, cx + r * 0.5, tip_y + r * 0.12],
                 fill=(255, 255, 255, 190))


def build_tap_frames() -> int:
    os.makedirs(TAPS, exist_ok=True)
    if len(os.listdir(TAPS)) >= TAP_FRAMES:
        return len(os.listdir(TAPS))
    c = TAP_SIZE / 2
    for i in range(TAP_FRAMES):
        p = i / (TAP_FRAMES - 1)
        frame = Image.new("RGBA", (TAP_SIZE, TAP_SIZE), (0, 0, 0, 0))
        d = ImageDraw.Draw(frame)

        # press curve: approach (0-0.28), hold down (0.28-0.45), release (0.45-0.7)
        if p < 0.28:
            press = p / 0.28
        elif p < 0.45:
            press = 1.0
        elif p < 0.70:
            press = 1.0 - (p - 0.45) / 0.25
        else:
            press = 0.0

        # glow under the finger while pressed
        if press > 0.05:
            glow = Image.new("RGBA", (TAP_SIZE, TAP_SIZE), (0, 0, 0, 0))
            gd = ImageDraw.Draw(glow)
            gr = 62 + 10 * press
            gd.ellipse([c - gr, c - gr, c + gr, c + gr],
                       fill=ACCENT + (int(120 * press),))
            glow = glow.filter(ImageFilter.GaussianBlur(26))
            frame = Image.alpha_composite(frame, glow)
            d = ImageDraw.Draw(frame)

        # ripple rings launched at the moment of contact
        if p >= 0.28:
            for k, delay in enumerate((0.0, 0.09, 0.18)):
                rp = (p - 0.28 - delay) / 0.55
                if rp <= 0 or rp >= 1:
                    continue
                ease = 1 - (1 - rp) ** 2
                rad = 34 + ease * (168 - 34)
                alpha = int(210 * (1 - rp) ** 1.4)
                if alpha <= 2:
                    continue
                width = max(2, int(9 * (1 - rp)) + 2)
                ring = Image.new("RGBA", (TAP_SIZE, TAP_SIZE), (0, 0, 0, 0))
                rd = ImageDraw.Draw(ring)
                rd.ellipse([c - rad, c - rad, c + rad, c + rad],
                           outline=ACCENT + (alpha,), width=width)
                if k == 0:
                    rd.ellipse([c - rad, c - rad, c + rad, c + rad],
                               fill=ACCENT + (int(alpha * 0.13),))
                ring = ring.filter(ImageFilter.GaussianBlur(1.6))
                frame = Image.alpha_composite(frame, ring)
            d = ImageDraw.Draw(frame)

        # fade the whole overlay in and out
        if p < 0.06 or p > 0.86:
            fade = min(p / 0.06, (1.0 - p) / 0.14, 1.0)
            fade = max(0.0, fade)
        else:
            fade = 1.0

        finger(d, c + 6, c, press)

        if fade < 1.0:
            alpha = frame.split()[3].point(lambda v, f=fade: int(v * f))
            frame.putalpha(alpha)

        frame.save(os.path.join(TAPS, f"tap_{i:03d}.png"))
    return TAP_FRAMES


if __name__ == "__main__":
    n_bases = build_bases()
    n_taps = build_tap_frames()
    sample = sorted(os.listdir(BASES))[0]
    with Image.open(os.path.join(BASES, sample)) as im:
        size = im.size
    print(f"base frames: {n_bases} (e.g. {sample} {size[0]}x{size[1]})")
    print(f"tap frames: {n_taps} ({TAP_FRAMES / 30:.2f}s at 30fps)")
    print(f"math check ok: {math.isclose(size[0] / size[1], OUT_AR, rel_tol=0.01)}")
