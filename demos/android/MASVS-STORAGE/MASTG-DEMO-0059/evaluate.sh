#!/bin/bash

jq -r -s '
  flatten
  | .[]
  | "Class: \(.class), Method: \(.method), Params: \([.inputParameters[]?.value?] | join(", "))"
' output.json > evaluation.txt