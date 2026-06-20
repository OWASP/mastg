---
title: Monitoring UIActivity Data Sharing
platform: ios
---

When an app shares data through the system Share Sheet (see @MASTG-KNOW-0081), you can inspect the shared items, custom activities, and excluded types at runtime by hooking the relevant Objective-C methods:

- Hook [`init(activityItems:applicationActivities:)`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller/init(activityitems:applicationactivities:)) to read the `activityItems` and `applicationActivities`.
- Hook the [`excludedActivityTypes`](https://developer.apple.com/documentation/uikit/uiactivityviewcontroller/excludedactivitytypes) getter to read the excluded activities.

The following @MASTG-TOOL-0039 script implements both hooks:

```js
Interceptor.attach(
ObjC.classes.
    UIActivityViewController['- initWithActivityItems:applicationActivities:'].implementation, {
  onEnter: function (args) {

    printHeader(args)

    this.initWithActivityItems = ObjC.Object(args[2]);
    this.applicationActivities = ObjC.Object(args[3]);

    console.log("initWithActivityItems: " + this.initWithActivityItems);
    console.log("applicationActivities: " + this.applicationActivities);

  },
  onLeave: function (retval) {
    printRet(retval);
  }
});

Interceptor.attach(
ObjC.classes.UIActivityViewController['- excludedActivityTypes'].implementation, {
  onEnter: function (args) {
    printHeader(args)
  },
  onLeave: function (retval) {
    printRet(retval);
  }
});

function printHeader(args) {
  console.log(Memory.readUtf8String(args[1]) + " @ " + args[1])
};

function printRet(retval) {
  console.log('RET @ ' + retval + ': ' );
  try {
    console.log(new ObjC.Object(retval).toString());
  } catch (e) {
    console.log(retval.toString());
  }
};
```

Store this as a JavaScript file, e.g. `inspect_send_activity_data.js`, and load it:

```bash
frida -U Telegram -l inspect_send_activity_data.js
```

Observe the output when you first share a picture:

```js
[*] initWithActivityItems:applicationActivities: @ 0x18c130c07
initWithActivityItems: (
    "<UIImage: 0x1c4aa0b40> size {571, 264} orientation 0 scale 1.000000"
)
applicationActivities: nil
RET @ 0x13cb2b800:
<UIActivityViewController: 0x13cb2b800>

[*] excludedActivityTypes @ 0x18c0f8429
RET @ 0x0:
nil
```

and then a text file:

```js
[*] initWithActivityItems:applicationActivities: @ 0x18c130c07
initWithActivityItems: (
    "<QLActivityItemProvider: 0x1c4a30140>",
    "<UIPrintInfo: 0x1c0699a50>"
)
applicationActivities: (
)
RET @ 0x13c4bdc00:
<_UIDICActivityViewController: 0x13c4bdc00>

[*] excludedActivityTypes @ 0x18c0f8429
RET @ 0x1c001b1d0:
(
    "com.apple.UIKit.activity.MarkupAsPDF"
)
```

You can see that:

- For the picture, the activity item is a `UIImage` and there are no excluded activities.
- For the text file there are two different activity items and `com.apple.UIKit.activity.MarkupAsPDF` is excluded.

In this example there were no custom `applicationActivities` and only one excluded activity. To better illustrate what you can expect from other apps, the following output comes from another app sharing a picture, where you can see several custom application activities and excluded activities (the output was edited to hide the originating app's name):

```js
[*] initWithActivityItems:applicationActivities: @ 0x18c130c07
initWithActivityItems: (
    "<SomeActivityItemProvider: 0x1c04bd580>"
)
applicationActivities: (
    "<SomeActionItemActivityAdapter: 0x141de83b0>",
    "<SomeActionItemActivityAdapter: 0x147971cf0>",
    "<SomeOpenInSafariActivity: 0x1479f0030>",
    "<SomeOpenInChromeActivity: 0x1c0c8a500>"
)
RET @ 0x142138a00:
<SomeActivityViewController: 0x142138a00>

[*] excludedActivityTypes @ 0x18c0f8429
RET @ 0x14797c3e0:
(
    "com.apple.UIKit.activity.Print",
    "com.apple.UIKit.activity.AssignToContact",
    "com.apple.UIKit.activity.SaveToCameraRoll",
    "com.apple.UIKit.activity.CopyToPasteboard",
)
```
