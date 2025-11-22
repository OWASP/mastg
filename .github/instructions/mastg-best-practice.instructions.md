## Best Practices

[https://mas.owasp.org/MASTG/best-practices/](https://mas.owasp.org/MASTG/best-practices/)
[https://github.com/OWASP/owasp-mastg/tree/master/best-practices](https://github.com/OWASP/owasp-mastg/tree/master/best-practices) 

Best practices live under `best-practices/` and each file name must be the best-practice ID, for example `MASTG-BEST-0001.md`.

Best practices must be linked from MASTG tests using the `best-practices:` key in the test’s YAML front matter (use bare IDs, without the leading @).

They must include official references. You can cite the MASTG as a hub only when it points to official sources (for example, Google/Apple documentation, standards, or vendor advisories).

Scope and relationship to Knowledge:

- Best Practices are prescriptive: they state what to do and why from a security perspective.
- Keep background explanations minimal; for conceptual background on OS features, link to Knowledge pages under `knowledge/`.

### Markdown: Metadata

Include a YAML front matter block with the following fields:

- title: Concise, action-oriented recommendation.
- id: Best-practice ID in the form `MASTG-BEST-\d{4}`.
- platform: One of: android, ios.

Optional metadata:

- alias: URL-friendly slug (lowercase, hyphen-separated) used for navigation.
- status: draft, placeholder, deprecated.
- note: Short free-form note.
- available_since: Minimum platform version/API level where this recommendation applies (Android: integer API level; iOS: release version, for example `13`).
- deprecated_since: Last applicable platform/API level.

Example:

```yaml
---
title: Preventing Screenshots and Screen Recording
alias: preventing-screenshots-and-screen-recording
id: MASTG-BEST-0014
platform: android
---
```

Notes:

- In body text, reference in-project identifiers with a leading @ (for example, @MASTG-TEST-0252). In YAML front matter, omit the @ and use bare IDs.

Best Practices should contain:

- what's the recommendation
- why is that good
- any caveats or considerations (for example, "it's good to have it, but remember it can be bypassed this way")
- official references

### Recommended Structure

Use clear sections to improve readability and enable consistent rendering.

- Overview or Recommendation: State the practice and its scope in one or two short paragraphs. Link to relevant Knowledge pages for background when needed.
- Rationale: Explain the security value and platform implications; keep conceptual detail brief and defer to Knowledge pages.
- Caveats or Considerations: Note limits, bypasses, compatibility constraints, or trade-offs.
- References: Bullet list of official, authoritative sources.

Example References section:

```markdown
## References

- Android docs: https://developer.android.com/security/fraud-prevention/activities#flag_secure
- Apple docs: https://developer.apple.com/documentation
- Standard: https://www.rfc-editor.org
```

### Cross-linking

- From tests: use `best-practices: [MASTG-BEST-0001, MASTG-BEST-0011]` in the test’s YAML front matter. The site generator will automatically create Mitigations links.
- In body text: reference tests, tools, or techniques with @ (for example, @MASTG-TEST-0252, @MASTG-TOOL-0031).

### Style

- Follow the global Markdown rules and writing style (second person, active voice, concise, American spelling).
- Use Chicago title case for titles.
- Keep content focused and avoid duplicating other guide sections. Link out where appropriate.