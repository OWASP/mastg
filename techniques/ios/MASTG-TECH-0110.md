---
title: Intercepting Flutter HTTPS Traffic
platform: ios
---

Flutter is an open-source UI software development kit (SDK) created by Google. It is used for building natively compiled applications for mobile, web, and desktop from a single codebase. Flutter uses Dart, which is not proxy-aware and uses its own certificate store. The application doesn't use the system's proxy configuration and sends data directly to the server. Connections are verified against built-in certificates; any certificates installed on the system are ignored. As a result, it is not possible to intercept HTTPS requests because the proxy's certificate will never be trusted.

To intercept Flutter HTTPS traffic, we need to deal with two problems:

- Make sure the traffic is sent to the proxy.
- Disable the TLS verification of any HTTPS connection.

There are generally two approaches to this: **@MASTG-TOOL-0100** and **@MASTG-TOOL-0039**.

- **reFlutter**: This tool creates a modified version of the Flutter module, which is then repackaged into the IPA. It configures the internal libraries to use a specified proxy and disable TLS verification.
- **Frida**: The [disable-flutter-tls.js script](https://github.com/NVISOsecurity/disable-flutter-tls-verification) can dynamically remove the TLS verification without the need for repackaging. As it doesn't modify the proxy configuration, additional steps are needed (e.g. VPN, DNS, iptables, Wi-Fi hotspot).

## Intercepting Traffic using reFlutter

1. Patch the app to enable traffic interception.

    Run the command to patch the app, select the **Traffic monitoring and interception** option, and enter the IP address of the machine running the interception proxy.

    ```plaintext
    $ reflutter demo.ipa

    Choose an option:

        Traffic monitoring and interception
        Display absolute code offset for functions

    [1/2]? 1

    Example: (192.168.1.154) etc.
    Please enter your BurpSuite IP: 192.168.29.216
    ```

    This will create a **release.RE.ipa** file in the output folder.

2. Sign (@MASTG-TECH-0092) the patched **release.RE.ipa** with the Apple certificates. This will create a signed ".ipa" file in the output folder.

3. Install the signed patched app on the mobile device.

4. Configure the interception proxy. For example, in Burp:

   - Under Proxy -> Proxy settings -> Add new Proxy setting.
   - Bind listening Port to `8083`.
   - Select `Bind to address` to `All interfaces`.
   - Request Handling -> support for invisible proxying.

5. Open the app and start intercepting traffic.

## Intercepting Traffic using Wi-Fi Hotspot / openVPN with Frida

1. Configure using [Wi-Fi hotspot / openVPN](https://blog.nviso.eu/2022/08/18/intercept-flutter-traffic-on-ios-and-android-http-https-dio-pinning/) method to redirect requests to Burp.

2. Install the @MASTG-APP-0025 on the mobile device.

3. Configure the interception proxy. For example, in Burp:

   - Under Proxy -> Proxy settings -> Add new Proxy setting.
   - Bind listening Port to `8080`.
   - Select `Bind to address` to `All interfaces`.
   - Request Handling -> support for invisible proxying.

4. Run the @MASTG-TOOL-0101 Frida script.

    ```bash
    frida -U -f eu.nviso.flutterPinning -l disable-flutter-tls.js
    ```

5. Start intercepting traffic.
