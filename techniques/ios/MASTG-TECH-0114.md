---
title: Demangling Symbols
platform: ios
---

To make some identifiers unique within the program, the compiler processes their symbol names. This process is called "name mangling" or simply "mangling". Often, the resulting symbols are complex for humans to interpret. Additionally, their format is specific to the input language and the compiler and may even be version-dependent.

You can use demangling tools to reverse the mangling process. For Swift, see @MASTG-TOOL-0067 and for C++ function names, @MASTG-TOOL-0122.

## swift-demangle

Pass the mangled symbol to @MASTG-TOOL-0067:

```bash
$ xcrun swift-demangle __T0So9WKWebViewCABSC6CGRectV5frame_So0aB13ConfigurationC13configurationtcfcTO
_T0So9WKWebViewCABSC6CGRectV5frame_So0aB13ConfigurationC13configurationtcfcTO ---> @nonobjc __C.WKWebView.init(frame: __C_Synthesized.CGRect, configuration: __C.WKWebViewConfiguration) -> __C.WKWebView
```

## c++filt

You can demangle C++ symbols with @MASTG-TOOL-0122:

```bash
c++filt _ZSt6vectorIiSaIiEE
std::vector<int, std::allocator<int>>
```
