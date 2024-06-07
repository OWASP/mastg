---
title: Frida CodeShare
platform: generic
source: https://codeshare.frida.re/
---

[Frida CodeShare](https://codeshare.frida.re/ "Frida CodeShare") is a repository containing a collection of ready-to-run Frida scripts which can enormously help when performing concrete tasks both on Android as on iOS as well as also serve as inspiration to build your own scripts. Two representative examples are:

- Universal Android SSL Pinning Bypass with Frida - <https://codeshare.frida.re/@pcipolloni/universal-android-ssl-pinning-bypass-with-frida/>
- ObjC method observer - <https://codeshare.frida.re/@mrmacete/objc-method-observer/>

Using them is as simple as including the `--codeshare <handler>` flag and a handler when using the Frida CLI. For example, to use "ObjC method observer", enter the following:

```bash
frida --codeshare mrmacete/objc-method-observer -f YOUR_BINARY
```
