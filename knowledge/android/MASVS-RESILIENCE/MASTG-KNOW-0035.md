---
masvs_category: MASVS-RESILIENCE
platform: android
title: Google Play Integrity API
---

[Google Play Integrity API](https://developer.android.com/google/play/integrity/overview "Google Play Integrity API") was launched to improve the security and integrity of apps and games on Android starting with Android 4.4 (level 19). It supersedes the deprecated [SafetyNet Attestation API](https://developer.android.com/training/safetynet/attestation), which was shut down in 2024. Play Integrity was developed with the core capabilities of SafetyNet and added broader coverage for app and account integrity signals.

Internally, the Play Integrity API uses the same cryptographic techniques as @MASTG-KNOW-0044 to root its verdicts in hardware-backed trust.

## Safeguards

The Play Integrity API provides a server-verifiable verdict covering:

- Whether the app binary is the original, unmodified version from Google Play
- Whether the app is running on a genuine Android device
- Whether the device passes basic integrity checks

The API provides four macro categories of information to help the security team make a decision. These categories include:

1. **Request Details**: In this section, details are obtained about the app package that requested the integrity check, including its format (e.g., com.example.myapp), a base64-encoded ID provided by the developer to link the request with the integrity certificate, and the execution time of the request in milliseconds.

2. **App Integrity**: This section provides information about the integrity of the app, including the result of the verification (denominated verdict), which indicates whether the app's installation source is trusted (via Play Store) or unknown/suspicious. If the installation source is considered secure, the app version will also be displayed.

3. **Account Details**: This category provides information about the app licensing status. The result can be `LICENSED`, indicating that the user purchased or installed the app on the Google Play Store; `UNLICENSED`, meaning that the user does not own the app or did not acquire it through the Google Play Store; or `UNEVALUATED`, which means that the licensing details could not be evaluated because a necessary requirement is missing, that is, the device may not be trustworthy enough or the installed app version is not recognized by the Google Play Store.

4. **Device Integrity**: This section presents information that verifies the authenticity of the Android environment in which the app is running.

- `MEETS_DEVICE_INTEGRITY`: The app is on an Android device with Google Play Services, passing system integrity checks and compatibility requirements.
- `MEETS_BASIC_INTEGRITY`: The app is on a device that may not be approved to run Google Play Services but passes basic integrity checks, possibly due to an unrecognized Android version, unlocked bootloader, or lack of manufacturer certification.
- `MEETS_STRONG_INTEGRITY`: The app is on a device with Google Play Services, ensuring robust system integrity with features like hardware-protected boot.
- `MEETS_VIRTUAL_INTEGRITY`: The app runs in an emulator with Google Play Services, passing system integrity checks and meeting Android compatibility requirements.

## API Errors

The API can return local errors such as `APP_NOT_INSTALLED` and `APP_UID_MISMATCH`, which can indicate a fraud attempt or attack. In addition, outdated Google Play Services or Play Store can also cause errors, and it is important to check these situations to ensure proper integrity verification functionality and to ensure the environment is not intentionally set up for an attack. You can find more details on the [official page](https://developer.android.com/google/play/integrity/error-codes).

## Limitations

- **Quota**: The default daily limit is 10.000 requests per day. Applications needing more must contact Google to request an increased limit.
- **Requires Google Play Services**: The API only works on devices with Google Play Services. Apps distributed outside Google Play or running on non-GMS devices (e.g., custom ROMs, some enterprise deployments) cannot obtain a verdict.
- **Requires network connectivity**: Verdict generation requires a live connection to Google's servers. The API cannot be used offline.
