---
name: 'Writing MASTG Best Practices Files'
applyTo: 'best-practices/*.md'
---

[https://mas.owasp.org/MASTG/best-practices/](https://mas.owasp.org/MASTG/best-practices/)
[https://github.com/OWASP/owasp-mastg/tree/master/best-practices](https://github.com/OWASP/owasp-mastg/tree/master/best-practices)

Best practices live under `best-practices/` and each file name must be the best-practice ID, for example `MASTG-BEST-0001.md`.

## Creating Best Practice IDs

When creating a new best practice (whether during porting or writing from scratch), use a **fake ID** with the notation `MASTG-BEST-0x##` (for example, `MASTG-BEST-0x33`). This prevents conflicts between parallel pull requests. Create new fake IDs incrementally (e.g., `MASTG-BEST-0x33`, `MASTG-BEST-0x34`, `MASTG-BEST-0x35`) as you add new content.

Once your pull request is reviewed and ready to merge, the team will assign real IDs (for example, `MASTG-BEST-0025`) before the content is published.

They must include official references. You can cite the MASTG as a hub only when it points to official sources (for example, Google/Apple documentation, standards, or vendor advisories).

Scope and relationship to Knowledge:

- Best Practices are prescriptive: they state what to do and why from a security perspective.
- Keep background explanations minimal; for conceptual background on OS features, link to Knowledge pages under `knowledge/`.

Relationship to Tests and Knowledge (nuance):

- Tests (MASTG-TEST) describe the issue and "what can go wrong", then show how to detect it.
- Knowledge (MASTG-KNOW) describes the feature/API neutrally (no "what can go wrong").
- Best Practices (MASTG-BEST) explains how to prevent or mitigate the issue the tests detect.

## Markdown: Metadata

Include a YAML front matter block with the following fields:

- `title`: Concise, action-oriented recommendation.
- `id`: Best-practice ID in the form `MASTG-BEST-\d{4}`.
- `platform`: One of: android, ios, generic.

Optional metadata:

- `alias`: URL-friendly slug (lowercase, hyphen-separated) used for navigation.
- `status`: draft, placeholder, deprecated.
- `note`: Short free-form note.
- `knowledge`: One or more related Knowledge pages (`MASTG-KNOW-####`) for neutral background and API reference.
- `available_since`: Minimum platform version/API level where this recommendation applies (Android: integer API level; iOS: release version, for example `13`).
- `deprecated_since`: Last applicable platform/API level.

Example:

```yaml
---
title: Preventing Screenshots and Screen Recording
alias: preventing-screenshots-and-screen-recording
id: MASTG-BEST-0014
platform: android
knowledge: [MASTG-KNOW-0105, MASTG-KNOW-0106, MASTG-KNOW-0107]
---
```

Notes:

- In body text, reference in-project identifiers with a leading @ (for example, @MASTG-TEST-0252). In YAML front matter, omit the @ and use bare IDs.

Best Practices should contain:

- what's the recommendation (stated clearly and concisely at the start)
- why is that good (security value and context, woven into the explanation)
- any caveats or considerations (for example, "it's good to have it, but remember it can be bypassed this way")
- official references (embedded inline as markdown links, not in a separate section)

## Structure

Best practices are flexible in structure and adapt to the content. They typically:

- Start with 1-2 paragraphs explaining the recommendation and its security value
- Use ## subheadings to organize content by platform, language, approach, or topic (e.g., "Java/Kotlin", "Swift / Objective-C", "Using ProGuard", "Common Misconceptions")
- Embed official documentation links inline using markdown syntax
- Reference related MASTG content with @ notation (e.g., @MASTG-TOOL-0022, @MASTG-KNOW-0018)
- Keep explanations concise and focused on the practice itself
- Include minimal code examples when helpful to illustrate the recommendation. Prefer to link to MASTG-DEMOs that have metadata `kind: pass`.
- Use **Note** or **Warning** Admonitions for important caveats or clarifications

Examples of good structure:

- Brief opening paragraph stating the practice → subsections for different languages/approaches → inline references
- Opening explanation with security context → detailed guidance with code examples → caveats woven in
- Direct recommendation → platform-specific implementation details → misconceptions or warnings

If additional resources need to be highlighted, list them informally at the end with natural phrasing (e.g., "See these resources for more details:") rather than a formal "References" section.

## Cross-linking

- From MASTG-TEST: use `best-practices: [MASTG-BEST-0001, MASTG-BEST-0011]` in the test's YAML front matter.
- Anywhere in markdown bodies: use @MASTG-BEST-0001 to link to a best practice.
