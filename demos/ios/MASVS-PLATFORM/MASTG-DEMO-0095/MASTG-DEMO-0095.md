---
platform: ios
title: Attacker-Controlled Input in a WebView Leading to Unintended Navigation
code: [swift]
id: MASTG-DEMO-0095
test: MASTG-TEST-0332
kind: fail
---

## Sample

This sample demonstrates how attacker controlled input inside a WebView can alter the rendered page and trigger unintended navigation. The app loads a trusted local HTML file, but the page reads the `username` parameter from the URL and injects it into the DOM using `innerHTML`.

Although the app uses `webView.loadFileURL(urlWithUsername, allowingReadAccessTo: docDir)`, broad file read access is not the focus of this demo. See @MASTG-DEMO-0096 for a deeper analysis of the file access aspect of this vulnerability. The issue demonstrated here is that attacker-controlled input is rendered as HTML, which allows the attacker to inject content that changes page behavior and causes unintended navigation.

When selecting payloads, note that `<script>` payloads usually do not execute in this case because scripts inserted through `innerHTML` are generally inert. However, other injected elements can still have side effects. For example, `<img onerror>` and `<svg onload>` can execute JavaScript through event handlers, and `<meta http-equiv="refresh">` may also trigger navigation by instructing the page to refresh to a different URL.

Example payloads:

- `<meta http-equiv="refresh" content="1; url=https://evil.com">`
- `<img src=x onerror="window.location='https://evil.com'">`
- `<svg onload="window.location='https://evil.com'"></svg>`

Summary of steps leading to this vulnerability.

1. The app creates a trusted local HTML file and loads it into a `WKWebView`.
2. The WebView URL includes attacker controlled input in the `username` query parameter.
3. The page reads the `username` value from `window.location.search`.
4. The value is inserted into the DOM using `innerHTML`.
5. Because the input is treated as HTML instead of plain text, the attacker can inject markup.
6. The injected markup introduces active behavior, such as an event handler or a refresh directive.
7. As a result, the WebView navigates to an attacker-chosen destination.

{{ MastgTest.swift # ai-decompiled.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ load_webview.r2 }}

{{ run.sh }}

## Observation

The output shows all cross-references and disassembled snippets.

{{ output.txt # showWebView.asm }}

## Evaluation

The test case fails because the `username` parameter (attacker-controlled) is inserted into the WebView URL without validation, and the page then assigns it directly to `innerHTML`. Because the browser treats the value as HTML rather than plain text, an attacker can inject markup that triggers unintended navigation.

The code also contains a separate issue where the WebView is granted read access to the entire `Documents` directory, which contains sensitive files. However, the focus of this demo is on the HTML injection and unintended navigation aspect of the vulnerability. See @MASTG-DEMO-0096 for a deeper analysis of the file access aspect.

### AI-Decompiled Code Analysis

!!! note "About `ai-decompiled.swift`"
    The `ai-decompiled.swift` file is an AI-assisted reconstruction derived from `showWebView.asm`, `docDir-init.asm`, and `fileURL-init.asm` and is provided only as a convenience for understanding the logic. It may be inaccurate or incomplete; the assembly and the original binary are the authoritative sources for analysis.

1. On **line 27**, the function constructs a URL string by concatenating `fileURL.absoluteString`, `"?username="`, and the attacker-controlled `username` argument without any validation.
2. On **line 29**, the concatenated string is passed to `URL(string:)` to create a `URL` object without validating the resulting scheme, host, path, or structure.
3. On **line 35**, the constructed URL is passed to `WKWebView.loadFileURL`, allowing a user who can alter `username` to influence the URL that is ultimately loaded.

### Disassembly Analysis

The `loadFileURL:allowingReadAccessToURL:` call is at `0x100004f20` in the `username` completion closure at `0x100004d24` (all addresses below are from `showWebView.asm`). The two arguments are built as follows.

**Step 1 — Build the URL string with the attacker-controlled `username`:**

At `0x100004df8`, the `fileURL` static property is projected and its `absoluteString` is fetched. The query suffix `?username=` is then encoded inline as immediate character constants and appended to form the final URL string:

```text
0x100004e04      bl sym.imp.Foundation.URL.absoluteString_...vg_  ; get fileURL as a string
0x100004e0c      mov x0, 0x753f                            ; '?u'
0x100004e10      movk x0, 0x6573, lsl 16                   ; 'se'
0x100004e14      movk x0, 0x6e72, lsl 32                   ; 'rn'
0x100004e18      movk x0, 0x6d61, lsl 48                   ; 'am'
0x100004e20      mov x1, 0x3d65                            ; 'e='
0x100004e28      bl sym.imp.append_...ySSF_                 ; append "?username"
0x100004e2c      mov x0, x27                               ; load attacker-controlled username value
0x100004e30      mov x1, x26
0x100004e34      bl sym.imp.append_...ySSF_                 ; append username directly (no validation)
```

The attacker-controlled value (passed in as `username`) is appended at `0x100004e34` without any sanitization or encoding.

The concatenated string is then passed to `Foundation.URL.string(_:)` at `0x100004e44` to produce a `URL` object. If that `URL` is successfully created (the `b.ne` check at `0x100004e74` succeeds), execution falls through to the `loadFileURL` call.

**Step 2 — Call `loadFileURL:allowingReadAccessToURL:`:**

```text
0x100004f10      ldr x1, [x8, 0x118]   ; reloc.fixup.loadFileURL:allowingReadAccessToURL:
0x100004f14      mov x0, x25           ; WKWebView instance
0x100004f18      mov x2, x26           ; fileURL+?username=<value> (attacker influenced)
0x100004f1c      mov x3, x20           ; docDir (Documents directory)
0x100004f20      bl sym.imp.objc_msgSend
```

`x2` holds the constructed URL containing the unvalidated `username` value. `x3` holds the `docDir` lazy static, which resolves to the app's `Documents` directory (see @MASTG-DEMO-0096 for the analysis of that initializer).

**Step 3 — `innerHTML` injection (HTML/JS side):**

The `innerHTML` injection does not appear as a Swift `evaluateJavaScript` call. Instead, the HTML template is statically embedded in the binary as a string literal. We can confirm this by searching for `innerHTML` with r2, which reports a hit at `0x10000b261` in `output.txt`. The HTML written to disk embeds the following JavaScript:

```javascript
const name = new URLSearchParams(window.location.search).get('username');
document.getElementById('username').innerHTML = name;
```

Because the `username` query parameter is read directly from `window.location.search` and assigned to `innerHTML` without escaping, any HTML or JavaScript event handler the attacker places in the `username` value is rendered as markup by the WebView.

### How to Fix

Replace `innerHTML` with `textContent` in the embedded HTML template so that the `username` value is always treated as plain text, not markup. This prevents the browser from parsing attacker-controlled input as HTML and eliminates the unintended navigation vector.

{{ fix.diff }}

To apply the fix to `MastgTest.swift`, run the following command from this demo directory:

```sh
patch MastgTest.swift -o MastgTest-fixed.swift < fix.diff
```

This code has other issues (e.g. the URL construction is still unsafe), but this change alone is sufficient to prevent the specific vulnerability shown in this demo.
