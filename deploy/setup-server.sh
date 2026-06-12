#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [[ "$(id -u)" -ne 0 ]]; then
  echo "Run this script as root." >&2
  exit 1
fi

command -v java >/dev/null
command -v nginx >/dev/null

install -d -o www-data -g www-data /opt/xiao-club/releases
install -d -o www-data -g www-data /var/log/xiao-club
install -d -m 700 /etc/xiao-club
install -m 755 "$SCRIPT_DIR/deploy.sh" /opt/xiao-club/deploy.sh
install -m 644 "$SCRIPT_DIR/xiao-club.service" /etc/systemd/system/xiao-club.service
install -m 644 "$SCRIPT_DIR/xiao-club.nginx.conf" /etc/nginx/sites-available/xiao-club

if [[ ! -f /etc/xiao-club/xiao-club.env ]]; then
  install -m 600 "$SCRIPT_DIR/xiao-club.env.example" /etc/xiao-club/xiao-club.env
  echo "Created /etc/xiao-club/xiao-club.env. Update its database credentials before deploying."
fi

ln -sfn /etc/nginx/sites-available/xiao-club /etc/nginx/sites-enabled/xiao-club
rm -f /etc/nginx/sites-enabled/default

systemctl daemon-reload
systemctl enable xiao-club
nginx -t
systemctl reload nginx

echo "Server setup completed."
