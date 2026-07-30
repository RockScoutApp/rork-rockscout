#!/bin/bash
# Build remaining Ken Burns clips - batch builder
# Usage: bash tmp/build_batch.sh <start_clip> <end_clip>
set -e

ROOT="/home/user/rork-app"
SHOTS="${ROOT}/web/screenshots"
OUT="/tmp/rockscout_video/segments"
FPS=30

# Scale to 1.3x output for zoom headroom (faster than 1620x3508)
SCALE_W=1404
SCALE_H=3040

generate_clip() {
  local img="$1"
  local effect="$2"
  local duration="$3"
  local clip_idx="$4"
  local outfile="${OUT}/clip_$(printf '%03d' $clip_idx).mp4"
  local src="${SHOTS}/${img}"
  local frames=$(awk "BEGIN {printf \"%d\", ${duration}*${FPS}}")
  
  if [ ! -f "$src" ]; then
    echo "MISSING: $src"
    return 1
  fi

  local x_expr y_expr z_expr
  
  case "$effect" in
    ci) z_expr="z='1+0.3*on/${frames}'"; x_expr="x='(iw-iw/zoom)/2'"; y_expr="y='(ih-ih/zoom)/2'" ;;
    co) z_expr="z='1.3-0.3*on/${frames}'"; x_expr="x='(iw-iw/zoom)/2'"; y_expr="y='(ih-ih/zoom)/2'" ;;
    pd) z_expr="z='1.15'"; x_expr="x='(iw-iw/zoom)/2'"; y_expr="y='(ih-ih/zoom)*on/${frames}'" ;;
    pu) z_expr="z='1.15'"; x_expr="x='(iw-iw/zoom)/2'"; y_expr="y='(ih-ih/zoom)*(1-on/${frames})'" ;;
    pr) z_expr="z='1.15'"; x_expr="x='(iw-iw/zoom)*on/${frames}'"; y_expr="y='(ih-ih/zoom)/2'" ;;
    pl) z_expr="z='1.15'"; x_expr="x='(iw-iw/zoom)*(1-on/${frames})'"; y_expr="y='(ih-ih/zoom)/2'" ;;
    zt) z_expr="z='1+0.3*on/${frames}'"; x_expr="x='(iw-iw/zoom)/2'"; y_expr="y='(ih-ih/zoom)*0.2'" ;;
    zb) z_expr="z='1+0.3*on/${frames}'"; x_expr="x='(iw-iw/zoom)/2'"; y_expr="y='(ih-ih/zoom)*0.8'" ;;
    *) z_expr="z='1+0.3*on/${frames}'"; x_expr="x='(iw-iw/zoom)/2'"; y_expr="y='(ih-ih/zoom)/2'" ;;
  esac

  ffmpeg -y -loop 1 -t "${duration}" -i "$src" \
    -vf "scale=${SCALE_W}:${SCALE_H}:force_original_aspect_ratio=increase,zoompan=${z_expr}:${x_expr}:${y_expr}:d=1:s=1080x1920:fps=${FPS},format=yuv420p" \
    -c:v libx264 -preset ultrafast -crf 30 -pix_fmt yuv420p -t "${duration}" \
    "$outfile" 2>/dev/null
  echo "  OK: clip_$(printf '%03d' $clip_idx) (${duration}s, ${effect})"
}

# Full clip list: index|image|effect|duration
# Chapter durations: 32.862 134.191 68.859 80.431 50.651 97.985 81.215 67.056 82.051 49.998 63.138 63.007 98.168 105.822 112.431 30.119
ALL_CLIPS=(
  "0|01_home.png|ci|32.862"
  "1|02_identify.png|zt|44.730"
  "2|03_scan.png|pd|44.730"
  "3|04_pdf_report.png|zb|44.730"
  "4|05_collection.png|pd|22.953"
  "5|06_specimen_detail.png|ci|22.953"
  "6|07_saved_images.png|pr|22.953"
  "7|08_field_captures.png|ci|26.810"
  "8|09_field_camera.png|zt|26.810"
  "9|10_upload_pill.png|zb|26.810"
  "10|11_dig_sites_map.png|ci|25.326"
  "11|12_gem_shows.png|pd|25.326"
  "12|13_trip_planner.png|pr|32.662"
  "13|14_archived_trips.png|pd|32.662"
  "14|15_field_journal.png|ci|32.662"
  "15|16_trade_board.png|ci|20.304"
  "16|17_trading_floor.png|pr|20.304"
  "17|18_my_trades.png|ci|20.304"
  "18|19_community.png|pd|20.304"
  "19|20_rockscouts_map.png|ci|22.352"
  "20|21_friends.png|pd|22.352"
  "21|22_discover_hunters.png|pd|22.352"
  "22|23_aurora.png|pd|27.350"
  "23|24_stars_landing.png|ci|27.350"
  "24|25_constellations.png|ci|27.350"
  "25|26_profile.png|ci|24.999"
  "26|27_achievements.png|pd|24.999"
  "27|28_periodic_table.png|co|21.046"
  "28|29_specimen_database.png|pd|21.046"
  "29|30_search.png|zt|21.046"
  "30|31_artifacts.png|pd|31.504"
  "31|32_natural_wonders.png|ci|31.504"
  "32|33_blm_guide.png|pd|24.542"
  "33|34_state_parks.png|pd|24.542"
  "34|35_meteorite_hunting.png|ci|24.542"
  "35|36_gear_guide.png|pd|24.542"
  "36|38_reference_hub.png|ci|13.228"
  "37|39_crystal_systems.png|ci|13.228"
  "38|40_fluorescence.png|ci|13.228"
  "39|41_rock_types.png|ci|13.228"
  "40|42_rock_cycle.png|ci|13.228"
  "41|43_glossary.png|pd|13.228"
  "42|44_mineral_id.png|ci|13.228"
  "43|45_lapidary.png|ci|13.228"
  "44|46_paywall.png|ci|112.431"
  "45|01_home.png|co|30.119"
)

START=${1:-0}
END=${2:-45}

pids=()
for clip_line in "${ALL_CLIPS[@]}"; do
  IFS='|' read -r idx img effect dur <<< "$clip_line"
  if [ "$idx" -ge "$START" ] && [ "$idx" -le "$END" ]; then
    outfile="${OUT}/clip_$(printf '%03d' $idx).mp4"
    if [ -f "$outfile" ]; then
      echo "SKIP: clip_$(printf '%03d' $idx) already exists"
      continue
    fi
    generate_clip "$img" "$effect" "$dur" "$idx" &
    pids+=($!)
    # Run 4 parallel
    if (( ${#pids[@]} >= 4 )); then
      wait "${pids[@]}"
      pids=()
    fi
  fi
done
wait "${pids[@]}" 2>/dev/null || true
echo "Batch $START-$END complete"
