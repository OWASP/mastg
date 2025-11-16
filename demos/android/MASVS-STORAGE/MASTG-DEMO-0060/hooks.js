var target = {
  category: "STORAGE",
  demo: "0060",
  hooks: [
    {
      class: "androidx.security.crypto.EncryptedSharedPreferences$Editor",
      methods: [
        "putString",
        "putStringSet"
      ]
    },
    {
      class: "android.app.SharedPreferencesImpl$EditorImpl",
      methods: [
        "putString",
        "putStringSet"
      ]
    }
  ]
}
