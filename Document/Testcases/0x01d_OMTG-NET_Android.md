## Android

### <a name="[OMTG-NET-001]"></a>OMTG-NET-001: Test for unencrypted sensitive data on the network

#### Overview

A functionality of most mobile applications requires sending or receiving information from services on the Internet. This reveals another surface of attacks aimed at data on the way. It's possible for an attacker to sniff or even modify (MiTM attacks) an unencrypted information if he controls any part of network infrastructure (e.g. an WiFi Access Point) [1]. For this reason, developers should make a general rule, that any confidential data cannot be sent in a cleartext [2].

#### White-box Testing

Identify all external endpoints (backend APIs, third-party web services), which communicate with tested application and ensure that all those communication channels are encrypted.

#### Black-box Testing

The recommended approach is to intercept all network traffic coming to or from tested application and check if it is encrypted. A network traffic can be intercepted using one of the following approaches:

* Capture all network traffic, using [Tcpdump]. You can begin live capturing via command:
```
adb shell "tcpdump -s 0 -w - | nc -l -p 1234"
adb forward tcp:1234 tcp:1234
```

Then you can display captured traffic in a human-readable way, using [Wireshark]
```
nc localhost 1234 | sudo wireshark -k -S -i –
```

* Capture all network traffic using intercept proxy, like [OWASP ZAP] or [Burp Suite] and observe whether all requests are using HTTPS instead of HTTP.

> Please note, that some applications may not work with proxies like Burp or ZAP (because of customized HTTP/HTTPS implementation, or Cert Pinning). In such case you may use a VPN server to forward all traffic to your Burp/ZAP proxy. You can easily do this, using [Vproxy]

It is important to capture all traffic (TCP and UDP), so you should run all possible functions of tested application after starting interception. This should include a process of patching application, because sending a patch to application via HTTP may allow an attacker to install any application on victim's device (MiTM attacks).

#### Remediation

Ensure that sensitive information is being sent via secure channels, using [HTTPS], or [SSLSocket] for socket-level communication using TLS.

> Please be aware that `SSLSocket` **does not** verify hostname. The hostname verification should be done by using `getDefaultHostnameVerifier()` with expected hostname. [Here] you can find an example of correct usage.

Some applications may use localhost address, or binding to INADDR_ANY for handling sensitive IPC, what is bad from security perspective, as this interface is accessible for other applications installed on a device. For such purpose developers should consider using secure [Android IPC mechanism].

#### OWASP MASVS

V5.1: "Sensitive data is encrypted on the network using TLS. The secure channel is used consistently throughout the app."

#### OWASP Mobile Top 10

M3 - Insecure Communication

#### CWE

[CWE 319]

#### References

- [1] https://cwe.mitre.org/data/definitions/319.html
- [2] https://developer.android.com/training/articles/security-tips.html#Networking


[Tcpdump]: http://www.androidtcpdump.com/
[Wireshark]: https://www.wireshark.org/download.html
[OWASP ZAP]: https://security.secure.force.com/security/tools/webapp/zapandroidsetup
[Burp Suite]: https://support.portswigger.net/customer/portal/articles/1841101-configuring-an-android-device-to-work-with-burp
[HTTPS]: https://developer.android.com/reference/javax/net/ssl/HttpsURLConnection.html
[SSLSocket]: https://developer.android.com/reference/javax/net/ssl/SSLSocket.html
[Android IPC mechanism]: https://developer.android.com/reference/android/app/Service.html
[CWE 319]: https://cwe.mitre.org/data/definitions/319.html
[Vproxy]: https://github.com/B4rD4k/Vproxy
[Here]: https://developer.android.com/training/articles/security-ssl.html#WarningsSslSocket


### <a name="[OMTG-NET-002]"></a>OMTG-NET-002: Test X.509 certificate verification

#### Overview

Using TLS for transporting sensitive information over the network is essential from security point of view. However, implementing a mechanism of encrypted communication between mobile application and backend API is not a trivial task. Developers often decides for easier, but less secure (e.g. self-signed certificates) solutions to ease a development process what often is not fixed after going on production [1].

#### White-box Testing

There are 2 main issues related with validating TLS connection: the first one is verification if a certificate comes from trusted source and the second one is a check whether the endpoint server presents the right certificate [2].

##### Verifying server certificate

A mechanism responsible for verifying conditions to establish a trusted connection in Android is called `TrustedManager`. Conditions to be checked at this point, are the following:

* is the certificate signed by a "trusted" CA?
* is the certificate expired?
* Is the certificate self-sgined?

You should look in a code if there are control checks of aforementioned conditions. For example, the following code will accept any certificate:

