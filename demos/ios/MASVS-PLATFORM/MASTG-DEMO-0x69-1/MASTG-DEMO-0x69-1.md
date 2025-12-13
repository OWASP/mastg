---
platform: ios
title: Declaring Dangerous Permissions in embedded.mobileprovision
code: [swift]
id: MASTG-DEMO-0x69-1
test: MASTG-TEST-0069
---

### Sample

The code snippet below shows sample entitlements declared in the provisioning profile. The embedded.mobileprovision file contains multiple entitlements that the app uses to request special permissions.

{{ decoded.mobileprovision.xml }}


### Steps
1. Sign the `.ipa` using @MASTG-TECH-0092.
2. Unzip the app package (@MASTG-TECH-0058) and locate the provisioning profile , which in this case is at `./Payload/MASTestApp.app/embedded.mobileprovision`.
3. Run the `run.sh` script to decode the binary formatted profile into a readable XML format using @MASTG-TECH-.

{{ run.sh }}

### Observation

The output reveals the entitlements declared in the app's embedded.mobileprovision file.

{{ output.txt }}

### Evaluation

The test fails because the app declares multiple entitlements that may be excessive for its core functionality.