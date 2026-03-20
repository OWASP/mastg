---
title: Testing Universal Links
platform: ios
---

Universal Links allow iOS apps to handle HTTPS URLs and route them directly into the app rather than opening a browser. Testing Universal Link handling involves both static analysis of the app's entitlements and binary, and dynamic analysis to trace how incoming and outgoing URLs are processed at runtime.

## Static Analysis

### Extracting the Associated Domains Entitlement

The [`com.apple.developer.associated-domains`](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_developer_associated-domains) entitlement lists the domains the app is authorized to handle as Universal Links. Extract the app's entitlements using @MASTG-TECH-0058 and inspect the `com.apple.developer.associated-domains` key for overly permissive wildcards (e.g., `applinks:*`) or domains that do not belong to the application.

### Identifying Objective-C Selectors in the Binary

Use @MASTG-TECH-0047 to extract Objective-C selectors from the compiled app binary. Two selectors are relevant for Universal Links:

- `application:continue:restorationHandler:` — the [`UIApplicationDelegate`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1623072-application) method that receives incoming Universal Links as `NSUserActivity` objects.
- `openURL:options:completionHandler:` — the [`UIApplication.open(_:options:completionHandler:)`](https://developer.apple.com/documentation/uikit/uiapplication/1648685-open) method used to hand off URLs to the operating system or other apps.

The presence of these selectors confirms the app contains Universal Link entry points that require further review.

### Reviewing the URL Handler Implementation

Use @MASTG-TECH-0048 to decompile the binary and inspect the implementation of `application:continue:restorationHandler:`. Check whether the app validates the host, path, and query parameters of the incoming `NSUserActivity.webpageURL` using [`URLComponents`](https://developer.apple.com/documentation/foundation/urlcomponents) or equivalent before routing the user or modifying application state.

## Dynamic Analysis

### Injecting Universal Link Payloads

Use @MASTG-TECH-0067 to inject a Frida script into the app's memory space and programmatically call the non-public iOS URL routing API `UIApplication.sharedApplication().openURL_()`. This bypasses the OS-level AASA domain check and forces the app to evaluate an arbitrary URL payload, allowing testers to observe whether the application accepts attacker-controlled links that would otherwise be filtered.

### Tracing the Universal Link Receiver

Use @MASTG-TECH-0095 to hook the `NSUserActivity` class and intercept calls to the `webpageURL` selector. This reveals the exact raw URL the app extracts from the OS and actively processes, confirming whether untrusted or malicious payloads reach the application's internal routing logic without prior validation.

### Tracing Outgoing URL Requests

Use @MASTG-TECH-0095 to hook `-[UIApplication openURL:options:completionHandler:]` and capture every URL the application passes to the operating system. This reveals whether the app constructs outgoing URLs from unvalidated inbound data, making it vulnerable to URI Scheme Hijacking.

## Using @MASTG-TOOL-0039

@MASTG-TOOL-0039 can be used for all dynamic analysis steps described above. Attach to the running app process or spawn it using `frida-ps` to identify the process name, then load a JavaScript hook script:

```bash
frida -U -f <bundle-id> -l script.js
```

To trace the Universal Link receiver:

```javascript
var NSUserActivity = ObjC.classes.NSUserActivity;
Interceptor.attach(NSUserActivity["- webpageURL"].implementation, {
  onLeave: function (retval) {
    if (!retval.isNull()) {
      var url = new ObjC.Object(retval).absoluteString().toString();
      console.log("[Receiver] webpageURL -> " + url);
    }
  }
});
```

To trace outgoing URL requests:

```javascript
var UIApplication = ObjC.classes.UIApplication;
Interceptor.attach(
  UIApplication["- openURL:options:completionHandler:"].implementation,
  {
    onEnter: function (args) {
      var url = new ObjC.Object(args[2]).absoluteString().toString();
      console.log("[Outgoing] openURL -> " + url);
    }
  }
);
```

## Validating the Live AASA File

In addition to in-app analysis, verify the live server configuration by fetching the [`apple-app-site-association`](https://developer.apple.com/documentation/xcode/supporting-associated-domains) file directly from the domain listed in the entitlement using standard network interception techniques (@MASTG-TECH-0059):

```bash
curl -s https://<domain>/.well-known/apple-app-site-association
```

Inspect the response to confirm it is served over HTTPS, contains the correct Team ID and Bundle ID in the `appIDs` array, and does not use overly permissive path wildcards for sensitive routes.
