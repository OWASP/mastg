---
platform: android
title: Custom URL Scheme Handler Without Input Validation with semgrep
id: MASTG-DEMO-0152
code: [kotlin]
test: MASTG-TEST-0394
kind: fail
---

## Sample

The app registers the custom URL scheme `mastestapp://transfer` on `DeepLinkActivity`. Any app on the device can open it, for example with `mastestapp://transfer?amount=9999999`.

The deep link handler entry point is `DeepLinkActivity.onCreate`, which reads the incoming URI with `getIntent().getData()` (reverse engineered as `DeepLinkActivity_reversed.java`). The URI is then processed in `MastgTest`, which extracts the `amount` query parameter with `getQueryParameter("amount")` and passes it directly to `processTransfer()` as a raw `String`, without calling `toLong()` or performing any bounds or range check.

{{ MastgTest.kt # DeepLinkActivity_reversed.java # MastgTest_reversed.java }}

The custom URL scheme is declared in the manifest:

{{ AndroidManifest_reversed.xml # AndroidManifest.xml }}

## Steps

Run @MASTG-TOOL-0110 with two rules. The first rule scans the reversed manifest to locate the custom URL scheme declarations and the activity that handles them. The second rule scans the reverse-engineered handler code to locate the deep link handler entry point (`getIntent().getData()`) and where the URI parameters are read (`getQueryParameter`), so they can be reviewed for validation (see @MASTG-TECH-0023).

{{ ../../../../rules/mastg-android-custom-deeplink-scheme.yml }}

{{ ../../../../rules/mastg-android-deeplink-unvalidated-parameter.yml }}

{{ run.sh }}

## Observation

The first rule identified the `mastestapp://transfer` custom URL scheme declared on `DeepLinkActivity`, confirming that any app on the device can reach this handler. The second rule pointed to the handler entry point in `DeepLinkActivity_reversed.java` (the reverse engineered `DeepLinkActivity`), where the incoming URI is read with `getIntent().getData()` (line 24), and to `MastgTest_reversed.java`, where the `amount` query parameter is extracted with `getQueryParameter("amount")` (line 25).

{{ output.txt }}

Following the URI from the handler entry point, `DeepLinkActivity` (`DeepLinkActivity_reversed.java`) reads it with `getIntent().getData()` and the `amount` parameter is later extracted with `getQueryParameter("amount")` in `MastgTest_reversed.java`, then passed straight to `processTransfer()` without validation.

## Evaluation

The test case fails because the handler uses the `amount` query parameter directly, without validating it. Analyzing the handler method confirms this:

```java
String amount = data.getQueryParameter("amount");   // raw String from the URI
...
return processTransfer(amount);                      // used as-is
...
private final String processTransfer(String amount) {
    return "Transferring " + amount + " units";      // no toLong(), no bounds check
}
```

The value is never:

- Converted to a numeric type (e.g., `toLong()`).
- Checked against an expected range.

Any app can therefore trigger `mastestapp://transfer?amount=9999999` or supply a non-numeric value to bypass the expected business logic constraints. Use @MASTG-TOOL-0004 to confirm:

```bash
adb shell am start -a android.intent.action.VIEW -d "mastestapp://transfer?amount=9999999"
adb shell am start -a android.intent.action.VIEW -d "mastestapp://transfer?amount=-1"
adb shell am start -a android.intent.action.VIEW -d "mastestapp://transfer?amount=not-a-number"
```

After triggering the deep link, tap **Start** in the app to process it. The result (for example, `Transferring 9999999 units`) confirms that the handler accepts the attacker-controlled value and uses it without numeric conversion or bounds checking.
