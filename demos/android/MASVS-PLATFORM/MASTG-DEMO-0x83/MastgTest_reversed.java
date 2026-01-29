package org.owasp.mastestapp;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import kotlin.jvm.internal.Intrinsics;

public final class MastgTest {
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        LinearLayout layout = new LinearLayout(this.context);
        layout.setOrientation(1);
        
        // FAIL: [MASTG-TEST-0x35] Sensitive button without overlay protection
        Button vulnerableButton = new Button(this.context);
        vulnerableButton.setText("Vulnerable: Confirm Payment");
        vulnerableButton.setOnClickListener(view -> {
            // Sensitive action: confirming a payment
        });
        layout.addView(vulnerableButton);
        
        // PASS: [MASTG-TEST-0x35] Button with overlay protection using filterTouchesWhenObscured
        Button protectedButton = new Button(this.context);
        protectedButton.setText("Protected: Confirm Payment");
        protectedButton.setFilterTouchesWhenObscured(true);
        protectedButton.setOnClickListener(view -> {
            // Sensitive action protected from overlay attacks
        });
        layout.addView(protectedButton);
        
        // PASS: [MASTG-TEST-0x35] Custom view with manual obscured check
        Button customProtectedButton = new Button(this.context) {
            public boolean onFilterTouchEventForSecurity(MotionEvent event) {
                if ((event.getFlags() & MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0) {
                    // Window is obscured, filter the touch event
                    return false;
                }
                return super.onFilterTouchEventForSecurity(event);
            }
        };
        customProtectedButton.setText("Custom Protection: Grant Permission");
        customProtectedButton.setOnClickListener(view -> {
            // Sensitive permission grant protected by custom implementation
        });
        layout.addView(customProtectedButton);
        
        return "Created buttons with various overlay protections:\n" +
               "1. Vulnerable button (no protection)\n" +
               "2. Protected button (filterTouchesWhenObscured)\n" +
               "3. Custom protected button (onFilterTouchEventForSecurity)";
    }
}
