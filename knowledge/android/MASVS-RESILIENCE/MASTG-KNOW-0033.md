---
masvs_category: MASVS-RESILIENCE
platform: android
title: Obfuscation
---

@MASTG-KNOW-0111 introduces common obfuscation techniques that apply across platforms. On Android, those techniques can affect both the Java or Kotlin layer and the native layer.

This page describes common Android obfuscation techniques across both layers.

## Java Layer

Java and Kotlin source code in Android apps is transformed into DEX bytecode for execution on Android.

When reverse engineering the application, the DEX bytecode is decompiled into human-readable pseudocode.

Obfuscation at this layer changes names, literals, control flow, or code loading behavior to reduce how much information the decompiled output reveals and increase the effort required to recover program logic.

Common open-source obfuscators that apply some of the techniques below are [R8](https://developer.android.com/topic/performance/app-optimization/enable-app-optimization), @MASTG-TOOL-0022, and @MASTG-TOOL-0153.

### Identifier Renaming

Android classes, methods, fields, packages, and local variables can be renamed to make it harder to correlate an identifier with its purpose when analyzing decompiled code. Instead of descriptive names, the output may contain short or meaningless identifiers that reveal little about the original identifier purpose in the app.

This is a type of layout obfuscation, which doesn't impact the program's performance.

R8 and @MASTG-TOOL-0022 can rename classes, methods, and fields during the release build process. The behavior is controlled through the release build configuration and keep rules that preserve selected identifiers. See the [Android app optimization documentation](https://developer.android.com/topic/performance/app-optimization/enable-app-optimization), the [Android keep rules guide](https://developer.android.com/topic/performance/app-optimization/add-keep-rules), and the @MASTG-TOOL-0022 [manual](https://www.guardsquare.com/manual/configuration/usage).

The following example shows how identifier renaming can be enabled in a release build with R8 or @MASTG-TOOL-0022.

```groovy
android {
    buildTypes {
        release {
            // Enables code shrinking, obfuscation, and optimization for only
            // your project's release build type.
            minifyEnabled true

            // Includes the default optimization rules files packaged with
            // the Android Gradle plugin. To learn more, go to the section about
            // R8 configuration files.
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

The file `proguard-rules.pro` is where you define custom @MASTG-TOOL-0022 rules. With the flag `-keep` you can keep certain code that is not being removed by R8, which might otherwise produce errors. For example:

```pro
-keep class com.example.api.PublicApi { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
```

### String Encryption

String literals can reveal implementation details that are relevant for reverse engineering and understanding of the business logic of the app, such as endpoint URLs, file paths, feature names, API keys, device attestation detection artifacts (such as root or jailbreak paths), and error messages.

String encryption replaces plaintext literals with encoded or encrypted representations and adds runtime logic that reconstructs the original value before use.

When this technique is applied, the original string values may no longer appear directly in the decompiled code or extracted DEX data; the clear strings are only present at runtime.

@MASTG-TOOL-0153 extends the @MASTG-TOOL-0022 rule format with string obfuscation passes. See its [strings encryption documentation](https://obfuscator.re/dprotect/passes/strings/) for more information about how to encrypt strings.

The following example shows how string obfuscation can be configured with @MASTG-TOOL-0153.

```pro
-obfuscate-strings class com.example.sensitive.ApiClient {
    private static java.lang.String API_KEY;
    public java.lang.String buildHeader();
}
```

### Dynamic Code Loading and Packing

Android apps can keep part of their logic outside the main static DEX view and make it available only at runtime. This can be done by loading additional code with class loaders such as `DexClassLoader`, `PathClassLoader`, or `InMemoryDexClassLoader` (see the [Android documentation](https://developer.android.com/reference/dalvik/system/package-summary)), or by storing code in a transformed representation and restoring it before execution.

This changes where executable logic is stored and when it becomes available to the runtime. As a result, part of the application logic may be separated from the main static codebase and may only become visible after the corresponding loading or unpacking logic executes.

For example, an app can store `payload.dex.enc` in `assets/`, decrypt it after startup, and load the resulting DEX bytes with `InMemoryDexClassLoader`.

```kotlin
val encrypted = assets.open("payload.dex.enc").readBytes()
val dexBuffer = ByteBuffer.wrap(decrypt(encrypted, runtimeKey))
val loader = InMemoryDexClassLoader(dexBuffer, classLoader)
val entry = loader.loadClass("com.example.protected.Entry")
```

Use @MASTG-TOOL-0009 with @MASTG-TECH-0165 to identify known compilers, obfuscators, and packers in APKs. The absence of a known signature does not prove that dynamic loading or custom packing is not present.

### Reflection and Indirect Invocation

Reflection is a property of some programming languages, including Java, that allows an app or process to inspect types and invoke constructors, methods, and fields dynamically at runtime.

This indirect style can reduce the number of explicit call sites visible in decompiled code. Instead of a direct method call, the code may obtain a class with `Class.forName`, resolve a method with `getDeclaredMethod`, and invoke it through the reflection APIs.

Reflection is separate from dynamic code loading. It operates on code that is already present in the app or otherwise available to the runtime, but it changes how that code is referenced and executed.

### Control Flow Obfuscation

The control-flow graph of a function is a representation of the elementary computational blocks and the conditions required to reach them. This representation is usually an early step in the decompilation process.

Control flow obfuscation modifies the bytecode to produce a more complex control-flow graph in the decompiled output. Common examples include inserted conditions, opaque predicates, or transformations around branch instructions such as `GOTO #offset`.

@MASTG-TOOL-0153 provides a dedicated pass for this transformation. See its [control-flow obfuscation documentation](https://obfuscator.re/dprotect/passes/control-flow/).

The following example shows how control-flow obfuscation can be configured with @MASTG-TOOL-0153.

```pro
-obfuscate-control-flow class com.example.sensitive.** { *; }
```

### Dead Code Injection

Dead code injection adds code paths, methods, branches, or classes that do not contribute to the program's final behavior. This increases the amount of code that appears relevant during reverse engineering and makes static analysis outputs noisier.

This technique is different from control flow obfuscation. Control flow obfuscation mainly restructures real logic, while dead code injection adds extra logic that is not required for the program's intended result.

### Resource and Asset Encryption

Obfuscation in Android apps is not limited to executable code. Apps can also encode or encrypt files stored in `assets/`, `res/raw/`, or other packaged resources so that their content is not directly visible after extracting the APK.

These resources may contain configuration data, scripts, model files, web assets, or auxiliary data consumed by the Java or Kotlin layer at runtime. The app then includes logic to decode or decrypt the resource before using it.

For example, an app can store `rules.json.enc` or `model.bin.enc` in `assets/` and decrypt the file with `javax.crypto.Cipher` before parsing or passing it to the relevant runtime component.

```kotlin
val encrypted = assets.open("rules.json.enc").readBytes()
val cleartext = cipher.doFinal(encrypted)
val rules = JSONObject(cleartext.decodeToString())
```

This protects against direct resource extraction from the APK, but it does not prevent recovery of the decrypted data or decryption material during runtime analysis.

## Native Layer

Native code can be obfuscated with many different transformations. On Android, these techniques are commonly applied to C or C++ code compiled with the NDK and operate on lower-level representations such as LLVM IR or machine code, rather than on DEX bytecode.

An open-source reference for native obfuscation techniques is [O-MVLL](https://obfuscator.re/omvll/passes/arithmetic/), an LLVM-based obfuscator that documents several common protections and their tradeoffs.

### Symbol Stripping

Symbol stripping is also a basic form of obfuscation in native code because it removes function names and other information that can make reverse engineering easier. This topic is covered in more detail in @MASTG-KNOW-0008.

Native libraries that use name-based JNI resolution still expose descriptive exported `Java_*` symbols even after symbol stripping. Registering native methods dynamically through `JNI_OnLoad` and `RegisterNatives` can reduce that exposure, as the symbols of such functions can be stripped due to not needing to follow the `Java_*` convention.

### Control Flow Flattening

Control flow flattening modifies the graph structure of a function by breaking it into basic blocks and placing them behind a dispatcher-driven structure, such as a loop and switch-based state machine.

This makes the execution flow harder to follow because the natural branching structure of the original function is no longer directly visible in the disassembled or decompiled output.

[O-MVLL](https://obfuscator.re/omvll/passes/control-flow-flattening/) provides a control-flow flattening pass for native code.

The following example shows how control-flow flattening can be enabled with O-MVLL.

```python
def flatten_cfg(self, mod: omvll.Module, func: omvll.Function):
    if func.name == "check_password":
        return True
    return False
```

### Junk Code

Junk code aims to add code that is noisy or annoying to analyze without changing the intended behavior of the original function.

In native code, one way to achieve this is to add duplicate basic blocks, redundant branches, or to introduce extra code paths that increase the size and complexity of the control-flow graph.

For example, adding duplicate basic blocks is made by [O-MVLL Basic Block Duplicate](https://obfuscator.re/omvll/passes/basic-block-duplicate/), which, as said previously, duplicates the selected basic blocks and inserts a runtime branch that chooses between equivalent versions of the same logic.

The following example shows how junk code can be introduced with O-MVLL's Basic Block Duplicate pass.

```python
def basic_block_duplicate(self, mod: omvll.Module, func: omvll.Function):
    return omvll.BasicBlockDuplicateWithProbability(20)
```

### Arithmetic Obfuscation

Arithmetic obfuscation replaces simple arithmetic or bitwise operations with more complex equivalent expressions. This makes sensitive computations harder to recognize in native code because the resulting instructions no longer closely resemble the original source-level operation.

[O-MVLL](https://obfuscator.re/omvll/passes/arithmetic/) provides an arithmetic obfuscation pass that rewrites operations into more complex expressions.

The following example shows how arithmetic obfuscation can be enabled with O-MVLL.

```python
def obfuscate_arithmetic(self, mod: omvll.Module,
                         func: omvll.Function) -> omvll.ArithmeticOpt:
    if func.name == "encode":
        return omvll.ArithmeticOpt(rounds=8)
    return False
```

### Strings Encoding

Plaintext strings in native code can reveal implementation details such as file names, API tokens, feature names, error messages, or endpoint URLs. String encoding replaces these values with encoded representations and adds logic to reconstruct them before use.

[O-MVLL](https://obfuscator.re/omvll/passes/strings-encoding/) provides several string encoding options for native code.

The following example shows how string encoding can be configured with O-MVLL.

```python
def obfuscate_string(self, _, __, string: bytes):
    if string == b"OMVLL":
        return omvll.StringEncOptLocal()
    return False
```

### Opaque Constants

Opaque constants protect integer constants by replacing them with more complex computations that reconstruct the original value at runtime. This makes well-known values, magic numbers, and algorithm-specific constants less obvious during static analysis.

[O-MVLL](https://obfuscator.re/omvll/passes/opaque-constants/) provides a pass for obfuscating constants in native code.

The following example shows how opaque constants can be enabled with O-MVLL.

```python
class Config(omvll.ObfuscationConfig):
    def obfuscate_constants(self, mod: omvll.Module, func: omvll.Function):
        if "init_context_all" in func.demangled_name:
            return True
        return False
```

### Obfuscated Function Calls

Obfuscated function calls hide the original callee by replacing direct calls with indirect or reconstructed calls. This makes the call graph harder to recover because static analysis tools can no longer follow all call edges directly from the disassembly.

[O-MVLL](https://obfuscator.re/omvll/passes/indirect-call/) provides an indirect call pass that converts direct calls into indirect calls.

The following example shows how indirect calls can be enabled with O-MVLL.

```python
def indirect_call(self, mod: omvll.Module, func: omvll.Function):
    if func.name == "check_password":
        return True
    return False
```
