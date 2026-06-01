---
platform: android
title: References to StrictMode APIs
id: MASTG-TEST-0265
type: [static, code]
weakness: MASWE-0094
best-practices: []
profiles: [R]
---

## Overview

This test checks whether the app uses `StrictMode`. While useful for developers to log policy violations such as disk I/O or network operations during development, it can expose sensitive implementation details in the logs that could be exploited by attackers.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should identify all instances of `StrictMode` usage in the app.

## Evaluation

The test case fails if the app uses `StrictMode` APIs.
