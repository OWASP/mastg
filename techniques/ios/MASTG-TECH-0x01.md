---
title: Opening Deep Links
platform: ios
---

To test how an iOS app handles deep links, you can open a URL through Apple tooling, through another app such as Safari or Notes, or through dynamic instrumentation. The best method depends on whether you are testing a simulator, a physical non jailbroken device, or a jailbroken device.

## Using `xcrun devicectl`

Use `xcrun devicectl` to open a deep link on a physical device connected to your Mac. First list connected devices and copy the device identifier:

```bash
xcrun devicectl list devices
```

Then launch the target app with the URL payload:

```bash
xcrun devicectl device process launch \
  --device 76557B40-8C76-5087-B9C5-F708C051911A \
  --payload-url 'mastgtest://import?session=<payload>' \
  org.owasp.mastestapp.MASTestApp-iOS
```

Replace the device identifier, URL, and bundle identifier with the values for your target.

## Using `uiopen`

On a jailbroken device shell (see @MASTG-TECH-0052), you can use `uiopen` to ask iOS to open the URL directly:

```bash
uiopen 'mastgtest://import?session=<payload>'
```

This is useful when you are already working from the device shell and want a quick way to trigger the app's URL handler.

## Using Safari

You can also open deep links from Safari. This is useful to test behavior that is closer to a user initiated flow.

For example, entering the following URL in Safari opens the system phone flow:

```text
tel://123456789
```

iOS shows a confirmation prompt before dialing. If the user confirms, the Phone app starts the call.

For custom URL schemes, enter or navigate to a URL such as:

```text
mastgtest://import?session=<payload>
```

If an installed app has registered the scheme, iOS can offer to open the URL with that app.

## Using the Notes app

You can paste deep links into the Notes app and open them from there. Exit editing mode first, then tap or long press the link.

```text
mastgtest://import?session=<payload>
```

This is a simple way to test user initiated deep links without writing a webpage. If the URL is not recognized as a link, check that the target app is installed, that the scheme is registered, and that the URL is properly encoded.

## Using @MASTG-TOOL-0039

If you are instrumenting the device with Frida, you can also trigger a URL programmatically. This is useful during dynamic analysis, especially when testing many payloads.

The following example runs inside the target app process and asks `UIApplication` to open the URL:

```js
function openURL(url) {
    const UIApplication = ObjC.classes.UIApplication.sharedApplication();
    const NSURL = ObjC.classes.NSURL;
    const toOpen = NSURL.URLWithString_(url);
    return UIApplication.openURL_(toOpen);
}

openURL("mastgtest://import?session=<payload>");
```

`openURL:` is deprecated for app development, but it is still commonly seen in instrumentation snippets because it is short and easy to call from Frida. When writing app code, prefer `openURL:options:completionHandler:` or the Swift equivalent `open(_:options:completionHandler:)`.

Another common dynamic analysis approach is to run the Frida script in SpringBoard and call the non public `LSApplicationWorkspace` API:

```js
function openURL(url) {
    const workspace = ObjC.classes.LSApplicationWorkspace.defaultWorkspace();
    const toOpen = ObjC.classes.NSURL.URLWithString_(url);
    return workspace.openSensitiveURL_withOptions_(toOpen, null);
}

openURL("mastgtest://import?session=<payload>");
```

This style is useful for black box URL scheme testing because SpringBoard is responsible for dispatching the URL to the registered app.

!!! note
   `LSApplicationWorkspace` is a non-public API. Do not use it in App Store apps. For security testing and dynamic analysis on a test device, it can be useful to trigger URL handling paths that are otherwise difficult to exercise repeatedly.
