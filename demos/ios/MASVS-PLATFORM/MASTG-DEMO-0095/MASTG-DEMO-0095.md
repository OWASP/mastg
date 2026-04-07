---
platform: ios
title: Attacker Controlled Input in a WebView Leading to Unintended Navigation
code: [swift]
id: MASTG-DEMO-0095
test: MASTG-TEST-0332
kind: fail
---

## Sample

This sample demonstrates how attacker controlled input inside a WebView can alter the rendered page and trigger unintended navigation. The app loads a trusted local HTML file, but the page reads the `username` parameter from the URL and injects it into the DOM using `innerHTML`.

Although the app uses `webView.loadFileURL(urlWithUsername, allowingReadAccessTo: docDir)`, broad file read access is not the focus of this demo. The issue demonstrated here is that attacker controlled input is rendered as HTML, which allows the attacker to inject content that changes page behavior and causes unintended navigation.

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
7. As a result, the WebView navigates to an attacker chosen destination.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ load_webview.r2 }}

{{ run.sh }}

## Observation

The output contains the disassembled code of the function using `loadFileURL(urlWithUsername, allowingReadAccessTo: docDir)`. This function is large and complex, so to simplify the analysis, we can use an LLM to assist with reverse engineering the application.

!!! note "About `ai-decompiled.swift`"
    The `ai-decompiled.swift` file is an AI-assisted reconstruction derived from `function.asm` and is provided only as a convenience for understanding the logic. It may be inaccurate or incomplete; the assembly in `function.asm` and the original binary are the authoritative sources for analysis.
{{ output.txt # function.asm # ai-decompiled.swift }}

1. On **lines 6–8**, the function constructs a URL from a user-modifiable `username` argument.
2. On **lines 8**, this user-controlled value is appended to the hardcoded base string meaning the final URL string is dynamically constructed rather than fixed.
3. On **line 11**, the application creates a `URL` object directly from this concatenated string without validating the resulting host, path, or structure.
4. On **line 21**, the constructed request is passed to `WKWebView.loadFileURL`, allowing a user who can alter `username` to influence the URL that is ultimately loaded.

## Evaluation

The test case fails if attacker-controllable input (such as the `username` parameter) is used to construct the `URL` or `URLRequest` passed to any `WKWebView.load*` method, and the resulting URL is not validated against an allowlist of expected schemes, hosts, or paths.
