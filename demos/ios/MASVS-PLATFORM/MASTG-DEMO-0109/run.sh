#!/bin/bash

PASTEBOARD_PATTERN="UIPasteboard\\.general|setItems\\("
SHARED_CONTAINER_PATTERN="containerURL\\(forSecurityApplicationGroupIdentifier"
FILE_COORDINATION_PATTERN="NSFileCoordinator|coordinate\\(writingItemAt"

grep -nE "$PASTEBOARD_PATTERN|$SHARED_CONTAINER_PATTERN|$FILE_COORDINATION_PATTERN" MastgTest_reversed.swift > output.txt
