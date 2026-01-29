## Tests

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

- [MASTG-TEST-0207](https://mas.owasp.org/MASTG/tests-beta/android/MASVS-STORAGE/MASTG-TEST-0207/)
- [MASTG-TEST-0216](https://mas.owasp.org/MASTG/tests-beta/android/MASVS-STORAGE/MASTG-TEST-0216/)
- [MASTG-TEST-0263](https://mas.owasp.org/MASTG/tests-beta/android/MASVS-STORAGE/MASTG-TEST-0263/)

Notes:

- Tests with `platform: network` are still organized under the OS folder that the MASVS category belongs to (for example, Android network tests live under `tests-beta/android/MASVS-NETWORK/`).
- Old tests under `tests/` do not follow these new guidelines. We are currently working to deprecate all of them in favor of these new approach. 

Each test has two parts: the [Markdown metadata](#markdown-metadata) (YAML `front matter`) and the [Markdown body](#markdown-body).

### Markdown: Metadata

#### title

Test titles should be concise and clearly state the purpose of the test.

In some cases, the test name and the weakness may have the same title, but typically, tests cover different aspects of a weakness. Titles should reflect that.

Avoid including Android or iOS unless necessary, as in "Insecure use of the Android Protected Confirmation API".

Follow a consistent style across all test titles.

**Conventions**

- Static: "References to…" (semgrep/r2)
- Dynamic: "Runtime Use …" (frida/frooky)

Exceptions may apply where "Runtime ..." feels forced, for example, tests using adb, local backups, or filesystem snapshots.

#### platform

The mobile platform. One of the following:

- `android`
- `ios`
- `network`: for platform-agnostic traffic analysis tests where the checks are performed purely on captured/observed traffic (often paired with `type: [network]`).

#### id

The test ID.

#### weakness

The MASWE weakness ID associated with this test.

- In YAML front matter, specify the bare identifier (for example, `weakness: MASWE-0069`). In body text, include the leading `@` (for example, @MASWE-0069).

#### type

One or more test types.

Supported:

- `static`: analysis of the app binary, reverse-engineered source code, or developer artifacts that are available in the APK/IPA app package (e.g., Android manifest, Info.plist, entitlements, etc.). No execution of the app is required.
- `dynamic`: analysis of the app while it is running and involves runtime analysis such as hooking or method tracing.
- `manual`: manual steps that require human judgment, such as inspecting app behavior, UI, or configuration. This may include reverse engineering or runtime analysis that cannot be fully automated.
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

#### best-practices

Reference platform-specific mitigations or best practices. Automation generates a "Mitigations" section.

Reference the related `best-practices/` pages for background using their ID. Create the pages if they don't exist yet.

Example:

```md
best-practices: [MASTG-BEST-0001]
```

#### prerequisites

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

#### profiles

Specify the MAS profiles to which the test applies. Valid values: L1, L2, P, R.
The profiles are described in [MAS Testing Profiles Guide](Document/0x03b-Testing-Profiles.md)

- L1 denotes Essential Security.
- L2 denotes Advanced Security.
- P denotes Privacy.
- R denotes Resilience.

Example:

```md
profiles: [L1, L2, P]
```

#### knowledge

Reference the related `knowledge/` pages for background using their ID. Create the pages if they don't exist yet.

Example:

```md
knowledge: [MASTG-KNOW-0013]
```

#### optional fields

Include these if relevant:

- `status:` draft, placeholder, deprecated
- `note:` short free-form note
- `available_since:` minimum platform/API level
- `deprecated_since:` last applicable platform/API level
- `apis:` list of relevant APIs

Notes:

- For Android, available/deprecated API levels are integers (for example, `deprecated_since: 24`). For iOS, use the iOS release version (for example, `available_since: 13`).

### Markdown: Body

#### Overview

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

#### Steps

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

1. Run @MASTG-TECH-0014 on the app and look for insecure random APIs.
```

Notes:

- Always link to existing MASTG-TECH by ID (for example, @MASTG-TECH-0014)
- Don't reference MASTG tools directly (this may still be happening in some tests, and we must fix it.)
- Be consistent by reusing the steps from existing tests. Do not create new phrasing or wording when it's not necessary.

#### Observation

The output you get after executing all steps. It serves as evidence.

It MUST start with "The output should contain ...".

Example:

```md
## Observation

The output should contain a list of locations where insecure random APIs are used.
```

#### Evaluation

Using the observation as input, describe how to evaluate it. State explicitly what makes the test fail.

It MUST start with "The test case fails if ...".

Example:

```md
## Evaluation

The test case fails if you can find random numbers generated using those APIs that are used in security-relevant contexts.
```

IMPORTANT: Do not include remediation advice or best practices in the evaluation section. Remediation belongs in `best-practices/` and must be linked in the test metadata `best-practices`. If it does not exist yet, create it.