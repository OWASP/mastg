## Knowledge Articles

Authoring standards for the Knowledge articles under `knowledge/`. Knowledge pages explain Android or iOS features and concepts that tests and techniques rely on. They are descriptive, not prescriptive: do not include security recommendations here. Best practices belong in `best-practices/`.

Locations and taxonomy:

- `knowledge/android/MASVS-<CATEGORY>/MASTG-KNOW-####.md`
- `knowledge/ios/MASVS-<CATEGORY>/MASTG-KNOW-####.md`
- `knowledge/index.md` is the catalog landing page.

`<CATEGORY>` is one of: `AUTH`, `CODE`, `CRYPTO`, `NETWORK`, `PLATFORM`, `PRIVACY`, `RESILIENCE`, `STORAGE`.

File naming and IDs:

- The filename defines the knowledge ID: `MASTG-KNOW-\d{4}.md`.
- Do not add an `id:` field to the YAML front matter.
- Place the file in the platform (`android` or `ios`) and MASVS category folder that best matches the subject.

Follow the global Markdown rules (see `.github/instructions/markdown.instructions.md`). Use `##` and `###` headings in the body.

### Markdown: Metadata

Include a YAML front matter block with these fields.

Required:

- `masvs_category:` One of: `MASVS-AUTH`, `MASVS-CODE`, `MASVS-CRYPTO`, `MASVS-NETWORK`, `MASVS-PLATFORM`, `MASVS-PRIVACY`, `MASVS-RESILIENCE`, `MASVS-STORAGE`.
- `platform:` One of: `android`, `ios`.
- `title:` Clear and specific concept title (for example, "Android KeyStore").

Optional:

- `best-practices:` Reference platform-specific mitigations or best practices from `best-practices/`, when relevant.
- `status:` `placeholder`, or `deprecated`.
- `available_since:` Minimum platform/API level (Android: integer API level; iOS: release version, for example `13`).
- `deprecated_since:` Last applicable platform/API level.

Examples:

```yaml
---
masvs_category: MASVS-STORAGE
platform: android
title: Android KeyStore
available_since: 18
---
```

### Markdown: Body

Keep content authoritative, concise, and platform-focused. Avoid duplicating OS documentation. Instead, cite it and summarize the feature, including its purpose and functionality.

Considerations for writing the content:

- Define the concept and its scope. Relate to OS components, APIs, or security model elements.
- Explain typical security-relevant behaviors or implications of the feature without necessarily recommending mitigations (that's for "best practices").
- Include specific API names, version nuances, storage locations, and configuration knobs. Try to avoid code samples. Instead, refer to the existing MASTG-DEMO code files. If you include them, keep them short and explanatory.
- Use references from official docs and standards. Avoid non-authoritative sources.
- In body text, reference internal MAS identifiers with a leading `@` (for example, @MASTG-KNOW-0001, @MASTG-TEST-0204, @MASTG-TECH-0014, @MASTG-TOOL-0031, MASWE-0089).


### Writing conventions

- Use American spelling, second person, and active voice.
- Prefer short paragraphs and bullet lists for scannability.
- Use HTML `<img>` for images as per the markdown instructions.

### Edge cases and guidance

- Cross-category content: If a topic spans two MASVS categories, choose the best fit and reference the other in the body; avoid duplicate pages.
- Generic vs platform: Concepts that are identical across platforms should be split into platform folders if platform detail matters; otherwise, place details where they differ and keep overviews succinct.
- Where to place recommendations: Keep all prescriptive advice (do/don't, secure configuration values, mitigations) in Best Practices pages under `best-practices/`, and link them from the "Related" section.
- When writing tests, techniques, or tools, always try to link to a knowledge article or create one if it's missing.

## Deprecation

If the source is gone, not relevant anymore, or too old, set the following in the YAML front matter:

- `status:` Must be set to `deprecated`
- `deprecation_note:` Short clarifying note for deprecation. Keep phrasing concise and imperative
- `covered_by:` List of MASTG-TOOL-xxxx tools covering for this one, if any.

**Example**

```yaml
---
masvs_category: MASVS-AUTH
platform: android
title: FingerprintManager
deprecated_since: 28
available_since: 23
status: deprecated
deprecation_note: "The FingerprintManager class is deprecated in Android 9 (API level 28) and should not be used for new applications. Instead, use the BiometricPrompt API or the Biometric library for Android."
covered_by: [MASTG-KNOW-0001]
---
```