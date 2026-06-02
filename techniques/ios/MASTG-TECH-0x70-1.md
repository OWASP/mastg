---
title: Testing Universal Links
platform: ios
---

Universal Links allow iOS apps to handle HTTPS URLs and route them directly into the app rather than opening a browser. Testing Universal Link handling involves both static analysis of the app's entitlements and binary, and dynamic analysis to trace how incoming and outgoing URLs are processed at runtime.

## Using @MASTG-TOOL-0126

Extract the app's entitlements using @MASTG-TECH-0111 and save the output to `entitlements.plist`. Then use @MASTG-TOOL-0126 to parse it and inspect the [`com.apple.developer.associated-domains`](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_developer_associated-domains) key for overly permissive wildcards (e.g., `applinks:*`) or domains that do not belong to the application.

```bash
plistutil -i entitlements.plist -f xml | grep -A 3 "com.apple.developer.associated-domains"
```

Example output showing a wildcard entitlement:

```xml
<key>com.apple.developer.associated-domains</key>
<array>
    <string>applinks:*.example.com</string>
</array>
```

## Using @MASTG-TOOL-0129

Use @MASTG-TOOL-0129 to extract Objective-C selectors from the compiled app binary. Two selectors are relevant for Universal Links:

- `scene:continueUserActivity:`: The `UIWindowSceneDelegate` method that receives incoming Universal Links as `NSUserActivity` objects.
- `openURL:options:completionHandler:`: The [`UIApplication.open(_:options:completionHandler:)`](https://developer.apple.com/documentation/uikit/uiapplication/1648685-open) method used to hand off URLs to the operating system or other apps.

Search for the Universal Link receiver selector:

```bash
rabin2 -zq Payload/MASTestApp.app/MASTestApp | grep "continueUserActivity"
```

Output:

```plaintext
0x10000a350 28 27 scene:continueUserActivity:
```

Search for the outgoing URL selector:

```bash
rabin2 -zq Payload/MASTestApp.app/MASTestApp | grep openURL
```

Output:

```plaintext
0x10000b37c 35 34 openURL:options:completionHandler:
```

The presence of these selectors confirms the app handles Universal Links. Use @MASTG-TECH-0048 to decompile the binary and inspect the implementation of `scene:continueUserActivity:`, checking whether the app validates the host, path, and query parameters of the incoming `NSUserActivity.webpageURL` using [`URLComponents`](https://developer.apple.com/documentation/foundation/urlcomponents) before routing the user or modifying application state.

## Using @MASTG-TOOL-0039

@MASTG-TOOL-0039 can be used for dynamic analysis: triggering Universal Links and tracing the receiver and outgoing URL methods.

### Triggering Universal Links

Use @MASTG-TECH-0067 to programmatically call `UIApplication.sharedApplication().openURL_()` to route an arbitrary URL through the system. Note that `openURL` returning `true` only means the system accepted the routing request (for example, by opening Safari for `https://` URLs). It does not confirm that the AASA domain check was bypassed or that the app's Universal Link receiver method executed. Use the tracing hooks in the following section to confirm whether the receiver ran.

```bash
frida -U -f org.owasp.mastestapp.MASTestApp-iOS -l script.js
```

Example `script.js`:

```javascript
if (ObjC.available) {

    ObjC.schedule(ObjC.mainQueue, function () {
        console.log("[*] Triggering URL via Frida...");

        var UIApplication = ObjC.classes.UIApplication.sharedApplication();

        var targetUrl = "https://attacker.example.com/reset_password?token=malicious_123";
        var toOpen = ObjC.classes.NSURL.URLWithString_(targetUrl);
        var result = UIApplication.openURL_(toOpen);

        console.log("[+] UIApplication.openURL_ executed.");
        console.log("[+] Target: " + targetUrl);
        console.log("[+] Result: " + result);
    });
} else {
    console.log("[-] Objective-C runtime is not available.");
}
```

Output:

```plaintext
Spawned `org.owasp.mastestapp.MASTestApp-iOS`. Resuming main thread!
[iPhone::org.owasp.mastestapp.MASTestApp-iOS ]-> [*] Triggering URL via Frida...
[+] UIApplication.openURL_ executed.
[+] Target: https://attacker.example.com/reset_password?token=malicious_123
[+] Result: true
```

The `Result: true` only confirms the OS accepted the routing request, not that the Universal Link was delivered to the app's receiver. See the tracing section below to confirm receiver execution.

### Tracing the Receiver and Outgoing URLs

Use @MASTG-TECH-0095 to hook the `NSUserActivity` class and intercept calls to the `webpageURL` selector. This reveals the exact URL the app extracts from the OS, confirming whether untrusted payloads reach the receiver without validation.

```bash
frida -U -f org.owasp.mastestapp.MASTestApp-iOS -l script.js
```

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

Hook `-[UIApplication openURL:options:completionHandler:]` to capture every URL the application passes to the operating system. This reveals whether the app constructs outgoing URLs from unvalidated inbound data, making it vulnerable to URI Scheme Hijacking.

```bash
frida -U -f org.owasp.mastestapp.MASTestApp-iOS -l script.js
```

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

Retrieve the `apple-app-site-association` (AASA) file from the server using the associated domains obtained in `entitlements.plist`. The file must be served over HTTPS without redirects.

Apple checks two paths on the domain's server:

- `https://<domain>/apple-app-site-association`
- `https://<domain>/.well-known/apple-app-site-association`

You can retrieve it via your browser, or using the command line:

```bash
curl -s https://<domain>/.well-known/apple-app-site-association
```

To validate the response without relying on a third-party service, compare it with Apple's [Universal Links documentation](https://developer.apple.com/documentation/xcode/supporting-universal-links-in-your-app) and parse it locally to confirm that it is valid JSON and contains the expected keys.

```bash
curl -s https://<domain>/.well-known/apple-app-site-association | jq .
```
