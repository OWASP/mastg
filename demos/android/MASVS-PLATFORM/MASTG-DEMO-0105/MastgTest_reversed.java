package org.owasp.mastestapp.pass;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\n\u001a\u00020\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u0007X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\f"}, d2 = {"Lorg/owasp/mastestapp/pass/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "getShouldRunInMainThread", "()Z", "mastgTest", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;
    private final boolean shouldRunInMainThread;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.shouldRunInMainThread = true;
    }

    public final boolean getShouldRunInMainThread() {
        return this.shouldRunInMainThread;
    }

    public final String mastgTest() {
        LinearLayout layout = new LinearLayout(this.context);
        layout.setOrientation(1);
        layout.setGravity(17);
        layout.setBackgroundColor(0);
        int halfWidth = (int) (this.context.getResources().getDisplayMetrics().widthPixels * 0.5d);
        LinearLayout.LayoutParams $this$mastgTest_u24lambda_u241 = new LinearLayout.LayoutParams(halfWidth, -2);
        $this$mastgTest_u24lambda_u241.gravity = 1;
        $this$mastgTest_u24lambda_u241.topMargin = 24;
        final Button $this$mastgTest_u24lambda_u243 = new Button(this.context);
        $this$mastgTest_u24lambda_u243.setText("Confirm Payment");
        $this$mastgTest_u24lambda_u243.setOnClickListener(new View.OnClickListener() { // from class: org.owasp.mastestapp.pass.MastgTest$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MastgTest.mastgTest$lambda$3$lambda$2($this$mastgTest_u24lambda_u243, view);
            }
        });
        layout.addView($this$mastgTest_u24lambda_u243, $this$mastgTest_u24lambda_u241);
        Context context = this.context;
        Activity activity = context instanceof Activity ? (Activity) context : null;
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= 31) {
                activity.getWindow().setHideOverlayWindows(true);
            }
            ViewGroup root = (ViewGroup) activity.findViewById(android.R.id.content);
            if (layout.getParent() == null) {
                root.addView(layout, new FrameLayout.LayoutParams(-1, -1));
                return "Created button with activity level overlay protection";
            }
            return "Created button with activity level overlay protection";
        }
        return "Created button with activity level overlay protection";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$3$lambda$2(Button this_apply, View it) {
        Intrinsics.checkNotNullParameter(this_apply, "$this_apply");
        Toast.makeText(this_apply.getContext(), "Button clicked", 0).show();
    }
}
