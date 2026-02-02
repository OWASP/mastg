#!/usr/bin/env python3
"""
MASTG-DEMO-0015: Detect uses of CommonCrypto hash functions.

This demo uses r2ooky to dynamically find and analyze CommonCrypto hash function
usage without hardcoded addresses.
"""

import sys
from pathlib import Path

# Add r2ooky to path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper, load_config


def main():
    """Run the demo."""
    # Load config
    config_path = Path(__file__).parent / "config.json"
    config = load_config(str(config_path))
    
    # Binary path
    binary_path = Path(__file__).parent / "MASTestApp"
    
    if not binary_path.exists():
        print(f"Error: Binary not found at {binary_path}")
        return 1
    
    # Open radare2 session
    with R2Session(str(binary_path), analyze=True) as session:
        func_finder = FunctionFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        
        # Find all CommonCrypto functions
        print()
        print("Uses of CommonCrypto hash function:")
        cc_funcs = func_finder.find_by_contains("CC_")
        for func in cc_funcs:
            print(f"{func.address:#x} {func.name}")
        
        # Find specific hash functions
        md5_match = func_finder.find_by_exact("CC_MD5")
        sha1_match = func_finder.find_by_exact("CC_SHA1")
        
        if not md5_match:
            print("\nWarning: CC_MD5 not found")
        if not sha1_match:
            print("\nWarning: CC_SHA1 not found")
        
        # Show xrefs to CC_MD5
        if md5_match:
            print()
            print("xrefs to CC_MD5:")
            md5_xrefs = xref_analyzer.get_xrefs_to(md5_match.address)
            for xref in md5_xrefs:
                func_info = f" (in {xref.from_func})" if xref.from_func else ""
                print(f"{xref.xref_type} {xref.from_addr:#x}{func_info}")
        
        # Show xrefs to CC_SHA1
        if sha1_match:
            print()
            print("xrefs to CC_SHA1:")
            sha1_xrefs = xref_analyzer.get_xrefs_to(sha1_match.address)
            for xref in sha1_xrefs:
                func_info = f" (in {xref.from_func})" if xref.from_func else ""
                print(f"{xref.xref_type} {xref.from_addr:#x}{func_info}")
        
        # Show disassembly around MD5 usage
        if md5_match:
            print()
            print("Use of MD5:")
            md5_xrefs = xref_analyzer.get_xrefs_to(md5_match.address)
            for xref in md5_xrefs:
                print()
                disasm_text = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
                print(disasm_text)
        
        # Show disassembly around SHA1 usage
        if sha1_match:
            print()
            print("Use of SHA1:")
            sha1_xrefs = xref_analyzer.get_xrefs_to(sha1_match.address)
            for xref in sha1_xrefs:
                print()
                disasm_text = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
                print(disasm_text)
    
    return 0


if __name__ == "__main__":
    sys.exit(main())
