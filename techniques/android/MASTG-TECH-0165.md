---
title: Identifying Compilers, Obfuscators, and Packers in Android Apps
platform: android
---

Use @MASTG-TOOL-0009 to scan the APK and identify the compilers, obfuscators, and packers applied to it.

```bash
apkid YourApp.apk
```

APKiD inspects the DEX files inside the APK for signatures left by known compilers, obfuscators, and packers. The output lists matches per DEX file and per native library, if present.

Example using @MASTG-APP-0015:

```bash
apkid ./r2pay-v1.0.apk

[+] APKiD 3.1.0 :: from RedNaga :: rednaga.io
[*] /input/r2pay-v1.0.apk!classes.dex
 |-> anti_vm : Build.TAGS check, possible ro.secure check
 |-> compiler : r8
 |-> obfuscator : unreadable field names, unreadable method names
[*] /input/r2pay-v1.0.apk!lib/arm64-v8a/libnative-lib.so
 |-> obfuscator : Obfuscator-LLVM version unknown (string encryption)
[*] /input/r2pay-v1.0.apk!lib/armeabi-v7a/libnative-lib.so
 |-> obfuscator : Obfuscator-LLVM version unknown (string encryption)
[*] /input/r2pay-v1.0.apk!lib/armeabi-v7a/libtool-checker.so
 |-> anti_root : RootBeer
[*] /input/r2pay-v1.0.apk!lib/x86_64/libnative-lib.so
 |-> obfuscator : Obfuscator-LLVM version unknown (string encryption)
[*] /input/r2pay-v1.0.apk!lib/x86_64/libtool-checker.so
 |-> anti_root : RootBeer
```

For each match, it indicates the type of protection (e.g., `compiler`, `obfuscator`, `packer`, `anti_vm`, `anti_root`), the name of the identified tool or technique, and any relevant details (e.g., version, specific features detected).

When no obfuscator or packer is identified, only compiler entries appear. The absence of `obfuscator` or `packer` entries indicates the code is not protected by a recognized tool.
