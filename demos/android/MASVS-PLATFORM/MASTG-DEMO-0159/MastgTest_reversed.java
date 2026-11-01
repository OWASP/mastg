package org.owasp.mastestapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.savedstate.serialization.ClassDiscriminatorModeKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\f\u001a\u00020\u000bJ\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000bH\u0002J\u0018\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u0016H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u0007X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082D¢\u0006\u0002\n\u0000Ê\u0001\f\b\u0018\u0012\b\b\u0019\u0012\u0004\b\u0003\u0010\u0000¨\u0006\u0017"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "getShouldRunInMainThread", "()Z", "formTag", "", "mastgTest", "closeForm", "", "form", "Landroid/view/View;", "message", "createField", "Landroid/widget/EditText;", "hintText", ClassDiscriminatorModeKt.CLASS_DISCRIMINATOR_KEY, "", "app", "Landroidx/compose/runtime/internal/StabilityInferred;", "parameters"}, k = 1, mv = {2, 4, 0}, xi = 48)
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;
    private final String formTag;
    private final boolean shouldRunInMainThread;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.shouldRunInMainThread = true;
        this.formTag = "mastg-demo-form";
    }

    public final boolean getShouldRunInMainThread() {
        return this.shouldRunInMainThread;
    }

    public final String mastgTest() {
        DemoResults r = new DemoResults("0159");
        if (Build.VERSION.SDK_INT < 34) {
            r.add(Status.ERROR, "Accessibility data sensitivity requires Android 14 (API level 34) or higher.");
            return r.toJson();
        }
        final LinearLayout form = new LinearLayout(this.context);
        form.setTag(this.formTag);
        form.setOrientation(1);
        form.setBackgroundColor(Color.rgb(48, 48, 48));
        form.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda0
            @Override // android.view.View.OnApplyWindowInsetsListener
            public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                return MastgTest.mastgTest$lambda$0$0(view, windowInsets);
            }
        });
        EditText usernameField = createField("Username", 1);
        form.addView(usernameField);
        EditText passwordField = createField("Password", 129);
        form.addView(passwordField);
        EditText otpField = createField("One-time code", 18);
        otpField.setFilterTouchesWhenObscured(true);
        form.addView(otpField);
        LinearLayout paymentContainer = new LinearLayout(this.context);
        paymentContainer.setOrientation(1);
        paymentContainer.setAccessibilityDataSensitive(1);
        EditText cardField = createField("Card number", 2);
        paymentContainer.addView(cardField);
        Button confirmButton = new Button(this.context);
        confirmButton.setText("Confirm Payment");
        confirmButton.setOnClickListener(new View.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MastgTest.mastgTest$lambda$2$0(this.f$0, form, view);
            }
        });
        confirmButton.setAccessibilityDataSensitive(2);
        paymentContainer.addView(confirmButton);
        form.addView(paymentContainer);
        Button button = new Button(this.context);
        button.setText("Cancel");
        button.setOnClickListener(new View.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MastgTest.mastgTest$lambda$3$0(this.f$0, form, view);
            }
        });
        form.addView(button);
        Context context = this.context;
        Activity activity = context instanceof Activity ? (Activity) context : null;
        if (activity != null) {
            ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content);
            View viewFindViewWithTag = viewGroup.findViewWithTag(this.formTag);
            if (viewFindViewWithTag != null) {
                viewGroup.removeView(viewFindViewWithTag);
            }
            viewGroup.addView(form, new FrameLayout.LayoutParams(-1, -2, 80));
            form.requestApplyInsets();
        }
        Iterable<Pair> iterableListOf = CollectionsKt.listOf((Object[]) new Pair[]{TuplesKt.to("Username field", usernameField), TuplesKt.to("Password field", passwordField), TuplesKt.to("One-time code field", otpField), TuplesKt.to("Card number field", cardField), TuplesKt.to("Confirm Payment button", confirmButton)});
        int i = 0;
        for (Pair pair : iterableListOf) {
            String str = (String) pair.component1();
            boolean zIsAccessibilityDataSensitive = ((TextView) pair.component2()).isAccessibilityDataSensitive();
            r.add(zIsAccessibilityDataSensitive ? Status.PASS : Status.FAIL, str + ": isAccessibilityDataSensitive=" + zIsAccessibilityDataSensitive);
            i = i;
            iterableListOf = iterableListOf;
            usernameField = usernameField;
        }
        return r.toJson();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final WindowInsets mastgTest$lambda$0$0(View view, WindowInsets insets) {
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(insets, "insets");
        int bottom = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.ime()).bottom;
        view.setPadding(48, 32, 48, bottom + 32);
        return insets;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$2$0(MastgTest this$0, LinearLayout $form, View it) {
        this$0.closeForm($form, "Payment confirmed.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void mastgTest$lambda$3$0(MastgTest this$0, LinearLayout $form, View it) {
        this$0.closeForm($form, "Payment cancelled.");
    }

    private final void closeForm(View form, String message) {
        ViewParent parent = form.getParent();
        ViewGroup viewGroup = parent instanceof ViewGroup ? (ViewGroup) parent : null;
        if (viewGroup != null) {
            viewGroup.removeView(form);
        }
        Log.i("MASTG-DEMO", message);
        Toast.makeText(this.context, message, 0).show();
    }

    private final EditText createField(String hintText, int type) {
        EditText editText = new EditText(this.context);
        editText.setHint(hintText);
        editText.setInputType(type);
        editText.setTextColor(-1);
        editText.setHintTextColor(-3355444);
        return editText;
    }
}
