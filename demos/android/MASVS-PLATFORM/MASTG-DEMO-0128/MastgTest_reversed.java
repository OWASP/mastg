package org.owasp.mastestapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.compose.material.MenuKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.owasp.mastestapp.MastgTest;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001:\u0002\b\tB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "PinEntryActivity", "SecretActivity", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        Context context = this.context;
        Intent $this$mastgTest_u24lambda_u240 = new Intent(this.context, (Class<?>) PinEntryActivity.class);
        $this$mastgTest_u24lambda_u240.addFlags(268435456);
        context.startActivity($this$mastgTest_u24lambda_u240);
        return "Launching PIN entry screen...";
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0014¨\u0006\b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$PinEntryActivity;", "Landroid/app/Activity;", "<init>", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class PinEntryActivity extends Activity {
        public static final int $stable = 0;

        @Override // android.app.Activity
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(1);
            layout.setPadding(64, MenuKt.InTransitionDuration, 64, 64);
            TextView $this$onCreate_u24lambda_u241 = new TextView(this);
            $this$onCreate_u24lambda_u241.setText("MASTestApp - Secure Area");
            $this$onCreate_u24lambda_u241.setTextSize(22.0f);
            TextView $this$onCreate_u24lambda_u242 = new TextView(this);
            $this$onCreate_u24lambda_u242.setText("Enter your PIN to access the secret screen.");
            $this$onCreate_u24lambda_u242.setTextSize(16.0f);
            $this$onCreate_u24lambda_u242.setPadding(0, 24, 0, 48);
            final EditText $this$onCreate_u24lambda_u243 = new EditText(this);
            $this$onCreate_u24lambda_u243.setHint("PIN");
            $this$onCreate_u24lambda_u243.setInputType(18);
            Button $this$onCreate_u24lambda_u245 = new Button(this);
            $this$onCreate_u24lambda_u245.setText("Start");
            $this$onCreate_u24lambda_u245.setOnClickListener(new View.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$PinEntryActivity$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    MastgTest.PinEntryActivity.onCreate$lambda$5$lambda$4($this$onCreate_u24lambda_u243, this, view);
                }
            });
            layout.addView($this$onCreate_u24lambda_u241);
            layout.addView($this$onCreate_u24lambda_u242);
            layout.addView($this$onCreate_u24lambda_u243);
            layout.addView($this$onCreate_u24lambda_u245);
            setContentView(layout);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void onCreate$lambda$5$lambda$4(EditText pinInput, PinEntryActivity this$0, View it) {
            Intrinsics.checkNotNullParameter(pinInput, "$pinInput");
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            if (Intrinsics.areEqual(pinInput.getText().toString(), "4321")) {
                this$0.startActivity(new Intent(this$0, (Class<?>) SecretActivity.class));
            } else {
                new AlertDialog.Builder(this$0).setTitle("Wrong PIN").setMessage("Incorrect PIN. Try again.").setPositiveButton("OK", (DialogInterface.OnClickListener) null).show();
            }
        }
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0014¨\u0006\b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$SecretActivity;", "Landroid/app/Activity;", "<init>", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class SecretActivity extends Activity {
        public static final int $stable = 0;

        @Override // android.app.Activity
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
            StringBuilder $this$onCreate_u24lambda_u240 = new StringBuilder();
            $this$onCreate_u24lambda_u240.append("SECRET SCREEN (reached without authentication)\n\n");
            $this$onCreate_u24lambda_u240.append("Account: 1234-5678-9012-3456\n");
            $this$onCreate_u24lambda_u240.append("Balance: 10,000\n");
            $this$onCreate_u24lambda_u240.append("Recovery PIN: 4321");
            String secret = $this$onCreate_u24lambda_u240.toString();
            ScrollView scrollView = new ScrollView(this);
            TextView $this$onCreate_u24lambda_u241 = new TextView(this);
            $this$onCreate_u24lambda_u241.setText(secret);
            $this$onCreate_u24lambda_u241.setTextSize(28.0f);
            $this$onCreate_u24lambda_u241.setPadding(48, 48, 48, 48);
            scrollView.addView($this$onCreate_u24lambda_u241);
            setContentView(scrollView);
        }
    }
}
