package org.owasp.mastestapp;

import android.content.Context;
import android.net.Uri;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0010\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "processTransfer", "amount", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        Uri data = DeepLinkActivity.INSTANCE.getLastDeepLink();
        if (data == null) {
            return "No deep link processed yet.\n\nTrigger the unverified App Link with:\nadb shell am start -n org.owasp.mastestapp/.DeepLinkActivity -a android.intent.action.VIEW -d \"https://deeplink.example.com/transfer?amount=100\"\n\nThen press Start again to see the result.";
        }
        String amountParam = data.getQueryParameter("amount");
        Long amount = amountParam != null ? StringsKt.toLongOrNull(amountParam) : null;
        if (amount == null || amount.longValue() <= 0 || amount.longValue() > 10000) {
            return "Rejected invalid amount: " + amountParam;
        }
        return processTransfer(amount.longValue());
    }

    private final String processTransfer(long amount) {
        return "Transferred " + amount + " units to the linked account";
    }
}
