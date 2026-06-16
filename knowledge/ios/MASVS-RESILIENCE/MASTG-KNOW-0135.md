---
masvs_category: MASVS-RESILIENCE
platform: ios
title: Virtual Devices Detection
---

## Overview

In the context of anti-reversing, the goal of emulator and virtual device detection is to increase the difficulty of running the app on an emulated or virtualized device. This increased difficulty forces the reverse engineer to defeat the checks or use a physical device, thereby limiting the access required for large-scale device analysis.

Virtual devices are environments that virtualize the hardware and operating system expected by iOS apps compiled for physical devices. Unlike the iOS Simulator, they can execute iOS device binaries when they preserve the relevant iOS architecture and runtime environment.

Since its release, @MASTG-TOOL-0108 (commercial tool) has made iOS virtualization available, [setting itself apart from the iOS Simulator](https://www.corellium.com/compare/ios-simulator).

Corellium describes its platform as using a [true ARM type 1 hypervisor](https://www.corellium.com/compare/ios-simulator), and states that its virtual devices are ARM native and can run production code without code modifications. This enables reverse engineering and security testing of iOS apps in virtual devices.

Recent research projects such as [vPhone](https://github.com/Lakr233/vphone-cli) and [Super Tart](https://github.com/wh1te4ever/super-tart-vphone-writeup) also fit under virtual device detection. vPhone describes itself as booting a virtual iPhone via Apple's [`Virtualization.framework`](https://developer.apple.com/documentation/virtualization) using [Private Cloud Compute Virtual Research Environment](https://security.apple.com/documentation/private-cloud-compute/virtualresearchenvironment) infrastructure. Super Tart describes a modified version of [Tart](https://tart.run/), an open source virtualization toolset for Apple silicon Macs, used to boot a virtual iPhone target identified in public research as `VPHONE600AP`.

!!! note
Do not confuse virtual devices with:

    - The iOS Simulator, which runs simulator builds, while virtual devices attempt to reproduce an iOS device environment for iOS device binaries. See @MASTG-KNOW-0088.
    - iPhone and iPad apps running on macOS, which use an official Mac App Store distribution path on Macs with Apple silicon. See @MASTG-KNOW-0136.

!!! warning "Security Considerations"
    Virtual device detection is inherently a cat-and-mouse game. Detection methods and bypass techniques evolve continuously, and determined attackers with sufficient time and resources can circumvent these protections, for example, by hooking or patching the detection logic.

    Use these techniques as part of a defense-in-depth strategy, not as a standalone solution.

There are several indicators that the device in question is being emulated or virtualized. The main detection strategy is to identify features and limitations of commonly used emulation or virtual device solutions.

### App Attest Validation

Apple's [App Attest service](https://developer.apple.com/documentation/devicecheck/dcappattestservice) can be used to assert the legitimacy of a particular instance of an app to the app's server. Apple documents this as a server side flow in ["Validating apps that connect to your server"](https://developer.apple.com/documentation/devicecheck/validating-apps-that-connect-to-your-server).

Before using App Attest, the app should check [`DCAppAttestService.shared.isSupported`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/issupported), because not all device types support the service. The app can then use [`generateKey(completionHandler:)`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/generatekey%28completionhandler%3A%29) to request creation of a cryptographic key, and [`attestKey(_:clientDataHash:completionHandler:)`](https://developer.apple.com/documentation/devicecheck/dcappattestservice/attestkey%28_%3Aclientdatahash%3Acompletionhandler%3A%29) so Apple can attest to the validity of that key.

Corellium states that users [cannot directly download an app from the App Store onto a Corellium device](https://support.corellium.com/features/apps/testing-ios-apps), and cannot load an encrypted App Store app copied from another device. Instead, Corellium users can only load a signed, unencrypted version of the app.

Therefore, when an App Store app is extracted from a physical device, decrypted, and then sideloaded or re-signed for execution on Corellium or another virtual device (see @MASTG-TECH-0056 and @MASTG-TECH-0092), it should not automatically be treated as the same production app instance that the backend expects to validate through App Attest. If the re-signing or repackaging flow changes the app identity expected by the server-side App Attest validation logic, validation should fail.

!!! note
This check is not limited to detecting virtual devices such as Corellium. The same App Attest validation failure can also occur when an App Store app is extracted, decrypted, and re-signed for execution on a physical device, because the relevant security property is not whether the device is virtualized, but whether the app instance still matches the genuine production identity expected by the backend. As a result, App Attest can help cover a broader class of tampering and repackaging scenarios beyond virtual device execution alone.

### Hardware Capability Probes

Virtual devices can be detected by trying to interact with hardware components through operating system APIs, as these devices might not expose hardware capabilities in the same way as physical devices.

The [official Corellium iOS documentation](https://support.corellium.com/devices/ios) states that Corellium iOS devices lack GPU and [Metal](https://developer.apple.com/documentation/metal/getting-the-default-gpu) support, and that NFC and Bluetooth are not currently supported for iOS.

Apple exposes APIs that can be used to query the presence or availability of these hardware capabilities:

#### Metal GPU

Apple documents [`MTLCreateSystemDefaultDevice()`](https://developer.apple.com/documentation/metal/mtlcreatesystemdefaultdevice%28%29) as a function that returns the default Metal device.

```swift
import Metal

let hasGPU = MTLCreateSystemDefaultDevice() != nil
```

#### NFC

Apple documents [`NFCReaderSession.readingAvailable`](https://developer.apple.com/documentation/corenfc/nfcreadersession-swift.class/readingavailable) as a Boolean value that determines whether the device supports NFC tag reading.

```swift
import CoreNFC

let hasNFC = NFCReaderSession.readingAvailable
```

#### Bluetooth

Apple documents [`CBCentralManager`](https://developer.apple.com/documentation/corebluetooth/cbcentralmanager) and [`CBCentralManagerDelegate`](https://developer.apple.com/documentation/corebluetooth/cbcentralmanagerdelegate) for Bluetooth central role operations. The manager state is exposed through [`CBManagerState`](https://developer.apple.com/documentation/corebluetooth/cbmanagerstate).

```swift
import CoreBluetooth

final class BTProbe: NSObject, CBCentralManagerDelegate {
    private var manager: CBCentralManager!
    var state: CBManagerState = .unknown

    override init() {
        super.init()
        manager = CBCentralManager(delegate: self, queue: nil)
    }

    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        state = central.state
    }
}
```

If `state` is [`.unsupported`](https://developer.apple.com/documentation/corebluetooth/cbmanagerstate/unsupported), the device does not support the Bluetooth low energy central or client role. If `state` is [`.poweredOff`](https://developer.apple.com/documentation/corebluetooth/cbmanagerstate/poweredoff), Bluetooth is currently powered off, but the device might still support Bluetooth.

!!! note
    Apps that access Core Bluetooth APIs must declare a Bluetooth usage purpose string, such as [`NSBluetoothAlwaysUsageDescription`](https://developer.apple.com/documentation/bundleresources/information-property-list/nsbluetoothalwaysusagedescription), when required by the target platform and SDK.

### Device Properties

The [`sysctlbyname`](https://developer.apple.com/documentation/kernel/1387446-sysctlbyname) call can be used to retrieve system information and make decisions dynamically based on the current system information.

For example, apps commonly use `sysctlbyname("hw.machine")` to retrieve a hardware machine identifier.

These properties can be used to cross-check whether the device should support the hardware capabilities queried above. A claimed device model whose capabilities do not match what that model should expose is another indicator of a virtual device.

### Presence of Specific Virtualization Engine Files

It is also possible to check for files or system components that a virtualization engine might add to the virtual device and that are not part of a standard iOS device.

Corellium documents that it adds a daemon called [`corelliumd`](https://support.corellium.com/troubleshooting-faqs/general-faqs#what-is-corelliumd) to Android and iOS devices. However, Corellium also documents that iOS devices can be created without the `corelliumd` daemon, so the absence of this daemon is not enough to rule out a Corellium virtual device.

The following example checks for the presence of a Corellium daemon path using the [`stat`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/stat.2.html) call:

```c
#include <sys/stat.h>

struct stat st;
return stat("/usr/libexec/corelliumd", &st) == 0;
```

This is only an indicator. File-based checks are easy to bypass by hiding, renaming, removing, or intercepting access to the checked path.
