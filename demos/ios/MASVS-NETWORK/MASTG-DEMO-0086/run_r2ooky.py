#!/usr/bin/env python3
"""
MASTG-DEMO-0086: Detect uses of BSD socket functions.

This demo shows how to find and analyze import usage.
"""

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, ImportFinder, XRefAnalyzer, DisasmHelper
from r2ooky.formatter import OutputBuffer


def main():
    """Run the demo."""
    binary_path = Path(__file__).parent / "MASTestApp"
    output_dir = Path(__file__).parent
    
    if not binary_path.exists():
        print(f"Error: Binary not found at {binary_path}")
        return 1
    
    socket_funcs = ["getaddrinfo", "send", "recv", "connect", "socket"]
    
    with R2Session(str(binary_path), analyze=True) as session:
        import_finder = ImportFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        output = OutputBuffer()
        
        # Find all socket imports
        found = []
        for name in socket_funcs:
            matches = import_finder.find_by_contains(name)
            found.extend([(name, m) for m in matches])
        
        # List imports
        lines = [f"0x{m.address:x} {m.name}" for _, m in found]
        output.add_section("Uses of the BSD sockets functions", "\n".join(lines))
        
        # Analyze each import
        for func_name, match in found:
            xrefs = xref_analyzer.get_xrefs_to(match.address)
            output.add_xref_list(xrefs, f"xrefs to {func_name}")
            
            if xrefs:
                for xref in xrefs:
                    code = disasm.disasm_at(xref.from_addr, lines=20, backward=True)
                    output.add_disasm(code, f"Use of {func_name}")
                    
                    # For getaddrinfo, try to extract the port value
                    if func_name == "getaddrinfo":
                        value = disasm.evaluate_expression("0x50")
                        if value:
                            output.add_section("Value passed to getaddrinfo", f"uint32: {value}")
        
        # Output
        output.print()
        output.write_to_file(output_dir / "output_r2ooky.txt")
    
    return 0


if __name__ == "__main__":
    sys.exit(main())
