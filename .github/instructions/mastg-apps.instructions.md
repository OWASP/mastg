# Reference Apps Authoring Instructions

Standards for authoring reference application pages under `apps/`. These pages describe vulnerable or exemplary applications used across tests, techniques, and demos.

## Locations

- `apps/android/MASTG-APP-####.md`
- `apps/ios/MASTG-APP-####.md`
- `apps/index.md` is the catalog landing page (don't edit it)

## File naming and IDs

- The filename defines the app ID: `MASTG-APP-\d{4}.md`
- Do not add an `id:` field to the YAML front matter
- Use the next available number in the platform folder. Coordinate in PRs to avoid collisions

## Markdown structure

- Follow the global Markdown rules in `.github/instructions/markdown.instructions.md`
- Headings in the body start at `##`. Use `##` and `###` only

## Metadata

Each file begins with a YAML front matter block.

**Required**

- `title:` App name, use the official capitalization. Add a disambiguator if needed (for example, *Android UnCrackable L1*)
- `platform:` One of `android`, `ios`
- `package:` Android application package ID or iOS bundle identifier (for example, `com.example.app`)
- `source:` Canonical page to obtain the app (official repo, release artifact, or MASTG crackmes catalog entry). Prefer a stable, versioned URL

**Optional**

- `download_url`: URL to download the APK/IPA.
- `store_url:` Store listing URL if relevant (e.g. Google Play, App Store)
- `status:` use `placeholder` only if it's a draft, otherwise do not include `status` (default is `new` and you don't have to add it explicitly)

**Examples**

```yaml
---
title: Android UnCrackable L1
platform: android
source: https://mas.owasp.org/crackmes/Android#android-uncrackable-l1
package: sg.vantagepoint.uncrackable1
---
```

## Body content

Entries should be short and referential. Do not duplicate installation or usage docs that belong in the app’s own repository.

- One or two sentences describing the app and its purpose
- Add any platform-specific hints, such as jailbreak expectations or proxy setup

### Cross-linking

In the YAML front matter, add `weaknesses` listing all the weaknesses from the MASWE that can be tested and are present in the app.

Example:

```md
weaknesses: [MASWE-0034, MASWE-0056]
```

Do not add specific app versions here. If a MASTG-DEMO requires a particular version of an app, document it in the demo, not in the app entry.

## Writing conventions

- Use the official app name and capitalization
- Prefer official sources or the MASTG Crackmes catalog for downloads

## Deprecation

If the original source is gone, not relevant anymore, or too old, set the following in the YAML front matter:

- `status:` Must be set to `deprecated`
- `deprecation_note:` Short clarifying note for deprecation. Keep phrasing concise and imperative
- `covered_by:` List of MASTG-APP-xxxx apps covering for this one, if any.
