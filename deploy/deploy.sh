#!/usr/bin/env bash
set -euo pipefail

ARCHIVE="${1:-/tmp/xiao-club-release.tar.gz}"
APP_ROOT="/opt/xiao-club"
RELEASES_DIR="$APP_ROOT/releases"
RELEASE_DIR="$RELEASES_DIR/$(date +%Y%m%d%H%M%S)"
CURRENT_LINK="$APP_ROOT/current"

if [[ ! -f "$ARCHIVE" ]]; then
  echo "Release archive not found: $ARCHIVE" >&2
  exit 1
fi

mkdir -p "$RELEASE_DIR"
tar -xzf "$ARCHIVE" -C "$RELEASE_DIR"

test -f "$RELEASE_DIR/frontend/index.html"
test -f "$RELEASE_DIR/backend/xiao-club.jar"

ln -sfn "$RELEASE_DIR" "$APP_ROOT/current.next"
mv -Tf "$APP_ROOT/current.next" "$CURRENT_LINK"

systemctl restart xiao-club
nginx -t
systemctl reload nginx
systemctl is-active --quiet xiao-club

rm -f "$ARCHIVE"
find "$RELEASES_DIR" -mindepth 1 -maxdepth 1 -type d -printf '%T@ %p\n' \
  | sort -nr \
  | tail -n +6 \
  | cut -d' ' -f2- \
  | xargs -r rm -rf

echo "Deployment completed: $RELEASE_DIR"
