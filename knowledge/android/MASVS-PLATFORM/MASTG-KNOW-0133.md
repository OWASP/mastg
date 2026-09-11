---
masvs_category: MASVS-PLATFORM
platform: android
title: Android Services
---

A [service](https://developer.android.com/guide/components/services) is an app component that performs long-running operations in the background without a user interface, such as processing data, performing network transactions, or interacting with content providers. A service extends the [`Service`](https://developer.android.com/reference/android/app/Service) class. Unless configured otherwise, a service runs in the main thread of its hosting process and does not create its own thread.

Services are an inter-process communication (IPC) entry point: other apps and the system can start or bind to a service through an [`Intent`](https://developer.android.com/reference/android/content/Intent), subject to manifest access controls such as `android:exported` and `android:permission`. This makes service visibility directly relevant to the app's attack surface. See @MASTG-KNOW-0020 for the IPC model and the role of Binder.

## Declaration

Each service must be declared in the `AndroidManifest.xml` file with a [`<service>`](https://developer.android.com/guide/topics/manifest/service-element) element nested inside `<application>`:

```xml
<service android:name=".MyService" />
```

The [`android:process`](https://developer.android.com/guide/topics/manifest/service-element#proc) attribute can place the service in a separate process (for example, `android:process=":remote"`), which is common for services that expose a remote interface to other apps.

## Types of Services

Android distinguishes services by how they are used:

- **Started services:** launched with [`startService`](https://developer.android.com/reference/android/content/Context#startService(android.content.Intent)) (or [`startForegroundService`](https://developer.android.com/reference/android/content/Context#startForegroundService(android.content.Intent))) and run until they stop themselves or are stopped.
- **Bound services:** other components bind to them with [`bindService`](https://developer.android.com/reference/android/content/Context#bindService(android.content.Intent,%20android.content.ServiceConnection,%20int)) to interact through a client-server interface. A bound service must return an [`IBinder`](https://developer.android.com/reference/android/os/IBinder) from [`onBind`](https://developer.android.com/reference/android/app/Service#onBind(android.content.Intent)). See [Bound services](https://developer.android.com/guide/components/bound-services).
- **Foreground services:** show an ongoing notification and are subject to [foreground service type](https://developer.android.com/about/versions/14/changes/fgs-types-required) requirements introduced in recent Android versions.

For background work that doesn't need to run immediately, modern Android favors [`WorkManager`](https://developer.android.com/topic/libraries/architecture/workmanager) and [`JobScheduler`](https://developer.android.com/reference/android/app/job/JobScheduler) over long-running started services.

## Communicating with a Bound Service

Apps interact with a bound service through one of these mechanisms:

- A [`Messenger`](https://developer.android.com/reference/android/os/Messenger), which serializes requests into [`Message`](https://developer.android.com/reference/android/os/Message) objects delivered to a [`Handler`](https://developer.android.com/reference/android/os/Handler). This is the simplest cross-process interface.
- The [Android Interface Definition Language (AIDL)](https://developer.android.com/guide/components/aidl), which generates the marshalling code for a remote interface and allows concurrent calls across processes.
- A local [`Binder`](https://developer.android.com/reference/android/os/Binder) subclass, when the service is only used within the same process.

The inputs a service expects (for example, the `what` field and arguments of a `Message`, or the methods of an AIDL interface) are defined in the service implementation. See @MASTG-KNOW-0020 for how Binder transactions carry these calls between processes.

## Access Control

Whether other apps can start or bind to a service is governed by manifest attributes:

- [`android:exported`](https://developer.android.com/guide/topics/manifest/service-element#exported): when `true`, components of other apps can start or bind to the service unless another control, such as `android:permission`, blocks the caller. When `false`, access is limited to the same app, apps with the same user ID, or privileged system components. Declaring an `<intent-filter>` historically caused `android:exported` to default to `true`; since Android 12 (API level 31) the attribute must be set explicitly when an intent filter is present.
- [`android:permission`](https://developer.android.com/guide/topics/manifest/service-element#prmsn): requires the caller to hold a specific permission to start or bind to the service. If the caller is not granted the permission, the intent is not delivered to the service. Combined with a custom permission and an appropriate [`android:protectionLevel`](https://developer.android.com/guide/topics/manifest/permission-element#plevel) (for example, `signature`), this restricts which apps can interact with the component. See @MASTG-KNOW-0017 for permission protection levels, component permission enforcement, and custom permissions, and see [Permission-based access control to exported components](https://developer.android.com/privacy-and-security/risks/access-control-to-exported-components).
- Inside the service, [`Context.checkCallingPermission`](https://developer.android.com/reference/android/content/Context#checkCallingPermission(java.lang.String)) and related methods can verify at runtime whether the caller holds a required permission before processing a Binder transaction. See [Binder and Messenger interfaces](https://developer.android.com/privacy-and-security/security-tips#binder-and-messenger-interfaces).

For an overview of how to restrict access to exported components, see @MASTG-BEST-0052.
