var target = {
    category: "NETWORK",
    demo: "0082",
    hooks: [
    {
      class: "java.net.URL",
      methods: [
        "$init"
      ]
    },
    {
      class: "java.net.URI",
      methods: [
        "$init"
      ]
    },
    {
      class: "android.net.Uri",
      methods: [
        "parse"
      ]
    },
    {
      class: "android.net.Uri$Builder",
      methods: [
        "scheme"
      ]
    },
    {
      class: "android.media.MediaPlayer",
      methods: [
        "setDataSource"
      ]
    }
  ]
}
