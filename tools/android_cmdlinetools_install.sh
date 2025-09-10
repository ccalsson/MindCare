#!/usr/bin/env bash
set -euo pipefail

# Android Command-line Tools bootstrapper (macOS/Linux)
# - Downloads cmdline-tools from Google
# - Installs to $ANDROID_SDK_ROOT/cmdline-tools/latest
# - Installs platform-tools, build-tools, platforms via sdkmanager
# - Accepts licenses
# - Integrates with Flutter

echo "[clt] Starting Android cmdline-tools install…"

# 1) Detect OS and set default SDK path
OS_NAME=$(uname -s)
case "$OS_NAME" in
  Darwin) OS_TAG="mac"; DEFAULT_SDK_ROOT="$HOME/Library/Android/sdk" ;;
  Linux)  OS_TAG="linux"; DEFAULT_SDK_ROOT="$HOME/Android/Sdk" ;;
  *) echo "[clt] Unsupported OS: $OS_NAME" >&2; exit 1 ;;
esac

export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$DEFAULT_SDK_ROOT}"
mkdir -p "$ANDROID_SDK_ROOT" "$ANDROID_SDK_ROOT/cmdline-tools" || true
echo "[clt] ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"

# 2) Choose download URL (override with ANDROID_CLT_URL)
# Known versions (newer first) used as fallbacks if download fails
DEFAULT_URLS=(
  "https://dl.google.com/android/repository/commandlinetools-${OS_TAG}-11076708_latest.zip"
  "https://dl.google.com/android/repository/commandlinetools-${OS_TAG}-10406996_latest.zip"
)

TMPDIR=$(mktemp -d)
trap 'rm -rf "$TMPDIR"' EXIT
cd "$TMPDIR"

URLS=()
if [[ -n "${ANDROID_CLT_URL:-}" ]]; then
  URLS=("$ANDROID_CLT_URL" "${DEFAULT_URLS[@]}")
else
  URLS=("${DEFAULT_URLS[@]}")
fi

ZIP_FILE="clt.zip"
DOWNLOADED=0
for u in "${URLS[@]}"; do
  echo "[clt] Downloading: $u"
  if curl -fL --retry 3 --retry-delay 2 -o "$ZIP_FILE" "$u"; then
    DOWNLOADED=1
    break
  fi
done

if [[ "$DOWNLOADED" -ne 1 ]]; then
  cat <<'EOT' >&2
[clt] ERROR: Could not download cmdline-tools package.
Please download it manually from:
  https://developer.android.com/studio#command-line-tools
Then unzip into: $ANDROID_SDK_ROOT/cmdline-tools/latest
Re-run this script afterwards.
EOT
  exit 1
fi

echo "[clt] Unzipping cmdline-tools…"
unzip -q "$ZIP_FILE"

# The archive contains a top-level directory named "cmdline-tools"
if [[ ! -d cmdline-tools ]]; then
  echo "[clt] ERROR: Unexpected archive structure; 'cmdline-tools' directory not found." >&2
  exit 1
fi

TARGET_DIR="$ANDROID_SDK_ROOT/cmdline-tools/latest"
if [[ -e "$TARGET_DIR" ]]; then
  mv "$TARGET_DIR" "$ANDROID_SDK_ROOT/cmdline-tools/latest.bak-$(date +%Y%m%d-%H%M%S)"
fi
mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"
mv cmdline-tools "$TARGET_DIR"
echo "[clt] Installed to $TARGET_DIR"

# 3) Ensure PATH includes cmdline-tools and platform-tools
export PATH="$TARGET_DIR/bin:$ANDROID_SDK_ROOT/cmdline-tools/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH"
hash -r || true

if ! command -v sdkmanager >/dev/null 2>&1; then
  echo "[clt] ERROR: sdkmanager still not found on PATH. Check $TARGET_DIR/bin" >&2
  exit 1
fi

# 4) JAVA_HOME: prefer 17 on macOS, otherwise keep existing
if [[ "$OS_NAME" == "Darwin" ]] && command -v /usr/libexec/java_home >/dev/null 2>&1; then
  export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null || /usr/libexec/java_home 2>/dev/null || echo "")
  if [[ -n "${JAVA_HOME:-}" ]]; then
    echo "[clt] Using JAVA_HOME=$JAVA_HOME"
  else
    echo "[clt] WARN: JAVA_HOME not set; consider installing Java 17 (Temurin)."
  fi
fi

# 5) Install essential SDK components
echo "[clt] Installing SDK components…"
yes | sdkmanager --licenses || true
sdkmanager --install \
  "cmdline-tools;latest" \
  "platform-tools" \
  "platforms;android-34" \
  "build-tools;34.0.0"

echo "[clt] Accepting licenses…"
yes | sdkmanager --licenses || true

# 6) Configure Flutter if available
if command -v flutter >/dev/null 2>&1; then
  flutter config --android-sdk "$ANDROID_SDK_ROOT" || true
  flutter doctor -v || true
else
  echo "[clt] Flutter not found in PATH; skipping flutter config."
fi

cat <<'EON'

[clt] Done.

Persist these to your shell profile (~/.zshrc or ~/.bashrc) if not already present:
  export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"    # macOS default (use $HOME/Android/Sdk on Linux)
  export PATH="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH"
  # macOS only (if available): export JAVA_HOME=$(/usr/libexec/java_home -v 17)

Open a new terminal or run: source ~/.zshrc
Then run: flutter doctor --android-licenses

EON

