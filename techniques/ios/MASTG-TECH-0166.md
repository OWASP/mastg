---
title: Identifying Custom URL Scheme Registrations in iOS Apps
platform: ios
---

iOS apps declare custom URL schemes in the `Info.plist` file and handle incoming URLs through delegate methods. This technique covers how to identify registered URL schemes and locate their handler implementations in the app binary.

## Checking Info.plist for Registered URL Schemes

The app bundle's `Info.plist` file lists every custom URL scheme the app registers under `CFBundleURLTypes`. After extracting the IPA using @MASTG-TECH-0054 and @MASTG-TECH-0058, inspect the plist directly with `grep`:

```bash
grep -A 5 CFBundleURLSchemes ./Payload/MASTestApp.app/Info.plist
```

### Checking for Queried URL Schemes

The `LSApplicationQueriesSchemes` key in `Info.plist` lists the URL schemes the app is allowed to query via [`canOpenURL:`](https://developer.apple.com/documentation/uikit/uiapplication/1622952-canopenurl). Inspect it together with `CFBundleURLSchemes`:

```bash
grep -A 5 LSApplicationQueriesSchemes Info.plist
```

## Locating URL Handler Methods in the Binary

After identifying the registered schemes, locate the delegate methods responsible for processing incoming URL requests. The following selectors are commonly found in iOS app binaries.

### Modern Handler (iOS 9+)

[`application:openURL:options:`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1623112-application) is the current delegate method for handling incoming URLs. It receives an `options` dictionary that includes the source application identifier (`UIApplicationOpenURLOptionsSourceApplicationKey`).

### Using @MASTG-TOOL-0129

`rabin2` lists all Objective-C selector strings in the binary:

```bash
rabin2 -zzq MASTestApp | grep -i openurl
```

### Using @MASTG-TOOL-0073

Use flags (`f`) and the `~` filter to find method references by name:

```bash
r2 -qc "aaa; f~openURL:options" MASTestApp
```

Then use `axt` to find cross-references to a flagged address:

```bash
r2 -qc "aaa; axt @ reloc.fixup.application:openURL:options:" MASTestApp
```

Identify where in the binary `UIApplicationOpenURLOptionsSourceApplicationKey` is referenced, indicating the handler reads the source application from the options dictionary:

```bash
r2 -qc "aaa; f~UIApplicationOpenURLOptionsSourceApplicationKey" MASTestApp
```

To disassemble the handler function, use `pdf` at the address returned by `axt`:

```bash
r2 -qc "aaa; pdf @ <handler_address>" MASTestApp
```
