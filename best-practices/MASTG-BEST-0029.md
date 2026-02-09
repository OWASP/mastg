---
title: Implementing Runtime Hook Detection
alias: runtime-hook-detection
id: MASTG-BEST-0029
platform: android
knowledge: [MASTG-KNOW-0030, MASTG-KNOW-0032, MASTG-KNOW-00kw]
---

Defending against runtime hooking requires a layered approach that combines several types of security controls:

- **Preventive controls** reduce the attack surface by ensuring the app runs in a trusted environment. Root detection (@MASTG-KNOW-0027) and device/app attestation (@MASTG-KNOW-0035) are the first layer of defense, since most hooking frameworks (e.g., Frida server, Xposed) traditionally rely on rooted devices.
- **Detective controls** identify hooking attempts at runtime. These include artifact-based detection (@MASTG-KNOW-0030), which scans for tool signatures, and runtime integrity verification (@MASTG-KNOW-0032), which detects modifications to the app's code and memory.
- **Deterrent controls** increase the cost and effort of attacking. Code obfuscation, scattering checks throughout the app, and varying their timing make it harder for attackers to locate and systematically bypass protections.
- **Responsive controls** define how the app reacts once a threat is detected, such as terminating the session, clearing sensitive data from memory, or alerting a backend server.

However, hooking can also occur on non-rooted devices, for example by repackaging the app with an embedded frida-gadget or by using other injection techniques that do not require root. The detective, deterrent, and responsive controls described in this document address these scenarios by operating directly at runtime, regardless of the device's root status. Since no single control type is sufficient on its own, combining all of them is recommended.

## Detective Controls

### Combine Artifact-Based and Integrity-Based Detection

Use both artifact-based detection (@MASTG-KNOW-0030) and runtime integrity verification (@MASTG-KNOW-0032) together. Artifact-based detection identifies known tool signatures (e.g., Frida server processes, libraries, open ports), while integrity-based detection catches the _modifications_ these tools make to the app's code and memory (e.g., GOT hooks, inline trampolines, ART method entry point changes). Relying on only one approach leaves gaps that attackers can exploit.

### Apply Multiple Detection Techniques

Combine several techniques to increase the cost of bypassing protections:

- **Memory scanning**: Scan `/proc/self/maps` and process memory for known artifacts (e.g., "LIBFRIDA", frida-agent libraries, Xposed bridge classes).
- **Integrity checksums**: Compute and verify checksums of critical code sections at runtime to detect patches and inline hooks.
- **GOT/PLT verification**: Validate that Global Offset Table entries point to addresses within their expected libraries.
- **Function prologue inspection**: Check the first bytes of security-critical functions for trampoline patterns (e.g., `LDR X16, .+8; BR X16` on ARM64).
- **ART method verification**: Use JNI's `FromReflectedMethod` to verify that Java method entry points fall within legitimate regions (OAT file, interpreter, or JIT code cache).
- **Network-based checks**: Probe for D-Bus responses on open ports, which can reveal frida-server even when renamed.

## Deterrent Controls

### Implement Detection in Native Code

Perform detection checks in native (C/C++) code rather than Java/Kotlin whenever possible. Native code is harder to hook and reverse engineer compared to Java bytecode, which can be easily intercepted via Frida's Java API or Xposed modules. Use JNI to bridge results back to the application layer.

### Obfuscate Detection Logic

Apply [code obfuscation](../Document/0x04c-Tampering-and-Reverse-Engineering.md#obfuscation) to detection routines to make them harder to locate and bypass. Scatter checks throughout the app rather than centralizing them in a single function, and vary the timing of checks (e.g., periodic, event-driven, or randomized) to make systematic bypassing more difficult.

## Responsive Controls

Define clear response actions when hooks are detected, such as:

- Terminating the app session immediately.
- Clearing sensitive data from memory before exiting.
- Alerting a backend server to flag the compromised session.

Avoid silent failures that leave the app running in a compromised state. The response mechanism itself should be protected against hooking (e.g., by implementing it in native code and obfuscating the control flow).

## Limitations

Runtime hook detection is inherently a cat-and-mouse game (@MASTG-KNOW-00kw). Determined attackers with root access and sufficient time can typically bypass these protections. Therefore:

- Treat hook detection as one layer in a defense-in-depth strategy, not a standalone solution.
- Protect critical security logic server-side where possible.
- Balance detection aggressiveness with the risk of false positives (e.g., legitimate accessibility tools or custom ROMs).
- Plan for ongoing updates as new bypass techniques emerge.
