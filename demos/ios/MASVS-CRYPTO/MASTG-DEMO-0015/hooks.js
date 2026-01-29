var target = {
  category: "CRYPTO",
  hooks: [
    // CC_SHA1(const void *data, CC_LONG len, unsigned char *md)
    {
      native: true,
      symbol: "CC_SHA1",
      maxFrames: -1,
      filterEventsByStacktrace: "MASTestApp",
      args: [
        { name: "data", type: "bytes", lengthInArg: "len" },
        { name: "len", type: "uint32" },
        { name: "md", type: "bytes", length: 20, direction: "out" } // SHA1 digest length
      ]
    },
    // CC_MD5(const void *data, CC_LONG len, unsigned char *md)
    {
      native: true,
      symbol: "CC_MD5",
      maxFrames: -1,
      filterEventsByStacktrace: "MASTestApp",
      args: [
        { name: "data", type: "bytes", lengthInArg: "len" },
        { name: "len", type: "uint32" },
        { name: "md", type: "bytes", length: 16, direction: "out" } // MD5 digest length
      ]
    }
  ]
};
