#!/usr/bin/env bash
set -euo pipefail

# TODO: REVERT before PR
repo_url="https://github.com/bernhste/mas-website.git"
# repo_url="https://github.com/OWASP/mas-website.git"
dir="/workspace/mas-website"

# TODO: REMOVE before PR
branch="new-devcontainer"

if [ -d "${dir}/.git" ]; then
  # TODO: REVERT before PR
  # git -C "${dir}" fetch --depth 1 origin
  # git -C "${dir}" reset --hard origin/HEAD
  git -C "${dir}" fetch --depth 1 origin "${branch}"
  git -C "${dir}" checkout -B "${branch}" FETCH_HEAD
  git -C "${dir}" reset --hard FETCH_HEAD
else
  # git clone --depth 1 "${repo_url}" "${dir}"
  git clone --depth 1 --branch "${branch}" --single-branch "${repo_url}" "${dir}"
fi


