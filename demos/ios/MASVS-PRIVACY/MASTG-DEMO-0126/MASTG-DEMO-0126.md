---
platform: ios
title: Runtime Location Capture with a Deceptive Purpose String
id: MASTG-DEMO-0126
code: [swift, xml]
test: MASTG-TEST-0361
---

## Sample

The app shows a 3-second countdown popup when the **Start** button is tapped. That is the only user-visible feature: there is no map, no search, and no other location-based functionality anywhere in the UI.

Despite that, the app requests location access and collects coarse location coordinates (kilometer-level accuracy) in the background for the full duration of the countdown. When the countdown ends, the coordinates are written to `location_capture.txt` in the app's Documents directory. The user sees a "3s Timer started" popup with no mention of location at any point.

The `Info.plist` declares a single purpose string, `NSLocationWhenInUseUsageDescription`, with the text "We use your location to show you nearby content and recommendations." This is **deceptive**: it describes a feature that does not exist in the app, and the purpose string shown in the system prompt is inconsistent with the only observable behavior (a countdown).

{{ MastgTest.swift # Info.plist }}

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0153 to retrieve `./Payload/MASTestApp.app/Info.plist` and save it as `Info.plist` in this demo directory.
3. Use @MASTG-TECH-0138 to convert `Info.plist` to a readable format if needed.
4. Use @MASTG-TECH-0154 to inspect the purpose strings by running `run.sh`.
5. Install the app on a device using @MASTG-TECH-0056.
6. Make sure @MASTG-TOOL-0039 is installed on your machine and `frida-server` is running on the device.
7. Run `run_frida.sh` to spawn the app with Frida (@MASTG-TECH-0095).
8. Tap the **Start** button to trigger the countdown and observe the location access in the background.
9. Stop the script by pressing `Ctrl+C`.

{{ run_frida.sh # script.js }}

## Observation

The output reveals the purpose string declared in the app's `Info.plist` file.

{{ output_purpose_strings.txt }}

The only declared purpose string is:

- `NSLocationWhenInUseUsageDescription`

The Frida script output reveals the location APIs reached at runtime while the countdown runs.

{{ output_frida.txt }}

The runtime trace shows that tapping **Start**:

1. Calls `CLLocationManager.requestWhenInUseAuthorization`, which displays `NSLocationWhenInUseUsageDescription` to the user. The backtrace links the call to `LocationCapture.init()`, invoked via `LocationCapture.__allocating_init()` from `MastgTest.mastgTest(completion:)`.
2. Calls `CLLocationManager.startUpdatingLocation` once the user grants permission. The backtrace originates in `LocationCapture.locationManagerDidChangeAuthorization(_:)`, called by CoreLocation after authorization is granted, confirming that coarse location collection begins immediately.
3. Calls `CLLocationManager.stopUpdatingLocation` when the countdown ends. The backtrace shows `LocationCapture.stop()` called from `closure #1 in closure #1 in static MastgTest.mastgTest(completion:)`, which is the `onFinish` block invoked by UIKit (`-[UIPresentationController transitionDidFinish:]`) when the countdown alert's dismiss animation completes.

## Evaluation

The test case fails because the declared purpose string is deceptive. It tells the user that location is used to show nearby content and recommendations, but the app does not provide any nearby content, recommendations, map, search, or other location-based feature.

The only user-visible feature is a 3-second countdown popup. The runtime trace confirms that location authorization and collection APIs are reached during that countdown flow, so the purpose string is not merely unused or stale. It is shown for reachable location access that is inconsistent with the app's observable behavior.

The user must still grant location permission before the app can access the protected resource. However, the issue remains valid because the authorization prompt is based on an inaccurate explanation, and the observed location access is not justified by the app's visible functionality.
