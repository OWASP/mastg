---
masvs_category: MASVS-RESILIENCE
platform: android
title: Runtime Integrity Verification
---

defensive controls in this category verify the integrity of the app's memory to defend against runtime memory patches. Such changes include unwanted modifications to native code, bytecode execution targets, function pointer tables, important runtime data structures, and unauthorized executable code loaded into process memory.

Unlike @MASTG-KNOW-0030, which covers artifact-based detection (e.g., scanning for tool-specific strings or checking for open ports), this document focuses on detecting the _modifications_ that instrumentation tools make to the app's code and memory.

!!! note
  Runtime integrity verification is inherently a cat-and-mouse game. Detection methods and bypass defensive controls evolve continuously. Determined attackers with sufficient time and resources can typically circumvent these protections, especially on rooted devices (see [Tan, 2016](https://blackhat.com/docs/us-16/materials/us-16-Tan-Bad-For-Enterprise-Attacking-BYOD-Enterprise-Mobile-Security-Solutions-wp.pdf)). These defensive controls should be part of a defense-in-depth strategy, not a standalone solution.

The following sections present these defensive controls grouped into four categories based on the type of integrity violation they detect. Each category includes specific methods and a discussion of their effectiveness and potential bypasses. They help answer the following questions:

| Question | Category | Defensive Controls |
|---|---|---|
| Did an indirect call target change? | **[Control Flow Integrity Checks](#control-flow-integrity-checks)** | PLT/GOT hook detection, vtable hook detection, ART entry point verification |
| Did executable code or protected data change? | **[Code Integrity Verification](#code-integrity-verification)** | Memory checksums, inline hook detection |
| Was new executable code loaded into the process? | **[Runtime Code Injection Detection](#runtime-code-injection-detection)** | Dynamic library injection detection |
| Was the app runtime modified by framework injection? | **[Framework Runtime Modification Detection](#framework-runtime-modification-detection)** | Xposed detection |

## Control Flow Integrity Checks

Control flow integrity is a well-known security concept that aims to prevent unintended redirection of program control flow. This category focuses on runtime checks for indirect control flow targets, such as function pointers, vtable entries, and ART method entry points. See [Clang Control Flow Integrity](https://clang.llvm.org/docs/ControlFlowIntegrity.html) for a compiler-level implementation of related concepts.

All defensive controls in this category share the same attack primitive: overwriting a pointer in an indirection structure to redirect control flow. Detection follows the same pattern: walk the table and verify each relevant function pointer resolves to an expected executable mapping, using `/proc/self/maps` as one source of mapping information.

Installing such hooks requires the target memory region to be writable. The kernel records current memory permissions in [`/proc/self/maps`](https://man7.org/linux/man-pages/man5/proc_pid_maps.5.html). Under normal conditions, code sections (marked `r-xp`) are never writable, so an executable mapping that is also writable, such as `rwxp`, is suspicious. A writable mapping is also suspicious when it corresponds to a region expected to be read-only after relocation, such as RELRO-protected data. For RELRO-protected regions, the [expected state after relocation is read-only](https://android.googlesource.com/platform/bionic/%2B/master/linker/linker.cpp). Ordinary `rw-p` library data mappings are expected and should not be flagged by themselves. Checking for suspicious permissions provides a broad pre-filter before performing the specific pointer verification below.

### PLT/GOT Hook Detection

The Procedure Linkage Table / Global Offset Table (PLT/GOT) stores relocation-resolved function addresses used by dynamically linked calls. At runtime, the dynamic linker patches these entries with the absolute addresses of imported functions. _PLT/GOT hooks_ overwrite the stored function addresses, redirecting legitimate function calls to adversary-controlled code (e.g., using libraries such as [xHook](https://github.com/iqiyi/xHook)). This type of hook can be detected by enumerating the process memory map and verifying that each relevant PLT/GOT entry points to the expected symbol implementation or to an allowed executable mapping.

Android's bionic linker performs relocation while loading shared libraries, and [historically did not use lazy binding for PLT entries](https://android.googlesource.com/platform/bionic/%2B/android-4.2_r1/linker/linker.cpp). As a result, you can generally expect imported function entries to point to valid executable locations at runtime, rather than unresolved lazy-binding stubs. PLT/GOT hook detection methods typically walk these entries and verify this.

For PLT/GOT hook detection, the app can parse its own ELF structure, locate function relocation entries, and verify each one points to an address within the expected library's memory range or another trusted executable mapping, as reported by `/proc/self/maps`.

### Vtable Hook Detection

C++ classes with virtual methods have a _vtable_, an array of function pointers used for virtual dispatch. In Android native libraries, C++ vtables are commonly placed in relocation read-only sections such as `.data.rel.ro`, depending on compiler, linker, and binary layout. The linker writes relocation-resolved addresses into these sections and then marks them read-only before handing control to the app. This is similar to Full RELRO for the GOT, where the [linker protects relocation-resolved data after relocations are applied](https://android.googlesource.com/platform/bionic/%2B/master/linker/linker.cpp). Overwriting a vtable entry to redirect virtual calls therefore requires an attacker to restore write permissions first, for example by calling `mprotect`.

Detection mirrors PLT/GOT hook detection: parse the ELF to locate relocation read-only regions such as `.data.rel.ro`, identify vtable entries, and verify each pointer falls within an expected executable mapping as reported by `/proc/self/maps`. Any entry pointing outside the expected set of executable mappings should be treated as suspicious and investigated.

### ART Entry Point Verification

Some Java method hooking defensive controls modify the `ArtMethod` structure in ART's internal representation. Every Java method in memory is represented by an `ArtMethod` object containing fields such as [`access_flags_`](https://android.googlesource.com/platform/art/%2B/android-6.0.0_r26/runtime/art_method.h) and pointer-sized fields such as [`entry_point_from_quick_compiled_code_`](https://android.googlesource.com/platform/art/%2B/master/runtime/art_method.h). Relevant fields include:

- `entry_point_from_quick_compiled_code_`: Pointer to the compiled native code
- `entry_point_from_interpreter_`: Pointer to interpreter entry
- `access_flags_`: Method modifiers (public, native, etc.)

!!! note
    There is no public API to obtain a reference to the `ArtMethod` structure that backs a Java method. JNI's `FromReflectedMethod` returns a `jmethodID` which could be reinterpreted as an `ArtMethod*`, but this depends on ART configuration and it is not a guarantee. On runtimes using opaque/index JNI IDs, it is not a raw pointer. The `ArtMethod` layout itself also varies across Android versions, requiring version-specific offset handling that is brittle and error-prone.

When a framework hooks a method, it may replace the original entry points with a pointer to hook or bridge code. Detection approaches include:

- **Entry point verification**: Inspect the relevant `ArtMethod` entrypoint fields for the target Android version and verify that they fall within legitimate regions (OAT file, interpreter bridge, JNI/native stubs, or JIT code cache)
- **Access flags inspection**: Check if `kAccNative` (0x0100) was unexpectedly set
- **Trampoline detection**: Scan the entry points for known hook signatures

Stack inspection can also reveal instrumentation-related frames during execution, but this is closer to artifact-based tool detection and is therefore covered in @MASTG-KNOW-0030.

See ["The Jiu-Jitsu of Detecting Frida"](https://web.archive.org/web/20181227120751/http://www.vantagepoint.sg/blog/90-the-jiu-jitsu-of-detecting-frida) by Bernhard Mueller, ["Detecting and bypassing frida dynamic function call tracing: exploitation and mitigation"](https://link.springer.com/article/10.1007/s11416-022-00458-7), and the ["Anti-Frida Techniques"](https://github.com/apkunpacker/Anti-Frida) collection for additional detection approaches.

## Code Integrity Verification

Code integrity verification is a common protection against code tampering. OWASP describes code tampering defenses as runtime checks that detect whether code has been added or changed from what the app expects based on its original integrity state (see [OWASP Mobile Top 10 M8: Code Tampering](https://owasp.org/www-project-mobile-top-10/2016-risks/m8-code-tampering/)).

Unlike [Control Flow Integrity Checks](#control-flow-integrity-checks), which detect pointer overwrites in indirection tables, the defensive controls in this category detect direct modifications to code or memory, either as arbitrary changes (checksums) or as specific known patterns (inline hook signatures).

### Memory Checksums

Memory checksums are integrity verification values computed over regions of an application's memory at runtime. At build time, the app records expected hashes for critical file-backed sections or function byte ranges. At runtime, the app maps those expectations to the loaded memory ranges, recalculates the checksum, and compares the result. If the values differ, the memory has been modified.

This technique can detect code patches, inline hooks (trampolines inserted at function entry points), and data tampering. However, attackers can bypass it by hooking the checksum function itself or by patching the comparison logic. Regions containing relocations, pointers, or runtime-patched instructions must be normalized or excluded, otherwise legitimate loader changes may cause false positives.

### Inline Hook Detection

Inline hook detection scans memory for known byte patterns that indicate unwanted modifications. Unlike checksums, which detect _any_ change, this approach looks for _specific_ patterns associated with hooking frameworks or tampering techniques, identifying the _type_ of modification rather than just detecting that a change occurred. However, attackers can evade detection by using alternative hooking methods or by obfuscating the hook signatures.

_Inline hooks_ usually overwrite the first few instructions of a function, although other patch locations are possible. At runtime, this so-called trampoline redirects execution to the injected code. You can detect inline hooks by inspecting the prologues and epilogues of library functions for suspect instructions, such as unconditional branches, indirect branches, or literal loads followed by branches to locations outside the library. Common patterns to scan for include:

- **Inline hook trampolines**: A trampoline is a small piece of code that redirects execution from one location to another. Hooking frameworks insert trampolines at function entry points to intercept calls. When the original function is called, the trampoline jumps to the hook handler instead. On ARM64, a common trampoline pattern loads a 64-bit target address into a scratch register and branches to it: `LDR X16, .+8; BR X16` followed by the 8-byte absolute address. Scratch registers ([X16 and X17 on ARM64](https://github.com/ARM-software/abi-aa/blob/main/aapcs64/aapcs64.rst)) are temporary registers that the calling convention allows to be overwritten without saving, making them useful for trampolines. Based on the [ARM A64 instruction set encoding](https://developer.arm.com/documentation/ddi0602/latest/), one common encoding of this sequence is the bytes `50 00 00 58 00 02 1F D6` (hex encoding). Scanning for such patterns at function entry points can reveal hooks. The [O-MVLL anti-hooking pass](https://obfuscator.re/omvll/passes/anti-hook/) exploits the fact that Frida's Interceptor requires X16/X17 as scratch registers by injecting prologues that use these registers, preventing Frida from hooking. Note that a custom Frida modification that uses different registers or inserts opcodes into the sequence may break the detection script, thereby bypassing the defense. Also note that ARM32/Thumb code uses different trampoline patterns (e.g., `LDR PC, [PC, #-4]`) and should be checked separately if the app includes 32-bit libraries.
- **Modified function prologues**: Comparing the first few bytes of critical functions against their expected values can detect patches. For example, if a function's original prologue is known, any deviation indicates modification.

## Runtime Code Injection Detection

Code injection allows foreign code to execute inside the application process at runtime. Once loaded, this code can interact with internal application state, call existing methods, install hooks, or redirect execution through modified function pointers, patched instructions, or runtime metadata.

### Dynamic Library Injection Detection

Attackers inject code by forcing the application to load unauthorized shared libraries (`.so` files). These files act as external plug-ins that can modify the app's behavior, automate hooking, or steal data. The primary way to detect these is by auditing the process memory layout via the [`/proc/self/maps`](https://man7.org/linux/man-pages/man5/proc_pid_maps.5.html) file, which lists the mapped memory regions in the current process.

**Detection approaches:**

- **Path Validation**: Scan `/proc/self/maps` for libraries loaded from writable or unexpected locations, such as `/data/local/tmp`, temporary extraction directories, or the app's writable data and cache directories. Legitimate native code should normally come from the app package, installed split APKs, trusted dynamic feature modules, or system and APEX locations.
- **Whitelisting**: Compare the list of loaded `.so` files against a known list of authorized dependencies. Any unrecognized library that isn't part of the original app package, an expected dynamic feature module, or the Android OS is flagged as a potential threat. Maintain the allowlist per ABI, build variant, and Android version, because system libraries and APEX paths vary.
- **Executable Mapping Validation**: Check for unexpected executable mappings, including anonymous executable mappings, deleted file-backed executable mappings, or executable mappings from writable paths. These checks focus on whether unauthorized executable code is present, not on tool-specific names or strings.

Artifact-based checks, such as searching for Frida strings, Frida thread names, named pipes, or default ports, are covered in @MASTG-KNOW-0030.

## Framework Runtime Modification Detection

Framework runtime modification detection checks whether the app's live runtime environment has been modified by a hooking framework. This category focuses on runtime state changes inside the app process, such as classes injected into the app's class loader. It does not cover installed packages, framework files, daemon processes, ports, or other tool presence artifacts, which are covered in @MASTG-KNOW-0030.

### Xposed Detection

Xposed works by injecting the `XposedBridge` class into the app's class loader. Modern Xposed-compatible frameworks such as [LSPosed](https://github.com/LSPosed/LSPosed) continue to ship compatibility with the legacy `de.robv.android.xposed.*` API, so the classic class-based detection described below still applies. However, modern LSPosed APIs use different entry points and metadata, including [`META-INF/xposed/java_init.list` and `META-INF/xposed/native_init.list`](https://github.com/LSPosed/LSPosed/wiki/Develop-Xposed-Modules-Using-Modern-Xposed-API). Additional probes for newer class names (see [examples](https://github.com/eltavine/Duck-Detector-Refactoring/blob/98e80ce900bfc278d32ed9be7f479e928ffebd00/app/src/main/java/com/eltavine/duckdetector/features/lsposed/data/probes/LSPosedClassProbe.kt#L136)) may be required for comprehensive detection.

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
