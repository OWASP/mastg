---
title: iOS UnCrackable L2
platform: ios
source: https://mas.owasp.org/crackmes/iOS#ios-uncrackable-l2
---

This app holds a secret inside - and this time it won't be tampered with!

> By [Bernhard Mueller](https://github.com/muellerberndt "Bernhard Mueller")

## ⚠️ Known Issues

### Anti-Debugging Check Bug
There is a known issue with the anti-debugging check in iOS CrackMe Level 2 that may prevent the intended solution from working correctly.

**Issue Details:**
- The anti-debugging validation check is not working as intended
- Valid solutions may fail validation due to this bug
- The challenge should work on neither 32-bit nor 64-bit devices

**Workaround Guidelines:**
- **Do not analyze the 64-bit version** - it can mislead you due to cross-platform issues
- **Dynamic analysis should not be sufficient** - this is intended behavior
- **Static analysis won't reveal the password** - but you can recover the logic
- **Verify with sources** - check your logic against the source code

**For Testing:**
1. Focus on the 32-bit version for analysis
2. Use static analysis to understand the logic
3. Verify your approach with the source code
4. Report issues to the main repository: [commjoen/uncrackable_app#10](https://github.com/commjoen/uncrackable_app/issues/10)

**Expected Behavior:**
- The solution should be accepted when the anti-debugging check works correctly
- Until the main repository is fixed, validation may fail even with correct solutions

## 📋 Learning Objectives

Despite the known issue, this challenge teaches:
- Anti-tampering and anti-debugging techniques
- Static analysis of iOS applications
- Reverse engineering methodologies
- Understanding iOS security mechanisms
