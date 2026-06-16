---
masvs_category: MASVS-PLATFORM
platform: ios
title: SiriKit and Siri Shortcuts
available_since: 10
---

SiriKit and Siri Shortcuts let apps expose selected functionality to Siri, the Shortcuts app, Spotlight, and Siri Suggestions. These mechanisms allow users to invoke app actions outside the normal app UI, either through voice, suggestions, or user configured shortcuts.

For the modern Swift framework used to expose app actions to Siri, Shortcuts, Spotlight, widgets, controls, and Apple Intelligence, see @MASTG-KNOW-0129.

## SiriKit

[`SiriKit`](https://developer.apple.com/documentation/sirikit) is the original intent framework introduced in iOS 10. Apps implement predefined intent domains, such as messaging, calls, payments, workouts, ride booking, and others, by providing an Intents extension based on [`INExtension`](https://developer.apple.com/documentation/intents/inextension).

When SiriKit handles a request, the system invokes the Intents extension. The extension receives an [`INIntent`](https://developer.apple.com/documentation/intents/inintent), resolves and confirms parameters when required, performs the requested action, and returns a response to the system.

If the Intents extension and its containing app need to share files, preferences, or state, they can use an [App Group container](https://developer.apple.com/documentation/xcode/configuring-app-groups).

## Siri Shortcuts and Donations

Starting in iOS 12, apps can make actions available to Siri and the Shortcuts app through donations.

Common shortcut mechanisms include:

- **[`NSUserActivity`](https://developer.apple.com/documentation/foundation/nsuseractivity) shortcuts**: Apps mark activities as eligible for prediction using [`isEligibleForPrediction`](https://developer.apple.com/documentation/foundation/nsuseractivity/iseligibleforprediction), allowing Siri to suggest them.
- **[`INInteraction`](https://developer.apple.com/documentation/intents/ininteraction) donations**: Apps donate completed intent based actions so the system can learn usage patterns and suggest shortcuts.
- **Shortcut phrases and configuration**: Users can configure shortcuts and invoke them through Siri or the Shortcuts app.

## How Data Flows

A SiriKit or shortcut action carries parameters provided by the user, donated by the app, configured in a shortcut, or resolved by the system. The system may invoke an Intents extension, continue an `NSUserActivity`, or launch the app to complete the action.

Data may flow through:

- Intent parameters.
- `NSUserActivity.userInfo`.
- Donated `INInteraction` objects.
- Shared state between an Intents extension and its containing app.
- User configured shortcut parameters.

## Scope and Constraints

- SiriKit intents are limited to Apple defined domains.
- Intents and shortcuts run with the app or extension sandbox and entitlements.
- Apps should validate all parameters received from Siri, Shortcuts, donated interactions, or restored user activities.
- Sensitive or destructive actions should require explicit user confirmation before execution.
- Shortcut definitions and configured parameters may be synced across the user's devices, so they should not contain secrets.
- Donated interactions and `NSUserActivity.userInfo` should contain only minimal, non sensitive data.
- Authorization checks should be enforced in the intent handler or continuation path, not only in the app UI.

## Security Testing Notes

Security testing should treat SiriKit intents and Siri Shortcuts as external entry points into the app.

Review:

- Which SiriKit domains, intents, activities, and donations the app exposes.
- Whether intent parameters and `NSUserActivity.userInfo` are validated.
- Whether sensitive actions can be triggered without authentication or confirmation.
- Whether donated interactions or shortcut parameters persist sensitive data.
- Whether App Group data shared between the Intents extension and containing app is protected and minimized.
- Whether the intent handler performs the same authorization checks as the normal app UI.
