package org.owasp.mastestapp;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.platform.ComposeView;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.owasp.mastestapp.MastgTest;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\f\u001a\u00020\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000b¨\u0006\u000e"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "shouldRunInMainThread", "", "getShouldRunInMainThread", "()Z", "setShouldRunInMainThread", "(Z)V", "mastgTest", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;
    private boolean shouldRunInMainThread;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
        this.shouldRunInMainThread = true;
    }

    public final boolean getShouldRunInMainThread() {
        return this.shouldRunInMainThread;
    }

    public final void setShouldRunInMainThread(boolean z) {
        this.shouldRunInMainThread = z;
    }

    public final String mastgTest() {
        if (this.context instanceof Activity) {
            ComposeView composeHost = new ComposeView(this.context, null, 0, 6, null);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(-1, -1);
            composeHost.setContent(ComposableLambdaKt.composableLambdaInstance(-1584904752, true, new AnonymousClass1(composeHost)));
            ((Activity) this.context).addContentView(composeHost, lp);
            return "SUCCESS!!\n\nThe Compose dialog should be visible.\n\nIt has SecureFlagPolicy.SecureOff set, so it should appear in screenshots or the recent apps view.";
        }
        return "ERROR: Context is not an Activity";
    }

    /* compiled from: MastgTest.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    /* renamed from: org.owasp.mastestapp.MastgTest$mastgTest$1, reason: invalid class name */
    static final class AnonymousClass1 implements Function2<Composer, Integer, Unit> {
        final /* synthetic */ ComposeView $composeHost;

        AnonymousClass1(ComposeView composeView) {
            this.$composeHost = composeView;
        }

        @Override // kotlin.jvm.functions.Function2
        public /* bridge */ /* synthetic */ Unit invoke(Composer composer, Integer num) {
            invoke(composer, num.intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C34@1210L220:MastgTest.kt#vyvp3i");
            if (($changed & 11) != 2 || !$composer.getSkipping()) {
                final ComposeView composeView = this.$composeHost;
                MastgTestKt.SecureComposeDialog(new Function0() { // from class: org.owasp.mastestapp.MastgTest$mastgTest$1$$ExternalSyntheticLambda0
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MastgTest.AnonymousClass1.invoke$lambda$0(composeView);
                    }
                }, $composer, 0);
            } else {
                $composer.skipToGroupEnd();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Unit invoke$lambda$0(ComposeView composeHost) {
            Intrinsics.checkNotNullParameter(composeHost, "$composeHost");
            ViewParent parent = composeHost.getParent();
            ViewGroup parent2 = parent instanceof ViewGroup ? (ViewGroup) parent : null;
            if (parent2 != null) {
                parent2.removeView(composeHost);
            }
            return Unit.INSTANCE;
        }
    }
}
