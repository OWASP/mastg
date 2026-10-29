#!/bin/bash

jq -r '
  select(.type == "hook")
  | "Class: \(.class), Method: \(.method), Params: \([
      .inputParameters[]?
      | if (.value | type) == "string" then .value
        elif (.value | type) == "number" then (.value | tostring)
        elif .value == null then "null"
        else "<\(.runtimeType // "object")>"
        end
    ] | join(", "))"
' output.json > evaluation.txt
