---
title: Getting Loaded Classes and Methods Dynamically
platform: android
---

You can use the command `Java` in the Frida CLI to access the Java runtime and retrieve information from the running app. Remember that, unlike Frida for iOS, in Android, you need to wrap your code inside a `Java.perform` function. Thus, it's more convenient to use Frida scripts, e.g. get a list of loaded Java classes and their corresponding methods and fields, or for more complex information gathering or instrumentation. One such script is listed below. The script to list the class's methods used below is available on [Github](https://github.com/frida/frida-java-bridge/issues/44 "Github").

```js
// Get list of loaded Java classes and methods

// Filename: java_class_listing.js

'use strict';

Java.perform(function () {
  var classes = [];
  var seen = Object.create(null);

  Java.enumerateLoadedClasses({
    onMatch: function (name) {
      if (seen[name]) return;
      seen[name] = true;

      classes.push({
        name: name,
        package: name.indexOf(".") > 0 ? name.substring(0, name.lastIndexOf(".")) : ""
      });
    },
    onComplete: function () {
      console.log(JSON.stringify({
        type: "loaded_classes",
        total: classes.length,
        classes: classes
      }));
    }
  });
});
```

After saving the script to a file called `java_class_listing.js`, you can tell Frida CLI to load it by using the flag `-l` and inject it into the foreground application using the flag `-F`.

```bash
frida -U -q -F -l java_class_listing.js -o classes.json
```

The `classes.json` file will contain the output of the script, which is a JSON object with the total number of loaded classes and an array of class names and their corresponding packages.

```json
{
    "type": "loaded_classes",
    "total": 30914,
    "classes": [
        {
            "name": "org.owasp.mastestapp.MastgTest",
            "package": "org.owasp.mastestapp"
        },
        {
            "name": "java.io.ObjectStreamException",
            "package": "java.io"
        },
        {
            "name": "javax.crypto.IllegalBlockSizeException",
            "package": "javax.crypto"
        },
        ... 
    ]
}
```

Given the verbosity of the output, the system classes can be filtered out programmatically to make the output more readable and relevant to the use case.

For example, use `jq` to filter out classes that belong to the `org.owasp.mastestapp.*` packages:

```bash
jq '.classes[] | select(.package | startswith("org.owasp.mastestapp"))' classes.json
```

Output:

```json
{
  "name": "org.owasp.mastestapp.MastgTest",
  "package": "org.owasp.mastestapp"
}
{
  "name": "org.owasp.mastestapp.BaseScreenKt$BaseScreen$2",
  "package": "org.owasp.mastestapp"
}
{
  "name": "org.owasp.mastestapp.MainActivityKt$MainScreen$2",
  "package": "org.owasp.mastestapp"
}
...
```
