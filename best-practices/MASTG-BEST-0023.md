---
title: Exclude Sensitive Information from Backups
alias: exclude-sensitive-information-from-backups
id: MASTG-BEST-0023
platform: ios
knowledge: [MASTG-KNOW-0102]
---

iOS does not provide a guaranteed mechanism to exclude files from backups. Setting [`NSURLIsExcludedFromBackupKey`](https://developer.apple.com/documentation/foundation/urlresourcekey/isexcludedfrombackupkey) instructs the system not to include a file in backups, but it does not ensure exclusion. To reduce data exposure, apply the following techniques:

## Bind Data to the Current Device

Store sensitive data in the Keychain and mark it with [`kSecAttrAccessibleWhenUnlockedThisDeviceOnly`](https://developer.apple.com/documentation/security/ksecattraccessiblewhenunlockedthisdeviceonly) or [`kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly`](https://developer.apple.com/documentation/security/ksecattraccessiblewhenpasscodesetthisdeviceonly) to keep secrets restricted to the current device. When implementing this, create a new Keychain entry and set the `kSecAttrAccessible` attribute to one of the `ThisDeviceOnly` values when inserting the item.

### Handling Larger Files

For larger files, store them encrypted within the app container and keep the decryption key in the Keychain using a `ThisDeviceOnly` accessibility class. When you need to use the files, decrypt them only into RAM or into non‑backed‑up locations such as `/Library/Caches` or `/tmp`. Note that these locations are [_purgeable_](https://developer.apple.com/documentation/foundation/optimizing-your-app-s-data-for-icloud-backup), and the system may delete their contents at any time; avoid treating them as durable storage. Be prepared to re‑decrypt the files on demand if they are cleared.

## Bind Data to the User via a Server-Managed Key

If binding to the device is insufficient, you can instead bind data to a user by storing the decryption key on your server and releasing it only after successful authentication. Do not persist this key on the device; keep it only in RAM and decrypt files as described above.
