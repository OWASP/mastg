#!/usr/bin/env python3
"""
MASTG-DEMO-0014: Detect uses of CryptoKit ECDSA private key raw representation.

This demo shows how to find CryptoKit private key usage and dump function disassembly.
"""

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper
from r2ooky.formatter import OutputBuffer


def main():
    """Run the demo."""
    binary_path = Path(__file__).parent / "MASTestApp"
    output_dir = Path(__file__).parent
    
    if not binary_path.exists():
        print(f"Error: Binary not found at {binary_path}")
        return 1
    
    with R2Session(str(binary_path), analyze=True) as session:
        func_finder = FunctionFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        output = OutputBuffer()
        
        # Find CryptoKit PrivateKey functions
        pk_funcs = func_finder.find_by_contains("CryptoKit.P256.Signing.PrivateKey")
        output.add_match_list(pk_funcs, "Uses of CryptoKit.P256.Signing.PrivateKey")
        
        # Find rawRepresentation method
        raw_rep = func_finder.find_by_contains("CryptoKit.P256.Signing.PrivateKey.rawRepresentation")
        
        if raw_rep:
            target = raw_rep[0] if isinstance(raw_rep, list) else raw_rep
            
            # Get xrefs
            xrefs = xref_analyzer.get_xrefs_to(target.address)
            output.add_xref_list(xrefs, "xrefs to CryptoKit.P256.Signing.PrivateKey.rawRepresentation")
            
            # Disassemble around usage
            for xref in xrefs:
                code = disasm.disasm_at(xref.from_addr, lines=9, backward=True)
                output.add_disasm(code, "Use of CryptoKit.P256.Signing.PrivateKey.rawRepresentation")
                
                # Get function containing the xref and dump to file
                func_start = disasm.seek_to_function_start(xref.from_addr)
                if func_start:
                    func_disasm = disasm.disasm_function(func_start)
                    with open(output_dir / "function.asm", "w") as f:
                        f.write(func_disasm)
        
        # Output
        output.print()
        output.write_to_file(output_dir / "output_r2ooky.txt")
    
    return 0


if __name__ == "__main__":
    sys.exit(main())
