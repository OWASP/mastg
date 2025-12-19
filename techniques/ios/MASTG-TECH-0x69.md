---
title: Obtaining App Permissions
platform: ios
---

iOS app permissions and capabilities are declared through three mechanisms: purpose strings in the `Info.plist` file, code-signing entitlements, and embedded provisioning profiles. Purpose strings explain to users why the app needs access to sensitive resources like location, camera, or contacts. Code-signing entitlements specify what capabilities the app is allowed to use at runtime. The embedded provisioning profile links the app to an Apple developer account and grants additional entitlements based on the developer's capabilities.

## Using @MASTG-TOOL-0062

The `plutil` tool converts and inspects property list files. Use it to examine purpose strings declared in the `Info.plist` file. These strings explain why the app requests access to sensitive resources.

> **Note:** The `plistutil` binary is part of the `libimobiledevice` suite and can be used for plist conversion and inspection.

Convert `Info.plist` to XML format:

```bash
plutil -convert xml1 Payload/MyApp.app/Info.plist -o -
```

Alternatively, filter directly for keys containing `UsageDescription`:

```bash
plutil -i Info.plist -f xml | grep -i UsageDescription
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

Verify that granted entitlements match the app's declared functionality.

## Using @MASTG-TOOL-0114

If you only have the app's IPA or an installed app on a jailbroken device, extract the entitlements directly from the signed app binary using the `codesign` tool. This is useful when `.entitlements` files or the `embedded.mobileprovision` file are not separately accessible.

```bash
codesign -d --entitlements :- Payload/MyApp.app
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

## Reviewing Permission Usage in Source Code

Perform a manual source code review to verify how permissions and capabilities are used in the app. Examine the code to ensure that declared permissions match their actual usage and that no sensitive data is unnecessarily exposed.

When reviewing source code, check for:

- Whether declared purpose strings in `Info.plist` correspond to actual permission requests in the code.
- Whether the app properly checks authorization status before accessing protected resources.
- Whether permission related data flows are secure and not unnecessarily logged or transmitted.
- Whether the app verifies user preferences when accessing sensitive features.

Look for common authorization patterns in iOS frameworks:

- Bluetooth: check calls to `CBCentralManager` and verification of [`state`](https://developer.apple.com/documentation/corebluetooth/cbmanager/1648600-state?language=objc "CBManager state") property
- Location: search for `CLLocationManager` usage, particularly [`locationServicesEnabled()`](https://developer.apple.com/documentation/corelocation/cllocationmanager/1423648-locationservicesenabled?language=objc "CLLocationManager locationServicesEnabled") calls
- Camera/Microphone: look for `AVCaptureDevice` authorization checks
- Health data: verify `HKHealthStore` authorization requests

Example authorization check pattern:

```swift
func checkForLocationServices() {
    if CLLocationManager.locationServicesEnabled() {
        // Location services are available, proceed with location access.
    } else {
        // Location services disabled, update UI accordingly.
    }
}
```

Review how the app handles data obtained through permissions. If sensitive data is stored locally, verify proper encryption. If transmitted over the network, ensure TLS/SSL is used.

See the [Apple Developer Documentation](https://developer.apple.com/documentation/corelocation/adding_location_services_to_your_app "Getting the availability of Core Location services") for authorization patterns for each framework.

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

    setTimeout(function() {
        try {
            var resolver = new ApiResolver('objc');
            resolver.enumerateMatches('-[* locationManager:didChangeAuthorizationStatus:]').forEach(function(match) {
                Interceptor.attach(match.address, {
                    onEnter: function(args) {
                        var status = new NativePointer(args[3]).toInt32();
                        var statusStr = statusMap[status] || "Unknown(" + status + ")";
                        var granted = status === 3 || status === 4;
                        console.log("[Location] " + statusStr + " | " + (granted ? "GRANTED" : "DENIED"));
                    }
                });
            });
        } catch(e) {}
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

The output shows which permission APIs are called during app execution, helping verify that declared permissions match actual runtime behavior.
