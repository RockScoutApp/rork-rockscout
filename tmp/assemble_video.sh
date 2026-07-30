#!/bin/bash
# Final assembly: narration + low background music, 15 labelled subtitle tracks.
set -e
ROOT=/home/user/rork-app
OUT=/tmp/rockscout_video
NARR=/tmp/narration_final
SUBS=$OUT/subs
MUSIC="$ROOT/ios-rockscout/RockScout/Resources/desert_campfire_ambient.mp3"

echo "=== narration ==="
> "$OUT/narration_list.txt"
for i in $(seq -w 1 16); do echo "file '$NARR/ch$i.mp3'" >> "$OUT/narration_list.txt"; done
ffmpeg -y -f concat -safe 0 -i "$OUT/narration_list.txt" -c:a libmp3lame -b:a 160k "$OUT/narration_full.mp3" 2>/dev/null
NDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "$OUT/narration_full.mp3")
VDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "$OUT/video_full.mp4")
echo "narration=${NDUR}s video=${VDUR}s"

echo "=== mix music under the voice ==="
ffmpeg -y -i "$OUT/narration_full.mp3" -stream_loop -1 -i "$MUSIC" \
  -filter_complex "[1:a]volume=0.07,afade=t=in:st=0:d=3[bg];[0:a]dynaudnorm=f=250:g=7,volume=1.05[voice];[voice][bg]amix=inputs=2:duration=first:normalize=0[a]" \
  -map "[a]" -c:a aac -b:a 192k -t "$VDUR" "$OUT/audio_mixed.m4a" 2>/dev/null
echo "audio mixed"

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
ffmpeg -y -i "$OUT/video_small.mp4" -i "$OUT/audio_mixed.m4a" $ARGS \
  $MAPS -c:v copy -c:a copy -c:s srt $META \
  -metadata title="RockScout Tutorial" \
  "$OUT/rockscout_tutorial.mkv" 2>&1 | tail -3

echo ""
echo "=== result ==="
ffprobe -v error -show_entries format=duration:stream=index,codec_type,codec_name:stream_tags=language \
  -of default=noprint_wrappers=1 "$OUT/rockscout_tutorial.mkv" | rg -v '^$' | tr '\n' ' ' | sed 's/index=/\nindex=/g'
echo ""
du -h "$OUT/rockscout_tutorial.mkv"
