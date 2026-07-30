#!/bin/bash
# Ken Burns video builder for RockScout tutorial
# Creates 1080x1920 portrait video with zoom/pan effects on each screenshot
set -e

ROOT="/home/user/rork-app"
SHOTS="${ROOT}/web/screenshots"
NARR="/tmp/narration_final"
MUSIC="${ROOT}/ios-rockscout/RockScout/Resources/desert_campfire_ambient.mp3"
OUT="/tmp/rockscout_video"
mkdir -p "${OUT}/segments"

# Chapter durations (from ffprobe)
DURS=(32.862 134.191 68.859 80.431 50.651 97.985 81.215 67.056 82.051 49.998 63.138 63.007 98.168 105.822 112.431 30.119)

# Screenshots per chapter (pipe-separated) with effect type (colon-separated)
# Effects: ci=center zoom-in, co=center zoom-out, pd=pan-down, pu=pan-up, pl=pan-left, pr=pan-right
#          zt=zoom top, zb=zoom bottom, zl=zoom left, zr=zoom right
declare -a SEGMENTS=(
  "01_home.png:ci"
  "02_identify.png:zt|03_scan.png:pd|04_pdf_report.png:zb"
  "05_collection.png:pd|06_specimen_detail.png:ci|07_saved_images.png:pr"
  "08_field_captures.png:ci|09_field_camera.png:zt|10_upload_pill.png:zb"
  "11_dig_sites_map.png:ci|12_gem_shows.png:pd"
  "13_trip_planner.png:pr|14_archived_trips.png:pd|15_field_journal.png:ci"
  "16_trade_board.png:ci|17_trading_floor.png:pr|18_my_trades.png:ci|19_community.png:pd"
  "20_rockscouts_map.png:ci|21_friends.png:pd|22_discover_hunters.png:pd"
  "23_aurora.png:pd|24_stars_landing.png:ci|25_constellations.png:ci"
  "26_profile.png:ci|27_achievements.png:pd"
  "28_periodic_table.png:co|29_specimen_database.png:pd|30_search.png:zt"
  "31_artifacts.png:pd|32_natural_wonders.png:ci"
  "33_blm_guide.png:pd|34_state_parks.png:pd|35_meteorite_hunting.png:ci|36_gear_guide.png:pd"
  "38_reference_hub.png:ci|39_crystal_systems.png:ci|40_fluorescence.png:ci|41_rock_types.png:ci|42_rock_cycle.png:ci|43_glossary.png:pd|44_mineral_id.png:ci|45_lapidary.png:ci"
  "46_paywall.png:ci"
  "01_home.png:co"
)

FPS=30
SCALE_W=1620
SCALE_H=3508  # 828x1792 scaled to 1620 width

generate_clip() {
  local img="$1"
  local effect="$2"
  local duration="$3"
  local outfile="$4"
  local frames=$(awk "BEGIN {printf \"%d\", ${duration}*${FPS}}")
  local src="${SHOTS}/${img}"
  
  if [ ! -f "$src" ]; then
    echo "MISSING: $src"
    return 1
  fi

  local x_expr y_expr z_expr
  
  case "$effect" in
    ci) # center zoom-in: zoom 1.0 -> 1.3, centered
      z_expr="z='1+0.3*on/${frames}'"
      x_expr="x='(iw-iw/zoom)/2'"
      y_expr="y='(ih-ih/zoom)/2'"
      ;;
    co) # center zoom-out: zoom 1.3 -> 1.0, centered
      z_expr="z='1.3-0.3*on/${frames}'"
      x_expr="x='(iw-iw/zoom)/2'"
      y_expr="y='(ih-ih/zoom)/2'"
      ;;
    pd) # pan down: zoom 1.15, pan top to bottom
      z_expr="z='1.15'"
      x_expr="x='(iw-iw/zoom)/2'"
      y_expr="y='(ih-ih/zoom)*on/${frames}'"
      ;;
    pu) # pan up: zoom 1.15, pan bottom to top
      z_expr="z='1.15'"
      x_expr="x='(iw-iw/zoom)/2'"
      y_expr="y='(ih-ih/zoom)*(1-on/${frames})'"
      ;;
    pr) # pan right: zoom 1.15, pan left to right
      z_expr="z='1.15'"
      x_expr="x='(iw-iw/zoom)*on/${frames}'"
      y_expr="y='(ih-ih/zoom)/2'"
      ;;
    pl) # pan left: zoom 1.15, pan right to left
      z_expr="z='1.15'"
      x_expr="x='(iw-iw/zoom)*(1-on/${frames})'"
      y_expr="y='(ih-ih/zoom)/2'"
      ;;
    zt) # zoom toward top: zoom 1.0 -> 1.3, y biased to top
      z_expr="z='1+0.3*on/${frames}'"
      x_expr="x='(iw-iw/zoom)/2'"
      y_expr="y='(ih-ih/zoom)*0.2'"
      ;;
    zb) # zoom toward bottom: zoom 1.0 -> 1.3, y biased to bottom
      z_expr="z='1+0.3*on/${frames}'"
      x_expr="x='(iw-iw/zoom)/2'"
      y_expr="y='(ih-ih/zoom)*0.8'"
      ;;
    zl) # zoom toward left
      z_expr="z='1+0.3*on/${frames}'"
      x_expr="x='(iw-iw/zoom)*0.2'"
      y_expr="y='(ih-ih/zoom)/2'"
      ;;
    zr) # zoom toward right
      z_expr="z='1+0.3*on/${frames}'"
      x_expr="x='(iw-iw/zoom)*0.8'"
      y_expr="y='(ih-ih/zoom)/2'"
      ;;
    *) # default: center zoom-in
      z_expr="z='1+0.3*on/${frames}'"
      x_expr="x='(iw-iw/zoom)/2'"
      y_expr="y='(ih-ih/zoom)/2'"
      ;;
  esac

  ffmpeg -y -loop 1 -t "${duration}" -i "$src" \
    -vf "scale=${SCALE_W}:${SCALE_H}:force_original_aspect_ratio=increase,zoompan=${z_expr}:${x_expr}:${y_expr}:d=1:s=1080x1920:fps=${FPS},format=yuv420p" \
    -c:v libx264 -preset ultrafast -crf 28 -pix_fmt yuv420p -t "${duration}" \
    "$outfile" 2>/dev/null
  
  echo "  OK: $(basename $outfile) (${duration}s, ${effect})"
}

