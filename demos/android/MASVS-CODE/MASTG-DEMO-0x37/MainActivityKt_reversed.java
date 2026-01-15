package org.owasp.mastestapp;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.owasp.mastestapp.MastgTest;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000 \n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a%\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006H\u0007¢\u0006\u0002\u0010\u0007\u001a\r\u0010\b\u001a\u00020\u0003H\u0007¢\u0006\u0002\u0010\t\u001a\r\u0010\n\u001a\u00020\u0003H\u0007¢\u0006\u0002\u0010\t\u001a\u0015\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH\u0007¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u000f\u001a\u00020\u0003H\u0007¢\u0006\u0002\u0010\t\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"MASTG_TEXT_TAG", "", "MainScreen", "", "displayString", "onStartClick", "Lkotlin/Function0;", "(Ljava/lang/String;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V", "MainScreenPreview", "(Landroidx/compose/runtime/Composer;I)V", "UpdateCheckingScreen", "UpdateRequiredScreen", "state", "Lorg/owasp/mastestapp/MastgTest$UpdateState;", "(Lorg/owasp/mastestapp/MastgTest$UpdateState;Landroidx/compose/runtime/Composer;I)V", "UpdateRequiredScreenPreview", "app_debug"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MainActivityKt {
    public static final String MASTG_TEXT_TAG = "mastgTestText";

    /* compiled from: MainActivity.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[MastgTest.UpdateState.values().length];
            try {
                iArr[MastgTest.UpdateState.UPDATE_REQUIRED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[MastgTest.UpdateState.UPDATE_IN_PROGRESS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[MastgTest.UpdateState.UPDATE_CANCELED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[MastgTest.UpdateState.UPDATE_FAILED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$1(String displayString, Function0 function0, int i, int i2, Composer composer, int i3) {
        Intrinsics.checkNotNullParameter(displayString, "$displayString");
        MainScreen(displayString, function0, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1), i2);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreenPreview$lambda$2(int i, Composer composer, int i2) {
        MainScreenPreview(composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit UpdateCheckingScreen$lambda$5(int i, Composer composer, int i2) {
        UpdateCheckingScreen(composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit UpdateRequiredScreen$lambda$8(MastgTest.UpdateState state, int i, Composer composer, int i2) {
        Intrinsics.checkNotNullParameter(state, "$state");
        UpdateRequiredScreen(state, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit UpdateRequiredScreenPreview$lambda$9(int i, Composer composer, int i2) {
        UpdateRequiredScreenPreview(composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    public static final void MainScreen(final String displayString, final Function0<Unit> function0, Composer $composer, final int $changed, final int i) {
        Intrinsics.checkNotNullParameter(displayString, "displayString");
        Composer $composer2 = $composer.startRestartGroup(-958245146);
        ComposerKt.sourceInformation($composer2, "C(MainScreen)170@6877L280,170@6837L320:MainActivity.kt#vyvp3i");
        int $dirty = $changed;
        if ((i & 1) != 0) {
            $dirty |= 6;
        } else if (($changed & 14) == 0) {
            $dirty |= $composer2.changed(displayString) ? 4 : 2;
        }
        int i2 = i & 2;
        if (i2 != 0) {
            $dirty |= 48;
        } else if (($changed & 112) == 0) {
            $dirty |= $composer2.changedInstance(function0) ? 32 : 16;
        }
        if (($dirty & 91) != 18 || !$composer2.getSkipping()) {
            if (i2 != 0) {
                function0 = new Function0() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda1
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return Unit.INSTANCE;
                    }
                };
            }
            BaseScreenKt.BaseScreen(function0, ComposableLambdaKt.rememberComposableLambda(891526107, true, new Function2<Composer, Integer, Unit>() { // from class: org.owasp.mastestapp.MainActivityKt.MainScreen.2
                @Override // kotlin.jvm.functions.Function2
                public /* bridge */ /* synthetic */ Unit invoke(Composer composer, Integer num) {
                    invoke(composer, num.intValue());
                    return Unit.INSTANCE;
                }

                public final void invoke(Composer $composer3, int $changed2) {
                    ComposerKt.sourceInformation($composer3, "C171@6887L264:MainActivity.kt#vyvp3i");
                    if (($changed2 & 11) != 2 || !$composer3.getSkipping()) {
                        TextKt.m3574TextNvy7gAk(displayString, TestTagKt.testTag(PaddingKt.m830padding3ABfNKs(Modifier.INSTANCE, Dp.m8394constructorimpl(16)), MainActivityKt.MASTG_TEXT_TAG), Color.INSTANCE.m5676getBlack0d7_KjU(), null, TextUnitKt.getSp(16), null, null, FontFamily.INSTANCE.getMonospace(), 0L, null, null, 0L, 0, false, 0, 0, null, null, $composer3, 25008, 0, 261992);
                        return;
                    }
                    $composer3.skipToGroupEnd();
                }
            }, $composer2, 54), $composer2, (($dirty >> 3) & 14) | 48, 0);
        } else {
            $composer2.skipToGroupEnd();
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = $composer2.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda2
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.MainScreen$lambda$1(displayString, function0, $changed, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    public static final void MainScreenPreview(Composer $composer, final int $changed) {
        Composer $composer2 = $composer.startRestartGroup(396371301);
        ComposerKt.sourceInformation($composer2, "C(MainScreenPreview)186@7212L45:MainActivity.kt#vyvp3i");
        if ($changed != 0 || !$composer2.getSkipping()) {
            MainScreen("App is running.", null, $composer2, 6, 2);
        } else {
            $composer2.skipToGroupEnd();
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = $composer2.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda5
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.MainScreenPreview$lambda$2($changed, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    public static final void UpdateCheckingScreen(Composer $composer, final int $changed) {
        Function0 factory$iv$iv$iv;
        Function0 factory$iv$iv$iv2;
        Composer $composer2 = $composer.startRestartGroup(-72418931);
        ComposerKt.sourceInformation($composer2, "C(UpdateCheckingScreen)196@7465L705:MainActivity.kt#vyvp3i");
        if ($changed != 0 || !$composer2.getSkipping()) {
            Modifier modifier$iv = SizeKt.fillMaxSize$default(Modifier.INSTANCE, 0.0f, 1, null);
            Alignment contentAlignment$iv = Alignment.INSTANCE.getCenter();
            ComposerKt.sourceInformationMarkerStart($composer2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo");
            MeasurePolicy measurePolicy$iv = BoxKt.maybeCachedBoxMeasurePolicy(contentAlignment$iv, false);
            int $changed$iv$iv = (54 << 3) & 112;
            ComposerKt.sourceInformationMarkerStart($composer2, -1159599143, "CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int compositeKeyHash$iv$iv = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode($composer2, 0));
            CompositionLocalMap localMap$iv$iv = $composer2.getCurrentCompositionLocalMap();
            Modifier materialized$iv$iv = ComposedModifierKt.materializeModifier($composer2, modifier$iv);
            Function0 factory$iv$iv$iv3 = ComposeUiNode.INSTANCE.getConstructor();
            int $changed$iv$iv$iv = (($changed$iv$iv << 6) & 896) | 6;
            ComposerKt.sourceInformationMarkerStart($composer2, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer2.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer2.startReusableNode();
            if ($composer2.getInserting()) {
                factory$iv$iv$iv = factory$iv$iv$iv3;
                $composer2.createNode(factory$iv$iv$iv);
            } else {
                factory$iv$iv$iv = factory$iv$iv$iv3;
                $composer2.useNode();
            }
            Composer $this$Layout_u24lambda_u240$iv$iv = Updater.m4969constructorimpl($composer2);
            Updater.m4976setimpl($this$Layout_u24lambda_u240$iv$iv, measurePolicy$iv, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4976setimpl($this$Layout_u24lambda_u240$iv$iv, localMap$iv$iv, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Function2 block$iv$iv$iv = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
            if ($this$Layout_u24lambda_u240$iv$iv.getInserting() || !Intrinsics.areEqual($this$Layout_u24lambda_u240$iv$iv.rememberedValue(), Integer.valueOf(compositeKeyHash$iv$iv))) {
                $this$Layout_u24lambda_u240$iv$iv.updateRememberedValue(Integer.valueOf(compositeKeyHash$iv$iv));
                $this$Layout_u24lambda_u240$iv$iv.apply(Integer.valueOf(compositeKeyHash$iv$iv), block$iv$iv$iv);
            }
            Updater.m4976setimpl($this$Layout_u24lambda_u240$iv$iv, materialized$iv$iv, ComposeUiNode.INSTANCE.getSetModifier());
            int i = ($changed$iv$iv$iv >> 6) & 14;
            ComposerKt.sourceInformationMarkerStart($composer2, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            BoxScopeInstance boxScopeInstance = BoxScopeInstance.INSTANCE;
            int i2 = ((54 >> 6) & 112) | 6;
            ComposerKt.sourceInformationMarkerStart($composer2, -2127034358, "C200@7573L591:MainActivity.kt#vyvp3i");
            Alignment.Horizontal centerHorizontally = Alignment.INSTANCE.getCenterHorizontally();
            Arrangement.HorizontalOrVertical horizontalOrVerticalM689spacedBy0680j_4 = Arrangement.INSTANCE.m689spacedBy0680j_4(Dp.m8394constructorimpl(16));
            ComposerKt.sourceInformationMarkerStart($composer2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            Modifier modifier$iv2 = Modifier.INSTANCE;
            MeasurePolicy measurePolicy$iv2 = ColumnKt.columnMeasurePolicy(horizontalOrVerticalM689spacedBy0680j_4, centerHorizontally, $composer2, ((432 >> 3) & 14) | ((432 >> 3) & 112));
            int $changed$iv$iv2 = (432 << 3) & 112;
            ComposerKt.sourceInformationMarkerStart($composer2, -1159599143, "CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
            int compositeKeyHash$iv$iv2 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode($composer2, 0));
            CompositionLocalMap localMap$iv$iv2 = $composer2.getCurrentCompositionLocalMap();
            Modifier materialized$iv$iv2 = ComposedModifierKt.materializeModifier($composer2, modifier$iv2);
            Function0 factory$iv$iv$iv4 = ComposeUiNode.INSTANCE.getConstructor();
            int $changed$iv$iv$iv2 = (($changed$iv$iv2 << 6) & 896) | 6;
            ComposerKt.sourceInformationMarkerStart($composer2, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!($composer2.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer2.startReusableNode();
            if ($composer2.getInserting()) {
                factory$iv$iv$iv2 = factory$iv$iv$iv4;
                $composer2.createNode(factory$iv$iv$iv2);
            } else {
                factory$iv$iv$iv2 = factory$iv$iv$iv4;
                $composer2.useNode();
            }
            Composer $this$Layout_u24lambda_u240$iv$iv2 = Updater.m4969constructorimpl($composer2);
            Updater.m4976setimpl($this$Layout_u24lambda_u240$iv$iv2, measurePolicy$iv2, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4976setimpl($this$Layout_u24lambda_u240$iv$iv2, localMap$iv$iv2, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Function2 block$iv$iv$iv2 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
            if ($this$Layout_u24lambda_u240$iv$iv2.getInserting() || !Intrinsics.areEqual($this$Layout_u24lambda_u240$iv$iv2.rememberedValue(), Integer.valueOf(compositeKeyHash$iv$iv2))) {
                $this$Layout_u24lambda_u240$iv$iv2.updateRememberedValue(Integer.valueOf(compositeKeyHash$iv$iv2));
                $this$Layout_u24lambda_u240$iv$iv2.apply(Integer.valueOf(compositeKeyHash$iv$iv2), block$iv$iv$iv2);
            }
            Updater.m4976setimpl($this$Layout_u24lambda_u240$iv$iv2, materialized$iv$iv2, ComposeUiNode.INSTANCE.getSetModifier());
            int i3 = ($changed$iv$iv$iv2 >> 6) & 14;
            ComposerKt.sourceInformationMarkerStart($composer2, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            int i4 = ((432 >> 6) & 112) | 6;
            ComposerKt.sourceInformationMarkerStart($composer2, 906323155, "C204@7731L46,205@7790L364:MainActivity.kt#vyvp3i");
            ProgressIndicatorKt.m3149CircularProgressIndicator4lLiAd8(null, Color.INSTANCE.m5676getBlack0d7_KjU(), 0.0f, 0L, 0, 0.0f, $composer2, 48, 61);
            TextKt.m3574TextNvy7gAk("Checking for mandatory updates...", TestTagKt.testTag(PaddingKt.m830padding3ABfNKs(Modifier.INSTANCE, Dp.m8394constructorimpl(16)), MASTG_TEXT_TAG), Color.INSTANCE.m5676getBlack0d7_KjU(), null, TextUnitKt.getSp(16), null, null, FontFamily.INSTANCE.getMonospace(), 0L, null, TextAlign.m8243boximpl(TextAlign.INSTANCE.m8250getCentere0LSkKk()), 0L, 0, false, 0, 0, null, null, $composer2, 25014, 0, 260968);
            ComposerKt.sourceInformationMarkerEnd($composer2);
            ComposerKt.sourceInformationMarkerEnd($composer2);
            $composer2.endNode();
            ComposerKt.sourceInformationMarkerEnd($composer2);
            ComposerKt.sourceInformationMarkerEnd($composer2);
            ComposerKt.sourceInformationMarkerEnd($composer2);
            ComposerKt.sourceInformationMarkerEnd($composer2);
            ComposerKt.sourceInformationMarkerEnd($composer2);
            $composer2.endNode();
            ComposerKt.sourceInformationMarkerEnd($composer2);
            ComposerKt.sourceInformationMarkerEnd($composer2);
            ComposerKt.sourceInformationMarkerEnd($composer2);
        } else {
            $composer2.skipToGroupEnd();
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = $composer2.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.UpdateCheckingScreen$lambda$5($changed, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x01de  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x01ea  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x01ee  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x021c  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0232  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x028c  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0295  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final void UpdateRequiredScreen(final org.owasp.mastestapp.MastgTest.UpdateState r72, androidx.compose.runtime.Composer r73, final int r74) {
        /*
            Method dump skipped, instructions count: 896
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.owasp.mastestapp.MainActivityKt.UpdateRequiredScreen(org.owasp.mastestapp.MastgTest$UpdateState, androidx.compose.runtime.Composer, int):void");
    }

    public static final void UpdateRequiredScreenPreview(Composer $composer, final int $changed) {
        Composer $composer2 = $composer.startRestartGroup(1382907638);
        ComposerKt.sourceInformation($composer2, "C(UpdateRequiredScreenPreview)264@9866L59:MainActivity.kt#vyvp3i");
        if ($changed != 0 || !$composer2.getSkipping()) {
            UpdateRequiredScreen(MastgTest.UpdateState.UPDATE_REQUIRED, $composer2, 6);
        } else {
            $composer2.skipToGroupEnd();
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = $composer2.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda4
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.UpdateRequiredScreenPreview$lambda$9($changed, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }
}
