---
masvs_category: MASVS-PLATFORM
platform: ios
title: Core Bluetooth
available_since: 5
---

[Core Bluetooth](https://developer.apple.com/documentation/corebluetooth) is the iOS framework for Bluetooth Low Energy (BLE) communication. Apps use it to discover, connect to, and exchange data with BLE peripherals, such as fitness trackers, medical sensors, smart home hardware, and companion devices.

## APIs

Apps interact with BLE primarily through two manager classes:

- **[`CBCentralManager`](https://developer.apple.com/documentation/corebluetooth/cbcentralmanager)**: Scans for, discovers, and connects to BLE peripherals. After connecting, the app reads, writes, and subscribes to GATT characteristics to exchange data.
- **[`CBPeripheralManager`](https://developer.apple.com/documentation/corebluetooth/cbperipheralmanager)**: Lets the local device act as a BLE peripheral by advertising, publishing GATT services and characteristics, and responding to read, write, and subscription requests from connected centrals.

Data is exchanged through GATT services and characteristics. Characteristics can support operations such as read, write, notify, and indicate, depending on how the peripheral defines them.

## Permissions

Apps linked on or after iOS 13 must declare [`NSBluetoothAlwaysUsageDescription`](https://developer.apple.com/documentation/bundleresources/information_property_list/nsbluetoothalwaysusagedescription) in `Info.plist`. The system presents a permission prompt when the app first attempts to use Bluetooth.

For older apps, [`NSBluetoothPeripheralUsageDescription`](https://developer.apple.com/documentation/bundleresources/information_property_list/nsbluetoothperipheralusagedescription) was used on iOS 6 through iOS 13.

Background Bluetooth use requires the [`UIBackgroundModes`](https://developer.apple.com/documentation/bundleresources/information_property_list/uibackgroundmodes) key with `bluetooth-central` or `bluetooth-peripheral`, depending on the app's role. Background scanning and advertising are subject to additional system restrictions.
