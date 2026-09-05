---
masvs_category: MASVS-PLATFORM
platform: ios
title: Universal Links
---

Universal links are the iOS equivalent to Android App Links (aka. Digital Asset Links) and are used for deep linking. When tapping a universal link (to the app's website), the user will seamlessly be redirected to the corresponding installed app without going through Safari. If the app isn't installed, the link will open in Safari.

Universal links are standard web links (HTTP/HTTPS) and are not to be confused with custom URL schemes (@MASTG-KNOW-0079), which originally were also used for deep linking.

For example, the Telegram app supports both custom URL schemes and universal links:

- `tg://resolve?domain=fridadotre` is a custom URL scheme and uses the `tg://` scheme.
- `https://telegram.me/fridadotre` is a universal link and uses the `https://` scheme.

Both result in the same action, the user will be redirected to the specified chat in Telegram ("fridadotre" in this case). However, universal links give several key benefits that are not applicable when using custom URL schemes and are the recommended way to implement deep linking, according to the [Apple Developer Documentation](https://developer.apple.com/documentation/xcode/allowing-apps-and-websites-to-link-to-your-content "Allowing apps and websites to link to your content"). Specifically, universal links are:

- **Unique**: Unlike custom URL schemes, universal links can't be claimed by other apps, because they use standard HTTP or HTTPS links to the app's website. They were introduced as a way to _prevent_ URL scheme hijacking attacks (an app installed after the original app may declare the same scheme and the system might target all new requests to the last installed app).
- **Secure**: When users install the app, iOS downloads and checks a file (the Apple App Site Association or AASA) that was uploaded to the web server to make sure that the website allows the app to open URLs on its behalf. Only the legitimate owners of the URL can upload this file, so the association of their website with the app is secure.
- **Flexible**: Universal links work even when the app is not installed. Tapping a link to the website would open the content in Safari, as users expect.
- **Simple**: One URL works for both the website and the app.
- **Private**: Other apps can communicate with the app without needing to know whether it is installed.

## Associated Domains Entitlement

To support universal links, the app must declare the `com.apple.developer.associated-domains` entitlement and list each domain it handles with the `applinks:` prefix (for example, `applinks:www.example.com`). In Xcode, this is configured under **Signing & Capabilities** → **Associated Domains**.

In a compiled app, the entitlement is embedded in the code signature of the main binary and can be extracted as described in @MASTG-TECH-0111. The following example comes from Telegram's entitlements:

```xml
<key>com.apple.developer.associated-domains</key>
<array>
    <string>applinks:telegram.me</string>
    <string>applinks:t.me</string>
</array>
```

See [Supporting associated domains](https://developer.apple.com/documentation/xcode/supporting-associated-domains "Supporting associated domains") for the full syntax, including the `?mode=` option used during development.

## Apple App Site Association (AASA) and Verification

Universal link verification happens **on the device, enforced by the OS, at app installation time** (and is periodically refreshed). iOS retrieves the `apple-app-site-association` (AASA) file for each domain declared in the `applinks:` entitlement, either directly from `https://<domain>/.well-known/apple-app-site-association` or through Apple's CDN at `https://app-site-association.cdn-apple.com/a/v1/<domain>`. The file must be served over HTTPS without redirects.

The AASA file maps each domain to the app IDs allowed to handle its links and to the URL paths each app may open:

```json
{
    "applinks": {
        "details": [
            {
                "appIDs": ["W74U47NE8E.com.example.app"],
                "components": [
                    { "/": "/shop/buy-*" },
                    { "/": "/today" },
                    { "/": "/shop/buy-iphone/*", "exclude": true }
                ]
            }
        ]
    }
}
```

The `appIDs` value must match the app's `application-identifier` (Team ID + bundle ID), which is what binds the website to the app. If verification does not succeed (for example, the AASA file is missing, not served over HTTPS, or the `appIDs` do not match), iOS does not route the link to the app and instead opens it in Safari. Because this association is controlled by the domain owner and validated by the OS, another app cannot register itself to receive a domain's universal links.

Hosting and serving the AASA file is the responsibility of the website backend, not the app, and it is not part of the app package. You can retrieve the AASA file and inspect the on-device verification status with @MASTG-TECH-0175.

The `exclude` key (formerly the `NOT` path prefix) lets the developer specify paths that the app should not handle; it is a routing filter, not a security control.

## Handling Incoming Universal Links

When iOS opens an app from a universal link, it delivers an [`NSUserActivity`](https://developer.apple.com/documentation/foundation/nsuseractivity) object with an `activityType` of `NSUserActivityTypeBrowsingWeb`. The activity's [`webpageURL`](https://developer.apple.com/documentation/foundation/nsuseractivity/1418086-webpageurl) property holds the HTTP or HTTPS URL the user accessed.

The delivery entry point depends on the app's architecture:

| Architecture | Entry point |
| --- | --- |
| UIKit app delegate | [`application(_:continue:restorationHandler:)`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1623072-application) |
| UIKit scene delegate | [`scene(_:continue:)`](https://developer.apple.com/documentation/uikit/uiscenedelegate/3238056-scene) and `scene(_:willConnectTo:options:)` (via `connectionOptions.userActivities`) |
| SwiftUI | [`onContinueUserActivity(NSUserActivityTypeBrowsingWeb, perform:)`](https://developer.apple.com/documentation/swiftui/view/oncontinueuseractivity(_:perform:)) |

Unlike custom URL schemes, universal links are **not** delivered through `application(_:open:options:)` or SwiftUI's `onOpenURL(perform:)`, and there is no `sourceApplication` value associated with an incoming universal link, since the request originates from a web context rather than from a specific calling app.

The continuation method is shared with Apple's Handoff feature (@MASTG-KNOW-0123): Handoff delivers its own `NSUserActivity` through the same entry point, distinguished by its `activityType` (an app-defined type and `userInfo` payload, rather than `NSUserActivityTypeBrowsingWeb` and a `webpageURL`). You can observe which method receives a link and how the app forwards it at runtime with @MASTG-TECH-0176.

## URL Structure and Parsing

The `webpageURL` follows the standard URL structure, and its path and query parameters are typically parsed with [`URLComponents`](https://developer.apple.com/documentation/foundation/urlcomponents) and [`URLQueryItem`](https://developer.apple.com/documentation/foundation/urlqueryitem):

```text
https://www.example.com/path?key=value&key2=value2
```

While the OS verifies the **domain**, the **path and query parameters are caller-controlled**: anyone can craft a link to a verified domain (for example, by sending it to the user) with arbitrary path and query values. Apple's documentation on [handling universal links](https://developer.apple.com/documentation/xcode/supporting-universal-links-in-your-app "Supporting universal links in your app") notes that universal links are an entry point into the app and that incoming URLs and their parameters should be treated as untrusted input.

## Sending Universal Links

An app can open a universal link in another app with [`open(_:options:completionHandler:)`](https://developer.apple.com/documentation/uikit/uiapplication/1648685-open). Passing the option [`universalLinksOnly`](https://developer.apple.com/documentation/uikit/uiapplication/openexternalurloptionskey/2865839-universallinksonly) set to `true` opens the URL only if it is a valid universal link with an installed app capable of handling it, instead of falling back to Safari.

When an app calls `open(_:options:completionHandler:)` on a link to **its own** associated website, iOS does not treat it as a universal link, because the request originates from the app itself; the URL opens in Safari instead. Universal links are routed to the app only when opened from a different context, such as another app or a web page.

The URLs an app sends to other apps this way can carry data in their path or query, so the same considerations that apply to any inter-app channel apply here (see @MASTG-KNOW-0078 and @MASTG-BEST-0045). You can trace outgoing `open(_:)` calls at runtime with @MASTG-TECH-0176.

!!! note
    Also note that typing a universal link in Safari's address bar does **not** open the app. The user must follow an existing link on a web page so that iOS treats it as a navigation. See @MASTG-TECH-0169 for more ways to trigger universal links during testing.

You can learn more about Universal Links in the post ["Learning about Universal Links and Fuzzing URL Schemes on iOS with Frida"](https://grepharder.github.io/blog/0x03_learning_about_universal_links_and_fuzzing_url_schemes_on_ios_with_frida.html "Learning about Universal Links and Fuzzing URL Schemes on iOS with Frida") by Carlos Holguera.
