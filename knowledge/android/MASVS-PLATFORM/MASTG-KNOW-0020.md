---
masvs_category: MASVS-PLATFORM
platform: android
title: Inter-Process Communication (IPC) Mechanisms
---

Every Android process runs in its own sandboxed address space. [Inter-process communication (IPC)](https://developer.android.com/guide/components/processes-and-threads) lets apps and the system exchange data and invoke functionality across these process boundaries. Instead of relying on traditional techniques such as shared files or network sockets, Android provides higher-level IPC mechanisms built on a common foundation. This article gives an overview of those mechanisms and links to the detailed knowledge articles for each.

## Binder

Android's IPC is based on [Binder](https://developer.android.com/reference/android/os/Binder), a custom kernel driver and framework derived from OpenBinder. Most Android system services and all high-level IPC mechanisms depend on it. The term _Binder_ refers to several related concepts:

- **Binder driver:** the kernel-level driver exposed as the `/dev/binder` character device.
- **Binder protocol:** the low-level `ioctl`-based protocol used to communicate with the driver.
- **`IBinder` interface:** the well-defined behavior that Binder objects implement.
- **Binder object, service, and client:** the implementation exposing functionality and the objects that consume it.

The Binder framework follows a client-server model. To make an IPC call, an app invokes a method on a proxy object. The proxy _marshals_ the parameters into a _parcel_ and sends a transaction to the Binder server, which holds a thread pool to handle incoming requests and dispatches the call to the target object. From the caller's perspective this looks like an ordinary method call; the framework performs the marshalling and transport.

<img src="Images/Chapters/0x05a/binder.jpg" width="400px" />

Services that allow other apps to bind to them are called _bound services_ and provide an `IBinder` interface to clients. Developers commonly use the [Android Interface Definition Language (AIDL)](https://developer.android.com/guide/components/aidl) to define remote interfaces. The [`ServiceManager`](https://developer.android.com/reference/android/os/IBinder) system daemon registers system services and resolves them by name. You can list the registered system services with the `service list` command:

```bash
adb shell service list
```

## Intents

[Intent messaging](https://developer.android.com/guide/components/intents-filters) is an asynchronous communication framework built on top of Binder. An [`Intent`](https://developer.android.com/reference/android/content/Intent) is a messaging object used to request an action from another app component. Intents support three fundamental use cases:

- **Starting an activity** by passing an intent to `startActivity` (see @MASTG-KNOW-0132).
- **Starting or binding to a service** by passing an intent to `startService` or `bindService` (see @MASTG-KNOW-0133).
- **Delivering a broadcast** by passing an intent to `sendBroadcast` or `sendOrderedBroadcast` (see @MASTG-KNOW-0134).

Intents can be **explicit** (naming the target component) or **implicit** (describing an action that the system resolves to a component based on its [`<intent-filter>`](https://developer.android.com/guide/topics/manifest/intent-filter-element) declarations). For details, see @MASTG-KNOW-0025. Related intent-based concepts are covered in @MASTG-KNOW-0024 (Pending Intents) and @MASTG-KNOW-0019 (Deep Links).

## App Components as IPC Entry Points

Four [app component](https://developer.android.com/guide/components/fundamentals#Components) types act as IPC entry points. Each is declared in the `AndroidManifest.xml` file and its visibility to other apps is controlled by the [`android:exported`](https://developer.android.com/privacy-and-security/risks/android-exported) attribute and, optionally, permission attributes. For the permission model behind these controls, including protection levels, component permission enforcement, and custom permissions, see @MASTG-KNOW-0017:

- **Activities** provide user-interface screens that other apps can start. See @MASTG-KNOW-0132.
- **Services** run background operations that other apps can start or bind to. See @MASTG-KNOW-0133.
- **Broadcast Receivers** respond to broadcast messages from other apps and the system. See @MASTG-KNOW-0134.
- **Content Providers** expose structured data through a URI-based interface. See @MASTG-KNOW-0117.

## Other IPC Mechanisms

Apps can also exchange data through mechanisms that aren't tied to a specific component type, including the [clipboard](https://developer.android.com/develop/ui/views/touch-and-input/copy-paste), [`Messenger`](https://developer.android.com/reference/android/os/Messenger) objects, shared files exposed through a [`FileProvider`](https://developer.android.com/reference/androidx/core/content/FileProvider), and local sockets. The component-based mechanisms above are the most common entry points exposed to other apps.
