---
platform: ios
title: Undeclared Known Tracking Domains
id: MASTG-TEST-0281
type: [static, dynamic]
weakness: MASWE-0108
profiles: [P]
---

## Overview

This test identifies whether the app properly declares all known tracking domains it may communicate with in the [`NSPrivacyTrackingDomains`](https://developer.apple.com/documentation/bundleresources/app-privacy-configuration/nsprivacytrackingdomains) section of its [Privacy Manifest](https://developer.apple.com/documentation/bundleresources/privacy_manifest_files) files.

To perform this test, use one or more curated lists of known trackers. These lists include domains and identifiers associated with advertising networks, analytics providers, and user profiling services. They are commonly used in privacy-focused tools and browsers to detect and block tracking behavior.

Some example lists:

- **[DuckDuckGo iOS Trackers](https://github.com/duckduckgo/tracker-blocklists/blob/main/web/v5/ios-tds.json)**: Includes domains, matching rules, descriptions, and categories such as "Action Pixels," "Ad Fraud," "Ad Motivated Tracking," and "Advertising."
- **[Exodus Privacy Trackers](https://reports.exodus-privacy.eu.org/en/trackers/)**: Includes tracker names, categories (e.g., "Advertisement," "Analytics," "Profiling"), descriptions, and detection metadata such as network and code signatures.

These references can be used to match hardcoded or dynamically accessed domains within your app and verify whether appropriate declarations exist in the Privacy Manifest.

In terms of APIs, you should consider any networking API that allows the app to communicate with external servers, such as `URLSession`, `NSURLSession`, `NSURLConnection`, or third-party networking libraries (e.g., Alamofire, AFNetworking). Additionally, look for references to well-known tracking libraries (e.g., Facebook SDK, Google Analytics) that may indicate the presence of tracking functionality.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.
3. Use @MASTG-TECH-0071 to search for hardcoded strings representing known tracking domains.
4. Use @MASTG-TECH-0136 to extract the app's privacy manifest files, including those from third-party SDKs or frameworks.
5. Use @MASTG-TECH-0137 to obtain the list of declared tracking domains from the privacy manifest files.
6. Use @MASTG-TECH-0062 to intercept and log all outbound network traffic.

## Observation

The output should contain:

- All extracted privacy manifests from the app.
- A list of declared tracking domains from the `NSPrivacyTrackingDomains` key in the manifests (preferably with associated components).
- The network traffic capture, which you can use to extract all contacted domains during runtime.
- A list of all domains contacted during dynamic testing.
- A list of code matches for known tracking domains or tracking libraries from static analysis.

## Evaluation

The test case fails if any of the following is missing in the privacy manifest files' `NSPrivacyTrackingDomains` key for the app or any of its components (Frameworks, Plugins, etc.):

- Tracking domains contacted by the app at runtime.
- Tracking domains found in the code.
- Domains corresponding to tracking SDKs found in the code.
