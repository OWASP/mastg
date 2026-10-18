---
title: Use Immutable PendingIntents with Explicit Intents
alias: use-immutable-pendingintents-with-explicit-intents
id: MASTG-BEST-0063
platform: android
---

When creating a [`PendingIntent`](https://developer.android.com/reference/android/app/PendingIntent), always use `FLAG_IMMUTABLE` and ensure the base intent is explicit (targets a specific component).

For background on the `PendingIntent` API and its security model, see @MASTG-KNOW-0024.

## Use FLAG_IMMUTABLE

Always set [`PendingIntent.FLAG_IMMUTABLE`](https://developer.android.com/reference/android/app/PendingIntent#FLAG_IMMUTABLE) when creating a `PendingIntent`. This prevents a receiving app from modifying the intent's unfilled fields, which could otherwise be used to redirect the intent to a malicious component.

```kotlin
val pendingIntent = PendingIntent.getActivity(
    context,
    requestCode,
    intent,
    PendingIntent.FLAG_IMMUTABLE  // prevents modification by the receiver
)
```

`FLAG_IMMUTABLE` [must be always specified](https://developer.android.com/guide/components/intents-filters#DeclareMutabilityPendingIntent "must be always specified") on apps targeting Android 12 or higher for each `PendingIntent` object that the app creates.

Only use `FLAG_MUTABLE` when the receiving component explicitly needs to fill in intent fields that cannot be known in advance (for example, [inline reply actions](https://developer.android.com/develop/ui/views/notifications/build-notification#reply-action) or [app widget](https://developer.android.com/develop/ui/views/appwidgets) [Pending Intents](../Document/0x05h-Testing-Platform-Interaction.md#pending-intents) that require a call back into the app). Even then, set only the fields you expect the receiver to modify.

## Use Explicit Intents

The base `Intent` wrapped in a `PendingIntent` must specify its target component explicitly (package, class, or both), so that only the intended app and component can receive it.

```kotlin
// Explicit: specifies both package and class
val intent = Intent(context, TargetActivity::class.java).apply {
    setPackage(context.packageName)
}
val pendingIntent = PendingIntent.getActivity(
    context,
    requestCode,
    intent,
    PendingIntent.FLAG_IMMUTABLE
)
```

Avoid using an implicit intent (one that specifies only an action or category), because any app on the device can respond to it and potentially hijack the `PendingIntent`.

## References

- Android Developer Docs: [Intents and Intent Filters — Pending Intents](https://developer.android.com/guide/components/intents-filters#PendingIntent)
- Android Security Risk: [Pending intents](https://developer.android.com/topic/security/risks/pending-intent)
- Android 12 behavior changes: [Pending intent mutability](https://developer.android.com/about/versions/12/behavior-changes-12#pending-intent-mutability)
- Pending Intents mutability: [Specify mutability](https://developer.android.com/guide/components/intents-filters#DeclareMutabilityPendingIntent)
