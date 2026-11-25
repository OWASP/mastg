---
name: 'Writing MASTG Demo Files'
applyTo: 'demos/**/*.md'
---

A collection of demos (demonstrative examples) of the test that include working code samples and test scripts to ensure reproducibility and reliability.

Demos live in `demos/android/` or `demos/ios/` under the corresponding MASVS category folder. Each demo has its own folder named using its ID and contains:

- Markdown file: `MASTG-DEMO-xxx.md`
Code samples (`*.kt`, `*.swift`, `*.cpp`, `*.xml`, `*.plist`, `*.proto`)
- Build customization files (`build.gradle.kts.*`, `proguard-rules.pro`, `CMakeLists.txt`, `entitlements.plist`)
- Testing code (e.g. `*.sh`, `*.py`)
- Output files (e.g. `*.txt`, `*.json`, `*.sarif`)

**Language:** The samples are written in **Kotlin** or **Swift**, depending on the platform. In some cases, the samples will also include configuration files such as `AndroidManifest.xml` or `Info.plist`.

**Decompiled Code:** Decompiled code must be provided if the demo involves static analysis.

- **Android:** From the demo folder, run `apk-install .` and then `apk-extract .` (see ["MASTestApp for Android - Extract the Reversed Files"](https://github.com/cpholguera/mas-app-android#extract-the-reversed-files)) to obtain the relevant files, such as the reversed `AndroidManifest.xml` and the `MastgTest_reversed.java`, which is the MASTestApp's main file. The decompiled classes use the demo's package, the same as the APKs built by the CI. Including the full version of these files is also useful for understanding the code in the context of the application, regardless of whether the demo focuses on a specific snippet.
- **iOS:** Follow the instructions in ["MASTestApp for iOS - Reverse Engineering"](https://github.com/cpholguera/MASTestApp-iOS) to obtain the relevant files, such as the IPA file and the reversed `Info.plist` already converted to XML format. Currently, you need to extract the main binary, MASTestApp, manually, and include it in the demo folder (we allow this for now since the files are sufficiently small). The demos will typically use reverse engineering tools, such as `r2`, on this binary.

The **demos MUST WORK**. See [Code Samples](#code-samples).

Demos are required to be **fully self-contained** and should **not rely on external resources or dependencies**. This ensures that the demos can be run independently and that the results are reproducible. They must be proven to work on the provided sample applications and must be tested thoroughly before being included in the MASTG.

**Don't create demos for outdated OS versions** that aren't supported by the MASTG. The MASTestApp is always intended to be up to date and aligned with the versions supported by the MASTG, thereby avoiding additional maintenance of the MASTestApp. However, you can include demos showcasing the "good case" in the metadata using `kind: pass` in certain cases where it can be helpful or educational. This is permitted as long as the demos work with the current version of the MASTestApp. For demos that act as a standalone attacker app targeting a victim app, use `kind: attack` (see [kind](#kind) for details).

Please specify the mobile platform version, IDE version, and device.

Android Example:

```sh
% ls -1 -F demos/android/MASVS-CRYPTO/MASTG-DEMO-0007

MASTG-DEMO-0007.md
MastgTest.kt
MastgTest_reversed.java
output.txt
run.sh*
```

iOS Example:

```sh
% ls -1 -F ./demos/ios/MASVS-CRYPTO/MASTG-DEMO-0016/
MASTG-DEMO-0016.md
MASTestApp*
MastgTest.swift
cryptokit_hash.r2
output.txt
run.sh*
```

Android Example (native code with build customization):

```sh
% ls -1 -F demos/android/MASVS-RESILIENCE/MASTG-DEMO-0x02/

MASTG-DEMO-0x02.md
build.gradle.kts.android
MastgTest.kt
MastgTest_reversed.java
native-root-check.cpp
output.txt
run.sh*
```

## Android: App Customization Files

The build system (`.github/workflows/build-android-demos.yml`) builds each demo with the `tools/apk-build.sh` script from [mas-app-android](https://github.com/cpholguera/mas-app-android). The script applies the following optional files from the demo folder to a temporary copy of the base app and builds a debug APK. Include only the files relevant to the demo.

To build and run a demo locally the same way, clone mas-app-android, run its `tools/install-cli.sh` once, and then run `apk-build .` or `apk-install .` from the demo folder. See the [mas-app-android build documentation](https://github.com/cpholguera/mas-app-android/blob/main/docs/demo-build-and-packaging.md) for details.

### Package and app name

Each demo is built as its own app, so demos never replace each other on a device:

- **Package:** `org.owasp.mastestapp.<demo ID>`, with the demo ID lowercased and the dashes removed. For example, `MASTG-DEMO-0054` is built as `org.owasp.mastestapp.mastgdemo0054`.
- **App name:** the demo ID, for example `MASTG-DEMO-0054`. The APK is named after it too: `MASTG-DEMO-0054.apk`.

**IMPORTANT:** Keep `package org.owasp.mastestapp` in all `.kt` files. The build moves the sources to the demo's package automatically, so:

- In `AndroidManifest.xml`, refer to the app's own classes with relative names (`.MainActivity`, `.MastgTest$ShareReportActivity`), not fully qualified ones.
- In code, use `context.packageName` when you need the app's own package at runtime.
- In `run.sh`, Frida or frooky scripts, and commands in the demo's Markdown, use the demo's package (for example `frida -U -f org.owasp.mastestapp.mastgdemo0054 ...`), including in class names (`org.owasp.mastestapp.mastgdemo0054.MastgTest`).
- When the demo gets its real ID before merging, update these references too, because the package changes with the ID (`MASTG-DEMO-0x01` is built as `org.owasp.mastestapp.mastgdemo0x01`).

Content provider authorities and custom intent actions are plain strings that the build doesn't change. Prefer deriving authorities from the package (`${applicationId}.provider` in the manifest, `"${context.packageName}.provider"` in code) so that demos can be installed side by side.

### Source files

These files replace the corresponding Kotlin source files located under `app/src/main/java/org/owasp/mastestapp/` (moved to the demo's package at build time, see [Package and app name](#package-and-app-name)). All source files use Kotlin, not Java.

- `MastgTest.kt` — main test entry point (required for most demos)
- `MainActivity.kt` — override the main activity
- `MastgTestWebView.kt` — override the WebView activity

### Resource and configuration files

These files replace the corresponding resource files in the base app:

| Demo file | Destination in base app |
| --- | --- |
| `AndroidManifest.xml` | `app/src/main/AndroidManifest.xml` |
| `accessibility_service.xml` | `app/src/main/res/xml/accessibility_service.xml` |
| `filepaths.xml` | `app/src/main/res/xml/filepaths.xml` |
| `network_security_config.xml` | `app/src/main/res/xml/network_security_config.xml` |
| `backup_rules.xml` | `app/src/main/res/xml/backup_rules.xml` |
| `data_extraction_rules.xml` | `app/src/main/res/xml/data_extraction_rules.xml` |
| `icon.png` | Launcher icon foreground (`app/src/main/res/drawable/ic_launcher_icon_fg`) |

### Build customization files

The following files are injected into `app/build.gradle.kts` at the matching `// ADD_<KIND>_HERE` placeholder comment:

| Demo file | Injected at |
| --- | --- |
| `build.gradle.kts.plugins` | `// ADD_PLUGINS_HERE` — inside the `plugins {}` block |
| `build.gradle.kts.sections` | `// ADD_SECTIONS_HERE` — as a new top-level block |
| `build.gradle.kts.android` | `// ADD_ANDROID_HERE` — inside the `android {}` block |
| `build.gradle.kts.libs` | `// ADD_LIBS_HERE` — inside the `dependencies {}` block |
| `build.gradle.kts.build` | `// ADD_BUILD_HERE` — inside the `buildTypes {}` block |

Use these files to add Gradle plugins, dependency declarations, or `android {}` configuration (for example, ProGuard or CMake settings) without modifying the base app.

To customize ProGuard/R8 rules, provide a `proguard-rules.pro` file. It replaces `app/proguard-rules.pro`. Write the rules for `org.owasp.mastestapp`; the build rewrites them for the demo's package. An `obfuscationdictionary.txt` file is copied to `app/` for use with `-obfuscationdictionary`.

### Native code files

To add native C/C++ code, provide a `CMakeLists.txt` file. All `*.cpp`, `*.c` and `*.h` files in the demo folder are also copied to `app/src/main/cpp/`. You must also provide a `build.gradle.kts.android` file that enables the CMake build:

```kotlin
externalNativeBuild {
    cmake {
        path = file("src/main/cpp/CMakeLists.txt")
    }
}
```

### Protocol Buffer files

All `*.proto` files in the demo folder are copied to `app/src/main/proto/`.

## iOS: App Customization Files

The build system (`.github/workflows/build-ios-demos.yml`) processes the following optional files from the demo folder and injects them into the `mas-app-ios` base app before building the IPA:

| Demo file | Purpose |
| --- | --- |
| `MastgTest.swift` | Replaces `MASTestApp/MastgTest.swift` (main test entry point) |
| `Info.plist` | Replaces `MASTestApp/Info.plist` (app configuration) |
| `entitlements.plist` | Passed to `ldid` during signing (for example, to request `com.apple.developer.associated-domains`) |

## Creating Demo IDs

When creating a new demo (whether porting from v1 or writing from scratch), use a **fake ID** starting at `MASTG-DEMO-0x01` and incrementing within the PR (e.g., `MASTG-DEMO-0x01`, `MASTG-DEMO-0x02`, `MASTG-DEMO-0x03`). This prevents conflicts between parallel pull requests.

Once your pull request is reviewed and ready to merge, the team will assign real IDs (for example, `MASTG-DEMO-0054`) before the content is published.

## Markdown: Metadata

### id

The demo ID. This should match the folder name.

Example:

```md
id: MASTG-DEMO-0054
```

### title

The title should concisely express what the demo is about.

Example:

```md
title: Common Uses of Insecure Random APIs
```

### platform

The mobile platform. One of: `ios`, `android`.

### code

The language(s) in which the samples are written. This must not include the reverse-engineered files (e.g. `.java`, `.asm`, etc.)

Example:

```md
code: [kotlin]
```

Multi-language example:

```md
code: [xml, kotlin]
```

### kind

Optional. When helpful, specify whether the demo demonstrates a passing or failing case, or whether it is a standalone attacker app.

Valid values: `pass`, `fail`, `attack`.

- `fail`: the sample demonstrates the vulnerable behavior (default for most demos).
- `pass`: the sample demonstrates the secure / correctly implemented behavior.
- `attack`: the sample IS the attacker app — a separate APK that exploits a vulnerability in a victim app (typically the one demonstrated in a linked `fail` demo). Use this only when the demo's `MastgTest.kt` acts as the attacking party rather than the victim. Attacker-app demos are excluded from the "Confirming the Vulnerability" requirement in the parent test, since the demo itself embodies the attack.

See [Attacker Apps](#attacker-apps) for the additional requirements of demos with `kind: attack`.

Example:

```md
kind: attack
```

### optional fields

Include these if relevant:

- `status:` draft, placeholder, deprecated
- `note:` short free-form note providing additional context

## Attacker Apps

Demos with `kind: attack` represent a self-contained attacker app that targets a victim app. They have additional requirements beyond those of regular demos.

### Safety Requirements

Attacker apps are published by OWASP, so they **MUST** be harmless by design and scoped to the demo's sample app. The website automatically shows an "Attacker App Disclaimer" banner on every demo with `kind: attack`, and its statements must remain true for every attacker app.

**Harmless by design:**

- Keep the attacker app a minimal proof of concept that demonstrates the weakness and nothing more.
- Only use public, documented platform APIs, and only permissions or special access that the user explicitly grants, such as enabling an accessibility service or allowing overlays.
- Don't exploit vulnerabilities in the operating system or in apps other than the demo's sample app. Don't use hidden or private APIs, native exploits, privilege escalation, or root.
- Don't persist beyond the demo (for example, no boot receivers, scheduled jobs, or foreground services that keep running), and don't hide the app or its activity from the user.
- Don't send data off the device. Show the results in the attacker app's UI or in logcat only.
- Use benign, clearly labeled payloads (for example, `COMPROMISED BY ATTACKER` in a file in the attacker's own cache), and don't modify anything outside the attacker app and the sample app.

**Scoped to the sample app:**

- Target only the MASTestApp sample from the linked demo, using its package (for example `org.owasp.mastestapp.mastgdemo0122` for @MASTG-DEMO-0122, see [Package and app name](#package-and-app-name)), for example with explicit component names, the sample's custom intent actions, or authorities.
- When an API can observe or interact with other apps, restrict it to the sample's package. For example, set `android:packageNames="org.owasp.mastestapp.mastgdemo0122"` in an accessibility service configuration and `packageNames` in `AccessibilityServiceInfo`. If the demo needs to show that a real attacker wouldn't use such a filter, explain it in the "Sample" section instead of removing the filter.
- Use dummy data only, and tell readers in the "Steps" section to use a test device or emulator and to disable or uninstall the attacker app, and revoke any special access, when they've finished.


### MastgTest.kt conventions

Attacker-app demos **MUST** follow the same `MastgTest.kt` conventions as all other demos:

- `package org.owasp.mastestapp` (the build moves it to the demo's package)
- `class MastgTest(private val context: Context)` as the primary class
- `fun mastgTest(): String` as the entry point called by the Start button in `MainActivity`

`mastgTest()` should start the activity or service that drives the attack and return a descriptive string (for example, instructions on where to observe the result). Additional classes (such as an `AttackerActivity` that holds an `ActivityResultLauncher`) are placed in the same file.

### MastgTest.swift conventions

Similarly to Android attacker-apps, iOS attacker-app demos **MUST** follow the same `MastgTest.swift` conventions as all other demos.

## Markdown: Body

### Sample

Shortly describe the sample and specify the exact sample files using this notation:

**Single file:**

```md
{{ MastgTest.kt }}
```

**Multi-file rendered in tabs:**

```md
{{ MastgTest.kt # MastgTest_reversed.java }}
```

You can reuse any files from other demos to avoid duplication (this also applies to scripts, hooks, rules, etc):

```md
{{ ../MASTG-DEMO-0095/MastgTest.swift }}
```

Example:

```md
## Sample

The snippet below shows sample code that sends sensitive data over the network using the `HttpURLConnection` class. The data is sent to `https://httpbin.org/post`, which is a dummy endpoint that returns the data it receives.

{{ MastgTest.kt # MastgTest_reversed.java }}
```

### Steps

A concise write-up of every action needed to reproduce the test result. Always include placeholders for any testing code and scripts involved (for example, SAST rules, `run.sh`, `hooks.json`).

Always reference official tool IDs (for example, `@MASTG-TOOL-0110`) and technique IDs (for example, `@MASTG-TECH-0005`). Only fall back to a well-known tool name when no official ID exists.

Use **prose** for single-command static analysis (for example, a semgrep rule run):

```md
## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-random-apis-insufficient-entropy.yml }}

{{ run.sh }}
```

Use a **numbered list** for multi-step or interactive workflows (for example, dynamic analysis with Frida):

```md
## Steps

1. Install the app on a device (@MASTG-TECH-0005).
2. Make sure you have @MASTG-TOOL-0145 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Tap the **Start** button.
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

{{ hooks.json # run.sh }}
```

### Observation

A concise description of the observation for this specific demo, including placeholders for output files (for example, `output.txt`, `output.json`).

Example:

```md
## Observation

The rule has identified some instances in the code file where a non-random source is used. The specified line numbers can be located in the original code for further investigation and remediation.

{{ output.txt }}
```

### Evaluation

A concise explanation of how you applied the test's "Evaluation" section to this demo. If lines are present in the observation, explain each relevant line.

It MUST start with "The test case fails because ...".

Example:

```md
## Evaluation

The test case fails because the app generates random numbers using insecure APIs in security-relevant contexts.

Review each of the reported instances.

- Line 12 seems to be used to generate random numbers for security purposes, in this case for generating authentication tokens.
- Line 17 is part of the function `get_random`. Review any calls to this function to ensure that the random number is not used in a security-relevant context.
- Line 27 is part of the password generation function, which is a security-critical operation.

Note that line 37 did not trigger the rule because the random number is generated using `SecureRandom`, which is a secure random number generator.
```

## Code Samples

Code samples for demos **must be** **created using one of our test apps** to ensure consistency across demos and facilitate the review process:

- [https://github.com/cpholguera/mas-app-android](https://github.com/cpholguera/mas-app-android)
- [https://github.com/cpholguera/mas-app-ios](https://github.com/cpholguera/mas-app-ios)

Simply clone the repository and follow the instructions in the README files to run the apps on your local machine. You **must use these apps to validate the demos** before submitting them to the MASTG.

### File

Must be a modified version of the original files in the apps' repos:

- Android: `app/src/main/java/org/owasp/mastestapp/MastgTest.kt`
- iOS: `MASTestApp/MastgTest.swift`

When working on a new demo, you **must include the whole file** with the original name in the demo folder.

When reusing source or configuration files from another demo, include relative symbolic links instead of duplicating their contents.

From the demo directory, create a link for each file you need to reuse:

```sh
ln -s ../<source-demo>/<filename> <filename>
```

For example, to reuse `MastgTest.kt` and `AndroidManifest.xml` from `MASTG-DEMO-YYYY` in Android demo `MASTG-DEMO-XXXX`, run these commands from the `MASTG-DEMO-XXXX` directory:

```sh
ln -s ../MASTG-DEMO-YYYY/MastgTest.kt MastgTest.kt
ln -s ../MASTG-DEMO-YYYY/AndroidManifest.xml AndroidManifest.xml
```

### Summary

Must contain a summary as a comment.

Example:

```kt
// SUMMARY: This sample demonstrates various common ways of generating random numbers insecurely in Java.
```

### Logic

The file must include code that demonstrates the addressed weakness.
The provided default `MastgTest.kt` and `MastgTest.swift` files contain some basic logic that returns a string to the UI. If possible, try to return some meaningful string.

For example, if you generate a random number, you can return it; or if you write files to external storage, you can return a list of file paths so that the app's user can read them. You can also use that string to display some meaningful errors.

### Fail/Pass

Optionally, may contain comments indicating fail/pass and the test alias. This way, we can validate that the output is correct (e.g., the code contains three failures of `MASTG-TEST-0204`). We can easily parse and count the comments, and we can do the same in the output.

Each FAIL/PASS comment must include the test ID and an explanation of why it fails/passes.

Example:

```kt
// FAIL: [MASTG-TEST-0204] The app insecurely generates authentication tokens using random numbers.
return r.nextDouble();


// PASS: [MASTG-TEST-0204] The app uses a secure random number generator.
return number.nextInt(21);
```

### run.sh

Every demo that can be automated must contain a `run.sh` file that runs the analysis or instrumentation and generates the referenced output artifacts.

#### Static

Static demos must work using the **reverse-engineered code**. The apps' repositories contain scripts or instructions to obtain the reversed files.

Example: semgrep

`NO_COLOR=true semgrep -c ../../../../rules/mastg-android-insecure-random-use.yaml ./MastgTest_reversed.java --text -o output.txt`

#### Dynamic

Example: frooky (preferred, see [mastg-frooky-hooks.instructions](mastg-frooky-hooks.instructions.md))

`frooky -U -f org.owasp.mastestapp.mastgdemo0054 --platform android hooks.json`

Example: frida-trace

`frida-trace -U -f org.owasp.mastestapp.mastgdemo0054 --runtime=v8 -j '*!*certificate*/isu' > output.txt`

Example: frida (use only when frooky is not enough, see [mastg-frida-scripts.instructions](mastg-frida-scripts.instructions.md))

`frida -U org.owasp.mastestapp.mastgdemo0054 -l hook_edittext.js > output.txt`

#### Networking

Example: mitmproxy (see [mastg-mitmproxy-scripts.instructions](mastg-mitmproxy-scripts.instructions.md))

`mitmdump -s mitm_sensitive_logger.py`
