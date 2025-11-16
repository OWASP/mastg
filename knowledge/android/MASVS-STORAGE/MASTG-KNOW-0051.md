---
masvs_category: MASVS-STORAGE
platform: android
title: Process Memory
---

Many applications handle sensitive information, provided either by the user, the operating system or the backend. This data is often available in-memory while the user is using the application, as it is needed for the application to function. When the application no longer actively needs the sensitive data, it should dispose of it immediately.

The investigation of an application's memory can be done from memory dumps, and from analyzing the memory in real time via a debugger or a binary instrumentation framework (see @MASTG-TECH-0044 and @MASTG-TECH-0031).

For an overview of possible sources of data exposure, check the documentation and identify application components before you examine the source code. For example, sensitive data from a backend may be in the HTTP client, the XML parser, etc. You want all these copies to be removed from memory as soon as possible.

In addition, understanding the application's architecture and the architecture's role in the system will help you identify sensitive information that doesn't have to be exposed in memory at all. For example, assume your app receives data from one server and transfers it to another without any processing. That data can be handled in an encrypted format, which prevents exposure in memory.

However, if you need to expose sensitive data in memory, you should make sure that your app is designed to expose as few data copies as possible as briefly as possible. In other words, you want the handling of sensitive data to be centralized (i.e., with as few components as possible) and based on primitive, mutable data structures.

The latter requirement gives developers direct memory access. Make sure that they use this access to overwrite the sensitive data with dummy data (typically zeroes). Examples of preferable data types include `byte []` and `char []`, but not `String` or `BigInteger`. Whenever you try to modify an immutable object like `String`, you create and change a copy of the object.

Using non-primitive mutable types like `StringBuffer` and `StringBuilder` may be acceptable, but it's indicative and requires care. Types like `StringBuffer` are used to modify content (which is what you want to do). To access such a type's value, however, you would use the `toString` method, which would create an immutable copy of the data. There are several ways to use these data types without creating an immutable copy, but they require more effort than using a primitive array. Safe memory management is one benefit of using types like `StringBuffer` , but this can be a two-edged sword. If you try to modify the content of one of these types and the copy exceeds the buffer capacity, the buffer size will automatically increase. The buffer content may be copied to a different location, leaving the old content without a reference use to overwrite it.

Support for true in-place destruction is rare, and even when implemented, it cannot remove other copies that may exist in heap memory. This is an inherent limitation of Java and Android memory management for in-heap secrets. Examples include:

- Overwriting the byte array returned by `secretKey.getEncoded()` [does not clear the internal key](https://bugs.openjdk.org/browse/JDK-6263419), because `getEncoded()` returns a copy. Zeroing that copy has no effect on the bytes held by the key object.
- `SecretKeySpec` does not provide a functional `destroy()` implementation, and this [gap has been tracked in the JDK](https://bugs.openjdk.org/browse/JDK-8160206). Calling `destroy()` on a `SecretKey` backed by `SecretKeySpec` therefore does not guarantee that the key material is wiped.
- RSA keys handled outside the Android Keystore are backed by immutable `BigInteger` objects, which cannot be cleared once created, leaving their data in memory until garbage collected.
- User-provided data (such as credentials, social security numbers, or credit card information) entered through `EditText` is delivered via the `Editable` interface. If your app does not supply a custom `Editable.Factory`, the data will likely remain in memory longer than necessary. The default `Editable` implementation, `SpannableStringBuilder`, has the same retention issues as Java's `StringBuilder` and `StringBuffer`, leaving sensitive content in memory until garbage collected.

> **Why doesn't the OWASP MASTG have tests for this anymore?**
>
> The tests for sensitive data in process memory were removed from the OWASP MASTG because they provide limited practical value in assessing real-world risks and are difficult to execute reliably. Memory inspection typically requires privileged access, such as root or jailbreak, which falls outside the threat model for most production environments. Such access already compromises the device's overall security, making memory-level findings less meaningful in isolation.
>
> Moreover, results from memory dumps are highly variable and prone to false positives or negatives depending on timing, garbage collection, and system-level optimizations. These inconsistencies make the test unsuitable as a standardized or repeatable security control.
>
> Instead of relying on runtime memory inspection, OWASP now emphasizes preventing these issues through secure design and development practices. Frameworks like the [OWASP Software Assurance Maturity Model (SAMM)](https://owaspsamm.org/model/) and [NIST Special Publication (SP) 800-218, Secure Software Development Framework (SSDF)](https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-218.pdf) guide developers to handle sensitive data securely in memory, minimize exposure windows, and use safe APIs for cryptography and data storage. The related OWASP MASVS categories on secure data storage (@MASVS-STORAGE), secure communication (@MASVS-NETWORK), and secure authentication and session management (@MASVS-AUTH) already cover the necessary safeguards to prevent sensitive information from being inadvertently exposed at runtime. For specific guidance about handling secrets in memory, see the [OWASP Secrets Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html#25-handling-secrets-in-memory).
