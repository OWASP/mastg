#!/usr/bin/env bash
set -euo pipefail

clone_or_update() {
  local repo_url="$1"
  local dir="$2"

  if [ -d "${dir}/.git" ]; then
    git -C "${dir}" fetch --depth 1 origin
    git -C "${dir}" reset --hard origin/HEAD
  else
    git clone --depth 1 "${repo_url}" "${dir}"
  fi
}

clone_or_update "https://github.com/OWASP/masvs.git"       /workspace/masvs
clone_or_update "https://github.com/OWASP/maswe.git"       /workspace/maswe
clone_or_update "https://github.com/OWASP/mas-website.git" /workspace/mas-website
