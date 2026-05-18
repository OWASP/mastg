---
platform: ios
title: Integrity and Authenticity Validation of Local Storage Data with r2
code: [swift]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
status: draft
---

## Sample

The sample implements a small role-based demo using `UserDefaults`. On the first run, it initializes two entries (`user_role_insecure` and `user_role_secure`) with the value `user`. The secure entry is stored together with an HMAC. On subsequent runs, the app reads both values and uses them to simulate a security-relevant decision.

The key difference is that `user_role_insecure` is loaded and used directly without any integrity check, whereas `user_role_secure` is validated with an HMAC before the value is trusted.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ local_storage.r2 }}

{{ run.sh }}

## Observation

The output shows `UserDefaults.string(forKey:)` being called three times in `mastgTest` and once in `computeHMAC(for:)`. The HMAC-related symbol (`HMAC.authenticationCode(for:using:)`) appears only inside `computeHMAC(for:)`, not adjacent to the insecure read path in `mastgTest`.

{{ output.txt }}

## Evaluation

The test case fails because at least one security-relevant local storage read loads and trusts data without integrity or authenticity validation.

The output is a starting point, not the conclusion. To determine whether the test fails, inspect how the loaded values are handled after they are read.

### Failing case: data loaded and trusted without integrity validation

Reversing `mastgTest` shows that one role is loaded as:

```swift
let insecureRole = defaults.string(forKey: keyRoleInsecure) ?? "error"
```

No HMAC is computed or compared before `insecureRole` is used in the `insecureResult` expression. Because this value can influence a security-relevant decision (role check), and no verification is performed, this path fails the test.

You can demo this by modifying the `UserDefaults` plist file directly on a jailbroken device or a simulator:

```sh
# On a simulator (adjust the UUID and bundle ID):
plutil -replace user_role_insecure -string admin \
  ~/Library/Developer/CoreSimulator/Devices/<UUID>/data/Containers/Data/Application/<AppUUID>/Library/Preferences/org.owasp.mastestapp.plist
```

Restart the app and tap **Start** — you will see `❌ Insecure check bypassed.`

### Passing case: data validated with HMAC before use

The second role is loaded and verified through `computeHMAC(for:)` before being used. The HMAC call is visible in the output adjacent to the `computeHMAC` function rather than the main read path, confirming that the result of `UserDefaults.string(forKey: keyRoleSecure)` is not used until the HMAC is validated.

Modifying only `user_role_secure` in the plist (without updating the HMAC) will cause the comparison to fail, and the function will return `"tampered or missing"` instead of the tampered value.

### Final note

Although this demo uses a hardcoded HMAC key for illustration purposes, a real implementation must store the key in the Keychain. An attacker who recovers the hardcoded key can compute a valid HMAC for any forged value and bypass the integrity check. This separate issue is covered by a different test.
