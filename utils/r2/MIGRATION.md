# Migration Guide: .r2 Scripts to Python with r2ooky

This guide shows how to convert each existing `.r2` script pattern into Python with r2ooky.

## Demo Patterns Overview

The existing 19 demos use these common patterns:

1. **CommonCrypto hash functions** - List functions, show xrefs, disassemble call sites
2. **CryptoKit private keys** - Find methods, show xrefs, dump function and bytes
3. **Network port literals** - Find function, show xrefs, extract literal values
4. **BSD sockets** - Find imports, show xrefs for each, disassemble usage
5. **HTTP URL strings** - Find strings, show xrefs, disassemble around usage
6. **Import with shell eval** - Replace `ii | grep` + `axtj` with pure Python

## Pattern 1: CommonCrypto Hash Functions

**Demo**: MASTG-DEMO-0015 (cchash.r2)

**Old .r2 script**:
```r2cmd
?e Uses of CommonCrypto hash function:
afl~CC_

?e
?e xrefs to CC_MD5:
axt @ 0x1000071a8

?e xrefs to CC_SHA1:
axt @ 0x1000071b4

?e
?e Use of MD5:
pd-- 5 @ 0x1000048c4

?e
?e Use of SHA1:
pd-- 5 @ 0x10000456c
```

**New Python script**:
```python
#!/usr/bin/env python3
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper

def main():
    binary_path = Path(__file__).parent / "MASTestApp"
    
    with R2Session(str(binary_path), analyze=True) as session:
        func_finder = FunctionFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        
        # List all CC_ functions
        print()
        print("Uses of CommonCrypto hash function:")
        cc_funcs = func_finder.find_by_contains("CC_")
        for func in cc_funcs:
            print(f"{func.address:#x} {func.name}")
        
        # Find specific functions
        md5_func = func_finder.find_by_exact("CC_MD5")
        sha1_func = func_finder.find_by_exact("CC_SHA1")
        
        # Show xrefs to CC_MD5
        if md5_func:
            print()
            print("xrefs to CC_MD5:")
            md5_xrefs = xref_analyzer.get_xrefs_to(md5_func.address)
            for xref in md5_xrefs:
                print(f"{xref.xref_type} {xref.from_addr:#x}")
        
        # Show xrefs to CC_SHA1
        if sha1_func:
            print()
            print("xrefs to CC_SHA1:")
            sha1_xrefs = xref_analyzer.get_xrefs_to(sha1_func.address)
            for xref in sha1_xrefs:
                print(f"{xref.xref_type} {xref.from_addr:#x}")
        
        # Disassemble around MD5 usage
        if md5_func:
            print()
            print("Use of MD5:")
            md5_xrefs = xref_analyzer.get_xrefs_to(md5_func.address)
            for xref in md5_xrefs:
                print()
                code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
                print(code)
        
        # Disassemble around SHA1 usage
        if sha1_func:
            print()
            print("Use of SHA1:")
            sha1_xrefs = xref_analyzer.get_xrefs_to(sha1_func.address)
            for xref in sha1_xrefs:
                print()
                code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
                print(code)

if __name__ == "__main__":
    sys.exit(main())
```

**Config-based approach**:
```json
{
  "category": "CRYPTO",
  "targets": [
    {"type": "function", "name": "CC_", "match_mode": "contains", "description": "CC funcs"},
    {"type": "function", "name": "CC_MD5", "match_mode": "exact", "description": "CC_MD5"},
    {"type": "function", "name": "CC_SHA1", "match_mode": "exact", "description": "CC_SHA1"}
  ],
  "actions": [
    {"type": "list_matches", "target": "CC funcs", "output": "Uses of CommonCrypto hash function"},
    {"type": "show_xrefs", "target": "CC_MD5", "output": "xrefs to CC_MD5"},
    {"type": "show_xrefs", "target": "CC_SHA1", "output": "xrefs to CC_SHA1"},
    {"type": "disasm_around_xrefs", "target": "CC_MD5", "before": 5, "after": 0, "output": "Use of MD5"},
    {"type": "disasm_around_xrefs", "target": "CC_SHA1", "before": 5, "after": 0, "output": "Use of SHA1"}
  ]
}
```

## Pattern 2: CryptoKit Private Key

**Demo**: MASTG-DEMO-0014 (cryptokit_hardcoded_ecdsa.r2)

**Old .r2 script**:
```r2cmd
e asm.bytes=false
e scr.color=false
e asm.var=false

?e Uses of CryptoKit.P256.Signing.PrivateKey:
afl~CryptoKit.P256.Signing.PrivateKey

?e
?e xrefs to CryptoKit.P256.Signing.PrivateKey.rawRepresentation:
axt @ 0x100007388

?e
?e Use of CryptoKit.P256.Signing.PrivateKey.rawRepresentation:
pd-- 9 @ 0x1000048d4

pdf @ sym.func.1000046dc > function.asm

px 32 @ 0x1000100c8 > key.asm
```

