---
platform: android
title: Sensitive Data in Serialized Objects
id: MASTG-DEMO-0263
code: [java, kotlin]
test: MASTG-TEST-0263
---

### Sample

This demo uses a sample Android project to demonstrate how to identify serialization usage and potential sensitive data exposure.

```java
// Example of Java Serialization
import java.io.Serializable;

public class UserSession implements Serializable {
    private String username;
    private String sessionToken; // Sensitive data

    public UserSession(String username, String sessionToken) {
        this.username = username;
        this.sessionToken = sessionToken;
    }
}
```

```kotlin
// Example of JSON Serialization with kotlinx.serialization
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userId: String,
    val email: String, // Sensitive data
    val phoneNumber: String? = null
)
```

### Steps

1.  Search the source code for serialization-related keywords.
2.  Analyze the identified classes for sensitive data fields.

{{ run.sh }}

### Observation

The script identifies classes implementing `Serializable` or using serialization-related annotations.

{{ output.txt }}

### Evaluation

The demo highlights that `UserSession` and `UserProfile` contain sensitive data (`sessionToken`, `email`) and are being serialized. Further investigation should be performed to ensure this data is encrypted and HMACed if stored or transmitted.
