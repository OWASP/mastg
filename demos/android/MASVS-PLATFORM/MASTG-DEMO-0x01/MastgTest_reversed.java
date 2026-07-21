package org.owasp.mastestapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.compose.material.MenuKt;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.owasp.mastestapp.MastgTest;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0007\u0018\u0000 \b2\u00020\u0001:\u0006\b\t\n\u000b\f\rB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "Companion", "VaultActivity", "AdminActivity", "PasswordResetReceiver", "VaultRefreshReceiver", "AdminCommandReceiver", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    public static final String ACTION_ADMIN_COMMAND = "org.owasp.mastestapp.ADMIN_COMMAND";
    public static final String ACTION_RESET_PASSWORD = "org.owasp.mastestapp.RESET_PASSWORD";
    public static final String ACTION_VAULT_UPDATED = "org.owasp.mastestapp.VAULT_UPDATED";
    public static final String DEFAULT_PASSWORD = "originalPass123";
    public static final String KEY_PASSWORD_STORE = "vault_password";
    public static final String PERMISSION_ADMIN_COMMAND = "org.owasp.mastestapp.ADMIN_COMMAND_PERMISSION";
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
    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014J\b\u0010\u000e\u001a\u00020\u000bH\u0014J\b\u0010\u000f\u001a\u00020\u000bH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$VaultActivity;", "Landroid/app/Activity;", "<init>", "()V", NotificationCompat.CATEGORY_STATUS, "Landroid/widget/TextView;", "passwordResetReceiver", "Lorg/owasp/mastestapp/MastgTest$PasswordResetReceiver;", "vaultRefreshReceiver", "Lorg/owasp/mastestapp/MastgTest$VaultRefreshReceiver;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "showPassword", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class VaultActivity extends Activity {
        public static final int $stable = 8;
        private TextView status;
        private final PasswordResetReceiver passwordResetReceiver = new PasswordResetReceiver();
        private final VaultRefreshReceiver vaultRefreshReceiver = new VaultRefreshReceiver();

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
            Button $this$onCreate_u24lambda_u246 = new Button(this);
            $this$onCreate_u24lambda_u246.setText("Admin");
            $this$onCreate_u24lambda_u246.setOnClickListener(new View.OnClickListener() { // from class: org.owasp.mastestapp.MastgTest$VaultActivity$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    MastgTest.VaultActivity.onCreate$lambda$6$lambda$5(this.f$0, view);
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
            layout.addView($this$onCreate_u24lambda_u246);
            setContentView(layout);
            ContextCompat.registerReceiver(this, this.passwordResetReceiver, new IntentFilter(MastgTest.ACTION_RESET_PASSWORD), 2);
            ContextCompat.registerReceiver(this, this.vaultRefreshReceiver, new IntentFilter(MastgTest.ACTION_VAULT_UPDATED), 4);
            showPassword();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void onCreate$lambda$4$lambda$3(VaultActivity this$0, View it) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            this$0.showPassword();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void onCreate$lambda$6$lambda$5(VaultActivity this$0, View it) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            this$0.startActivity(new Intent(this$0, (Class<?>) AdminActivity.class));
        }

        @Override // android.app.Activity
        protected void onDestroy() {
            super.onDestroy();
            unregisterReceiver(this.passwordResetReceiver);
            unregisterReceiver(this.vaultRefreshReceiver);
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
    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0014J\b\u0010\n\u001a\u00020\u0007H\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$AdminActivity;", "Landroid/app/Activity;", "<init>", "()V", "adminCommandReceiver", "Lorg/owasp/mastestapp/MastgTest$AdminCommandReceiver;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class AdminActivity extends Activity {
        public static final int $stable = 0;
        private final AdminCommandReceiver adminCommandReceiver = new AdminCommandReceiver();

        @Override // android.app.Activity
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
            String recoveryKey = getSharedPreferences(MastgTest.PREFS, 0).getString(MastgTest.KEY_PASSWORD_STORE, "");
            TextView $this$onCreate_u24lambda_u240 = new TextView(this);
            $this$onCreate_u24lambda_u240.setText("ADMIN CONSOLE\n\nVault recovery key: " + recoveryKey);
            $this$onCreate_u24lambda_u240.setTextSize(18.0f);
            $this$onCreate_u24lambda_u240.setPadding(64, MenuKt.InTransitionDuration, 64, 64);
            setContentView($this$onCreate_u24lambda_u240);
            ContextCompat.registerReceiver(this, this.adminCommandReceiver, new IntentFilter(MastgTest.ACTION_ADMIN_COMMAND), MastgTest.PERMISSION_ADMIN_COMMAND, null, 2);
        }

        @Override // android.app.Activity
        protected void onDestroy() {
            super.onDestroy();
            unregisterReceiver(this.adminCommandReceiver);
        }
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$PasswordResetReceiver;", "Landroid/content/BroadcastReceiver;", "<init>", "()V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class PasswordResetReceiver extends BroadcastReceiver {
        public static final int $stable = 0;

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(intent, "intent");
            String newPassword = intent.getStringExtra("newpass");
            if (newPassword == null) {
                return;
            }
            SharedPreferences prefs = context.getSharedPreferences(MastgTest.PREFS, 0);
            String oldPassword = prefs.getString(MastgTest.KEY_PASSWORD_STORE, "");
            Log.d("MASTG-DEMO", "Password changed from " + oldPassword + " to " + newPassword);
            prefs.edit().putString(MastgTest.KEY_PASSWORD_STORE, newPassword).apply();
        }
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$VaultRefreshReceiver;", "Landroid/content/BroadcastReceiver;", "<init>", "()V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class VaultRefreshReceiver extends BroadcastReceiver {
        public static final int $stable = 0;

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(intent, "intent");
            Log.d("MASTG-DEMO", "Vault updated event received, refreshing UI");
        }
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$AdminCommandReceiver;", "Landroid/content/BroadcastReceiver;", "<init>", "()V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class AdminCommandReceiver extends BroadcastReceiver {
        public static final int $stable = 0;

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(intent, "intent");
            Log.d("MASTG-DEMO", "AdminCommandReceiver received broadcast: " + intent.getAction());
            if (Intrinsics.areEqual(intent.getStringExtra("command"), "wipe")) {
                context.getSharedPreferences(MastgTest.PREFS, 0).edit().remove(MastgTest.KEY_PASSWORD_STORE).apply();
                Log.d("MASTG-DEMO", "Vault wiped by admin command");
            }
        }
    }
}