**New Python script**:
```python
#!/usr/bin/env python3
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper

def main():
    binary_path = Path(__file__).parent / "MASTestApp"
    output_dir = Path(__file__).parent
    
    with R2Session(str(binary_path), analyze=True) as session:
        func_finder = FunctionFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        
        # Find CryptoKit functions
        print("Uses of CryptoKit.P256.Signing.PrivateKey:")
        pk_funcs = func_finder.find_by_contains("CryptoKit.P256.Signing.PrivateKey")
        for func in pk_funcs:
            print(f"{func.address:#x} {func.name}")
        
        # Find rawRepresentation method
        raw_rep = func_finder.find_by_contains("CryptoKit.P256.Signing.PrivateKey.rawRepresentation")
        
        if raw_rep and len(raw_rep) > 0:
            target = raw_rep[0]
            
            print()
            print("xrefs to CryptoKit.P256.Signing.PrivateKey.rawRepresentation:")
            xrefs = xref_analyzer.get_xrefs_to(target.address)
            for xref in xrefs:
                print(f"{xref.xref_type} {xref.from_addr:#x}")
            
            print()
            print("Use of CryptoKit.P256.Signing.PrivateKey.rawRepresentation:")
            print()
            for xref in xrefs:
                code = disasm.disasm_at(xref.from_addr, lines=9, backward=True)
                print(code)
                
                # Get function containing the xref and dump it
                func_start = disasm.seek_to_function_start(xref.from_addr)
                if func_start:
                    func_disasm = disasm.disasm_function(func_start)
                    with open(output_dir / "function.asm", "w") as f:
                        f.write(func_disasm)
            
            # Note: For dumping bytes at a specific address like key data,
            # you'd need to identify the address dynamically or from the
            # disassembly context. This is demo-specific.
            # Example: bytes_dump = disasm.dump_bytes(key_addr, 32)

if __name__ == "__main__":
    sys.exit(main())
```

## Pattern 3: Network Port Literals

**Demo**: MASTG-DEMO-0085 (low_level_network.r2)

**Old .r2 script**:
```r2cmd
e asm.bytes=false
e scr.color=false
e asm.var=false

?e Uses of the Network.NWEndpoint.Port.integerLiteral function:
f~Network.NWEndpoint.Port.integerLiteral

?e
?e xrefs to Network.NWEndpoint.Port.integerLiteral:
axt @ 0x100006c00

?e
?e Use of Network.NWEndpoint.Port.integerLiteral:
pd-- 5 @ 0x1000047f4

?e
?e Value passed to Network.NWEndpoint.Port.integerLiteral:
? 0x50~uint32[1]
```

**New Python script**:
```python
#!/usr/bin/env python3
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper

def main():
    binary_path = Path(__file__).parent / "MASTestApp"
    
    with R2Session(str(binary_path), analyze=True) as session:
        func_finder = FunctionFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        
        # Find the port literal function
        print("Uses of the Network.NWEndpoint.Port.integerLiteral function:")
        port_funcs = func_finder.find_by_contains("Network.NWEndpoint.Port.integerLiteral")
        for func in port_funcs:
            print(f"{func.address:#x} {func.name}")
        
        if port_funcs:
            target = port_funcs[0]
            
            print()
            print("xrefs to Network.NWEndpoint.Port.integerLiteral:")
            xrefs = xref_analyzer.get_xrefs_to(target.address)
            for xref in xrefs:
                print(f"{xref.xref_type} {xref.from_addr:#x}")
            
            print()
            print("Use of Network.NWEndpoint.Port.integerLiteral:")
            print()
            for xref in xrefs:
                code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
                print(code)
            
            # Evaluate literal value
            print()
            print("Value passed to Network.NWEndpoint.Port.integerLiteral:")
            print()
            value = disasm.evaluate_expression("0x50")
            if value:
                print(f"uint32: {value}")

if __name__ == "__main__":
    sys.exit(main())
```

## Pattern 4: BSD Sockets

**Demo**: MASTG-DEMO-0086 (bsd_sockets.r2)

**Old .r2 script**:
```r2cmd
e asm.bytes=false
e scr.color=false
e asm.var=false

?e Uses of the BSD sockets functions:
ii~getaddrinfo,send,recv,connect,socket

?e
?e xrefs to getaddrinfo:
axt @ 0x1000069c4

?e
?e Use of getaddrinfo:
pd-- 20 @ 0x1000041d0

?e
?e Value passed to getaddrinfo:
? 0x50~uint32[1]
```

