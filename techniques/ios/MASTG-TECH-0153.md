---
title: Retrieving Info.plist Files
platform: ios
---

The `Info.plist` file is the primary property list configuration file included in every iOS app bundle. It contains key-value pairs that describe the app's configuration, including permissions, capabilities, and security settings such as the App Transport Security (ATS) policy.

After extracting an app with @MASTG-TECH-0058, you can locate the `Info.plist` file at the root of the `.app` bundle. For example, assuming you have extracted an iOS app named `MyApp.ipa` using @MASTG-TECH-0058, you can run the following command from the `Payload/` folder:

```sh
find . -name "Info.plist" -maxdepth 2

./MyApp.app/Info.plist
```

The `-maxdepth 2` flag limits the search to the app bundle root and avoids listing `Info.plist` files from nested frameworks and extensions. If you also need to inspect frameworks or extensions, increase the depth or remove the limit.

Apps distributed through the App Store typically ship the `Info.plist` in binary plist format. If the file is in binary format, convert it to a human-readable format using @MASTG-TECH-0138 before inspecting it, or use @MASTG-TECH-0154 to analyze it directly.
