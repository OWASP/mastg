---
masvs_category: MASVS-RESILIENCE
platform: android
title: Runtime Integrity Verification
---

Controls in this category verify the integrity of the app's memory to defend against runtime memory patches. Such patches include unwanted changes to binary code, bytecode, function pointer tables, and important data structures, as well as rogue code loaded into process memory. Integrity can be verified by:

1. Comparing memory contents or a checksum of those contents against good values,
2. Searching memory for signatures of unwanted modifications.

There's some overlap with @MASTG-KNOW-0030, which covers artifact-based detection, such as scanning process memory for Frida-related strings. Below are examples of integrity monitoring techniques that detect modifications rather than artifacts.

## Detecting Tampering with the Java Runtime

Hooking frameworks such as @MASTG-TOOL-0027 and Frida's Java API modify the Android Runtime (ART) to intercept method calls. These modifications leave detectable traces. The following code snippet from the [XPosedDetector](https://github.com/vvb2060/XposedDetector/) project demonstrates how to detect Xposed by looking for its injected classes.

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

## Detecting Native Hooks

Native function hooks can be installed in ELF binaries by overwriting function pointers in memory (e.g., Global Offset Table or PLT hooking) or by patching parts of the function code itself (inline hooking). Checking the integrity of the corresponding memory regions is one way to detect this type of hook.

The Global Offset Table (GOT) resolves library function calls. At runtime, the dynamic linker patches this table with the absolute addresses of global symbols. _GOT hooks_ overwrite the stored function addresses, redirecting legitimate function calls to adversary-controlled code. This type of hook can be detected by enumerating the process memory map and verifying that each GOT entry points to a legitimate library.

Unlike GNU `ld`, which resolves symbol addresses only when they are first used (lazy binding), the Android linker resolves all external functions and writes the corresponding GOT entries immediately after a library is loaded (immediate binding). As a result, you can expect all GOT entries to point to valid memory locations in the code sections of their respective libraries at runtime. GOT hook detection methods typically walk the GOT and verify this.

_Inline hooks_ overwrite a few instructions at the beginning or end of the function code. At runtime, this so-called trampoline redirects execution to the injected code. You can detect inline hooks by inspecting the prologues and epilogues of library functions for suspect instructions, such as far jumps to locations outside the library.
