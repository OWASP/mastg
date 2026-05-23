---
masvs_category: MASVS-PLATFORM
platform: ios
title: Core NFC
available_since: 11
---

[Core NFC](https://developer.apple.com/documentation/corenfc) is the iOS framework for reading and writing NFC (Near Field Communication) tags. Apps use it most commonly to read NFC tags embedded in physical objects such as product labels, transport cards, and smart posters.

## APIs

Core NFC provides two commonly used tag reading session classes:

- **[`NFCNDEFReaderSession`](https://developer.apple.com/documentation/corenfc/nfcndefreadersession)**: Reads NDEF records from NFC tags. NDEF tag writing support was added in iOS 13.
- **[`NFCTagReaderSession`](https://developer.apple.com/documentation/corenfc/nfctagreadersession)**: Provides lower-level access to specific tag types, including ISO 7816, ISO 15693, FeliCa, and MIFARE. Available since iOS 13.

## Permissions and Entitlements

Apps must declare [`NFCReaderUsageDescription`](https://developer.apple.com/documentation/bundleresources/information_property_list/nfcreaderusagedescription) in `Info.plist`. Access through `NFCTagReaderSession` requires the [`com.apple.developer.nfc.readersession.formats`](https://developer.apple.com/documentation/bundleresources/entitlements/com.apple.developer.nfc.readersession.formats) entitlement, enabled through the Near Field Communication Tag Reading capability in Xcode.

Some tag technologies require additional `Info.plist` keys, such as [`com.apple.developer.nfc.readersession.iso7816.select-identifiers`](http://developer.apple.com/documentation/bundleresources/entitlements/com.apple.developer.nfc.readersession.iso7816.select-identifiers) for ISO 7816 application identifiers and [`com.apple.developer.nfc.readersession.felica.systemcodes`](https://developer.apple.com/documentation/bundleresources/entitlements/com.apple.developer.nfc.readersession.felica.systemcodes) for FeliCa system codes.
