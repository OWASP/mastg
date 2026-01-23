package org.owasp.mastestapp;

import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u001a\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\u0010\u0004\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"MASTG_TEXT_TAG", "", "MainScreen", "", "(Landroidx/compose/runtime/Composer;I)V", "app_debug"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MainActivityKt {
    public static final String MASTG_TEXT_TAG = "mastgTestText";

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$0(int i, Composer composer, int i2) {
        MainScreen(composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    public static final void MainScreen(Composer $composer, final int $changed) {
        Composer $composer2 = $composer.startRestartGroup(2010408835);
        ComposerKt.sourceInformation($composer2, "C(MainScreen)34@1094L546:MainActivity.kt#vyvp3i");
        if ($changed != 0 || !$composer2.getSkipping()) {
            BaseScreenKt.BaseScreen(null, ComposableSingletons$MainActivityKt.INSTANCE.m10514getLambda4$app_debug(), $composer2, 48, 1);
        } else {
            $composer2.skipToGroupEnd();
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = $composer2.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.MainScreen$lambda$0($changed, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }
}
