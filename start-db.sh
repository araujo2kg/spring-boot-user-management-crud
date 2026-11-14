#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

if [[ ! -f .env ]]; then
    cp .env.example .env
    echo "Created .env. Set its passwords, then run this script again."
    exit 1
fi

docker compose up -d --wait --wait-timeout 120 mysql
