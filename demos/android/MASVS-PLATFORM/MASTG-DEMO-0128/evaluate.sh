#!/bin/bash

# Stage 3 (reduce) — from the exported, unprotected activities found in Stage 1, keep only the
# ones the app itself declares. Activities in framework/library namespaces (android.*, androidx.*,
# com.google.android.*) are shipped by dependencies, not authored by the app, so they are triaged
# separately. Whatever remains is the shortlist to inspect manually for sensitive functionality.
jq -r '.results[]
  | select(.check_id | endswith("activity-exported-without-permission"))
  | .extra.message
  | sub("(?s).*activity="; "") | gsub("\\s+"; "")' manifest_scan.json \
  | grep -vE '^(android|androidx|com\.google\.android|com\.android)\.' \
  > evaluation.txt
