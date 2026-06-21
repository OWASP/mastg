---
title: Use Native Views for Sensitive Text Entry Over a WebView
alias: use-native-views-for-sensitive-text-entry-over-webview
id: MASTG-BEST-0060
platform: ios
knowledge: [MASTG-KNOW-0076, MASTG-KNOW-0139]
---

When a `WKWebView` contains an HTML `<input type="password">` or any sensitive text field, the typed value is stored in the element's `.value` property. Any JavaScript running on the page, including injected XSS payloads, can read it with `document.querySelector('input[type=password]').value`. The page does not need a native bridge to do this.

The safer approach is to intercept user focus on the sensitive field before any typing occurs, then present a native `UITextField` (configured with `isSecureTextEntry = true`) overlaid at the exact position of the HTML element. The user types into the native view and the value never enters the DOM.

## Detect Focus with an Isolated Script

Use a `WKUserScript` registered in a custom [`WKContentWorld`](https://developer.apple.com/documentation/webkit/wkcontentworld) (@MASTG-KNOW-0139) to listen for focus events on sensitive inputs. The script runs in an isolated world, so page JavaScript cannot override it or the bridge it uses:

```swift
let appWorld = WKContentWorld.world(withName: "AppWorld")

config.userContentController.add(inputHandler, contentWorld: appWorld, name: "secureInput")

let script = WKUserScript(source: """
    document.querySelectorAll('input[type="password"], input[data-sensitive]').forEach(el => {
        el.addEventListener('focus', () => {
            const r = el.getBoundingClientRect();
            window.webkit.messageHandlers.secureInput.postMessage({
                x: r.left, y: r.top, width: r.width, height: r.height
            });
            el.blur(); // prevent the system keyboard from appearing for this field
        });
    });
""", injectionTime: .atDocumentStart, forMainFrameOnly: true, in: appWorld)

config.userContentController.addUserScript(script)
```

The message carries only layout coordinates. No sensitive content crosses this channel.

## Overlay a Native Secure Input View

Present a `UITextField` directly over the `WKWebView` at the reported position:

```swift
func userContentController(_ ucc: WKUserContentController,
                            didReceive message: WKScriptMessage) {
    guard message.name == "secureInput",
          let body = message.body as? [String: Double] else { return }

    let frame = CGRect(x: body["x"] ?? 0, y: body["y"] ?? 0,
                       width: body["width"] ?? 200, height: body["height"] ?? 44)

    let secureField = UITextField(frame: frame)
    secureField.isSecureTextEntry = true
    secureField.borderStyle = .roundedRect
    secureField.becomeFirstResponder()

    webView.superview?.addSubview(secureField)
}
```

Because the `UITextField` is a native view outside the `WKWebView`'s rendering context, page JavaScript has no access to it or to the characters the user types.

## Send the Value Natively

Once the user confirms the input (for example, on return key or a native button), send the value directly from Swift (to your own API, Keychain, or another native layer) without writing it back into the DOM:

```swift
secureField.addTarget(self, action: #selector(submitSecureValue(_:)), for: .editingDidEndOnExit)

@objc func submitSecureValue(_ field: UITextField) {
    guard let value = field.text else { return }
    // Submit natively, never assign to a DOM element
    authManager.submitPasscode(value)
    field.removeFromSuperview()
}
```

If the downstream flow requires the page to proceed (for example, to submit a form), trigger the form submission from the isolated world using `evaluateJavaScript(_:in:in:completionHandler:)` rather than populating the password field's `.value` in the DOM.

## Do Not Write the Value Back into the DOM

Setting a password input's `.value` from Swift (even from an isolated world) makes the value readable by page JavaScript. If the submission flow requires the page's form, consider bypassing the HTML form entirely and submitting the credential directly from native code.
