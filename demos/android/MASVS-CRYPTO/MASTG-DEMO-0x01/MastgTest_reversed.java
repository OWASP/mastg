package org.owasp.mastestapp;

import android.content.Context;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\u0010\u0012\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\f\u0010\u0006\u001a\u00020\u0007*\u00020\bH\u0002J\u0006\u0010\t\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "toHex", "", "", "mastgTest", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    private final String toHex(byte[] $this$toHex) {
        return ArraysKt.joinToString$default($this$toHex, (CharSequence) "", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, new Function1() { // from class: org.owasp.mastestapp.MastgTest$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return MastgTest.toHex$lambda$0(((Byte) obj).byteValue());
            }
        }, 30, (Object) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence toHex$lambda$0(byte it) {
        String str = String.format("%02x", Arrays.copyOf(new Object[]{Byte.valueOf(it)}, 1));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        return str;
    }

    public final String mastgTest() throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = "superSecretPassword!".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
        byte[] bArrDigest = md5.digest(bytes);
        Intrinsics.checkNotNullExpressionValue(bArrDigest, "digest(...)");
        String md5Password = toHex(bArrDigest);
        Log.d("MASTG-TEST", "MD5 password hash: " + md5Password);
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] bytes2 = "session-token-user-42".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
        byte[] bArrDigest2 = sha1.digest(bytes2);
        Intrinsics.checkNotNullExpressionValue(bArrDigest2, "digest(...)");
        String sha1Token = toHex(bArrDigest2);
        Log.d("MASTG-TEST", "SHA-1 token hash: " + sha1Token);
        MessageDigest sha1Alias = MessageDigest.getInstance("SHA1");
        byte[] bytes3 = "session-token-user-42".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
        byte[] bArrDigest3 = sha1Alias.digest(bytes3);
        Intrinsics.checkNotNullExpressionValue(bArrDigest3, "digest(...)");
        String sha1AliasToken = toHex(bArrDigest3);
        Log.d("MASTG-TEST", "SHA1 alias token hash: " + sha1AliasToken);
        MessageDigest md5ChecksumDigest = MessageDigest.getInstance("MD5");
        byte[] bytes4 = "readme.txt".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes4, "getBytes(...)");
        byte[] bArrDigest4 = md5ChecksumDigest.digest(bytes4);
        Intrinsics.checkNotNullExpressionValue(bArrDigest4, "digest(...)");
        String md5Checksum = toHex(bArrDigest4);
        Log.d("MASTG-TEST", "MD5 checksum: " + md5Checksum);
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] bytes5 = "superSecretPassword!".getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue(bytes5, "getBytes(...)");
        byte[] bArrDigest5 = sha256.digest(bytes5);
        Intrinsics.checkNotNullExpressionValue(bArrDigest5, "digest(...)");
        String sha256Password = toHex(bArrDigest5);
        Log.d("MASTG-TEST", "SHA-256 password hash: " + sha256Password);
        StringBuilder $this$mastgTest_u24lambda_u241 = new StringBuilder();
        $this$mastgTest_u24lambda_u241.append("MD5 password hash: " + md5Password).append('\n');
        $this$mastgTest_u24lambda_u241.append("SHA-1 token hash: " + sha1Token).append('\n');
        $this$mastgTest_u24lambda_u241.append("SHA1 alias token hash: " + sha1AliasToken).append('\n');
        $this$mastgTest_u24lambda_u241.append("MD5 checksum: " + md5Checksum).append('\n');
        $this$mastgTest_u24lambda_u241.append("SHA-256 password hash: " + sha256Password).append('\n');
        return $this$mastgTest_u24lambda_u241.toString();
    }
}
