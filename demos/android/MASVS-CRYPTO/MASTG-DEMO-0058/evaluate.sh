#!/bin/bash

jq -r -s '
  flatten
  | .[]
  | select(
      .method=="setBlockModes"
      and any(.inputParameters[]?.value[]?; . == "ECB")
    )
  | "Class: \(.class), Method: \(.method), Block modes: \([.inputParameters[]?.value[]?] | join(", "))"
' output.json > evaluation.txt