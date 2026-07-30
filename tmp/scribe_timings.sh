#!/bin/bash
# Get word-level timestamps for each narration chapter (ElevenLabs Scribe, direct API).
set -e
ROOT=/home/user/rork-app
set -a
. "$ROOT/web/.env"
set +a
OUT=/tmp/scribe
mkdir -p "$OUT"

IDS=(
  8c14bb23-c10e-40a8-b176-7f7e28fd56a8
  675c012b-abb7-438b-a2e4-19ed3cd5794d
  3447d746-6b82-472d-b504-ce7498b07fc9
  7d05e8a4-389c-40f8-89d8-66ea72e859df
  6f937b8c-dbfb-430d-a3f7-a9b78866838d
  37d010f8-6792-43ce-ac86-e622ea65bee8
  8f9afd80-ca26-4db4-9edf-d110445f16f4
  bff80ff3-19a4-4fa4-981b-80768c5be0b4
  f5b84f97-36d6-4321-8a9f-a48fddc9c09a
  8bfc737e-e2e8-401e-925c-5df6b562ca74
  724c7172-0d0e-4b54-a7f9-fc2f23f95270
  db93f03e-5445-4a20-913a-061da1f1324e
  9d802493-e43b-4179-ac96-86978aae59a2
  04c561aa-932a-41ef-82c9-6f6530a64ce6
  0b5c2fb9-a49f-4f88-9b11-a2af8dbbc26f
  a8c33166-1e97-42da-b41e-c62da861a624
)

i=1
for id in "${IDS[@]}"; do
  n=$(printf "%02d" "$i")
  i=$((i+1))
  if [ -s "$OUT/ch$n.json" ] && [ "$(jq -r '.text // empty' "$OUT/ch$n.json" | wc -c)" -gt 20 ]; then
    echo "ch$n cached"
    continue
  fi
  url="https://r2-pub.rork.com/generated-audio/jvns5dfy7fpytx79a2tb3/$id.mp3"
  code=$(curl -sS -o "$OUT/ch$n.json" -w '%{http_code}' \
    -X POST "https://api.elevenlabs.io/v1/speech-to-text" \
    -H "xi-api-key: $ELEVENLABS_API_KEY" \
    -F "model_id=scribe_v1" \
    -F "language_code=eng" \
    -F "timestamps_granularity=word" \
    -F "source_url=$url"); sleep 3
  words=$(jq '[.words[]? | select(.type=="word")] | length' "$OUT/ch$n.json" 2>/dev/null || echo 0)
  echo "ch$n http=$code words=$words"
done
