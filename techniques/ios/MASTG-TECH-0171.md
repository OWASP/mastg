---
title: Monitoring Data Sharing Between App Extensions and Containing Apps
platform: ios
---

App extensions and their containing apps exchange data through inter-process communication built on XPC (see @MASTG-KNOW-0082). You can observe the items being passed by hooking the relevant Foundation APIs at runtime with @MASTG-TECH-0095.

## Inspecting the Items Being Shared

Hook `NSExtensionContext - inputItems` in the data-originating app to inspect the items an extension receives.

As an example, in Telegram we use the "Share" button on a text file (received from a chat) to create a note in the Notes app:

<img src="Images/Chapters/0x06h/telegram_share_extension.png" width="400px" />

If we run a trace, we'd see the following output:

```bash
(0x1c06bb420) NSExtensionContext - inputItems
0x18284355c Foundation!-[NSExtension _itemProviderForPayload:extensionContext:]
0x1828447a4 Foundation!-[NSExtension _loadItemForPayload:contextIdentifier:completionHandler:]
0x182973224 Foundation!__NSXPCCONNECTION_IS_CALLING_OUT_TO_EXPORTED_OBJECT_S3__
0x182971968 Foundation!-[NSXPCConnection _decodeAndInvokeMessageWithEvent:flags:]
0x182748830 Foundation!message_handler
0x181ac27d0 libxpc.dylib!_xpc_connection_call_event_handler
0x181ac0168 libxpc.dylib!_xpc_connection_mach_event
...
RET: (
"<NSExtensionItem: 0x1c420a540> - userInfo:
{
    NSExtensionItemAttachmentsKey =     (
    "<NSItemProvider: 0x1c46b30e0> {types = (\n \"public.plain-text\",\n \"public.file-url\"\n)}"
    );
}"
)
```

Here we can observe that:

- This occurred under-the-hood via XPC, concretely it is implemented via a `NSXPCConnection` that uses the `libxpc.dylib` Framework.
- The UTIs included in the `NSItemProvider` are `public.plain-text` and `public.file-url`, the latter being included in `NSExtensionActivationRule` from the [`Info.plist` of the "Share Extension" of Telegram](https://github.com/TelegramMessenger/Telegram-iOS/blob/master/Telegram/Share/Info.plist "Info.plist of the \'Share Extension\' of Telegram").

## Identifying the App Extensions Involved

You can also find out which app extension handles the requests and responses by hooking `NSExtension - _plugIn`:

We run the same example again:

```bash
(0x1c0370200) NSExtension - _plugIn
RET: <PKPlugin: 0x1163637f0 ph.telegra.Telegraph.Share(5.3) 5B6DE177-F09B-47DA-90CD-34D73121C785
1(2) /private/var/containers/Bundle/Application/15E6A58F-1CA7-44A4-A9E0-6CA85B65FA35
/Telegram X.app/PlugIns/Share.appex>

(0x1c0372300)  -[NSExtension _plugIn]
RET: <PKPlugin: 0x10bff7910 com.apple.mobilenotes.SharingExtension(1.5) 73E4F137-5184-4459-A70A-83
F90A1414DC 1(2) /private/var/containers/Bundle/Application/5E267B56-F104-41D0-835B-F1DAB9AE076D
/MobileNotes.app/PlugIns/com.apple.mobilenotes.SharingExtension.appex>
```

As you can see there are two app extensions involved:

- `Share.appex` is sending the text file (`public.plain-text` and `public.file-url`).
- `com.apple.mobilenotes.SharingExtension.appex` which is receiving and will process the text file.

If you want to learn more about what's happening under-the-hood in terms of XPC, we recommend to take a look at the internal calls from "libxpc.dylib". For example you can use [`frida-trace`](https://www.frida.re/docs/frida-trace/ "frida-trace") and then dig deeper into the methods that you find more interesting by extending the automatically generated stubs.
