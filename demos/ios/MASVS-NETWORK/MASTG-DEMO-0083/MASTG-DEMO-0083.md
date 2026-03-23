---
platform: ios
title: Insecure ATS Configuration Allowing Cleartext Traffic
code: [xml]
id: MASTG-DEMO-0083
test: MASTG-TEST-0322
kind: fail
---

## Sample

The code below shows an insecure ATS configuration in an `Info.plist` file that disables App Transport Security in many ways:

- Globally via `NSAllowsArbitraryLoads`
- For web content via `NSAllowsArbitraryLoadsInWebContent`
- For media via `NSAllowsArbitraryLoadsForMedia`
- For local networking via `NSAllowsArbitraryLoadsForLocalNetworking`
- For specific domains (api.example.com and httpbin.org) via `NSExceptionDomains`

{{ Info.plist # Info_reversed.plist # MastgTest.swift }}

## Steps

1. Extract the app (@MASTG-TECH-0058) and locate the `Info.plist` file inside the app bundle (which we'll name `Info_reversed.plist`).
2. Convert the Info.plist to pretty printed JSON (@MASTG-TECH-0138)
3. Extract the relevant keys and values from the `NSAppTransportSecurity` configuration. In this case we use `gron` to transform the JSON into a greppable format and `egrep` to search for specific regex patterns.

{{ run.sh }}

## Observation

The output shows the relevant ATS configuration keys and values found in the `Info_reversed.plist` file:

{{ output.txt # Info_reversed.json }}

## Evaluation

The test fails because several ATS settings are set to `true`, which disables ATS globally and allows cleartext HTTP traffic to any domain. Specifically, the following settings are misconfigured:

- `NSAllowsArbitraryLoadsForLocalNetworking = true` allows cleartext traffic on local networks.
- `NSAllowsArbitraryLoadsForMedia = true` allows cleartext traffic for media resources.
- `NSAllowsArbitraryLoadsInWebContent = true` allows cleartext traffic in WebViews.
- Domain-specific exceptions for `api.example.com` and `httpbin.org` also allow insecure HTTP loads.

Note that even though `NSAllowsArbitraryLoads = true` is present, it is ignored because `NSAllowsArbitraryLoadsForLocalNetworking`, `NSAllowsArbitraryLoadsForMedia`, and `NSAllowsArbitraryLoadsInWebContent` are also present (regardless of their values), which take precedence.

**Context Considerations:**

If you reverse the app binary, you will find that its code make a HTTP request to `http://httpbin.org/get` using `URLSession`, which is affected by the ATS exceptions.

- The connection to `httpbin.org` is only allowed due to the domain-specific exception and not because of the global `NSAllowsArbitraryLoads` setting.
- There are no connections to `api.example.com`, so the domain-specific exceptions for it does not have an effect in this case. Regardless, having such exceptions is still a misconfiguration and should be avoided.
- The app does not use WebViews or media resources, so the corresponding ATS exceptions (`NSAllowsArbitraryLoadsInWebContent` and `NSAllowsArbitraryLoadsForMedia`) do not have an effect in this case. Regardless, having such exceptions is still a misconfiguration and should be avoided.
- The app does not connect to local network resources, so the corresponding ATS exception (`NSAllowsArbitraryLoadsForLocalNetworking`) does not have an effect in this case. Regardless, having such an exception is still a misconfiguration and should be avoided.
