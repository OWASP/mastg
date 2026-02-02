"""
Finders for functions, imports, and strings with pattern matching.
"""

from typing import List, Dict, Any, Optional
import re
from .core import R2Session


class MatchResult:
    """Represents a match result with metadata."""
    
    def __init__(self, name: str, address: int, match_type: str = "exact", metadata: Optional[Dict] = None):
        """
        Initialize match result.
        
        Args:
            name: Name of the matched symbol/string
            address: Address of the match
            match_type: How it was matched ("exact", "contains", "regex")
            metadata: Additional metadata about the match
        """
        self.name = name
        self.address = address
        self.match_type = match_type
        self.metadata = metadata or {}
    
    def __repr__(self):
        return f"MatchResult(name='{self.name}', addr=0x{self.address:x}, type={self.match_type})"
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary."""
        return {
            "name": self.name,
            "address": f"0x{self.address:x}",
            "match_type": self.match_type,
            "metadata": self.metadata
        }


class FunctionFinder:
    """
    Find functions by name patterns.
    
    Supports exact match, substring contains, and regex matching.
    """
    
    def __init__(self, session: R2Session):
        """
        Initialize function finder.
        
        Args:
            session: Active R2Session
        """
        self.session = session
    
    def find_all(self) -> List[Dict[str, Any]]:
        """
        Get all functions.
        
        Returns:
            List of function dictionaries from radare2
        """
        return self.session.cmdj("aflj") or []
    
    def find_by_exact(self, name: str) -> Optional[MatchResult]:
        """
        Find function by exact name match.
        
        Args:
            name: Exact function name to find
            
        Returns:
            MatchResult if found, None otherwise
        """
        functions = self.find_all()
        for func in functions:
            if func.get("name") == name:
                return MatchResult(
                    name=func["name"],
                    address=func["offset"],
                    match_type="exact",
                    metadata={"size": func.get("size", 0)}
                )
        return None
    
    def find_by_contains(self, pattern: str) -> List[MatchResult]:
        """
        Find functions whose names contain the pattern.
        
        Args:
            pattern: Substring to search for in function names
            
        Returns:
            List of MatchResult objects
        """
        functions = self.find_all()
        results = []
        for func in functions:
            name = func.get("name", "")
            if pattern in name:
                results.append(MatchResult(
                    name=name,
                    address=func["offset"],
                    match_type="contains",
                    metadata={"size": func.get("size", 0)}
                ))
        return results
    
    def find_by_regex(self, pattern: str) -> List[MatchResult]:
        """
        Find functions by regex pattern.
        
        Args:
            pattern: Regular expression to match function names
            
        Returns:
            List of MatchResult objects
        """
        functions = self.find_all()
        regex = re.compile(pattern)
        results = []
        for func in functions:
            name = func.get("name", "")
            if regex.search(name):
                results.append(MatchResult(
                    name=name,
                    address=func["offset"],
                    match_type="regex",
                    metadata={"size": func.get("size", 0)}
                ))
        return results
    
    def find(self, pattern: str, mode: str = "contains") -> List[MatchResult]:
        """
        Find functions using specified matching mode.
        
        Args:
            pattern: Pattern to search for
            mode: Matching mode ("exact", "contains", "regex")
            
        Returns:
            List of MatchResult objects
        """
        if mode == "exact":
            result = self.find_by_exact(pattern)
            return [result] if result else []
        elif mode == "contains":
            return self.find_by_contains(pattern)
        elif mode == "regex":
            return self.find_by_regex(pattern)
        else:
            raise ValueError(f"Unknown mode: {mode}")


class ImportFinder:
    """
    Find imports by name patterns.
    """
    
    def __init__(self, session: R2Session):
        """
        Initialize import finder.
        
        Args:
            session: Active R2Session
        """
        self.session = session
    
    def find_all(self) -> List[Dict[str, Any]]:
        """
        Get all imports.
        
        Returns:
            List of import dictionaries from radare2
        """
        return self.session.cmdj("iij") or []
    
    def find_by_exact(self, name: str) -> Optional[MatchResult]:
        """
        Find import by exact name match.
        
        Args:
            name: Exact import name to find
            
        Returns:
            MatchResult if found, None otherwise
        """
        imports = self.find_all()
        for imp in imports:
            if imp.get("name") == name:
                return MatchResult(
                    name=imp["name"],
                    address=imp.get("plt", 0),
                    match_type="exact",
                    metadata={"type": imp.get("type", "")}
                )
        return None
    
    def find_by_contains(self, pattern: str) -> List[MatchResult]:
        """
        Find imports whose names contain the pattern.
        
        Args:
            pattern: Substring to search for in import names
            
        Returns:
            List of MatchResult objects
        """
        imports = self.find_all()
        results = []
        for imp in imports:
            name = imp.get("name", "")
            if pattern in name:
                results.append(MatchResult(
                    name=name,
                    address=imp.get("plt", 0),
                    match_type="contains",
                    metadata={"type": imp.get("type", "")}
                ))
        return results
    
    def find_by_regex(self, pattern: str) -> List[MatchResult]:
        """
        Find imports by regex pattern.
        
        Args:
            pattern: Regular expression to match import names
            
        Returns:
            List of MatchResult objects
        """
        imports = self.find_all()
        regex = re.compile(pattern)
        results = []
        for imp in imports:
            name = imp.get("name", "")
            if regex.search(name):
                results.append(MatchResult(
                    name=name,
                    address=imp.get("plt", 0),
                    match_type="regex",
                    metadata={"type": imp.get("type", "")}
                ))
        return results
    
    def find(self, pattern: str, mode: str = "contains") -> List[MatchResult]:
        """
        Find imports using specified matching mode.
        
        Args:
            pattern: Pattern to search for
            mode: Matching mode ("exact", "contains", "regex")
            
        Returns:
            List of MatchResult objects
        """
        if mode == "exact":
            result = self.find_by_exact(pattern)
            return [result] if result else []
        elif mode == "contains":
            return self.find_by_contains(pattern)
        elif mode == "regex":
            return self.find_by_regex(pattern)
        else:
            raise ValueError(f"Unknown mode: {mode}")


class StringFinder:
    """
    Find strings by content patterns.
    """
    
    def __init__(self, session: R2Session):
        """
        Initialize string finder.
        
        Args:
            session: Active R2Session
        """
        self.session = session
    
    def find_all(self) -> List[Dict[str, Any]]:
        """
        Get all strings.
        
        Returns:
            List of string dictionaries from radare2
        """
        return self.session.cmdj("izj") or []
    
    def find_by_exact(self, content: str) -> Optional[MatchResult]:
        """
        Find string by exact content match.
        
        Args:
            content: Exact string content to find
            
        Returns:
            MatchResult if found, None otherwise
        """
        strings = self.find_all()
        for s in strings:
            if s.get("string") == content:
                return MatchResult(
                    name=s["string"],
                    address=s["vaddr"],
                    match_type="exact",
                    metadata={"length": s.get("length", 0), "type": s.get("type", "")}
                )
        return None
    
    def find_by_contains(self, pattern: str) -> List[MatchResult]:
        """
        Find strings that contain the pattern.
        
        Args:
            pattern: Substring to search for in strings
            
        Returns:
            List of MatchResult objects
        """
        strings = self.find_all()
        results = []
        for s in strings:
            content = s.get("string", "")
            if pattern in content:
                results.append(MatchResult(
                    name=content,
                    address=s["vaddr"],
                    match_type="contains",
                    metadata={"length": s.get("length", 0), "type": s.get("type", "")}
                ))
        return results
    
    def find_by_regex(self, pattern: str) -> List[MatchResult]:
        """
        Find strings by regex pattern.
        
        Args:
            pattern: Regular expression to match string content
            
        Returns:
            List of MatchResult objects
        """
        strings = self.find_all()
        regex = re.compile(pattern)
        results = []
        for s in strings:
            content = s.get("string", "")
            if regex.search(content):
                results.append(MatchResult(
                    name=content,
                    address=s["vaddr"],
                    match_type="regex",
                    metadata={"length": s.get("length", 0), "type": s.get("type", "")}
                ))
        return results
    
    def find(self, pattern: str, mode: str = "contains") -> List[MatchResult]:
        """
        Find strings using specified matching mode.
        
        Args:
            pattern: Pattern to search for
            mode: Matching mode ("exact", "contains", "regex")
            
        Returns:
            List of MatchResult objects
        """
        if mode == "exact":
            result = self.find_by_exact(pattern)
            return [result] if result else []
        elif mode == "contains":
            return self.find_by_contains(pattern)
        elif mode == "regex":
            return self.find_by_regex(pattern)
        else:
            raise ValueError(f"Unknown mode: {mode}")
