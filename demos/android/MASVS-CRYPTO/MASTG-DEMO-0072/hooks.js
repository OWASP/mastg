var target = {
  category: "CRYPTO",
  hooks: [
    {
      class: "javax.crypto.Cipher",
      method: "init",
      overloads: [
        { args: ["int", "java.security.Key"] },
      ],
      maxFrames: 3
    },
    {
      class: "java.security.Signature",
      methods: ["initSign", "initVerify"],
      maxFrames: 10
    },
    {
      class: "android.security.keystore.KeyGenParameterSpec$Builder",
      methods: ["$init"],
      maxFrames: 10
    }
  ]
};
