---
name: 'Writing MASTG Technique Files'
applyTo: 'techniques/**/*.md'
---

Authoring standards for the techniques catalog under `techniques/`. Techniques document reusable procedures and workflows that are referenced by tests, demos, and other content across the guide.

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

Note: YAML front matter uses `---` delimiters. Do not use `---` as a horizontal rule in the body.

## Markdown: Metadata

Include a YAML front matter block with these fields.

Required:

- `title:` Clear, action-oriented name for the technique (for example, "Method Tracing").
- `platform:` One of: `android`, `ios`, `generic`.

Optional:

- `status:` Use this only when needed. Currently, `placeholder` is used for stub pages. Other values such as `draft` or `deprecated` may be used when the catalog needs them.

Avoid adding extra front matter keys unless you know the site uses them.

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

## Markdown: Body

Keep techniques practical and focused. Many existing technique pages start with a short introduction paragraph and then jump directly into sections. A dedicated "Overview" section is optional.

Prefer tool-agnostic wording where possible. Where tool specifics are necessary, link to the tool pages and keep commands minimal.

Do not force a fixed section template. Techniques in this repo commonly use whatever headings best describe the workflow, for example:

- Task-oriented headings such as "Remote Shell", "Installing the Proxy Certificate", "Simulator Shell".
- Tool- or format-oriented headings such as "Using jq", "Using plistlib".

When a technique describes multiple tool-specific ways to achieve the same goal, prefer splitting them into tool subsections titled like `## Using @MASTG-TOOL-####`. This matches existing technique pages and keeps the options scannable. You can add short qualifiers in parentheses when needed (for example, "(Jailbroken Devices Only)").

Cross-linking rules:

- In body text, reference project identifiers with a leading `@` (for example, @MASTG-TEST-0204, @MASTG-TOOL-0031).
- In YAML front matter, always use bare identifiers (no `@`).

MkDocs callouts:

- You can use MkDocs Material admonitions for long notes, version caveats, and collapsible content (for example, `??? info` and `??? note`). Follow existing patterns in the catalog.

## Writing conventions

- Prefer imperative voice in steps ("Run", "Attach", "Export").
- Keep commands copyable and self-contained. Where platform prompts or additional context are needed, explain in one short sentence.
- Favor official sources for installation instructions; avoid endorsing third-party distributions.
- Use HTML `<img>` tags for images, per the markdown instructions.

## Examples

````markdown
---
title: Obtaining App Permissions
platform: android
---

Android permissions are declared in the `AndroidManifest.xml` file using the `<uses-permission>` tag. You can use multiple tools to view them.

## Using the AndroidManifest

Extract the `AndroidManifest.xml` as explained in @MASTG-TECH-0117 and retrieve all [`<uses-permission>`](https://developer.android.com/guide/topics/manifest/uses-permission-element) elements.

## Using @MASTG-TOOL-0124

`aapt` can be used to view the permissions requested by an application.

```bash
$ aapt d permissions org.owasp.mastestapp.apk
package: org.owasp.mastestapp
uses-permission: name='android.permission.INTERNET'
...
```

...

````

### Edge cases and guidance

- Multi-platform techniques: If steps are identical across platforms, place the page under `generic/`. If they differ meaningfully, create separate platform pages.
- Tool selection: Link to multiple tools when appropriate and list trade-offs briefly in the text; keep detailed tool usage on the tool pages.
- Deprecation: If a technique becomes obsolete (for example, a platform's removed capability), set `status: deprecated` and add a short note at the top explaining why, with links to alternatives.
