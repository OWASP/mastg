---
masvs_category: MASVS-RESILIENCE
platform: generic
title: Obfuscation
---

Obfuscation is the process of transforming code and data to make it more difficult to comprehend (and sometimes even difficult to disassemble). It is usually an integral part of the software protection scheme. Obfuscation isn't something that can be simply turned on or off, programs can be made incomprehensible, in whole or in part, in many ways and to different degrees.

!!! note
    All the presented techniques below will not stop someone with enough time and budget from reverse engineering your app. However, combining these techniques will make their job significantly harder. The aim is thus to discourage reverse engineers from performing further analysis and not making it worth the effort.

The following techniques can be used to obfuscate an application:

- Name obfuscation
- Instruction substitution
- Control flow flattening
- Dead code injection
- String encryption
- Packing

## Name Obfuscation

The standard compiler generates binary symbols based on class and function names from the source code. Therefore, if no obfuscation is applied, symbol names remain meaningful and can easily be extracted from the app binary. For instance, a function which detects a jailbreak can be located by searching for relevant keywords (e.g. "jailbreak"). The listing below shows the disassembled function `JailbreakDetectionViewController.jailbreakTest4Tapped` from the @MASTG-APP-0024.

```assembly
__T07DVIA_v232JailbreakDetectionViewControllerC20jailbreakTest4TappedyypF:
stp        x22, x21, [sp, #-0x30]!
mov        rbp, rsp
```

After the obfuscation we can observe that the symbol's name is no longer meaningful as shown on the listing below.

```assembly
__T07DVIA_v232zNNtWKQptikYUBNBgfFVMjSkvRdhhnbyyFySbyypF:
stp        x22, x21, [sp, #-0x30]!
mov        rbp, rsp
```

Nevertheless, this only applies to the names of functions, classes and fields. The actual code remains unmodified, so an attacker can still read the disassembled version of the function and try to understand its purpose (e.g. to retrieve the logic of a security algorithm).

## Instruction Substitution

This technique replaces standard binary operators like addition or subtraction with more complex representations. For example, an addition `x = a + b` can be represented as `x = -(-a) - (-b)`. However, using the same replacement representation could be easily reversed, so it is recommended to add multiple substitution techniques for a single case and introduce a random factor. This technique can be reversed during decompilation, but depending on the complexity and depth of the substitutions, reversing it can still be time consuming.

## Control Flow Flattening

Control flow flattening replaces original code with a more complex representation. The transformation breaks the body of a function into basic blocks and puts them all inside a single infinite loop with a switch statement that controls the program flow. This makes the program flow significantly harder to follow because it removes the natural conditional constructs that usually make the code easier to read.

<img src="Images/Chapters/0x06j/control-flow-flattening.png" width="100%" />

The image shows how control flow flattening alters code. See ["Obfuscating C++ programs via control flow flattening"](https://web.archive.org/web/20240414202600/http://ac.inf.elte.hu/Vol_030_2009/003.pdf) for more information.

## Dead Code Injection

This technique makes the program's control flow more complex by injecting dead code into the program. Dead code is a stub of code that doesn't affect the original program's behavior but increases the overhead of the reverse engineering process.

## String Encryption

Applications are often compiled with hardcoded keys, licences, tokens and endpoint URLs. By default, all of them are stored in plaintext in the data section of an application's binary. This technique encrypts these values and injects stubs of code into the program that will decrypt that data before it is used by the program.

## Packing

[Packing](https://attack.mitre.org/techniques/T1027/002/) is a dynamic rewriting obfuscation technique which compresses or encrypts the original executable into data and dynamically recovers it during execution. Packing an executable changes the file signature in an attempt to avoid signature-based detection.
