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
- `network`: for platform-agnostic traffic analysis tests where the checks are performed purely on captured/observed traffic (often paired with `type: [network]`).

### id

The test ID.

### weakness

The MASWE weakness ID associated with this test.

- In YAML front matter, specify the bare identifier (for example, `weakness: MASWE-0069`). In body text, include the leading `@` (for example, @MASWE-0069).

### type

One or more test types.

Supported:

- `static`: analysis of the app binary, reverse-engineered source code, or developer artifacts that are available in the APK/IPA app package (e.g., Android manifest, Info.plist, entitlements, etc.). No execution of the app is required.
- `dynamic`: analysis of the app while it is running and involves runtime analysis such as hooking or method tracing.
- `manual`: manual steps that require human judgment, such as inspecting app behavior, UI, or configuration. This may include reverse engineering or runtime analysis that cannot be fully automated. Any test that includes a `**Further Validation Required:**` block MUST include `manual` in its `type` array (e.g., `[static, manual]`, `[dynamic, manual]`).
- `network`: analysis of network traffic, while the app is running. Done externally, for example, using a proxy or network capture tool.
- `filesystem`: analysis of the app's file system, including local storage or backups, which doesn't involve runtime analysis such as hooking or method tracing.
- `source-code`: tests only the developer can perform because they require access to the source code, build process, or other internal resources.

Example:

```md
type: [static]
```

Examples with multiple types:

```md
type: [dynamic, manual]
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

### Steps

A test must include at least one step. Steps can be static, dynamic, manual, or a combination of these.

Example, to check app notifications:

1. method trace for related APIs (dynamic)
2. use the app (manual)
3. reverse engineer code or use backtraces and hooks (static)
4. perform taint analysis with controlled values (dynamic)
5. grep traces or integrate "grep" in a frida script (static/dynamic)

Example:

```md
## Steps

