---
title: Analyzing the ATS Configuration
platform: ios
---

App Transport Security (ATS) settings are declared under the `NSAppTransportSecurity` key in the `Info.plist` file. ATS controls the security requirements for network connections made through the [URL Loading System](https://developer.apple.com/documentation/foundation/url_loading_system) (for example, via `URLSession`). By default, ATS requires HTTPS with TLS 1.2 or higher and enforces forward secrecy. Apps can weaken or disable these defaults through exceptions in `Info.plist`.

After retrieving the `Info.plist` with @MASTG-TECH-0153, use @MASTG-TECH-0154 to analyze its contents and look for the keys below. See @MASTG-KNOW-0071 for more details on ATS configuration and exceptions.

## Using plutil and jq

Convert the `Info.plist` to JSON and extract the full ATS configuration:

```sh
plutil -convert json -o Info.json MyApp.app/Info.plist
cat Info.json | jq '.NSAppTransportSecurity'
```

Example output for an app with several exceptions configured:

```json
{
  "NSAllowsArbitraryLoads": false,
  "NSExceptionDomains": {
    "example.com": {
      "NSExceptionAllowsInsecureHTTPLoads": true,
      "NSIncludesSubdomains": true
    },
    "legacy.example.com": {
      "NSExceptionMinimumTLSVersion": "TLSv1.0",
      "NSExceptionRequiresForwardSecrecy": false
    }
  }
}
```

To check for any domain that disables forward secrecy:

```sh
cat Info.json | jq '.NSAppTransportSecurity.NSExceptionDomains | to_entries[] | select(.value.NSExceptionRequiresForwardSecrecy == false) | .key'
```

To check for the global `NSAllowsArbitraryLoads` flag:

```sh
cat Info.json | jq '.NSAppTransportSecurity.NSAllowsArbitraryLoads'
```

## Using PlistBuddy

Use the built-in `PlistBuddy` tool to print only the ATS subtree:

```sh
/usr/libexec/PlistBuddy -c "Print :NSAppTransportSecurity" MyApp.app/Info.plist
```
