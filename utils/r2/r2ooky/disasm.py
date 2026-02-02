"""
Disassembly and code display utilities.
"""

from typing import List, Dict, Any, Optional
from .core import R2Session
from .xref import XRefResult


class DisasmHelper:
    """
    Helper for disassembly operations.
    """
    
    def __init__(self, session: R2Session):
        """
        Initialize disassembly helper.
        
        Args:
            session: Active R2Session
        """
        self.session = session
    
    def disasm_at(self, address: int, lines: int = 10, backward: bool = False) -> str:
        """
        Disassemble at an address.
        
        Args:
            address: Address to disassemble
            lines: Number of lines to disassemble
            backward: If True, disassemble backward (before address)
            
        Returns:
            Disassembly as string
        """
        if backward:
            return self.session.cmd(f"pd-- {lines} @ {address}")
        else:
            return self.session.cmd(f"pd {lines} @ {address}")
    
    def disasm_function(self, address: int) -> str:
        """
        Disassemble entire function at address.
        
        Args:
            address: Address within the function
            
        Returns:
            Full function disassembly
        """
        return self.session.cmd(f"pdf @ {address}")
    
    def disasm_function_json(self, address: int) -> Optional[Dict[str, Any]]:
        """
        Get function disassembly as JSON.
        
        Args:
            address: Address within the function
            
        Returns:
            Function info and disassembly as dictionary
        """
        return self.session.cmdj(f"pdfj @ {address}")
    
    def disasm_around_xref(self, xref: XRefResult, before: int = 5, after: int = 5) -> str:
        """
        Disassemble around a cross-reference location.
        
        Args:
            xref: XRefResult to disassemble around
            before: Lines to show before
            after: Lines to show after
            
        Returns:
            Disassembly as string
        """
        # First disassemble backward, then forward
        output = []
        if before > 0:
            backward = self.session.cmd(f"pd-- {before} @ {xref.from_addr}")
            output.append(backward)
        
        # Current location
        current = self.session.cmd(f"pd 1 @ {xref.from_addr}")
        output.append(current)
        
        if after > 0:
            # Calculate next address and show forward
            forward = self.session.cmd(f"pd {after} @ {xref.from_addr}+4")
            output.append(forward)
        
        return "\n".join(output)
    
    def dump_bytes(self, address: int, size: int) -> str:
        """
        Dump bytes at an address.
        
        Args:
            address: Address to dump from
            size: Number of bytes to dump
            
        Returns:
            Hex dump as string
        """
        return self.session.cmd(f"px {size} @ {address}")
    
    def dump_bytes_json(self, address: int, size: int) -> List[int]:
        """
        Get bytes at an address as JSON array.
        
        Args:
            address: Address to dump from
            size: Number of bytes to dump
            
        Returns:
            List of byte values
        """
        result = self.session.cmdj(f"pxj {size} @ {address}")
        return result if result else []
    
    def read_string(self, address: int, length: Optional[int] = None) -> str:
        """
        Read string at address.
        
        Args:
            address: Address to read from
            length: Maximum length (None for null-terminated)
            
        Returns:
            String content
        """
        if length:
            return self.session.cmd(f"ps {length} @ {address}").strip()
        else:
            return self.session.cmd(f"ps @ {address}").strip()
    
    def evaluate_expression(self, expression: str) -> Optional[int]:
        """
        Evaluate a radare2 expression.
        
        Args:
            expression: Expression to evaluate (e.g., "0x50")
            
        Returns:
            Evaluated value as integer
        """
        result = self.session.cmd(f"?v {expression}").strip()
        try:
            # Handle hex or decimal
            if result.startswith("0x"):
                return int(result, 16)
            else:
                return int(result)
        except ValueError:
            return None
    
    def seek_to_function_start(self, address: int) -> Optional[int]:
        """
        Find the start of the function containing the address.
        
        Args:
            address: Address within function
            
        Returns:
            Function start address or None
        """
        func_info = self.session.cmdj(f"afij @ {address}")
        if func_info and len(func_info) > 0:
            return func_info[0].get("offset")
        return None
