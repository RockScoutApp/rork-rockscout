#!/bin/bash
# Final assembly: concatenate clips, mix audio, mux subtitles
set -e

ROOT="/home/user/rork-app"
OUT="/tmp/rockscout_video"
MUSIC="${ROOT}/ios-rockscout/RockScout/Resources/desert_campfire_ambient.mp3"
SEG="${OUT}/segments"

echo "=== Step 1: Concatenate 46 video clips ==="
VLIST="${OUT}/video_list.txt"
> "$VLIST"
for i in $(seq 0 45); do
  idx=$(printf '%03d' $i)
  f="${SEG}/clip_${idx}.mp4"
  if [ -f "$f" ]; then
    echo "file '${f}'" >> "$VLIST"
  else
    echo "MISSING: clip_${idx}.mp4"
  fi
done

ffmpeg -y -f concat -safe 0 -i "$VLIST" -c copy "${OUT}/video_full.mp4" 2>&1 | tail -2
VDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${OUT}/video_full.mp4")
echo "Video duration: ${VDUR}s"

echo ""
echo "=== Step 2: Concatenate 16 narration chapters ==="
cat > "${OUT}/narration_list.txt" << 'EOF'
file '/tmp/narration_final/ch01.mp3'
file '/tmp/narration_final/ch02.mp3'
file '/tmp/narration_final/ch03.mp3'
file '/tmp/narration_final/ch04.mp3'
file '/tmp/narration_final/ch05.mp3'
file '/tmp/narration_final/ch06.mp3'
file '/tmp/narration_final/ch07.mp3'
file '/tmp/narration_final/ch08.mp3'
file '/tmp/narration_final/ch09.mp3'
file '/tmp/narration_final/ch10.mp3'
file '/tmp/narration_final/ch11.mp3'
file '/tmp/narration_final/ch12.mp3'
file '/tmp/narration_final/ch13.mp3'
file '/tmp/narration_final/ch14.mp3'
file '/tmp/narration_final/ch15.mp3'
file '/tmp/narration_final/ch16.mp3'
EOF
ffmpeg -y -f concat -safe 0 -i "${OUT}/narration_list.txt" -c:a libmp3lame -b:a 128k "${OUT}/narration_full.mp3" 2>&1 | tail -2
NDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${OUT}/narration_full.mp3")
echo "Narration duration: ${NDUR}s"

echo ""
echo "=== Step 3: Mix narration + background music ==="
# Use the shorter of video/narration as duration target
FINAL_DUR=$(awk "BEGIN {if ($VDUR < $NDUR) print $VDUR; else print $NDUR}")
ffmpeg -y -i "${OUT}/narration_full.mp3" -i "$MUSIC" \
  -filter_complex "[1:a]volume=0.10,aloop=loop=-1:size=2e9[bg];[0:a][bg]amix=inputs=2:duration=first:dropout_transition=0[a]" \
  -map "[a]" -c:a aac -b:a 192k -t "$FINAL_DUR" "${OUT}/audio_mixed.aac" 2>&1 | tail -2
echo "Audio mixed: ${FINAL_DUR}s"

echo ""
echo "=== Step 4: Mux video + audio + subtitles ==="
# Build the ffmpeg command with subtitle inputs
INPUT_ARGS="-i ${OUT}/video_full.mp4 -i ${OUT}/audio_mixed.aac"
MAP_ARGS="-map 0:v:0 -map 1:a:0"
META_ARGS="-c:v copy -c:a copy -metadata:s:a:0 language=eng -metadata title=RockScout Tutorial"

SUB_FILES=(/tmp/new_subs/ind.srt /tmp/new_subs/jpn.srt /tmp/new_subs/fil.srt /tmp/new_subs/vie.srt /tmp/new_subs/rus.srt /tmp/new_subs/pol.srt /tmp/new_subs/ita.srt)

input_idx=2
sub_idx=0
for srt in "${SUB_FILES[@]}"; do
  lang=$(basename "$srt" .srt)
  INPUT_ARGS="${INPUT_ARGS} -i ${srt}"
  MAP_ARGS="${MAP_ARGS} -map ${input_idx}:s:0"
  META_ARGS="${META_ARGS} -c:s:${sub_idx} srt -metadata:s:${sub_idx} language=${lang}"
  input_idx=$((input_idx+1))
  sub_idx=$((sub_idx+1))
done

eval ffmpeg -y ${INPUT_ARGS} ${MAP_ARGS} ${META_ARGS} \
  "${OUT}/rockscout_tutorial.mkv" 2>&1 | tail -3

FDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${OUT}/rockscout_tutorial.mkv")
FSIZE=$(du -h "${OUT}/rockscout_tutorial.mkv" | cut -f1)

echo ""
echo "=== DONE ==="
echo "File: ${OUT}/rockscout_tutorial.mkv"
echo "Duration: ${FDUR}s"
echo "Size: ${FSIZE}"

# Print chapter timestamps (ms) for app code
echo ""
echo "=== Chapter timestamps (ms) ==="
DURS=(32.862 134.191 68.859 80.431 50.651 97.985 81.215 67.056 82.051 49.998 63.138 63.007 98.168 105.822 112.431 30.119)
cum=0
for i in "${!DURS[@]}"; do
  ms=$(awk "BEGIN {printf \"%d\", ${cum}*1000}")
  printf "Chapter %d: %d\n" $((i+1)) $ms
  cum=$(awk "BEGIN {printf \"%.3f\", ${cum}+${DURS[$i]}}")
done
