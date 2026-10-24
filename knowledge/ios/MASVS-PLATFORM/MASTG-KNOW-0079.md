---
masvs_category: MASVS-PLATFORM
platform: ios
title: Custom URL Schemes
---

Custom URL schemes allow iOS apps to receive requests from other apps or web pages via a custom URI protocol (for example, `myapp://action?param=value`). An app declares the schemes it handles and processes incoming URLs through delegate methods. Apple documentation is available at [Defining a Custom URL Scheme for Your App](https://developer.apple.com/documentation/xcode/defining-a-custom-url-scheme-for-your-app "Defining a Custom URL Scheme for Your App").

## Registering a Custom URL Scheme

### Source Code

In Xcode, registered URL schemes appear on the app target's **Info** tab under **URL Types**. Underneath, they are stored in the `Info.plist` as a `CFBundleURLTypes` array:

```xml
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLName</key>
        <string>com.example.myapp</string>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>myapp</string>
        </array>
    </dict>
</array>
```

Each entry can declare one or more scheme strings under `CFBundleURLSchemes`. The `CFBundleURLName` is an optional reverse-DNS identifier for the scheme owner.

### Compiled App Bundle

In an IPA or installed app bundle, the same values are found in `Info.plist` at the root of the `.app` directory. They can be inspected as described in @MASTG-TECH-0166.

### Scheme Collision

If multiple apps register the same URL scheme, iOS routes incoming requests to one of them without a guaranteed resolution order. See [Registering Custom URL Schemes](https://developer.apple.com/library/archive/documentation/iPhone/Conceptual/iPhoneOSProgrammingGuide/Inter-AppCommunication/Inter-AppCommunication.html#//apple_ref/doc/uid/TP40007072-CH6-SW7 "Registering Custom URL Schemes") for details.

## Handling Incoming URLs

All incoming URL requests are delivered to the app delegate. The system calls the delegate method that best matches the set of methods the app has implemented.

### `application:openURL:options:` (iOS 9+, current)

```swift
func application(_ app: UIApplication,
                 open url: URL,
                 options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> Bool
```

This is the current method for handling incoming URLs. The `options` dictionary carries contextual metadata:

| Key | Description |
| --- | --- |
| `UIApplication.OpenURLOptionsKey.sourceApplication` | Bundle identifier of the app that sent the request, or `nil` if not available. |
| `UIApplication.OpenURLOptionsKey.annotation` | Property-list value supplied by the originating app (optional). |
| `UIApplication.OpenURLOptionsKey.openInPlace` | Boolean indicating whether the URL refers to a file that should be opened in place. |

The `sourceApplication` key provides the caller's bundle identifier. It is populated by UIKit when the caller used `openURL:options:completionHandler:`. It may be `nil` when the URL is opened by the system (for example, from a web browser or a universal link redirect) or when the originating app did not supply an identifier. See [application(_:open:options:)](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1623112-application) in the Apple developer documentation.

### Deprecated Delegate Methods

The following delegate methods are deprecated and receive no `options` dictionary:

- [`application:handleOpenURL:`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1622964-application) — deprecated in iOS 9.0.
- [`application:openURL:sourceApplication:annotation:`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1623073-application) — deprecated in iOS 9.0.

The UIApplication instance method [`openURL:`](https://developer.apple.com/documentation/uikit/uiapplication/1622961-openurl) (for _sending_ URL requests to other apps) is deprecated since iOS 10.0 in favor of [`openURL:options:completionHandler:`](https://developer.apple.com/documentation/uikit/uiapplication/1648685-openurl).

## Querying Other Apps

Before opening a URL in another app, an app can call [`canOpenURL:`](https://developer.apple.com/documentation/uikit/uiapplication/1622952-canopenurl) to check whether a registered handler exists. Since iOS 9.0, the schemes passed to `canOpenURL:` must be declared in the calling app's `Info.plist` under `LSApplicationQueriesSchemes`:

```xml
<key>LSApplicationQueriesSchemes</key>
<array>
    <string>instagram</string>
    <string>googledrive</string>
</array>
```

Up to 50 schemes may be declared. `canOpenURL:` returns `NO` for any scheme not listed, regardless of whether a handler app is installed. The `openURL:options:completionHandler:` method is not subject to this restriction and will attempt to open any URL.

## URL Structure and Parameters

Incoming URLs follow the standard URI structure:

```text
scheme://host/path?key=value&key2=value2
```

URL parameters are typically parsed via [`URLComponents`](https://developer.apple.com/documentation/foundation/urlcomponents) or [`URLQueryItem`](https://developer.apple.com/documentation/foundation/urlqueryitem). The `host`, `path`, and individual query items are accessible as named properties of those types.
