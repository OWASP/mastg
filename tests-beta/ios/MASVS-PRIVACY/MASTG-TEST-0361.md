---
title: Runtime Use of Protected Resource APIs Without Accurate Purpose Strings
platform: ios
id: MASTG-TEST-0361
type: [dynamic, hooks, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0051]
knowledge: [MASTG-KNOW-0077]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0360. See @MASTG-TEST-0360 for background on the relationship between protected resources, usage description keys, purpose strings, and framework APIs.

This test verifies which protected resource authorization or access APIs the app actually reaches at runtime, and whether the observed runtime behavior is justified by user-visible functionality and accurately explained by the corresponding purpose strings.

!!! note "Out of Scope"

    This test does not check for declared purpose strings where no corresponding protected resource API use is present. A declared purpose string is not a privacy violation by itself.

    This test does not check for reachable protected resource access without a matching required purpose string. That issue is treated separately as an App Store blocker. Missing purpose strings can cause access to fail or the app to exit, and App Store Connect may report this as `ITMS-90683: Missing purpose string in Info.plist`.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0153 to retrieve the `Info.plist` file.
3. Use @MASTG-TECH-0138 to convert the `Info.plist` file to a readable format if needed.
4. Use @MASTG-TECH-0154 to inspect all `*UsageDescription` keys.
5. Use @MASTG-TECH-0095 to hook the relevant APIs.
6. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain:

- The usage description keys declared by the app together with their user-facing purpose strings.
- The protected resource authorization or access APIs observed during app usage.

For each observed call, record:

- Method names and classes.
- Arguments and requested resource type, where available.
- Return values or authorization status, where available.
- Call stack or backtrace to understand the context.
- The user action, screen, or app flow that triggered the call.
- Whether a system authorization prompt was displayed, and which purpose string was shown.

## Evaluation

The test case fails if there is evidence that the app has a runtime code path that requests or accesses a protected resource and the purpose string does not meaningfully, accurately, and specifically explain why the app needs that protected resource.

The test case also fails if a runtime code path requests or accesses a protected resource without a matching required purpose string.

**Further Validation Required:**

Use the observed runtime calls, trigger conditions, declared purpose strings, app metadata and App Store information, visible app features, prompts, and backtraces to determine whether each protected resource access path is justified and accurately explained.

Consider the following when evaluating:

- Is the observed protected resource access connected to the user action, screen, or user-visible feature that triggered it?
- Does the purpose string accurately and specifically explain the observed protected resource access, without being vague, generic, deceptive, or inconsistent with the runtime behavior?

Runtime analysis may miss protected resource access in flows that were not triggered, code paths that depend on account state, device state, location, granted permissions, remote configuration, feature flags, backend responses, unavailable hardware, or app extensions. Treat missing runtime calls as absence of evidence, not proof that the app never requests or accesses the protected resource.

Use static analysis to complement runtime analysis and identify APIs that are present in the app's code but were not observed at runtime. See @MASTG-TEST-0360.
