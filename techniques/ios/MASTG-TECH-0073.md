---
title: Information Gathering - API Usage
platform: ios
---

The iOS platform provides many built-in libraries for common application functionality, including cryptography, Bluetooth, NFC, networking, and location services. Determining whether these libraries are present in an application can provide valuable insight into its underlying logic.

For instance, if an application imports the `CC_SHA256` function, it indicates that the application will perform a SHA-256 hash. Further information on analyzing iOS's cryptographic APIs is provided in the section "[iOS Cryptographic APIs](../../Document/0x06e-Testing-Cryptography.md "iOS Cryptographic APIs")".

Similarly, the approach above can be used to determine where and how an application uses Bluetooth. For instance, an application that communicates over the Bluetooth channel must use functions from the Core Bluetooth framework, such as `CBCentralManager` or `connect`. Using the [iOS Bluetooth documentation](https://developer.apple.com/documentation/corebluetooth "iOS Bluetooth documentation"), you can identify the critical functions and begin analysis from their import dependencies.
