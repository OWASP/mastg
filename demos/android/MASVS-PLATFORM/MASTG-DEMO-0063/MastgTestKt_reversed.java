package org.owasp.mastestapp;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.MaterialThemeKt;
import androidx.compose.material3.SurfaceKt;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.TextLayoutResult;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.window.AndroidDialog_androidKt;
import androidx.compose.ui.window.DialogProperties;
import androidx.compose.ui.window.SecureFlagPolicy;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.scheduling.WorkQueueKt;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001b\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003¢\u0006\u0002\u0010\u0004¨\u0006\u0005"}, d2 = {"SecureComposeDialog", "", "onDismiss", "Lkotlin/Function0;", "(Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V", "app_debug"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTestKt {
    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit SecureComposeDialog$lambda$0(Function0 onDismiss, int i, Composer composer, int i2) {
        Intrinsics.checkNotNullParameter(onDismiss, "$onDismiss");
        SecureComposeDialog(onDismiss, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void SecureComposeDialog(final Function0<Unit> function0, Composer $composer, final int $changed) {
        Composer $composer2 = $composer.startRestartGroup(69645263);
        ComposerKt.sourceInformation($composer2, "C(SecureComposeDialog)56@1997L462,53@1864L595:MastgTest.kt#vyvp3i");
        int $dirty = $changed;
        if (($changed & 14) == 0) {
            $dirty |= $composer2.changedInstance(function0) ? 4 : 2;
        }
        if (($dirty & 11) != 2 || !$composer2.getSkipping()) {
            AndroidDialog_androidKt.Dialog(function0, new DialogProperties(false, false, SecureFlagPolicy.SecureOff, false, false, 27, null), ComposableLambdaKt.rememberComposableLambda(-882880282, true, new Function2<Composer, Integer, Unit>() { // from class: org.owasp.mastestapp.MastgTestKt.SecureComposeDialog.1
                @Override // kotlin.jvm.functions.Function2
                public /* bridge */ /* synthetic */ Unit invoke(Composer composer, Integer num) {
                    invoke(composer, num.intValue());
                    return Unit.INSTANCE;
                }

                public final void invoke(Composer $composer3, int $changed2) {
                    ComposerKt.sourceInformation($composer3, "C57@2021L432,57@2007L446:MastgTest.kt#vyvp3i");
                    if (($changed2 & 11) != 2 || !$composer3.getSkipping()) {
                        final Function0<Unit> function02 = function0;
                        MaterialThemeKt.MaterialTheme(null, null, null, ComposableLambdaKt.rememberComposableLambda(1688954298, true, new Function2<Composer, Integer, Unit>() { // from class: org.owasp.mastestapp.MastgTestKt.SecureComposeDialog.1.1
                            @Override // kotlin.jvm.functions.Function2
                            public /* bridge */ /* synthetic */ Unit invoke(Composer composer, Integer num) {
                                invoke(composer, num.intValue());
                                return Unit.INSTANCE;
                            }

                            public final void invoke(Composer $composer4, int $changed3) {
                                ComposerKt.sourceInformation($composer4, "C58@2043L400,58@2035L408:MastgTest.kt#vyvp3i");
                                if (($changed3 & 11) != 2 || !$composer4.getSkipping()) {
                                    final Function0<Unit> function03 = function02;
                                    SurfaceKt.m2565SurfaceT9BRK9s(null, null, 0L, 0L, 0.0f, 0.0f, null, ComposableLambdaKt.rememberComposableLambda(-195282113, true, new Function2<Composer, Integer, Unit>() { // from class: org.owasp.mastestapp.MastgTestKt.SecureComposeDialog.1.1.1
                                        @Override // kotlin.jvm.functions.Function2
                                        public /* bridge */ /* synthetic */ Unit invoke(Composer composer, Integer num) {
                                            invoke(composer, num.intValue());
                                            return Unit.INSTANCE;
                                        }

                                        public final void invoke(Composer $composer5, int $changed4) {
                                            ComposerKt.sourceInformation($composer5, "C59@2061L368:MastgTest.kt#vyvp3i");
                                            if (($changed4 & 11) != 2 || !$composer5.getSkipping()) {
                                                Modifier modifier$iv = PaddingKt.m681padding3ABfNKs(Modifier.INSTANCE, Dp.m6664constructorimpl(16));
                                                Function0<Unit> function04 = function03;
                                                ComposerKt.sourceInformationMarkerStart($composer5, -483455358, "CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
                                                Arrangement.Vertical verticalArrangement$iv = Arrangement.INSTANCE.getTop();
                                                Alignment.Horizontal horizontalAlignment$iv = Alignment.INSTANCE.getStart();
                                                MeasurePolicy measurePolicy$iv = ColumnKt.columnMeasurePolicy(verticalArrangement$iv, horizontalAlignment$iv, $composer5, ((6 >> 3) & 14) | ((6 >> 3) & 112));
                                                int $changed$iv$iv = (6 << 3) & 112;
                                                ComposerKt.sourceInformationMarkerStart($composer5, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                                                int compositeKeyHash$iv$iv = ComposablesKt.getCurrentCompositeKeyHash($composer5, 0);
                                                CompositionLocalMap localMap$iv$iv = $composer5.getCurrentCompositionLocalMap();
                                                Modifier materialized$iv$iv = ComposedModifierKt.materializeModifier($composer5, modifier$iv);
                                                Function0 factory$iv$iv$iv = ComposeUiNode.INSTANCE.getConstructor();
                                                int $changed$iv$iv$iv = (($changed$iv$iv << 6) & 896) | 6;
                                                ComposerKt.sourceInformationMarkerStart($composer5, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                                                if (!($composer5.getApplier() instanceof Applier)) {
                                                    ComposablesKt.invalidApplier();
                                                }
                                                $composer5.startReusableNode();
                                                if ($composer5.getInserting()) {
                                                    $composer5.createNode(factory$iv$iv$iv);
                                                } else {
                                                    $composer5.useNode();
                                                }
                                                Composer $this$Layout_u24lambda_u240$iv$iv = Updater.m3675constructorimpl($composer5);
                                                Updater.m3682setimpl($this$Layout_u24lambda_u240$iv$iv, measurePolicy$iv, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                                                Updater.m3682setimpl($this$Layout_u24lambda_u240$iv$iv, localMap$iv$iv, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                                                Function2 block$iv$iv$iv = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                                                if ($this$Layout_u24lambda_u240$iv$iv.getInserting() || !Intrinsics.areEqual($this$Layout_u24lambda_u240$iv$iv.rememberedValue(), Integer.valueOf(compositeKeyHash$iv$iv))) {
                                                    $this$Layout_u24lambda_u240$iv$iv.updateRememberedValue(Integer.valueOf(compositeKeyHash$iv$iv));
                                                    $this$Layout_u24lambda_u240$iv$iv.apply(Integer.valueOf(compositeKeyHash$iv$iv), block$iv$iv$iv);
                                                }
                                                Updater.m3682setimpl($this$Layout_u24lambda_u240$iv$iv, materialized$iv$iv, ComposeUiNode.INSTANCE.getSetModifier());
                                                int i = ($changed$iv$iv$iv >> 6) & 14;
                                                ComposerKt.sourceInformationMarkerStart($composer5, -384784025, "C88@4444L9:Column.kt#2w3rfo");
                                                ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
                                                int i2 = ((6 >> 6) & 112) | 6;
                                                ComposerKt.sourceInformationMarkerStart($composer5, 1156231831, "C60@2146L33,61@2200L42,62@2263L148:MastgTest.kt#vyvp3i");
                                                TextKt.m2715Text4IGK_g("This is a Compose dialog.", (Modifier) null, 0L, 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, $composer5, 6, 0, 131070);
                                                TextKt.m2715Text4IGK_g("Secure policy is set to SecureOff.", (Modifier) null, 0L, 0L, (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, $composer5, 6, 0, 131070);
                                                ButtonKt.Button(function04, PaddingKt.m685paddingqDBjuR0$default(Modifier.INSTANCE, 0.0f, Dp.m6664constructorimpl(12), 0.0f, 0.0f, 13, null), false, null, null, null, null, null, null, ComposableSingletons$MastgTestKt.INSTANCE.m8560getLambda1$app_debug(), $composer5, 805306416, 508);
                                                ComposerKt.sourceInformationMarkerEnd($composer5);
                                                ComposerKt.sourceInformationMarkerEnd($composer5);
                                                $composer5.endNode();
                                                ComposerKt.sourceInformationMarkerEnd($composer5);
                                                ComposerKt.sourceInformationMarkerEnd($composer5);
                                                ComposerKt.sourceInformationMarkerEnd($composer5);
                                                return;
                                            }
                                            $composer5.skipToGroupEnd();
                                        }
                                    }, $composer4, 54), $composer4, 12582912, WorkQueueKt.MASK);
                                } else {
                                    $composer4.skipToGroupEnd();
                                }
                            }
                        }, $composer3, 54), $composer3, 3072, 7);
                    } else {
                        $composer3.skipToGroupEnd();
                    }
                }
            }, $composer2, 54), $composer2, ($dirty & 14) | 432, 0);
        } else {
            $composer2.skipToGroupEnd();
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = $composer2.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: org.owasp.mastestapp.MastgTestKt$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MastgTestKt.SecureComposeDialog$lambda$0(function0, $changed, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }
}
