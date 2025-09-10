#!/usr/bin/env bash
set -euo pipefail

# Android SDK + cmdline-tools setup helper for macOS (works on Linux with tweaks)
# - Sets ANDROID_SDK_ROOT and PATH
# - Tries to select a suitable JAVA_HOME (17 preferred)
# - Optionally installs SDK components and accepts licenses when AUTO_INSTALL=1
# - Integrates SDK path with Flutter

echo "[android_setup] Starting…"

# 1) Detect OS and set default SDK path
OS_NAME=$(uname -s)
if [[ "${OS_NAME}" == "Darwin" ]]; then
  DEFAULT_SDK_ROOT="$HOME/Library/Android/sdk"
else
  DEFAULT_SDK_ROOT="$HOME/Android/Sdk"
fi

export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$DEFAULT_SDK_ROOT}"
mkdir -p "$ANDROID_SDK_ROOT" || true

# 2) Java: prefer 17, fallback 11
if command -v /usr/libexec/java_home >/dev/null 2>&1; then
  if /usr/libexec/java_home -V 2>&1 | grep -q "1[7]"; then
    export JAVA_HOME=$(/usr/libexec/java_home -v 17)
  elif /usr/libexec/java_home -V 2>&1 | grep -q "1[1]"; then
    export JAVA_HOME=$(/usr/libexec/java_home -v 11)
  else
    # Best effort: pick any available Java
    export JAVA_HOME=$(/usr/libexec/java_home || true)
  fi
fi

if [[ -z "${JAVA_HOME:-}" ]]; then
  echo "[android_setup] WARN: JAVA_HOME not set. Install Java 17 (Temurin) and re-run."
else
  echo "[android_setup] Using JAVA_HOME=$JAVA_HOME"
fi

# 3) PATH additions for cmdline-tools and platform-tools
CMDLINE_LATEST_BIN="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin"
PLATFORM_TOOLS="$ANDROID_SDK_ROOT/platform-tools"
export PATH="$CMDLINE_LATEST_BIN:$PLATFORM_TOOLS:$PATH"

echo "[android_setup] ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"
echo "[android_setup] PATH updated with cmdline-tools and platform-tools"

# 4) Ensure sdkmanager exists or guide installation
if ! command -v sdkmanager >/dev/null 2>&1; then
  if [[ -x "$CMDLINE_LATEST_BIN/sdkmanager" ]]; then
    echo "[android_setup] Found sdkmanager at $CMDLINE_LATEST_BIN"
  else
    cat <<'EOT'
[android_setup] sdkmanager not found.

Install via Android Studio:
  - Open Android Studio → Settings/Preferences → Appearance & Behavior → System Settings → Android SDK → SDK Tools
  - Check: "Android SDK Command-line Tools (latest)" and "Android SDK Platform-Tools" → Apply

Or via manual download (advanced):
  - Download Command line tools from: https://developer.android.com/studio#command-line-tools
  - Unzip into: $ANDROID_SDK_ROOT/cmdline-tools/latest

Re-run this script after installing cmdline-tools.
EOT
    exit 1
  fi
fi

# 5) Optional: install minimal components and accept licenses
if [[ "${AUTO_INSTALL:-0}" == "1" ]]; then
  echo "[android_setup] Installing SDK components…"
  sdkmanager --install \
    "cmdline-tools;latest" \
    "platform-tools" \
    "platforms;android-34" \
    "build-tools;34.0.0"

  echo "[android_setup] Accepting licenses…"
  yes | sdkmanager --licenses || true
else
  echo "[android_setup] Skipping installs (set AUTO_INSTALL=1 to auto-install components)."
fi

# 6) Integrate with Flutter (if available)
if command -v flutter >/dev/null 2>&1; then
  echo "[android_setup] Configuring Flutter SDK path…"
  flutter config --android-sdk "$ANDROID_SDK_ROOT" || true
  echo "[android_setup] Flutter doctor (summary):"
  flutter doctor -v || true
else
  echo "[android_setup] Flutter not detected in PATH; skipping flutter config."
fi

cat <<'EON'

[android_setup] Done.

To persist environment variables, add to your ~/.zshrc or ~/.bashrc:

  export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"   # macOS default
  export PATH="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH"
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)

Then start a new terminal and (optional):

  AUTO_INSTALL=1 tools/android_setup.sh

EON

