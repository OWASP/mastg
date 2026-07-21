package org.owasp.mastestapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.compose.material.MenuKt;
import androidx.core.app.NotificationCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.owasp.mastestapp.MastgTest;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0007\u0018\u0000 \b2\u00020\u0001:\u0003\b\t\nB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "Companion", "VaultActivity", "AuthService", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    public static final String DEFAULT_PASSWORD = "originalPass123";
    public static final String KEY_PASSWORD_STORE = "vault_password";
    public static final String PREFS = "secure_prefs";
    private final Context context;
    public static final int $stable = 8;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        SharedPreferences prefs = this.context.getSharedPreferences(PREFS, 0);
        if (!prefs.contains(KEY_PASSWORD_STORE)) {
            prefs.edit().putString(KEY_PASSWORD_STORE, DEFAULT_PASSWORD).apply();
        }
        Context context = this.context;
        Intent $this$mastgTest_u24lambda_u240 = new Intent(this.context, (Class<?>) VaultActivity.class);
        $this$mastgTest_u24lambda_u240.addFlags(268435456);
        context.startActivity($this$mastgTest_u24lambda_u240);
        return "Opening the password vault…";
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0014J\b\u0010\n\u001a\u00020\u0007H\u0014J\b\u0010\u000b\u001a\u00020\u0007H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$VaultActivity;", "Landroid/app/Activity;", "<init>", "()V", NotificationCompat.CATEGORY_STATUS, "Landroid/widget/TextView;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "showPassword", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class VaultActivity extends Activity {
        public static final int $stable = 8;
        private TextView status;

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
            $this$onCreate_u24lambda_u241.setText("MASTestApp – Password Vault");
            $this$onCreate_u24lambda_u241.setTextSize(22.0f);
            TextView $this$onCreate_u24lambda_u242 = new TextView(this);
            $this$onCreate_u24lambda_u242.setTextSize(18.0f);
            $this$onCreate_u24lambda_u242.setPadding(0, 48, 0, 48);
            this.status = $this$onCreate_u24lambda_u242;
            Button $this$onCreate_u24lambda_u244 = new Button(this);
            $this$onCreate_u24lambda_u244.setText("Refresh");
            $this$onCreate_u24lambda_u244.setOnClickListener(new View.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$VaultActivity$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    MastgTest.VaultActivity.onCreate$lambda$4$lambda$3(this.f$0, view);
                }
            });
            layout.addView($this$onCreate_u24lambda_u241);
            TextView textView = this.status;
            if (textView == null) {
                Intrinsics.throwUninitializedPropertyAccessException(NotificationCompat.CATEGORY_STATUS);
                textView = null;
            }
            layout.addView(textView);
            layout.addView($this$onCreate_u24lambda_u244);
            setContentView(layout);
            showPassword();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void onCreate$lambda$4$lambda$3(VaultActivity this$0, View it) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            this$0.showPassword();
        }

        @Override // android.app.Activity
        protected void onResume() {
            super.onResume();
            showPassword();
        }

        private final void showPassword() {
            String pwd = getSharedPreferences(MastgTest.PREFS, 0).getString(MastgTest.KEY_PASSWORD_STORE, "");
            TextView textView = this.status;
            if (textView == null) {
                Intrinsics.throwUninitializedPropertyAccessException(NotificationCompat.CATEGORY_STATUS);
                textView = null;
            }
            textView.setText("Current vault password:\n\n" + pwd);
        }
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\b\u0007\u0018\u0000 \f2\u00020\u0001:\u0001\fB\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\"\u0010\b\u001a\u00020\t2\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0016¨\u0006\r"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$AuthService;", "Landroid/app/Service;", "<init>", "()V", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onStartCommand", "", "flags", "startId", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class AuthService extends Service {
        public static final int $stable = 0;
        public static final String KEY_PASSWORD = "org.owasp.mastestapp.PASSWORD";

        @Override // android.app.Service
        public IBinder onBind(Intent intent) {
            Intrinsics.checkNotNullParameter(intent, "intent");
            return null;
        }

        @Override // android.app.Service
        public int onStartCommand(Intent intent, int flags, int startId) {
            String newPassword = intent != null ? intent.getStringExtra(KEY_PASSWORD) : null;
            if (newPassword != null) {
                getApplicationContext().getSharedPreferences(MastgTest.PREFS, 0).edit().putString(MastgTest.KEY_PASSWORD_STORE, newPassword).apply();
                return 2;
            }
            return 2;
        }
    }
}
