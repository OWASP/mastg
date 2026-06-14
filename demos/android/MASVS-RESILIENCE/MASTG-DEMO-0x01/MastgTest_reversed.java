package org.owasp.mastestapp;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Base64;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\b\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000¨\u0006\t"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "keyAlias", "", "mastgTest", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;
    private final String keyAlias;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.keyAlias = "mastgAttestationKey";
    }

    public final String mastgTest() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(this.keyAlias)) {
            keyStore.deleteEntry(this.keyAlias);
        }
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "AndroidKeyStore");
        int i = 0;
        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(this.keyAlias, 12).setDigests("SHA-256").setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1")).build();
        Intrinsics.checkNotNullExpressionValue(spec, "build(...)");
        kpg.initialize(spec);
        kpg.generateKeyPair();
        Certificate[] certChain = keyStore.getCertificateChain(this.keyAlias);
        StringBuilder sb = new StringBuilder();
        sb.append("Sending certificate chain to https://example.com/attestation-verify ...").append('\n');
        sb.append("Chain length: " + certChain.length).append('\n');
        Intrinsics.checkNotNull(certChain);
        int index$iv = 0;
        int length = certChain.length;
        int i2 = 0;
        while (i2 < length) {
            String pem = Base64.encodeToString(certChain[i2].getEncoded(), i);
            Intrinsics.checkNotNull(pem);
            sb.append("Certificate[" + index$iv + "] (first 60 chars): " + StringsKt.take(pem, 60) + "...").append('\n');
            i2++;
            index$iv++;
            kpg = kpg;
            i = 0;
        }
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
