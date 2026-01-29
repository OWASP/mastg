---
name: 'Style and Formatting for MASTG Markdown Files'
applyTo: '**/*.md'
---

## Markdown Content Rules

The following markdown content rules are enforced in the validators:

1. **Headings**: Use appropriate heading levels (H2, H3, etc.) to structure your content. Do not use an H1 heading, as this will be generated based on the title.
2. **Lists**: Use bullet points or numbered lists for lists. Ensure proper indentation and spacing.
3. **Code Blocks**: Use fenced code blocks for code snippets. Specify the language for syntax highlighting.
4. **Links**: Use standard Markdown link syntax.
5. **Images**: Use HTML `<img>` tags for images. See the "Images" section for examples and guidelines.
6. **Tables**: Use Markdown tables for tabular data. Ensure proper formatting and alignment.
7. **Whitespace**: Use appropriate whitespace to separate sections and improve readability.
8. **Front Matter**: Include YAML front matter at the beginning of the file with required metadata fields.

## Formatting and Structure

Follow these guidelines for formatting and structuring your Markdown content:

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
- Ensure [cohesion and coherence](https://writing.chalmers.se/chalmers-writing-guide/writing-a-text/coherence-cohesion/): link ideas logically; keep each paragraph focused on one idea; lead sections with the key point; use bullet points for scannability when appropriate.
- Write for an international audience with a basic technical background. Avoid hard-to-translate slang.
- Provide context and orientation: use a unique page heading, a concise introduction, and links to background information where helpful.

### Scannability

Use these techniques to make content easy to scan:

- Use lists rather than dense paragraphs where possible.
- Keep one main idea per paragraph.
- Put the most important information at the top of the page.
- Use meaningful headings and subheadings.
- Prefer short, simple words.

For longer pages, consider:

- Anchor links.
- Cross-links to relevant sections.
- Meaningful graphics (where appropriate).

### Technical Writing Resources

If you are writing or reviewing content for the project, these free courses are helpful:

- [Google Technical Writing One](https://developers.google.com/tech-writing/one)
- [Google Technical Writing Two](https://developers.google.com/tech-writing/two)

### Content Length and Organization

- Use short, scannable pages where possible (roughly one to two screens of text). For extensive sections, consider moving details to a supporting document and linking to it for clarity and conciseness.
- For digital content, favor shorter, cross-linked pages. If the content is intended for print, longer, comprehensive pages are acceptable.

### Timeliness

When you include statistical data:

- Use current information.
- Cite the source.
- Include the date the data was consulted.

### Gender Neutrality

- Avoid gendered pronouns and gendered constructions.
- Prefer alternatives:
    - Omit the pronoun where possible. Example: "The user authenticates using ...".
    - Use articles ("the", "a") where appropriate. Example: "When the user enters the password ...".
    - Use plural nouns and pronouns ("they") when it improves clarity. Example: "Attackers will use their jailbroken devices ...".
    - Use the second person ("you") or imperative form. "If you run this code, you can bypass ..." or "Run this code to bypass ...".

### Language and Conventions

- Use American spelling and terminology.
- Title capitalization follows the Chicago Manual of Style:
    - Capitalize first and last words; nouns, pronouns, verbs, adjectives, adverbs, and subordinating conjunctions.
    - Lowercase articles, prepositions, and coordinating conjunctions (except when first or last).
- Numbers: spell out zero through ten (e.g., "three", "ten"); use numerals for numbers greater than ten (e.g., "12", "300").
- Android versions: write as "Android X (API level YY)" and avoid codenames.
- Contractions: prefer common contractions (e.g., "don't", "can't", "it's").

### Contractions

Prefer common contractions in prose (when they improve readability), such as:

- "are not" -> "aren't"
- "cannot" -> "can't"
- "could not" -> "couldn't"
- "did not" -> "didn't"
- "do not" -> "don't"
- "does not" -> "doesn't"
- "has not" -> "hasn't"
- "had not" -> "hadn't"
- "have not" -> "haven't"
- "is not" -> "isn't"
- "it is" -> "it's"
- "that is" -> "that's"
- "there is" -> "there's"
- "was not" -> "wasn't"
- "were not" -> "weren't"
- "will not" -> "won't"
- "would not" -> "wouldn't"
- "you are" -> "you're"
- "you have" + _verb_ -> "you've" + _verb_
- "you will" -> "you'll"

### Plurals

- Use standard grammar and punctuation for plurals.
- For calendar years, do not use an apostrophe (for example, "1990s", not "1990's").

### Standardization

The MAS project (MASVS, MASTG, MASWE) aims for consistent wording that is clear and unambiguous.

- If you notice inconsistent terms or abbreviations, please open a GitHub discussion suggesting a standard wording.
- If you are unsure which wording to standardize on, open a GitHub discussion for community input.

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
- Weaknesses: `@MASWE-0023` (this one is an exception to the usual pattern)

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
- Avoid horizontal rules (`---`) to separate sections (`---` is still required for YAML front matter delimiters).
- Emphasis/strong style: underscores for emphasis (`_text_`), asterisks for strong (`**text**`).
- Trailing punctuation allowed in headings (MD026) is limited to: `.,;:`
- Always prefer commas or parentheses over em-dashes, en-dashes, or hyphens.

### Lint-Friendly Whitespace and Quotes

The repository linting rules enforce a few extra constraints:

- Do not use curly quotes.
- Do not use no-break spaces.
- Avoid trailing spaces.
- Avoid double spaces in prose.

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

## External References

### Web Links

Use Markdown inline link format:

- `[TEXT](URL "TITLE")`, or
- `[TEXT](URL)`.

If you use the optional title form, escape special characters inside the title (especially apostrophes and backticks) to avoid broken rendering.

### References Section Links

When adding links to a **"References"** section at the end of a chapter in `Document/0x*.md`, use the format `- Title - <url>`. This helps LaTeX print URLs correctly in the PDF.

Example:

```markdown
- adb - <https://developer.android.com/studio/command-line/adb>
```

### Books and Papers

For books and papers, cite using the format `[#NAME]`, then add the full reference under a **"References"** section.

Example:

```markdown
An obfuscated encryption algorithm can generate its key (or part of the key)
using data collected from the environment [#riordan].

## References

- [#riordan] - James Riordan, Bruce Schneier. Environmental Key Generation towards Clueless Agents. Mobile Agents and Security, Springer Verlag, 1998
```

## References Within the Guide

Use internal references sparingly.

- When possible, name the chapter or section in prose.
- If you need a deep link, link directly to the target section and use a lowercase, hyphenated anchor.

Example:

```markdown
See the section "[App Bundles](0x05a-Platform-Overview.md#app-bundles)" in the chapter "Platform Overview".
```

## Technical Terms

- For specific technical terms, spell and punctuate them as used by the vendor.
- For generic technical terms, prefer established references (for example, a dictionary or a style manual). For example:
    - "email" (not "e-mail") per Merriam-Webster
    - "login" (not "log-in") per Merriam-Webster
    - "JavaScript" (not "Javascript" or "Java Script") per Mozilla Developer Network
    - "Wi-Fi" (not "WiFi" or "wifi") per Wi-Fi Alliance
    - "iOS" (not "IOS" or "i-phone OS") per Apple

When in doubt, open a GitHub discussion to suggest a standard term.

The table below lists preferred noun forms and adjectival forms for some common terms:

| Noun Form | Adjectival Form |
| --- | --- |
| App Store | NA |
| backend | backend |
| Base64 | Base64- |
| black box | _same_ |
| Bundle ID | NA |
| bytecode | NA |
| client side | client-side |
| codebase | _same_ |
| code signing | _same_ |
| command line | _same_ |
| disassembler | NA |
| end users | NA |
| file name | _same_ |
| macOS | NA |
| OS X | NA |
| pentest | _same_ |
| PhoneGap | NA |
| Python | NA |
| repackage | NA |
| runtime | _same_ |
| server side | server-side |
| snapshot length | NA |
| use case | _same_ |
| Wi-Fi | _same_ |
| white box | _same_ |

## Comments

Use mkdocs admonition comments to annotate special content:

```markdown
!!! note "Note Title"
    Note body text.
```

or

```markdown
??? info "Info Title"
    Info body text.
```

See [mkdocs admonitions documentation](https://squidfunk.github.io/mkdocs-material/reference/admonitions/) for details.

## Code and Shell Commands

- Use fenced code blocks for sample code, shell commands, and paths.
- Specify the language for syntax highlighting when possible.
- For shell commands, do not include prompts (host name, username, etc.).

Example:

````markdown
```shell
echo 'Hello World'
```
````

When a command includes parameters the reader must change, surround them with angle brackets:

```shell
adb pull <remote_file> <target_destination>
```

Do not prepend dollar signs (`$`) or other prompt characters to shell commands.

## In-Text Keywords

When not in a code block:

- Use backticks for code identifiers (for example, function names, class names, command names, file paths).
- Use straight double quotes for human-readable names (for example, section titles, chapter titles, menu items).
- Do not add parentheses or other punctuation inside backticks (for example, write `main`, not `main()`).

If a noun in backticks is plural, place the "s" outside the backticks (for example, `RuntimeException`s).

## Navigation

When referring to any UI element by name, put its name in boldface, using `**<name>**` (e.g., **Home** -> **Menu**).

## Validation Requirements

Ensure compliance with the following validation requirements:

- **Content Rules**: Ensure that the content follows the markdown content rules specified above.
- **Formatting**: Ensure that the content is appropriately formatted and structured according to the guidelines.
- **Validation**: Run the validation tools to check for compliance with the rules and guidelines.
