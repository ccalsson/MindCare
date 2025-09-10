#!/usr/bin/env bash
set -euo pipefail

# Helper to prep and run the iOS app on a simulator

export LANG=${LANG:-en_US.UTF-8}
export LC_ALL=${LC_ALL:-en_US.UTF-8}

echo "[ios_run] Flutter doctor summary:"
flutter doctor -v || true

echo "[ios_run] Precache iOS engine…"
flutter precache --ios || true

echo "[ios_run] Ensuring CocoaPods are installed…"
pushd ios >/dev/null
pod --version || (echo "[ios_run] CocoaPods not installed. Install with: sudo gem install cocoapods" && exit 1)
pod install
popd >/dev/null

echo "[ios_run] Listing devices…"
flutter devices || true

TARGET=${1:-ios}
echo "[ios_run] Running on target: $TARGET"
exec flutter run -d "$TARGET"

