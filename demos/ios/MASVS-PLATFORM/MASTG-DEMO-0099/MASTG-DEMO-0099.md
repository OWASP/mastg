---
platform: ios
title: Runtime Monitoring of WebView File Access with Frida
id: MASTG-DEMO-0099
code: [swift]
test: MASTG-TEST-0336
---

## Sample

This demo uses the same sample as @MASTG-DEMO-0098.

{{ ../MASTG-DEMO-0098/MastgTest.swift }}

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Make sure you have @MASTG-TOOL-0039 installed on your machine and the `frida-server` running on the device.
3. Launch the MASTestApp on the device.
4. Run `run.sh` to attach Frida to the running app.
5. Click the **Start** button in the app to trigger the WebView configuration.
6. Stop the script by pressing `Ctrl+C`.

{{ run.sh # script.js }}

The Frida script hooks several WebKit APIs at runtime and logs their arguments when they are invoked:

- `WKPreferences _setAllowFileAccessFromFileURLs:` to detect when the app enables access from `file://` pages to other local files.
- `WKWebViewConfiguration _setAllowUniversalAccessFromFileURLs:` to detect when the app allows `file://` pages to access any origin.
- `WKPreferences setJavaScriptEnabled:` to determine whether JavaScript execution is enabled in the WebView.
- `WKWebView loadFileURL:allowingReadAccessToURL:` to identify when the app loads local files into a WebView and to capture both the loaded file and the granted read access scope.

## Observation

The output shows that the application enables file access from `file://` URLs in the WebView and loads a local HTML file from a demo directory under the app's caches directory.

{{ output.txt }}

The logs indicate that:

- `allowFileAccessFromFileURLs` is set to `1`.
- `allowUniversalAccessFromFileURLs` remains `0` (disabled).
- The WebView loads a local file such as `.../Library/Caches/demoRoot/index.html`.
- `allowingReadAccessTo` is set to the `demoRoot` directory under the app's caches directory `.../Library/Caches/demoRoot/`.

## Evaluation

The test **fails** because the application enables file access from `file://` URLs for a `WKWebView` that loads local `file://` content from the app's caches directory.

Specifically:

- `allowFileAccessFromFileURLs` is set to `true`, allowing JavaScript running in a `file://` page to access other local files within the granted read scope.
- The WebView loads a local HTML file and grants read access to the `demoRoot` directory under the application's caches directory.

These settings weaken the isolation normally applied to local content and increase the impact of WebView vulnerabilities. If attacker-controlled JavaScript executes in the local page context, it may access files within the granted read scope and potentially exfiltrate them to remote servers.
