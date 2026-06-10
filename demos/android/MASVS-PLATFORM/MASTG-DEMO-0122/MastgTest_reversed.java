package org.owasp.mastestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001\bB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\t"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "ShareReportActivity", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        DemoResults r = new DemoResults("0122");
        try {
            FilesKt.writeText$default(new File(this.context.getFilesDir(), "session_token.txt"), "sess_7f3a9b1e4d2c8f0a5e6b3c1d9f4a2e7b", null, 2, null);
            new File(this.context.getFilesDir(), "reports").mkdirs();
            FilesKt.writeText$default(new File(this.context.getFilesDir(), "reports/lab_result_3829.pdf"), "%PDF-1.4 stub: cholesterol 195, HbA1c 6.8", null, 2, null);
            r.add(Status.FAIL, "FileProvider path=\".\" exposes all of filesDir. Intended: content://org.owasp.mastestapp.fileprovider/app_files/reports/lab_result_3829.pdf — but session_token.txt is also reachable via the same authority.");
            return r.toJson();
        } catch (Exception e) {
            r.add(Status.ERROR, e.getClass().getSimpleName() + ": " + e.getMessage());
            return r.toJson();
        }
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0014¨\u0006\b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$ShareReportActivity;", "Landroid/app/Activity;", "<init>", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class ShareReportActivity extends Activity {
        public static final int $stable = 0;

        @Override // android.app.Activity
        protected void onCreate(Bundle savedInstanceState) throws XmlPullParserException, IOException {
            super.onCreate(savedInstanceState);
            String requested = getIntent().getStringExtra("file_name");
            if (requested == null) {
                ShareReportActivity $this$onCreate_u24lambda_u240 = this;
                $this$onCreate_u24lambda_u240.setResult(0);
                $this$onCreate_u24lambda_u240.finish();
                return;
            }
            File file = new File(getFilesDir(), requested);
            Uri uri = FileProvider.getUriForFile(this, "org.owasp.mastestapp.fileprovider", file);
            Intent result = new Intent();
            result.setData(uri);
            result.addFlags(1);
            setResult(-1, result);
            finish();
        }
    }
}