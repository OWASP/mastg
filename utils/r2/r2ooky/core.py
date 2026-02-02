"""
Core r2pipe session management.
"""

import r2pipe
from typing import Optional, Dict, Any, List
import json


class R2Session:
    """
    Manages a radare2 session with analysis and common settings.
    
    This class wraps r2pipe and ensures consistent configuration across
    all MASTG demos: analysis is run, colors are disabled, output is stable.
    """
    
    def __init__(self, binary_path: str, analyze: bool = True, arch: Optional[str] = None):
        """
        Initialize radare2 session.
        
        Args:
            binary_path: Path to the binary to analyze
            analyze: Whether to run analysis (default: True)
            arch: Architecture to use for fat binaries (e.g., "arm64")
        """
        self.binary_path = binary_path
        self.arch = arch
        
        # Open with r2pipe
        flags = ["-2"]  # Disable stderr output
        if arch:
            flags.extend(["-a", arch])
        
        self.r2 = r2pipe.open(binary_path, flags=flags)
        
        # Configure for stable, reproducible output
        self._configure()
        
        # Run analysis if requested
        if analyze:
            self._analyze()
    
    def _configure(self):
        """Set radare2 configuration for stable output."""
        self.r2.cmd("e scr.color=false")
        self.r2.cmd("e scr.interactive=false")
        self.r2.cmd("e asm.bytes=false")
        self.r2.cmd("e asm.var=false")
        self.r2.cmd("e scr.utf8=false")
    
    def _analyze(self):
        """Run radare2 analysis."""
        # Run standard analysis
        self.r2.cmd("aaa")
    
    def cmd(self, command: str) -> str:
        """
        Execute a radare2 command and return text output.
        
        Args:
            command: Radare2 command to execute
            
        Returns:
            Command output as string
        """
        return self.r2.cmd(command)
    
    def cmdj(self, command: str) -> Any:
        """
        Execute a radare2 command and parse JSON output.
        
        Args:
            command: Radare2 command to execute (should return JSON)
            
        Returns:
            Parsed JSON object (dict, list, etc.)
        """
        return self.r2.cmdj(command)
    
    def get_info(self) -> Dict[str, Any]:
        """
        Get binary information.
        
        Returns:
            Dictionary with binary info (arch, bits, format, etc.)
        """
        return self.cmdj("ij")
    
    def close(self):
        """Close the radare2 session."""
        if self.r2:
            self.r2.quit()
    
    def __enter__(self):
        """Context manager entry."""
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        """Context manager exit."""
        self.close()
