---
masvs_category: MASVS-RESILIENCE
platform: ios
title: File Integrity Checks
best-practices: [MASTG-BEST-0x01]
---

iOS apps can implement two complementary approaches to verify integrity at runtime: checking the application source code itself, and checking the integrity of data stored on the device.

## Application Source Code Integrity Checks

iOS uses code signing to verify app authenticity before launch (see @MASTG-TECH-0084). Apps can also implement additional runtime checks that inspect the Mach-O binary structure to verify the integrity of the executable code. A common approach is to:

1. Use `dladdr` to resolve the base address of the loaded binary.
2. Parse the Mach-O `mach_header` and iterate through load commands to locate the `__TEXT/__text` section.
3. Compute a cryptographic hash over the `__text` section bytes and compare it against a stored reference value.

The following C example illustrates this pattern using `CC_SHA256` from CommonCrypto:

```c
int xyz(char *dst) {
    const struct mach_header * header;
    Dl_info dlinfo;

    if (dladdr(xyz, &dlinfo) == 0 || dlinfo.dli_fbase == NULL) {
        NSLog(@" Error: Could not resolve symbol xyz");
        [NSThread exit];
    }

    while(1) {

        header = dlinfo.dli_fbase;  // Pointer on the Mach-O header
        struct load_command * cmd = (struct load_command *)(header + 1); // First load command
        // Now iterate through load command
        //to find __text section of __TEXT segment
        for (uint32_t i = 0; cmd != NULL && i < header->ncmds; i++) {
            if (cmd->cmd == LC_SEGMENT) {
                // __TEXT load command is a LC_SEGMENT load command
                struct segment_command * segment = (struct segment_command *)cmd;
                if (!strcmp(segment->segname, "__TEXT")) {
                    // Stop on __TEXT segment load command and go through sections
                    // to find __text section
                    struct section * section = (struct section *)(segment + 1);
                    for (uint32_t j = 0; section != NULL && j < segment->nsects; j++) {
                        if (!strcmp(section->sectname, "__text"))
                            break; //Stop on __text section load command
                        section = (struct section *)(section + 1);
                    }
                    // Get here the __text section address, the __text section size
                    // and the virtual memory address so we can calculate
                    // a pointer on the __text section
                    uint32_t * textSectionAddr = (uint32_t *)section->addr;
                    uint32_t textSectionSize = section->size;
                    uint32_t * vmaddr = segment->vmaddr;
                    char * textSectionPtr = (char *)((int)header + (int)textSectionAddr - (int)vmaddr);
                    // Calculate the SHA-256 hash of the __text section
                    unsigned char digest[CC_SHA256_DIGEST_LENGTH];
                    CC_SHA256(textSectionPtr, textSectionSize, digest);
                    for (int i = 0; i < sizeof(digest); i++)
                        sprintf(dst + (2 * i), "%02x", digest[i]);

                    // return strcmp(originalSignature, signature) == 0;    // verify signatures match

                    return 0;
                }
            }
            cmd = (struct load_command *)((uint8_t *)cmd + cmd->cmdsize);
        }
    }

}
```

These checks can be bypassed on jailbroken devices, for example by patching the stored reference hash or hooking the comparison logic at runtime.

## File Storage Integrity Checks

Apps can protect data stored on the device (for example in the Keychain, `UserDefaults`/`NSUserDefaults`, or a database) by computing an HMAC or cryptographic signature over it and verifying that value before each use.

A common approach uses `CCHmac` from CommonCrypto with a key held in the Keychain:

```objectivec
// Generate HMAC
NSMutableData* actualData = [getData];
NSData* key = [getKey];  // key retrieved from the Keychain
NSMutableData* digestBuffer = [NSMutableData dataWithLength:CC_SHA256_DIGEST_LENGTH];
CCHmac(kCCHmacAlgSHA256, [key bytes], (CC_LONG)[key length], [actualData bytes], (CC_LONG)[actualData length], [digestBuffer mutableBytes]);
[actualData appendData: digestBuffer];
```

Verification recomputes the HMAC and compares it to the stored value:

```objectivec
// Verify HMAC
NSData* hmac = [data subdataWithRange:NSMakeRange(data.length - CC_SHA256_DIGEST_LENGTH, CC_SHA256_DIGEST_LENGTH)];
NSData* actualData = [data subdataWithRange:NSMakeRange(0, (data.length - hmac.length))];
NSMutableData* digestBuffer = [NSMutableData dataWithLength:CC_SHA256_DIGEST_LENGTH];
CCHmac(kCCHmacAlgSHA256, [key bytes], (CC_LONG)[key length], [actualData bytes], (CC_LONG)[actualData length], [digestBuffer mutableBytes]);
return [hmac isEqual: digestBuffer];
```

Alternatively, the [Security framework](https://developer.apple.com/documentation/security) provides `SecKeyCreateSignature` and `SecKeyVerifySignature` for asymmetric signing of stored data.

When data is both encrypted and MACed, the [Encrypt-then-MAC](https://web.archive.org/web/20210804035343/https://cseweb.ucsd.edu/~mihir/papers/oem.html "Authenticated Encryption: Relations among notions and analysis of the generic composition paradigm") ordering provides stronger integrity guarantees: the HMAC is computed over the ciphertext rather than the plaintext.

These checks can be circumvented on jailbroken devices by extracting the HMAC key from the Keychain or by intercepting the verification function at runtime.
