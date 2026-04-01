---
masvs_category: MASVS-STORAGE
platform: ios
title: App Sandbox Directories
---

On iOS, each application gets a sandboxed folder to store its data. As per the iOS security model, an application's sandboxed folder cannot be accessed by another application. Additionally, the users do not have direct access to the [iOS filesystem](https://developer.apple.com/library/archive/documentation/FileManagement/Conceptual/FileSystemProgrammingGuide/FileSystemOverview/FileSystemOverview.html#//apple_ref/doc/uid/TP40010672-CH2-SW12), thus preventing browsing or extraction of data from the filesystem.

There are several ways to access the app's sandboxed folder:

- **On any device - Only Debug Builds**: You can use Xcode's Devices and Simulators window to download the app container.
- **On the iOS Simulator - All Built-in Apps and Debug Builds**: You can navigate to the app's sandboxed folder directly from the macOS filesystem.
- **On a non-jailbroken device - Only Repackaged Apps or Debug Builds**: You can use @MASTG-TECH-0090 and after that, use @MASTG-TOOL-0074 to explore the app's directory structure.
- **On a jailbroken device - All Apps**:
    - You can use SSH or a file explorer app to navigate the filesystem and access the sandboxed folder directly.
    - You can use @MASTG-TOOL-0074 to explore the app's directory structure.

## Application Folder Structure

The following illustration represents the application folder structure:

<img src="Images/Chapters/0x06a/iOS_Folder_Structure.png" width="400px" />

On iOS, system applications can be found in the `/Applications` directory while user-installed apps are available under `/private/var/containers/`. However, finding the right folder just by navigating the file system is not a trivial task as every app gets a random 128-bit UUID (Universal Unique Identifier) assigned for its directory names.

```txt
Bundle: /private/var/containers/Bundle/Application/3ADAF47D-A734-49FA-B274-FBCA66589E67
Application: /private/var/containers/Bundle/Application/3ADAF47D-A734-49FA-B274-FBCA66589E67/iGoat-Swift.app
Data: /private/var/mobile/Containers/Data/Application/8C8E7EB0-BC9B-435B-8EF8-8F5560EB0693
```

As you can see, apps have two main locations:

- The Bundle directory (`/var/containers/Bundle/Application/3ADAF47D-A734-49FA-B274-FBCA66589E67/`).
- The Data directory (`/var/mobile/Containers/Data/Application/8C8E7EB0-BC9B-435B-8EF8-8F5560EB0693/`).

These folders contain information that must be examined closely during application security assessments (for example when analyzing the stored data for sensitive data).

### Bundle directory

- **AppName.app**
    - This is the application bundle as seen in the IPA, it contains essential application resources as well as the compiled binary.
    - This directory is not writable at runtime and is not normally visible to users in the Files app.
    - Content in this directory is not backed up, it is distributed with the app and can be restored by reinstalling the app.
    - The contents of this folder are used to validate the code signature.

### Data directory

- **Documents/**
    - Contains user data that should persist and be included in backups, typically user created or user visible content.
    - May be visible to users in the Files app or via file sharing, depending on app configuration, and users can write to it in those cases.
    - Content in this directory is backed up by default.

- **Library/**
    - Contains app specific support files, such as caches, preferences, cookies, and configuration data that is not directly user facing.
    - iOS apps usually use the `Application Support` and `Caches` subdirectories, but the app can create custom subdirectories under `Library`.

- **Library/Caches/**
    - Contains semi-persistent cached files that can be regenerated.
    - Invisible to users and users cannot write to it directly.
    - Content in this directory is not backed up.
    - The OS may delete files in this directory automatically, for example when storage space is low.

- **Library/Application Support/**
    - Contains persistent files necessary for running the app, such as databases or other support data.
    - Invisible to users and users cannot write to it directly.
    - Content in this directory is backed up by default.

- **Library/Preferences/**
    - Used for storing preference values that persist across launches.
    - Information is saved, unencrypted, inside the application sandbox in a plist file named after the app bundle identifier, for example `[BUNDLE_ID].plist`.
    - All the key-value pairs stored using `UserDefaults` or `NSUserDefaults` can be found in this file.
    - Content in this directory is backed up by default.

- **tmp/**
    - Use this directory to write temporary files that do not need to persist between app launches.
    - Contains non-persistent cached or scratch files.
    - Invisible to users and users cannot write to it directly.
    - Content in this directory is not backed up.
    - The OS may delete files in this directory automatically at any time, including while the app is not running, especially when storage space is low.
