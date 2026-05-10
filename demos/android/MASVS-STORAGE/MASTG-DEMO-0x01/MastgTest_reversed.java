package org.owasp.mastestapp;

/*...*/
import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
/* compiled from: MastgTest.kt */
@Metadata(d1 = {}, k = 1, mv = {1, 9, 0}, xi = 48)
/* loaded from: classes4.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;
    private final String password = "MyS3cr3tP4ssw0rd";
    private final String apiKey = "AKIAABCDEFGHIJKLMNOP";

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        try {
            String result = "";

            // FAIL: [MASTG-TEST-0x01] The app stores the password unencrypted using openFileOutput, exposing it to attackers with device access.
            FileOutputStream tokenOutputStream = this.context.openFileOutput("secret_token.txt", Context.MODE_PRIVATE);
            byte[] passwordBytes = this.password.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(passwordBytes, "this as java.lang.String).getBytes(charset)");
            tokenOutputStream.write(passwordBytes);
            Log.d("FileAPIs", "Written unencrypted password to secret_token.txt");
            CloseableKt.closeFinally(tokenOutputStream, null);
            result += "[FAIL]: Stored unencrypted password in secret_token.txt using openFileOutput.\n\n";

            // FAIL: [MASTG-TEST-0x01] The app stores the API key unencrypted using FileOutputStream, making it readable by attackers with sandbox access.
            FileOutputStream apiKeyOutputStream = new FileOutputStream(new File(this.context.getFilesDir(), "api_key.txt"));
            byte[] apiKeyBytes = this.apiKey.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(apiKeyBytes, "this as java.lang.String).getBytes(charset)");
            apiKeyOutputStream.write(apiKeyBytes);
            Log.d("FileAPIs", "Written unencrypted API key to api_key.txt");
            CloseableKt.closeFinally(apiKeyOutputStream, null);
            result += "[FAIL]: Stored unencrypted API key in api_key.txt using FileOutputStream.\n\n";

            return result;
        } catch (IOException e) {
            return "Error during MastgTest: " + e.getMessage();
        }
    }
}
