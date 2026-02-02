"""
Configuration file handling for r2ooky demos.
"""

import json
from typing import Dict, Any, List, Optional
from pathlib import Path


class DemoConfig:
    """
    Configuration for a demo run.
    
    Defines targets (functions, imports, strings) and actions to perform.
    """
    
    def __init__(self, config_dict: Dict[str, Any]):
        """
        Initialize from config dictionary.
        
        Args:
            config_dict: Configuration dictionary
        """
        self.config = config_dict
        self.category = config_dict.get("category", "UNKNOWN")
        self.description = config_dict.get("description", "")
        self.targets = config_dict.get("targets", [])
        self.actions = config_dict.get("actions", [])
    
    def get_targets_by_type(self, target_type: str) -> List[Dict[str, Any]]:
        """
        Get all targets of a specific type.
        
        Args:
            target_type: Type of target ("function", "import", "string")
            
        Returns:
            List of target configurations
        """
        return [t for t in self.targets if t.get("type") == target_type]
    
    def get_actions_by_type(self, action_type: str) -> List[Dict[str, Any]]:
        """
        Get all actions of a specific type.
        
        Args:
            action_type: Type of action ("list", "xrefs", "disasm", etc.)
            
        Returns:
            List of action configurations
        """
        return [a for a in self.actions if a.get("type") == action_type]
    
    def to_dict(self) -> Dict[str, Any]:
        """Get configuration as dictionary."""
        return self.config


def load_config(config_path: str) -> DemoConfig:
    """
    Load demo configuration from JSON file.
    
    Args:
        config_path: Path to JSON config file
        
    Returns:
        DemoConfig object
        
    Raises:
        FileNotFoundError: If config file doesn't exist
        json.JSONDecodeError: If config is invalid JSON
    """
    path = Path(config_path)
    if not path.exists():
        raise FileNotFoundError(f"Config file not found: {config_path}")
    
    with open(path, 'r') as f:
        config_dict = json.load(f)
    
    return DemoConfig(config_dict)


def create_sample_config() -> Dict[str, Any]:
    """
    Create a sample configuration for reference.
    
    Returns:
        Sample config dictionary
    """
    return {
        "category": "CRYPTO",
        "description": "Detect uses of CommonCrypto hash functions",
        "targets": [
            {
                "type": "function",
                "name": "CC_MD5",
                "match_mode": "exact",
                "description": "MD5 hash function"
            },
            {
                "type": "function", 
                "name": "CC_SHA1",
                "match_mode": "exact",
                "description": "SHA1 hash function"
            },
            {
                "type": "function",
                "name": "CC_",
                "match_mode": "contains",
                "description": "All CommonCrypto functions"
            }
        ],
        "actions": [
            {
                "type": "list_matches",
                "target": "All CommonCrypto functions",
                "output": "Functions matching CC_"
            },
            {
                "type": "show_xrefs",
                "target": "CC_MD5",
                "output": "xrefs to CC_MD5"
            },
            {
                "type": "show_xrefs",
                "target": "CC_SHA1", 
                "output": "xrefs to CC_SHA1"
            },
            {
                "type": "disasm_around_xrefs",
                "target": "CC_MD5",
                "before": 5,
                "after": 0,
                "output": "Use of MD5"
            },
            {
                "type": "disasm_around_xrefs",
                "target": "CC_SHA1",
                "before": 5,
                "after": 0,
                "output": "Use of SHA1"
            }
        ]
    }
