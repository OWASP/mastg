package org.owasp.mastestapp;

/*...*/
import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import kotlin.Metadata;
import kotlin.Unit;
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
    private final String keyAlias = "mastgFileKey";

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    private final SecretKey getOrCreateSecretKey() {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(this.keyAlias)) {
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(this.keyAlias, null)).getSecretKey();
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyGenerator.init(
            new KeyGenParameterSpec.Builder(this.keyAlias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        );
        return keyGenerator.generateKey();
    }

    private final String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey());
        byte[] iv = cipher.getIV();
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(Charsets.UTF_8));
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);
        return Base64.encodeToString(combined, Base64.DEFAULT);
    }

    public final String mastgTest() {
        try {
            String result = "";

            // FAIL: [MASTG-TEST-0x01] The app stores the password unencrypted using openFileOutput, exposing it to attackers with device access.
            FileOutputStream fos1 = this.context.openFileOutput("secret_token.txt", Context.MODE_PRIVATE);
            FileOutputStream output1 = fos1;
            byte[] bytes1 = this.password.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes1, "this as java.lang.String).getBytes(charset)");
            output1.write(bytes1);
            Log.d("FileAPIs", "Written unencrypted password to secret_token.txt");
            CloseableKt.closeFinally(fos1, null);
            result += "[FAIL]: Stored unencrypted password in secret_token.txt using openFileOutput.\n\n";

            // FAIL: [MASTG-TEST-0x01] The app stores the API key unencrypted using FileOutputStream, making it readable by attackers with sandbox access.
            File apiKeyFile = new File(this.context.getFilesDir(), "api_key.txt");
            FileOutputStream fos2 = new FileOutputStream(apiKeyFile);
            FileOutputStream output2 = fos2;
            byte[] bytes2 = this.apiKey.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "this as java.lang.String).getBytes(charset)");
            output2.write(bytes2);
            Log.d("FileAPIs", "Written unencrypted API key to api_key.txt");
            CloseableKt.closeFinally(fos2, null);
            result += "[FAIL]: Stored unencrypted API key in api_key.txt using FileOutputStream.\n\n";

            // PASS: [MASTG-TEST-0x01] The app encrypts the API key with AES-GCM using a KeyStore-backed key before writing, preventing plaintext exposure.
            File encryptedApiKeyFile = new File(this.context.getFilesDir(), "encrypted_api_key.bin");
            FileOutputStream fos3 = new FileOutputStream(encryptedApiKeyFile);
            FileOutputStream output3 = fos3;
            String encryptedApiKey = encrypt(this.apiKey);
            byte[] bytes3 = encryptedApiKey.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes3, "this as java.lang.String).getBytes(charset)");
            output3.write(bytes3);
            Log.d("FileAPIs", "Written encrypted API key to encrypted_api_key.bin");
            CloseableKt.closeFinally(fos3, null);
            result += "[OK]: Stored encrypted API key in encrypted_api_key.bin using FileOutputStream with AES-GCM.\n\n";

            return result;
        } catch (Exception e) {
            return "Error during MastgTest: " + e.getMessage();
        }
    }
}
