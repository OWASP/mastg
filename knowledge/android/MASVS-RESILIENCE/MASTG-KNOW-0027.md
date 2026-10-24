---
masvs_category: MASVS-RESILIENCE
platform: android
title: Root Detection
---

In the context of anti-reversing, the goal of root detection is to make running the app on a rooted device a bit more difficult, which in turn blocks some of the tools and techniques reverse engineers like to use. Like most other defenses, root detection is not very effective by itself, but implementing multiple root checks that are scattered throughout the app can improve the effectiveness of the overall anti-tampering scheme.

For Android, we define "root detection" a bit more broadly, including custom ROMs detection, i.e., determining whether the device is a stock Android build or a custom build.

Root detection can also be implemented through libraries such as @MASTG-TOOL-0146 or @MASTG-TOOL-0147 (none of them endorsed by OWASP, see the disclaimers in their respective sections). These libraries implement multiple root detection techniques using both Java and native code to make bypassing more difficult. They also provide sample apps to demonstrate their capabilities, see @MASTG-APP-0032 and @MASTG-APP-0033.

## File Existence Checks

Perhaps the most widely used method of programmatic detection is checking for files typically found on rooted devices, such as package files of common rooting apps and their associated files and directories, including the following:

```sh
/system/app/Superuser.apk
/system/etc/init.d/99SuperSUDaemon
/dev/com.koushikdutta.superuser.daemon/
/system/xbin/daemonsu
```

Detection code also often looks for binaries that are usually installed once a device has been rooted. These searches include checking for busybox and attempting to open the _su_ binary at different locations:

```sh
/sbin/su
/system/bin/su
/system/bin/failsafe/su
/system/xbin/su
/system/xbin/busybox
/system/sd/xbin/su
/data/local/su
/data/local/xbin/su
/data/local/bin/su
```

Checking whether `su` is on the PATH also works:

```java
    public static boolean checkRoot(){
        for(String pathDir : System.getenv("PATH").split(":")){
            if(new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }
```

File checks can be easily implemented in both Java and native code. The following JNI example (adapted from [rootinspector](https://github.com/devadvance/rootinspector/ "rootinspector")) uses the `stat` system call to retrieve information about a file and returns "1" if the file exists.

```c
jboolean Java_com_example_statfile(JNIEnv * env, jobject this, jstring filepath) {
  jboolean fileExists = 0;
  jboolean isCopy;
  const char * path = (*env)->GetStringUTFChars(env, filepath, &isCopy);
  struct stat fileattrib;
  if (stat(path, &fileattrib) < 0) {
    __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NATIVE: stat error: [%s]", strerror(errno));
  } else
  {
    __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NATIVE: stat success, access perms: [%d]", fileattrib.st_mode);
    return 1;
  }

  return 0;
}
```

## Executing Privileged Commands

Another way of determining whether `su` exists is attempting to execute it through the `Runtime.getRuntime.exec` method. An IOException will be thrown if `su` is not on the PATH. The same method can be used to check for other programs often found on rooted devices, such as busybox and the symbolic links that typically point to it.

## Checking Running Processes

Supersu-by far the most popular rooting tool-runs an authentication daemon named `daemonsu`, so the presence of this process is another sign of a rooted device. Running processes can be enumerated with the `ActivityManager.getRunningAppProcesses` and `manager.getRunningServices` APIs, the `ps` command, and browsing through the `/proc` directory. The following is an example implemented in [rootinspector](https://github.com/devadvance/rootinspector/ "rootinspector"):

```java
    public boolean checkRunningProcesses() {

      boolean returnValue = false;

      // Get currently running application processes
      List<RunningServiceInfo> list = manager.getRunningServices(300);

      if(list != null){
        String tempName;
        for(int i=0;i<list.size();++i){
          tempName = list.get(i).process;

          if(tempName.contains("supersu") || tempName.contains("superuser")){
            returnValue = true;
          }
        }
      }
      return returnValue;
    }
```

## Checking Installed App Packages

You can probe for known root manager packages using `PackageManager`, for example by calling `getPackageInfo` for specific package names. Common examples include:

```sh
eu.chainfire.supersu
com.noshufou.android.su
com.koushikdutta.superuser
com.topjohnwu.magisk
```

On Android 11 and later, [package visibility restrictions](https://developer.android.com/training/package-visibility) affect this technique. If a package is installed but not visible to the app, [`getPackageInfo`](https://developer.android.com/reference/android/content/pm/PackageManager#getPackageInfo(java.lang.String,%20int)) behaves the same as if the package were not installed, typically by throwing [`PackageManager.NameNotFoundException`](https://developer.android.com/reference/android/content/pm/PackageManager.NameNotFoundException). This can create false negatives for package based root detection.

Developers can query specific packages on Android 11 and later by declaring them in the app manifest using the `<queries>` element:

```xml
<queries>
    <package android:name="com.topjohnwu.magisk" />
</queries>
```

Otherwise they can use the `QUERY_ALL_PACKAGES` permission, which grants visibility to all installed apps but is [subject to Google Play restrictions](https://support.google.com/googleplay/android-developer/answer/10158779) and may not be justifiable for many use cases.

## Checking for Writable Partitions and System Directories

Unusual permissions on system directories may indicate a customized or rooted device. Although the system and data directories are normally mounted read-only, you'll sometimes find them mounted read-write when the device is rooted. Look for these filesystems mounted with the "rw" flag or try to create a file in the data directories.

## Checking for Custom Android Builds

Checking for signs of test builds and custom ROMs is also helpful. One way to do this is to check the `BUILD.TAGS` for [`test-keys`](https://source.android.com/docs/core/ota/sign_builds#release-keys), which normally [indicates a custom Android image](https://web.archive.org/web/20160404172508/https://resources.infosecinstitute.com/android-hacking-security-part-8-root-detection-evasion/). For example, @MASTG-TOOL-0146 [checks the BUILD.TAGS as follows](https://github.com/scottyab/rootbeer/blob/0.1.1/rootbeerlib/src/main/java/com/scottyab/rootbeer/RootBeer.java#L71-L80):

```java
public boolean detectTestKeys() {
    String buildTags = android.os.Build.TAGS;

    return buildTags != null && buildTags.contains("test-keys");
}
```

Missing Google Over-The-Air (OTA) certificates is another sign of a custom ROM: on stock Android builds, [OTA updates Google's public certificates](https://www.netspi.com/blog/technical-blog/mobile-application-penetration-testing/android-root-detection-techniques/ "Android Root Detection Techniques").
