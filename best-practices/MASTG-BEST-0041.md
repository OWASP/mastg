---
title: Hardening Against Runtime Hooking
alias: hardening-against-runtime-hooking
id: MASTG-BEST-0041
platform: android
knowledge: [MASTG-KNOW-0027, MASTG-KNOW-0030, MASTG-KNOW-0032, MASTG-KNOW-0118, MASTG-KNOW-0033, MASTG-KNOW-0119, MASTG-KNOW-0120]
---

Defending against runtime hooking requires a layered approach that combines several types of security controls:

- **Preventive controls**: Implement root detection (@MASTG-KNOW-0027) and device/app attestation (@MASTG-KNOW-0120, @MASTG-KNOW-0119) as the first line of defense, since most hooking frameworks (e.g., Frida server, Xposed) require rooted devices.
- **Detective controls**: Scan for tool signatures using artifact-based detection (@MASTG-KNOW-0030) and verify the app's code and memory integrity at runtime (@MASTG-KNOW-0032) to detect hooking attempts.
- **Deterrent controls**: Obfuscate detection logic, scatter checks throughout the app, and vary their timing to increase the cost and effort required to bypass protections.
- **Responsive controls**: Terminate the session, clear sensitive data from memory, or even alert the backend server when a threat is detected.

Because hooking can also occur on non-rooted devices (e.g., by repackaging the app with an embedded frida-gadget, see @MASTG-TECH-0026), do not rely solely on preventive controls. Apply the detective, deterrent, and responsive controls described below to protect against hooking regardless of the device's root status.

## Detective Controls

### Combine Artifact-Based and Integrity-Based Detection

Implement both artifact-based detection (@MASTG-KNOW-0030) and runtime integrity verification (@MASTG-KNOW-0032). Use artifact-based detection to scan for known tool signatures (e.g., Frida server processes, libraries, open ports) and runtime integrity verification to detect the _modifications_ these tools make to the app's code and memory (e.g., GOT hooks, inline trampolines, ART method entry point changes). Do not rely on only one approach, as each has blind spots the other covers.

### Apply Multiple Detection Techniques

Layer several techniques to maximize detection coverage:

- **Memory scanning**: Scan `/proc/self/maps` and process memory for known artifacts (e.g., "LIBFRIDA", frida-agent libraries, Xposed bridge classes).
- **Integrity checksums**: Compute checksums of critical code sections at build time and verify them periodically at runtime to detect patches and inline hooks.
- **GOT/PLT verification**: Verify that Global Offset Table entries point to addresses within their expected libraries.
- **Function prologue inspection**: Compare the first bytes of security-critical functions against their expected values to detect trampoline patterns (e.g., `LDR X16, .+8; BR X16` on ARM64).
- **ART method verification**: Use JNI's `FromReflectedMethod` to confirm that Java method entry points fall within legitimate regions (OAT file, interpreter, or JIT code cache).
- **Network-based checks**: Probe for D-Bus responses on open ports to reveal frida-server even when renamed.

## Deterrent Controls

### Implement Detection in Native Code

Consider writing detection checks in native (C/C++) code rather than Java/Kotlin. Native code is significantly harder to hook and reverse engineer than Java bytecode, which can be easily intercepted via Frida's Java API or Xposed modules.

!!! note
  Remember, that if you use JNI to bridge results back to the application layer for any responsive controls, an attacker can tamper the results.

### Obfuscate Detection Logic

Apply code obfuscation (@MASTG-KNOW-0033) across all detection routines. Scatter checks throughout the application instead of centralizing them in a single function, and vary their timing - such as periodically, randomly, or based on events (see [Repeat Checks Before Sensitive Operations](#repeat-checks-before-sensitive-operations) - to prevent systematic bypassing.)

### Inline Detection Logic

Avoid placing detection code in dedicated, callable functions. A named function has a fixed entry point that hooking frameworks can intercept with a single hook. When detection logic is inlined directly into the surrounding application code, there is no entry point to target — the check becomes an inseparable part of the surrounding control flow.

The most reliable approach is to copy the detection logic directly at each call site, leaving no function entry point to target. When compiler assistance is preferred, use `__attribute__((always_inline))` in native C/C++ or `private inline` in Kotlin for a compile-time inlining guarantee.

### Randomize Check Placement Per Release

Beyond scattering checks within a single build, vary which call sites are active and where they appear with each release. Because most app updates leave the overall logic largely unchanged, static analysis of one version can be reused to find and patch checks in the next. Randomizing check placement at build time forces attackers to do fresh analysis for every release, significantly increasing the cost of maintaining automated bypass tools.

Use build-time code transformation (such as LLVM passes, custom Gradle tasks, or R8/ProGuard rules) to inject detection calls at different locations, move or remove existing call sites, and change the number of active checks between builds. Combine this with native code implementation and obfuscation for maximum impact.

### Repeat Checks Before Sensitive Operations

Perform checks multiple times from different locations, especially immediately before critical actions like starting a transfer, unlocking a premium feature, or submitting credentials. Spreading out checks to obscure their purpose can be helpful, but an attacker who patches one instance can still access the critical step. Making all redundant checks pass before a sensitive operation completes ensures that each bypassed instance becomes a potential failure point, significantly increasing the difficulty for an attacker to succeed.

## Responsive Controls

Trigger the following response actions when hooks are detected:

- Terminate the app session immediately.
- Clear sensitive data from memory and/or disk.
- Alert the backend server to flag the compromised session.

Do not allow the app to continue running in a compromised state. Protect the response mechanism itself against hooking by implementing it in native code and obfuscating its control flow.
