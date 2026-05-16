---
platform: android
title: Internal Component Unintentionally Exported
id: MASTG-TEST-0x03
type: [static]
weakness: MASWE-0066
best-practices: [MASTG-BEST-0x14]
knowledge: [MASTG-KNOW-0025]
profiles: [L1, L2]
---

## Overview

Android app components (activities, services, broadcast receivers, content providers) are declared in `AndroidManifest.xml`. A component can be made available to other apps on the device by setting `android:exported="true"` and, in many cases, by adding an `<intent-filter>` that advertises which intents it handles. Components intended only for internal use within the app must remain unexported. If a developer declares an internal component as exported — explicitly via `android:exported="true"` or implicitly by adding an `<intent-filter>` — the component becomes reachable by any other app on the device. An attacker can then invoke the component directly, regardless of how the original app intended it to be reached, leading to unintended behavior, privilege misuse, or further attacks against the app.

## Steps

1. Run @MASTG-TECH-0014 on the `AndroidManifest.xml` file.

## Observation

The output should contain a component (activity, service, receiver, or provider) that is declared with `android:exported="true"` together with an `<intent-filter>`, despite not being intended as a public entry point of the app.

## Evaluation

The test case fails if the `AndroidManifest.xml` declares an internal component with `android:exported="true"` and an `<intent-filter>` that exposes it to other apps.
