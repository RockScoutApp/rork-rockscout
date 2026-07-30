#!/usr/bin/env python3
"""Retime SRT subtitles to match new narration timing using per-chapter proportional mapping."""
import re
import sys
import os

# Old chapter boundaries (start times in seconds) from VideoPlayerState.kt
OLD_CHAPTER_STARTS = [
    0.0,      # Ch1
    27.611,   # Ch2
    137.978,  # Ch3
    193.044,  # Ch4
    257.175,  # Ch5
    296.359,  # Ch6
    378.122,  # Ch7
    442.383,  # Ch8
    497.501,  # Ch9
    568.685,  # Ch10
    609.253,  # Ch11
    660.897,  # Ch12
    715.049,  # Ch13
    794.592,  # Ch14
    880.352,  # Ch15
    971.598,  # Ch16
]
OLD_END = 997.0  # ~16:37

# New chapter durations (seconds) from ffprobe
NEW_CHAPTER_DURATIONS = [
    32.862,   # Ch1
    134.191,  # Ch2
    68.859,   # Ch3
    80.431,   # Ch4
    50.651,   # Ch5
    97.985,   # Ch6
    81.215,   # Ch7
    67.056,   # Ch8
    82.051,   # Ch9
    49.998,   # Ch10
    63.138,   # Ch11
    63.007,   # Ch12
    98.168,   # Ch13
    105.822,  # Ch14
    112.431,  # Ch15
    30.119,   # Ch16
]

# Compute new chapter starts
NEW_CHAPTER_STARTS = []
cum = 0.0
for d in NEW_CHAPTER_DURATIONS:
    NEW_CHAPTER_STARTS.append(cum)
    cum += d
NEW_END = cum  # 1217.983

# Old chapter durations
OLD_CHAPTER_DURATIONS = []
for i in range(len(OLD_CHAPTER_STARTS)):
    end = OLD_CHAPTER_STARTS[i+1] if i+1 < len(OLD_CHAPTER_STARTS) else OLD_END
    OLD_CHAPTER_DURATIONS.append(end - OLD_CHAPTER_STARTS[i])

def find_chapter(t):
    """Find which chapter a timestamp falls in."""
    for i in range(len(OLD_CHAPTER_STARTS)):
        end = OLD_CHAPTER_STARTS[i+1] if i+1 < len(OLD_CHAPTER_STARTS) else OLD_END
        if OLD_CHAPTER_STARTS[i] <= t < end:
            return i
    return len(OLD_CHAPTER_STARTS) - 1

def remap_time(t):
    """Map old timestamp to new timestamp using per-chapter proportional scaling."""
    if t >= OLD_END:
        # Clamp to end
        return NEW_END - 0.1
    ch = find_chapter(t)
    old_start = OLD_CHAPTER_STARTS[ch]
    old_dur = OLD_CHAPTER_DURATIONS[ch]
    new_start = NEW_CHAPTER_STARTS[ch]
    new_dur = NEW_CHAPTER_DURATIONS[ch]
    
    if old_dur <= 0:
        return new_start
    
    frac = (t - old_start) / old_dur
    return new_start + frac * new_dur

def time_to_seconds(ts):
    """Convert SRT timestamp (HH:MM:SS,mmm) to seconds."""
    m = re.match(r'(\d+):(\d+):(\d+),(\d+)', ts)
    if not m:
        return 0.0
    h, mn, s, ms = int(m.group(1)), int(m.group(2)), int(m.group(3)), int(m.group(4))
    return h * 3600 + mn * 60 + s + ms / 1000.0

def seconds_to_time(sec):
    """Convert seconds to SRT timestamp (HH:MM:SS,mmm)."""
    sec = max(0, sec)
    h = int(sec // 3600)
    sec -= h * 3600
    mn = int(sec // 60)
    sec -= mn * 60
    s = int(sec)
    ms = int((sec - s) * 1000)
    return f"{h:02d}:{mn:02d}:{s:02d},{ms:03d}"

def retime_srt(input_path, output_path):
    """Retime an SRT file."""
    with open(input_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Split into blocks
    blocks = re.split(r'\n\n+', content.strip())
    out_blocks = []
    
    for block in blocks:
        lines = block.strip().split('\n')
        if len(lines) < 2:
            continue
        
        idx = lines[0]
        time_line = lines[1]
        text = '\n'.join(lines[2:]) if len(lines) > 2 else ''
        
        m = re.match(r'(\d+:\d+:\d+,\d+)\s*-->\s*(\d+:\d+:\d+,\d+)', time_line)
        if not m:
            continue
        
        start_old = time_to_seconds(m.group(1))
        end_old = time_to_seconds(m.group(2))
        
        start_new = remap_time(start_old)
        end_new = remap_time(end_old)
        
        # Ensure minimum duration
        if end_new - start_new < 0.5:
            end_new = start_new + 0.5
        
        new_time = f"{seconds_to_time(start_new)} --> {seconds_to_time(end_new)}"
        out_blocks.append(f"{idx}\n{new_time}\n{text}")
    
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write('\n\n'.join(out_blocks) + '\n')
    
    return len(out_blocks)

# Process all subtitle files
sub_dir = '/tmp/old_subs'
out_dir = '/tmp/new_subs'
os.makedirs(out_dir, exist_ok=True)

for fname in sorted(os.listdir(sub_dir)):
    if not fname.endswith('.srt'):
        continue
    lang = fname.replace('.srt', '')
    inp = os.path.join(sub_dir, fname)
    outp = os.path.join(out_dir, fname)
    count = retime_srt(inp, outp)
    print(f"  {lang}: {count} entries retimed")

print(f"\nNew chapter starts: {[f'{s:.1f}' for s in NEW_CHAPTER_STARTS]}")
print(f"New total duration: {NEW_END:.1f}s ({NEW_END/60:.1f} min)")
