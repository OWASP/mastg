---
masvs_category: MASVS-RESILIENCE
platform: generic
---

# Mobile App Tampering and Reverse Engineering

For a long time, reverse engineering and tampering techniques have belonged to the realm of crackers, modders, and malware analysts. For "traditional" security testers and researchers, reverse engineering has been more of a complementary skill. However, the tides are turning. Mobile app black-box testing increasingly requires disassembling compiled apps, applying patches, and tampering with binary code or live processes. The fact that many mobile apps implement defenses against tampering doesn't make things easier for security testers.

Reverse engineering a mobile app involves analyzing the compiled app to extract information about its source code. The goal of reverse engineering is to _comprehend_ the code.

_Tampering_ is the process of changing a mobile app (either the compiled app or the running process) or its environment to affect its behavior. For example, an app might refuse to run on your rooted test device, which would make it impossible to run some of your tests. In such cases, you'll want to alter the app's behavior.

Mobile security testers benefit from understanding basic reverse engineering concepts. They should also be familiar with mobile devices and operating systems, including processor architecture, executable formats, and programming language intricacies.

Reverse engineering is an art form, and describing all its facets would fill a library. The range of techniques and specializations is staggering: one could spend years working on a very specific, isolated subproblem, such as automating malware analysis or developing novel deobfuscation methods. Security testers are generalists. To be effective reverse engineers, they must filter through vast amounts of relevant information.

There is no generic reverse engineering process that always works. That said, we'll describe commonly used methods and tools later in this guide and provide examples of how to tackle the most common defenses.

## Why You Need It

There are several reasons why mobile security testing requires at least basic reverse engineering skills:

1. **To enable black-box testing of mobile apps:** Modern apps often include controls that hinder dynamic analysis. SSL pinning and end-to-end (E2E) encryption can prevent you from intercepting or manipulating traffic with a proxy. Root detection could prevent the app from running on a rooted device, which would prevent you from using advanced testing tools. You must be able to deactivate these defenses.

2. **To enhance static analysis in black-box security testing:** In a black-box test, static analysis of the app's bytecode or binary code helps you understand its internal logic. It also allows you to identify flaws, such as hardcoded credentials.

3. **To assess resilience against reverse engineering:** Apps that implement the software protection measures listed in the Mobile Application Security Verification Standard Anti-Reversing Controls (MASVS-R) should withstand reverse engineering to a certain degree. To verify the effectiveness of these controls, the tester performs a _resilience assessment_ as part of the general security test. In this assessment, the tester assumes the role of the reverse engineer and attempts to bypass defenses.

Before we dive into the world of mobile app reverse engineering, we have some good news and some bad news. Let's start with the good news.

**Ultimately, the reverse engineer always wins.**

This is especially true in the mobile industry. The way mobile apps are deployed and sandboxed is, by design, more restrictive than the deployment and sandboxing of traditional desktop apps. Therefore, including rootkit-like defensive mechanisms, which are often found in Windows software (e.g., DRM systems), is simply not feasible. Android's openness allows reverse engineers to modify the operating system, which aids the reverse engineering process, while iOS gives reverse engineers less control but also has more limited defensive options.

The bad news is that dealing with multi-threaded anti-debugging controls, cryptographic white boxes, stealthy anti-tampering features, and highly complex control flow transformations is not for the faint of heart. The most effective software protection schemes are proprietary and cannot be defeated with standard tweaks and tricks. Defeating them requires tedious manual analysis and coding. Depending on your personality, it can also lead to sleepless nights and strained relationships.

Beginners may be overwhelmed by the scope of reversing. The best way to start is to set up basic tools (see the relevant sections in the Android and iOS reversing chapters) and begin with simple reversing tasks and crackmes. You'll need to learn the assembler/bytecode language, operating system, and obfuscations you encounter. Start with simple tasks and gradually progress to more difficult ones.

This section provides an overview of the most commonly used techniques in mobile app security testing. These include basic tampering techniques, as well as static and dynamic binary analysis.

More complicated tasks, such as de-obfuscating heavily obfuscated binaries, require the automation of certain parts of the analysis. For instance, manually analyzing a complex control flow graph in a disassembler would take years and likely drive you mad before completion. Instead, you can augment your workflow with custom-made tools. Fortunately, modern disassemblers come with scripting and extension APIs, and many useful extensions are available for popular disassemblers. There are also open-source disassembly engines and binary analysis frameworks.

As is often the case in hacking, anything goes: simply use whatever is most efficient. Every binary is different, and all reverse engineers have their own style. Often, the best way to achieve your goal is to combine approaches, such as emulator-based tracing and symbolic execution. To get started, choose a good disassembler and/or reverse engineering framework and become familiar with its features and APIs. Ultimately, the best way to improve is to gain hands-on experience.
