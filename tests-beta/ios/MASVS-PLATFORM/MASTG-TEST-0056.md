---
platform: ios
title: Determining Whether Sensitive Data Is Exposed via IPC Mechanisms
id: MASTG-TEST-0056
type: [static]
weakness: MASWE-0053
threat: [app]
best-practices: [MASTG-BEST-0042]
profiles: [L1, L2]
prerequisites:
- identify-sensitive-data
knowledge: [MASTG-KNOW-0121]
---

## Overview

iOS apps can exchange data with other processes through mechanisms such as the general pasteboard, URL-based handoff, app groups, and extension-related file coordination. If an app writes sensitive values to those channels without strict scope and lifetime controls, another local app or process can read data that was not intended for it.

This test verifies whether the app exposes sensitive data through IPC-related APIs, entitlements, or shared containers that can be reached outside the app's intended trust boundary.

## Steps

1. Extract the app package and binaries as described in @MASTG-TECH-0058.
2. Run static analysis using @MASTG-TECH-0070 to find references to iOS IPC mechanisms, for example `UIPasteboard.general`, `UIPasteboard.setItems`, URL handling APIs, app group container APIs, `NSFileCoordinator`, `CFMessagePort`, and `NSXPCConnection`.
3. Review each reported code path with @MASTG-TECH-0076 and determine whether sensitive data is written to those IPC channels, including how scope and lifetime are restricted.

## Observation

The output should contain a list of IPC-related call sites and data flows where sensitive values are written to shared channels.

## Evaluation

The test case fails if sensitive data is written to IPC mechanisms that are accessible beyond the app's intended trust boundary or without adequate scope and lifetime restrictions.

For each finding, verify whether:

- The transmitted data is classified as sensitive.
- The IPC channel can be reached by other apps or processes.
- The implementation applies restrictive controls (for example, local-only/expiring pasteboard entries, narrowly scoped shared containers, or short-lived exchange data).
