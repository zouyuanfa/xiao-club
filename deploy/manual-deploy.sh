#!/usr/bin/env bash
set -euo pipefail

REPO_DIR="${REPO_DIR:-/root/xiao-club}"
FRONTEND_DIR="$REPO_DIR/src"
BACKEND_DIR="$REPO_DIR/backend"
WEB_ROOT="${WEB_ROOT:-/var/www/html/xiaoclub}"
JAR_NAME="tang-club-backend-1.0.0.jar"
JAR_PATH="$BACKEND_DIR/target/$JAR_NAME"
BACKEND_LOG="${BACKEND_LOG:-/root/backend.log}"
PID_FILE="/run/xiao-club.pid"

log() {
  printf '[%s] %s\n' "$(date '+%F %T')" "$*"
}

if [[ "$(id -u)" -ne 0 ]]; then
  echo "Run this script as root." >&2
  exit 1
fi

for command_name in git npm java mvn nginx curl; do
  command -v "$command_name" >/dev/null
done

if [[ ! -d "$REPO_DIR/.git" ]]; then
  echo "Git repository not found: $REPO_DIR" >&2
  exit 1
fi

if [[ ! -f "$BACKEND_DIR/src/main/resources/application.yml" \
   && ! -f "$BACKEND_DIR/src/main/resources/application.properties" ]]; then
  echo "Production application configuration is missing." >&2
  exit 1
fi

log "Pulling latest main branch"
git -C "$REPO_DIR" fetch origin main
git -C "$REPO_DIR" checkout main
git -C "$REPO_DIR" pull --ff-only origin main

log "Building frontend"
cd "$FRONTEND_DIR"
npm ci
npm run build
test -f dist/index.html

log "Building backend"
cd "$BACKEND_DIR"
mvn --batch-mode clean package -DskipTests
test -f "$JAR_PATH"

log "Publishing frontend"
install -d -m 755 "$WEB_ROOT"
find "$WEB_ROOT" -mindepth 1 -maxdepth 1 -exec rm -rf -- {} +
cp -a "$FRONTEND_DIR/dist/." "$WEB_ROOT/"
chown -R www-data:www-data "$WEB_ROOT"
find "$WEB_ROOT" -type d -exec chmod 755 {} +
find "$WEB_ROOT" -type f -exec chmod 644 {} +

log "Restarting backend"
old_pids="$(pgrep -f '^java .*tang-club-backend-1\.0\.0\.jar$' || true)"
if [[ -n "$old_pids" ]]; then
  kill $old_pids
  for _ in {1..30}; do
    if ! kill -0 $old_pids 2>/dev/null; then
      break
    fi
    sleep 1
  done
fi

cd "$BACKEND_DIR"
nohup java ${JAVA_OPTS:-} -jar "target/$JAR_NAME" \
  >>"$BACKEND_LOG" 2>&1 &
echo "$!" >"$PID_FILE"

for _ in {1..60}; do
  if ss -lnt 2>/dev/null | grep -q ':8080 '; then
    break
  fi
  if ! kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
    tail -n 80 "$BACKEND_LOG" >&2
    exit 1
  fi
  sleep 1
done

ss -lnt 2>/dev/null | grep -q ':8080 '

log "Reloading nginx"
nginx -t
systemctl reload nginx

frontend_status="$(curl -k -sS -o /dev/null -w '%{http_code}' \
  https://wance.fun/xiaoclub/)"
api_status="$(curl -k -sS -X OPTIONS -o /dev/null -w '%{http_code}' \
  https://wance.fun/api/survey/add)"

if [[ "$frontend_status" != "200" || "$api_status" != "200" ]]; then
  echo "Health check failed: frontend=$frontend_status api=$api_status" >&2
  exit 1
fi

log "Deployment completed"
printf 'commit=%s\n' "$(git -C "$REPO_DIR" rev-parse --short HEAD)"
printf 'frontend=%s api=%s\n' "$frontend_status" "$api_status"
