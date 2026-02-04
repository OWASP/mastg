package org.owasp.mastestapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MastgTest {
    private final Context context;

    public MastgTest(Context context) {
        this.context = context;
    }

    public final String mastgTest() {
        List checks = new ArrayList();
        
        if (this.checkForSuBinary()) {
            checks.add("✓ Found su binary");
        } else {
            checks.add("✗ No su binary found");
        }
        
        if (this.checkForRootPackages()) {
            checks.add("✓ Found root management apps");
        } else {
            checks.add("✗ No root management apps found");
        }
        
        if (this.checkForTestKeys()) {
            checks.add("✓ Device has test-keys build");
        } else {
            checks.add("✗ Device has release-keys build");
        }
        
        if (this.checkForDangerousProps()) {
            checks.add("✓ Found dangerous system properties");
        } else {
            checks.add("✗ No dangerous system properties");
        }
        
        boolean isRooted = this.checkForSuBinary() || this.checkForRootPackages() 
                        || this.checkForTestKeys() || this.checkForDangerousProps();
        
        StringBuilder sb = new StringBuilder("Root Detection Results:\n\n");
        for (int i = 0; i < checks.size(); i++) {
            sb.append((String)checks.get(i));
            if (i < checks.size() - 1) {
                sb.append("\n");
            }
        }
        sb.append("\n\nDevice appears to be rooted: ").append(isRooted);
        return sb.toString();
    }

    private final boolean checkForSuBinary() {
        String[] paths = new String[]{
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        };
        
        int length = paths.length;
        for (int i = 0; i < length; i++) {
            String path = paths[i];
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    private final boolean checkForRootPackages() {
        String[] packages = new String[]{
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.kingo.root",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot"
        };
        
        int length = packages.length;
        for (int i = 0; i < length; i++) {
            String packageName = packages[i];
            try {
                this.context.getPackageManager().getPackageInfo(packageName, 0);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                // Continue
            }
        }
        return false;
    }

    private final boolean checkForTestKeys() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private final boolean checkForDangerousProps() {
        Map dangerousProps = new HashMap();
        dangerousProps.put("ro.debuggable", "1");
        dangerousProps.put("ro.secure", "0");
        
        for (Object entry : dangerousProps.entrySet()) {
            Map.Entry e = (Map.Entry)entry;
            String prop = (String)e.getKey();
            String value = (String)e.getValue();
            String propValue = this.getSystemProperty(prop);
            if (value.equals(propValue)) {
                return true;
            }
        }
        return false;
    }

    private final String getSystemProperty(String key) {
        try {
            Process process = Runtime.getRuntime().exec("getprop " + key);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            String result = reader.readLine();
            reader.close();
            return result != null ? result.trim() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
