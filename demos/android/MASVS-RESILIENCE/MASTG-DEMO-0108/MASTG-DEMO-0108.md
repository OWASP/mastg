---
platform: android
title: Bypassing Frida Detection in /proc/self/maps to Extract Sensitive Data
id: MASTG-DEMO-0108
code: [kotlin, cpp]
test: MASTG-TEST-0341
kind: fail
---

## Sample

This sample extends @MASTG-DEMO-0107. It encrypts and decrypts a sensitive API key using AES/GCM via the Android KeyStore. It also uses the hardcoded native secret `sk-OWASP-MAS-SuperSecretNativeKey-1234567890`, encrypts it using an Android KeyStore-backed `Cipher` called from JNI, replaces the IV and ciphertext in the app's private files directory, and decrypts it from disk each time **Start** is pressed. Native code uses the platform `Cipher` through JNI, not a bundled cryptographic implementation. Before each native operation, an inlined check looks for the Frida branch opcode observed on ARM64 (`emulator-5554`) or x86_64 (`127.0.0.1:39399`). A second check calculates a quick FNV-1a checksum of bionic libc's loaded `.text` section and its bytes on disk; a difference blocks the native operation. The existing runtime hook detection scans `/proc/self/maps` for Frida-related libraries and terminates via `Process.killProcess()`. The bypass hooks `BufferedReader.readLine()` to hide Frida entries so `detectHooking()` returns `false`.

See @MASTG-KNOW-0030 and @MASTG-KNOW-0032 for more context on bypassing runtime detection mechanisms.

!!! note
    This is a series of correlated tests.

    - @MASTG-DEMO-0106 is a failed test (failed defence/successful attack) against a data exfiltration attack.
    - @MASTG-DEMO-0107 is a successful test (successful defense/failed attack) against the attack of @MASTG-DEMO-0106.
    - This test is a failed test (failed defence/successful attack) against the defenses of @MASTG-DEMO-0107 by using a more "complex" attack.

{{ MastgTest.kt # native-key.cpp # CMakeLists.txt # NativeFlowTest.kt }}

Use `build.gradle.kts.android` to enable the native build when adding this sample to the test app.

## Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Run `run.sh` to spawn the app with the bypass script
3. Click the **Start** button
4. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI

{{ bypass.js # native-hooks.js # run.sh }}

To inspect the native hooks separately, load `native-hooks.js` together with `bypass.js`; the native checks then reject calls to either hooked JNI function.

## Observation

The captured output below is from the original Java-only variant: eight `frida-agent-64.so` memory segments are filtered from `/proc/self/maps` across two scans. The updated sample additionally calls `Cipher.doFinal()` from native code when it creates and recovers the key. Every press of **Start** encrypts the native secret with a fresh IV, replaces `native-secret.bin`, and recovers it from private storage.

On `emulator-5554` (ARM64, Frida 17.17.0), hooking each JNI export changed its first eight bytes to `50 00 00 58 00 02 1f d6` (`ldr x16, #8; br x16`). The check only matches `br xN` in the second instruction, ignoring the register number; it does not verify the preceding instruction. The following eight bytes are a process-specific target address and are not compared. With these hooks installed, calling `storeNativeSecret` throws `SecurityException: Native store hook detected`, and calling `recoverNativeSecret` returns `Error: Native recover hook detected`. Both operations succeed without native hooks. On the Fedora host's x86_64 device (`127.0.0.1:39399`), Frida patched each function with `e9 ?? ?? ?? ?? 66 0f 1f 44 00 00` (`jmp rel32` followed by a NOP). The four jump-displacement bytes varied between functions. The x86_64 check looks only for the leading `e9`. Hooked calls to both functions raised the corresponding `SecurityException`; without native hooks, encryption and recovery succeeded. These byte checks are illustrative: an unrelated `br xN` or `jmp rel32` at the checked offset can also trigger them, and they are not a general Frida defense.

The libc check uses the ELF `.text` section's file offset and loaded address. Unhooked encryption and recovery succeeded twice in succession on both devices, with fresh ciphertext on each run. With Frida attached on ARM64, it reported `libc .text modified in memory` before writing the native secret. This fast checksum is an illustrative integrity check, not a tamper-proof defense; failure to read or locate `.text` is reported as an error, not as a detected hook.

{{ output.txt }}

## Evaluation

The test fails because the `BufferedReader.readLine()` hook successfully concealed all Frida memory segments from `/proc/self/maps`, causing `detectHooking()` to return `false`. With detection bypassed, the app proceeds with its cryptographic operations, which the `Cipher.doFinal()` hooks can intercept, including the native JNI calls that encrypt and recover the hardcoded secret. The bundled output only demonstrates extraction of the original sensitive API key `sk-OWASP-MAS-SuperSecretKey-1234567890` in plaintext.
