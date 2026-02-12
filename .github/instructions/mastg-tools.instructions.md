---
name: 'Writing MASTG Tool Files'
applyTo: 'tools/**/*.md'
---

Standards for authoring tool reference pages under `tools/`. These pages document tools used throughout tests, demos, and techniques in the MASTG.

## Locations

- `tools/android/MASTG-TOOL-####.md`
- `tools/ios/MASTG-TOOL-####.md`
- `tools/generic/MASTG-TOOL-####.md` (cross-platform or platform-agnostic tools)
- `tools/network/MASTG-TOOL-####.md` (traffic analysis and proxying)
- `tools/index.md` is the catalog landing page (don't edit it)

## File naming and IDs

- The tool ID is defined by the filename: `MASTG-TOOL-\d{4}.md`
- Do not add an `id:` field to the YAML front matter
- Use the next available number for the target folder. Coordinate in PRs to avoid clashes

## Markdown structure

- Follow the global Markdown rules in `.github/instructions/markdown.instructions.md`
- Headings in the body start at `##`. Use `##` and `###` only

### Metadata

Each file begins with a YAML front matter block.

**Required:**

- `title:` Concise tool name. Add a qualifier when needed to disambiguate (for example, "Frida for Android", "nm - iOS")
- `platform:` One of: `android`, `ios`, `generic`, `network`

**Preferred (add for new pages when possible):**

- `source:` Canonical homepage, repository URL, or official documentation URL
- `hosts:` List of operating systems the tool runs on
    - Prefer a YAML list under `hosts:` (for example, `hosts: [windows, linux, macOS]` or a multi-line list)
    - Common values used in this repo include `windows`, `linux`, `macOS`, `ios`, `android`

**Optional:**

- `alternatives:` List of tool IDs that provide comparable functionality (YAML list of IDs only)
- `status:` One of `draft`, `placeholder`, `deprecated`. If absent, the default is `new`

**Example:**

```yaml
---
title: Frida for Android
platform: android
source: https://github.com/frida/frida
hosts: [windows, linux, macOS]
---
```

### Body content

Keep pages practical, scannable, and focused on security testing use.

Tool pages are often intentionally short. Prefer a small amount of high-signal content over a large, rigid template.

Common patterns that match existing pages in `tools/`:

- Start with a short description paragraph (often with a link to the upstream project or docs).
- Add sections only when they add value for that tool. Typical headings are `## Installation`, `## Usage`, and tool-specific headings (for example, `## Installing Frida on iOS`).
- Include copyable commands when relevant. If usage is extensive, keep only the most common commands and link to a technique or upstream docs.
- Add caveats as `!!! note` / `!!! warning` admonitions when needed (version pinning, jailbreak/root requirements, security warnings).
- Link to related techniques/tests/demos where it helps the reader complete a workflow.

If you are editing an existing tool page, keep its current structure unless there is a clear benefit to reorganizing it.

MkDocs callouts:

- You can use MkDocs Material admonitions such as `!!! note`, `!!! warning`, and collapsible blocks (`??? info`) for version caveats and long side-notes.

## Cross-linking

- Tool pages may cross-link to techniques, tests, demos, and chapters when it helps the reader complete a workflow.
- Prefer using MASTG identifiers in body text where available (for example, @MASTG-TECH-0011).
- Tests, demos, and techniques should reference tools by ID whenever available (for example, @MASTG-TOOL-0031 in body text, or `tools: [MASTG-TOOL-0031]` in YAML where applicable).

## Conventions and quality

- Prefer official sources for installation steps. Avoid advertising or endorsing third-party distributions
- Favor commands that work across supported hosts when possible. Otherwise, clearly label host-specific commands
- For images, use HTML `<img>` tags per the markdown instructions (store assets in an appropriate images folder if needed)
- Keep examples minimal and verifiable. Longer walkthroughs belong in demos with runnable scripts

## How tests and demos should reference tools

- Tests and demos should reference tools by ID whenever available:
    - In body text: `@MASTG-TOOL-0031`
    - In YAML (for example, demo `tools:` list): `MASTG-TOOL-0031`
- If a commonly used tool lacks an official MASTG tool page, demos may temporarily list the tool by name (for example, `tools: [semgrep]`). Prefer adding a tool page and switching to the ID in follow-ups

## Deprecation

If the original source is gone, not relevant anymore, or too old, set the following in the YAML front matter:

- `status:` Must be set to `deprecated`
- `deprecation_note:` Short clarifying note for deprecation. Keep phrasing concise and imperative
- `covered_by:` List of MASTG-TOOL-xxxx tools covering for this one, if any.

**Example:**

```yaml
---
title: Cycript
platform: ios
source: https://www.cycript.org/
status: deprecated
deprecation_note: Cycript is no longer maintained and fails on modern iOS versions. Prefer Frida which is actively supported and more capable
covered_by: [MASTG-TOOL-0039]
---
```
