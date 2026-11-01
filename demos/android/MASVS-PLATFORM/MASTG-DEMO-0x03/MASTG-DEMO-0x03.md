---
platform: android
title: Reading Exposed UI Data with a Non-Tool Accessibility Service
id: MASTG-DEMO-0x03
code: [kotlin, xml]
kind: attack
test: MASTG-TEST-0404
---

## Sample

This attack demo runs a separate attacker app (`org.owasp.mastestapp.a11y`) against two victim samples: the `View`-based form from @MASTG-DEMO-0159 and the Jetpack Compose payment dialog from @MASTG-DEMO-0x01. The attacker implements a custom Android `AccessibilityService` that declares `isAccessibilityTool="false"` and requests access to window content. The demonstration requires Android 14 (API level 34) or higher.

The attacker does not know the victim app's package name, layout, or element labels. It observes the active app through `rootInActiveWindow`, recursively enumerates accessible nodes, and logs visible elements containing text, hints, descriptions, or clickable or editable controls. To reduce noise, it skips empty layout containers and only reports new or changed elements. It does not invoke actions on the discovered controls.

In the `View` sample, the username and password aren't marked sensitive, the one-time code is protected through `filterTouchesWhenObscured`, and the card number inherits protection from its parent container. The **Confirm Payment** button explicitly disables the protection inherited from that container.

In the Compose sample, the username explicitly sets `isSensitiveData` to `false`; the password and **Confirm Payment** button don't set it. The card number sets it to `true`. The username also uses `sensitiveContent` for screen-sharing protection, which is separate from accessibility data sensitivity.

{{ MastgTest.kt # AndroidManifest.xml # accessibility_service.xml }}

## Steps

1. Build and install the attacker APK (`org.owasp.mastestapp.a11y`) using @MASTG-TECH-0005.
2. In the attacker app, tap **Start** and enable **MASTG Accessibility Observer** in Android's Accessibility Settings, or enable it with `adb` as described in @MASTG-TECH-0177. The service must remain enabled while observing the victims.
3. Build and install the victim from @MASTG-DEMO-0159 (@MASTG-TECH-0005).
4. From the demo directory, run `./run.sh` to capture the accessibility service's logcat messages. The script clears existing logcat messages and writes to `output.txt`. We renamed it to `output_views.txt` for this demo.
5. Open the victim app, tap **Start** to display its form, and enter dummy values. Stop the capture with `Ctrl+C` and temporarily rename `output.txt` to `output_views.txt`.
6. Build and install the victim from @MASTG-DEMO-0x01, then repeat the capture. Rename the resulting `output.txt` to `output_compose.txt`.

The following screenshots show, from left to right, the service listed under **Downloaded apps** in Accessibility Settings, its settings page, the confirmation dialog that Android shows before enabling it, and the attacker app with the service enabled (its accessibility button appears on the right edge):

<img src="Images/Demos/MASTG-DEMO-0x03/a11y-1.png" width="24%" />
<img src="Images/Demos/MASTG-DEMO-0x03/a11y-2.png" width="24%" />
<img src="Images/Demos/MASTG-DEMO-0x03/a11y-3.png" width="24%" />
<img src="Images/Demos/MASTG-DEMO-0x03/a11y-4.png" width="24%" />

Note that both victims use the same package name, so install and test them separately.

{{ run.sh }}

## Observation

**View-Based Form:**

The attacker discovers the **username** field as an editable node and retrieves its actual value, `mastester`, along with its hint, `Username`. It also discovers the **password** field, the **Confirm Payment** button, and the **Cancel** button, without searching for those labels in advance.

{{ output_views.txt }}

The **password** field appears as an editable node marked as a password. Its reported value is mostly masked with bullet characters, but the node remains exposed and some newly typed characters are briefly readable in the captured changes. The log does not capture every character, so it does not demonstrate recovery of the complete password.

The **one-time code** and **card number** don't appear as editable nodes in the attacker's hierarchy. The attacker cannot name or count these missing elements by itself because Android doesn't provide placeholders for protected nodes. Their absence can be assessed by comparing this output against the full view hierarchy obtained in @MASTG-DEMO-0160, which lists every view on the same screen together with its effective accessibility data sensitivity.

**Compose Payment Dialog:**

{{ output_compose.txt }}

The attacker discovers the **username** and **password** as editable nodes, with their labels in child text nodes. It reads the **username** value `mastester`. Despite the **password** node being marked as a password, the captured changes contain a growing unmasked password value. Both buttons appear as clickable nodes with their labels in child text nodes.

The **card number** field and its label don't appear in this capture. The Compose semantics dump in @MASTG-DEMO-0x02 identifies the **card number** field and shows its `isSensitiveData=true` setting, providing a comparison with the attacker's restricted view.

## Evaluation

The test case fails because, in both samples, a non-tool accessibility service can read sensitive input and discover a clickable control that performs a sensitive action.

- **View-based form:** the **username** and **password** aren't marked sensitive, and the **Confirm Payment** button explicitly uses `ACCESSIBILITY_DATA_SENSITIVE_NO` despite its sensitive parent. The attacker reads the **username** and observes some **password** characters; the **Confirm Payment** button is exposed as clickable.
- **Compose dialog:** the **username** explicitly sets `isSensitiveData=false`, and the **password** and **Confirm Payment** button don't set the property. The attacker reads the **username** and the unmasked **password** value in the captured changes and finds the confirmation button as clickable. Note that the **password** is actually masked in the UI but exposed in the accessibility service's captured changes.

The **Cancel** button is accessible in both samples, as expected. In contrast, the protected one-time code and card number are absent from the `View` sample's observed hierarchy, and the protected card number is absent from the Compose hierarchy. @MASTG-DEMO-0160 and @MASTG-DEMO-0x02 show the corresponding views and Compose semantics, respectively. Absence from a generic accessibility dump alone isn't sufficient to prove why a particular element is missing, which is why these comparisons matter.
