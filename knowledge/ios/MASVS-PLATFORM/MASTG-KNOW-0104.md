---
masvs_category: MASVS-PLATFORM
platform: ios
title: Low-Level System IPC Mechanisms
---

iOS includes several low-level IPC mechanisms that Apple frameworks and system daemons use internally: [XPC](https://developer.apple.com/forums/thread/708877), Mach ports, and [`CFMessagePort`](https://developer.apple.com/documentation/corefoundation/cfmessageport). Unlike the user-mediated or entitlement-scoped channels described in @MASTG-KNOW-0078, these mechanisms are not designed for general-purpose communication between unrelated third-party apps. Their use is restricted by the iOS sandbox, and typical App Store apps rarely use them directly. Apple DTS explicitly states that there is [no supported way to directly communicate between iOS apps using XPC](https://developer.apple.com/forums/thread/715338).

These mechanisms are primarily relevant to:

- Apps that use supported extension-based architectures, including File Provider services, enhanced security helper extensions, BrowserEngineKit, and ExtensionFoundation.
- System frameworks and daemons that need structured, privilege-separated IPC.
- Security researchers analyzing how system components, app extensions, or extension-based services communicate.

## XPC Services

[XPC](https://developer.apple.com/library/content/documentation/MacOSX/Conceptual/BPSystemStartup/Chapters/CreatingXPCServices.html) is a structured, asynchronous IPC mechanism managed by `launchd`. It lets a process communicate with another process, such as a helper service or extension process, over a system-managed channel.

XPC's main use case is **privilege and crash isolation**: moving dangerous or resource-intensive work, such as parsing untrusted data, into a separate process. If the helper crashes, the main app can continue running. If the helper is compromised, the damage may be contained within its own sandbox, depending on the entitlements and sandbox profile assigned to that process.

In practice, XPC is rarely used directly by typical third-party iOS apps. It is far more common on macOS, where it underpins many system daemons, helper tools, and app-private services. On iOS, Apple DTS notes that XPC comes into play in a [few supported cases](https://developer.apple.com/forums/thread/708877), such as [File Provider services](https://developer.apple.com/documentation/fileprovider), [enhanced security helper extensions](https://developer.apple.com/documentation/xcode/creating-enhanced-security-helper-extensions), [`BrowserEngineKit`](https://developer.apple.com/documentation/browserenginekit), and [`ExtensionFoundation`](https://developer.apple.com/documentation/extensionfoundation). These are supported platform-specific designs, not general-purpose XPC communication between unrelated apps. In the traditional app extension model, the system launches the extension process and the extension communicates directly with the [host app](https://developer.apple.com/library/archive/documentation/General/Conceptual/ExtensibilityPG/ExtensionOverview.html). The containing app does not communicate directly with its extension, but they can share data through an [App Group container](https://developer.apple.com/documentation/xcode/configuring-app-groups).

Apple DTS summarizes [XPC as having three APIs](https://developer.apple.com/forums/thread/708877):

- **[`NSXPCConnection`](https://developer.apple.com/documentation/foundation/nsxpcconnection)**: An Objective-C/Swift API that wraps XPC connections with a proxy-based interface. The caller invokes methods on a remote object proxy; the system serializes and delivers the call to the service.
- **Low-level Swift XPC API**: A lower-level Swift API for working with XPC objects and connections.
- **XPC Services C API** (`<xpc/xpc.h>`): A lower-level C API that gives direct control over XPC objects and connections.

## Mach Ports

Mach ports are the lowest-level IPC primitive on Apple platforms. Higher-level IPC mechanisms, including XPC and [`NSMachPort`](https://developer.apple.com/documentation/foundation/nsmachport), are built on top of Mach ports.

Apps can interact with Mach ports through the Foundation wrapper [`NSMachPort`](https://developer.apple.com/documentation/foundation/nsmachport) or the Core Foundation wrapper [`CFMachPort`](https://developer.apple.com/documentation/corefoundation/cfmachport). [Apple DTS strongly recommends against using Mach messaging directly](https://developer.apple.com/forums/thread/715338) because it is difficult to use correctly. Direct use of Mach messaging should not be treated as a general-purpose IPC mechanism for App Store apps.

Mach ports allow only local, on-device communication. They are not suitable for arbitrary communication between unrelated third-party apps under the iOS sandbox.

## CFMessagePort

[`CFMessagePort`](https://developer.apple.com/documentation/corefoundation/cfmessageport) provides a simple message-passing channel built on top of lower-level port mechanisms. It supports named local ports for lightweight data exchange on platforms where the API is available.

In practice, `CFMessagePort` should not be presented as a practical IPC option for modern iOS apps. Apple documents that [`CFMessagePortCreateRunLoopSource`](https://developer.apple.com/documentation/corefoundation/cfmessageportcreaterunloopsource(_:_:_:)), [`CFMessagePortCreateLocal`](https://developer.apple.com/documentation/corefoundation/cfmessageportcreatelocal(_:_:_:_:_:)), and [`CFMessagePortCreateRemote`](https://developer.apple.com/documentation/corefoundation/cfmessageportcreateremote(_:_:)) are not available on iOS 7 and later, return `NULL`, and log a sandbox violation. It is most often encountered when reverse-engineering older code, cross-platform code, or system frameworks.

## Relevance for Security Testing

Although typical App Store apps don't use these mechanisms directly, they are relevant in the following contexts:

- **App extension analysis**: The system launches extensions, such as Share, Today, keyboard, and others, in a separate process. Analyzing the communication between an extension and its host app can reveal what data is exchanged and whether it's validated.
- **Shared container analysis**: When an extension and its containing app share data through an [App Group container](https://developer.apple.com/documentation/xcode/configuring-app-groups), review the shared files, preferences, database state, locking, and data lifetime.
- **Framework and daemon analysis**: On jailbroken devices or when auditing system components, Mach ports and XPC connections between daemons are visible and can be inspected with several tools based on @MASTG-TOOL-0039.
- **Custom IPC in non-App-Store contexts**: Enterprise apps, internal tooling, or platform-specific extension mechanisms may use XPC for helper communication; these are worth examining for missing input validation or over-privileged services.
