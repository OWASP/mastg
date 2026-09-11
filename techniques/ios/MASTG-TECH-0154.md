---
title: Analyzing Info.plist Files
platform: ios
---

Once you have the `Info.plist` file as described in @MASTG-TECH-0153, you can analyze its contents to inspect security-relevant settings such as permissions, entitlements, and ATS configuration.

If the file is in binary plist format, convert it first using @MASTG-TECH-0138.

## Using plutil

Use @MASTG-TOOL-0062 to print all key-value pairs in the `Info.plist` file in a human-readable format:

```sh
plutil -p MyApp.app/Info.plist
```

This outputs a structured representation of the plist content:

```sh
{
  "CFBundleDisplayName" => "MyApp"
  "CFBundleIdentifier" => "com.example.myapp"
  "CFBundleShortVersionString" => "1.0"
  "NSAppTransportSecurity" => {
    "NSAllowsArbitraryLoads" => 0
    "NSExceptionDomains" => {
      "example.com" => {
        "NSExceptionAllowsInsecureHTTPLoads" => 1
        "NSIncludesSubdomains" => 1
      }
    }
  }
  ...
}
```

To query a specific key, convert the plist to JSON and use `jq`:

```sh
plutil -convert json -o Info.json MyApp.app/Info.plist
cat Info.json | jq '.NSAppTransportSecurity'
```

## Using PlistBuddy

Use the built-in `PlistBuddy` tool to read a specific key:

```sh
/usr/libexec/PlistBuddy -c "Print :NSAppTransportSecurity" MyApp.app/Info.plist
```

This prints only the `NSAppTransportSecurity` subtree:

```sh
Dict {
    NSAllowsArbitraryLoads = false
    NSExceptionDomains = Dict {
        example.com = Dict {
            NSExceptionAllowsInsecureHTTPLoads = true
            NSIncludesSubdomains = true
        }
    }
}
```

## Using plistlib

Use Python's built-in @MASTG-TOOL-0136 module to parse and inspect the plist programmatically:

```python
import plistlib
import json

with open('MyApp.app/Info.plist', 'rb') as fp:
    data = plistlib.load(fp)

print(json.dumps(data, indent=2, default=str))
```
