---
title: Information Gathering - Network Communication
platform: ios
---

Most of the apps you might encounter connect to remote endpoints. Even before you perform any dynamic analysis (e.g., traffic capture and analysis), you can obtain some initial inputs or entry points by enumerating the domains to which the application is supposed to communicate.

Typically, these domains are stored as strings in the application's binary. One can extract domains by retrieving strings (as discussed above) or by checking them with tools like Ghidra. The latter option has a clear advantage: it provides context, as you can see which context each domain is used in by checking the cross-references.

From here on, you can use this information to derive more insights that might be of use later during your analysis, e.g., you could match the domains to the pinned certificates or perform further reconnaissance on domain names to know more about the target environment.

Implementing and verifying secure connections can be an intricate process, with many factors to consider. For instance, many applications use protocols other than HTTP, such as XMPP or plain TCP, or perform certificate pinning to deter MITM attacks.

Remember that, in most cases, relying solely on static analysis is not enough and can be highly inefficient compared with dynamic alternatives, which often yield more reliable results (e.g., by using an interception proxy). In this section, we've only scratched the surface. Please refer to @MASTG-TECH-0062 in the "iOS Basic Security Testing" chapter and review the test cases in the "[iOS Network Communication](../../Document/0x06g-Testing-Network-Communication.md)" chapter for further information.
