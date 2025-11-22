# Tools Authoring Instructions

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

## Metadata

Each file begins with a YAML front matter block.

**Required**

- `title:` Concise tool name. Add a qualifier when needed to disambiguate (for example, "Frida for Android", "nm - iOS")
- `platform:` One of: `android`, `ios`, `generic`, `network`
- `source:` Canonical homepage or repository URL
- `host:` List of operating systems the tool runs on. Allowed values (case-sensitive): `windows`, `linux`, `macOS`, `ios`, `android`
  - Use `host:` with a YAML list (preferred). If you find `hosts:` in legacy pages, migrate to `host:` when touching the file
  - Use `ios` or `android` for on-device tools (for example, Filza)

**Optional**

- `alternatives:` List of tool IDs that provide comparable functionality (YAML list of IDs only)
- `status:` One of `draft`, `placeholder`, `deprecated`. If absent, the default is `new`

**Examples**

```yaml
---
title: Frida for Android
platform: android
source: https://github.com/frida/frida
host: [windows, linux, macOS]
---
```

## Body content

Keep pages practical, scannable, and focused on security testing use.

### Recommended content

- Overview: One short paragraph describing what the tool is and where it fits
- Capabilities and Use Cases: Bullet list of relevant features for mobile testing
- Installation: Platform-specific notes (link out to official docs when appropriate). Include minimal, verified commands to get started
- Usage: Common commands, flags, or workflows relevant to MASTG tests and demos. Prefer concrete, copyable examples
- Examples: Short, targeted examples for typical tasks. If examples are long, link to demos or techniques instead
- Caveats and Limitations: Version compatibility, jailbreak or root requirements, known issues
- References: Links to official docs, repos, and authoritative resources
- Related: Cross-link to relevant tests, demos, and techniques

### Cross-linking

- Do not cross-link here to tests, demos, and techniques, etc.
- Those components will cross-link to tools, typically techniques, and demos
- To do that, they must add @MASTG-TOOL-0031 in their markdown body or as `tools: [MASTG-TOOL-0031]` in their YAML front matter

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

**Example**

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