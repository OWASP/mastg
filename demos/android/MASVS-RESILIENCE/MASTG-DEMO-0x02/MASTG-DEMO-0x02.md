---
platform: android
title: Behavioral Detection of Emulator Detection Mechanisms
id: MASTG-DEMO-0x02
code: [kotlin, xml]
test: MASTG-TEST-0x02
kind: pass
---

## Sample

The snippet below shows sample code that performs common emulator indicator checks and logs the matches against common emulator values (see @MASTG-KNOW-0031 for more information about common emulator checks and emulator values).

The checks cover several categories (build properties, telephony identifiers, package visibility, and OpenGL renderer information).

Notes about the checks performed:

- The sample avoids `PackageManager.getInstalledPackages()` because Android 11+ requires the `QUERY_ALL_PACKAGES` permission to access the full installed app inventory. Google Play treats that inventory as sensitive and allows it only for apps with a strong, declared need. Instead, this demo uses launcher package queries and explicit checks for known emulator packages.
- The sample avoids Play Integrity checks (@MASTG-KNOW-0035) because they require Play Console configuration and server-side verification, which breaks the self-contained requirement for MASTG demos.
- The manifest declares `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` so the runtime permission prompts can be shown before querying telephony values, and it includes `<queries>` entries for package visibility checks.

{{ ../MASTG-DEMO-0114/MastgTest.kt # ../MASTG-DEMO-0114/AndroidManifest.xml }}

## Steps

1. Use @MASTG-TECH-0005 to install the app on a real device.
2. Open the app.
3. Tap **Start**.
4. Grant the `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` permissions when prompted, then tap **Start** and observe the displayed results.
5. Use @MASTG-TECH-0005 to install the same app on an emulated device provided by @MASTG-TOOL-0007.
6. Open the app.
7. Tap **Start**.
8. Grant the `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` permissions when prompted, then tap **Start** and compare the app's response with the results from the real device.

## Observation

The app behaves differently on the two devices. It reports no emulator indicators on the real device, but reports detected indicators on the emulated one.

### Real Device

No emulator indicators were found in the device.

{{ output-real.txt }}

### Emulated Device

Multiple emulator indicators are triggered for @MASTG-TOOL-0007 emulators:

- **System/Build Properties**: We observe indicators for `sdk_gphone64_arm64` and `ranchu`. Ranchu (also known as "Goldfish"), is the name of the virtual hardware platform used by @MASTG-TOOL-0007 for its emulated devices. More details can be found in [AOSP docs](https://github.com/aosp-mirror/platform_external_qemu/blob/main/docs/GOLDFISH-VIRTUAL-HARDWARE.TXT).
- **GPU Device**: The emulator reports the host's GPU device as its graphics renderer.

{{ output-emulator.txt }}

## Evaluation

The test passes because the app reports no emulator indicators on the real device and detects indicators on the emulated device. In this demo, displaying the results is the app's response to the detections being triggered. In a real app that treats emulation as a threat, that response could instead be blocking a sensitive action or closing the app.
