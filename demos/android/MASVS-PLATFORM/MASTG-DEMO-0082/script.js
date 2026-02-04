function enumerateDeleteAllDataMethod() {
  const res = Java.enumerateMethods('com.android.webview.chromium.*!deleteAllData');
  return res && res[0];
}

function enumerateSetDomStorageEnabledMethod() {
  const res = Java.enumerateMethods('com.android.webview.chromium.*!setDomStorageEnabled');
  return res && res[0];
}

function ensureClassLoaded(className) {
  try {
    Java.use(className);
  } catch (e) {
  }
}

function printBacktrace(maxLines = 8) {
  let Exception = Java.use("java.lang.Exception");
  let stackTrace = Exception.$new().getStackTrace().toString().split(",");

  console.log("\nBacktrace:");
  for (let i = 0; i < Math.min(maxLines, stackTrace.length); i++) {
      console.log(stackTrace[i]);
  }
  console.log("\n");
}

Java.perform(function () {
  console.log('Enumerating chromium.');

  if (enumerateDeleteAllDataMethod() === undefined) {
    console.log('Bring WebStorage to memory so we can hook its deleteAllData method.');
    Java.use('android.webkit.WebStorage').getInstance();
  }

  const deleteAllDataMethod = enumerateDeleteAllDataMethod();
  if (deleteAllDataMethod !== undefined) {
    Java.classFactory.loader = deleteAllDataMethod.loader;
    const WebStorageAdapter = Java.use(deleteAllDataMethod.classes[0].name);

    WebStorageAdapter.deleteAllData.implementation = function () {
      console.log('WebStorage.deleteAllData called.');
      printBacktrace();
      return this.deleteAllData();
    };

    console.log('WebStorage.deleteAllData hooked.');
  } else {
    console.log('WebStorage.deleteAllData not found.');
  }

  ensureClassLoaded('android.webkit.WebView');
  ensureClassLoaded('android.webkit.WebSettings');

  const domMethod = enumerateSetDomStorageEnabledMethod();
  if (domMethod !== undefined) {
    Java.classFactory.loader = domMethod.loader;
    const ContentSettingsAdapter = Java.use(domMethod.classes[0].name);

    if (ContentSettingsAdapter.setDomStorageEnabled.overloads.length === 0) {
      console.log('setDomStorageEnabled found but no overloads exposed.');
    } else {
      ContentSettingsAdapter.setDomStorageEnabled.overloads.forEach(function (ov) {
        ov.implementation = function () {
          const enabled = arguments.length > 0 ? arguments[0] : undefined;
          console.log('ContentSettingsAdapter.setDomStorageEnabled called, enabled is ' + enabled + '.');
          printBacktrace();
          return ov.apply(this, arguments);
        };
      });

      console.log('ContentSettingsAdapter.setDomStorageEnabled hooked.');
    }
  } else {
    console.log('ContentSettingsAdapter.setDomStorageEnabled not found.');
  }
});