**New Python script**:
```python
#!/usr/bin/env python3
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, ImportFinder, XRefAnalyzer, DisasmHelper

def main():
    binary_path = Path(__file__).parent / "MASTestApp"
    
    with R2Session(str(binary_path), analyze=True) as session:
        import_finder = ImportFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        
        # Find BSD socket imports
        socket_funcs = ["getaddrinfo", "send", "recv", "connect", "socket"]
        
        print("Uses of the BSD sockets functions:")
        found_imports = []
        for func_name in socket_funcs:
            matches = import_finder.find_by_contains(func_name)
            for match in matches:
                print(f"{match.address:#x} {match.name}")
                found_imports.append((func_name, match))
        
        # For each import, show xrefs and usage
        for func_name, match in found_imports:
            print()
            print(f"xrefs to {func_name}:")
            xrefs = xref_analyzer.get_xrefs_to(match.address)
            for xref in xrefs:
                print(f"{xref.xref_type} {xref.from_addr:#x}")
            
            if xrefs:
                print()
                print(f"Use of {func_name}:")
                print()
                for xref in xrefs:
                    code = disasm.disasm_at(xref.from_addr, lines=20, backward=True)
                    print(code)
                    
                    # Try to extract argument values if needed
                    # This is context-specific and may require parsing disassembly

if __name__ == "__main__":
    sys.exit(main())
```

## Pattern 5: HTTP URL Strings

**Demo**: MASTG-DEMO-0084 (http_urls.r2)

**Old .r2 script**:
```r2cmd
e asm.bytes=false
e scr.color=false
e asm.var=false

?e Uses of http:// URLs:
iz~http://

?e
?e xrefs to http://httpbin.org/get:
axt @ 0x100006c60

?e
?e Use of http://httpbin.org/get:
pd 15 @ 0x100005130
?e ...
pd-- 5 @ 0x100005238
```

**New Python script**:
```python
#!/usr/bin/env python3
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, StringFinder, XRefAnalyzer, DisasmHelper

def main():
    binary_path = Path(__file__).parent / "MASTestApp"
    
    with R2Session(str(binary_path), analyze=True) as session:
        string_finder = StringFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        
        # Find HTTP URLs
        print("Uses of http:// URLs:")
        print()
        http_strings = string_finder.find_by_contains("http://")
        for string in http_strings:
            print(f"{string.address:#x} {string.name}")
        
        # For each URL, show xrefs and usage
        for string in http_strings:
            print()
            print(f"xrefs to {string.name}:")
            xrefs = xref_analyzer.get_xrefs_to(string.address)
            for xref in xrefs:
                print(f"{xref.xref_type} {xref.from_addr:#x}")
            
            if xrefs:
                print()
                print(f"Use of {string.name}:")
                for xref in xrefs:
                    print()
                    # Show forward disassembly
                    code = disasm.disasm_at(xref.from_addr, lines=15)
                    print(code)
                    print("...")
                    # Show backward disassembly from another point if needed
                    # code2 = disasm.disasm_at(other_addr, lines=5, backward=True)
                    # print(code2)

if __name__ == "__main__":
    sys.exit(main())
```

## Pattern 6: Import Resolution (No Shell Eval)

**Old approach using shell evaluation**:
```bash
# In .r2 script:
ii~getaddrinfo    # Filter imports
axt @ `ii~getaddrinfo[2]`  # Extract address and use in axt
```

**New Python approach**:
```python
# Pure Python, no shell evaluation
import_finder = ImportFinder(session)
xref_analyzer = XRefAnalyzer(session)

# Find the import
matches = import_finder.find_by_contains("getaddrinfo")

# For each match, get xrefs directly
for match in matches:
    xrefs = xref_analyzer.get_xrefs_to(match.address)
    for xref in xrefs:
        print(f"{xref.xref_type} from {xref.from_addr:#x}")
```

## File Structure for Migrated Demo

```
demos/ios/MASVS-CRYPTO/MASTG-DEMO-0015/
├── MASTG-DEMO-0015.md      # Demo documentation
├── MASTestApp              # Binary
├── MastgTest.swift         # Source code
├── cchash.r2               # OLD: Keep for reference
├── run.sh                  # OLD: Keep for reference
├── output.txt              # OLD: Original output
├── config.json             # NEW: Configuration
├── run_r2ooky.py          # NEW: Python runner
└── output_r2ooky.txt      # NEW: New output
```

## Validation Checklist

When migrating a demo:

- [ ] Python script runs without errors
- [ ] Output matches original format
- [ ] All addresses are dynamically resolved (no hardcoded addresses)
- [ ] Handles missing symbols gracefully
- [ ] Works on the actual binary
- [ ] Config file is valid JSON
- [ ] Script has proper imports and path setup
- [ ] Output files are generated correctly
- [ ] run.sh is updated or run_r2ooky.py is used

## Next Steps

1. Migrate each demo following these patterns
2. Test each migration with actual binaries
3. Update demo markdown files to reference new Python scripts
4. Keep old .r2 scripts for reference
5. Update instruction files to document r2ooky usage
