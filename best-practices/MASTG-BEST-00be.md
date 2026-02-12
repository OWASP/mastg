---
title: Hardening Against Runtime Hooking
alias: hardening-against-runtime-hooking
id: MASTG-BEST-00be
platform: android
knowledge: [MASTG-KNOW-0027, MASTG-KNOW-0030, MASTG-KNOW-0032, MASTG-KNOW-00kw]
---

Defending against runtime hooking requires a layered approach that combines several types of security controls:

- **Preventive controls**: Implement root detection (@MASTG-KNOW-0027) and device/app attestation (https://github.com/OWASP/mastg/issues/3505) as the first line of defense, since most hooking frameworks (e.g., Frida server, Xposed) require rooted devices.
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
- **Integrity checksums**: Compute checksums of critical code sections at build/load time and verify them periodically at runtime to detect patches and inline hooks.
- **GOT/PLT verification**: Verify that Global Offset Table entries point to addresses within their expected libraries.
- **Function prologue inspection**: Compare the first bytes of security-critical functions against their expected values to detect trampoline patterns (e.g., `LDR X16, .+8; BR X16` on ARM64).
- **ART method verification**: Use JNI's `FromReflectedMethod` to confirm that Java method entry points fall within legitimate regions (OAT file, interpreter, or JIT code cache).
- **Network-based checks**: Probe for D-Bus responses on open ports to reveal frida-server even when renamed.

## Deterrent Controls

### Implement Detection in Native Code

Consider writing detection checks in native (C/C++) code rather than Java/Kotlin. Native code is significantly harder to hook and reverse engineer than Java bytecode, which can be easily intercepted via Frida's Java API or Xposed modules. Use JNI to bridge results back to the application layer.

### Obfuscate Detection Logic

Apply [code obfuscation](../Document/0x04c-Tampering-and-Reverse-Engineering.md#obfuscation) to all detection routines. Scatter checks throughout the app rather than centralizing them in a single function, and vary their timing (e.g., periodic, event-driven, or randomized) to prevent systematic bypassing.

## Responsive Controls

Trigger the following response actions when hooks are detected:

- Terminate the app session immediately.
- Clear sensitive data from memory before exiting.
- Alert the backend server to flag the compromised session.

Do not allow the app to continue running in a compromised state. Protect the response mechanism itself against hooking by implementing it in native code and obfuscating its control flow.
