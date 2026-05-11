package org.owasp.mastestapp;

import android.content.Context;
import android.util.Log;
import androidx.autofill.HintConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\t\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", HintConstants.AUTOFILL_HINT_PASSWORD, "", "apiKey", "mastgTest", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final String apiKey;
    private final Context context;
    private final String password;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.password = "MyS3cr3tP4ssw0rd";
        this.apiKey = "AKIAABCDEFGHIJKLMNOP";
    }

    public final String mastgTest() throws FileNotFoundException {
        try {
            FileOutputStream fileOutputStreamOpenFileOutput = this.context.openFileOutput("secret_token.txt", 0);
            try {
                FileOutputStream output = fileOutputStreamOpenFileOutput;
                byte[] bytes = this.password.getBytes(Charsets.UTF_8);
                Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                output.write(bytes);
                Log.d("FileAPIs", "Written unencrypted password to secret_token.txt");
                CloseableKt.closeFinally(fileOutputStreamOpenFileOutput, null);
                String result = "[FAIL]: Stored unencrypted password in secret_token.txt using openFileOutput.\n\n";
                File apiKeyFile = new File(this.context.getFilesDir(), "api_key.txt");
                fileOutputStreamOpenFileOutput = new FileOutputStream(apiKeyFile);
                try {
                    FileOutputStream output2 = fileOutputStreamOpenFileOutput;
                    byte[] bytes2 = this.apiKey.getBytes(Charsets.UTF_8);
                    Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                    output2.write(bytes2);
                    Log.d("FileAPIs", "Written unencrypted API key to api_key.txt");
                    CloseableKt.closeFinally(fileOutputStreamOpenFileOutput, null);
                    return result + "[FAIL]: Stored unencrypted API key in api_key.txt using FileOutputStream.\n\n";
                } finally {
                }
            } finally {
                try {
                    throw th;
                } finally {
                }
            }
        } catch (IOException e) {
            String message = e.getMessage();
            if (message == null) {
                message = "Unknown error";
            }
            return "Error during MastgTest: " + message;
        }
    }
}
