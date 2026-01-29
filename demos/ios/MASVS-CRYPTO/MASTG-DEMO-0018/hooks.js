var target = {
  category: "MASVS-CRYPTO",
  hooks: [
    {
      native: true,
      symbol: "CCCrypt",
      maxFrames: -1,
      args: [
        { name: "op", type: "uint32", filter: [0] }, // kCCEncrypt
        { name: "alg", type: "uint32", filter: [2] }, // kCCAlgorithm3DES
        { name: "options", type: "uint32" },
        { name: "key", type: "bytes", lengthInArg: "keyLength" },
        { name: "keyLength", type: "uint32" },
        { name: "iv", type: "bytes", length: 8 },
        { name: "dataIn", type: "bytes", lengthInArg: "dataInLength" },
        { name: "dataInLength", type: "uint32" },
        { name: "dataOut", type: "bytes", lengthInArg: "dataOutMoved", direction: "out" },
        { name: "dataOutAvailable", type: "uint32" },
        { name: "dataOutMoved", type: "pointer" }
      ],
      debug: true
    }
  ]
};

