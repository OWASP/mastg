---
masvs_category: MASVS-PLATFORM
platform: ios
title: WKContentWorld
available_since: 14
---

[`WKContentWorld`](https://developer.apple.com/documentation/webkit/wkcontentworld), introduced in iOS 14, represents an isolated JavaScript execution environment within a `WKWebView`. Each content world has its own JavaScript global scope and its own copy of the built-in prototype chain, but all worlds share the same underlying DOM.

## Available Worlds

Three types of content worlds are available:

- **`.page`**: the JavaScript environment of the loaded web page. Scripts and handlers registered here share scope with the page's own JavaScript.
- **`.defaultClient`**: a pre-defined isolated world separate from the page. Variables and functions declared here are not visible to page JavaScript and do not conflict with the page's globals.
- **Custom worlds**: created with [`WKContentWorld.world(withName:)`](https://developer.apple.com/documentation/webkit/wkcontentworld/world(withname:)). Multiple named worlds can coexist, each fully isolated from the others and from the page.

## Isolation Boundaries

### What is isolated

Each world maintains its own:

- **Global scope**: variables and functions declared in one world do not appear in any other world's `window` or global namespace.
- **Prototype chain**: built-in prototypes such as `Array.prototype`, `Object.prototype`, and `Function.prototype` are independent copies per world. Modifications to a prototype in one world (for example, adding or overriding methods) do not affect the same prototype in any other world.

### What is shared

All content worlds share the same DOM. This means:

- HTML element references (`document.getElementById(...)`, `document.querySelector(...)`, and similar) return the same underlying element objects regardless of which world evaluates them.
- Modifying element properties such as `.textContent`, `.value`, or `.style` in one world is immediately visible to all other worlds and to the page.
- DOM events dispatched from one world (via `dispatchEvent`) are observed by listeners in all worlds that are attached to the same element.

## APIs That Accept a Content World

The following APIs were updated in iOS 14 to accept a [`WKContentWorld`](https://developer.apple.com/documentation/webkit/wkcontentworld) parameter, allowing callers to choose where code executes:

| API | Notes |
|---|---|
| [`WKUserContentController.add(_:contentWorld:name:)`](https://developer.apple.com/documentation/webkit/wkusercontentcontroller/add(_:contentworld:name:)) | Registers a `WKScriptMessageHandler` in the specified world |
| [`WKUserContentController.addScriptMessageHandler(_:contentWorld:name:)`](https://developer.apple.com/documentation/webkit/wkusercontentcontroller/addscriptmessagehandler(_:contentworld:name:)) | Registers a `WKScriptMessageHandlerWithReply` in the specified world |
| [`WKUserScript(source:injectionTime:forMainFrameOnly:in:)`](https://developer.apple.com/documentation/webkit/wkuserscript/init(source:injectiontime:formainframeonly:in:)) | Injects a script into the specified world at document load |
| [`WKWebView.evaluateJavaScript(_:in:in:completionHandler:)`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:in:in:completionhandler:)) | Evaluates a JavaScript string in the specified world |
| [`WKWebView.callAsyncJavaScript(_:arguments:in:in:completionHandler:)`](https://developer.apple.com/documentation/webkit/wkwebview/callasyncjavascript(_:arguments:in:in:completionhandler:)) | Evaluates an async JavaScript function in the specified world and awaits a result |

The common variants of these APIs — `add(_:name:)`, `evaluateJavaScript(_:completionHandler:)`, and `WKUserScript(source:injectionTime:forMainFrameOnly:)` — always operate in the `.page` world.
