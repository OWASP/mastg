---
platform: android
title: Object Deserialization Using Serializable with semgrep
id: MASTG-DEMO-0100
code: [kotlin]
test: MASTG-TEST-0337
---

## Sample

The sample code demonstrates an insecure deserialization flaw in an Android app. The app reads a Base64 encoded serialized object from the `payload_b64` `Intent` extra, deserializes it with `ObjectInputStream.readObject()`, and uses the result to overwrite the current user state. In this demo, an attacker can exploit the flaw by launching the activity with a crafted serialized `AdminUser` object, for example with:

```bash
adb shell am start -n org.owasp.mastestapp/.MainActivity --es payload_b64 'rO0ABXNyAChvcmcub3dhc3AubWFzdGVzdGFwcC5NYXN0Z1Rlc3QkQWRtaW5Vc2VyAAAAAAAAAMgCAAFaAAdpc0FkbWlueHIAJ29yZy5vd2FzcC5tYXN0ZXN0YXBwLk1hc3RnVGVzdCRCYXNlVXNlcgAAAAAAAABkAgABTAAIdXNlcm5hbWV0ABJMamF2YS9sYW5nL1N0cmluZzt4cHQAD0V4cGxvaXRlZCBBZG1pbgE='
```

The payload was generated using `PayloadGenerator.java`. It instantiates a `MastgTest.AdminUser` object (with `username = "Exploited Admin"` and `isAdmin = true`), serializes it via `ObjectOutputStream` into a byte array, and encodes the result in base64. The final `adb` command string (with the encoded payload) is printed to stdout and can be used directly to launch the attack. You can regenerate the payload using the following commands, which prints the complete payload as shown above.

```plaintext
mkdir -p build/payloadgen
javac -d build/payloadgen PayloadGenerator.java
java -cp build/payloadgen org.owasp.mastestapp.PayloadGenerator
```

This payload causes the app to accept attacker controlled serialized data and replace `UserManager.currentUser` with an admin object, resulting in privilege escalation without authentication.
**Behavior:** When the app is launched normally and the Start button is pressed, it calls `mastgTest()` and displays the default user state, showing a standard user and `(Not an Admin)`. However, the `MainActivity` also calls `MastgTest(this).processIntent(intent)` in `onCreate()`. This means that if the activity is started with a crafted `payload_b64` extra, the app processes and deserializes that attacker controlled object before the test result is shown. As a result, pressing Start after sending the `adb` command causes the app to display `PRIVILEGED ADMIN!` because the current user state has been overwritten with a malicious `AdminUser` object.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-object-deserialization.yml }}

{{ run.sh }}

## Observation

The output contains the locations of all `ObjectInputStream.readObject()` usages in the code.

{{ output.txt }}

## Evaluation

The test fails because the app deserializes untrusted data from an external `Intent` extra using `ObjectInputStream.readObject()` without any class filtering before deserialization.
`processIntent()` takes the Base64 value from `payload_b64`, decodes it, deserializes it, and if the result is a `BaseUser`, stores it as the current user.

```java
public final void processIntent(Intent intent) ... {
    ...
    if (intent.hasExtra("payload_b64")) {
        String b64Payload = intent.getStringExtra("payload_b64");
        ...
        byte[] serializedPayload = Base64.getDecoder().decode(b64Payload);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serializedPayload));
        Object untrustedObject = ois.readObject();
        ois.close();
        if (untrustedObject instanceof BaseUser) {
            UserManager.INSTANCE.setCurrentUser((BaseUser) untrustedObject);
            ...
        }
    }
}
```

This means an attacker can send a crafted serialized `BaseUser` subclass, such as `AdminUser` with `isAdmin = true`, and overwrite `UserManager.currentUser`. The result is privilege escalation without authentication. In a broader scenario, unsafe deserialization can also lead to code execution if a suitable gadget chain is present.
