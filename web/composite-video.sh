#!/bin/bash
# RockScout Tutorial Video Compositing
# Creates 1080x1920 (9:16) portrait video from real screenshots + narration + music
set -e

ROOT="/home/user/rork-app"
SCREENSHOTS="${ROOT}/web/screenshots"
AUDIO="${ROOT}/ios-rockscout/RockScout/Resources"
MUSIC="${AUDIO}/desert_campfire_ambient.mp3"
OUT="/tmp/rockscout_video"
mkdir -p "${OUT}/segments"

# Narration files in chapter order
NARR=(
  "${AUDIO}/rockscout_welcome_intro.mp3"
  "${AUDIO}/rock_identification_voice.mp3"
  "${AUDIO}/rocks_collection_guide.mp3"
  "${AUDIO}/field_capture_voice.mp3"
  "${AUDIO}/treasure_map_voice.mp3"
  "${AUDIO}/trip_planner_voice.mp3"
  "${AUDIO}/trade_board_intro_voice.mp3"
  "${AUDIO}/social_network_voice.mp3"
  "${AUDIO}/aurora_forecaster_voice.mp3"
  "${AUDIO}/profile_level_up_voice.mp3"
  "${AUDIO}/periodic_table_voice_guide.mp3"
  "${AUDIO}/artifact_catalog_voice.mp3"
  "${AUDIO}/rockhounding_guide_voice.mp3"
  "${AUDIO}/educational_guides_intro.mp3"
  "${AUDIO}/pricing_explanation_voice.mp3"
  "${AUDIO}/rockscout_voice_intro.mp3"
)

# Screenshots per chapter (pipe-separated)
SHOTS=(
  "01_home.png"
  "02_identify.png|03_scan.png|04_pdf_report.png"
  "05_collection.png|06_specimen_detail.png|07_saved_images.png"
  "08_field_captures.png|09_field_camera.png|10_upload_pill.png"
  "11_dig_sites_map.png|12_gem_shows.png"
  "13_trip_planner.png|14_archived_trips.png|15_field_journal.png"
  "16_trade_board.png|17_trading_floor.png|18_my_trades.png|19_community.png"
  "20_rockscouts_map.png|21_friends.png|22_discover_hunters.png"
  "23_aurora.png|24_stars_landing.png|25_constellations.png"
  "26_profile.png|27_achievements.png"
  "28_periodic_table.png|29_specimen_database.png|30_search.png"
  "31_artifacts.png|32_natural_wonders.png"
  "33_blm_guide.png|34_state_parks.png|35_meteorite_hunting.png|36_gear_guide.png"
  "38_reference_hub.png|39_crystal_systems.png|40_fluorescence.png|41_rock_types.png|42_rock_cycle.png|43_glossary.png|44_mineral_id.png|45_lapidary.png"
  "46_paywall.png"
  "01_home.png"
)

echo "=== Step 1: Concatenate narration ==="
LIST="${OUT}/narration.txt"
> "$LIST"
for f in "${NARR[@]}"; do echo "file '$f'" >> "$LIST"; done
ffmpeg -y -f concat -safe 0 -i "$LIST" -c:a libmp3lame -b:a 128k "${OUT}/narration_full.mp3" 2>&1 | tail -2
NARR_DUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${OUT}/narration_full.mp3")
echo "Narration: ${NARR_DUR}s"

echo "=== Step 2: Build video segments ==="
VLIST="${OUT}/video_list.txt"
> "$VLIST"
for i in "${!SHOTS[@]}"; do
  IFS='|' read -ra SS <<< "${SHOTS[$i]}"
  SDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${NARR[$i]}")
  NS=${#SS[@]}
  PDUR=$(awk "BEGIN {printf \"%.2f\", ${SDUR}/${NS}}")
  echo "  Ch$((i+1)): ${NS} shots, ${SDUR}s, ${PDUR}s each"
  
  CLIST="${OUT}/ch_${i}.txt"
  > "$CLIST"
  for s in "${SS[@]}"; do
    P="${SCREENSHOTS}/${s}"
    if [ -f "$P" ]; then
      echo "file '$P'" >> "$CLIST"
      echo "duration ${PDUR}" >> "$CLIST"
    fi
  done
  echo "file '${SCREENSHOTS}/${SS[-1]}'" >> "$CLIST"
  
  SF="${OUT}/segments/seg_${i}.mp4"
  ffmpeg -y -f concat -safe 0 -i "$CLIST" \
    -vf "scale=1080:1920:force_original_aspect_ratio=increase,crop=1080:1920,fps=30,format=yuv420p" \
    -c:v libx264 -preset ultrafast -crf 28 -t "$SDUR" "$SF" 2>&1 | tail -1
  echo "file '$SF'" >> "$VLIST"
done

echo "=== Step 3: Concatenate video ==="
ffmpeg -y -f concat -safe 0 -i "$VLIST" -c copy "${OUT}/video_full.mp4" 2>&1 | tail -2
VDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${OUT}/video_full.mp4")
echo "Video: ${VDUR}s"

echo "=== Step 4: Mix narration + music ==="
ffmpeg -y -i "${OUT}/narration_full.mp3" -i "$MUSIC" \
  -filter_complex "[1:a]volume=0.12,aloop=loop=-1:size=2e9[bg];[0:a][bg]amix=inputs=2:duration=first:dropout_transition=0[a]" \
  -map "[a]" -c:a aac -b:a 192k -t "$VDUR" "${OUT}/audio_mixed.aac" 2>&1 | tail -2

echo "=== Step 5: Mux final MKV ==="
ffmpeg -y -i "${OUT}/video_full.mp4" -i "${OUT}/audio_mixed.aac" \
  -map 0:v:0 -map 1:a:0 -c copy \
  -metadata:s:a:0 language=eng -metadata title="RockScout Tutorial" \
  "${OUT}/rockscout_tutorial.mkv" 2>&1 | tail -2

FDUR=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "${OUT}/rockscout_tutorial.mkv")
FSIZE=$(du -h "${OUT}/rockscout_tutorial.mkv" | cut -f1)
echo "=== DONE ==="
echo "File: ${OUT}/rockscout_tutorial.mkv"
echo "Duration: ${FDUR}s"
echo "Size: ${FSIZE}"
