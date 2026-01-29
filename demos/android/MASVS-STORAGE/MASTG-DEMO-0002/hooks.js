var target = {
  category: "STORAGE",
  hooks: [
    {
      class: "android.content.ContentResolver",
      method: "insert",
      overloads: [
        {
          args: ["android.net.Uri", "android.content.ContentValues"]
        }
      ]
    },
    {
      native: true,
      module: "libc.so",
      symbol: "open",
      // maxFrames: 15,
      filterEventsByStacktrace: "org.owasp.mastestapp",
      args: [
        { type: "string",
          name: "path",
          filter: ["/sdcard", "/storage/emulated"]
        }
      ]
    }
  ]
};

