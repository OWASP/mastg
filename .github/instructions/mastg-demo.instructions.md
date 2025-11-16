## Demos

A collection of demos (demonstrative examples) of the test that include working code samples and test scripts to ensure reproducibility and reliability.

Demos live in `demos/android/` or `demos/ios/` under the corresponding MASVS category folder. Each demo has its own folder named using its ID and contains:

* Markdown file: `MASTG-DEMO-xxx.md`
* Code samples (e.g. `*.kt`, `*.swift`, `*.xml`, `*.plist`)
* Testing code (e.g. `*.sh`, `*.py`)
* Output files (e.g. `*.txt`, `*.json`, `*.sarif`)

**Language:** The samples are written in **Kotlin** or **Swift**, depending on the platform. In some cases, the samples will also include configuration files such as `AndroidManifest.xml` or `Info.plist`.

**Decompiled Code:** Decompiled code must be provided if the demo involves static analysis.

- **Android:** Follow the instructions in ["MASTestApp for Android - Run the Extraction Script"](https://github.com/cpholguera/mas-app-android) to obtain the relevant files, such as the reversed `AndroidManifest.xml` and the `MastgTest_reversed.java`, which is the MASTestApp's main file. Including the full version of these files is also useful for understanding the code in the context of the application, regardless of whether the demo focuses on a specific snippet.
- **iOS:** Follow the instructions in ["MASTestApp for iOS - Reverse Engineering"](https://github.com/cpholguera/MASTestApp-iOS) to obtain the relevant files, such as the IPA file and the reversed `Info.plist` already converted to XML format. Currently, you need to extract the main binary, MASTestApp, manually, and include it in the demo folder (we allow this for now since the files are sufficiently small). The demos will typically use reverse engineering tools, such as `r2`, on this binary.

The **demos MUST WORK**. See [Code Samples](#code-samples).

Demos are required to be **fully self-contained** and should **not rely on external resources or dependencies**. This ensures that the demos can be run independently and that the results are reproducible. They must be proven to work on the provided sample applications and must be tested thoroughly before being included in the MASTG.

**Don't create demos for outdated OS versions** that aren't supported by the MASTG. The MASTestApp is always intended to be up to date and aligned with the versions supported by the MASTG, thereby avoiding additional maintenance of the MASTestApp. However, you can include demos showcasing the "good case" in the metadata using `kind: pass` in certain cases where it can be helpful or educational. This is permitted as long as the demos work with the current version of the MASTestApp.

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

### Markdown: Metadata

#### id

The demo ID. This should match the folder name.

Example:

```md
id: MASTG-DEMO-0054
```

#### title

The title should concisely express what the demo is about.

Example:

```md
title: Common Uses of Insecure Random APIs
```

#### platform

The mobile platform. One of: `ios`, `android`.

#### tools

Tools used in the demo.

When available, prefer referencing official tool IDs from https://mas.owasp.org/MASTG/tools/ (for example, `MASTG-TOOL-0031`). If an official ID is not available, you may use a well-known tool name (for example, `semgrep`).

Example:

```md
tools: [MASTG-TOOL-0031]
```

Example without an official ID:

```md
tools: [semgrep]
```

#### code

The language(s) in which the samples are written. Multiple values are supported.

Example:

```md
code: [java]
```

Multi-language example:

```md
code: [xml, kotlin]
```

#### kind

Optional. When helpful, specify whether the demo demonstrates a passing or failing case.

Valid values: `pass`, `fail`.

Example:

```md
kind: pass
```

#### optional fields

Include these if relevant:

- `status:` draft, placeholder, deprecated
- `note:` short free-form note providing additional context

### Markdown: Body

#### Sample

Shortly describe the sample and specify the exact sample files using this notation:

**Single file:**

```md
{{ MastgTest.kt }}
```

**Multi-file rendered in tabs:**

```md
{{ MastgTest.kt # MastgTest_reversed.java }}
```

Example:

```md
### Sample

The snippet below shows sample code that sends sensitive data over the network using the `HttpURLConnection` class. The data is sent to `https://httpbin.org/post`, which is a dummy endpoint that returns the data it receives.

{{ MastgTest.kt # MastgTest_reversed.java }}
```

#### Steps

A concise write-up following all steps from the linked test, including placeholders for testing code and scripts (for example, SAST rules, `run.sh`).

Example:

```md
### Steps

Let's run our semgrep rule against the sample code.

{{ ../../../../rules/mastg-android-non-random-use.yaml }}

{{ run.sh }}
```

#### Observation

A concise description of the observation for this specific demo, including placeholders for output files (for example, `output.txt`, `output.json`).

Example:

```md
### Observation

The rule has identified some instances in the code file where a non-random source is used. The specified line numbers can be located in the original code for further investigation and remediation.

{{ output.txt }}
```

#### Evaluation

A concise explanation of how you applied the test’s “Evaluation” section to this demo. If lines are present in the observation, explain each relevant line.

Example:

```md
### Evaluation

Review each of the reported instances.

- Line 12 seems to be used to generate random numbers for security purposes, in this case for generating authentication tokens.
- Line 17 is part of the function `get_random`. Review any calls to this function to ensure that the random number is not used in a security-relevant context.
- Line 27 is part of the password generation function, which is a security-critical operation.

Note that line 37 did not trigger the rule because the random number is generated using `SecureRandom`, which is a secure random number generator.
```


### Code Samples {#code-samples}

Code samples for demos **must be** **created using one of our test apps** to ensure consistency across demos and facilitate the review process:

* [https://github.com/cpholguera/MASTestApp-Android](https://github.com/cpholguera/MASTestApp-Android)
* [https://github.com/cpholguera/MASTestApp-iOS](https://github.com/cpholguera/MASTestApp-iOS) 

Simply clone the repository and follow the instructions to run the apps on your local machine. You **must use these apps to validate the demos** before submitting them to the MASTG.

#### File

Must be a modified version of the original files in the apps’ repos: 

* Android: `app/src/main/java/org/owasp/mastestapp/MastgTest.kt`
* iOS: `MASTestApp/MastgTest.swift`

When working on a new demo, you **must include the whole file** with the original name in the demo folder.

#### Summary

Must contain a summary as a comment.

Example:

```kt
// SUMMARY: This sample demonstrates various common ways of generating random numbers insecurely in Java.
```

#### Logic

The file must include code that demonstrates the addressed weakness.
The provided default `MastgTest.kt` and `MastgTest.swift` files contain some basic logic that returns a string to the UI. If possible, try to return some meaningful string.

For example, if you generate a random number, you can return it; or if you write files to external storage, you can return a list of file paths so that the app's user can read them. You can also use that string to display some meaningful errors.

#### Fail/Pass

Must contain comments indicating fail/pass and the test alias. This way, we can validate that the output is correct (e.g., the code contains three failures of `MASTG-TEST-0204`). We can easily parse and count the comments, and we can do the same in the output.

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

Static demos must work using the **reverse-engineered code**. The apps’ repositories contain scripts or instructions to obtain the reversed files.

Example: semgrep

`NO_COLOR=true semgrep -c ../../../../rules/mastg-android-insecure-random-use.yaml ./MastgTest_reversed.java --text -o output.txt`

#### Dynamic

Example: frida-trace

`frida-trace -U -f com.google.android.youtube --runtime=v8 -j '*!*certificate*/isu' > output.txt`

Example: frida

`frida -U sg.vp.owasp_mobile.omtg_android -l hook_edittext.js > output.txt`

#### Networking

Example: mitmproxy

`mitmdump -s mitm_sensitive_logger.py`
