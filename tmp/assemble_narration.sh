#!/bin/bash
# Download and assemble all 16 narration chapters
set -e

OUT=/tmp/narration_final
mkdir -p "$OUT"

# Download URL-based chapters (1,4,6,8,9,11,12,13,15,16)
echo "=== Downloading URL chapters ==="
curl -sL "https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/1ac04237-5e63-4c48-8e61-9622b9b20188.mp3" -o "$OUT/ch01.mp3" &
curl -sL "https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/836dac53-9082-4ac1-8258-0c844aa4e3d1.mp3" -o "$OUT/ch04.mp3" &
curl -sL "https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/e0d5b433-2bf6-495d-a23f-56e2a416a698.mp3" -o "$OUT/ch06.mp3" &
curl -sL "https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/9eb7eb6e-68bb-4e6b-9428-279fae57e560.mp3" -o "$OUT/ch08.mp3" &
curl -sL "https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/a19664ba-ca3b-4c5c-94a9-c38cc5cec834.mp3" -o "$OUT/ch09.mp3" &
curl -sL "https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/d0c3da62-6647-40fb-9869-c92d46ffa651.mp3" -o "$OUT/ch11.mp3" &
curl -sL "https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/d7138360-afdb-4377-9f5b-6a7c1c804e58.mp3" -o "$OUT/ch12.mp3" &
curl -sL "https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/ddf52b2a-ce48-4bb3-809a-26f6a1510a7d.mp3" -o "$OUT/ch13.mp3" &
curl -sL "https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/4a24d211-60c2-498e-be42-02e3f4bb139d.mp3" -o "$OUT/ch15.mp3" &
curl -sL "https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/7ca7b641-c2c1-462b-9b48-99025a509458.mp3" -o "$OUT/ch16.mp3" &
wait

# Copy from Resources (chapters saved there: 2,3,5,7,10,14)
echo "=== Copying from Resources ==="
cp ios-rockscout/RockScout/Resources/rock_identification_instructions.mp3 "$OUT/ch02.mp3"
cp ios-rockscout/RockScout/Resources/rocks_collection_voice.mp3 "$OUT/ch03.mp3"
cp ios-rockscout/RockScout/Resources/treasure_map_guide.mp3 "$OUT/ch05.mp3"
cp ios-rockscout/RockScout/Resources/trade_board_voice.mp3 "$OUT/ch07.mp3"
cp ios-rockscout/RockScout/Resources/profile_card_voice.mp3 "$OUT/ch10.mp3"
cp ios-rockscout/RockScout/Resources/educational_guide_intro.mp3 "$OUT/ch14.mp3"

# Measure durations
echo ""
echo "=== Durations ==="
TOTAL=0
for ch in $(seq 1 16); do
  printf -v ch2 "%02d" $ch
  f="$OUT/ch${ch2}.mp3"
  if [ -f "$f" ]; then
    dur=$(ffprobe -v quiet -show_entries format=duration -of csv=p=0 "$f" 2>/dev/null || echo "0")
    size=$(du -h "$f" | cut -f1)
    TOTAL=$(awk "BEGIN {printf \"%.2f\", $TOTAL + $dur}")
    echo "Ch${ch2}: ${dur}s (${size})"
  else
    echo "Ch${ch2}: MISSING!"
  fi
done
echo ""
echo "=== Total: ${TOTAL}s ($(awk "BEGIN {printf \"%.1f\", $TOTAL/60}") min) ==="
