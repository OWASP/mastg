var target = {
  category: "CRYPTO",
  hooks: [
    // Generate RSA key pair: SecKeyCreateRandomKey(attributes CFDictionary, &error) -> SecKey (private)
    {
      native: true,
      symbol: "SecKeyCreateRandomKey",
      maxFrames: -1,
      // filterEventsByStacktrace: "MASTG-DEMO-0011",
      args: [
        { name: "keyAttributes", type: "CFDictionary" },
        { name: "error", type: "pointer" },
        { name: "privateKey", type: "pointer", returnValue: true, direction: "out" }
      ]
    },
    // Capture signature creation: SecKeyCreateSignature(privateKey, algorithm, dataToSign CFData, &error) -> CFData signature
    {
      native: true,
      symbol: "SecKeyCreateSignature",
      maxFrames: -1,
      // filterEventsByStacktrace: "MASTG-DEMO-0011",
      args: [
        { name: "privateKey", type: "pointer" },
        { name: "algorithm", type: "pointer" },
        { name: "dataToSign", type: "CFData" },
        { name: "error", type: "pointer" },
        { name: "signature", type: "CFData", returnValue: true, direction: "out" }
      ]
    },

    // Capture verification: SecKeyVerifySignature(publicKey, algorithm, dataToSign CFData, signature CFData, &error) -> bool
    {
      native: true,
      symbol: "SecKeyVerifySignature",
      maxFrames: -1,
      args: [
        { name: "publicKey", type: "pointer" },
        { name: "algorithm", type: "pointer" },
        { name: "dataToSign", type: "CFData" },
        { name: "signature", type: "CFData" },
        { name: "error", type: "pointer" }
      ]
    },

    // Capture external representation (DER) of keys
    {
      native: true,
      symbol: "SecKeyCopyExternalRepresentation",
      maxFrames: -1,
      args: [
        { name: "key", type: "pointer" },
        { name: "error", type: "pointer" },
        { name: "der", type: "CFData", returnValue: true, direction: "out" }
      ]
    }
  ]
};
