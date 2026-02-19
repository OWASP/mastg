---
title: Inspecting iOS App Permissions
platform: ios
---

iOS app permissions and capabilities are declared through three mechanisms: purpose strings in the `Info.plist` file, code-signing entitlements, and embedded provisioning profiles. Purpose strings explain to users why the app needs access to sensitive resources like location, camera, or contacts. Code-signing entitlements specify what capabilities the app is allowed to use at runtime. The embedded provisioning profile links the app to an Apple developer account and grants additional entitlements based on the developer's capabilities.

## Using @MASTG-TOOL-0062

The `plutil` tool converts and inspects property list files. Use it to examine purpose strings declared in the `Info.plist` file. These strings explain why the app requests access to sensitive resources.

!!! note
    The `plistutil` binary is part of the @MASTG-TOOL-0126 and can be used for plist conversion and inspection.

Convert `Info.plist` to XML format for keys containing `UsageDescription`:

```bash
plutil -convert xml1 -o - Info.plist | grep -i -A 1 UsageDescription
```

Example purpose strings in XML format:

```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>Your location is used to provide turn-by-turn directions to your destination.</string>
<key>NSCameraUsageDescription</key>
<string>We want to access your camera</string>
<key>NSHealthClinicalHealthRecordsShareUsageDescription</key>
<string>Share your health data with us!</string>
```

## Using @MASTG-TOOL-0063

To inspect the embedded provisioning profile (usually at `Payload/<appname>.app/embedded.mobileprovision`), use the `security` tool on macOS. This file is encoded in Cryptographic Message Syntax (CMS) format and contains the Entitlements dictionary.

```bash
security cms -D -i Payload/MyApp.app/embedded.mobileprovision > decoded.mobileprovision.xml
```

Then search for the Entitlements dictionary to review all granted capabilities:

Example output:

```xml
<key>Entitlements</key>
<dict>
    <key>com.apple.developer.homekit</key>
    <true/>
    <key>com.apple.security.application-groups</key>
    <array>
        <string>group.com.example.myapp</string>
    </array>
</dict>
```

The output lists all entitlements granted to the app by Apple's provisioning system.

## Using @MASTG-TOOL-0114

If you only have the app's IPA or an installed app on a jailbroken device, extract the entitlements directly from the signed app binary using the `codesign` tool. This is useful when `.entitlements` files or the `embedded.mobileprovision` file are not separately accessible.

```bash
codesign -d --entitlements :- Payload/MyApp.app/MyApp
```

This prints the entitlements plist to stdout:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>com.apple.developer.homekit</key>
    <true/>
    <key>application-identifier</key>
    <string>ABC123DEF456.com.example.myapp</string>
</dict>
</plist>
```

## Locating Permission APIs in Source Code

Search the app's source code for framework-specific classes and methods that request or query permission status. The following list covers the most common iOS permission APIs:

- **Location**: [`CLLocationManager`](https://developer.apple.com/documentation/corelocation/cllocationmanager) — `requestWhenInUseAuthorization()`, `requestAlwaysAuthorization()`, `authorizationStatus`
- **Camera/Microphone**: [`AVCaptureDevice`](https://developer.apple.com/documentation/avfoundation/avcapturedevice) — `requestAccess(for:completionHandler:)`, `authorizationStatus(for:)`
- **Contacts**: [`CNContactStore`](https://developer.apple.com/documentation/contacts/cncontactstore) — `requestAccess(for:completionHandler:)`, `authorizationStatus(for:)`
- **Calendar**: [`EKEventStore`](https://developer.apple.com/documentation/eventkit/ekeventstore) — `requestFullAccessToEvents(completion:)`, `authorizationStatus(for:)`
- **Photos**: [`PHPhotoLibrary`](https://developer.apple.com/documentation/photokit/phphotolibrary) — `requestAuthorization(for:handler:)`, `authorizationStatus(for:)`
- **Bluetooth**: [`CBCentralManager`](https://developer.apple.com/documentation/corebluetooth/cbcentralmanager) — `authorization`, [`state`](https://developer.apple.com/documentation/corebluetooth/cbmanager/1648600-state)
- **Health**: [`HKHealthStore`](https://developer.apple.com/documentation/healthkit/hkhealthstore) — `requestAuthorization(toShare:read:completion:)`
- **Notifications**: [`UNUserNotificationCenter`](https://developer.apple.com/documentation/usernotifications/unusernotificationcenter) — `requestAuthorization(options:completionHandler:)`
- **Motion**: [`CMMotionActivityManager`](https://developer.apple.com/documentation/coremotion/cmmotionactivitymanager) — `startActivityUpdates(to:withHandler:)`, `authorizationStatus()`
- **Siri**: [`INPreferences`](https://developer.apple.com/documentation/sirikit/inpreferences) — `requestSiriAuthorization(_:)`, `siriAuthorizationStatus()`

Use `grep -rn "requestWhenInUseAuthorization\|requestAlwaysAuthorization\|requestAccess"` or your IDE's search to locate usages of these classes and their authorization methods across the codebase.

## Using @MASTG-TOOL-0039

Frida can be used to dynamically trace permission API calls at runtime. This allows you to observe which permissions are actually requested when the app is running and under what conditions.

Create a Frida script to hook permission APIs:

```javascript
'use strict';

function traceLocationPermission() {
    var CLLocationManager = ObjC.classes.CLLocationManager;
    if (!CLLocationManager) return;

    var statusMap = {0: "NotDetermined", 1: "Restricted", 2: "Denied", 3: "AuthorizedAlways", 4: "AuthorizedWhenInUse"};

    try {
        Interceptor.attach(CLLocationManager['- requestWhenInUseAuthorization'].implementation, {
            onEnter: function(args) {
                console.log("[Location] Requesting...");
            }
        });
    } catch(e) {}

    try {
        Interceptor.attach(CLLocationManager['- requestAlwaysAuthorization'].implementation, {
            onEnter: function(args) {
                console.log("[Location] Requesting Always...");
            }
        });
    } catch(e) {}

    setTimeout(function() {
        try {
            var resolver = new ApiResolver('objc');

            resolver.enumerateMatches('-[* locationManagerDidChangeAuthorization:]').forEach(function(match) {
                Interceptor.attach(match.address, {
                    onEnter: function(args) {
                        var manager = ObjC.Object(args[2]);
                        var s = Number(manager.authorizationStatus());
                        var statusStr = statusMap[s] || "Unknown(" + s + ")";
                        var granted = s === 3 || s === 4;
                        console.log("[Location] " + statusStr + " | " + (granted ? "GRANTED" : "DENIED"));
                    }
                });
            });
        } catch(e) {
            console.log("  [!] Location delegate hook error: " + e);
        }
    }, 500);
}

if (ObjC.available) {
    traceLocationPermission();
}
```

Run the script:

```bash
frida -U -f com.example.app -l script.js
```

The output shows which permission APIs are called during app execution, to verify that declared permissions match actual runtime behavior.
