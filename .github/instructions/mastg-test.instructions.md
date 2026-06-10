---
name: 'Writing MASTG Test Files'
applyTo: 'tests-beta/**/*.md'
---

A MASWE weakness can have one or more platform-specific tests associated with it.

Tests have an [overview](#overview) and contain [Steps](#steps) which produce outputs called [observations](#observation) which must be [evaluated](#evaluation).

Tests must be located under `tests-beta/android/` or `tests-beta/ios/`, within the corresponding MASVS category. Their file names are the test IDs.

Example structure:

```sh
% ls -1 -F tests-beta/android/MASVS-CRYPTO/
MASTG-TEST-0204.md
MASTG-TEST-0205.md
```

Example tests for reference:

- [MASTG-TEST-0207](https://mas.owasp.org/MASTG/tests/android/MASVS-STORAGE/MASTG-TEST-0207/)
- [MASTG-TEST-0216](https://mas.owasp.org/MASTG/tests/android/MASVS-STORAGE/MASTG-TEST-0216/)
- [MASTG-TEST-0263](https://mas.owasp.org/MASTG/tests/android/MASVS-RESILIENCE/MASTG-TEST-0263/)

Notes:

- Tests with `platform: network` are still organized under the OS folder that the MASVS category belongs to (for example, Android network tests live under `tests-beta/android/MASVS-NETWORK/`).
- Old tests under `tests/` do not follow these new guidelines. We are currently working to deprecate all of them in favor of these new approach.

Each test has two parts: the [Markdown metadata](#markdown-metadata) (YAML `front matter`) and the [Markdown body](#markdown-body).

## Creating Test IDs

When creating a new test (whether porting from v1 or writing from scratch), use a **fake ID** with the notation `MASTG-TEST-0x##` (for example, `MASTG-TEST-0x33`). This prevents conflicts between parallel pull requests. Create new fake IDs incrementally (e.g., `MASTG-TEST-0x33`, `MASTG-TEST-0x34`, `MASTG-TEST-0x35`) as you add new content.

Once your pull request is reviewed and ready to merge, the team will assign real IDs (for example, `MASTG-TEST-0233`) before the content is published.

## Markdown: Metadata

### title

Test titles should be concise and clearly state the purpose of the test.

In some cases, the test name and the weakness may have the same title, but typically, tests cover different aspects of a weakness. Titles should reflect that.

Avoid including Android or iOS unless necessary, as in "Insecure use of the Android Protected Confirmation API".

Follow a consistent style across all test titles.

#### Conventions

- Static: "References to…" (semgrep/r2)
- Dynamic: "Runtime Use …" (frida/frooky)

Exceptions may apply where "Runtime ..." feels forced, for example, tests using adb, local backups, or filesystem snapshots.

### platform

The mobile platform. One of the following:

- `android`
- `ios`
- `network`: for platform-agnostic traffic analysis tests where the checks are performed purely on captured/observed traffic (often paired with `type: [dynamic, network]`).

### id

The test ID.

### weakness

The MASWE weakness ID associated with this test.

- In YAML front matter, specify the bare identifier (for example, `weakness: MASWE-0069`). In body text, include the leading `@` (for example, @MASWE-0069).

### type

One or more test types.

Supported:

- `static`: base type for analysis that does not require app execution. Always combined with `code`, `config`, or `package` to indicate the specific kind of static analysis performed.
- `code`: code-level analysis via decompilation or disassembly of the app binary, reverse-engineered source code, or developer artifacts. Always combined with `static` (e.g., `type: [static, code]`).
- `config`: static analysis of configuration files within the app package (e.g., AndroidManifest, Network Security Configuration, Info.plist, App Transport Security settings). Always combined with `static` (e.g., `type: [static, config]`).
- `package`: static inspection of specific binary files or resources within the app package (e.g., native libraries, XML resource files). Always combined with `static` (e.g., `type: [static, package]`).
- `dynamic`: base type for analysis that requires the app to be running. Always combined with `hooks`, `logs`, `filesystem`, or `network` to indicate the specific kind of runtime analysis performed.
- `hooks`: runtime method interception or instrumentation at the API level (for example, via Frida). Always combined with `dynamic` (e.g., `type: [dynamic, hooks]`).
- `logs`: observation of platform-level log output produced by the app or the system (for example, via adb logcat or `os_log`). Always combined with `dynamic` (e.g., `type: [dynamic, logs]`).
- `manual`: manual steps that require human judgment, such as inspecting app behavior, UI, or configuration. This may include reverse engineering or runtime analysis that cannot be fully automated. Any test that includes a `**Further Validation Required:**` block MUST include `manual` in its `type` array (e.g., `[static, manual]`, `[dynamic, manual]`).
- `network`: analysis of network traffic captured from the running app, for example via a proxy or network capture tool. Always combined with `dynamic` (e.g., `type: [dynamic, network]`).
- `filesystem`: analysis of the app's file system, including local storage or backups. Always combined with `dynamic` (e.g., `type: [dynamic, filesystem]`).
- `developer`: tests only the developer can perform because they require access to the source code, build process, or other internal resources.

Example:

```md
type: [static, code]
```

Examples with multiple types:

```md
type: [dynamic, hooks, manual]
```

### best-practices

Reference platform-specific mitigations or best practices. Automation generates a "Mitigations" section.

Reference the related `best-practices/` pages for background using their ID. Create the pages if they don't exist yet.

Example:

```md
best-practices: [MASTG-BEST-0001]
```

### prerequisites

List the conditions that must be known or available before running or evaluating the test. These items capture internal context that only the developer or the organization can provide. Existing files are in the `prerequisites/` folder. Create new ones when needed.

Common examples include:

- Defined categories of sensitive data and their sensitivity levels used within the app.
- A list of first party packages, libraries, and modules.
- A list of first party network domains and services the app is expected to contact.

If there are no prerequisites, you can omit this field or use an empty list.

Example:

```md
prerequisites:
- identify-sensitive-data
- identify-security-relevant-contexts
```

### profiles

Specify the MAS profiles to which the test applies. Valid values: L1, L2, P, R.
The profiles are described in [MAS Testing Profiles Guide](../../Document/0x03b-Testing-Profiles.md)

- L1 denotes Essential Security.
- L2 denotes Advanced Security.
- P denotes Privacy.
- R denotes Resilience.

Example:

```md
profiles: [L1, L2, P]
```

### knowledge

Must always reference related `knowledge/` pages for background using their ID. Create the pages if they don't exist yet.

Example:

```md
knowledge: [MASTG-KNOW-0013]
```

### optional fields

Include these if relevant:

- `status:` draft, placeholder, deprecated
- `note:` short free-form note
- `available_since:` minimum platform/API level (e.g. 13 in Android or 2.0 in iOS)
- `deprecated_since:` last applicable platform/API level (e.g. 24 in Android or 12.0 in iOS)
- `apis:` list of relevant APIs

Notes:

- For Android, available/deprecated API levels are integers (for example, `deprecated_since: 24`). For iOS, use the iOS release version (for example, `available_since: 13`).

## Markdown: Body

### Overview

The overview is platform-specific and extends the weakness overview with details on the area tested (the Knowledge items from the `knowledge` in the metadata).

Very important: the overview must be phrased like an issue.

- Describe the relevant platform feature/API from the perspective of "what can go wrong" (risk, failure mode, exposure).
- Make it clear why the test exists: what the tester is trying to detect and why that matters.

Do not repeat the weakness description here. Focus on the specific issue the test is checking for on the given platform.

Good patterns for issue framing:

- "If the app uses/implements/configures X, Y can happen …"
- "This can lead to … (exposure, bypass, integrity failure, privacy leak) …"
- "This test checks/verifies whether the app …"

Do not write the overview like a neutral platform description. Neutral/descriptive explanations belong in `knowledge/`.

Example:

```md
## Overview

Android apps sometimes use insecure pseudorandom number generators (PRNGs) such as `java.util.Random`, which is essentially a linear congruential generator. This type of PRNG generates a predictable sequence of numbers for any given seed value, making the sequence reproducible and insecure for cryptographic use. In particular, `java.util.Random` and `Math.random()` ([the latter](https://franklinta.com/2014/08/31/predicting-the-next-math-random-in-java/) simply calling `nextDouble()` on a static `java.util.Random` instance) produce identical number sequences when initialized with the same seed across all Java implementations.
```

#### Example Attack Scenario

The Overview MUST include an `**Example Attack Scenario:**` block. It helps the reader understand how an attacker would actually abuse the issue and what the real-world consequence is. Place it at the end of the Overview, immediately before the `## Steps` section.

Write it as a short setup sentence followed by a numbered list of concrete attacker steps that ends with the impact (for example, data exposure, authentication bypass, account takeover, or code execution). Refer to relevant techniques with their `@MASTG-TECH-XXXX` IDs where helpful.

See @MASTG-TEST-0250 and @MASTG-TEST-0252 for reference.

Example:

```md
**Example Attack Scenario:**

Suppose a banking app protects its account screen behind a login activity but also declares an account-details activity that is exported.

1. An attacker reverse engineers the app and finds the exported activity.
2. The attacker writes a malicious app that starts it directly by its component name.
3. The account screen opens without going through the login activity.
4. The victim's account data is exposed, bypassing authentication entirely.
```

### Steps

A test must include at least one step. Steps can be static, dynamic, manual, or, in very specific cases, a combination of these.

- Don't reference MASTG tools directly. Always link to existing MASTG-TECH by ID (for example, @MASTG-TECH-0014)
- When a MASTG-TECH exists, always start step instructions with `Use @MASTG-TECH-XXXX to ...`. Avoid `Run`, `Execute`, or parenthetical-only references such as `(@MASTG-TECH-XXXX)` as the primary action.
- Use "reverse engineer" (non-hyphenated) when referring to the process (e.g. "to reverse engineer an app") and "reverse-engineered" (hyphenated) when referring to the code (e.g. "reverse-engineered code").
- Be consistent by preferring to use canonical steps from the sections below. Do not create new phrasing or wording when it's not necessary.

Example:

```md
## Steps

1. Use @MASTG-TECH-0014 to look for the relevant APIs.
```

#### Preferred TECH IDs by Platform and Test Type

Always use the **most specific** technique available. Avoid broad techniques unless no specific alternative exists.

**Android:**

| Purpose | Preferred TECH | Title | Notes |
|---|---|---|---|
| Install the app | @MASTG-TECH-0005 | Installing Apps | Step 1 for all tests with `dynamic` in their type on Android |
| Reverse engineer the app | @MASTG-TECH-0013 | Reverse Engineering Android Apps | Points to @MASTG-TECH-0016, @MASTG-TECH-0017, @MASTG-TECH-0018 |
| Static code analysis | @MASTG-TECH-0014 | Static Analysis on Android | Step 2 in the standard Android `[static, code]` template |
| Manual code inspection | @MASTG-TECH-0023 | Reviewing Decompiled Java Code | Use in `**Further Validation Required:**` blocks only; do not use for test steps |
| Decompile Java code | @MASTG-TECH-0017 | Decompiling Java Code | Use when specifically decompiling; prefer @MASTG-TECH-0014 for general static analysis |
| **Avoid** | @MASTG-TECH-0016 | Disassembling Code to Smali | Use only when Smali output is explicitly needed |
| Disassemble native code | @MASTG-TECH-0018 | Disassembling Native Code | Use for native libraries |
| **Avoid** | @MASTG-TECH-0033 | Method Tracing | Prefer @MASTG-TECH-0043; use 0033 only for passive logging or monitoring of API calls or low-level system calls |
| Method hooking | @MASTG-TECH-0043 | Method Hooking | Preferred for instrumentation, interception, and tracing |
| Monitor network traffic | @MASTG-TECH-0010 | Basic Network Monitoring/Sniffing | Step 1 for all `[dynamic, network]` tests on Android |
| Monitor system logs | @MASTG-TECH-0009 | Monitoring System Logs | Use instead of @MASTG-TECH-0043 when observing platform-level log output |
| Extract the AndroidManifest | @MASTG-TECH-0117 | Obtaining Information from the AndroidManifest | Precedes @MASTG-TECH-0150 in AndroidManifest analysis tests |
| Analyze the AndroidManifest | @MASTG-TECH-0150 | Analyzing the AndroidManifest | Precedes @MASTG-TECH-0151 when the NSC is also inspected |
| Analyze the Network Security Configuration | @MASTG-TECH-0151 | Analyzing the Network Security Configuration | Requires the NSC reference found via @MASTG-TECH-0150 |
| **Avoid** | @MASTG-TECH-0015 | Dynamic Analysis on Android | Too broad; don't use for tests |
| Search for strings | @MASTG-TECH-0019 | Retrieving Strings |  |
| Explore the app package | @MASTG-TECH-0007 | Exploring the App Package | Use to extract specific files from the APK (e.g., XML resource files) |

**iOS:**

| Purpose | Preferred TECH | Title | Notes |
|---|---|---|---|
| Install the app | @MASTG-TECH-0056 | Installing Apps | Step 1 for all tests with `dynamic` in their type on iOS |
| Extract the app | @MASTG-TECH-0054 | Obtaining and Extracting Apps | Too broad; don't use for tests |
| Reverse engineer the app | @MASTG-TECH-0065 | Reverse Engineering iOS Apps | Points to @MASTG-TECH-0068, @MASTG-TECH-0069 |
| Static code analysis | @MASTG-TECH-0066 | Static Analysis on iOS | Step 2 in the standard iOS `[static, code]` template |
| Manual code inspection | @MASTG-TECH-0076 | Reviewing Disassembled Objective-C and Swift Code | Use in `**Further Validation Required:**` blocks only; do not use for test steps |
| Method hooking | @MASTG-TECH-0095 | Method Hooking | Preferred over @MASTG-TECH-0067 for hooking and instrumentation |
| Monitor network traffic | @MASTG-TECH-0062 | Basic Network Monitoring/Sniffing | Step 1 for all `[dynamic, network]` tests on iOS |
| Monitor device logs | @MASTG-TECH-0060 | Monitoring System Logs | Use instead of @MASTG-TECH-0095 when observing platform-level log output |
| **Avoid** | @MASTG-TECH-0067 | Dynamic Analysis on iOS | Too broad; don't use for tests |
| Search for strings | @MASTG-TECH-0071 | Retrieving Strings |  |
| Explore the app package | @MASTG-TECH-0058 | Exploring the App Package | Step 1 for all `[static, ...]` tests on iOS and used to extract specific files from the app package |
| Retrieve the Info.plist | @MASTG-TECH-0153 | Retrieving Info.plist Files | Precedes @MASTG-TECH-0154 and @MASTG-TECH-0155 in Info.plist analysis tests |
| Analyze Info.plist settings | @MASTG-TECH-0154 | Analyzing Info.plist Files | Use after @MASTG-TECH-0153 for general plist key inspection |
| Analyze the ATS configuration | @MASTG-TECH-0155 | Analyzing the ATS Configuration | Use after @MASTG-TECH-0153 for ATS-specific analysis |

#### Canonical Step Templates by Test Type

The combination of `type` and `platform` uniquely identifies the canonical template a test must follow:

| type | platform | canonical template |
|---|---|---|
| `[static, code]` | android, ios | Static Analysis - Code Inspection |
| `[static, config]` | android, ios | Static Analysis - Configuration and Manifest Inspection |
| `[static, package]` | android, ios | Static Analysis - App Package Content Inspection |
| `[static, developer]` | android, ios | Static Analysis - Developer Artifacts |
| `[dynamic, hooks]` | android, ios | Dynamic Analysis - Hooking |
| `[dynamic, logs]` | android, ios | Dynamic Analysis - Log Monitoring |
| `[dynamic, filesystem]` | android, ios | Dynamic Analysis - Filesystem |
| `[dynamic, network]` | android, ios | Dynamic Analysis - Network Monitoring |

Each combination has a **required step pattern**. Use these as the base and add further steps only when the test genuinely requires more detail (e.g., extra navigation steps, filtering instructions, or additional manual actions).

##### Static Analysis - Code Inspection

Use when the test analyzes the app's code via decompilation or disassembly to detect insecure API usage or implementation issues.

**`type: [static, code]` — Android**

```md
1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.
```

**`type: [static, code]` — iOS**

```md
1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.
```

##### Static Analysis - Configuration and Manifest Inspection

Use when the test inspects configuration files or manifest declarations in the app package (for example, the AndroidManifest, Network Security Configuration, or Info.plist).

**`type: [static, config]` — Android (AndroidManifest analysis)**

When the test inspects specific attributes in the AndroidManifest (for example, `minSdkVersion`, `android:debuggable`, or `android:networkSecurityConfig`):

```md
1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0150 to [read/check the relevant attribute].
```

**`type: [static, config]` — Android (Network Security Configuration analysis)**

When the test inspects elements in the Network Security Configuration file (for example, trust anchors, certificate pins, or cleartext traffic settings):

```md
1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0150 to check if `android:networkSecurityConfig` is present.
4. Use @MASTG-TECH-0151 to [extract the relevant elements].
```

**`type: [static, config]` — iOS (Info.plist analysis)**

When the test inspects security-relevant settings in the `Info.plist` file (for example, permissions, entitlements, or general configuration keys):

```md
1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0153 to retrieve the `Info.plist` file.
3. Use @MASTG-TECH-0154 to inspect the relevant settings.
```

**`type: [static, config]` — iOS (ATS configuration analysis)**

When the test inspects App Transport Security (ATS) settings under `NSAppTransportSecurity` in the `Info.plist` file:

```md
1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0153 to retrieve the `Info.plist` file.
3. Use @MASTG-TECH-0155 to analyze the ATS configuration.
```

##### Static Analysis - App Package Content Inspection

Use when the test inspects specific files within the app package (for example, XML resource files, or other non-code assets) without requiring full code analysis.

**`type: [static, package]` — Android**

```md
1. Use @MASTG-TECH-0007 to extract [the relevant files] from the app package.
```

**`type: [static, package]` — iOS**

```md
1. Use @MASTG-TECH-0058 to extract [the relevant files] from the app package.
```

##### Static Analysis - Native Libraries

Use when the test requires analysis of native libraries included in the app package (for example, checking for the presence of security features in native code).

**`type: [static, package]` — Android**

```md
1. Use @MASTG-TECH-0157 to extract the native libraries from the app package.
2. Use @MASTG-TECH-0115 on each native library to obtain the compiler-provided security features.
```

**`type: [static, package]` — iOS**

```md
1. Use @MASTG-TECH-0082 to identify all bundled libraries.
2. Use @MASTG-TECH-0118 on the main binary and each shared library to obtain all relevant artifacts related to the compiler-provided security features.
```

##### Static Analysis - Developer Artifacts

Use when the test requires access to the source code, build system, or other developer resources not available in the distributed app package (for example, generating an SBOM from the build).

**`type: [static, developer]` — Android**

```md
1. Use @MASTG-TECH-0130 to generate a SBOM, or request one in CycloneDX format from the development team.
```

**`type: [static, developer]` — iOS**

```md
1. Use @MASTG-TECH-0132 to generate a SBOM, or request one in CycloneDX format from the development team.
```

##### Static + Dynamic Analysis

**`type: [static, dynamic]` — Android, iOS**

This is currently not allowed (with a few exceptions). Please use separate static and dynamic tests. If you think a combined test is necessary, please discuss it with the team.

##### Dynamic Analysis - Hooking

Use when the test hooks, intercepts or modifies API call behavior at runtime.

**`type: [dynamic, hooks]` — Android**

```md
1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
```

**`type: [dynamic, hooks]` — iOS**

```md
1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
```

##### Dynamic Analysis - Network Monitoring

Use when the test captures and inspects network traffic generated by the running app to detect insecure communication patterns.

**`type: [dynamic, network]` — Android**

```md
1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0010 to capture the app traffic.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
```

**`type: [dynamic, network]` — iOS**

```md
1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0062 to capture the app traffic.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
```

##### Dynamic Analysis - Filesystem

Use when the test inspects files created, modified, or stored by the running app on the device file system.

**`type: [dynamic, filesystem]` — Android (filesystem snapshot/diff pattern)**

Use when the test identifies files created or modified by the app by comparing the device storage before and after exercising the app.

```md
1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0002 to get a baseline list of files.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
4. Use @MASTG-TECH-0002 to retrieve the list of files again.
5. Calculate the difference between the two lists.
```

If only retrieval is needed (for example, to check the files in external storage), you can omit the baseline retrieval and the diff step.

```md
1. Use @MASTG-TECH-0005 to install the app.
2. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
3. Use @MASTG-TECH-0002 to retrieve the list of files in the external storage.
```

**`type: [dynamic, filesystem]` — iOS (filesystem snapshot/diff pattern)**

Use when the test identifies files created or modified by the app by comparing the device storage before and after exercising the app.

```md
1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0059 to get a baseline list of files.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
4. Use @MASTG-TECH-0059 to retrieve the list of files again.
5. Calculate the difference between the two lists.
```

If only one retrieval is needed (for example, to check the data protection classes of files in private storage), you can omit the baseline retrieval and the diff step.

```md
1. Use @MASTG-TECH-0056 to install the app.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
3. Use @MASTG-TECH-0059 to retrieve the list of files including their data protection classes.
```

##### Dynamic Analysis - Log Monitoring

Use when the test monitors log output generated by the running app or the platform to detect sensitive data leakage or unexpected behavior.

**`type: [dynamic, logs]` — Android**

Use when the test observes system-level log entries produced by the app or the platform (for example, StrictMode output).

```md
1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0009 to monitor the system logs.
```

**`type: [dynamic, logs]` — iOS**

Use when the test monitors device-level log output (for example, `os_log` entries).

```md
1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0060 to monitor the device logs.
```

**Key rules:**

- All tests with `dynamic` in their `type` array **MUST** start with an install step:
    - Android: `1. Use @MASTG-TECH-0005 to install the app.`
    - iOS: `1. Use @MASTG-TECH-0056 to install the app.`
- For `[dynamic, hooks]` tests, use @MASTG-TECH-0043/@MASTG-TECH-0095 for intercepting or modifying API behavior; use @MASTG-TECH-0033 only for passive observation and logging of API calls.
- Step descriptions are intentionally vague e.g. `to look for uses of the relevant APIs`. This is to allow for ease reuse across tests and easy refactoring whenever needed in the future. The specific APIs to look for are determined by the test's metadata `apis` field (even if it's currently optional) and the test overview, and should be clear to the tester without needing to be explicitly stated in the step instructions.

### Observation

The output you get after executing all steps. It serves as evidence.

It MUST start with "The output should contain ...".

Example:

```md
## Observation

The output should contain a list of locations where insecure random APIs are used.
```

### Evaluation

Using the observation as input, describe how to evaluate it. State explicitly what makes the test fail.

It MUST start with "The test case fails if ...".

Example:

```md
## Evaluation

The test case fails if you can find random numbers generated using those APIs that are used in security-relevant contexts.
```

An explanation of the conditions that make the test pass must not be added. It is always assumed that the test fails for certain conditions and passes otherwise, making the pass explanation redundant.

A pass explanation can only be added for rare edge cases where it is unavoidable due to conditions particular to that case.

In that case, it MUST start with "The test case passes if ..." and must be added after the fail explanation.

IMPORTANT: Do not include remediation advice or best practices in the evaluation section. Remediation belongs in `best-practices/` and must be linked in the test metadata `best-practices`. If it does not exist yet, create it.

#### Further Validation Required

When automated tools alone can't confirm whether the fail condition is met, add a `**Further Validation Required:**` block immediately after the fail condition sentence. Use it in exactly two situations:

1. **Security-relevance is context-dependent**: the tool finds uses of an API but can't determine whether the usage is security-relevant (for example, insecure random APIs may be used for non-security purposes such as UI animations or game mechanics).
2. **Implementation correctness requires code inspection**: the tool finds a code pattern (for example, a custom `TrustManager`) but can't confirm whether the implementation is actually incorrect — a human must read the code to determine which failure case applies.

Do NOT add this block when the fail condition is self-evident from the observation alone (for example, a known-dangerous flag is set, or a specific insecure API is called with a hardcoded bad argument).

The block MUST appear immediately after the "The test case fails if ..." sentence (and its optional case list). Choose the phrasing based on the test type and what the observation provides:

**`type: [static, manual]` — code location inspection:**

When the observation contains code locations from static analysis tools:

```md
The test case fails if [condition].

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-XXXX to determine whether [reason]:

- Determine whether ...
```

```md
The test case fails if [condition].

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-XXXX, looking for cases such as:

- **Case name:** ...
```

Use the first phrasing when confirming security-relevance; use the second when confirming an incorrect implementation (for example, "looking for cases such as: trust manager that does nothing").

**`type: [dynamic, manual]` — hook output with backtraces (code inspection):**

When the observation comes from runtime hooks (@MASTG-TECH-0043 or @MASTG-TECH-0095) and includes backtraces:

```md
The test case fails if [condition].

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-XXXX to determine whether [reason]:

- Determine whether ...
```

Or, when listing multiple things to inspect without a single inline reason:

```md
The test case fails if [condition].

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-XXXX:

- Determine whether ...
- Determine whether ...
```

**`type: [dynamic, filesystem, manual]` — filesystem output (file content inspection):**

When the observation is a list of files (from a filesystem snapshot diff or file hooks), not code locations:

```md
The test case fails if [condition].

**Further Validation Required:**

Inspect the content of each reported file to determine whether the data is sensitive:

- Determine whether the file contains sensitive information (e.g., personal data, credentials, or tokens).
- Determine whether the data is stored without encryption.
```

**`type: [dynamic, manual]` — visual output (screenshot inspection):**

When the observation is a collection of screenshots:

```md
The test case fails if [condition].

**Further Validation Required:**

Inspect each screenshot visually, looking for sensitive information such as passwords, tokens, personally identifiable information, or other sensitive content that should not be exposed.
```

**Requirement:** Any test with a `**Further Validation Required:**` block MUST include `manual` in its `type` array, in addition to its other types (for example, `type: [static, manual]`).

#### Expected False Negatives

When a test has known technical limitations that may cause it to miss certain findings, add an `**Expected False Negatives:**` block at the very end of the Evaluation section, after any `**Further Validation Required:**` block and any admonition notes.

Use this block to explain:

- Which specific implementation patterns or technical conditions may prevent the test from detecting the issue (for example, custom UI frameworks, native code, obfuscation, or dynamic loading).
- What additional steps a tester may take to compensate (for example, manual reverse engineering or custom instrumentation).

Do NOT use this block for general test coverage limitations. Reserve it for specific, identifiable technical conditions that are known to cause false negatives for this particular test.

Example:

```md
**Expected False Negatives:**

This test may produce false negatives if the app uses custom text input controls that do not rely on standard UIKit classes such as `UITextField` or `UITextView` (for example, in custom UI frameworks or game engines), or if text entry is handled through nonstandard abstractions that prevent reliable observation of input traits at runtime.
```

Example:

```md
**Expected False Negatives:**

This test may produce false negatives if the app uses root detection techniques that are not covered by the hooks or traces used in this test, or if the root detection logic is implemented in a way that evades detection (for example, through obfuscation, dynamic code loading, or anti-instrumentation techniques). In such cases, the absence of findings does not guarantee the absence of root detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze root detection mechanisms.
```
