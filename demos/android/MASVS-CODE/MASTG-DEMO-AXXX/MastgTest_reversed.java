package org.owasp.mastestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u0013\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0006\u0010\b\u001a\u00020\tJ\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0015R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "Landroidx/appcompat/app/AppCompatActivity;", "runnerContext", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "mastgTest", "", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest extends AppCompatActivity {
    private final Context runnerContext;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    public static final int $stable = 8;
    private static final List<Pair<String, String>> dynamicResults = new ArrayList();

    /* JADX WARN: Multi-variable type inference failed */
    public MastgTest() {
        this(null, 1, 0 == true ? 1 : 0);
    }

    public MastgTest(Context runnerContext) {
        this.runnerContext = runnerContext;
    }

    public /* synthetic */ MastgTest(Context context, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : context);
    }

    /* compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R#\u0010\u0004\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$Companion;", "", "<init>", "()V", "dynamicResults", "", "Lkotlin/Pair;", "", "getDynamicResults", "()Ljava/util/List;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final List<Pair<String, String>> getDynamicResults() {
            return MastgTest.dynamicResults;
        }
    }

    public final boolean shouldRunInMainThread() {
        return false;
    }

    public final String mastgTest() throws JSONException, InterruptedException {
        dynamicResults.clear();
        Context context = this.runnerContext;
        if (context == null) {
            return "Error: No Context provided";
        }
        Intent intent = new Intent(context, (Class<?>) MastgTest.class);
        intent.addFlags(268435456);
        context.startActivity(intent);
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();
        if (dynamicResults.isEmpty()) {
            JSONObject obj = new JSONObject();
            obj.put(NotificationCompat.CATEGORY_STATUS, "ERROR");
            obj.put("message", "Activity failed to report results.");
            obj.put("demoId", "1");
            jsonArray.put(obj);
        } else {
            for (Pair<String, String> pair : dynamicResults) {
                String status = pair.component1();
                String message = pair.component2();
                JSONObject obj2 = new JSONObject();
                obj2.put(NotificationCompat.CATEGORY_STATUS, status);
                obj2.put("message", message);
                obj2.put("demoId", "1");
                jsonArray.put(obj2);
            }
        }
        String string = jsonArray.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    /* JADX WARN: Type inference failed for: r4v4, types: [org.owasp.mastestapp.MastgTest$onCreate$loginButton$1] */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setBackgroundColor(-1);
        setContentView(rootLayout);
        LinearLayout loginContainer = new LinearLayout(this);
        loginContainer.setOrientation(1);
        loginContainer.setGravity(17);
        loginContainer.setPadding(60, 60, 60, 60);
        loginContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        EditText editText = new EditText(this);
        editText.setHint("Enter PIN");
        editText.setInputType(18);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(0, 0, 0, 50);
        editText.setLayoutParams(layoutParams);
        ?? r4 = new AppCompatButton(this) { // from class: org.owasp.mastestapp.MastgTest$onCreate$loginButton$1
            {
                super(this);
            }

            @Override // android.view.View
            public boolean onFilterTouchEventForSecurity(MotionEvent event) {
                Intrinsics.checkNotNullParameter(event, "event");
                if ((event.getFlags() & 1) != 0 || (event.getFlags() & 2) != 0) {
                    Toast.makeText(getContext(), "Protected! Touch blocked by onFilterTouchEventForSecurity.", 1).show();
                    Context context = getContext();
                    Activity activity = context instanceof Activity ? (Activity) context : null;
                    if (activity != null) {
                        activity.finish();
                        return false;
                    }
                    return false;
                }
                return super.onFilterTouchEventForSecurity(event);
            }
        };
        r4.setText("Login (Secure)");
        r4.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        loginContainer.addView(editText);
        loginContainer.addView((View) r4);
        rootLayout.addView(loginContainer);
        r4.setOnClickListener(new View.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MastgTest.onCreate$lambda$3(this.f$0, view);
            }
        });
        dynamicResults.add(new Pair<>("PASS", "Secure: Custom onFilterTouchEventForSecurity is implemented via anonymous class to block obscured touches."));
        View overlayView = new View(this);
        overlayView.setBackgroundColor(Color.parseColor("#99FF0000"));
        overlayView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        overlayView.setClickable(false);
        overlayView.setFocusable(false);
        addContentView(overlayView, overlayView.getLayoutParams());
        TextView warningText = new TextView(this);
        warningText.setText("SECURE MODE\nTouches blocked when obscured");
        warningText.setTextColor(-1);
        warningText.setTextSize(20.0f);
        warningText.setGravity(17);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-1, -2);
        layoutParams2.gravity = 48;
        layoutParams2.setMargins(0, 100, 0, 0);
        warningText.setLayoutParams(layoutParams2);
        addContentView(warningText, warningText.getLayoutParams());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$3(MastgTest this$0, View it) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Toast.makeText(this$0, "Login Clicked", 0).show();
    }
}
