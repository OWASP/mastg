---
title: Runtime Use of Entitlement-Backed APIs for Unjustified Capability Exposure
platform: ios
id: MASTG-TEST-0363
type: [dynamic, hooks, manual]
weakness: MASWE-0117
profiles: [P]
best-practices: [MASTG-BEST-0051]
knowledge: [MASTG-KNOW-0077]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0362. See @MASTG-TEST-0362 for background on the relationship between Xcode capabilities, signed entitlements, and entitlement-backed APIs or entry points.

This test verifies which entitlement-backed APIs, shared containers, or system entry points the app actually reaches at runtime, and whether that runtime behavior is justified by the app's signed entitlements and user-visible functionality.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0111 to extract entitlements from the app binaries, including the main app and app extensions.
3. Use @MASTG-TECH-0095 to hook the relevant entitlement-backed APIs, shared container APIs, and system entry points.
4. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain:

- The entitlements embedded in the app's code signatures.
- The entitlement-backed APIs, shared container APIs, or system entry points reached during app usage.

For each observed call or entry point, record:

- Method names and classes.
- The entitlement or capability associated with the observed behavior.
- Relevant identifiers or arguments, such as HealthKit data types, App Group identifiers, iCloud container identifiers, or associated-domain activities.
- Call stack, such as a backtrace, to understand the context.
- The user action or app flow that triggered the call.

## Evaluation

The test case fails if the collected evidence shows that the app uses an entitlement-backed API, shared container, or system entry point without a reasonable connection to a user-visible feature, or if the runtime behavior creates a broader privacy-relevant capability, data access, or data sharing surface than the app's feature set justifies.

**Further Validation Required:**

Use the observed runtime calls, trigger conditions, signed entitlements, app metadata, visible app features, relevant identifiers, and backtraces to determine whether each entitlement-backed runtime behavior is justified, prioritizing behavior that affects personal data, shared storage, cross-app communication, cloud data, home data, network behavior, or system entry points.

Consider the following when evaluating:

- Is the observed entitlement-backed API or entry point reasonably connected to the user action or feature that triggered it?
- Does the observed runtime behavior create a personal data access, shared storage, cross-app communication, or system integration surface that is broader or more sensitive than the feature requires?
- Could the app use a narrower alternative instead, such as local app storage instead of an App Group container when cross-app or app-extension sharing is not required?

Dynamic analysis may miss entitlement-backed APIs or entry points in flows that were not triggered, code paths that depend on account state, device state, location, permissions granted, remote configuration, feature flags, experiments, extensions, associated domains, backend responses, or unavailable hardware. Treat missing runtime calls as absence of evidence, not proof that the app never uses the entitlement-backed service.

Use static analysis to complement the runtime analysis and identify entitlement-backed APIs, identifiers, or entry points that are present in the app's code but were not observed at runtime. See @MASTG-TEST-0362.
