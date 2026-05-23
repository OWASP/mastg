---
masvs_category: MASVS-PLATFORM
platform: ios
title: App Intents and AI Agent Exposure
available_since: 16
---

The [`App Intents`](https://developer.apple.com/documentation/appintents) framework lets apps expose actions and content to system surfaces such as Siri, the Shortcuts app, Spotlight, widgets, Live Activities, controls, and the Action button on supported devices. Apple also uses App Intents to integrate app capabilities with [Siri and Apple Intelligence](https://developer.apple.com/documentation/appintents/integrating-actions-with-siri-and-apple-intelligence).

For legacy SiriKit intents, `NSUserActivity` shortcuts, and `INInteraction` donations, see @MASTG-KNOW-0124.

## How App Intents Work

Apps declare intent types that describe discrete actions the app can perform. The system discovers these intents from static metadata and can surface them in supported system experiences.

An App Intent may run in the app process or in an extension process, depending on how the app is structured and how the intent is invoked. The intent receives parameters provided by the user, configured in a shortcut, resolved by the system, or selected from app entities. The result is returned to the invoking system surface.

Common App Intents types include:

- **[`AppIntent`](https://developer.apple.com/documentation/appintents/appintent)**: Defines an action the app can perform.
- **[`IntentParameter`](https://developer.apple.com/documentation/appintents/intentparameter)**: Defines typed input values for an intent.
- **[`AppEntity`](https://developer.apple.com/documentation/appintents/appentity)**: Represents typed app objects that can be referenced by intents and surfaced to the system.
- **[`AppShortcutsProvider`](https://developer.apple.com/documentation/appintents/appshortcutsprovider)**: Defines app shortcuts, suggested phrases, and entry points for Siri, Shortcuts, Spotlight, and other surfaces.

## Exposure to AI Agents and System Intelligence

App Intents are the main Apple supported mechanism for exposing app actions and content to Siri and Apple Intelligence. From a security perspective, an App Intent is an exposed capability that may be invoked outside the app's normal UI.

This does not mean arbitrary third party AI agents can directly call private app functionality. It means that actions declared through App Intents may become available to Apple supported system surfaces, including AI driven experiences, depending on the platform version, device capabilities, user configuration, and intent metadata.

Security testing should treat each App Intent as an external entry point into the app.

## Scope and Constraints

- App Intents run with the app's sandbox and entitlements.
- Intent parameters can come from Siri, Shortcuts, Spotlight, widgets, controls, Apple Intelligence features, or user configured automations.
- Sensitive or destructive actions should require explicit confirmation using [`requestConfirmation()`](https://developer.apple.com/documentation/appintents/appintent/requestconfirmation%28%29) or appropriate authentication using [`authenticationPolicy`](https://developer.apple.com/documentation/appintents/appintent/authenticationpolicy).
- App Intents can run in the foreground or background depending on their supported modes and whether they require the app to open.
- Shortcut definitions and configured parameters may sync across the user's devices, so they should not contain secrets.
- Intent results, dialogs, summaries, and entity metadata may be shown in system UI and should not disclose sensitive data unnecessarily.

## Security Testing Notes

Review each App Intent as a callable app capability.

Check whether:

- The intent modifies data, sends messages, makes purchases, accesses files, changes settings, or discloses personal information.
- All `IntentParameter` values and resolved `AppEntity` objects are validated as untrusted input.
- Authorization checks are enforced inside the intent implementation, not only in the app UI.
- Sensitive, destructive, financial, privacy impacting, or irreversible actions require confirmation or authentication.
- Intent results, dialogs, summaries, Spotlight metadata, and Siri responses avoid exposing sensitive data.
- Shortcuts or configured parameters persist secrets, tokens, identifiers, or personal data.
- The same action has consistent security checks when invoked from the app UI, Siri, Shortcuts, Spotlight, widgets, controls, or Apple Intelligence features.
