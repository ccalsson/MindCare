#!/usr/bin/env bash
set -euo pipefail

PROJECT_ID=${1:-mindcare-745ca}
SA_JSON=${GOOGLE_APPLICATION_CREDENTIALS:-}

if [[ -z "$SA_JSON" || ! -f "$SA_JSON" ]]; then
  echo "Error: set GOOGLE_APPLICATION_CREDENTIALS to your service account JSON (e.g., export GOOGLE_APPLICATION_CREDENTIALS=server/keys/mindcare-sa.json)" >&2
  exit 1
fi

ROOT_DIR=$(cd "$(dirname "$0")/.." && pwd)

# Ensure Node can resolve dependencies installed under functions
export NODE_PATH="$ROOT_DIR/server/functions/node_modules:${NODE_PATH:-}"

echo "Seeding Firestore..."
node "$ROOT_DIR/server/seed_firestore.mjs" "$PROJECT_ID"

echo "Uploading Storage assets..."
node "$ROOT_DIR/server/upload_storage.mjs" "$PROJECT_ID"

echo "Done. Firestore seeded and assets uploaded."

