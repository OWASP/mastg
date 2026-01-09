---
masvs_category: MASVS-NETWORK
platform: android
title: Android Network APIs
---

Android provides different APIs to establish network connections. They can separated into Java/Kotlin app and native code.

## Package `java.net`

The `java.net` package can be roughly divided in two sections:

1. A High Level API, which deals with the following abstractions:
    - URIs, which represent Universal Resource Identifiers.
    - URLs, which represent Universal Resource Locators.
    - Connections, which represents connections to the resource pointed to by URLs.
2. A Low Level API, which deals with the following abstractions:
    - Addresses, which are networking identifiers, like IP addresses.
    - Sockets, which are basic bidirectional data communication mechanisms.
    - Interfaces, which describe network interfaces.

### Using java.net High Level API

The class `HttpURLConnection` can be used to establish HTTP but also HTTPS connections depending on the specified protocol. The following code demonstrates a GET request:

```kotlin
val con = URL("https://mas.owasp.org").openConnection() as HttpURLConnection
try {
    con.requestMethod = "GET"
    con.setRequestProperty("Connection", "close")
    val responseCode = con.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK)  {
        val reader = BufferedReader(InputStreamReader(con.inputStream))
        reader.forEachLine {
            line  -  > println(line)
        }
    }
} catch (e: Exception) {
    print(e)
} finally {
    con.disconnect()
}
```

### Using `java.net` Low Level API

Using the low level API, developers can establish TCP/UDP connection using `java.net.Socket` or `java.net.DatagramSocket` respectively.

!!! Warning
    @MASTG-KNOW-0014 cannot not apply to raw sockets. The means it is the developers responsibility to make sure, that no sensitive data is transmitted. It is therefore not recommended using raw sockets, if more secure options are available.

The following code demonstrates how a TCP connection is established and the plain text `HEARTBEAT: OK` is transmitted:

```kotlin
try {
    Socket("10.0.0.10", 80).use {
        socket  -  > val writer = PrintWriter(socket.getOutputStream(), true)
        writer.println("HEARTBEAT: OK")
        writer.flush()
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        reader.forEachLine {
            line  -  > println(line)
        }
    }
} catch (e: Exception) {
    print(e)
}
```

`javax.net.ssl.SSLSocket` is a direct subclass from `java.net.Socket` and can be used to establish TLS connections. The class does apply the policy defined in the @MASTG-KNOW-0014. The connection will _not be established_ if certificate chain does not contain a pinned a certificate.

## Package `android.net`

This package implements network related classes which server various mobile devices or Android specific needs. Examples are:

- **CaptivePortal** A class allowing apps handling the ConnectivityManager.ACTION_CAPTIVE_PORTAL_SIGN_IN activity to indicate to the system different outcomes of captive portal sign in.
- **ConnectivityManager** Class that answers queries about the state of network connectivity such as monitoring Wi-Fi, GPRS or UMTS connections.
- **DnsResolver**: Dns resolver class for asynchronous dns querying.
- **LocalSocket**: Creates a (non-server) socket in the UNIX-domain namespace. Can be used to connect to an existing local server socket. This is also possible across different apps.
- **LocalServerSocket**: Non-standard class for creating an inbound UNIX-domain socket in the Linux abstract namespace. Other apps can connect to this socket using `LocalSocket`.
- **Proxy**: A convenience class for accessing the user and default proxy settings.
- **Uri**: Immutable URI reference.
- **VpnManager**: This class provides an interface for apps to manage platform VPN profiles. Apps can use this API to provide profiles with which the platform can set up a VPN without further app intermediation.

While most classes add functionality not covered by `java.net`, some implement the same functionality but differ in certain areas.

For examples classes like `java.net.InetAddress` use the operating systems resolver and are synchronous. `android.net.DnsResolver` on the other hand resolves domains asynchronously and can caches queries system wide.

A second example is `android.net.Uri`. Compared to `java.net.URI` and `java.net.URL`, Androids implementation does generally less validation, is immutable and `Parcelable`. They are optimized for internal use, such as for Intents or ContentProviders.

## Native Code Networking

When developing native code using the Android NDK toolset, there are no libraries available by default which implement protocols above the transport layer. Developers can interact with POSIX sockets and the [NDK Networking API](https://developer.android.com/ndk/reference/group/networking).

If a developer wants to do networking with protocols such as HTTP or TLS in the native code, they have to provide their own library.

!!! Warning
    @MASTG-KNOW-0014 may not be respected by custom libraries. The means it is the developers responsibility to make sure, that no sensitive data is transmitted. It is therefore not recommended doing networking in native code in general.
