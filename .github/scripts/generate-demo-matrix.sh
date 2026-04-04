#!/bin/bash
# Generate a matrix of demo directories for GitHub Actions
# Usage: generate-demo-matrix.sh <platform> <event_name> [base_sha]
# Example: generate-demo-matrix.sh android pull_request abc123

set -e
shopt -s nullglob  # Make globs expand to nothing if no matches

PLATFORM="$1"
EVENT_NAME="$2"
BASE_SHA="$3"

if [ -z "$PLATFORM" ] || [ -z "$EVENT_NAME" ]; then
  echo "Usage: $0 <platform> <event_name> [base_sha]" >&2
  exit 1
fi

DEMO_PATH="demos/${PLATFORM}"

if [ "$EVENT_NAME" = "pull_request" ]; then
  if [ -z "$BASE_SHA" ]; then
    echo "Error: base_sha is required for pull_request events" >&2
    exit 1
  fi

  # Get list of changed files in demos/<platform>/ directory
  changed_files=$(git diff --name-only "$BASE_SHA" HEAD -- "${DEMO_PATH}/*")

  echo "Changed files:" >&2
  echo "$changed_files" >&2

  # Extract unique demo directories
  matrix=$(echo "$changed_files" | grep -oE "${DEMO_PATH}/[^/]*/MASTG-DEMO-[^/]+" | sort -u | head -c -1 | tr '\n' ' ' | sed 's/ /","/g')

  # If no changes, set empty matrix
  if [ -z "$matrix" ]; then
    echo '{"demo":[]}'
  else
    echo "{\"demo\":[\"$matrix\"]}"
  fi
else
  # Default behavior: include all demos for master branch
  demos=(${DEMO_PATH}/*/MASTG-DEMO-*)
  if [ ${#demos[@]} -eq 0 ]; then
    echo '{"demo":[]}'
  else
    matrix=$(printf '%s\n' "${demos[@]}" | sed 's/.*/"&"/' | paste -sd, -)
    echo "{\"demo\":[$matrix]}"
  fi
fi
