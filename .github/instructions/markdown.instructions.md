---
description: 'Documentation and content creation standards'
applyTo: '**/*.md'
---

## Markdown Content Rules

The following markdown content rules are enforced in the validators:

1. **Headings**: Use appropriate heading levels (H2, H3, etc.) to structure your content. Do not use an H1 heading, as this will be generated based on the title.
2. **Lists**: Use bullet points or numbered lists for lists. Ensure proper indentation and spacing.
3. **Code Blocks**: Use fenced code blocks for code snippets. Specify the language for syntax highlighting.
4. **Links**: Use standard Markdown link syntax.
5. **Images**: Use HTML `<img>` tags for images. See the "Images" section for examples and guidelines.
6. **Tables**: Use markdown tables for tabular data. Ensure proper formatting and alignment.
7. **Whitespace**: Use appropriate whitespace to separate sections and improve readability.
8. **Front Matter**: Include YAML front matter at the beginning of the file with required metadata fields.

## Formatting and Structure

Follow these guidelines for formatting and structuring your markdown content:

- **Headings**: Use `##` for H2 and `###` for H3. Ensure that headings are used hierarchically. Recommend restructuring if the content includes H4, and more strongly recommend for H5.
- **Lists**: Use `-` for bullet points and `1.` for numbered lists. Indent nested lists with four spaces to match the linter configuration. Prefer dashes `-` over asterisks `*` for unordered lists. Generally:
  - Limit a single list to at most nine items when reasonable.
  - Avoid more than two levels of nesting.
  - Punctuate and capitalize list items consistently. Do not add end punctuation to list items that are not complete sentences unless they complete the introductory sentence. If list items complete an introductory sentence, end each (except the last) with a comma, omit the "and" before the last, and end the last item with appropriate punctuation.
- **Code Blocks**: Use triple backticks to create fenced code blocks. Specify the language after the opening backticks for syntax highlighting (e.g., kt, java, xml).
- **Links**: Ensure the link text is descriptive and the URL is valid.
- **Tables**: Use `|` to create tables. Ensure that columns are properly aligned and headers are included.
  - Include leading and trailing pipes to conform to the linter setting (MD055: `leading_and_trailing`).
- **Line Length**: There is no enforced hard limit.
- **Whitespace**: Use blank lines to separate sections and improve readability. Avoid excessive whitespace.

### Writing Style and Tone

- Keep content factual, brief, and focused. Avoid duplicating other sections of the guide and refrain from advertising commercial tools or services.
- Address the reader directly in the second person ("you"). Prefer active voice over passive voice.
- Ensure cohesion and coherence: link ideas logically; keep each paragraph focused on one idea; lead sections with the key point; use bullet points for scannability when appropriate.
- Write for an international audience with a basic technical background. Avoid hard-to-translate slang.
- Provide context and orientation: use a unique page heading, a concise introduction, and links to background information where helpful.

### Content Length and Organization

- Use short, scannable pages where possible (roughly one to two screens of text). For extensive sections, consider moving details to a supporting document and linking to it for clarity and conciseness.
- For digital content, favor shorter, cross-linked pages. If the content is intended for print, longer, comprehensive pages are acceptable.

### Gender Neutrality

- Avoid gendered pronouns (she/her/hers/herself, he/him/his/himself) and constructions like "he/she", "s/he", "his or her".
- Prefer alternatives:
  - Omit the pronoun where possible.
  - Use articles ("the", "a") where appropriate.
  - Use plural nouns and pronouns ("they") when it improves clarity.
  - Use the second person ("you") or imperative form.

### Language and Conventions

- Use American spelling and terminology.
- Title capitalization follows the Chicago Manual of Style:
  - Capitalize first and last words; nouns, pronouns, verbs, adjectives, adverbs, and subordinating conjunctions.
  - Lowercase articles, prepositions, and coordinating conjunctions (except when first or last).
- Numbers: spell out zero through ten; use numerals for numbers greater than ten.
- Android versions: write as "Android X (API level YY)" and avoid codenames.
- Contractions: prefer common contractions (e.g., "don't", "can't", "it's").

### Abbreviations

- On first use, spell out the term followed by the abbreviation in parentheses; use the abbreviation alone on subsequent uses within the chapter.
- If the term appears only once, prefer the full term instead of the abbreviation.
- In titles/headings, abbreviations are acceptable, but introduce them properly in the following text.
- Use "a" or "an" based on pronunciation (e.g., a URL, an APK).
- Form plurals by adding "s" unless the abbreviation already represents a plural noun (e.g., APIs, CSS).
- For common file formats like APK, IPA, or ZIP, do not prefix with a dot unless referring explicitly to the file extension.

### In-project Identifiers

Use special identifiers to reference project components consistently:

- Tests: `@MASTG-TEST-0001`
- Tools: `@MASTG-TOOL-0034`
- Similar patterns may exist for other entities (e.g., best practices, techniques) following `@MASTG-<KIND>-NNNN`.
- Weaknesses: `@MASWE-0123` (this one is an exception to the usual pattern)

Usage rules:

- In body text (Markdown content), include the leading `@` when referencing an item.
- In YAML front matter, omit the `@` and use the bare identifier (e.g., `MASTG-TEST-0001`).

Examples:

```markdown
You can validate this with @MASTG-TEST-0001 and compare results using @MASTG-TOOL-0034.
```

```yaml
weakness: MASWE-0069
best-practices: [MASTG-BEST-0010, MASTG-BEST-0011, MASTG-BEST-0012]
```

### Punctuation and Typographic Conventions

- After a colon, lowercase the first word unless it is a proper noun or starts two or more complete sentences or a direct question.
- Use the serial comma (Oxford comma).
- Use straight quotes and apostrophes (not curly quotes/apostrophes).
- Never use Horizontal rules like `---`.
- Emphasis/strong style: underscores for emphasis (`_text_`), asterisks for strong (`**text**`).
- Trailing punctuation allowed in headings (MD026) is limited to: `.,;:`
- Always prefer commas or parentheses over em-dashes, en-dashes, or hyphens.

## Images

For MASTG chapters and related content, always embed pictures using an HTML `<img>` element rather than Markdown image syntax:

- Put `src` as the first attribute.
- Optionally specify a `width` (e.g., `width="80%"`).
- Store images in the appropriate directory (e.g., `Document/Images/Chapters` for MASTG chapters).
- Inline HTML is permitted; the linter rule MD033 is disabled to allow this.

Example:

```markdown
<img src="Images/Chapters/0x05b/r2_pd_10.png" width="80%" />
```

Note: The linter does not require alt text for images (MD045 is disabled); however, including descriptive context in the surrounding text is helpful for accessibility.

## Validation Requirements

Ensure compliance with the following validation requirements:

- **Content Rules**: Ensure that the content follows the markdown content rules specified above.
- **Formatting**: Ensure that the content is appropriately formatted and structured according to the guidelines.
- **Validation**: Run the validation tools to check for compliance with the rules and guidelines.
