---
title: Implementing Root Detection
alias: implementing-root-detection
id: MASTG-BEST-0030
platform: generic
knowledge: [MASTG-KNOW-0027]
---

Root detection is an environment risk signal that helps identify devices with elevated privilege or common rooting artifacts. It is a cost raising measure and it is bypassable, so it should be used only when rooted device risk materially impacts the app.

Apply the relevant root detection techniques described in @MASTG-KNOW-0027 based on the app's threat model and risk tolerance.

## Best Practices

1. **Layer defenses:** Pair root signals with integrity checks, anti debugging signals, and backend enforcement.
2. **Distribute checks:** Place checks near sensitive operations and session establishment, avoid a single centralized gate.
3. **Use multiple methods:** Combine filesystem artifacts, property checks, process checks, and native level checks.
4. **Avoid well-known patterns only:** Do not rely only on public signature lists or a single library default configuration.
5. **Use proportional responses:** Limit high risk operations first, add step up authentication, avoid full lockout when confidence is low.
6. **Validate server-side:** Use server policy to decide whether to allow transactions, based on risk and user context.
7. **Rotate and randomize:** Vary which checks run per session and per release, rotate indicators to reduce reusable bypass patches.

## Caveats and Considerations

Root detection is inherently bypassable through hooking and patching, and by hiding root artifacts. See @MASTG-TECH-0144.

Root detection can flag legitimate scenarios such as custom ROMs, enterprise test devices, and security research environments. Aggressive blocking can push users to modified app builds, and can increase support costs.
