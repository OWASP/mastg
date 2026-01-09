---
platform: android
title: Usage of Clear Text Network Protocols
id: MASTG-DEMO-0082
code: [kotlin]
test: MASTG-TEST-0238
---

## Sample

This sample creates insecure and secure URIs using the following Android API:

- `java.net.URL`
- `java.net.URI`
- `android.net.Uri`
- `android.media.MediaPlayer`

The following URI schemes are used in this demo:

```kotlin
data class Schemes(
    val insecure: String,
    val secure: String
)

val schemes = listOf(
  Schemes("http", "https"),
  Schemes("ws", "wss"),
  Schemes("sip", "sips"),
  Schemes("ldap", "ldaps"),
  Schemes("ftp", "ftps"),
  Schemes("rtsp", "rtsps"),
  Schemes("stun", "stuns")
)
```

The demo does not really load the remote resources. If these URIs are used, data could be sent in clear text over the network. However, it may be that @MASTG-KNOW-0014 prohibits the connection setup.

{{ MastgTest.kt }}

### Steps

1. Install the app on a device (@MASTG-TECH-0005)
2. Make sure you have @MASTG-TOOL-0001 installed on your machine and the frida-server running on the device
3. Run `run.sh` to spawn the app with Frida
4. Click the **Start** button
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

{{ hooks.js # run.sh }}

### Observation

The script will generate the file `output.json` which contains information about how the functions were called.

### Evaluation

Run `evaluate.sh output.json`. This script will parse the generated `output.json`, assess the URIs and write the identified risks to the file `evaluation.txt`.

{{ evaluation.txt # evaluate.sh}}
