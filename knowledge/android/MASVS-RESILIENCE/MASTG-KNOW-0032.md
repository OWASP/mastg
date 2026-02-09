---
masvs_category: MASVS-RESILIENCE
platform: android
title: Runtime Integrity Verification
---

Controls in this category verify the integrity of the app's memory to defend against runtime memory patches. Such patches include unwanted changes to binary code, bytecode, function pointer tables, and important data structures, as well as rogue code loaded into process memory.

Unlike @MASTG-KNOW-0030, which covers artifact-based detection (e.g., scanning for tool-specific strings or checking for open ports), this document focuses on detecting the _modifications_ that instrumentation tools make to the app's code and memory.

!!! note
    Runtime integrity verification is inherently a cat-and-mouse game. Detection methods and bypass techniques evolve continuously—determined attackers with sufficient time and resources can typically circumvent these protections, especially on rooted devices (see [Tan, 2016](https://blackhat.com/docs/us-16/materials/us-16-Tan-Bad-For-Enterprise-Attacking-BYOD-Enterprise-Mobile-Security-Solutions-wp.pdf)). These techniques should be part of a defense-in-depth strategy, not a standalone solution.

## Techniques

The following runtime integrity verification techniques are covered in this document:

1. **Memory checksums**: Comparing memory contents or checksums against known good values.
2. **Signature-based detection**: Searching memory for signatures of unwanted modifications.
3. **Java Runtime tampering detection**: Detecting modifications to the Android Runtime (ART) made by hooking frameworks.
4. **Injected class detection**: Identifying classes injected by frameworks like Xposed.
5. **GOT hook detection**: Verifying Global Offset Table entries point to legitimate libraries.
6. **Inline hook detection**: Inspecting function prologues/epilogues for trampolines or suspicious jump instructions.

Techniques 1 and 2 are foundational approaches that underpin the more specific detection methods (3-6).

### Memory Checksums

Memory checksums are integrity verification values computed over regions of an application's memory at runtime. At build or load time, the app calculates a hash or checksum (e.g. SHA-256) of critical memory regions such as code sections, function bodies, or constants. At runtime, the app periodically recalculates the checksum and compares it against the expected value. If the values differ, the memory has been modified.

This technique can detect code patches, inline hooks (trampolines inserted at function entry points), and data tampering. However, attackers can bypass it by hooking the checksum function itself or by patching the comparison logic.

### Signature-based Detection

Signature-based detection actively involves scanning memory for known byte patterns that indicate unwanted modifications. Unlike checksums which detect _any_ change, signature-based detection looks for _specific_ patterns associated with hooking frameworks or tampering techniques.

On Android, common signatures to detect include:

- **Inline hook trampolines**: A trampoline is a small piece of code that redirects execution from one location to another. Hooking frameworks insert trampolines at function entry points to intercept calls—when the original function is called, the trampoline jumps to the hook handler instead. On ARM64, a common trampoline pattern loads a 64-bit target address into a scratch register and branches to it: `LDR X16, .+8; BR X16` followed by the 8-byte absolute address. Scratch registers (X16 and X17 on ARM64) are temporary registers that the calling convention allows to be overwritten without saving, making them ideal for trampolines. Based on the [ARM A64 instruction set encoding](https://developer.arm.com/documentation/ddi0602/latest/), this sequence encodes to bytes `50 00 00 58 00 02 1F D6`. Scanning for such patterns at function entry points can reveal hooks. The [O-MVLL anti-hooking pass](https://obfuscator.re/omvll/passes/anti-hook/) exploits the fact that Frida's Interceptor requires X16/X17 as scratch registers by injecting prologues that use these registers, preventing Frida from hooking. Note that ARM32/Thumb code uses different trampoline patterns (e.g., `LDR PC, [PC, #-4]`) and should be checked separately if the app includes 32-bit libraries.
- **Modified function prologues**: Comparing the first few bytes of critical functions against their expected values can detect patches. For example, if a function's original prologue is known, any deviation indicates modification.
- **Suspicious branch targets**: Branch instructions pointing outside the library's code section suggest redirection to injected code.

This technique complements checksums by identifying the specific type of modification rather than just detecting that a change occurred. However, attackers can evade detection by using alternative hooking methods or by obfuscating the hook signatures.

### Java Runtime Tampering Detection

Hooking frameworks such as @MASTG-TOOL-0027 and Frida's Java API modify the Android Runtime (ART) to intercept method calls. These modifications leave detectable traces.

#### Xposed Detection

Xposed works by injecting the `XposedBridge` class into the app's classloader. The following code snippet from the [XPosedDetector](https://github.com/vvb2060/XposedDetector/) project demonstrates how to detect Xposed by looking for its injected classes.

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

#### Frida Detection

Frida's Java API hooks methods differently. It modifies the `ArtMethod` structure in ART's internal representation. Every Java method in memory is represented by an `ArtMethod` object containing fields such as:

- `entry_point_from_quick_compiled_code_`: Pointer to the compiled native code
- `entry_point_from_interpreter_`: Pointer to interpreter entry
- `access_flags_`: Method modifiers (public, native, etc.)

When Frida hooks a method, it replaces the original entry point with a pointer to its trampoline. Detection approaches include:

- **Entry point verification**: Using JNI's `FromReflectedMethod` to obtain the `ArtMethod` pointer and verify the entry point falls within legitimate regions (OAT file, interpreter, or JIT code cache)
- **Access flags inspection**: Check if `kAccNative` (0x0100) was unexpectedly set
- **Trampoline detection**: Scan the entry point for known hook signatures
- **Stack inspection**: Look for Frida-related stack frames during execution

See ["The Jiu-Jitsu of Detecting Frida"](https://web.archive.org/web/20181227120751/http://www.vantagepoint.sg/blog/90-the-jiu-jitsu-of-detecting-frida) by Bernhard Mueller, [Soriano-Salvador & Guardiola-Múzquiz (2023)](https://link.springer.com/article/10.1007/s11416-022-00458-7), and the [Anti-Frida Techniques](https://github.com/apkunpacker/Anti-Frida) collection for additional detection approaches.

!!! note
    `ArtMethod` structure layout varies across Android versions, requiring version-specific offset handling.

### Detecting Native Hooks

Native function hooks can be installed in ELF binaries by overwriting function pointers in memory (e.g., Global Offset Table or PLT hooking) or by patching parts of the function code itself (inline hooking). Checking the integrity of the corresponding memory regions is one way to detect this type of hook.

#### GOT Hook Detection

The Global Offset Table (GOT) resolves library function calls. At runtime, the dynamic linker patches this table with the absolute addresses of global symbols. _GOT hooks_ overwrite the stored function addresses, redirecting legitimate function calls to adversary-controlled code (e.g., using libraries such as [xHook](https://github.com/iqiyi/xHook)). This type of hook can be detected by enumerating the process memory map and verifying that each GOT entry points to a legitimate library.

Unlike GNU `ld`, which resolves symbol addresses only when they are first used (lazy binding), the Android linker resolves all external functions and writes the corresponding GOT entries immediately after a library is loaded (immediate binding). As a result, you can expect all GOT entries to point to valid memory locations in the code sections of their respective libraries at runtime. GOT hook detection methods typically walk the GOT and verify this.

For GOT hook detection, the app can parse its own ELF structure, locate the GOT entries, and verify each points to an address within the expected library's memory range (as reported by `/proc/self/maps`).

#### Inline Hook Detection

_Inline hooks_ overwrite a few instructions at the beginning or end of the function code. At runtime, this so-called trampoline redirects execution to the injected code. You can detect inline hooks by inspecting the prologues and epilogues of library functions for suspect instructions, such as far jumps to locations outside the library.
