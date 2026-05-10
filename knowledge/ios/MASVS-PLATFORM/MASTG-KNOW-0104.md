---
masvs_category: MASVS-PLATFORM
platform: ios
title: Low-Level System IPC Mechanisms
---

iOS exposes several low-level IPC mechanisms that Apple frameworks and system daemons use internally: XPC Services, Mach ports, and CFMessagePort. Unlike the user-mediated or entitlement-scoped channels described in @MASTG-KNOW-0078, these mechanisms are not designed for general-purpose communication between unrelated third-party apps. Their use is restricted by the iOS sandbox, and typical App Store apps almost never use them directly.

These mechanisms are primarily relevant to:

- Apps that ship their own app extensions (which communicate with the host app through XPC under the hood).
- System frameworks and daemons that need structured, privilege-separated IPC.
- Security researchers analyzing how system components or app extensions communicate.

## XPC Services

XPC is a structured, asynchronous IPC library managed by `launchd`. It lets a process spawn a helper process (an XPC service) with a tightly scoped sandbox, separate from the calling process. The XPC service runs with minimal entitlements, no root access, and restricted file system and network access.

XPC's main use case in iOS app development is **privilege and crash isolation**: moving dangerous or resource-intensive work, such as parsing untrusted data, into a separate process. If the helper crashes, the main app continues running. If the helper is compromised, the damage is contained within its sandbox.

In practice, XPC is rarely used directly by third-party iOS apps. It is far more common on macOS, where it underpins most system daemons and helper tools. On iOS it surfaces primarily through app extensions, which the system launches as XPC services automatically.

Two APIs are available:

- **`NSXPCConnection`**: An Objective-C/Swift API that wraps XPC connections with a proxy-based interface. The caller invokes methods on a remote object proxy; the system serializes and delivers the call to the service.
- **XPC Services C API** (`<xpc/xpc.h>`): A lower-level C API that gives direct control over XPC objects and connections.

## Mach Ports

Mach ports are the lowest-level IPC primitive on all Apple platforms. Virtually all higher-level IPC mechanisms, including XPC and `NSMachPort`, are built on top of Mach ports.

Apps can interact with Mach ports through the Foundation wrapper `NSMachPort` or the Core Foundation wrapper `CFMachPort`. Direct use of the Mach kernel API is restricted in App Store apps by the sandbox.

Mach ports allow only local, on-device communication. They are not accessible to arbitrary third-party apps under the sandbox.

## CFMessagePort

`CFMessagePort` provides a simple message-passing channel built on top of Mach ports. It supports named local ports (local to the device) for lightweight data exchange.

In practice, `CFMessagePort` is deprecated for new development, very rarely used in iOS apps, and effectively inaccessible to App Store apps when used as a named port because the sandbox prevents registering system-wide port names. It is most often encountered when reverse-engineering older code or system frameworks.

## Relevance for Security Testing

Although typical App Store apps don't use these mechanisms directly, they are relevant in the following contexts:

- **App extension analysis**: The system launches extensions (Share, Today, keyboard, and others) as XPC services. Analyzing the XPC interface between an extension and its host app can reveal what data is exchanged and whether it's validated.
- **Framework and daemon analysis**: On jailbroken devices or when auditing system components, Mach ports and XPC connections between daemons are visible and can be inspected with tools such as `frida-trace` or `xpcspy`.
- **Custom IPC in non-App-Store contexts**: Enterprise apps or internal tooling may use XPC for helper communication; these are worth examining for missing input validation or over-privileged services.

## References

- [XPC Services - Apple Developer Documentation](https://developer.apple.com/library/content/documentation/MacOSX/Conceptual/BPSystemStartup/Chapters/CreatingXPCServices.html)
- [NSXPCConnection - Apple Developer Documentation](https://developer.apple.com/documentation/foundation/nsxpcconnection)
- [NSMachPort - Apple Developer Documentation](https://developer.apple.com/documentation/foundation/nsmachport)
- [Inter-Process Communication - NSHipster](https://nshipster.com/inter-process-communication/)
