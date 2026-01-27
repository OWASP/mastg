# iOS UnCrackable Level 2

## 🎯 Challenge Overview

This app holds a secret inside - and this time it won't be tampered with! This advanced iOS reverse engineering challenge focuses on anti-tampering and anti-debugging techniques.

> By [Bernhard Mueller](https://github.com/muellerberndt "Bernhard Mueller")

## ⚠️ IMPORTANT - Known Issues

### Anti-Debugging Bug
**CRITICAL**: There is a known bug in the anti-debugging validation check that affects this challenge.

#### Issue Details:
- **Problem**: Anti-debugging validation check is not working correctly
- **Impact**: Valid solutions may fail validation even when correct
- **Status**: Reported in main repository [commjoen/uncrackable_app#10](https://github.com/commjoen/uncrackable_app/issues/10)

#### Workaround Guidelines:
1. **🚫 DO NOT analyze the 64-bit version** - Cross-platform issues can mislead you
2. **✅ Focus on 32-bit version** - Use this for your analysis
3. **🔍 Static analysis required** - Dynamic analysis alone should not be sufficient
4. **📚 Logic verification** - You won't find the password directly, but can recover the logic
5. **✅ Check with sources** - Verify your approach against the source code

#### Expected Behavior:
- The app should work on neither 32-bit nor 64-bit devices (intended)
- Anti-debugging checks should detect tampering attempts
- Validation should accept correct solutions (currently broken)

## 📁 Files

- `UnCrackable-Level2.ipa` - The iOS application package
- `README.md` - This documentation file

## 🎯 Learning Objectives

Despite the known issue, this challenge teaches:

- **Anti-Tampering Techniques**: Understanding how apps protect themselves
- **Anti-Debugging Methods**: Learning various debugging detection mechanisms
- **Static Analysis**: Advanced static analysis techniques for iOS apps
- **Reverse Engineering**: Comprehensive reverse engineering methodologies
- **iOS Security**: Deep understanding of iOS security mechanisms

## 🔧 Tools Needed

- **Static Analysis**: Ghidra, IDA Pro, Hopper Disassembler
- **iOS Analysis**: Class-dump, Hopper, radare2
- **Debugging**: lldb, Frida (for learning purposes)
- **File Extraction**: IPA extraction tools

## 📋 Step-by-Step Approach

### 1. Setup Environment
```bash
# Extract the IPA
unzip UnCrackable-Level2.ipa
# Analyze the binary
# Focus on 32-bit version only
```

### 2. Static Analysis
- Extract and analyze the main binary
- Focus on 32-bit architecture
- Look for anti-debugging code
- Identify the validation logic

### 3. Understand the Logic
- Don't look for hardcoded passwords
- Recover the validation algorithm
- Understand the anti-tampering checks

### 4. Verify Your Solution
- Check against the source code if available
- Test your approach with the app
- Document your findings

## 🐛 Troubleshooting

### Common Issues:
1. **Validation Fails**: This is the known bug - your solution might be correct
2. **64-bit Analysis**: Avoid 64-bit version - it's misleading
3. **Dynamic Analysis**: Should not be sufficient by design
4. **Password Not Found**: Intended - focus on logic recovery

### Solutions:
1. **Focus on 32-bit**: Use only the 32-bit binary for analysis
2. **Static Analysis**: Use Ghidra or similar tools for static analysis
3. **Logic Recovery**: Understand the algorithm rather than finding passwords
4. **Source Verification**: Check your logic against source code

## 📚 Additional Resources

- [OWASP MASTG - iOS Testing](https://mas.owasp.org/MASTG/)
- [OWASP MASVS - Resilience Requirements](https://mas.owasp.org/MASVS/)
- [iOS Reverse Engineering Guide](https://mas.owasp.org/MASTG/Document/0x04c-Tampering-and-Reverse-Engineering.md)
- [Anti-Debugging Techniques](https://mas.owasp.org/MASTG/Document/0x05j-Testing-Resiliency-Against-Reverse-Engineering.md)

## 🤝 Contributing

If you find additional workarounds or solutions:
1. Document your approach clearly
2. Share findings with the community
3. Report issues to the main repository
4. Help improve the documentation

## ⚖️ License

This challenge is part of the OWASP Mobile Application Security Testing Guide and is licensed under the same terms as the MASTG project.

---

**Note**: This challenge is designed to be difficult and may require advanced reverse engineering skills. The known bug affects validation but doesn't diminish the learning value of understanding the anti-tampering techniques implemented.
