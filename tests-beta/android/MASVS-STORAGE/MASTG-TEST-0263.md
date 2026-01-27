---
platform: android
title: Sensitive Data in Serialized Objects
id: MASTG-TEST-0263
type: [static, dynamic]
weakness: MASWE-0001
best-practices: [MASTG-BEST-0001]
profiles: [L1, L2, P]
knowledge: [MASTG-KNOW-0021]
---

## Overview

Serialized objects on Android might contain sensitive data that, if not properly protected, could be exposed or manipulated. This test case focuses on identifying sensitive information within serialized data and ensuring it is adequately protected against unauthorized access and tampering.

There are a few generic remediation steps that you can always take:

1. Make sure that sensitive data has been encrypted and HMACed/signed after serialization/persistence. Evaluate the signature or HMAC before you use the data. See the chapter "[Android Cryptographic APIs](../../../Document/0x05e-Testing-Cryptography.md)" for more details.
2. Make sure that the keys used in step 1 can't be extracted easily. The user and/or application instance should be properly authenticated/authorized to obtain the keys. See the chapter "[Data Storage on Android](../../../Document/0x05d-Testing-Data-Storage.md)" for more details.
3. Make sure that the data within the de-serialized object is carefully validated before it is actively used (e.g., no exploit of business/application logic).

## Static Analysis

### Identification of Serialization

Search the source code for keywords related to the serialization libraries used by the application:

- **Java Serialization**: `import java.io.Serializable`, `implements Serializable`
- **JSON**:
    - `JSONObject`: `import org.json.JSONObject;`, `import org.json.JSONArray;`
    - `GSON`: `import com.google.gson`, `new Gson();`, annotations like `@Expose`, `@SerializedName`
    - `Jackson`: `import com.fasterxml.jackson.core`, `import org.codehaus.jackson`
    - `Moshi`: `import com.squareup.moshi`
    - `kotlinx.serialization`: `import kotlinx.serialization`
- **ORM**:
    - `OrmLite`: `import com.j256.*`
    - `SugarORM`: `extends SugarRecord<Type>`
    - `GreenDAO`: `import org.greenrobot.greendao.*`
    - `Realm`: `import io.realm.*`
- **Parcelable**: `implements Parcelable`, `writeToParcel`, `createFromParcel`
- **Protocol Buffers**: `import com.google.protobuf`

### Analysis of Serialized Data

For each identified serialization instance:

1.  Identify the classes being serialized and the data fields they contain.
2.  Determine if any of these fields contain sensitive information (e.g., PII, credentials, session tokens).
3.  Check if the sensitive data is encrypted/HMACed.
4.  Verify that the encryption keys are not hard-coded and are securely stored (e.g., using the Android Keystore).

## Dynamic Analysis

Use the following techniques to identify sensitive data in serialized objects during runtime:

1.  **File System Monitoring**: Monitor the app's private data directory (`/data/data/<package-name>/`) for files created or modified during app execution. Serialized objects are often stored in files with extensions like `.ser`, `.json`, `.db`, or `.realm`.
2.  **Method Hooking**: Use @MASTG-TOOL-0001 to hook into serialization and deserialization methods to inspect the objects being processed.
    - For Java Serialization: Hook `java.io.ObjectOutputStream.writeObject` and `java.io.ObjectInputStream.readObject`.
    - For JSON: Hook methods like `toJson` and `fromJson` in GSON, or `writeValueAsString` and `readValue` in Jackson.
3.  **Traffic Analysis**: If serialized objects are sent over the network, use a proxy to intercept and inspect the traffic.

## Evaluation

The test fails if:

- Sensitive data is found in serialized objects without being encrypted and signed (HMAC).
- Encryption keys used for protecting serialized data are easily extractable (e.g., hard-coded).
- Data within de-serialized objects is used without proper validation, leading to potential security issues.
