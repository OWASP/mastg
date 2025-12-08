package org.owasp.mastestapp;

import android.content.Context;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Iterator;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.CharsKt;
import kotlin.text.Charsets;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\u0010\u0012\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\f\u0010\u0006\u001a\u00020\u0007*\u00020\bH\u0002J\u0006\u0010\t\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "toHex", "", "", "mastgTest", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    private final String toHex(byte[] $this$toHex) {
        StringBuilder sb = new StringBuilder($this$toHex.length * 2);
        for (byte b : $this$toHex) {
            int v = b & UByte.MAX_VALUE;
            if (v < 16) {
                sb.append('0');
            }
            String string = Integer.toString(v, CharsKt.checkRadix(16));
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
            sb.append(string);
        }
        String string2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string2, "toString(...)");
        return string2;
    }

    public final String mastgTest() throws BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchProviderException, InvalidAlgorithmParameterException {
        StringBuilder results = new StringBuilder();
        Security.addProvider(new CustomProvider());
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        results.append("KeyStore provider: " + keyStore.getProvider().getName() + "\n");
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        results.append("Default KeyGenerator provider: " + keyGen.getProvider().getName() + "\n");
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        results.append("Default MessageDigest provider: " + digest.getProvider().getName() + "\n");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        results.append("Default Cipher provider: " + cipher.getProvider().getName() + "\n");
        Cipher cipherBC = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        results.append("Default Cipher provider: " + cipherBC.getProvider().getName() + "\n");
        byte[] keyBytes = new byte[16];
        new SecureRandom().nextBytes(keyBytes);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipherBC.init(1, key, gcmSpec);
        byte[] plaintext = "Hello MAS".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(plaintext, "getBytes(...)");
        byte[] ciphertext = cipherBC.doFinal(plaintext);
        Intrinsics.checkNotNull(ciphertext);
        results.append("\n\nCiphertext: " + toHex(ciphertext) + "\n");
        Cipher cipherCustom = Cipher.getInstance("AES/CBC/PKCS5Padding", "CustomProvider");
        cipherCustom.init(1, key, gcmSpec);
        byte[] plaintext2 = "Hello MAS".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(plaintext2, "getBytes(...)");
        byte[] ciphertext2 = cipherCustom.doFinal(plaintext2);
        Intrinsics.checkNotNull(ciphertext2);
        results.append("\n\nCiphertext: " + toHex(ciphertext2) + "\n\n---------------\n");
        StringBuilder builder = new StringBuilder();
        for (Iterator it = ArrayIteratorKt.iterator(Security.getProviders()); it.hasNext(); it = it) {
            Provider provider = (Provider) it.next();
            Cipher cipherCustom2 = cipherCustom;
            builder.append("provider: " + provider.getName() + " " + provider.getVersion() + " (" + provider.getInfo() + ")\n");
            cipherCustom = cipherCustom2;
            plaintext2 = plaintext2;
            ciphertext2 = ciphertext2;
        }
        String providers = builder.toString();
        Intrinsics.checkNotNullExpressionValue(providers, "toString(...)");
        results.append("\n\nProvider list: " + providers + "\n");
        String string = results.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
