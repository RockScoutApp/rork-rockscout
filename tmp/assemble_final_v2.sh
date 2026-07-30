#!/bin/bash
# Final assembly v2: stretch video to match narration, fix subtitle muxing
set -e

ROOT="/home/user/rork-app"
OUT="/tmp/rockscout_video"
MUSIC="${ROOT}/ios-rockscout/RockScout/Resources/desert_campfire_ambient.mp3"

# Video is 859.467s, narration is 1217.463s
# Stretch factor: 1217.463 / 859.467 = 1.4164
# This slows the Ken Burns motion slightly — actually looks better
STRETCH="1.4164"

echo "=== Step 1: Stretch video to match narration duration ==="
ffmpeg -y -i "${OUT}/video_full.mp4" \
  -vf "setpts=PTS*${STRETCH},fps=30" \
  -c:v libx264 -preset ultrafast -crf 28 -pix_fmt yuv420p \
  "${OUT}/video_stretched.mp4" 2>&1 | tail -3
VDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${OUT}/video_stretched.mp4")
echo "Stretched video: ${VDUR}s"

echo ""
echo "=== Step 2: Mix narration + background music ==="
ffmpeg -y -i "${OUT}/narration_full.mp3" -i "$MUSIC" \
  -filter_complex "[1:a]volume=0.10,aloop=loop=-1:size=2e9[bg];[0:a][bg]amix=inputs=2:duration=first:dropout_transition=0[a]" \
  -map "[a]" -c:a aac -b:a 192k -t "$VDUR" "${OUT}/audio_mixed.aac" 2>&1 | tail -2
echo "Audio mixed: ${VDUR}s"

echo ""
echo "=== Step 3: Mux video + audio + subtitles ==="
# Build the ffmpeg command carefully — no eval, direct execution
ffmpeg -y \
  -i "${OUT}/video_stretched.mp4" \
  -i "${OUT}/audio_mixed.aac" \
  -i /tmp/new_subs/ind.srt \
  -i /tmp/new_subs/jpn.srt \
  -i /tmp/new_subs/fil.srt \
  -i /tmp/new_subs/vie.srt \
  -i /tmp/new_subs/rus.srt \
  -i /tmp/new_subs/pol.srt \
  -i /tmp/new_subs/ita.srt \
  -map 0:v:0 -map 1:a:0 \
  -map 2:s:0 -map 3:s:0 -map 4:s:0 -map 5:s:0 -map 6:s:0 -map 7:s:0 -map 8:s:0 \
  -c:v copy -c:a copy \
  -c:s srt \
  -metadata:s:a:0 language=eng \
  -metadata:s:a:0 title="English Narration" \
  -metadata:s:s:0 language=ind \
  -metadata:s:s:1 language=jpn \
  -metadata:s:s:2 language=fil \
  -metadata:s:s:3 language=vie \
  -metadata:s:s:4 language=rus \
  -metadata:s:s:5 language=pol \
  -metadata:s:s:6 language=ita \
  -metadata title="RockScout Tutorial" \
  "${OUT}/rockscout_tutorial.mkv" 2>&1 | tail -5

FDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${OUT}/rockscout_tutorial.mkv")
FSIZE=$(du -h "${OUT}/rockscout_tutorial.mkv" | cut -f1)
echo ""
echo "=== DONE ==="
echo "File: ${OUT}/rockscout_tutorial.mkv"
echo "Duration: ${FDUR}s ($(awk "BEGIN {printf \"%.1f\", $FDUR/60}") min)"
echo "Size: ${FSIZE}"

# Verify streams
echo ""
echo "=== Streams ==="
ffprobe -v quiet -show_streams "${OUT}/rockscout_tutorial.mkv" 2>&1 | grep -E "codec_type|TAG:language|index" | head -40

# Chapter timestamps (ms) — these are the NARRATION chapter starts
# which match the video since we stretched to fit
echo ""
echo "=== Chapter timestamps (ms) for app code ==="
DURS=(32.862 134.191 68.859 80.431 50.651 97.985 81.215 67.056 82.051 49.998 63.138 63.007 98.168 105.822 112.431 30.119)
cum=0
for i in "${!DURS[@]}"; do
  ms=$(awk "BEGIN {printf \"%d\", ${cum}*1000}")
  printf "Chapter %d: %d_ms\n" $((i+1)) $ms
  cum=$(awk "BEGIN {printf \"%.3f\", ${cum}+${DURS[$i]}}")
done
