#!/usr/bin/env bash
set -euo pipefail

repo_url="https://github.com/OWASP/mas-website.git"
dir="/workspace/mas-website"

if [ -d "${dir}/.git" ]; then

  git -C "${dir}" fetch --depth 1 origin
  git -C "${dir}" reset --hard origin/HEAD
else
  git clone --depth 1 "${repo_url}" "${dir}"
fi
