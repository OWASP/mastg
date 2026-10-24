---
title: Emulation-based Analysis
platform: android
---

The Android emulator is based on QEMU, a generic and open-source machine emulator. QEMU emulates a guest CPU by translating the guest instructions on-the-fly into instructions the host processor can understand. Each basic block of guest instructions is disassembled and translated into an intermediate representation called Tiny Code Generator (TCG). The TCG block is compiled into a block of host instructions, stored in a code cache, and executed. After execution of the basic block, QEMU repeats the process for the next block of guest instructions, or loads the already-translated block from the cache. The whole process is called dynamic binary translation.

Because the Android emulator is based on QEMU, it exposes many QEMU features, including monitoring, debugging, and tracing facilities. QEMU-specific parameters can be passed to the emulator with the `-qemu` command-line flag. You can use QEMU's built-in tracing facilities to log translated instructions and CPU state. Starting QEMU with the `-d` command-line flag will cause it to dump the blocks of guest code, TCG operations, or host instructions being executed. With `-d in_asm`, QEMU logs basic blocks of guest code as they enter QEMU's translation function. The following command logs all translated blocks to a file:

```bash
emulator -show-kernel -avd Nexus_4_API_19 -snapshot default-boot -no-snapshot-save -qemu -d in_asm,cpu 2>/tmp/qemu.log
```

Unfortunately, generating a complete guest instruction trace with `-d in_asm` is not possible because code blocks are written to the log only when they are translated, not when they are reused from the cache. For example, if a block is repeatedly executed in a loop, only the first iteration will be printed to the log. There is no supported command-line option to disable TB caching in QEMU. Nevertheless, the functionality is sufficient for basic tasks, such as reconstructing the disassembly of a natively executed cryptographic algorithm.

Note that running an app in an emulator can expose runtime indicators that the app may use to detect the emulated environment. Examples include `Build` values, telephony identifiers, package names, and graphics renderer strings. See @MASTG-KNOW-0031 for common emulator indicators that can be detected by applications.
