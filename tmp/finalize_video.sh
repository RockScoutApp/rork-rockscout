#!/bin/bash
# Produce the shipping file: 720x1280 video, English audio, 15 subtitle tracks.
set -e
OUT=/tmp/rockscout_video
SUBS=$OUT/subs
DEST=/home/user/rork-app/web/public/tutorial
mkdir -p "$DEST"

echo "=== video (720x1280) ==="
if [ ! -s "$OUT/video_ship.mp4" ]; then
  ffmpeg -y -i "$OUT/video_full.mp4" -vf scale=720:1280:flags=lanczos \
    -c:v libx264 -preset veryfast -crf 28 -pix_fmt yuv420p -g 60 \
    "$OUT/video_ship.mp4" 2>/dev/null
fi
ls -la "$OUT/video_ship.mp4"

echo "=== audio (112k) ==="
ffmpeg -y -i "$OUT/audio_mixed.m4a" -c:a aac -b:a 112k "$OUT/audio_ship.m4a" 2>/dev/null
ls -la "$OUT/audio_ship.m4a"

echo "=== mux ==="
LANGS="eng spa fra deu por zho ara hin ind jpn fil vie rus pol ita"
ARGS=""
MAPS="-map 0:v:0 -map 1:a:0"
META="-metadata:s:a:0 language=eng -metadata:s:a:0 title=English"
idx=2
sidx=0
for l in $LANGS; do
  ARGS="$ARGS -i $SUBS/$l.srt"
  MAPS="$MAPS -map $idx:s:0"
  META="$META -metadata:s:s:$sidx language=$l"
  idx=$((idx+1)); sidx=$((sidx+1))
done

# shellcheck disable=SC2086
ffmpeg -y -i "$OUT/video_ship.mp4" -i "$OUT/audio_ship.m4a" $ARGS \
  $MAPS -c:v copy -c:a copy -c:s srt $META \
  -metadata title="RockScout Tutorial" \
  "$DEST/rockscout-tutorial.mkv" 2>&1 | tail -2

echo ""
ffprobe -v error -show_entries format=duration -of csv=p=0 "$DEST/rockscout-tutorial.mkv"
ffprobe -v error -show_entries stream=index,codec_type:stream_tags=language -of csv=p=0 "$DEST/rockscout-tutorial.mkv" | tr '\n' ' '
echo ""
du -h "$DEST/rockscout-tutorial.mkv"
