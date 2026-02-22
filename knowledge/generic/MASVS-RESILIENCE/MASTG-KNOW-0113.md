---
masvs_category: MASVS-RESILIENCE
platform: generic
title: Using Disassemblers and Decompilers
---

Disassemblers and decompilers allow you to translate an app's binary code or bytecode back into a more or less understandable format. By using these tools on native binaries, you can obtain assembler code that matches the architecture the app was compiled for.

## Disassemblers

Disassemblers convert machine code to assembly code which in turn is used by decompilers to generate equivalent high-level language code. Android Java apps can be disassembled to smali, which is an assembly language for the DEX format used by Dalvik, Android's Java VM. Smali assembly can also be quite easily decompiled back to equivalent Java code.

In theory, the mapping between assembly and machine code should be one-to-one, and therefore it may give the impression that disassembling is a simple task. But in practice, there are multiple pitfalls such as:

- Reliable distinction between code and data.
- Variable instruction size.
- Indirect branch instructions.
- Functions without explicit CALL instructions within the executable's code segment.
- Position independent code (PIC) sequences.
- Handcrafted assembly code.

## Decompilers

Decompilation is a very complicated process, involving many deterministic and heuristic based approaches. As a consequence, decompilation is usually not really accurate, but nevertheless very helpful in getting a quick understanding of the function being analyzed. The accuracy of decompilation depends on the amount of information available in the code being decompiled and the sophistication of the decompiler. In addition, many compilation and post-compilation tools introduce additional complexity to the compiled code in order to increase the difficulty of comprehension and/or even decompilation itself. Such code is referred to as _obfuscated code_ (see @MASTG-KNOW-0111).

Over the past decades many tools have perfected the process of disassembly and decompilation, producing output with high fidelity. Advanced usage instructions for any of the available tools can often easily fill a book of their own. The best way to get started is to simply pick up a tool that fits your needs and budget and read a well-reviewed user guide.
