package com.attacker.codeexec

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class AttackerMainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Attacker App — Code Execution"
            textSize = 22f
            setPadding(0, 0, 0, 24)
        }
        layout.addView(title)

        val info = TextView(this).apply {
            text = """
                This app intercepts REQUEST_LIBRARY implicit intents and achieves arbitrary code execution via a malicious ContentProvider.

                How it works:
                1. Victim app fires implicit intent with action REQUEST_LIBRARY
                2. This app intercepts it and returns a content:// URI
                3. Victim queries our ContentProvider:
                   - query() returns path-traversal filename "../lib_config.json"
                   - openFile() serves malicious payload content
                4. Victim copies attacker content using the traversal filename
                5. This overwrites the victim's legitimate lib_config.json
                6. In a real attack, a .so file would be overwritten and loaded via System.load()

                Status: Waiting for victim app to fire REQUEST_LIBRARY intent...

                After the attack, verify with:
                  adb shell run-as org.owasp.mastestapp cat files/lib_config.json
                Or check logcat:
                  adb logcat -s MASTG-DEMO ATTACKER
            """.trimIndent()
            textSize = 14f
        }
        layout.addView(info)

        val scrollView = ScrollView(this)
        scrollView.addView(layout)
        setContentView(scrollView)
    }
}
