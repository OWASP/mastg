---
platform: ios
title: References to File Access in WebViews with radare2
id: MASTG-DEMO-0098
code: [swift]
test: MASTG-TEST-0335
---

## Sample

This sample demonstrates a WKWebView with the undocumented `allowFileAccessFromFileURLs` property enabled, allowing JavaScript to read other local `file://` URLs even when `loadFileURL:allowingReadAccessToURL:` is intentionally granting broad access to the `demoRoot` directory. This is required because in the end, the sandbox always applies and this API is the one controlling it.

{{ MastgTest.swift }}

The sample sets up two sibling directories under `cachesDirectory/demoRoot/`:

- `app/`: contains `index.html` (the loaded page) and `api-key.txt` (a sensitive secret).
- `other/`: contains `other.html` (a page in a separate directory).

Using KVC, `allowFileAccessFromFileURLs` is set to `true` and `allowUniversalAccessFromFileURLs` is left `false`. The WebView loads `index.html` with `loadFileURL(_:allowingReadAccessTo:)`, intentionally passing `demoRoot` to the `allowingReadAccessTo` parameter. This means that JavaScript running in the WebView should be able to read any file in `demoRoot` (including `index.html` and `api-key.txt`).

`index.html` contains JavaScript that demonstrates three access methods:

- `fetch("./api-key.txt")`: reads `api-key.txt` from the same `app/` directory.
- `XMLHttpRequest` to `../other/other.html`: reads a file from the sibling `other/` directory.
- `<iframe src="../other/other.html">`: embeds the sibling file directly.

The `allowingReadAccessTo: demoRoot` choice is intentional: it shows that simply setting `allowFileAccessFromFileURLs = true` is not enough for JavaScript to reach any local `file://` URL via `fetch` or XHR. The `allowingReadAccessTo` parameter is not the target of this demo but it's required to demonstrate the impact in terms of the sandbox boundary. If the WebView were loaded with `allowingReadAccessTo: indexURL` instead, then JavaScript would only be able to read `index.html` and not `api-key.txt`, even with `allowFileAccessFromFileURLs = true`.

The app logs would show:

```sh
0x1110180c0 - [PID=709] WebProcessProxy::checkURLReceivedFromWebProcess: Received an unexpected URL from the web process
0x103870c18 - [pageProxyID=6, webPageID=7, PID=709] WebPageProxy::Ignoring request to load this main resource because it is outside the sandbox
```

## Steps

1. Extract the app binary from the IPA (@MASTG-TECH-0054).
2. Run @MASTG-TOOL-0073 (radare2) using the provided script to search for references to the relevant WebView methods.

