package org.owasp.mastestapp;

import android.content.Context;
import android.icu.util.GregorianCalendar;
import android.security.keystore.KeyGenParameterSpec;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;

/* compiled from: MastgTest.kt */
@Metadata(m69d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0012\u0010\f\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\rJ\u0010\u0010\u000f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\rJ\u0006\u0010\u0010\u001a\u00020\u0011J\n\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0002J\u0006\u0010\u0014\u001a\u00020\u0006J\u000e\u0010\u0015\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rJ\u0016\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0018\u001a\u00020\rR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"}, m70d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "ALGORITHM", "", "ANDROID_KEYSTORE", "BLOCK_MODE", "KEY_ALIAS", "PADDING", "TRANSFORMATION", "decrypt", "", "data", "encrypt", "generateKey", "Ljava/security/KeyPair;", "getCertificate", "Ljava/security/cert/X509Certificate;", "mastgTest", "sign", "verify", "", "signature", "app_debug"}, m71k = 1, m72mv = {1, 9, 0}, m74xi = 48)
/* loaded from: classes4.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final String ALGORITHM;
    private final String ANDROID_KEYSTORE;
    private final String BLOCK_MODE;
    private final String KEY_ALIAS;
    private final String PADDING;
    private final String TRANSFORMATION;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.ANDROID_KEYSTORE = "AndroidKeyStore";
        this.KEY_ALIAS = "MultiPurposeKey";
        this.ALGORITHM = "RSA";
        this.BLOCK_MODE = "ECB";
        this.PADDING = "PKCS1Padding";
        this.TRANSFORMATION = this.ALGORITHM + '/' + this.BLOCK_MODE + '/' + this.PADDING;
    }

    public final String mastgTest() {
        String str;
        generateKey();
        byte[] data = "secret".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(data, "this as java.lang.String).getBytes(charset)");
        String logs = "Original Data: '" + new String(data, Charsets.UTF_8) + "'\n\n";
        byte[] encryptedData = encrypt(data);
        String encryptedDataPreview = encryptedData != null ? StringsKt.take(new String(encryptedData, Charsets.UTF_8), 7) : null;
        if (encryptedData == null || encryptedDataPreview == null) {
            return "FAILURE - Encryption failed.";
        }
        String logs2 = logs + "Encrypted data preview: " + encryptedDataPreview + "...\n";
        byte[] decryptedData = decrypt(encryptedData);
        String logs3 = logs2 + "Decrypted data: " + (decryptedData != null ? new String(decryptedData, Charsets.UTF_8) : null) + "\n\n";
        byte[] signature = sign(data);
        String signaturePreview = StringsKt.take(new String(signature, Charsets.UTF_8), 10);
        StringBuilder append = new StringBuilder().append(((logs3 + "Signing data...\n") + "Signature preview: " + signaturePreview + "...\n") + "Verifying signature...\n");
        if (verify(data, signature)) {
            str = "Verification result: Signature is correct\n\n";
        } else {
            str = "Verification result: Signature is invalid\n\n";
        }
        String logs4 = append.append(str).toString();
        return logs4 + "SUCCESS!!";
    }

    public final KeyPair generateKey() {
        KeyStore ks = KeyStore.getInstance(this.ANDROID_KEYSTORE);
        ks.load(null);
        if (ks.containsAlias(this.KEY_ALIAS)) {
            ks.deleteEntry(this.KEY_ALIAS);
        }
        GregorianCalendar startDate = new GregorianCalendar();
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.add(1, 1);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", this.ANDROID_KEYSTORE);
        Intrinsics.checkNotNullExpressionValue(keyPairGenerator, "getInstance(...)");
        KeyGenParameterSpec.Builder $this$generateKey_u24lambda_u242 = new KeyGenParameterSpec.Builder(this.KEY_ALIAS, 15);
        $this$generateKey_u24lambda_u242.setCertificateSerialNumber(BigInteger.valueOf(777L));
        $this$generateKey_u24lambda_u242.setCertificateSubject(new X500Principal("CN=" + this.KEY_ALIAS));
        $this$generateKey_u24lambda_u242.setDigests("SHA-256");
        $this$generateKey_u24lambda_u242.setSignaturePaddings("PKCS1");
        $this$generateKey_u24lambda_u242.setEncryptionPaddings("PKCS1Padding");
        $this$generateKey_u24lambda_u242.setCertificateNotBefore(startDate.getTime());
        $this$generateKey_u24lambda_u242.setCertificateNotAfter(endDate.getTime());
        KeyGenParameterSpec parameterSpec = $this$generateKey_u24lambda_u242.build();
        Intrinsics.checkNotNullExpressionValue(parameterSpec, "run(...)");
        keyPairGenerator.initialize(parameterSpec);
        KeyPair genKeyPair = keyPairGenerator.genKeyPair();
        Intrinsics.checkNotNullExpressionValue(genKeyPair, "genKeyPair(...)");
        return genKeyPair;
    }

    public final byte[] encrypt(byte[] data) {
        Intrinsics.checkNotNullParameter(data, "data");
        try {
            X509Certificate cert = getCertificate();
            Intrinsics.checkNotNull(cert);
            Cipher cipher = Cipher.getInstance(this.TRANSFORMATION);
            cipher.init(1, cert.getPublicKey());
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public final byte[] decrypt(byte[] data) {
        try {
            KeyStore ks = KeyStore.getInstance(this.ANDROID_KEYSTORE);
            ks.load(null);
            Key key = ks.getKey(this.KEY_ALIAS, null);
            Intrinsics.checkNotNull(key, "null cannot be cast to non-null type java.security.PrivateKey");
            PrivateKey privateKey = (PrivateKey) key;
            Cipher cipher = Cipher.getInstance(this.TRANSFORMATION);
            cipher.init(2, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public final byte[] sign(byte[] data) {
        Intrinsics.checkNotNullParameter(data, "data");
        KeyStore ks = KeyStore.getInstance(this.ANDROID_KEYSTORE);
        ks.load(null);
        Key key = ks.getKey(this.KEY_ALIAS, null);
        Intrinsics.checkNotNull(key, "null cannot be cast to non-null type java.security.PrivateKey");
        PrivateKey privateKey = (PrivateKey) key;
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data);
        byte[] sign = sig.sign();
        Intrinsics.checkNotNullExpressionValue(sign, "sign(...)");
        return sign;
    }

    public final boolean verify(byte[] data, byte[] signature) {
        Intrinsics.checkNotNullParameter(data, "data");
        Intrinsics.checkNotNullParameter(signature, "signature");
        Signature sig = Signature.getInstance("SHA256withRSA");
        X509Certificate certificate = getCertificate();
        sig.initVerify(certificate != null ? certificate.getPublicKey() : null);
        sig.update(data);
        return sig.verify(signature);
    }

    private final X509Certificate getCertificate() {
        KeyStore ks = KeyStore.getInstance(this.ANDROID_KEYSTORE);
        ks.load(null);
        Certificate certificate = ks.getCertificate(this.KEY_ALIAS);
        if (certificate instanceof X509Certificate) {
            return (X509Certificate) certificate;
        }
        return null;
    }
}
