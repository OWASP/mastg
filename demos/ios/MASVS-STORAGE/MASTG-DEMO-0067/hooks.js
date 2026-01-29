var target = {
  category: "STORAGE",
  hooks: [
    {
      native: true,
      // module: "libc.so",
      symbol: "open",
      maxFrames: 5,
      filterEventsByStacktrace: "MASTestApp",
      args: [
        { type: "string",
          name: "path",
          filter: ["Containers/Data/Application/"]
        }
      ]
    }
  ]
};

