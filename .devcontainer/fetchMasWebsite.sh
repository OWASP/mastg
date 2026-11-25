#!/usr/bin/env bash
set -euo pipefail

URL="https://github.com/OWASP/mas-website.git"
DIR="/workspace/mas-website"

if [ ! -d "$DIR/.git" ]; then
    find "$DIR" -mindepth 1 -delete
    git -C "$DIR" init -q
    git -C "$DIR" remote add origin "$URL"
fi

git -C "$DIR" remote set-url origin "$URL"
git -C "$DIR" fetch --depth 1 origin main
git -C "$DIR" reset --hard FETCH_HEAD
git -C "$DIR" clean -fd