```
TrustManager[] trustAllCerts = new TrustManager[] {
new X509TrustManager()
{

    public java.security.cert.X509Certificate[] getAcceptedIssuers()
    {
        return new java.security.cert.X509Certificate[] {};
    }
    public void checkClientTrusted(X509Certificate[] chain,
    String authType) throws CertificateException
    {

    }
    public void checkServerTrusted(X509Certificate[] chain,
    String authType) throws CertificateException
    {

    }

}};

context.init(null, trustAllCerts, new SecureRandom());
```


##### Hostname verification

Another security fault in TLS implementation is lack of hostname verification. A development environment usually uses some internal addresses instead of valid domain names, so developers often disable hostname verification (or force an application to allow any hostname) and simply forget to change it when their application goes to production. The following code is responsible for disabling hostname verification:

```
final static HostnameVerifier NO_VERIFY = new HostnameVerifier()
{
    public boolean verify(String hostname, SSLSession session)
    {
              return true;
    }
};
```

It's also possible to accept any hostname using a built-in `HostnameVerifier`:

```
HostnameVerifier NO_VERIFY = org.apache.http.conn.ssl.SSLSocketFactory
                             .ALLOW_ALL_HOSTNAME_VERIFIER;
```

Ensure that your application verifies a hostname before setting trusted connection.


#### Black-box Testing

Improper TLS implementation may be found using static analysis tool called MalloDroid [3]. It simply decompiles an application and warns you if it finds something suspicious. You can check your application using a following command:

```
./mallodroid.py -f ExampleApp.apk -d ./outputDir
```

Now, you should be warned if any suspicious code was found by MalloDroid and in `./outputDir` you will find decompiled application for further manual analysis.

A TLS certificate of backend server can be inspected using SSLScan [4]:

```
./sslscan hostname
```

The above command will output any issues related with TLS implementation.

#### Remediation

Ensure, that the hostname and certificate is verified correctly. You can find a help how to overcome common TLS certificate issues here [2].

#### OWASP MASVS

V5.2: "	The app verifies the X.509 certificate of the remote endpoint when the secure channel is established. Only certificates signed by a valid CA are accepted."

#### OWASP Mobile Top 10

M3 - Insecure Communication

#### CWE

[CWE 295]

#### References

- [1] https://www.owasp.org/images/7/77/Hunting_Down_Broken_SSL_in_Android_Apps_-_Sascha_Fahl%2BMarian_Harbach%2BMathew_Smith.pdf
- [2] https://developer.android.com/training/articles/security-ssl.html
- [3] https://github.com/sfahl/mallodroid
- [4] https://github.com/rbsec/sslscan

[CWE 295]: https://cwe.mitre.org/data/definitions/295.html


### <a name="OMTG-NET-003"></a>OMTG-NET-003: Test SSL Pinning

#### Overview

Certificate pinning allows to hard-code in the client the certificate that is known to be used by the server. This technique is used to reduce the threat of a rogue CA and CA compromise. Pinning the server’s certificate take the CA out of games. Mobile applications that implements certificate pinning only have to connect to a limited numbers of server, so a small list of trusted CA can be hard-coded in the application.

#### White-box Testing

The process to implement the SSL pinning involves three main steps outlined below:

1. Obtain a certificate for the desired host
1. Make sure certificate is in .bks format
1. Pin the certificate to an instance of the default Apache Httpclient.

To analyze the correct implementations of the SSL pinning the HTTP client should:

1. Load the keystore:

```java
InputStream in = resources.openRawResource(certificateRawResource);
keyStore = KeyStore.getInstance("BKS");
keyStore.load(resourceStream, password);
```

Once the keystore is loaded we can use the TrustManager that trusts the CAs in our KeyStore :

```java
String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
tmf.init(keyStore);
Create an SSLContext that uses the TrustManager
// SSLContext context = SSLContext.getInstance("TLS");
sslContext.init(null, tmf.getTrustManagers(), null);
```

#### Black-box Testing

Black-box Testing can be performed by launching a MITM attack using your prefered Web Proxy to intercept the traffic exchanged between client (mobile application) and the backend server. If the Proxy is unable to intercept the HTTP requests/responses, the SSL pinning is correctly implemented.

#### Remediation

The SSL pinning process should be implemented as described on the static analysis section.

#### References

- Setting Burp Suite as a proxy for Android Devices : https://support.portswigger.net/customer/portal/articles/1841101-configuring-an-android-device-to-work-with-burp)
- OWASP - Certificate Pinning for Android :  https://www.owasp.org/index.php/Certificate_and_Public_Key_Pinning#Android
