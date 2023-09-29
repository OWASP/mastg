---
title: Sandbox Inspection
platform: ios
---

On iOS, each application gets a sandboxed folder to store its data. As per the iOS security model, an application's sandboxed folder cannot be accessed by another application. Additionally, the users do not have direct access to the iOS filesystem, thus preventing browsing or extraction of data from the filesystem. In iOS < 8.3 there were applications available which can be used to browse the device's filesystem, such as iExplorer and iFunBox, but in the recent version of iOS (>8.3) the sandboxing rules are more stringent and these applications do not work anymore. As a result, if you need to access the filesystem it can only be accessed on a jailbroken device. As part of the jailbreaking process, the application sandbox protection is disabled and thus enabling an easy access to sandboxed folders.

The contents of an application's sandboxed folder has already been discussed in "[Accessing App Data Directories](0x06b-iOS-Security-Testing.md#accessing-app-data-directories)" in the chapter iOS Basic Security Testing. This chapter gives an overview of the folder structure and which directories you should analyze.
