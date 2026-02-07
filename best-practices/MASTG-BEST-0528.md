---
title: Implementing Root Detection
alias: implementing-root-detection
id: MASTG-BEST-0528
platform: android
knowledge: [MASTG-KNOW-0027]
---

## Overview

Root detection is a defensive mechanism that allows Android apps to identify whether they are running on a rooted device. While root detection alone cannot prevent an attacker from analyzing or tampering with an app, it raises the bar by making it more difficult and time-consuming to perform attacks on rooted devices.

See @MASTG-KNOW-0027 for more information on root detection techniques and specific APIs and artifacts to look for.

To maximize effectiveness of the root detection techniques, consider the following best practices:

1. **Layer defenses**: Combine root detection with other security measures (integrity checks, anti-debugging, obfuscation).
2. **Distribute checks**: Scatter detection code throughout the app rather than centralizing it.
3. **Use multiple methods**: Implement checks at Java, native, and system call levels.
4. **Avoid well-known patterns**: Don't use only well-known detection patterns from public sources.
5. **Consider proportional responses**: Rather than blocking all functionality, consider limiting only high-risk operations.
6. **Server-side validation**: When possible, perform risk assessments server-side where they cannot be bypassed.
7. **Randomness:** Introduce randomness in the placement and implementation of detection logic, so attackers cannot easily reuse patches or prior knowledge of the checks' implementation across builds.

## Caveats and Considerations

Root detection has important limitations that should be understood:

- **Root detection is inherently bypassable:** see @MASTG-TECH-0542. Attackers with sufficient time and skill can:
    - Hook root detection methods using Frida or Xposed
    - Patch the app to remove detection logic
    - Use kernel-level hooks to hide root artifacts
    - Rename or hide files and processes being checked

- **Root detection may incorrectly flag legitimate scenarios:**
    - Development and testing devices that are intentionally rooted
    - Security researchers performing legitimate security assessments
    - Custom ROMs without root access
    - Devices with specific manufacturer customizations

- **Aggressive root detection can negatively impact user experience:**
    - Legitimate users may be unable to use the app on rooted devices
    - Power users who root for valid reasons (customization, productivity) are penalized
    - May drive users to modified/cracked versions without security features
