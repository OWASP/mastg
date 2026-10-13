#!/usr/bin/env bash
set -euo pipefail

readonly VALID_REPOS=(maswe masvs mastg mas-website)

usage() {
  echo "Usage: $0 <${VALID_REPOS[*]}> [<${VALID_REPOS[*]}> ...]" >&2
  exit 1
}

is_valid_repo() {
  local candidate="$1"
  local repo
  for repo in "${VALID_REPOS[@]}"; do
    if [[ "${candidate}" == "${repo}" ]]; then
      return 0
    fi
  done
  return 1
}

clone_or_update() {
  local name="$1"
  local repo_url="https://github.com/OWASP/${name}.git"
  local dir="/workspace/${name}"

  if [ -d "${dir}/.git" ]; then
    git -C "${dir}" fetch --depth 1 origin
    git -C "${dir}" reset --hard origin/HEAD
  else
    git clone --depth 1 "${repo_url}" "${dir}"
  fi
}

(( $# >= 1 )) || usage

for name in "$@"; do
  is_valid_repo "${name}" || usage
done

for name in "$@"; do
  clone_or_update "${name}"
done