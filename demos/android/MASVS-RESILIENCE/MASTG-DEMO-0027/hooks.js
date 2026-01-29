var target = {
  category: "STORAGE",
  hooks: [
    {
      class: "android.app.KeyguardManager",
      method: "isDeviceSecure",
      overloads: [
        {
          "args": []
        }
      ]
    },
    {
      class: "android.hardware.biometrics.BiometricManager",
      method: "canAuthenticate",
      overloads: [
        {
          args: ["int"]
        }
      ]
    }
  ]
};
