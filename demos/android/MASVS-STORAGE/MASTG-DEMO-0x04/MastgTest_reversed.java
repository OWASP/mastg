package org.owasp.mastestapp;

import android.content.Context;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u0000 \b2\u00020\u0001:\u0001\bB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\t"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    private static final String AWS_ACCESS_KEY_ID = "AKIAIOSFODNN7EXAMPLE";
    private static final String MAPS_API_KEY = "AIzaSyDFakeMastgDemoKeyNotARealKey12345";
    private final Context context;
    public static final int $stable = 8;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        String resourceKey = this.context.getString(R.string.maps_api_key);
        Intrinsics.checkNotNullExpressionValue(resourceKey, "getString(...)");
        StringBuilder sb = new StringBuilder();
        sb.append("Endpoint: https://api.example.com/v1/report").append('\n');
        sb.append("Maps API key (code): AIzaSyDFakeMastgDemoKeyNotARealKey12345").append('\n');
        sb.append("Maps API key (resources): " + resourceKey).append('\n');
        sb.append("AWS access key id: AKIAIOSFODNN7EXAMPLE").append('\n');
        sb.append("Client secret: s3cr3t-not-a-real-value-9f2b").append('\n');
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
