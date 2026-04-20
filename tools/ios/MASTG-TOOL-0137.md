---
title: GlobalWebInspect
platform: ios
source: https://github.com/ChiChou/GlobalWebInspect
hosts: [ios]
---

!!! warning

    This tool may or may not work depending on your macOS / iOS combination.

GlobalWebInspect can be installed on a jailbroken iOS device to enable Safari Web Inspector attachment to `WKWebView` and JavaScriptCore web content in apps that would not normally expose it. You can install the tweak by copying the package to your device and running `sudo dpkg -i <file>.deb`, or by installing it through a jailbreak package manager. It requires a MobileSubstrate compatible hooking environment, such as @MASTG-TOOL-0139.

## How it works

On iOS 16.4 and later, GlobalWebInspect hooks creation of `WKWebView` and `JSContext` related objects, then forcibly turns inspection on. In the project's own code and README, it says it hooks `WKWebView` creation and `JSContext`, and the implementation shows it calling `setInspectable:` on `WKWebView` during `-[WKWebView _initializeWithConfiguration:]`, and calling `JSGlobalContextSetInspectable(..., true)` for JavaScriptCore contexts.

For older iOS versions, the mechanism is different. The README says `webinspectord` checks certain entitlements before listing a process as inspectable, including `com.apple.security.get-task-allow` and several Web Inspector related entitlements. GlobalWebInspect injects into `webinspectord` and returns `true` for those entitlement queries, so apps that would normally be rejected still appear to Safari Web Inspector.
