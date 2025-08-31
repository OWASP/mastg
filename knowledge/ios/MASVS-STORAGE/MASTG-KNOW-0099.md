---
masvs_category: MASVS-STORAGE
platform: ios
title: Screenshots
---

Manufacturers want to provide device users with an aesthetically pleasing effect when an application is started or exited, so they introduced the concept of saving a screenshot when the application goes into the background. This feature can pose a security risk because screenshots (which may display sensitive information such as an email or corporate documents) are written to local storage, where they can be recovered by a rogue application with a sandbox bypass exploit or someone who steals the device.

Screenshots are stored inside the app's container at
`/var/mobile/Containers/Data/Application/$APP_ID/Library/SplashBoard/Snapshots/sceneID:$APP_NAME-default/`. They are overwritten every time the app enters the background state.

The system takes the screenshot after [`applicationDidEnterBackground`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/applicationdidenterbackground(_:)) completes, so it's common to display an overlay over the content of the screen. When an app comes back to the foreground, [`applicationWillEnterForeground`](https://developer.apple.com/documentation/uikit/uiapplicationdelegate/applicationwillenterforeground(_:)) is called.

You can find more information in ["Prepare your UI for the app snapshot"](https://developer.apple.com/documentation/uikit/preparing-your-ui-to-run-in-the-background#Prepare-your-UI-for-the-app-snapshot).
