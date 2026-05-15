---
title: Retrieving Strings
platform: ios
---

Strings are always a good starting point while analyzing a binary, as they provide context to the associated code. For instance, an error log string such as "Cryptogram generation failed" suggests that the adjoining code might be responsible for generating the cryptogram.

To extract strings from an iOS binary, you can use GUI tools such as Ghidra or [iaito](https://github.com/radareorg/iaito "iaito") or rely on CLI-based tools such as the _strings_ Unix utility (`strings <path_to_binary>`) or radare2's @MASTG-TOOL-0129 (`rabin2 -zz <path_to_binary>`). When using CLI-based tools, you can leverage other tools, such as grep (with regular expressions), to further filter and analyze the results.
