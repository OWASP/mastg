---
title: Sniffing Implicit Intents and Broadcasts
platform: android
---

When an app sends an implicit intent or broadcast without restricting the recipient (for example, without an explicit target package or a required permission), any app on the device can register to receive it. You can use this technique to observe such intents and broadcasts and inspect the data they carry. See @MASTG-KNOW-0025 for implicit intents and @MASTG-KNOW-0134 for broadcasts.

## Using @MASTG-TOOL-0004

You can observe recent broadcasts for a given action with the activity manager service. Note that this shows the intent metadata but not the contents of the extras:

```bash
adb shell dumpsys activity broadcasts | grep <action>
```

To capture the extras, register a receiver for the action and log the intents it receives. You can do this with a small purpose-built app or with the instrumentation tooling below.

## Using @MASTG-TOOL-0015

drozer can register a receiver that sniffs broadcasts matching a given action and prints the full intent, including its extras:

```bash
run app.broadcast.sniff --action <action>
```

Example output for a broadcast that carries sensitive data in its extras:

```text
Action: <action>
Raw: Intent { act=<action> flg=0x10 (has extras) }
Extra: <key>=<value> (java.lang.String)
```

## Using Method Hooking

You can also observe intents at the point they are sent or received by hooking the relevant APIs (for example, `Context.sendBroadcast`, `Context.startActivity`, or `BroadcastReceiver.onReceive`) as described in @MASTG-TECH-0043. This captures the intent objects from within the target app's process, which is useful when the recipient or required permission prevents an external receiver from observing them.
