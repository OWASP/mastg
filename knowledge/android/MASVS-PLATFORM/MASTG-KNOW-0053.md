---
masvs_category: MASVS-PLATFORM
platform: android
title: System-Generated Screenshots
---

Android captures visual representations of apps for different purposes, which can lead to unintended exposure of sensitive data. When an app goes into the background, the system creates a snapshot of its UI for the [Recents screen](https://developer.android.com/guide/components/activities/recents). This behavior is automatic and helps the user visually recognize tasks when switching apps. If the app does not obscure its UI, sensitive information may appear in these snapshots.

System-generated snapshots for the Recents screen are stored under `/data/system_ce/<user_id>/snapshots` on modern Android versions, or `/data/system` on older versions. These locations are protected by the system sandbox, meaning ordinary apps cannot read them, but they can be accessed on rooted or compromised devices or through forensic acquisition. The exact path and structure may vary depending on the Android version and device vendor.
