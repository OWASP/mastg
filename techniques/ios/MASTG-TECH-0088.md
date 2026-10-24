---
title: Emulation-based Analysis
platform: ios
---

## iOS Simulator

Apple provides a simulator app within Xcode that provides an _iOS device-looking_ user interface for iPhone, iPad, or Apple Watch. It allows you to rapidly prototype and test debug builds of your applications during development, but **it is not an emulator**. The difference between a simulator and an emulator is discussed in @MASTG-KNOW-0112.

While developing and debugging an application, the Xcode toolchain generates x86 code, which can be executed in the iOS simulator. However, for a release build, only ARM code is generated (incompatible with the iOS simulator). That's why applications downloaded from the Apple App Store cannot be used for any application analysis on the iOS simulator.

## Corellium

Corellium is a commercial tool that offers virtual iOS devices running real iOS firmware, making it the only publicly available iOS emulator. Since it is a proprietary product, little information is available about its implementation. Corellium has no community licenses available. Therefore, we won't go into much detail regarding its use.

Corellium allows you to launch multiple instances of a device (jailbroken or not), which are accessible as local devices (with a simple VPN configuration). It can take and restore device-state snapshots and also provides a convenient web-based shell for the device. Finally and most importantly, due to its "emulator" nature, you can execute applications downloaded from the Apple App Store, enabling any application analysis as you know it from real iOS (jailbroken) devices.

Note that in order to install an IPA on Corellium devices it has to be unencrypted and signed with a valid Apple developer certificate. See more information [in Corellium's documentation](https://support.corellium.com/features/apps/testing-ios-apps).

## Unicorn

[Unicorn](http://www.unicorn-engine.org/ "Unicorn") is a lightweight, multi-architecture CPU emulator framework based on [QEMU](https://www.qemu.org/ "QEMU") and [goes beyond it](https://www.unicorn-engine.org/docs/beyond_qemu.html "Beyond QEMU") by adding useful features especially made for CPU emulation. Unicorn provides the basic infrastructure needed to execute processor instructions. In this section, we will use [Unicorn's Python bindings](https://github.com/unicorn-engine/unicorn/tree/master/bindings/python "Unicorn Python bindings") to solve the @MASTG-APP-0025 challenge.

To use Unicorn's _full power_, we would need to implement all the necessary infrastructure, which generally is readily available from the operating system, e.g. binary loader, linker, and other dependencies, or use another higher-level framework such as [Qiling](https://qiling.io "Qiling") which leverages Unicorn to emulate CPU instructions, but understands the OS context. However, this is superfluous for this very localized challenge, where only executing a small part of the binary will suffice.

While performing manual analysis in @MASTG-TECH-0077, we determined that the function at address 0x1000080d4 is responsible for dynamically generating the secret string. As we'll see, all the necessary code is largely self-contained in the binary, making this a perfect scenario for using a CPU emulator like Unicorn.

<img src="Images/Chapters/0x06c/manual_reversing_ghidra_native_disassembly.png" width="100%" />

If we analyze the function and its subsequent calls, we will observe no hard dependency on any external library, nor does it perform any system calls. The only access external to the functions occurs, for instance, at address 0x1000080f4, where a value is being stored to address 0x10000dbf0, which maps to the `__data` section.

Therefore, to correctly emulate this section of the code, we also need to load the `__text` section (which contains the instructions), in addition to the `__data` section.

To solve the challenge using Unicorn, we will perform the following steps:

- Get the ARM64 version of the binary by running `lipo -thin arm64 <app_binary> -output uncrackable.arm64` (ARMv7 can be used as well).
- Extract the `__text` and `__data` section from the binary.
- Create and map the memory to be used as stack memory.
- Create memory and load the `__text` and `__data` section.
- Execute the binary by providing the start and end addresses.
- Finally, dump the return value from the function, which in this case is our secret string.

To extract the content of `__text` and `__data` sections from the Mach-O binary, we will use [LIEF](https://lief.quarkslab.com/ "Lief"), which provides a convenient abstraction to manipulate multiple executable file formats. Before loading these sections to memory, we need to determine their base addresses, e.g., by using Ghidra, Radare2, or IDA Pro.

<img src="Images/Chapters/0x06c/uncrackable_sections.png" width="500px"/>

From the above table, we will use the base address 0x10000432c for `__text` and 0x10000d3e8 for `__data` section to load them into the memory.

> While allocating memory for Unicorn, the memory addresses should be 4k page aligned, and also the allocated size should be a multiple of 1024.

The following script emulates the function at 0x1000080d4 and dumps the secret string:

```python

import lief
from unicorn import *
from unicorn.arm64_const import *

# --- Extract __text and __data section content from the binary ---
binary = lief.parse("uncrackable.arm64")
text_section = binary.get_section("__text")
text_content = text_section.content

data_section = binary.get_section("__data")
data_content = data_section.content

# --- Setup Unicorn for ARM64 execution ---
arch = "arm64le"
emu = Uc(UC_ARCH_ARM64, UC_MODE_ARM)

# --- Create Stack memory ---
addr = 0x40000000
size = 1024*1024
emu.mem_map(addr, size)
emu.reg_write(UC_ARM64_REG_SP, addr + size - 1)

# --- Load text section --
base_addr = 0x100000000
tmp_len = 1024*1024
text_section_load_addr = 0x10000432c
emu.mem_map(base_addr, tmp_len)
emu.mem_write(text_section_load_addr, bytes(text_content))

# --- Load data section ---
data_section_load_addr = 0x10000d3e8
emu.mem_write(data_section_load_addr, bytes(data_content))

# --- Hack for stack_chk_guard ---
# without this will throw invalid memory read at 0x0
emu.mem_map(0x0, 1024)
emu.mem_write(0x0, b"00")


# --- Execute from 0x1000080d4 to 0x100008154 ---
emu.emu_start(0x1000080d4, 0x100008154)
ret_value = emu.reg_read(UC_ARM64_REG_X0)

# --- Dump return value ---
print(emu.mem_read(ret_value, 11))
```

> You may notice that there is an additional memory allocation at address 0x0. This is a simple hack around the `stack_chk_guard` check. Without this, a memory read error will occur, and the binary cannot be executed. With this hack, the program will access the value at 0x0 and use it for the `stack_chk_guard` check.

To summarize, using Unicorn requires additional setup before executing the binary, but once configured, it can provide deep insights into the binary. It gives the flexibility to perform the full binary or only a subset. Unicorn also exposes APIs to attach hooks to the execution. Using these hooks, you can observe the state of the program at any point during the execution or even manipulate the register or variable values and forcefully explore other execution branches in a program. Another advantage when running a binary in Unicorn is that you don't need to worry about various checks like root/jailbreak detection or debugger detection, etc.
