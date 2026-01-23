---
title: Inspecting the Merged AndroidManifest
platform: android
---

During the build process, the Android build tools merge multiple manifest files from different sources (main app, flavors, and libraries) into a single merged manifest file. This merged manifest file is what gets packaged into the final APK or AAB.

Inspecting the merged manifest file can help you understand the final configuration of the app, including permissions, components (i.e, Activities, Services, Providers), and other settings. It can also help you identify the inherited permissions and components from third-party libraries.

### Using @MASTG-TOOL-0007

1. Open your project in Android Studio.
2. In the Project view, navigate to `app/src/main/AndroidManifest.xml`.
3. Click on the "Merged Manifest" tab at the bottom of the editor window. This will show you the merged manifest file, along with a tree view of the sources that contributed to each element.
4. You can click on any element, such as a permission or an Activity, in the merged manifest to see its source in the tree view.

### Using Command-Line Tools

> Limitation:
> With this method, you cannot track the source of each element in the merged manifest file. For this, you will need to use Android Studio, as described [here](#using-mastg-tool-0007).

1. You can manually generate the merged manifest file of the desired build variant using a command like `./gradlew app:processDebugManifest`. Replace `Debug` with the desired build variant if necessary.
2. After running the command, you can find the merged manifest file in the `app/build/intermediates/merged_manifests/debug/AndroidManifest.xml` directory. Again, replace `debug` with the appropriate build variant if needed.
3. Open the `AndroidManifest.xml` file in a text editor to inspect its contents.
