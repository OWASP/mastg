---
platform: ios
title: References to iOS IPC APIs Carrying Sensitive Data
id: MASTG-DEMO-0109
code: [swift]
test: MASTG-TEST-0056
kind: fail
---

## Sample

This sample demonstrates several IPC-related paths that carry sensitive values: writing to the general pasteboard and writing a token to an app group shared container.

{{ MastgTest.swift # MastgTest_reversed.swift }}

## Steps

1. Run `run.sh` to search the reversed sample for IPC-related API usage.

{{ run.sh }}

## Observation

The output shows references to general pasteboard usage, app group shared container access, and file coordination APIs in code paths that handle sensitive values.

{{ output.txt }}

## Evaluation

The test case fails because sensitive values are sent through IPC-related channels (`UIPasteboard.general` and an app group shared container) without restrictive controls such as local-only and expiring pasteboard options or minimized shared-container exposure.