1. Use @MASTG-TECH-0014 to look for insecure random APIs.
```

Notes:

- Always link to existing MASTG-TECH by ID (for example, @MASTG-TECH-0014)
- Don't reference MASTG tools directly (this may still be happening in some tests, and we must fix it.)
- Always start step instructions with `Use @MASTG-TECH-XXXX to ...`. Avoid `Run`, `Execute`, or parenthetical-only references such as `(@MASTG-TECH-XXXX)` as the primary action.
- Use "reverse engineer" (non-hyphenated) when referring to the process and "reverse-engineered" (hyphenated) when referring to the code.
- Be consistent by reusing the steps from existing tests. Do not create new phrasing or wording when it's not necessary.

#### Preferred TECH IDs by Platform and Test Type

Always use the **most specific** technique available. Avoid broad techniques unless no specific alternative exists.

**Android:**

| Purpose | Preferred TECH | Title | Notes |
|---|---|---|---|
| Install the app | @MASTG-TECH-0005 | Installing Android Apps | Default for Installing the app |
| Reverse Engineer | @MASTG-TECH-0013 | Reverse Engineering Android Apps | Default for Reverse Engineering. Points to @MASTG-TECH-0016, @MASTG-TECH-0017, @MASTG-TECH-0018 |
| Static analysis | @MASTG-TECH-0014 | Static Analysis on Android | Default for static steps |
| Reviewing Decompiled Java Code | @MASTG-TECH-0023 | Reviewing Decompiled Java Code on Android | Default for "Further Validation Required", do not use for test steps. |
| Decompiling Java/Kotlin | @MASTG-TECH-0017 | Decompiling Java Code | Use when specifically decompiling |
| **Avoid** | @MASTG-TECH-0016 | Disassembling Code to Smali | Use only when Smali output is explicitly needed |
| Disassembling native code | @MASTG-TECH-0018 | Disassembling Native Code | Use for native libraries |
| **Avoid** | @MASTG-TECH-0033 | Method Tracing | Prefer @MASTG-TECH-0043 unless explicit logging/monitoring of API calls is needed |
| Method hooking (dynamic) | @MASTG-TECH-0043 | Method Hooking | Preferred for instrumentation/interception/tracing |
| **Avoid** | @MASTG-TECH-0033 | Execution Tracing | Prefer @MASTG-TECH-0043 unless explicit logging/monitoring of low-level system API calls is needed |
| Network traffic monitoring | @MASTG-TECH-0010 | Basic Network Monitoring/Sniffing | |
| System log monitoring | @MASTG-TECH-0009 | Monitoring System Logs | For monitoring system/app log output |
| Extracting the AndroidManifest | @MASTG-TECH-0117 | Obtaining Information from the AndroidManifest | For extraction only |
| Analyzing the AndroidManifest | @MASTG-TECH-0x01 | Analyzing the AndroidManifest | For searching/inspecting extracted content |
| **Avoid** | @MASTG-TECH-0015 | Dynamic Analysis on Android | Too broad, don't use for tests |

**iOS:**

| Purpose | Preferred TECH | Title | Notes |
|---|---|---|---|
| Install the app | @MASTG-TECH-0056 | Installing iOS Apps | Default for Installing the app |
| Extracting the app (static) | @MASTG-TECH-0054 | Obtaining and Extracting Apps | Default step 1 for static tests |
| Reverse Engineer | @MASTG-TECH-0065 | Reverse Engineering iOS Apps | Default for Reverse Engineering. Points to @MASTG-TECH-0068, @MASTG-TECH-0069 |
| Static analysis | @MASTG-TECH-0066 | Static Analysis on iOS | Default for static steps |
| Reviewing Disassembled Objective-C and Swift Code | @MASTG-TECH-0076 | Reviewing Disassembled Objective-C and Swift Code on iOS | Default for "Further Validation Required", do not use for test steps. |
| Method hooking (dynamic) | @MASTG-TECH-0095 | Method Hooking | Preferred over 0067 for hooking/instrumentation |
| Network traffic monitoring | @MASTG-TECH-0062 | Basic Network Monitoring/Sniffing | |
| Device log monitoring | @MASTG-TECH-0060 | Monitoring System Logs | For monitoring device log output |
| **Avoid** | @MASTG-TECH-0067 | Dynamic Analysis on iOS | Too broad, don't use for tests |

#### Canonical Step Templates by Test Type

Each `type` combination has a **required step pattern**. Use these as the base and add further steps only when the test genuinely requires more detail (e.g., extra navigation steps, filtering instructions, or additional manual actions).

**`type: [static]` — Android**

```md
1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.
```

**`type: [static]` — iOS**

```md
1. Use @MASTG-TECH-0054 to extract the app.
2. Use @MASTG-TECH-0066 to look for the relevant APIs.
```

**`type: [static, dynamic]` — Android**

This is currently not allowed. Please use separate static and dynamic tests. If you think a combined test is necessary, please discuss it with the team.

**`type: [static, dynamic]` — iOS**

This is currently not allowed. Please use separate static and dynamic tests. If you think a combined test is necessary, please discuss it with the team.

**`type: [dynamic]` — Android (method hooking)**

Use when the test intercepts or modifies API call behavior at runtime.

```md
1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
```

**`type: [dynamic]` — iOS**

```md
1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
```

**`type: [network]` — Android**

```md
1. Use @MASTG-TECH-0010 to capture the app traffic.
```

**`type: [network]` — iOS**

```md
1. Use @MASTG-TECH-0062 to capture the app traffic.
```

**`type: [dynamic, filesystem]` — Android (filesystem snapshot/diff pattern)**

Use when the test identifies files created or modified by the app by comparing the device storage before and after exercising the app.

```md
1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0002 to get a baseline list of files.
3. Exercise the app.
4. Use @MASTG-TECH-0002 to retrieve the list of files again.
5. Calculate the difference between the two lists.
```

If only retrieval is needed (for example, to check the files in external storage), you can omit the baseline retrieval and the diff step.

```md
1. Use @MASTG-TECH-0005 to install the app.
2. Exercise the app.
3. Use @MASTG-TECH-0002 to retrieve the list of files in the external storage.
```

**`type: [dynamic, filesystem]` — iOS (filesystem snapshot/diff pattern)**

Use when the test identifies files created or modified by the app by comparing the device storage before and after exercising the app.

```md
1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0059 to get a baseline list of files.
3. Exercise the app.
4. Use @MASTG-TECH-0059 to retrieve the list of files again.
5. Calculate the difference between the two lists.
```

If only one retrieval is needed (for example, to check the data protection classes of files in private storage), you can omit the baseline retrieval and the diff step.

```md
1. Use @MASTG-TECH-0056 to install the app.
2. Exercise the app.
3. Use @MASTG-TECH-0059 to retrieve the list of files including their data protection classes.
```

**`type: [static]` with explicit reverse engineering — Android**

When the test explicitly requires reverse engineering the binary before applying static analysis (for example, analyzing native code or specific binary artifacts), a reverse engineering step may precede the static analysis step:

```md
1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.
```

**`type: [dynamic]` — Android (system log monitoring)**

Use when the test observes system-level log entries produced by the app or the platform (for example, StrictMode output). Use @MASTG-TECH-0009 instead of @MASTG-TECH-0043.

```md
1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0009 to monitor the system logs.
```

**`type: [dynamic]` — iOS (device log monitoring)**

Use when the test monitors device-level log output (for example, `os_log` entries). Use @MASTG-TECH-0060 instead of @MASTG-TECH-0095.

```md
1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0060 to monitor the device logs.
```

**Key rules:**

- All `[dynamic]` tests **MUST** start with an install step:
    - Android: `1. Use @MASTG-TECH-0005 to install the app.`
    - iOS: `1. Use @MASTG-TECH-0056 to install the app.`
- Use @MASTG-TECH-0043 (hooking) for intercepting/modifying API behavior; use @MASTG-TECH-0033 (tracing) only for passive observation and logging of API calls.
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
