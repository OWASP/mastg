---
title: References to Implicit Intents Carrying Sensitive Extras
platform: android
id: MASTG-TEST-0374
type: [static, code]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0056]
knowledge: [MASTG-KNOW-0025, MASTG-KNOW-0137]
profiles: [L1, L2]
---

## Overview

Sending sensitive data (such as authentication tokens, passwords, or personally identifiable information) as extras in an implicit intent is insecure. Implicit intents are resolved by the Android system and can be received by any app that registers a matching intent filter, exposing the sensitive data to unauthorized parties. Sensitive data should only be transmitted via explicit intents or other secure IPC mechanisms.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain instances where an implicit `Intent` is populated with sensitive extras such as tokens, passwords, or personally identifiable information.

## Evaluation

The test case fails if the app sends sensitive data via implicit intents.
