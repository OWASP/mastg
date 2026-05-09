---
masvs_category: MASVS-RESILIENCE
platform: android
title: Runtime Integrity Verification
---

Techniques in this category verify the integrity of the app's memory to defend against runtime memory patches. Such patches include unwanted changes to binary code, bytecode, function pointer tables, and important data structures, as well as rogue code loaded into process memory.

Unlike @MASTG-KNOW-0030, which covers artifact-based detection (e.g., scanning for tool-specific strings or checking for open ports), this document focuses on detecting the _modifications_ that instrumentation tools make to the app's code and memory.

!!! note
  Runtime integrity verification is inherently a cat-and-mouse game. Detection methods and bypass techniques evolve continuously-determined attackers with sufficient time and resources can typically circumvent these protections, especially on rooted devices (see [Tan, 2016](https://blackhat.com/docs/us-16/materials/us-16-Tan-Bad-For-Enterprise-Attacking-BYOD-Enterprise-Mobile-Security-Solutions-wp.pdf)). These techniques should be part of a defense-in-depth strategy, not a standalone solution.

## Techniques

| Category | Techniques |
|---|---|
| **[Indirect Pointer-Flow Integrity](#indirect-pointer-flow-integrity)** | GOT hook detection, vtable hook detection, ART entry point verification |
| **[Code Integrity](#code-integrity)** | Memory checksums, inline hook detection |
| **[Code Injection](#code-injection)** | Detection of dynamic library injection, Xposed detection |

## Indirect Pointer-Flow Integrity

All techniques in this category share the same attack primitive: overwriting a pointer in an indirection structure to redirect control flow. Detection follows the same pattern: walk the table and verify each pointer resolves to an address within a legitimate code region as reported by `/proc/self/maps`.

Installing such hooks requires the target memory region to be writable. The kernel records current memory permissions in `/proc/self/maps`. Under normal conditions, code sections (marked `r-xp`) are never writable; a `rwxp` or `rw-p` permission on a library's code region is a strong indicator that permissions were changed at runtime to allow modification. Checking for this provides a broad pre-filter before performing the specific pointer verification below.

### GOT Hook Detection

The Global Offset Table (GOT) resolves library function calls. At runtime, the dynamic linker patches this table with the absolute addresses of global symbols. _GOT hooks_ overwrite the stored function addresses, redirecting legitimate function calls to adversary-controlled code (e.g., using libraries such as [xHook](https://github.com/iqiyi/xHook)). This type of hook can be detected by enumerating the process memory map and verifying that each GOT entry points to a legitimate library.

Unlike GNU `ld`, which resolves symbol addresses only when they are first used (lazy binding), the Android linker resolves all external functions and writes the corresponding GOT entries immediately after a library is loaded (immediate binding). As a result, you can expect all GOT entries to point to valid memory locations in the code sections of their respective libraries at runtime. GOT hook detection methods typically walk the GOT and verify this.

For GOT hook detection, the app can parse its own ELF structure, locate the GOT entries, and verify each point to an address within the expected library's memory range (as reported by `/proc/self/maps`).

### Vtable Hook Detection

C++ classes with virtual methods have a _vtable_ - an array of function pointers used for virtual dispatch. On Android, vtables are placed in the `.data.rel.ro` section of ELF binaries. The linker writes relocation-resolved addresses into this section and then marks it read-only before handing control to the app (similar to Full RELRO for the GOT). Overwriting a vtable entry to redirect virtual calls therefore requires an attacker to call `mprotect` to restore write permissions first - the same prerequisite as GOT and inline hooks.

Detection mirrors GOT hook detection: parse the ELF to locate `.data.rel.ro`, identify vtable entries, and verify each pointer falls within a legitimate code region as reported by `/proc/self/maps`. Any entry pointing outside the expected library's executable range indicates a hook.

### ART Entry Point Verification

Frida's Java API hooks methods by modifying the `ArtMethod` structure in ART's internal representation. Every Java method in memory is represented by an `ArtMethod` object containing fields such as:

- `entry_point_from_quick_compiled_code_`: Pointer to the compiled native code
- `entry_point_from_interpreter_`: Pointer to interpreter entry
- `access_flags_`: Method modifiers (public, native, etc.)

!!! note
    `ArtMethod` structure layout varies across Android versions, requiring version-specific offset handling.

When Frida hooks a method, it replaces the original entry point with a pointer to its trampoline. Detection approaches include:

- **Entry point verification**: Using JNI's `FromReflectedMethod` to obtain the `ArtMethod` pointer and verify the entry point falls within legitimate regions (OAT file, interpreter, or JIT code cache)
- **Access flags inspection**: Check if `kAccNative` (0x0100) was unexpectedly set
- **Trampoline detection**: Scan the entry point for known hook signatures
- **Stack inspection**: Look for Frida-related stack frames during execution

See ["The Jiu-Jitsu of Detecting Frida"](https://web.archive.org/web/20181227120751/http://www.vantagepoint.sg/blog/90-the-jiu-jitsu-of-detecting-frida) by Bernhard Mueller, ["Detecting and bypassing frida dynamic function call tracing: exploitation and mitigation"](https://link.springer.com/article/10.1007/s11416-022-00458-7), and the ["Anti-Frida Techniques"](https://github.com/apkunpacker/Anti-Frida) collection for additional detection approaches.

## Code Integrity

Unlike [Indirect Pointer-Flow Integrity](#indirect-pointer-flow-integrity), which detects pointer overwrites in indirection tables, the techniques in this category detect direct modifications to code or memory - either as arbitrary changes (checksums) or as specific known patterns (inline hook signatures).

### Memory Checksums

Memory checksums are integrity verification values computed over regions of an application's memory at runtime. At build time, the app calculates a hash or checksum (e.g., SHA-256) of critical memory regions such as code sections, function bodies, or constants. At runtime, the app periodically recalculates the checksum and compares it against the expected value. If the values differ, the memory has been modified.

This technique can detect code patches, inline hooks (trampolines inserted at function entry points), and data tampering. However, attackers can bypass it by hooking the checksum function itself or by patching the comparison logic.

### Inline Hook Detection

Inline hook detection scans memory for known byte patterns that indicate unwanted modifications. Unlike checksums, which detect _any_ change, this approach looks for _specific_ patterns associated with hooking frameworks or tampering techniques - identifying the _type_ of modification rather than just detecting that a change occurred. However, attackers can evade detection by using alternative hooking methods or by obfuscating the hook signatures.

_Inline hooks_ overwrite a few instructions at the beginning or end of the function code. At runtime, this so-called trampoline redirects execution to the injected code. You can detect inline hooks by inspecting the prologues and epilogues of library functions for suspect instructions, such as far jumps to locations outside the library. Common patterns to scan for include:

- **Inline hook trampolines**: A trampoline is a small piece of code that redirects execution from one location to another. Hooking frameworks insert trampolines at function entry points to intercept calls - when the original function is called, the trampoline jumps to the hook handler instead. On ARM64, a common trampoline pattern loads a 64-bit target address into a scratch register and branches to it: `LDR X16, .+8; BR X16` followed by the 8-byte absolute address. Scratch registers (X16 and X17 on ARM64) are temporary registers that the calling convention allows to be overwritten without saving, making them ideal for trampolines. Based on the [ARM A64 instruction set encoding](https://developer.arm.com/documentation/ddi0602/latest/), this sequence encodes to the bytes `50 00 00 58 00 02 1F D6` (hex encoding). Scanning for such patterns at function entry points can reveal hooks. The [O-MVLL anti-hooking pass](https://obfuscator.re/omvll/passes/anti-hook/) exploits the fact that Frida's Interceptor requires X16/X17 as scratch registers by injecting prologues that use these registers, preventing Frida from hooking. Note that a custom Frida modification that uses different registers or inserts opcodes into the sequence may break the detection script, thereby bypassing the defense. Also note that ARM32/Thumb code uses different trampoline patterns (e.g., `LDR PC, [PC, #-4]`) and should be checked separately if the app includes 32-bit libraries.
- **Modified function prologues**: Comparing the first few bytes of critical functions against their expected values can detect patches. For example, if a function's original prologue is known, any deviation indicates modification.

## Code Injection

Code injection allows an attacker to introduce and execute foreign code within the application's process at runtime. Once injected, this malicious code can manipulate the application's behavior in two primary ways:

- **Logic Manipulation**: The attacker can programmatically invoke existing internal methods at any time, often with unauthorized or malicious arguments, to bypass security checks or leak data.
- **Control-Flow Hijacking**: By combining injection with the hooking techniques mentioned above, an attacker can redirect the application's execution path. Instead of running the original, legitimate code, the program is forced to jump to the newly injected malicious instructions.

### Detection of Runtime Dynamic Library Injection

Attackers inject malicious code by forcing the application to load unauthorized shared libraries (.so files). These files act as external "plug-ins" that can modify the app's behavior, automate hooking, or steal data. The primary way to detect these is by auditing the process memory layout via the `/proc/self/maps` file, which acts as a real-time directory of every file and memory block the app is currently using.

**Detections:**

- **Path Validation**: Scan `/proc/self/maps` for libraries loaded from "world-writable" locations like `/data/local/tmp` or the app's internal cache. Legitimate app and system files should only reside in protected, read-only paths (e.g., `/system/lib`, `/apex`, or the official app installation folder).
- **Whitelisting**: Compare the list of loaded .so files against a known list of authorized dependencies. Any unrecognized library that isn't part of the original app package or the Android OS is flagged as a potential threat.
- **Signature Scanning**: Even if a library is renamed to look innocent, the detection logic can scan its memory for "fingerprints", such as specific code patterns, strings, or new exported symbols belonging to known hacking frameworks like Frida or Substrate.

### Xposed Detection

Xposed works by injecting the `XposedBridge` class into the app's class loader. Modern Xposed-compatible frameworks such as [LSPosed](https://github.com/LSPosed/LSPosed) continue to ship compatibility with the legacy `de.robv.android.xposed.*` API, so the classic class-based detection described below still applies. However, LSPosed is also developing its own [newer API (v101+)](https://github.com/libxposed/api/pull/51) that injects different classes; additional probes for these newer class names (see [examples](https://github.com/eltavine/Duck-Detector-Refactoring/blob/98e80ce900bfc278d32ed9be7f479e928ffebd00/app/src/main/java/com/eltavine/duckdetector/features/lsposed/data/probes/LSPosedClassProbe.kt#L136)) may be required for comprehensive detection as the LSPosed API matures.

```cpp
static jclass findXposedBridge(C_JNIEnv *env, jobject classLoader) {
    return findLoadedClass(env, classLoader, "de/robv/android/xposed/XposedBridge"_iobfs.c_str());
}
void doAntiXposed(C_JNIEnv *env, jobject object, intptr_t hash) {
    if (!add(hash)) {
        debug(env, "checked classLoader %s", object);
        return;
    }
#ifdef DEBUG
    LOGI("doAntiXposed, classLoader: %p, hash: %zx", object, hash);
#endif
    jclass classXposedBridge = findXposedBridge(env, object);
    if (classXposedBridge == nullptr) {
        return;
    }
    if (xposed_status == NO_XPOSED) {
        xposed_status = FOUND_XPOSED;
    }
    disableXposedBridge(env, classXposedBridge);
    if (clearHooks(env, object)) {
#ifdef DEBUG
        LOGI("hooks cleared");
#endif
        if (xposed_status < ANTIED_XPOSED) {
            xposed_status = ANTIED_XPOSED;
        }
    }
}
```
