## Rules

SAST rules live in the `rules/` folder. They are referenced and reused by demos and should be stable and consistently named.

### Semgrep rules

[https://semgrep.dev/docs/getting-started/quickstart/](https://semgrep.dev/docs/getting-started/quickstart/)
[https://semgrep.dev/learn](https://semgrep.dev/learn)
[https://academy.semgrep.dev/courses/secure-guardrails](https://academy.semgrep.dev/courses/secure-guardrails)

Tip: use [https://semgrep.dev/playground/new](https://semgrep.dev/playground/new) for experimentation.

File naming and format

- Name files using the pattern: `mastg-<platform>-<short-topic>.(yml|yaml)` (for example, `mastg-android-non-random-use.yml`).
- Use either `.yml` or `.yaml` consistently per file; both are accepted.
- A file may contain one or more rules.

Semgrep rules must follow valid syntax: [https://semgrep.dev/docs/writing-rules/rule-syntax/](https://semgrep.dev/docs/writing-rules/rule-syntax/)

Required fields per rule:

- **id**: unique, stable identifier. For single-rule files, prefer matching the filename without extension. For multi-rule files, use a common prefix based on the filename and a descriptive suffix (for example, `mastg-android-data-unencrypted-shared-storage-no-user-interaction-mediastore`).
- **severity**:
    - INFO
    - WARNING
    - ERROR
- **languages**: usually `xml` or `java` (we don’t create rules for Kotlin as we work with decompiled Java; use `xml` for AndroidManifest and resource rules).
- **metadata**: must include summary
    - summary: Short description of the rule.
    - original_source: You may use rules from sources on the internet. Be sure to check that the license allows this, and always link to the source here. Modify the rule as needed, provided the license permits it.
- **message**: must start with a MASVS identifier and concisely explain what the rule is reporting. Prefer a specific control when applicable (for example, `[MASVS-PLATFORM-2]`); otherwise, use the MASVS category tag (for example, `[MASVS-STORAGE]`).
- **patterns**: see [https://semgrep.dev/docs/writing-rules/pattern-syntax/](https://semgrep.dev/docs/writing-rules/pattern-syntax/)

Multiple rules per file

- When grouping related detections, you may define multiple rules in a single file. Ensure each rule’s `id` shares a sensible prefix and is unique.
- Keep messages and severities aligned within the group; use `INFO` for inventory-style detections and `WARNING`/`ERROR` for clear weaknesses.

General guidance

- Do not include authors in the semgrep rules. If it was copied from another source, **include the link to the source**. Since many people will contribute, authorship is tracked via git history.
- Keep messages concise and actionable; they should be understandable without reading the pattern body.
- Test rules in the Semgrep Playground and against reversed code from the demos.

Example (single rule):

```yml
rules:
  - id: mastg-android-insecure-random-use
    severity: WARNING
    languages:
      - java
    metadata:
      summary: This rule looks for common patterns, including classes and methods.
    message: "[MASVS-CRYPTO-1] The application makes use of an insecure random number generator."

    pattern-either:
        - patterns:
            - pattern-inside: $M(...){ ... }
            - pattern-either:
                - pattern: Math.random(...)
                - pattern: (java.util.Random $X).$Y(...)
```

Example (multiple rules in one file):

```yml
rules:
  - id: mastg-android-data-unencrypted-shared-storage-no-user-interaction-external-api-public
    severity: WARNING
    languages: [java]
    metadata:
      summary: Methods returning locations on shared external storage.
    message: "[MASVS-STORAGE] Make sure to encrypt files at these locations if necessary"
    pattern-either:
      - pattern: $X.getExternalStorageDirectory(...)
      - pattern: $X.getExternalStoragePublicDirectory(...)

  - id: mastg-android-data-unencrypted-shared-storage-no-user-interaction-mediastore
    severity: WARNING
    languages: [java]
    metadata:
      summary: MediaStore API writes to external storage.
    message: "[MASVS-STORAGE] Data written via MediaStore can be readable by other apps on older Android versions"
    pattern-either:
      - pattern: import android.provider.MediaStore
      - pattern: $X.MediaStore
```