   ---
   platform: android
   title: Determining Whether Sensitive Stored Data Has Been Exposed via IPC Mechanisms
   id: MASTG-TEST-0007
   type: [static, dynamic]
   weakness: MASWE-0064
   profiles: [L1, L2]
   best-practices: [MASTG-BEST-XXXX]
   knowledge: [MASTG-KNOW-0020]
   ---

   ## Overview

   If the app exposes file system-based content providers without proper access restrictions, other apps on the device can use IPC to read sensitive stored data, such as internal files kept in the app sandbox. This can cause unauthorized disclosure of data that is intended to remain private to the application. This test case checks whether file-based content providers can be accessed from outside the app and whether they return sensitive stored data.

   ## Steps

   1. Inspect `AndroidManifest.xml` and identify all `<provider>` components.
   2. For each provider, determine whether it is accessible to other apps:
      - Check `android:exported` and any `<intent-filter>` that could implicitly export the provider.
      - Check for access restrictions such as `android:permission`, `android:readPermission`, `android:writePermission`, or path-based permissions.
   3. Inspect the source code and identify providers that handle file-based sensitive stored data:
      - Look for subclasses of `android.content.ContentProvider`.
      - Look for file access patterns such as `openFile`, `ParcelFileDescriptor.open`, `File`, and use of the app's private storage directories.
   4. Perform dynamic verification from an external context:
      1. Enumerate the app's content providers and their authorities.
      2. Identify file-based content providers.
      3. Attempt to read data from file-based providers via their `content://` URIs using known or guessable filenames under the provider's exposed path.
   5. Record the retrieved data and the URIs used to retrieve it.

   ## Observation

   The output should include a list of content provider authorities and one or more proof-of-access results indicating that an external caller can read file-based provider URIs, including any sensitive stored data returned, such as the contents of internal application files.

   ## Evaluation

   The test case will fail if an external caller is able to access one or more file-based content providers and obtain sensitive stored data from internal/private files without appropriate access restrictions, for example by using an exported provider with no enforced read permissions.
