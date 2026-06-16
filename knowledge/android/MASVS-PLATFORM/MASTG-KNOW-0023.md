---
masvs_category: MASVS-PLATFORM
platform: android
title: Enforced Updating
---

Forcing a user to update the application can be necessary in multiple cases:

- A client-side vulnerability was discovered which needs to be fixed
- Cryptographical key material that needs to be rotated (e.g. public key pinning)
- Migrating to a new API so that the old API can be decommissioned more quickly
- Updating a dependency to ensure compatibility with third-party backend systems

Keep in mind that updating the app does not resolve vulnerabilities residing on backend systems. A secure update mechanism should complement proper API and service lifecycle management. Similarly, if users are not forced to update, test older app versions against your backend and apply API versioning and deprecation policies to maintain security and stability across all supported releases.

For Google Play-distributed apps running on supported devices, developers can implement enforced update flows using the Play In-App Updates API. The feature is supported on devices running Android 5.0 (API level 21) or higher, and is supported for Android mobile devices, Android tablets, and ChromeOS devices. It is not compatible with apps that use APK expansion files (`.obb` files). See the official documentation: [In-App Updates](https://developer.android.com/guide/playcore/in-app-updates "In-App Updates"). This mechanism is far more reliable than legacy methods such as scraping Play Store pages or calling undocumented endpoints, which are unstable and unsupported.

## Google Play In-App Updates API

The [Play In-App Updates API](https://developer.android.com/guide/playcore/in-app-updates) is part of the Google Play Core libraries. Current integrations use the Play In-App Update Library, for example `com.google.android.play:app-update`, and expose the [`AppUpdateManager`](https://developer.android.com/reference/com/google/android/play/core/appupdate/AppUpdateManager "AppUpdateManager") class, which lets apps check for available updates and initiate update flows directly within the app.

The API supports two primary modes:

- **Immediate updates**, which use a full-screen flow requiring the user to update and restart the app before continuing. This is the appropriate mode for critical updates.
- **Flexible updates**, which allow users to continue using the app while the update downloads in the background. The app must monitor the download state and call `completeUpdate()` when the update is ready to install.

The typical code pattern:

1. Obtain an `AppUpdateManager` instance via [`AppUpdateManagerFactory.create(context)`](https://developer.android.com/reference/com/google/android/play/core/appupdate/AppUpdateManagerFactory).
2. Call [`getAppUpdateInfo()`](https://developer.android.com/reference/com/google/android/play/core/appupdate/AppUpdateManager#getAppUpdateInfo%28%29) on the manager to retrieve an [`AppUpdateInfo`](https://developer.android.com/reference/com/google/android/play/core/appupdate/AppUpdateInfo) object.
3. Check [`UpdateAvailability.UPDATE_AVAILABLE`](https://developer.android.com/reference/com/google/android/play/core/install/model/UpdateAvailability) and verify that the selected update type is allowed with `appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)` or `appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)`.
4. Optionally use `clientVersionStalenessDays()` or `updatePriority()` to decide whether to request a flexible update or an immediate update. Critical security updates should use high priority and the immediate flow where appropriate.
5. Start the flow using `startUpdateFlowForResult(...)` with an `ActivityResultLauncher` and [`AppUpdateOptions`](https://developer.android.com/reference/com/google/android/play/core/appupdate/AppUpdateOptions "AppUpdateOptions").
6. Handle result and lifecycle cases. Users can cancel or decline an immediate update, and an immediate update can become stalled if the app is closed or backgrounded before completion. The app should handle the activity result and check update state when the app returns to the foreground, for example in `onResume` and other app entry points. If `UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` is reported, restart the immediate update flow. If the update is mandatory, continue blocking access until the update is completed.

Google Play also provides Play Console recovery tools that can prompt users running an outdated or broken version of an already published app to update to the latest compatible version. This can be useful when a problematic version, such as one with a security vulnerability, is already in the field. This is configured in Play Console rather than implemented directly in app code, and it should not be treated as a substitute for app-side or server-side enforcement when strict blocking is required, because users can dismiss the prompt and will be shown it again after a cold restart. See Google Play's [Prompt users to update to your latest app version](https://support.google.com/googleplay/android-developer/answer/13812041) documentation.

## Custom Backend-Gated Flows

For apps distributed outside Google Play, or for apps that need stricter security enforcement than the Play update prompt alone provides, developers should design custom mechanisms to check for updates. Common approaches include querying a self-hosted update API or using a configuration service such as [Firebase Remote Config](https://firebase.google.com/docs/remote-config) to publish minimum supported version requirements.

For non-Play distribution, the app can direct users to the trusted update source, such as an enterprise app store, managed distribution channel, or verified download page. Installing an APK normally still requires platform installation flow and user approval unless the app is installed in a managed or privileged environment with the necessary permissions.

The app version is typically retrieved at runtime using either:

- Compile-time constants: `BuildConfig.VERSION_NAME` and `BuildConfig.VERSION_CODE`, generated by the build system for the app module.
- Runtime lookup: `context.packageManager.getPackageInfo(context.packageName, 0).versionName` and the package version code. Use `PackageInfo.getLongVersionCode()` on API level 28 or higher, or `PackageInfoCompat.getLongVersionCode(packageInfo)` for compatibility across older supported API levels.

For enforced update policies, prefer comparing numeric version codes rather than user-visible version names. `versionCode` is the internal ordering value used to determine whether one version is newer than another, while `versionName` is only the version string shown to users.

Practical guidance for backend-gated flows:

- Include the app version code, app version name, platform, application ID, build variant, and distribution channel in early requests.
- Have the backend return a minimum supported version code, update URL or store target, severity, message, and whether the update is mandatory.
- Enforce the policy on the client by presenting a blocking UI, for example a non-dismissible dialog or full-screen `Activity` that disables navigation until the update is completed.
- Enforce the policy on backend services where possible by rejecting requests from unsupported app versions, especially for security-critical updates.
- Consider integrity and tamper resistance. Avoid trusting only client-provided data, use platform integrity signals where appropriate, sign update policy responses if they are security-sensitive, and handle offline scenarios with a cached policy, a reasonable TTL, and a safe fallback.
