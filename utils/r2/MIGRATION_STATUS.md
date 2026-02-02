# Demo Migration Status

This document tracks the status of migrating all `.r2` scripts to Python with r2ooky.

## Migration Status

| Demo ID | Category | Description | Status | Config | Script |
|---------|----------|-------------|--------|--------|--------|
| MASTG-DEMO-0015 | CRYPTO | CommonCrypto hash functions | ✅ Complete | ✅ | ✅ |
| MASTG-DEMO-0014 | CRYPTO | CryptoKit ECDSA private key | ✅ Complete | ✅ | ✅ |
| MASTG-DEMO-0086 | NETWORK | BSD socket functions | ✅ Complete | ✅ | ✅ |
| MASTG-DEMO-0084 | NETWORK | HTTP URLs | ✅ Complete | ✅ | ✅ |
| MASTG-DEMO-0011 | CRYPTO | Security framework key size | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0013 | CRYPTO | Hardcoded RSA key | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0016 | CRYPTO | CryptoKit hash functions | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0018 | CRYPTO | CCCrypt usage | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0073 | CRYPTO | Insecure random | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0080 | CRYPTO | CCCrypt ECB mode | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0019 | STORAGE | isExcludedFromBackup | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0065 | STORAGE | Logging APIs | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0076 | STORAGE | Text inputs | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0085 | NETWORK | Low-level network (port literals) | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0021 | RESILIENCE | Jailbreak detection | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0024 | RESILIENCE | Device passcode check | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0041 | AUTH | Insecure biometric auth | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0043 | AUTH | Biometric auth fallback | 🔄 Pending | ❌ | ❌ |
| MASTG-DEMO-0045 | AUTH | Biometric enrollment change | 🔄 Pending | ❌ | ❌ |

**Total:** 19 demos  
**Completed:** 4 (21%)  
**Remaining:** 15 (79%)

## Pattern Distribution

The demos follow these main patterns:

### Pattern 1: Function → XRefs → Disasm (8 demos)
- MASTG-DEMO-0015 ✅ (CommonCrypto hash)
- MASTG-DEMO-0016 (CryptoKit hash)
- MASTG-DEMO-0018 (CCCrypt)
- MASTG-DEMO-0080 (CCCrypt ECB)
- MASTG-DEMO-0073 (Insecure random)
- MASTG-DEMO-0021 (Jailbreak detection)
- MASTG-DEMO-0024 (Device passcode)
- MASTG-DEMO-0019 (isExcludedFromBackup)

### Pattern 2: Function → XRefs → Disasm + Dump (4 demos)
- MASTG-DEMO-0014 ✅ (CryptoKit ECDSA)
- MASTG-DEMO-0013 (Hardcoded RSA)
- MASTG-DEMO-0011 (Key size)

### Pattern 3: Import → XRefs → Disasm (2 demos)
- MASTG-DEMO-0086 ✅ (BSD sockets)

### Pattern 4: String → XRefs → Disasm (2 demos)
- MASTG-DEMO-0084 ✅ (HTTP URLs)
- MASTG-DEMO-0065 (Logging APIs - might use strings)

### Pattern 5: Function → XRefs → Disasm + Literal Eval (1 demo)
- MASTG-DEMO-0085 (Network port literals)

### Pattern 6: UI/Widget Analysis (2 demos)
- MASTG-DEMO-0076 (Text inputs)
- MASTG-DEMO-0041 (Biometric auth)
- MASTG-DEMO-0043 (Biometric fallback)
- MASTG-DEMO-0045 (Biometric enrollment)

## Migration Priority

### Phase 1: Core Patterns (✅ Complete)
1. ✅ MASTG-DEMO-0015 - Pattern 1 reference
2. ✅ MASTG-DEMO-0014 - Pattern 2 reference
3. ✅ MASTG-DEMO-0086 - Pattern 3 reference
4. ✅ MASTG-DEMO-0084 - Pattern 4 reference

### Phase 2: Pattern Variations
5. MASTG-DEMO-0085 - Pattern 5 (port literals)
6. MASTG-DEMO-0016 - Pattern 1 variant
7. MASTG-DEMO-0018 - Pattern 1 variant
8. MASTG-DEMO-0073 - Pattern 1 variant

### Phase 3: Remaining Crypto/Storage
9. MASTG-DEMO-0080 - CCCrypt ECB
10. MASTG-DEMO-0013 - Hardcoded RSA
11. MASTG-DEMO-0011 - Key size
12. MASTG-DEMO-0019 - Backup exclusion
13. MASTG-DEMO-0065 - Logging APIs

### Phase 4: Resilience/Auth
14. MASTG-DEMO-0021 - Jailbreak detection
15. MASTG-DEMO-0024 - Device passcode
16. MASTG-DEMO-0076 - Text inputs
17. MASTG-DEMO-0041 - Biometric auth
18. MASTG-DEMO-0043 - Biometric fallback
19. MASTG-DEMO-0045 - Biometric enrollment

## Testing Checklist

For each migrated demo:

- [ ] Config file is valid JSON
- [ ] Python script imports r2ooky correctly
- [ ] Script runs without errors
- [ ] Output format matches original
- [ ] No hardcoded addresses remain
- [ ] Handles missing symbols gracefully
- [ ] Documentation updated
- [ ] Old files kept for reference

## Notes

- Keep original `.r2` files and `run.sh` for reference
- Add `config.json` and `run_r2ooky.py` alongside originals
- Generate `output_r2ooky.txt` to compare with `output.txt`
- Update demo markdown to reference both approaches during transition
