#!/usr/bin/env bash
set -euo pipefail

# One-shot helper to build and run iOS on a simulator.

export LANG=${LANG:-en_US.UTF-8}
export LC_ALL=${LC_ALL:-en_US.UTF-8}

DEVICE_ARG=${1:-}
CLEAN=${CLEAN:-0}
LOG_DIR=".ios_build_logs"
LOG_FILE="$LOG_DIR/$(date +%Y%m%d-%H%M%S)-build-run.log"
mkdir -p "$LOG_DIR"

echo "[iOS] Using locale: $LANG $LC_ALL"

echo "[iOS] Flutter doctor (summary)"
flutter doctor -v | tee "$LOG_FILE" || true

echo "[iOS] Precache iOS artifacts"
flutter precache --ios | tee -a "$LOG_FILE" || true

echo "[iOS] Pub get"
flutter pub get | tee -a "$LOG_FILE"

echo "[iOS] CocoaPods install"
pushd ios >/dev/null
pod --version | tee -a "../$LOG_FILE"
if [[ "$CLEAN" == "1" ]]; then
  echo "[iOS] CLEAN=1 → removing Pods and Podfile.lock"
  rm -rf Pods Podfile.lock
  pod repo update | tee -a "../$LOG_FILE" || true
fi
pod install | tee -a "../$LOG_FILE"
popd >/dev/null

if [[ "$CLEAN" == "1" ]]; then
  echo "[iOS] CLEAN=1 → removing DerivedData"
  rm -rf "$HOME/Library/Developer/Xcode/DerivedData"/* || true
fi

echo "[iOS] Preflight: verify workspace and settings"
if ! xcodebuild -list -workspace ios/Runner.xcworkspace -json >/dev/null 2>&1; then
  echo "[iOS] ERROR: ios/Runner.xcworkspace not recognized by xcodebuild." | tee -a "$LOG_FILE"
  exit 1
fi

echo "[iOS] Building for simulator (no codesign)"
flutter build ios --simulator | tee -a "$LOG_FILE" || true

echo "[iOS] If Flutter build failed, attempting direct xcodebuild for clearer errors…"
xcodebuild -workspace ios/Runner.xcworkspace -scheme Runner -configuration Debug -sdk iphonesimulator \
  -destination 'platform=iOS Simulator,name=iPhone SE (3rd generation)' \
  CODE_SIGNING_ALLOWED=NO build | tee -a "$LOG_FILE" || true

TARGET_DEVICE="${DEVICE_ARG}"
if [[ -z "$TARGET_DEVICE" ]]; then
  # Prefer an already booted iOS simulator if any
  BOOTED=$(xcrun simctl list devices booted iOS --json 2>/dev/null | awk '/\"udid\"/ { gsub(/[",]/, ""); print $2; exit }' || true)
  if [[ -n "$BOOTED" ]]; then
    TARGET_DEVICE="$BOOTED"
  else
    TARGET_DEVICE="ios"
  fi
fi

echo "[iOS] Running app on device: $TARGET_DEVICE"
exec flutter run -d "$TARGET_DEVICE" --no-pub -v | tee -a "$LOG_FILE"
