## Porting MASTG v1 tests to v2

### Before you start

This document focuses on the porting workflow. For how to write v2 tests (front matter fields, required sections, styles), see `mastg-test.instructions.md`. Do not duplicate that guidance here—follow it when drafting the ported test.

Check some of the current tests and take them as a reference.

* tests-beta
* demos

Always use the most recent tests (those with the highest IDs) as a reference/template. Use `tests-beta` files as the canonical template for structure and front matter.

Also, review these. We’ll be using them and creating new ones as well:

* [https://mas.owasp.org/MASTG/techniques/](https://mas.owasp.org/MASTG/techniques/)
* [https://mas.owasp.org/MASTG/tools/](https://mas.owasp.org/MASTG/tools/) 

#### Guidelines

* How to write new **tests**: [Writing MAS Weaknesses & Tests](https://docs.google.com/document/d/1EMsVdfrDBAu0gmjWAUEs60q-fWaOmDB5oecY9d9pOlg/edit?pli=1&tab=t.0#heading=h.j1tiymiuocrm)
* How to write **demos**: [Writing MAS Weaknesses & Tests](https://docs.google.com/document/d/1EMsVdfrDBAu0gmjWAUEs60q-fWaOmDB5oecY9d9pOlg/edit?pli=1&tab=t.0#heading=h.y294y561hx14) 

**Important** tips and guidelines: 

1. Create a rough draft.
   1. In most cases, you’ll have something in the v1 test
   2. Sometimes you can take the whole test or just a sentence or bullet point.
   3. You may use [chatGPT](https://chat.openai.com/) to create an initial draft and massage the content.
2. Edit it yourself using **your knowledge**
3. **Always include inline references** e.g. to
   - MASTG chapters
   - APIs
   - statements in the dev docs
4. Before pushing, use [deepl write](https://www.deepl.com/en/write) to correct any mistakes and tweak phrasing (especially if you used chatGPT, use deepl to humanize the text)

**About the IDs:**

- Use the decimal ID format as seen in `tests-beta` (for example, `MASTG-TEST-0233`). Do not use the `0x` notation.
- If a test must be split, suffix with a short decimal part during drafting (for example, `MASTG-TEST-0233-1`, `MASTG-TEST-0233-2`). Coordinate final IDs before merging.

**Language**: use simple language, be concise and clear

- Avoid passive voice
- Avoid long and complex sentences
  - **bad** "In order to encrypt data, Android offers the following Android APIs".
  - **good**: "You can use these APIs to encrypt data."
- See [Style Guide \- OWASP Mobile Application Security](https://mas.owasp.org/contributing/5_Style_Guide/) 

#### List of sources

Use the MASTG first: e.g., the guides for 0x04 (general), 0x05 (Android), and 0x06 (iOS), and **extend** with Android/Apple docs.

**Android** 

- [https://developer.android.com/privacy-and-security/security-tips](https://developer.android.com/privacy-and-security/security-tips)
- [https://developer.android.com/privacy-and-security/security-best-practices](https://developer.android.com/privacy-and-security/security-best-practices)
- [https://developer.android.com/privacy-and-security/risks](https://developer.android.com/privacy-and-security/risks) (these have special treatment in the metadata)
- [https://developer.android.com/privacy-and-security/about](https://developer.android.com/privacy-and-security/about)
- [https://developer.android.com/docs/quality-guidelines/core-app-quality\#sc](https://developer.android.com/docs/quality-guidelines/core-app-quality#sc)

**iOS**

- [https://developer.apple.com/app-store/user-privacy-and-data-use/](https://developer.apple.com/app-store/user-privacy-and-data-use/) 

## Porting workflow

Map to MASWE. You can check the new MASWE by searching for the old test ID in the **weaknesses/** directory, for example, MASTG-TEST-0017.

1. Create a branch for the ticket: `port-MASTG-TEST-0017` (adjust ID).

2. Place files correctly: If the relevant `MASVS-*` folder does not exist yet under `tests-beta/<platform>/`, create it.

3. Name the new file using the decimal ID (for example, `MASTG-TEST-0233.md`).

4. Decide scope changes: One v1 test may become one or multiple v2 tests (or be merged). Capture only what is testable and actionable; move general theory elsewhere (see below).

5. Gather references: Search for references in `Document/`, `techniques/`, `tools/`, and `knowledge/`.

6. Normalize background vs test logic: Add platform background as @MASTG-KNOW-xxxx references. If none exists, create a new knowledge page. Keep the test focused on detection and evaluation.

7. Link general concepts to Document: Some content belongs in the Document chapter. For example, general cryptography concepts such as symmetric/asymmetric encryption, hashing, signing, etc. If the knowledge is still in `Document/`, link to it.

   **Example:** ["Post Quantum"](https://mas.owasp.org/MASTG/Document/0x04g-Testing-Cryptography/#post-quantum)

8. Avoid duplication: You may summarize, but focus the test on how to detect the issue in Android/iOS and why it matters for this test.

9. Link mitigations: Ensure there is a clear mitigation in best practices; add the reference in front matter (field defined in `mastg-test.instructions.md`).

10. Trim non-essential parts: If parts of the v1 test aren’t relevant, remove them and note it in the ticket.

    **Example:** In MASTG-TEST-0017, what’s written in dynamic analysis does not make sense: why should we validate that `setUserAuthenticationValidityDurationSeconds` is for real (we are not testing if Android features work; we assume they do). Perhaps we could also employ dynamic analysis, but using a different approach.

11. Fill missing links to docs and developer references as needed.

    **Example:** this test and the referenced section both were missing links to [https://developer.android.com/](https://developer.android.com/) 

## Porting considerations

V1 tests often include techniques, tools, and theory/knowledge. Move that to the right place (or create it) and reference it (for example, @MASTG-TECH-0002, @MASTG-KNOW-0011, @MASTG-TOOL-0097). In the test body, use `@` prefixes; in YAML front matter, use bare IDs without `@`. For exact field names and section structure, see `mastg-test.instructions.md`.

Theory must always be linked to the @MASTG-KNOW-xxxx components. If the theory is not covered yet, create a new knowledge page. If it's a general concept, it may still belong in the Document chapter under 0x04.

Review existing content and **UPDATE** it. Especially references to Android/iOS versions and things you know have changed since the text was written.

#### Best practices linkage

Best practices are platform-specific and can be linked in the test metadata. Our automation creates a “Mitigations” section automatically.

1. Check if a best practice already exists in `best-practices/` folder
2. If it doesn’t exist yet, create it new in `best-practices/` using the next available ID.
3. Add a reference to the best practices with @MASTG-BEST-xxxx

#### Deprecating V1 tests

Add metadata to the v1 file:

- `status: deprecated`
- `covered_by: [MASTG-TEST-02xx]` (leave empty if no coverage)
- `deprecation_note: "New version available in MASTG V2"`

If the test isn’t covered in MASVS v2, enter the reason why.

If you don't know, open a ticket "Add Deprecation Note for MASTG-TEST-00xx".

### Threat-based alignment

Some findings apply only with stronger attacker capabilities (for example, root/jailbreak—typically L2). Double-check the threat model against the MASWE profile. If there’s a mismatch, consider creating a separate MASWE.

[https://mas.owasp.org/MASTG/tests/android/MASVS-PLATFORM/MASTG-TEST-0010/\#dynamic-analysis](https://mas.owasp.org/MASTG/tests/android/MASVS-PLATFORM/MASTG-TEST-0010/#dynamic-analysis)

Indicate in the test whether exploitation requires root/jailbreak.

> *On devices supporting file-based encryption (FBE) ↗, snapshots are stored in the /data/system\_ce/\<USER\_ID\>/\<IMAGE\_FOLDER\_NAME\> folder. \<IMAGE\_FOLDER\_NAME\> depends on the vendor, but the most common names are snapshots and recent\_images. If the device doesn't support FBE, the /data/system/\<IMAGE\_FOLDER\_NAME\> folder is used.*

Accessing these folders and the snapshots requires root.

EXAMPLE: cleartext configured globally in NSC. No need for root, network/mitm: yes

What other things should we consider here?

- **binary**: possible just because the attacker has the binary (decrypted on iOS)
- **app**: another app on the device with the right permissions (e.g. full access to external storage)
- **mitm**: a network-based attacker capable of performing MITM
- **user computer**: the user using adb
- **user UI**: using the app's or another app’s UI, e.g. third-party file manager;
- **user confirmation**: e.g. pasteboard confirmation

### Code Snippets

If the v1 test has snippets that inspire a demo, prefer Kotlin and Swift for new examples (avoid Java/Objective-C). You can create a demo with `status: draft` and finish later. See `mastg-demo.instructions.md` for demo authoring.

You can create a demo **status: draft** and come back to it later. To ensure we extracted everything from V1. Create a ticket **"Finish Demo Draft MASTG-DEMO-xxxx"**

- [https://github.com/WithSecureLabs/sieve/](https://github.com/WithSecureLabs/sieve/)
- Ovaa
- MASTG playground
- [https://mas.owasp.org/MASTG/apps/](https://mas.owasp.org/MASTG/apps/)

List of apps to get ideas from, as inspiration. You may include "inspired by" to reference the source whenever our new content is similar enough.

### Notes (keep in mind while porting)

- A test must not describe the general problem again (that’s the Weakness’s job). Keep the test focused on detection and evaluation.
- If you find an edge case, consider whether it deserves a separate test with distinct steps/evaluation.

### OS version

**For Android:** We have agreed on supporting the **current version \- 5**. This yields an average adoption rate of roughly 90%. See [https://apilevels.com/](https://apilevels.com/) 

If a test is no longer applicable for specific OS versions, add an applicability note so testers can discard it when it is not relevant.

Avoid creating demos for unsupported versions.

For example, WebView file access or defaults depend on this.

Refer to `mastg-test.instructions.md` for:

- Exact front matter fields and examples
- Required sections (Overview, Steps, Observation, Evaluation)
- Title conventions, platforms, profiles, types, and optional fields