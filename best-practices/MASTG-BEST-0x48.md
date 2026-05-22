---
title: Detecting Frida Instrumentation
alias: detecting-frida-instrumentation
id: MASTG-BEST-0x48
platform: android
knowledge: [MASTG-KNOW-0030]
---

Frida injects an agent (`frida-agent.so`) into the target process, starts native worker threads (`gum-js-loop`, `gmain`, `pool-frida`), and rewrites Java methods through ART trampolines. Every Java-side check is itself a Java method, so Frida can hook the check and silence it (see @MASTG-DEMO-0x49). Use multiple layers, and let the layer the attacker cannot reach decide who gets into the sensitive flow.

## Play Integrity

User-space code cannot reliably detect kernel-mode hooks. The only signal an attacker on a compromised device cannot forge is one signed inside the TEE / StrongBox and checked off-device.

- Require a **[Play Integrity API](https://developer.android.com/google/play/integrity) verdict** from your server before unlocking sensitive flows. A compromised kernel cannot forge the device-key-signed attestation — the signing happens inside the TEE / StrongBox.
- Store secrets in the [Android Keystore](https://developer.android.com/privacy-and-security/keystore) with `setIsStrongBoxBacked(true)`. The key cannot be pulled off a rooted device even if user-space is fully compromised.
- Have the server combine the attestation verdict with the score from the client-side checks below. Use client checks as inputs to the server's decision, not as a gate on their own.

## Native Probes

Java-side checks run in memory the attacker controls. Move the important detection into JNI, so a bypass has to hook `dlopen` / `dl_iterate_phdr` or patch the linker itself — not just a Java method.

- Use [`dl_iterate_phdr`](https://man7.org/linux/man-pages/man3/dl_iterate_phdr.3.html) from a small `.so` to walk every shared object loaded into the process. Flag anything outside `/data/app/<your-pkg>/`, `/system/`, `/apex/`, or `/vendor/`. This catches `frida-gadget` loaded via `LD_PRELOAD` and `frida-agent.so` loaded by `frida-server`, even when those files exist only as `/memfd:` mappings.
- Read `/proc/self/status` and check `TracerPid`. A non-zero value means something is `ptrace`-attached.
- Send the native verdict to your server with the attestation token. Don't let JNI return a plain boolean that Java code reads and acts on — that boolean is itself something Frida can hook.

## Memory and Process Inspection

These checks are cheap and catch lazy attacker setups. They are not enough on their own — any single one can be defeated — but together they raise the cost of a generic bypass.

- **Read `/proc/self/maps`** and flag any path that contains `frida-agent`, `libfrida`, `frida-gadget`, `gum-js-loop`, `linjector`, or `/gum`. Use a `BufferedReader` filter that loops until a clean line comes back, not a single `readLine()` call — @MASTG-DEMO-0x49 shows that a single-line filter leaks consecutive matches.
- **Enumerate `/proc/self/task/<tid>/comm`** for instrumentation thread names (`gum-js-loop`, `gmain`, `pool-frida`). This catches Frida even when the agent was loaded from `memfd` and the on-disk `.so` is unmapped.
- **Scan for `frida-server`-like processes**, but don't rely on the default `27042` port or the binary name `frida-server`. Both are trivially changed (`./frida-server -l 0.0.0.0:12345` + binary rename — see @MASTG-DEMO-0x49). A port / name check still belongs as a cheap trip for lazy attackers, but it must never be your only signal.

## Code Integrity

Frida's Java bridge clears `kAccNative` when it hooks a native method. Auditing that bit on methods that should always be native catches the in-process hook.

- Audit a small set of guaranteed-native methods with `java.lang.reflect.Method.getModifiers()` and require `Modifier.NATIVE` to still be set. Frida's Java bridge (and LSPlant) clear that bit when they hook a native method.
