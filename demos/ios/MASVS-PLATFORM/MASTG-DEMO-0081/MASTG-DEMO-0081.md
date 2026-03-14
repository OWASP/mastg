---
platform: ios
title: References to File Access in WebViews with radare2
id: MASTG-DEMO-0081
code: [swift]
test: MASTG-TEST-0318
---

## Sample

This sample demonstrates a WKWebView with file access enabled via the undocumented properties `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs`.

{{ MastgTest.swift }}

The sample:

- Creates a `WKWebView` instance with custom configuration.
- Uses Key-Value Coding (KVC) to set the undocumented properties:
  - `allowFileAccessFromFileURLs` is set to `true`, allowing JavaScript to access other local files.
  - `allowUniversalAccessFromFileURLs` is set to `true`, allowing JavaScript to access content from any origin.
- Loads a local HTML file that could potentially access and exfiltrate sensitive data.

## Steps

1. Extract the app binary from the IPA (@MASTG-TECH-0054).
2. Run @MASTG-TOOL-0129 (rabin2) to search for references to the relevant WebView methods.

{{ run.sh }}

The script searches for:

- References to `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs`.
- References to the `loadFileURL:allowingReadAccessToURL:` method.

## Observation

The output shows all cross-references and disassembled snippets.

{{ output.txt }}

## Evaluation

The test **fails** because the binary contains references to `allowFileAccessFromFileURLs` and `allowUniversalAccessFromFileURLs`, and both are set to `true`. The binary also calls `loadFileURL:allowingReadAccessToURL:`, which indicates that the app loads local files into a `WKWebView`.

Around `0x1000046f0`, we can see that the app sets `allowFileAccessFromFileURLs = true`. This is done using the classic Swift and Objective-C bridging pattern:

- create an `NSNumber` from boolean `1`
- create an `NSString` for the key `"allowFileAccessFromFileURLs"`
- call `setValue:forKey:` at `0x100004720`

The same pattern shows that `allowUniversalAccessFromFileURLs` is also set to `true` around `0x100004740`:

- create an `NSNumber` from boolean `1`
- create an `NSString` for the key `"allowUniversalAccessFromFileURLs"`
- call `setValue:forKey:` at `0x100004768`

We can see the call to `loadFileURL:allowingReadAccessToURL:` at `0x100004eb8`. However, we need to inspect the surrounding code to determine which path is being granted read access.

In `sym.func.100004000`, the key sequence is this:

```text
0x100004070      ldr x0, [0x1000101f0]      ; NSFileManager
0x10000407c      ldr x1, [0x100010178]      ; defaultManager
...
0x100004094      ldr x1, [0x100010180]
0x100004098      mov w2, 9
0x10000409c      mov w3, 1
0x1000040a0      bl sym.imp.objc_msgSend
```

This is the pattern for calling:

`FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)`

because:

- `9` is `NSDocumentDirectory`
- `1` is `NSUserDomainMask`

The function then bridges the returned `NSArray` to a Swift array and takes the first element. That resulting URL is stored in the global slot at `0x100010288`.

Later, this global slot is used as the second argument to `loadFileURL:allowingReadAccessToURL:`, which means the `readAccessURL` is the app's `Documents` directory.

The file loaded into the WebView is derived from the same base directory. In `sym.func.10000413c`, the constant `index.html` is constructed and used to initialize the global slot at `0x1000102a8`. This slot is later passed as the first argument to `loadFileURL:allowingReadAccessToURL:`. Therefore, the app loads `index.html` from the `Documents` directory and grants the WebView read access to the entire `Documents` directory.

You can find the enum values above in `/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/System/Library/Frameworks/Foundation.framework/Headers/NSPathUtilities.h`.

```c
typedef NS_ENUM(NSUInteger, NSSearchPathDirectory) {
    NSApplicationDirectory = 1,             // supported applications (Applications)
    NSDemoApplicationDirectory,             // unsupported applications, demonstration versions (Demos)
    NSDeveloperApplicationDirectory,        // developer applications (Developer/Applications). DEPRECATED - there is no one single Developer directory.
    NSAdminApplicationDirectory,            // system and network administration applications (Administration)
    NSLibraryDirectory,                     // various documentation, support, and configuration files, resources (Library)
    NSDeveloperDirectory,                   // developer resources (Developer) DEPRECATED - there is no one single Developer directory.
    NSUserDirectory,                        // user home directories (Users)
    NSDocumentationDirectory,               // documentation (Documentation)
    NSDocumentDirectory,                    // documents (Documents)
...
typedef NS_OPTIONS(NSUInteger, NSSearchPathDomainMask) {
    NSUserDomainMask = 1,       // user's home directory --- place to install user's personal items (~)
    NSLocalDomainMask = 2,      // local to the current machine --- place to install items available to everyone on this machine (/Library)
    NSNetworkDomainMask = 4,    // publicly available location in the local area network --- place to install items available on the network (/Network)
    NSSystemDomainMask = 8,     // provided by Apple, unmodifiable (/System)
    NSAllDomainsMask = 0x0ffff  // all domains: all of the above and future items
};
```