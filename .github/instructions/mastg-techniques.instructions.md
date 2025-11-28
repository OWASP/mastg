## Techniques

Authoring standards for the techniques catalog under `techniques/`. Techniques document reusable procedures and workflows used by tests and demos.

Locations:

- `techniques/android/MASTG-TECH-####.md`
- `techniques/ios/MASTG-TECH-####.md`
- `techniques/generic/MASTG-TECH-####.md` (platform-agnostic or cross-platform)
- `techniques/index.md` (catalog landing)

File naming and IDs:

- The filename defines the technique ID: `MASTG-TECH-\d{4}.md`.
- Do not add an `id:` field to the YAML front matter for techniques.
- Use the next available number within the platform folder. Coordinate in PRs to avoid collisions.

Follow the global Markdown rules (see `.github/instructions/markdown.instructions.md`). Use `##` for top-level sections inside the page.

### Markdown: Metadata

Include a YAML front matter block with these fields.

Required:

- `title:` Clear, action-oriented name for the technique (for example, "Method Tracing").
- `platform:` One of: `android`, `ios`, `generic`.

Optional:

- `status:` `draft`, `placeholder`, or `deprecated`.
- `note:` Short free-form note for scope or caveats.
- `available_since:` Minimum platform/API level where this technique applies (Android: integer API level; iOS: release version, for example `13`).
- `deprecated_since:` Last applicable platform/API level.

Examples:

```yaml
---
title: Method Tracing
platform: android
---
```

```yaml
---
title: Intercepting HTTP Traffic Using an Interception Proxy
platform: generic
---
```

### Markdown: Body

Keep techniques practical, step-by-step, and tool-agnostic when possible. Where tool specifics are necessary, prefer linking to tool pages and keep commands minimal.

Recommended structure:

- Overview: What the technique does and when to use it.
- Prerequisites: Any environmental requirements (device state, jailbreak/root, certificates, OS tooling).
- Steps: Ordered steps with commands and short explanations. Prefer resilient commands and include expected prompts/outputs briefly.
- Validation: How to confirm success (signals, artifacts, logs). Mention common failure modes and remediation tips.
- Variations: Platform- or framework-specific variants (for example, Flutter, React Native), and alternatives.
- References: Authoritative docs or guides.
- Related: Cross-links to tests, tools, and demos using this technique.

Cross-linking rules:

- In body text, reference project identifiers with a leading `@` (for example, @MASTG-TEST-0204, @MASTG-TOOL-0031).
- In YAML front matter, always use bare identifiers (no `@`).

### Writing conventions

- Prefer imperative voice in steps ("Run", "Attach", "Export").
- Keep commands copyable and self-contained. Where platform prompts or additional context are needed, explain in one short sentence.
- Favor official sources for installation instructions; avoid endorsing third-party distributions.
- Use HTML `<img>` tags for images, per the markdown instructions.

### Examples

````markdown
## Overview

Method tracing captures method entry/exit events to understand runtime behavior. This helps locate sensitive logic and verify mitigations.

## Prerequisites

- USB debugging enabled
- @MASTG-TOOL-0031 (Frida)

## Steps

1. List app processes: `frida-ps -Uai | grep com.example.app`
2. Trace target APIs:

```sh
frida-trace -U -i "*Network*" -n com.example.app
```

## Validation

You should see method entries in the console matching the targeted patterns. If nothing appears, verify the process name and that the device permits tracing.

## Related

- Tests: @MASTG-TEST-0252
- Tools: @MASTG-TOOL-0031
````

### Edge cases and guidance

- Multi-platform techniques: If steps are identical across platforms, place the page under `generic/`. If they differ meaningfully, create separate platform pages.
- Tool selection: Link to multiple tools when appropriate and list trade-offs briefly in the text; keep detailed tool usage on the tool pages.
- Deprecation: If a technique becomes obsolete (for example, a platform's removed capability), set `status: deprecated` and add a short note at the top explaining why, with links to alternatives.
