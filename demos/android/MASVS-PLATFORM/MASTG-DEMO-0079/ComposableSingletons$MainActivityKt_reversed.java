package org.owasp.mastestapp;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.text.input.TextFieldStateKt;
import androidx.compose.foundation.text.input.TextObfuscationMode;
import androidx.compose.material3.SecureTextFieldKt;
import androidx.compose.material3.TextFieldKt;
import androidx.compose.material3.TextFieldLabelScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class ComposableSingletons$MainActivityKt {
    public static final ComposableSingletons$MainActivityKt INSTANCE = new ComposableSingletons$MainActivityKt();

    /* renamed from: lambda-1, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f60lambda1 = ComposableLambdaKt.composableLambdaInstance(-1647210896, false, new Function2<Composer, Integer, Unit>() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$lambda-1$1
        @Override // kotlin.jvm.functions.Function2
        public /* bridge */ /* synthetic */ Unit invoke(Composer composer, Integer num) {
            invoke(composer, num.intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C26@1018L12:MainActivity.kt#vyvp3i");
            if (($changed & 11) != 2 || !$composer.getSkipping()) {
                MainActivityKt.MainScreen($composer, 0);
            } else {
                $composer.skipToGroupEnd();
            }
        }
    });

    /* renamed from: lambda-2, reason: not valid java name */
    public static Function3<TextFieldLabelScope, Composer, Integer, Unit> f61lambda2 = ComposableLambdaKt.composableLambdaInstance(-1250832379, false, new Function3<TextFieldLabelScope, Composer, Integer, Unit>() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$lambda-2$1
        @Override // kotlin.jvm.functions.Function3
        public /* bridge */ /* synthetic */ Unit invoke(TextFieldLabelScope textFieldLabelScope, Composer composer, Integer num) {
            invoke(textFieldLabelScope, composer, num.intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(TextFieldLabelScope TextField, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter(TextField, "$this$TextField");
            ComposerKt.sourceInformation($composer, "C38@1270L16:MainActivity.kt#vyvp3i");
            if (($changed & 81) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
            } else {
                TextKt.m3574TextNvy7gAk("Username", null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, $composer, 6, 0, 262142);
            }
        }
    });

    /* renamed from: lambda-3, reason: not valid java name */
    public static Function3<TextFieldLabelScope, Composer, Integer, Unit> f62lambda3 = ComposableLambdaKt.composableLambdaInstance(-278467749, false, new Function3<TextFieldLabelScope, Composer, Integer, Unit>() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$lambda-3$1
        @Override // kotlin.jvm.functions.Function3
        public /* bridge */ /* synthetic */ Unit invoke(TextFieldLabelScope textFieldLabelScope, Composer composer, Integer num) {
            invoke(textFieldLabelScope, composer, num.intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(TextFieldLabelScope SecureTextField, Composer $composer, int $changed) {
            Intrinsics.checkNotNullParameter(SecureTextField, "$this$SecureTextField");
            ComposerKt.sourceInformation($composer, "C45@1577L30:MainActivity.kt#vyvp3i");
            if (($changed & 81) == 16 && $composer.getSkipping()) {
                $composer.skipToGroupEnd();
            } else {
                TextKt.m3574TextNvy7gAk("Visible Password Field", null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, $composer, 6, 0, 262142);
            }
        }
    });

    /* renamed from: lambda-4, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f63lambda4 = ComposableLambdaKt.composableLambdaInstance(2027959928, false, new Function2<Composer, Integer, Unit>() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$lambda-4$1
        @Override // kotlin.jvm.functions.Function2
        public /* bridge */ /* synthetic */ Unit invoke(Composer composer, Integer num) {
            invoke(composer, num.intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer $composer, int $changed) {
            ComposerKt.sourceInformation($composer, "C35@1115L519:MainActivity.kt#vyvp3i");
            if (($changed & 11) != 2 || !$composer.getSkipping()) {
                Modifier modifier$iv = PaddingKt.m832paddingVpY3zN4$default(Modifier.Companion, 0.0f, Dp.m8394constructorimpl(16), 1, null);
                ComposerKt.sourceInformationMarkerStart($composer, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
                Arrangement.Vertical verticalArrangement$iv = Arrangement.INSTANCE.getTop();
                Alignment.Horizontal horizontalAlignment$iv = Alignment.Companion.getStart();
                MeasurePolicy measurePolicy$iv = ColumnKt.columnMeasurePolicy(verticalArrangement$iv, horizontalAlignment$iv, $composer, ((6 >> 3) & 14) | ((6 >> 3) & 112));
                int $changed$iv$iv = (6 << 3) & 112;
                ComposerKt.sourceInformationMarkerStart($composer, -1159599143, "CC(Layout)P(!1,2)80@3267L27,83@3433L360:Layout.kt#80mrfh");
                int compositeKeyHash$iv$iv = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode($composer, 0));
                CompositionLocalMap localMap$iv$iv = $composer.getCurrentCompositionLocalMap();
                Modifier materialized$iv$iv = ComposedModifierKt.materializeModifier($composer, modifier$iv);
                Function0 factory$iv$iv$iv = ComposeUiNode.Companion.getConstructor();
                int $changed$iv$iv$iv = (($changed$iv$iv << 6) & 896) | 6;
                ComposerKt.sourceInformationMarkerStart($composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!($composer.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                $composer.startReusableNode();
                if ($composer.getInserting()) {
                    $composer.createNode(factory$iv$iv$iv);
                } else {
                    $composer.useNode();
                }
                Composer $this$Layout_u24lambda_u240$iv$iv = Updater.m4969constructorimpl($composer);
                Updater.m4976setimpl($this$Layout_u24lambda_u240$iv$iv, measurePolicy$iv, ComposeUiNode.Companion.getSetMeasurePolicy());
                Updater.m4976setimpl($this$Layout_u24lambda_u240$iv$iv, localMap$iv$iv, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
                Function2 block$iv$iv$iv = ComposeUiNode.Companion.getSetCompositeKeyHash();
                if ($this$Layout_u24lambda_u240$iv$iv.getInserting() || !Intrinsics.areEqual($this$Layout_u24lambda_u240$iv$iv.rememberedValue(), Integer.valueOf(compositeKeyHash$iv$iv))) {
                    $this$Layout_u24lambda_u240$iv$iv.updateRememberedValue(Integer.valueOf(compositeKeyHash$iv$iv));
                    $this$Layout_u24lambda_u240$iv$iv.apply(Integer.valueOf(compositeKeyHash$iv$iv), block$iv$iv$iv);
                }
                Updater.m4976setimpl($this$Layout_u24lambda_u240$iv$iv, materialized$iv$iv, ComposeUiNode.Companion.getSetModifier());
                int i = ($changed$iv$iv$iv >> 6) & 14;
                ComposerKt.sourceInformationMarkerStart($composer, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
                ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
                int i2 = ((6 >> 6) & 112) | 6;
                ComposerKt.sourceInformationMarkerStart($composer, 1992602868, "C37@1218L24,36@1183L170,42@1407L24,41@1366L258:MainActivity.kt#vyvp3i");
                TextFieldKt.TextField(TextFieldStateKt.m1440rememberTextFieldStateLepunE(null, 0L, $composer, 0, 3), PaddingKt.m830padding3ABfNKs(Modifier.Companion, Dp.m8394constructorimpl(4)), false, false, null, null, ComposableSingletons$MainActivityKt.INSTANCE.m10512getLambda2$app_debug(), null, null, null, null, null, null, false, null, null, null, null, null, null, null, null, null, null, null, $composer, 1572912, 0, 0, 33554364);
                SecureTextFieldKt.m3241SecureTextFieldXvU6IwQ(TextFieldStateKt.m1440rememberTextFieldStateLepunE(null, 0L, $composer, 0, 3), PaddingKt.m830padding3ABfNKs(Modifier.Companion, Dp.m8394constructorimpl(4)), false, null, null, ComposableSingletons$MainActivityKt.INSTANCE.m10513getLambda3$app_debug(), null, null, null, null, null, null, false, null, TextObfuscationMode.Companion.m1459getVisiblevTwcZD0(), (char) 0, null, null, null, null, null, null, null, $composer, 196656, 0, 0, 8372188);
                ComposerKt.sourceInformationMarkerEnd($composer);
                ComposerKt.sourceInformationMarkerEnd($composer);
                $composer.endNode();
                ComposerKt.sourceInformationMarkerEnd($composer);
                ComposerKt.sourceInformationMarkerEnd($composer);
                ComposerKt.sourceInformationMarkerEnd($composer);
                return;
            }
            $composer.skipToGroupEnd();
        }
    });

    /* renamed from: getLambda-1$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m10511getLambda1$app_debug() {
        return f60lambda1;
    }

    /* renamed from: getLambda-2$app_debug, reason: not valid java name */
    public final Function3<TextFieldLabelScope, Composer, Integer, Unit> m10512getLambda2$app_debug() {
        return f61lambda2;
    }

    /* renamed from: getLambda-3$app_debug, reason: not valid java name */
    public final Function3<TextFieldLabelScope, Composer, Integer, Unit> m10513getLambda3$app_debug() {
        return f62lambda3;
    }

    /* renamed from: getLambda-4$app_debug, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m10514getLambda4$app_debug() {
        return f63lambda4;
    }
}
