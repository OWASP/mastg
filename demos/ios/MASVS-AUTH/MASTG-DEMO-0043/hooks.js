var target = {
  category: "AUTH",
  hooks: [
    // SecAccessControlCreateWithFlags(allocator, protection, flags, &error) -> SecAccessControl
    {
      native: true,
      symbol: "SecAccessControlCreateWithFlags",
      maxFrames: -1,
      args: [
        { name: "allocator", type: "pointer" },
        { name: "protection", type: "pointer" }, // CFString (e.g., kSecAttrAccessibleWhenUnlocked)
        { name: "flags", type: "uint32" }, // SecAccessControlCreateFlags bitmask
        { name: "error", type: "pointer" },
        { name: "accessControl", type: "pointer", returnValue: true, direction: "out" }
      ]
    },

    // SecItemAdd(query CFDictionary, CFTypeRef* result) -> OSStatus
    {
      native: true,
      symbol: "SecItemAdd",
      maxFrames: -1,
      args: [
        { name: "query", type: "CFDictionary" },
        { name: "result", type: "pointer" }
      ]
    },

    // SecItemDelete(query CFDictionary) -> OSStatus
    {
      native: true,
      symbol: "SecItemDelete",
      maxFrames: -1,
      args: [
        { name: "query", type: "CFDictionary" }
      ]
    },

    // SecItemCopyMatching(query CFDictionary, CFTypeRef* result) -> OSStatus
    {
      native: true,
      symbol: "SecItemCopyMatching",
      maxFrames: -1,
      args: [
        { name: "query", type: "CFDictionary" },
        { name: "result", type: "pointer" }
      ]
    }
  ]
};
