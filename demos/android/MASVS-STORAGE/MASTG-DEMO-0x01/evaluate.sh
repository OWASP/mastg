#!/bin/bash

jq -r '
  select(.type == "hook")
  | "Class: \(.class), Method: \(.method), Params: \([.inputParameters[]?.value?] | join(", "))"
' output.json > evaluation.txt
