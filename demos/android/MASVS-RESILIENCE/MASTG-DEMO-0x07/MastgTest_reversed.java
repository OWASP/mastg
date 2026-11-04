package org.owasp.mastestapp;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\t\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "keyAlias", "", "attestationOid", "mastgTest", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    public static final int $stable = 8;
    private final String attestationOid;
    private final Context context;
    private final String keyAlias;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.keyAlias = "mastgDeviceAttestationKey";
        this.attestationOid = "1.3.6.1.4.1.11129.2.1.17";
    }

    public final String mastgTest() throws NoSuchAlgorithmException, UnrecoverableKeyException, IOException, KeyStoreException, CertificateException, NoSuchProviderException, InvalidAlgorithmParameterException {
        String level;
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(this.keyAlias)) {
            keyStore.deleteEntry(this.keyAlias);
        }
        byte[] challenge = "server-issued-nonce".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(challenge, "getBytes(...)");
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "AndroidKeyStore");
        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(this.keyAlias, 12).setDigests("SHA-256").setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1")).setAttestationChallenge(challenge).build();
        Intrinsics.checkNotNullExpressionValue(spec, "build(...)");
        kpg.initialize(spec);
        kpg.generateKeyPair();
        Certificate[] certChain = keyStore.getCertificateChain(this.keyAlias);
        StringBuilder sb = new StringBuilder();
        sb.append("Chain length: " + certChain.length).append('\n');
        Certificate certificate = certChain[0];
        Intrinsics.checkNotNull(certificate, "null cannot be cast to non-null type java.security.cert.X509Certificate");
        X509Certificate leaf = (X509Certificate) certificate;
        byte[] extension = leaf.getExtensionValue(this.attestationOid);
        if (extension == null) {
            sb.append("Attestation extension (" + this.attestationOid + "): ABSENT").append('\n');
            sb.append("No device integrity signals are available in this chain.").append('\n');
        } else {
            sb.append("Attestation extension (" + this.attestationOid + "): present, " + extension.length + " bytes").append('\n');
            sb.append("This extension carries rootOfTrust (verifiedBootState, verifiedBootKey,").append('\n');
            sb.append("deviceLocked) and attestationSecurityLevel.").append('\n');
        }
        Key key = keyStore.getKey(this.keyAlias, null);
        Intrinsics.checkNotNull(key, "null cannot be cast to non-null type java.security.PrivateKey");
        PrivateKey privateKey = (PrivateKey) key;
        KeyFactory factory = KeyFactory.getInstance(privateKey.getAlgorithm(), "AndroidKeyStore");
        KeyInfo keyInfo = (KeyInfo) factory.getKeySpec(privateKey, KeyInfo.class);
        switch (keyInfo.getSecurityLevel()) {
            case 0:
                level = "Software";
                break;
            case 1:
                level = "TrustedEnvironment";
                break;
            case 2:
                level = "StrongBox";
                break;
            default:
                level = "Unknown (" + keyInfo.getSecurityLevel() + ")";
                break;
        }
        sb.append("Key security level: " + level).append('\n');
        sb.append('\n');
        sb.append("Sending chain to https://example.com/attestation-verify for evaluation ...").append('\n');
        sb.append("The server, not the app, evaluates rootOfTrust and attestationSecurityLevel.").append('\n');
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
