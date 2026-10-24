---
title: Monitoring Deep Link Handlers at Runtime with Frida
platform: android
---

Here you will use the list of deep links from the static analysis (see @MASTG-TECH-0172) to iterate and determine each handler method and the processed data, if any. You will first start a @MASTG-TOOL-0031 hook and then begin invoking the deep links.

The following example assumes a target app that accepts this deep link: `deeplinkdemo://load.html`. However, we don't know the corresponding handler method yet, nor the parameters it potentially accepts.

**[Step 1] Frida Hooking:**

You can use the script ["Android Deep Link Observer"](https://codeshare.frida.re/@leolashkevych/android-deep-link-observer/) from @MASTG-TOOL-0032 to monitor all invoked deep links triggering a call to `Intent.getData`. You can also use the script as a base to include your own modifications depending on the use case at hand. In this case we [included the stack trace](https://github.com/FrenchYeti/frida-trick/blob/master/README.md) in the script since we are interested in the method which calls `Intent.getData`.

**[Step 2] Invoking Deep Links:**

Now you can invoke any of the deep links using @MASTG-TOOL-0004 and the [Activity Manager (am)](https://developer.android.com/training/app-links/deep-linking#testing-filters "Activity Manager") which will send intents within the Android device. For example:

```bash
adb shell am start -W -a android.intent.action.VIEW -d "deeplinkdemo://load.html/?message=ok#part1"

Starting: Intent { act=android.intent.action.VIEW dat=deeplinkdemo://load.html/?message=ok }
Status: ok
LaunchState: WARM
Activity: com.mstg.deeplinkdemo/.WebViewActivity
TotalTime: 210
WaitTime: 217
Complete
```

> This might trigger the disambiguation dialog when using the "http/https" schema or if other installed apps support the same custom URL schema. You can include the package name to make it an explicit intent.

This invocation will log the following:

```bash
[*] Intent.getData() was called
[*] Activity: com.mstg.deeplinkdemo.WebViewActivity
[*] Action: android.intent.action.VIEW

[*] Data
- Scheme: deeplinkdemo://
- Host: /load.html
- Params: message=ok
- Fragment: part1

[*] Stacktrace:

android.content.Intent.getData(Intent.java)
com.mstg.deeplinkdemo.WebViewActivity.onCreate(WebViewActivity.kt)
android.app.Activity.performCreate(Activity.java)
...
com.android.internal.os.ZygoteInit.main(ZygoteInit.java)
```

In this case we've crafted the deep link including arbitrary parameters (`?message=ok`) and fragment (`#part1`). We still don't know if they are being used. The information above reveals useful information that you can use now to reverse engineer the app.

- File: `WebViewActivity.kt`
- Class: `com.mstg.deeplinkdemo.WebViewActivity`
- Method: `onCreate`

> Sometimes you can even take advantage of other applications that you know interact with your target app. You can reverse engineer the app, (e.g. to extract all strings and filter those which include the target deep links, `deeplinkdemo:///load.html` in the previous case), or use them as triggers, while hooking the app as previously discussed.
