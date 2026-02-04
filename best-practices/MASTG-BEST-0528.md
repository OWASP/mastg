---
title: Implementing Root Detection
alias: implementing-root-detection
id: MASTG-BEST-0528
platform: android
knowledge: [MASTG-KNOW-0027]
---

## Overview

Root detection is a defensive mechanism that allows Android apps to identify whether they are running on a rooted device. While root detection alone cannot prevent an attacker from analyzing or tampering with an app, it raises the bar by making it more difficult and time-consuming to perform attacks on rooted devices.

Root detection should be implemented as part of a layered defense strategy, particularly for apps that handle sensitive data or operations.

## Recommendation

Implement multiple root detection checks scattered throughout the app at different API layers to make bypassing more difficult:

### File-Based Checks

Check for common files and directories associated with root:

- Root management binaries: `/system/xbin/su`, `/sbin/su`, `/system/bin/su`
- Root management apps: `/system/app/Superuser.apk`, `/system/app/SuperSU.apk`
- Magisk-related files: `/sbin/.magisk/`, `/data/adb/magisk`

### Package-Based Checks

Use Android's PackageManager to detect installed root management apps:

- `eu.chainfire.supersu`
- `com.topjohnwu.magisk`
- `com.noshufou.android.su`
- `com.koushikdutta.superuser`

### Process-Based Checks

Check for running processes associated with root:

- `daemonsu` (SuperSU daemon)
- `magiskd` (Magisk daemon)

### System Properties Checks

Verify build properties that may indicate custom ROMs or test builds:

- Check `Build.TAGS` for `test-keys` instead of `release-keys`
- Verify presence of Google OTA certificates

### Execution-Based Checks

Attempt to execute privileged commands:

- Try to execute `su` and check for success
- Attempt to write to system directories

### Use Established Libraries

Consider using well-tested libraries that implement multiple detection techniques:

- [RootBeer](https://github.com/scottyab/rootbeer) - A comprehensive root detection library

## Rationale

Root detection helps protect apps from:

- **Dynamic instrumentation**: Tools like Frida and Xposed require root access to hook into app methods
- **Debugger attachment**: Root access enables unrestricted debugging of any app
- **File system access**: Rooted devices allow access to app's private data directory
- **Code modification**: Root enables modification of app files and runtime behavior
- **Memory manipulation**: Root access allows reading and writing to app memory

By detecting root, apps can:

- Display warnings to users about security risks
- Disable sensitive functionality
- Log suspicious activity for monitoring
- Implement additional security measures

## Caveats and Considerations

Root detection has important limitations that should be understood:

### Bypassable by Design

Root detection is inherently bypassable. Attackers with sufficient time and skill can:

- Hook root detection methods using Frida or Xposed
- Patch the app to remove detection logic
- Use kernel-level hooks to hide root artifacts
- Rename or hide files and processes being checked

### False Positives

Root detection may incorrectly flag legitimate scenarios:

- Development and testing devices that are intentionally rooted
- Security researchers performing legitimate security assessments
- Custom ROMs without root access
- Devices with specific manufacturer customizations

### User Experience Impact

Aggressive root detection can negatively impact users:

- Legitimate users may be unable to use the app on rooted devices
- Power users who root for valid reasons (customization, productivity) are penalized
- May drive users to modified/cracked versions without security features

### Implementation Recommendations

To maximize effectiveness:

1. **Layer defenses**: Combine root detection with other security measures (integrity checks, anti-debugging, obfuscation)
2. **Distribute checks**: Scatter detection code throughout the app rather than centralizing it
3. **Use multiple methods**: Implement checks at Java, native, and system call levels
4. **Avoid predictability**: Don't use only well-known detection patterns from public sources
5. **Consider proportional responses**: Rather than blocking all functionality, consider limiting only high-risk operations
6. **Server-side validation**: When possible, perform risk assessments server-side where they cannot be bypassed

## References

- [Android Developer - SafetyNet Attestation API](https://developer.android.com/training/safetynet/attestation)
- [Play Integrity API](https://developer.android.com/google/play/integrity)
- [RootBeer Library Documentation](https://github.com/scottyab/rootbeer)
- [OWASP Mobile Security Testing Guide - Root Detection](https://mobile-security.gitbook.io/mobile-security-testing-guide/android-testing-guide/0x05j-testing-resiliency-against-reverse-engineering#root-detection)
