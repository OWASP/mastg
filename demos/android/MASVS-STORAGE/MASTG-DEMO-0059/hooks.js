var target = {
  category: "STORAGE",
  demo: "0059",
  hooks: [
    {
      class: "android.app.SharedPreferencesImpl$EditorImpl",
      methods: [
        "putString",
        "putStringSet"
      ]
    },
    {
      class: "javax.crypto.Cipher",
      methods: [
        "getInstance",
        "doFinal",
        "init",
        "update"
      ]
    },
    {
      class: "java.security.KeyStore",
      methods: [
        // "getInstance",
        "setEntry",
        "getEntry"
      ]
    },
    {
      class: "javax.crypto.KeyGenerator",
      methods: [
        "getInstance",
        // "init",
        "generateKey"
      ]
    },
    {
      class: "android.util.Base64",
      methods: [
        "encodeToString",
        "decode"
      ]
    }
  ]
}
