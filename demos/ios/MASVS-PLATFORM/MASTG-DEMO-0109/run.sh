#!/bin/bash

PASTEBOARD_PATTERN='UIPasteboard\.general\.string|UIPasteboard\.general\.setItems\('
SHARED_CONTAINER_PATTERN='containerURL\(forSecurityApplicationGroupIdentifier'
FILE_COORDINATION_PATTERN='NSFileCoordinator|coordinate\(writingItemAt'

if [ ! -f MastgTest_reversed.swift ]; then
    echo "MastgTest_reversed.swift not found" >&2
    exit 1
fi

if ! grep -nE "$PASTEBOARD_PATTERN|$SHARED_CONTAINER_PATTERN|$FILE_COORDINATION_PATTERN" MastgTest_reversed.swift > output.txt; then
    : > output.txt
fi

if [ -s output.txt ]; then
    perl -0pi -e 's/\n\z//' output.txt
fi
