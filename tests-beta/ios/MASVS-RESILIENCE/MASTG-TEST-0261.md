---
platform: ios
title: Debuggable Entitlement Enabled in the entitlements.plist
id: MASTG-TEST-0261
type: [static, code]
weakness: MASWE-0067
profiles: [R]
knowledge: [MASTG-KNOW-0062]
---

## Overview

The test evaluates whether an iOS application is configured to allow debugging. If an app is debuggable, attackers can leverage debugging tools (see @MASTG-TECH-0084) to analyse the runtime behaviour of the app, and potentially compromise sensitive data or functionality.

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0111 to extract entitlements from the main binary.

## Observation

The output should contain the entitlements embedded in the app.

## Evaluation

The test case fails if the `get-task-allow` entitlement is present and set to `true`.