{{ webview_file_access.r2 # run.sh }}

The script searches for:

- References to `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs`.
- References to the `loadFileURL:allowingReadAccessToURL:` method.

## Observation

The output shows all cross-references and disassembled snippets.

{{ output.txt # function.asm # function2.asm }}

## Evaluation

The test **fails** because the binary contains a reference to `allowFileAccessFromFileURLs` that is set to `true`, and the binary also calls `loadFileURL:allowingReadAccessToURL:`.

In `sym.func.100004ad8` (the `showWebView` function), around `0x100004b54`, we can see that the app sets `allowFileAccessFromFileURLs = true`. This follows the classic Swift-to-Objective-C bridging pattern:

- `mov w0, 1` loads boolean `true`, then bridges it to an `NSNumber` via `objc_retainAutoreleasedReturnValue`.
- At `0x100004b64`, `"allowFileAccessFromFileURLs"` is constructed as an `NSString`.
- `setValue:forKey:` is called via `fcn.10000c780` at `0x100004b8c`.

Immediately after, around `0x100004b9c`, the app sets `allowUniversalAccessFromFileURLs = false` using the same pattern:

- `mov w0, 0` loads boolean `false`, bridged to `NSNumber`.
- At `0x100004bac`, `"allowUniversalAccessFromFileURLs"` is constructed as an `NSString`.
- `setValue:forKey:` is called at `0x100004bd0`.

The actual call to `loadFileURL:allowingReadAccessToURL:` is inside the `presenter.present` completion closure, compiled into `sym.func.1000050c0`. Earlier in this function, around `0x100005130`, the `demoRoot` static property is resolved and stored in `x23`:

```text
0x100005130      adrp x1, segment.__DATA                   ; 0x100014000
0x100005134      add x1, x1, 0x340                         ; demoRoot storage
0x100005138      mov x0, x21
0x10000513c      bl sym.func.100007be4                      ; resolve demoRoot static
0x100005140      mov x23, x0                               ; x23 = demoRoot
```

Then the key sequence leading to the `loadFileURL:allowingReadAccessToURL:` call at `0x10000520c` is:

```text
0x1000051a4      adrp x1, segment.__DATA                   ; 0x100014000
0x1000051a8      add x1, x1, 0x300                         ; indexURL storage
0x1000051ac      mov x0, x21
0x1000051b0      bl sym.func.100007be4                      ; resolve indexURL static
0x1000051b4      mov x1, x0                               ; x1 = indexURL (passed directly)
0x1000051b8      mov x0, x22
0x1000051bc      mov x2, x21
0x1000051c0      blr x27                                   ; initialize Swift URL in x22 from indexURL
0x1000051c8      bl Foundation.URL._bridgeToObjectiveC.NSURL  ; bridge to NSURL
0x1000051cc      mov x24, x0                               ; x24 = NSURL(indexURL) [fileURL arg]
...
0x1000051d8      blr x28                                   ; destroy URL buffer
0x1000051dc      mov x0, x22
0x1000051e0      mov x1, x23                               ; x1 = x23 = demoRoot (from earlier)
0x1000051e4      mov x2, x21
0x1000051e8      blr x27                                   ; initialize Swift URL in x22 from demoRoot
0x1000051ec      bl Foundation.URL._bridgeToObjectiveC.NSURL  ; bridge to NSURL
0x1000051f0      mov x20, x0                               ; x20 = NSURL(demoRoot) [readAccess arg]
...
0x100005204      mov x2, x24                               ; fileURL = NSURL(indexURL)
0x100005208      mov x3, x20                               ; allowingReadAccessTo = NSURL(demoRoot)
0x10000520c      bl fcn.10000c680                          ; loadFileURL:allowingReadAccessToURL:
```

`x2` and `x3` are produced from **two different** static properties. The first URL (`x24`) comes from the `indexURL` static (resolved at `0x1000051b0` from `[segment.__DATA + 0x300]`), while the second URL (`x20`) comes from the `demoRoot` static (resolved earlier at `0x10000513c` from `[segment.__DATA + 0x340]` and stored in `x23`). This confirms that `loadFileURL` is called with `indexURL` as the first argument (the file to load) and `demoRoot` as the second argument (the read-access boundary), as written in the Swift source.

Because `allowingReadAccessTo: demoRoot` grants WebKit native file access to the entire `demoRoot` directory, and `allowFileAccessFromFileURLs = true` independently grants JavaScript the ability to issue `fetch()` and `XMLHttpRequest` calls to `file://` URLs within that boundary, JavaScript running in the WebView can read both `api-key.txt` and `other.html`.

!!! warning "iOS Simulator vs. Physical Device"
    The iOS Simulator is **more permissive** than a physical device when it comes to `file://` access in WebViews. On the Simulator, JavaScript can reach local files via `fetch` or XHR even when `allowingReadAccessTo` is restricted to a single file (e.g., `indexURL`). On a **physical device**, WebKit strictly enforces the sandbox boundary set by `allowingReadAccessTo`, so JavaScript can only access files within the directory (or file) specified by that parameter. Always validate WebView file-access behavior on a real device to get accurate results.
