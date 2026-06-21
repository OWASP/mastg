---
title: Validate Source Application in Custom URL Scheme Handlers
alias: validate-source-application-in-custom-url-scheme-handlers
id: MASTG-BEST-0055
platform: ios
knowledge: [MASTG-KNOW-0079]
---

When a custom URL scheme triggers a privileged or irreversible action, check [`sourceApplication`](https://developer.apple.com/documentation/uikit/uiscene/connectionoptions/sourceapplication) from [`UIOpenURLContext.options`](https://developer.apple.com/documentation/uikit/uiopenurl/options) before processing the request. This allows you to verify the calling app's bundle ID against an allowlist.

```swift
let allowedSources: Set<String> = ["com.example.myapp", "com.example.companion"]

guard let source = context.options.sourceApplication,
      allowedSources.contains(source) else {
    return
}
```

## Check Both URL Delivery Paths

When using the Scene lifecycle, URLs can arrive through two paths: [`scene(_:willConnectTo:options:)`](https://developer.apple.com/documentation/uikit/uiscenedelegate/scene(_:willconnectto:options:)) for cold launches and [`scene(_:openURLContexts:)`](https://developer.apple.com/documentation/uikit/uiscenedelegate/scene(_:openurlcontexts:)) for warm opens. Validate `sourceApplication` in both handlers to avoid leaving one path unprotected.

## Apple Developer Team Limitation

Apple only populates `sourceApplication` when the calling app belongs to the same [Apple Developer Team](https://developer.apple.com/help/account/manage-your-team/about-the-team-id/). Apps from other teams or system apps (e.g. Safari, Notes) will have `sourceApplication` set to `nil`. This means source validation is most useful for restricting URL scheme triggers to your own app suite, not for identifying arbitrary third-party callers.

!!! note
    For apps using pure SwiftUI with [`.onOpenURL`](https://developer.apple.com/documentation/swiftui/view/onopenurl(perform:)), `sourceApplication` is not available. If source validation is required, use the Scene lifecycle with `SceneDelegate` instead.