echo "=== Building Ken Burns video segments ==="
VLIST="${OUT}/video_list.txt"
> "$VLIST"

clip_idx=0
for ch_idx in "${!SEGMENTS[@]}"; do
  ch_dur="${DURS[$ch_idx]}"
  IFS='|' read -ra parts <<< "${SEGMENTS[$ch_idx]}"
  num_parts=${#parts[@]}
  # Equal duration per screenshot within a chapter
  part_dur=$(awk "BEGIN {printf \"%.3f\", ${ch_dur}/${num_parts}}")
  
  echo "Chapter $((ch_idx+1)): ${num_parts} shots, ${part_dur}s each"
  
  for part in "${parts[@]}"; do
    IFS=':' read -ra kv <<< "$part"
    img="${kv[0]}"
    effect="${kv[1]:-ci}"
    outfile="${OUT}/segments/clip_$(printf '%03d' $clip_idx).mp4"
    
    generate_clip "$img" "$effect" "$part_dur" "$outfile" &
    
    # Run 3 parallel at a time
    if (( clip_idx % 3 == 2 )); then
      wait
    fi
    
    echo "file '${outfile}'" >> "$VLIST"
    clip_idx=$((clip_idx+1))
  done
done
wait

echo ""
echo "=== Concatenating video segments ==="
ffmpeg -y -f concat -safe 0 -i "$VLIST" -c copy "${OUT}/video_full.mp4" 2>&1 | tail -2
VDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${OUT}/video_full.mp4")
echo "Video duration: ${VDUR}s"

echo ""
echo "=== Concatenating narration ==="
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
echo "=== Mixing narration + background music ==="
ffmpeg -y -i "${OUT}/narration_full.mp3" -i "$MUSIC" \
  -filter_complex "[1:a]volume=0.10,aloop=loop=-1:size=2e9[bg];[0:a][bg]amix=inputs=2:duration=first:dropout_transition=0[a]" \
  -map "[a]" -c:a aac -b:a 192k -t "$VDUR" "${OUT}/audio_mixed.aac" 2>&1 | tail -2

echo ""
echo "=== Muxing final MKV with subtitles ==="
# Build subtitle mapping args
SUB_ARGS=""
SUB_META=""
sub_idx=0
for srtfile in /tmp/new_subs/*.srt; do
  lang=$(basename "$srtfile" .srt)
  SUB_ARGS="${SUB_ARGS} -i ${srtfile}"
  SUB_META="${SUB_META} -map $((sub_idx+2)):s -c:s $((sub_idx)):srt -metadata:s:$((sub_idx)) language=${lang}"
  sub_idx=$((sub_idx+1))
done

eval ffmpeg -y -i "${OUT}/video_full.mp4" -i "${OUT}/audio_mixed.aac" ${SUB_ARGS} \
  -map 0:v:0 -map 1:a:0 ${SUB_META} \
  -c:v copy -c:a copy \
  -metadata:s:a:0 language=eng -metadata title="RockScout Tutorial" \
  "${OUT}/rockscout_tutorial.mkv" 2>&1 | tail -3

FDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${OUT}/rockscout_tutorial.mkv")
FSIZE=$(du -h "${OUT}/rockscout_tutorial.mkv" | cut -f1)
echo ""
echo "=== DONE ==="
echo "File: ${OUT}/rockscout_tutorial.mkv"
echo "Duration: ${FDUR}s"
echo "Size: ${FSIZE}"

# Print chapter timestamps for app code update
echo ""
echo "=== Chapter timestamps (ms) ==="
cum=0
for i in "${!DURS[@]}"; do
  printf "Chapter %d: %d\n" $((i+1)) $(awk "BEGIN {printf \"%d\", ${cum}*1000}")
  cum=$(awk "BEGIN {printf \"%.3f\", ${cum}+${DURS[$i]}}")
done
