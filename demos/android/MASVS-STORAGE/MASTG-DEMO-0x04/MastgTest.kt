package org.owasp.mastestapp

import android.content.Context

class MastgTest(private val context: Context) {

    companion object {
        // INSECURE: third-party API key embedded in the app package.
        private const val MAPS_API_KEY = "AIzaSyDFakeMastgDemoKeyNotARealKey12345"

        // INSECURE: cloud provider credential embedded in the app package.
        private const val AWS_ACCESS_KEY_ID = "AKIAIOSFODNN7EXAMPLE"
    }

    fun mastgTest(): String {
        // INSECURE: secret assigned to a credential-named local variable.
        val clientSecret = "s3cr3t-not-a-real-value-9f2b"

        // INSECURE: the key is also duplicated in res/values/strings.xml,
        // where it survives even if the code is obfuscated.
        val resourceKey = context.getString(R.string.maps_api_key)

        val endpoint = "https://api.example.com/v1/report"

        val sb = StringBuilder()
        sb.appendLine("Endpoint: $endpoint")
        sb.appendLine("Maps API key (code): $MAPS_API_KEY")
        sb.appendLine("Maps API key (resources): $resourceKey")
        sb.appendLine("AWS access key id: $AWS_ACCESS_KEY_ID")
        sb.appendLine("Client secret: $clientSecret")
        return sb.toString()
    }
}
