"""
Cross-reference analysis utilities.
"""

from typing import List, Dict, Any, Optional
from .core import R2Session
from .finder import MatchResult


class XRefResult:
    """Represents a cross-reference with metadata."""
    
    def __init__(self, from_addr: int, to_addr: int, xref_type: str, 
                 from_func: Optional[str] = None, metadata: Optional[Dict] = None):
        """
        Initialize XRef result.
        
        Args:
            from_addr: Address where the reference originates
            to_addr: Address being referenced
            xref_type: Type of reference (CALL, DATA, etc.)
            from_func: Name of function containing the reference
            metadata: Additional metadata
        """
        self.from_addr = from_addr
        self.to_addr = to_addr
        self.xref_type = xref_type
        self.from_func = from_func
        self.metadata = metadata or {}
    
    def __repr__(self):
        func_info = f", func={self.from_func}" if self.from_func else ""
        return f"XRefResult(from=0x{self.from_addr:x}, to=0x{self.to_addr:x}, type={self.xref_type}{func_info})"
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary."""
        return {
            "from": f"0x{self.from_addr:x}",
            "to": f"0x{self.to_addr:x}",
            "type": self.xref_type,
            "from_func": self.from_func,
            "metadata": self.metadata
        }


class XRefAnalyzer:
    """
    Analyze cross-references to symbols and addresses.
    """
    
    def __init__(self, session: R2Session):
        """
        Initialize XRef analyzer.
        
        Args:
            session: Active R2Session
        """
        self.session = session
    
    def get_xrefs_to(self, address: int) -> List[XRefResult]:
        """
        Get cross-references to an address.
        
        Args:
            address: Target address
            
        Returns:
            List of XRefResult objects
        """
        # Use axtj for JSON output
        xrefs = self.session.cmdj(f"axtj @ {address}") or []
        
        results = []
        for xref in xrefs:
            from_addr = xref.get("from", 0)
            xref_type = xref.get("type", "UNKNOWN")
            
            # Get function containing the reference
            from_func = self._get_function_at(from_addr)
            
            results.append(XRefResult(
                from_addr=from_addr,
                to_addr=address,
                xref_type=xref_type,
                from_func=from_func,
                metadata=xref
            ))
        
        return results
    
    def get_xrefs_from(self, address: int) -> List[XRefResult]:
        """
        Get cross-references from an address.
        
        Args:
            address: Source address
            
        Returns:
            List of XRefResult objects
        """
        # Use axfj for JSON output
        xrefs = self.session.cmdj(f"axfj @ {address}") or []
        
        results = []
        for xref in xrefs:
            to_addr = xref.get("to", 0)
            xref_type = xref.get("type", "UNKNOWN")
            
            results.append(XRefResult(
                from_addr=address,
                to_addr=to_addr,
                xref_type=xref_type,
                metadata=xref
            ))
        
        return results
    
    def get_call_sites(self, target_address: int) -> List[XRefResult]:
        """
        Get call sites for a function (only CALL type xrefs).
        
        Args:
            target_address: Target function address
            
        Returns:
            List of XRefResult objects for calls only
        """
        all_xrefs = self.get_xrefs_to(target_address)
        return [xref for xref in all_xrefs if xref.xref_type.upper() in ["CALL", "CODE"]]
    
    def _get_function_at(self, address: int) -> Optional[str]:
        """
        Get function name at address.
        
        Args:
            address: Address to check
            
        Returns:
            Function name if found, None otherwise
        """
        # Use afij to get function info at address
        func_info = self.session.cmdj(f"afij @ {address}")
        if func_info and len(func_info) > 0:
            return func_info[0].get("name")
        return None
    
    def resolve_and_get_xrefs(self, match: MatchResult) -> List[XRefResult]:
        """
        Get xrefs for a match result (convenience method).
        
        Args:
            match: MatchResult from a finder
            
        Returns:
            List of XRefResult objects
        """
        return self.get_xrefs_to(match.address)
