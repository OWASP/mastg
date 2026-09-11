Java.perform(() => {

    const Exception = Java.use("java.lang.Exception");
    const APP_PACKAGE = "org.owasp.mastestapp";

    function stackContainsApp() {
        return Exception.$new().getStackTrace().toString().includes(APP_PACKAGE);
    }

    function logIfApp(message) {
        if (stackContainsApp()) {
            console.log(message);
        }
    }

    function hookNoArgString(clazz, methodName, label) {
        try {
            const method = clazz[methodName].overload();
            method.implementation = function () {
                const result = method.call(this);
                logIfApp(`[${label}] -> ${result}`);
                return result;
            };
        } catch (err) {
            console.log(`[-] Unable to hook ${label}: ${err}`);
        }
    }

    function hookNoArgInt(clazz, methodName, label) {
        try {
            const method = clazz[methodName].overload();
            method.implementation = function () {
                const result = method.call(this);
                logIfApp(`[${label}] -> ${result}`);
                return result;
            };
        } catch (err) {
            console.log(`[-] Unable to hook ${label}: ${err}`);
        }
    }

    try {
        const MastgTest = Java.use("org.owasp.mastestapp.MastgTest");
        const queryBuildValue = MastgTest.queryBuildValue.overload("java.lang.String", "java.lang.String");
        queryBuildValue.implementation = function (name, value) {
            const result = queryBuildValue.call(this, name, value);
            logIfApp(`[Build] ${name}=${value}`);
            return result;
        };
    } catch (err) {
        console.log(`[-] Unable to hook build value queries: ${err}`);
    }

    try {
        const TelephonyManager = Java.use("android.telephony.TelephonyManager");
        hookNoArgString(TelephonyManager, "getLine1Number", "TelephonyManager.getLine1Number");
        hookNoArgString(TelephonyManager, "getNetworkCountryIso", "TelephonyManager.getNetworkCountryIso");
        hookNoArgInt(TelephonyManager, "getNetworkType", "TelephonyManager.getNetworkType");
        hookNoArgString(TelephonyManager, "getNetworkOperator", "TelephonyManager.getNetworkOperator");
        hookNoArgString(TelephonyManager, "getNetworkOperatorName", "TelephonyManager.getNetworkOperatorName");
        hookNoArgInt(TelephonyManager, "getPhoneType", "TelephonyManager.getPhoneType");
        hookNoArgString(TelephonyManager, "getSimCountryIso", "TelephonyManager.getSimCountryIso");
        hookNoArgString(TelephonyManager, "getVoiceMailNumber", "TelephonyManager.getVoiceMailNumber");
    } catch (err) {
        console.log(`[-] Unable to hook TelephonyManager: ${err}`);
    }

    try {
        const PackageManager = Java.use("android.app.ApplicationPackageManager");

        try {
            const hasSystemFeature = PackageManager.hasSystemFeature.overload("java.lang.String");
            hasSystemFeature.implementation = function (feature) {
                const result = hasSystemFeature.call(this, feature);
                logIfApp(`[PackageManager.hasSystemFeature] ${feature} -> ${result}`);
                return result;
            };
        } catch (err) {
            console.log(`[-] Unable to hook PackageManager.hasSystemFeature(String): ${err}`);
        }

        try {
            const hasSystemFeature = PackageManager.hasSystemFeature.overload("java.lang.String", "int");
            hasSystemFeature.implementation = function (feature, version) {
                const result = hasSystemFeature.call(this, feature, version);
                logIfApp(`[PackageManager.hasSystemFeature] ${feature} (${version}) -> ${result}`);
                return result;
            };
        } catch (err) {
            console.log(`[-] Unable to hook PackageManager.hasSystemFeature(String,int): ${err}`);
        }

        try {
            const queryIntentActivities = PackageManager.queryIntentActivities.overload("android.content.Intent", "int");
            queryIntentActivities.implementation = function (intent, flags) {
                const result = queryIntentActivities.call(this, intent, flags);
                const count = result ? result.size() : 0;
                logIfApp(`[PackageManager.queryIntentActivities] count=${count}`);
                return result;
            };
        } catch (err) {
            console.log(`[-] Unable to hook PackageManager.queryIntentActivities(Intent,int): ${err}`);
        }

        try {
            const queryIntentActivities = PackageManager.queryIntentActivities.overload(
                "android.content.Intent",
                "android.content.pm.PackageManager$ResolveInfoFlags"
            );
            queryIntentActivities.implementation = function (intent, flags) {
                const result = queryIntentActivities.call(this, intent, flags);
                const count = result ? result.size() : 0;
                logIfApp(`[PackageManager.queryIntentActivities] count=${count}`);
                return result;
            };
        } catch (err) {
            console.log(`[-] Unable to hook PackageManager.queryIntentActivities(Intent,ResolveInfoFlags): ${err}`);
        }

        try {
            const getPackageInfo = PackageManager.getPackageInfo.overload("java.lang.String", "int");
            getPackageInfo.implementation = function (packageName, flags) {
                try {
                    const result = getPackageInfo.call(this, packageName, flags);
                    logIfApp(`[PackageManager.getPackageInfo] ${packageName} -> found`);
                    return result;
                } catch (err) {
                    logIfApp(`[PackageManager.getPackageInfo] ${packageName} -> not found`);
                    throw err;
                }
            };
        } catch (err) {
            console.log(`[-] Unable to hook PackageManager.getPackageInfo(String,int): ${err}`);
        }

        try {
            const getPackageInfo = PackageManager.getPackageInfo.overload(
                "java.lang.String",
                "android.content.pm.PackageManager$PackageInfoFlags"
            );
            getPackageInfo.implementation = function (packageName, flags) {
                try {
                    const result = getPackageInfo.call(this, packageName, flags);
                    logIfApp(`[PackageManager.getPackageInfo] ${packageName} -> found`);
                    return result;
                } catch (err) {
                    logIfApp(`[PackageManager.getPackageInfo] ${packageName} -> not found`);
                    throw err;
                }
            };
        } catch (err) {
            console.log(`[-] Unable to hook PackageManager.getPackageInfo(String,PackageInfoFlags): ${err}`);
        }
    } catch (err) {
        console.log(`[-] Unable to hook PackageManager: ${err}`);
    }

    try {
        const ActivityManager = Java.use("android.app.ActivityManager");
        const getRunningServices = ActivityManager.getRunningServices.overload("int");
        getRunningServices.implementation = function (maxNum) {
            const result = getRunningServices.call(this, maxNum);
            const count = result ? result.size() : 0;
            logIfApp(`[ActivityManager.getRunningServices] count=${count}`);
            return result;
        };
    } catch (err) {
        console.log(`[-] Unable to hook ActivityManager.getRunningServices: ${err}`);
    }

    try {
        const GLES20 = Java.use("android.opengl.GLES20");
        const glGetString = GLES20.glGetString.overload("int");
        glGetString.implementation = function (name) {
            const result = glGetString.call(GLES20, name);
            let label = `0x${name.toString(16)}`;
            if (name === 0x1f00) {
                label = "GL_VENDOR";
            } else if (name === 0x1f01) {
                label = "GL_RENDERER";
            } else if (name === 0x1f02) {
                label = "GL_VERSION";
            }
            logIfApp(`[GLES20.glGetString] ${label} -> ${result}`);
            return result;
        };
    } catch (err) {
        console.log(`[-] Unable to hook GLES20.glGetString: ${err}`);
    }

    try {
        const Build = Java.use("android.os.Build");
        const getSerial = Build.getSerial.overload();
        getSerial.implementation = function () {
            const result = getSerial.call(Build);
            logIfApp(`[Build.getSerial] -> ${result}`);
            return result;
        };

        const getRadioVersion = Build.getRadioVersion.overload();
        getRadioVersion.implementation = function () {
            const result = getRadioVersion.call(Build);
            logIfApp(`[Build.getRadioVersion] -> ${result}`);
            return result;
        };
    } catch (err) {
        console.log(`[-] Unable to hook Build methods: ${err}`);
    }

    console.log("\n[+] Frida script loaded for emulator detection API tracing.\n");
});
