# UnCrackable Mobile Apps

<img align="left" width="100px" src="../Document/Images/Other/uncrackable-logo.png" />

These are the UnCrackable Apps for Android and iOS, a collection of mobile reverse engineering challenges. These challenges are used as examples throughout the OWASP MASTG. Of course, you can also solve them for fun.

See <https://mas.owasp.org/crackmes> for more information.

## ⚠️ Known Issues

### iOS UnCrackable Level 2
There is a known issue with the anti-debugging check in iOS CrackMe Level 2:

- **Issue**: Anti-debugging validation check not working correctly
- **Impact**: Valid solutions may fail validation
- **Workaround**: Focus on 32-bit version, use static analysis
- **Details**: See [iOS UnCrackable L2](iOS/Level_02/) and [MASTG-APP-0026](../apps/ios/MASTG-APP-0026.md)
- **Main Issue**: [commjoen/uncrackable_app#10](https://github.com/commjoen/uncrackable_app/issues/10)

## 📱 Available Challenges

### Android
- **Level 1**: Basic reverse engineering challenge
- **Level 2**: Anti-tampering and anti-debugging

### iOS
- **Level 1**: Basic string extraction challenge
- **Level 2**: Advanced anti-tampering with known issues

## 🔧 Getting Started

1. Choose your platform (Android/iOS)
2. Download the corresponding challenge
3. Read the specific documentation for each challenge
4. Check for known issues before starting
5. Refer to MASTG techniques for guidance
