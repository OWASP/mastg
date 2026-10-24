---
masvs_category: MASVS-RESILIENCE
platform: ios
title: Obfuscation
---

@MASTG-KNOW-0111 introduces common obfuscation techniques that apply across platforms. On iOS, these techniques can affect native Mach-O code, Objective-C and Swift runtime metadata, bundled resources, and data used by the app.

iOS applications are distributed as signed app bundles containing a main Mach-O executable and, often, embedded frameworks, app extensions, and resource files.

Unlike Android Java/Kotlin code, which is compiled to DEX bytecode and can often be decompiled back into Java-like code, iOS apps are compiled into native Mach-O binaries. Static analysis usually works from ARM64 machine code, Objective-C runtime metadata, Swift metadata, symbols, and strings. This means the original source structure, high-level control flow, local variable names, and many type details are not preserved in the same way, making iOS decompilation less direct than Android bytecode decompilation.

This page describes common iOS obfuscation techniques and the binary artifacts they affect.

Learn more about iOS obfuscation techniques:

- [O-MVLL](https://obfuscator.re/omvll/) is an LLVM-based obfuscator for native code. Its documentation describes passes such as string encoding, control-flow flattening, opaque constants, indirect calls and branches, Objective-C metadata cleaning, and anti-hooking.
- ["Protecting Million-User iOS Apps with Obfuscation: Motivations, Pitfalls, and Experience"](https://faculty.ist.psu.edu/wu/papers/obf-ii.pdf) describes practical iOS obfuscation at scale and discusses motivations, pitfalls, and lessons learned from deploying obfuscation in production apps.

## Mach-O Binaries and Runtime Metadata

Mach-O binaries can expose several categories of metadata during static analysis:

- Exported symbols and local symbol table entries.
- Objective-C class names, categories, protocols, properties, and selectors.
- Swift type descriptors, protocol metadata, and mangled Swift symbols.
- C and C++ symbols, including mangled C++ names.
- String literals and constants stored in Mach-O sections such as `__TEXT.__cstring`.

Release builds often strip debugging information and local symbols, but normal symbol stripping does not remove all runtime metadata. Objective-C and Swift features such as dynamic dispatch, reflection, Interface Builder references, and interoperability through `@objc` can require some names or descriptors to remain present in the binary.

Swift and C++ name mangling is different from deliberate obfuscation. Name mangling encodes type and namespace information into a compiler-specific symbol format; demangling tools can recover readable names in many cases (see @MASTG-TECH-0114).

### Symbol Stripping

Symbol stripping removes symbol information from Mach-O binaries, including function names and other metadata that can make reverse engineering easier. It is a basic form of native code obfuscation, but it does not transform control flow, encode strings, or remove all runtime metadata required by Objective-C and Swift. This topic overlaps with debug-symbol handling, which is covered in more detail in @MASTG-KNOW-0063.

### Identifier Renaming

iOS classes, methods, functions, properties, selectors, Swift symbols, type descriptors, storyboard identifiers, and nib references can be renamed to make it harder to correlate an identifier with its purpose during static analysis.

The standard compiler generates binary symbols based on class and function names from the source code. Therefore, if no obfuscation is applied, symbol names can remain meaningful and can be read from the app binary. For instance, a function which detects a jailbreak can be located by searching for relevant keywords such as `jailbreak`. The listing below shows the disassembled function `JailbreakDetectionViewController.jailbreakTest4Tapped` from @MASTG-APP-0024.

```assembly
__T07DVIA_v232JailbreakDetectionViewControllerC20jailbreakTest4TappedyypF:
stp        x22, x21, [sp, #-0x30]!
mov        rbp, rsp
```

After identifier renaming, the symbol name is no longer meaningful:

```assembly
__T07DVIA_v232zNNtWKQptikYUBNBgfFVMjSkvRdhhnbyyFySbyypF:
stp        x22, x21, [sp, #-0x30]!
mov        rbp, rsp
```

This only applies to the names of functions, classes, and fields. The actual code remains unmodified, so the disassembled version of the function can still reveal the function's logic.

Runtime features can depend on stable names. Examples include `NSClassFromString`, key-value coding, reflection, `Codable` key mapping, dynamic selectors, `@objc` declarations, storyboard references, and nib loading. iOS obfuscators therefore commonly use keep rules, mapping files, or build-time project analysis to preserve names that the runtime or app resources still need.

@MASTG-TOOL-0068 is an example of source-level Swift name obfuscation. It analyzes an Xcode project and replaces selected Swift identifiers before compilation.

### String Encryption

String literals can reveal implementation details that are relevant for reverse engineering and understanding the app's business logic, such as endpoint URLs, file paths, feature names, license strings, API keys, jailbreak or debugger detection artifacts, and error messages.

String encryption replaces plaintext literals with encoded or encrypted representations and adds runtime logic that reconstructs the original value before use. On iOS, string literals may appear in Mach-O sections, Swift metadata, Objective-C metadata, resource files, or generated code.

When this technique is applied, the original string values may no longer appear directly in extracted strings or static Mach-O data; the clear strings are only present at runtime.

Some strings cannot be transformed without additional handling because platform frameworks or app resources reference them by name. Examples include Objective-C selectors, class names used by the runtime, storyboard identifiers, localization keys, and values consumed by external services.

O-MVLL provides several [string encoding options](https://obfuscator.re/omvll/passes/strings-encoding/) for native code.

The following example shows how string encoding can be configured with O-MVLL.

```python
def obfuscate_string(self, _, __, string: bytes):
    if string == b"https://api.example.com":
        return omvll.StringEncOptLocal()
    return False
```

### Control Flow Obfuscation

The control-flow graph of a function represents the basic blocks and the conditions required to reach them. This representation is usually an early step in native code analysis and decompilation.

Control-flow obfuscation modifies the compiled representation of functions to produce a more complex control-flow graph in the disassembled or decompiled output. Common examples include control-flow flattening, opaque predicates, and transformations around branch instructions.

Control-flow flattening replaces original code with a more complex representation. The transformation breaks the body of a function into basic blocks and places them inside a dispatcher, commonly an infinite loop with a switch statement that controls the program flow. This removes the natural conditional constructs that usually make code easier to read.

<img src="Images/Chapters/0x06j/control-flow-flattening.png" width="600px" />

The image shows how control-flow flattening alters code. See ["Obfuscating C++ programs via control flow flattening"](https://web.archive.org/web/20240414202600/http://ac.inf.elte.hu/Vol_030_2009/003.pdf) for more information.

O-MVLL provides [control-flow flattening](https://obfuscator.re/omvll/passes/control-flow-flattening/) and [control-flow breaking](https://obfuscator.re/omvll/passes/control-flow-breaking/) passes for native code. Its control-flow breaking pass documents iOS Swift limitations and should be restricted to user-defined functions.

The following example shows how control-flow flattening can be enabled with O-MVLL.

```python
def flatten_cfg(self, mod: omvll.Module, func: omvll.Function):
    if func.name == "validate_license":
        return True
    return False
```

### Instruction and Arithmetic Obfuscation

Instruction substitution replaces standard operations with more complex representations. For example, an addition such as `x = a + b` can be represented as `x = -(-a) - (-b)`. Using a single replacement pattern repeatedly can make the transformation easier to identify, so implementations commonly use several substitution patterns and introduce randomness. Depending on the complexity and depth of the substitutions, reversing the transformed code can still be time consuming.

Arithmetic obfuscation replaces simple arithmetic or bitwise operations with more complex equivalent expressions. O-MVLL provides an [arithmetic obfuscation pass](https://obfuscator.re/omvll/passes/arithmetic/) that rewrites operations into more complex expressions.

The following example shows how arithmetic obfuscation can be enabled with O-MVLL.

```python
def obfuscate_arithmetic(self, mod: omvll.Module,
                         func: omvll.Function) -> omvll.ArithmeticOpt:
    if func.name == "compute_token":
        return omvll.ArithmeticOpt(rounds=8)
    return False
```

### Opaque Constants

Opaque constants protect integer constants by replacing them with more complex computations that reconstruct the original value at runtime. This makes well-known values, magic numbers, and algorithm-specific constants less obvious during static analysis.

O-MVLL provides an [opaque constants pass](https://obfuscator.re/omvll/passes/opaque-constants/) for native code.

The following example shows how opaque constants can be enabled with O-MVLL.

```python
class Config(omvll.ObfuscationConfig):
    def obfuscate_constants(self, mod: omvll.Module, func: omvll.Function):
        if "initialize_keys" in func.demangled_name:
            return True
        return False
```

### Dead Code and Junk Code

Dead code injection makes the program's control flow more complex by adding code that does not affect the original program behavior. These extra blocks increase the amount of code that must be inspected during reverse engineering.

Junk code has a similar goal: add noisy or annoying code paths without changing the intended behavior of the original function. O-MVLL's [Basic Block Duplicate](https://obfuscator.re/omvll/passes/basic-block-duplicate/) pass is an example of this type of transformation.

The following example shows how junk code can be introduced with O-MVLL's Basic Block Duplicate pass.

```python
def basic_block_duplicate(self, mod: omvll.Module, func: omvll.Function):
    return omvll.BasicBlockDuplicateWithProbability(20)
```

### Obfuscated Function Calls

Obfuscated function calls hide the original callee by replacing direct calls with indirect or reconstructed calls. This makes the call graph harder to recover because static analysis tools can no longer follow all call edges directly from the disassembly.

O-MVLL provides passes for [indirect calls](https://obfuscator.re/omvll/passes/indirect-call/) and [indirect branches](https://obfuscator.re/omvll/passes/indirect-branch/). Its indirect branch pass documents known limitations for iOS apps built in Release mode, so the transformation should be validated on the target build configuration.

The following example shows how indirect calls can be enabled with O-MVLL.

```python
def indirect_call(self, mod: omvll.Module, func: omvll.Function):
    if func.name == "perform_check":
        return True
    return False
```

### Objective-C Metadata Cleaning

Objective-C runtime metadata can expose class names, method names, selectors, and protocol information. Cleaning or transforming this metadata can reduce the amount of semantic information visible in the binary, but Objective-C runtime behavior depends on some of this information remaining consistent.

O-MVLL documents an [Objective-C Cleaner](https://obfuscator.re/omvll/passes/objcleaner/) pass, but its current documentation marks it as work in progress. Production tooling must still preserve metadata required by Objective-C runtime behavior and app resources.

### Packing and Runtime Unpacking

Packing stores code or data in a compressed or encrypted representation and restores it at runtime. On iOS, code signing and platform memory protections constrain arbitrary self-modifying code and runtime code generation. App shielding products therefore typically apply transformations before signing, use signed native loaders, or focus on runtime decoding of data and control-flow assets.

@MASTG-KNOW-0111 describes packing at a generic level. On iOS, a practical pattern is to avoid unsigned runtime-generated code and instead ship a signed loader, interpreter, or state machine together with encrypted data. For example, an app can include `policy.vm.enc` or `rules.bundle.enc` in the app bundle; the signed loader decrypts the blob after startup and interprets the resulting bytecode or state table in memory. Static extraction of the IPA then reveals only the encrypted blob and the loader, while dynamic analysis after decryption can still recover the plaintext representation.

### Resource and Asset Encryption

Obfuscation in iOS apps is not limited to executable code. Apps can also encode or encrypt files stored in the app bundle, such as configuration files, scripts, web assets, model files, or other auxiliary data. The app then includes logic to decode or decrypt the resource before using it.

For example, an app can store `security-rules.json.enc`, `model.bin.enc`, or `index.html.enc` in the app bundle and decrypt the file with CryptoKit or CommonCrypto before parsing or rendering it. See @MASTG-KNOW-0066 and @MASTG-KNOW-0067 for iOS cryptographic API context.

```swift
let url = Bundle.main.url(
    forResource: "security-rules",
    withExtension: "json.enc"
)!
let encrypted = try Data(contentsOf: url)
let cleartext = try decrypt(encrypted, with: runtimeKey)
let rules = try JSONDecoder().decode(SecurityRules.self, from: cleartext)
```

This protects against direct resource extraction from the IPA, but it does not prevent recovery of the decrypted data or decryption material during runtime analysis.
