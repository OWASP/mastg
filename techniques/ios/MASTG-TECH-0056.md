---
title: Installing Apps
platform: ios
---

Installing an application outside the Apple App Store is called sideloading. There are several ways of sideloading, as described below. On iOS, the installation process is handled by the installd daemon, which unpacks and installs the application. To integrate app services or be installed on an iOS device, all applications must be signed with a certificate issued by Apple. This means the application can be installed only after successful code-signature verification, as explained in @MASTG-TECH-0092.

**Disabling Signature Verification (optional)**: On a jailbroken device, you can bypass the signature verification requirement using @MASTG-TOOL-0127, which hooks the appropriate system daemon and disables signature verification for any installations you do with the tools listed below while it's enabled.

Different methods exist for installing an IPA package on an iOS device. They are described below.

## Sideloadly

@MASTG-TOOL-0118 is a GUI tool that automates all required steps for you. It requires valid Apple developer credentials because it obtains a signature from Apple servers.

Connect your device via USB, enter your Apple ID, and drag-and-drop the IPA file into SideLoadly. Click Start to sign and install the given IPA automatically.

<img src="Images/Techniques/0056-Sideloadly.png" width="400px" />

## libimobiledevice

On Linux and macOS, you can alternatively use @MASTG-TOOL-0126. This allows you to install apps over a USB connection by executing `ideviceinstaller`. The connection is implemented with the USB multiplexing daemon [usbmuxd](https://www.theiphonewiki.com/wiki/Usbmux "Usbmux"), which provides a TCP tunnel over USB.

Let's install the @MASTG-APP-0028 app with the following command:

```bash
$ ideviceinstaller -i Uncrackable.ipa
...
Install: Complete
```

## Filza

@MASTG-TOOL-0128 allows you to install an IPA file already on your device. You can use either `scp` (@MASTG-TECH-0053) or [AirDrop](https://support.apple.com/en-us/119857) to copy the IPA file to your device, after which you can navigate to the IPA file on your file system and click the `Install` button in the top right corner.

## ipainstaller

The IPA can also be installed directly on an iOS device via the command line using @MASTG-TOOL-0138. Naturally, this requires a jailbroken device; otherwise, you cannot SSH into it. After copying the file over to the device, for example, via `scp` (@MASTG-TECH-0053) or [AirDrop](https://support.apple.com/en-us/119857), you can execute `ipainstaller` with the IPA's filename:

```bash
ipainstaller Uncrackable.ipa
```

## ios-deploy

On macOS, you can also use the @MASTG-TOOL-0054 tool to install iOS apps from the command line. You'll need to unzip your IPA since ios-deploy uses the app bundles to install apps.

```bash
unzip UnCrackable.ipa
ios-deploy --bundle 'Payload/UnCrackable Level 1.app' -W -v
```

## xcrun

After installing @MASTG-TOOL-0071, you can execute the following command to install a signed IPA:

```bash
# Get the correct device id
$ xcrun devicectl list devices
Devices:
Name                 Hostname                                     Identifier                             State                Model
------------------   ------------------------------------------   ------------------------------------   ------------------   ------------------------------
Foobar               00008101-00FF28803FF9001E.coredevice.local   ABD1F3D8-7BC1-52CD-8DB6-9BFD794CE862   available (paired)   iPhone 14 Pro Max (iPhone15,3)

$ xcrun devicectl device install app --device 00008101-00FF28803FF9001E ~/signed.ipa
11:59:04  Acquired tunnel connection to device.
11:59:04  Enabling developer disk image services.
11:59:04  Acquired usage assertion.
4%... 12%... 28%... 30%... 31%... 32%... 33%... 35%... 36%... 37%... 39%... 40%... 42%... 43%... 45%... 49%... 51%... 52%... 54%... 55%... 57%... 59%... 60%... 62%... 66%... 68%... 72%... 76%... 80%... 84%... 88%... 92%... 96%... Complete!
App installed:
• bundleID: org.mas.myapp
• installationURL: file:///private/var/containers/Bundle/Application/DFC99D25-FC36-462E-91D2-18CDE717ED21/UnCrackable%20Level%201.app/
• launchServicesIdentifier: unknown
• databaseUUID: DA52A5EB-5D39-4628-810E-8F42A5561CDF
• databaseSequenceNumber: 1516
• options:
```

## Xcode

It is also possible to use the Xcode IDE to install iOS apps by executing the following steps:

1. Start Xcode
2. Select **Window/Devices and Simulators**
3. Select the connected iOS device and click on the **+** sign in **Installed Apps**.

## Allow Application Installation on a Non-iPad Device

Sometimes an application must be used on an iPad. If you only have iPhone or iPod touch devices, you can force the application to be installed and used on these devices. You can do this by changing the value of the property **UIDeviceFamily** to the value **1** in the **Info.plist** file.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>

  <key>UIDeviceFamily</key>
  <array>
    <integer>1</integer>
  </array>

</dict>
</plist>
```

Note that changing this value will break the original signature, so you must re-sign the IPA (@MASTG-TECH-0092) to install it on a device that does not have signature validation disabled.

This workaround might not work if the application requires capabilities specific to modern iPads, while your iPhone or iPod is a bit older.

Possible values for the property [UIDeviceFamily](https://developer.apple.com/library/archive/documentation/General/Reference/InfoPlistKeyReference/Articles/iPhoneOSKeys.html#//apple_ref/doc/uid/TP40009252-SW11 "UIDeviceFamily property") can be found in the Apple Developer documentation.
