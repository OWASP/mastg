---
title: Securely Implementing Universal Links
alias: securely-implementing-universal-links
id: MASTG-BEST-0x70-1
platform: ios
knowledge: [MASTG-KNOW-0080]
---

iOS [Universal Links](https://developer.apple.com/documentation/xcode/allowing-apps-and-websites-to-link-to-your-content) allow websites to link directly into an app using standard HTTPS URLs. Because the OS routes inbound URLs into the app's memory space, an improperly configured or implemented Universal Link handler can become an entry point for link hijacking, unauthorized state changes, or data exfiltration.

## Best Practices

1. **Restrict the associated-domains entitlement to specific domains.** The [`com.apple.developer.associated-domains`](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_developer_associated-domains) entitlement must list only the exact domains your organization controls. Avoid wildcards (e.g., `applinks:*.example.com`) — a wildcard grants every subdomain, including forgotten or third-party subdomains, the ability to route Universal Links into your app.

2. **Serve the AASA file securely.** Host the [`apple-app-site-association`](https://developer.apple.com/documentation/xcode/supporting-associated-domains) (AASA) file at `https://<domain>/.well-known/apple-app-site-association` over HTTPS only. The file must contain valid JSON, restrict `appIDs` to your authorized Team ID and Bundle ID, and limit the `paths` or `components` array to only the URL paths your app is designed to handle.

3. **Validate all URL components in the receiver method.** The [`application(_:continue:restorationHandler:)`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/1623072-application) delegate receives an [`NSUserActivity`](https://developer.apple.com/documentation/foundation/nsuseractivity) whose `webpageURL` originates from an external source and must be treated as untrusted. Use [`URLComponents`](https://developer.apple.com/documentation/foundation/urlcomponents) to strictly allow-list the scheme, host, path, and query parameters before processing. Drop the request and clear any intermediate state if the URL does not match the expected structure.

4. **Validate the destination scheme before calling `UIApplication.open`.** When your app opens external URLs via [`UIApplication.open(_:options:completionHandler:)`](https://developer.apple.com/documentation/uikit/uiapplication/1648685-open), verify that the URL scheme and host are on an explicit allow-list. Never pass a URL constructed from untrusted inbound data (such as query parameters from a received Universal Link) directly to this API, as it can trigger URI Scheme Hijacking by routing the user to an attacker-controlled app.

5. **Reject unexpected inputs gracefully.** If the incoming URL contains unexpected parameters, unknown hosts, or malformed components, the app must reject the link silently, log the event, and return the user to a safe application state. Do not surface error details that could aid an attacker in refining their payload.
