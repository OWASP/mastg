package org.owasp.mastestapp

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES20
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MastgTest(private val context: Context) {

    fun shouldRunInMainThread(): Boolean {
        return true
    }

    fun mastgTest(): String {
        // SUMMARY: Detect emulator indicators using build, telephony, file, package, and graphics signals.
        // NOTE: Play Integrity is not included here because it requires Play Console setup and
        // server-side token verification (service account + network), which breaks the
        // self-contained demo requirement. In real apps, check deviceRecognitionVerdict for
        // MEETS_VIRTUAL_INTEGRITY.
        val missingPermissions = ensureTelephonyPermissions()
        val buildQueries = queryBuildProperties()
        val telephonyQueries = queryTelephonyProperties()
        val packageQueries = queryPackageChecks()
        val openGlQueries = queryOpenGlProperties()

        val allQueries = buildQueries + telephonyQueries + packageQueries + openGlQueries
        val indicators = buildIndicators(buildQueries) +
            telephonyIndicators(telephonyQueries) +
            packageIndicators(packageQueries) +
            openGlIndicators(openGlQueries)

        val queryOutput = allQueries.joinToString("\n") { "${it.name}=${it.displayValue}" }
        val permissionNote = telephonyPermissionNote(missingPermissions)

        val indicatorSummary = if (indicators.isNotEmpty()) {
            "Indicators matched in this run: ${indicators.joinToString(", ")}"
        } else {
            "Indicators matched in this run: none"
        }
        // PASS: [MASTG-TEST-0351] The app implements emulator detection checks. In this case, this app is a PASS as the emulation detection checks are performed.
        // FAIL: [MASTG-TEST-0351] The test fails if the app lacks emulator detection checks.
        val output = "Queried properties:\n$queryOutput\n\n$indicatorSummary$permissionNote"

        Log.i("MASTG-TEST", output)
        return output
    }

    private data class QueryResult(
        val name: String,
        val rawValue: String?,
        val displayValue: String
    )

    private fun queryBuildProperties(): List<QueryResult> {
        return listOf(
            queryBuildValue("Build.BOARD", Build.BOARD),
            queryBuildValue("Build.BRAND", Build.BRAND),
            queryBuildValue("Build.DEVICE", Build.DEVICE),
            queryBuildValue("Build.FINGERPRINT", Build.FINGERPRINT),
            queryBuildValue("Build.MODEL", Build.MODEL),
            queryBuildValue("Build.MANUFACTURER", Build.MANUFACTURER),
            queryBuildValue("Build.PRODUCT", Build.PRODUCT),
            queryBuildValue("Build.HARDWARE", Build.HARDWARE),
            queryBuildValue("Build.ID", Build.ID),
            queryBuildValue("Build.RADIO", Build.getRadioVersion()),
            queryBuildValue("Build.SERIAL", safeBuildSerial()),
            queryBuildValue("Build.TAGS", Build.TAGS),
            queryBuildValue("Build.USER", Build.USER)
        )
    }

    private fun queryBuildValue(name: String, value: String?): QueryResult {
        val displayValue = value ?: "<null>"
        return QueryResult(name, value, displayValue)
    }

    private fun queryTelephonyProperties(): List<QueryResult> {
        val hasTelephony = context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        return listOf(
            QueryResult("PackageManager.FEATURE_TELEPHONY", hasTelephony.toString(), hasTelephony.toString()),
            queryTelephonyValue(telephonyManager, "TelephonyManager.getLine1Number") {
                @Suppress("DEPRECATION")
                it.line1Number
            },
            queryTelephonyValue(telephonyManager, "TelephonyManager.getNetworkCountryIso") {
                it.networkCountryIso
            },
            queryTelephonyIntValue(telephonyManager, "TelephonyManager.getNetworkType") {
                @Suppress("DEPRECATION")
                it.networkType
            },
            queryTelephonyValue(telephonyManager, "TelephonyManager.getNetworkOperator") {
                it.networkOperator
            },
            queryTelephonyValue(telephonyManager, "TelephonyManager.getNetworkOperatorName") {
                it.networkOperatorName
            },
            queryTelephonyIntValue(telephonyManager, "TelephonyManager.getPhoneType") {
                it.phoneType
            },
            queryTelephonyValue(telephonyManager, "TelephonyManager.getSimCountryIso") {
                it.simCountryIso
            },
            queryTelephonyValue(telephonyManager, "TelephonyManager.getVoiceMailNumber") {
                @Suppress("DEPRECATION")
                it.voiceMailNumber
            }
        )
    }

    private fun queryTelephonyValue(
        telephonyManager: TelephonyManager?,
        name: String,
        block: (TelephonyManager) -> String?
    ): QueryResult {
        if (telephonyManager == null) {
            return QueryResult(name, null, "<unavailable>")
        }

        return try {
            val value = block(telephonyManager)
            QueryResult(name, value, value ?: "<null>")
        } catch (e: SecurityException) {
            QueryResult(name, null, "<permission denied>")
        }
    }

    private fun queryTelephonyIntValue(
        telephonyManager: TelephonyManager?,
        name: String,
        block: (TelephonyManager) -> Int
    ): QueryResult {
        if (telephonyManager == null) {
            return QueryResult(name, null, "<unavailable>")
        }

        return try {
            val value = block(telephonyManager).toString()
            QueryResult(name, value, value)
        } catch (e: SecurityException) {
            QueryResult(name, null, "<permission denied>")
        }
    }

    private fun queryPackageChecks(): List<QueryResult> {
        val pm = context.packageManager
        val results = mutableListOf<QueryResult>()
        val prefixes = emulatorPackagePrefixes()

        val launcherPackages = queryLauncherPackages(pm)
        results.add(QueryResult(
            "PackageManager.queryIntentActivities(MAIN/LAUNCHER).count",
            launcherPackages.size.toString(),
            launcherPackages.size.toString()
        ))
        results.addAll(buildPrefixResults("LauncherPackagePrefix", prefixes, launcherPackages))

        for (pkg in emulatorPackageExact()) {
            val installed = isPackageInstalled(pm, pkg).toString()
            results.add(QueryResult("PackageManager.hasPackage:$pkg", installed, installed))
        }

        val runningServices = queryRunningServices()
        results.add(QueryResult(
            "ActivityManager.getRunningServices.count",
            runningServices.size.toString(),
            runningServices.size.toString()
        ))
        val serviceMatches = runningServices.filter { it.startsWith("com.bluestacks.") }
        val serviceDisplay = serviceMatches.joinToString(", ").ifEmpty { "<none>" }
        val serviceMatchValue = serviceMatches.isNotEmpty().toString()
        results.add(QueryResult(
            "RunningServicePrefix:com.bluestacks.",
            serviceMatchValue,
            serviceDisplay
        ))

        return results
    }

    private fun buildPrefixResults(
        label: String,
        prefixes: List<String>,
        packages: List<String>
    ): List<QueryResult> {
        return prefixes.map { prefix ->
            val matches = packages.filter { it.startsWith(prefix) }
            val display = matches.joinToString(", ").ifEmpty { "<none>" }
            val matched = matches.isNotEmpty().toString()
            QueryResult("$label:$prefix", matched, display)
        }
    }

    private fun queryLauncherPackages(pm: PackageManager): List<String> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = if (Build.VERSION.SDK_INT >= 33) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }
        return activities.mapNotNull { it.activityInfo?.packageName }.distinct()
    }

    private fun queryRunningServices(): List<String> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return emptyList()

        @Suppress("DEPRECATION")
        val services = activityManager.getRunningServices(20)
        return services.map { it.service.packageName }.distinct()
    }

    private fun isPackageInstalled(pm: PackageManager, packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= 33) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, 0)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun emulatorPackagePrefixes(): List<String> {
        return listOf(
            "com.vphone.",
            "com.bignox.",
            "com.bluestacks",
            "com.microvirt."
        )
    }

    private fun emulatorPackageExact(): List<String> {
        return listOf(
            "com.google.android.launcher.layouts.genymotion",
            "com.bignox.app",
        )
    }

    private fun queryOpenGlProperties(): List<QueryResult> {
        // OpenGL strings require a current EGL context, so create a small pbuffer surface.
        val display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (display == EGL14.EGL_NO_DISPLAY) {
            return openGlUnavailableResults()
        }

        val eglVersion = IntArray(2)
        if (!EGL14.eglInitialize(display, eglVersion, 0, eglVersion, 1)) {
            EGL14.eglTerminate(display)
            return openGlUnavailableResults()
        }

        val configAttribs = intArrayOf(
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        if (!EGL14.eglChooseConfig(display, configAttribs, 0, configs, 0, configs.size, numConfigs, 0)) {
            EGL14.eglTerminate(display)
            return openGlUnavailableResults()
        }

        val contextAttribs = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
        val context = EGL14.eglCreateContext(display, configs[0], EGL14.EGL_NO_CONTEXT, contextAttribs, 0)
        if (context == null || context == EGL14.EGL_NO_CONTEXT) {
            EGL14.eglTerminate(display)
            return openGlUnavailableResults()
        }

        val surfaceAttribs = intArrayOf(EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE)
        val surface = EGL14.eglCreatePbufferSurface(display, configs[0], surfaceAttribs, 0)
        if (surface == null || surface == EGL14.EGL_NO_SURFACE) {
            EGL14.eglDestroyContext(display, context)
            EGL14.eglTerminate(display)
            return openGlUnavailableResults()
        }

        if (!EGL14.eglMakeCurrent(display, surface, surface, context)) {
            EGL14.eglDestroySurface(display, surface)
            EGL14.eglDestroyContext(display, context)
            EGL14.eglTerminate(display)
            return openGlUnavailableResults()
        }

        val renderer = GLES20.glGetString(GLES20.GL_RENDERER)
        val vendor = GLES20.glGetString(GLES20.GL_VENDOR)
        val version = GLES20.glGetString(GLES20.GL_VERSION)

        EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
        EGL14.eglDestroySurface(display, surface)
        EGL14.eglDestroyContext(display, context)
        EGL14.eglTerminate(display)

        return listOf(
            QueryResult("OpenGL.Renderer", renderer, renderer ?: "<unavailable>"),
            QueryResult("OpenGL.Vendor", vendor, vendor ?: "<unavailable>"),
            QueryResult("OpenGL.Version", version, version ?: "<unavailable>")
        )
    }

    private fun openGlUnavailableResults(): List<QueryResult> {
        return listOf(
            QueryResult("OpenGL.Renderer", null, "<unavailable>"),
            QueryResult("OpenGL.Vendor", null, "<unavailable>"),
            QueryResult("OpenGL.Version", null, "<unavailable>")
        )
    }

    private fun buildIndicators(buildQueries: List<QueryResult>): List<String> {
        val indicators = mutableListOf<String>()
        val fingerprint = buildQueries.findValue("Build.FINGERPRINT")
        val model = buildQueries.findValue("Build.MODEL")
        val manufacturer = buildQueries.findValue("Build.MANUFACTURER")
        val hardware = buildQueries.findValue("Build.HARDWARE")
        val product = buildQueries.findValue("Build.PRODUCT")
        val brand = buildQueries.findValue("Build.BRAND")
        val device = buildQueries.findValue("Build.DEVICE")
        val board = buildQueries.findValue("Build.BOARD")
        val serial = buildQueries.findValue("Build.SERIAL")
        val id = buildQueries.findValue("Build.ID")
        val radio = buildQueries.findValue("Build.RADIO")
        val tags = buildQueries.findValue("Build.TAGS")
        val user = buildQueries.findValue("Build.USER")

        if (fingerprint.startsWith("generic") ||
            fingerprint.contains("test-keys") ||
            containsAny(fingerprint, listOf("generic/sdk/generic", "generic x86", "vbox86p", "ttvm"))
        ) {
            indicators.add("Build.FINGERPRINT=$fingerprint")
        }
        if (containsAny(model, listOf("sdk", "google_sdk", "emulator", "android sdk built for", "droid4x", "tiantianvm", "genymotion", "andy", "nox"))) {
            indicators.add("Build.MODEL=$model")
        }
        if (containsAny(manufacturer, listOf("unknown", "genymotion", "droid4x", "tiantianvm", "andy"))) {
            indicators.add("Build.MANUFACTURER=$manufacturer")
        }
        if (containsAny(hardware, listOf("goldfish", "ranchu", "vbox86", "nox", "ttvm"))) {
            indicators.add("Build.HARDWARE=$hardware")
        }
        if (product.startsWith("itoolsavm") ||
            containsAny(product, listOf("sdk", "google_sdk", "sdk_x86", "sdk_google", "vbox86p", "droid4x", "andy", "ttvm", "nox"))
        ) {
            indicators.add("Build.PRODUCT=$product")
        }
        if (brand.startsWith("generic") || containsAny(brand, listOf("generic x86", "ttvm", "andy", "nox"))) {
            indicators.add("Build.BRAND=$brand")
        }
        if (device.startsWith("generic") || containsAny(device, listOf("generic x86", "vbox86p", "ttvm", "andy", "nox", "droid4x"))) {
            indicators.add("Build.DEVICE=$device")
        }
        if (board == "unknown" || board.contains("nox")) {
            indicators.add("Build.BOARD=$board")
        }
        if (serial == "null" || serial == "unknown" || serial.contains("nox")) {
            indicators.add("Build.SERIAL=$serial")
        }
        if (id == "frf91") {
            indicators.add("Build.ID=$id")
        }
        if (radio == "unknown") {
            indicators.add("Build.RADIO=$radio")
        }
        if (tags.contains("test-keys")) {
            indicators.add("Build.TAGS=$tags")
        }
        if (user == "android-build") {
            indicators.add("Build.USER=$user")
        }

        return indicators
    }

    private fun telephonyIndicators(telephonyQueries: List<QueryResult>): List<String> {
        val indicators = mutableListOf<String>()
        val line1Number = telephonyQueries.findValue("TelephonyManager.getLine1Number")
        val lineNumberMatches = setOf(
            "15555215554",
            "15555215556",
            "15555215558",
            "15555215560",
            "15555215562",
            "15555215564",
            "15555215566",
            "15555215568",
            "15555215570",
            "15555215572",
            "15555215574",
            "15555215576",
            "15555215578",
            "15555215580",
            "15555215582",
            "15555215584"
        )
        if (line1Number.isNotEmpty() && lineNumberMatches.contains(line1Number)) {
            indicators.add("TelephonyManager.getLine1Number=$line1Number")
        }

        val networkOperatorName = telephonyQueries.findValue("TelephonyManager.getNetworkOperatorName")
        if (networkOperatorName.isNotEmpty() && networkOperatorName.contains("android")) {
            indicators.add("TelephonyManager.getNetworkOperatorName=$networkOperatorName")
        }

        val voiceMailNumber = telephonyQueries.findValue("TelephonyManager.getVoiceMailNumber")
        if (voiceMailNumber.isNotEmpty() && voiceMailNumber == "15552175049") {
            indicators.add("TelephonyManager.getVoiceMailNumber=$voiceMailNumber")
        }

        return indicators
    }

    private fun packageIndicators(packageQueries: List<QueryResult>): List<String> {
        return packageQueries.filter {
            it.rawValue == "true" && (
                    it.name.startsWith("LauncherPackagePrefix:") ||
                    it.name.startsWith("PackageManager.hasPackage:") ||
                    it.name.startsWith("RunningServicePrefix:")
                )
        }.map { "${it.name}=${it.displayValue}" }
    }

    private fun openGlIndicators(openGlQueries: List<QueryResult>): List<String> {
        val rendererValue = openGlQueries.findDisplayValue("OpenGL.Renderer")
        val rendererMatch = openGlQueries.findValue("OpenGL.Renderer")
        if (rendererMatch.contains("bluestacks") || rendererMatch.contains("translator")) {
            return listOf("OpenGL.Renderer=$rendererValue")
        }
        return emptyList()
    }

    private fun containsAny(value: String, tokens: List<String>): Boolean {
        return tokens.any { value.contains(it) }
    }

    private fun safeBuildSerial(): String? {
        return try {
            @Suppress("DEPRECATION")
            Build.getSerial()
        } catch (e: Exception) {
            @Suppress("DEPRECATION")
            Build.SERIAL
        }
    }

    private fun List<QueryResult>.findValue(name: String): String {
        return firstOrNull { it.name == name }?.rawValue?.lowercase() ?: ""
    }

    private fun List<QueryResult>.findDisplayValue(name: String): String {
        return firstOrNull { it.name == name }?.displayValue ?: ""
    }

    private fun ensureTelephonyPermissions(): List<String> {
        val permissions = mutableListOf(Manifest.permission.READ_PHONE_STATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions.add(Manifest.permission.READ_PHONE_NUMBERS)
        }

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isEmpty()) {
            return emptyList()
        }

        val activity = context as? Activity ?: return missing
        ActivityCompat.requestPermissions(activity, missing.toTypedArray(), 1001)
        return missing
    }

    private fun telephonyPermissionNote(missingPermissions: List<String>): String {
        if (missingPermissions.isEmpty()) {
            return ""
        }

        val displayNames = missingPermissions.map { it.substringAfterLast('.') }
        return "\n\nGrant ${displayNames.joinToString(", ")} and re-run to read telephony identifiers."
    }
}
