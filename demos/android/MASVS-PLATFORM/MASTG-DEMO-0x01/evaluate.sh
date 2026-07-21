#!/bin/bash

# Stage 4 (reduce) — classify the context-registered receivers found in Stage 2 into two lists,
# resolving each broadcastPermission to its declared protection level so the reader does not have
# to look it up by hand.

# "permission name <TAB> protection level" pairs from the declared-permission scan (Stage 1b).
PERMS=$(jq -r '.results[]
  | select(.check_id | test("declared-permission"))
  | .extra.message
  | gsub("\\s+"; " ")
  | capture("permission=(?<n>[^ ]+) level=(?<l>[^ ]+)")
  | "\(.n)\t\(.l)"' permissions_scan.json 2>/dev/null)

# Look up the protection level declared for a permission name (empty if undeclared).
lookup_level() { printf '%s\n' "$PERMS" | awk -F'\t' -v n="$1" '$1==n{print $2; exit}'; }

# Resolve a broadcastPermission token (a string literal or a constant reference such as
# MastgTest.PERMISSION_ADMIN_COMMAND) to the actual permission name by reading the decompiled
# constant declaration.
resolve_name() {
  case "$1" in
    \"*\") printf '%s' "${1//\"/}";;
    *) local simple="${1##*.}"
       grep -oE "String[[:space:]]+${simple}[[:space:]]*=[[:space:]]*\"[^\"]+\"" MastgTest_reversed.java \
         | grep -oE '"[^"]+"' | tr -d '"' | head -1;;
  esac
}

{
  # Reported as vulnerable: exported (RECEIVER_EXPORTED) with no broadcastPermission. Any app on
  # the device can reach these, so they are the candidates to inspect for sensitive functionality.
  echo "# Exported, no permission (reported as vulnerable):"
  jq -r '.results[]
    | select(.check_id | endswith("context-registered-receiver-exported"))
    | .extra.message
    | sub("(?s).*receiver="; "") | gsub("\\s+"; "") | sub("^this\\."; "")' code_scan.json

  echo
  # Exported but restricted with a broadcastPermission: only safe if the resolved protection level
  # is strong (signature/knownSigner/internal). A normal or dangerous level (or none) is reported
  # as vulnerable, because untrusted apps can obtain it and send the broadcast.
  echo "# Exported, permission-restricted (protection level resolved from the manifest):"
  jq -r '.results[]
    | select(.check_id | endswith("context-registered-receiver-exported-with-permission"))
    | .extra.message
    | gsub("\\s+"; " ")
    | capture("receiver=(?<r>[^ ]+) permission=(?<p>[^ ]+)")
    | "\(.r)\t\(.p)"' code_scan.json \
  | while IFS=$'\t' read -r receiver token; do
      receiver="${receiver#this.}"
      name=$(resolve_name "$token")
      [ -z "$name" ] && name="$token"
      level=$(lookup_level "$name")
      case "$level" in
        *signature*|*knownSigner*|*internal*) verdict="OK (strong, untrusted apps cannot hold it)";;
        "") verdict="REVIEW (permission not declared in this manifest; resolve it elsewhere)";;
        *) verdict="WEAK -> treat as vulnerable (normal/dangerous can be held by untrusted apps)";;
      esac
      echo "$receiver permission=$name protectionLevel=${level:-unknown} -> $verdict"
    done
} > evaluation.txt
