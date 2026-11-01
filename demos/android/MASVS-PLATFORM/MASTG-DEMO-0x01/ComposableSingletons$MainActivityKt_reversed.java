package org.owasp.mastestapp;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.style.StylePropertiesKt;
import androidx.compose.foundation.text.input.TextFieldState;
import androidx.compose.foundation.text.input.TextFieldStateKt;
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
import androidx.compose.ui.SensitiveContentKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertiesKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: MainActivity.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(k = 3, mv = {2, 4, 0}, xi = 48)
public final class ComposableSingletons$MainActivityKt {
    public static final ComposableSingletons$MainActivityKt INSTANCE = new ComposableSingletons$MainActivityKt();

    /* JADX INFO: renamed from: lambda$-1647210896, reason: not valid java name */
    private static Function2<Composer, Integer, Unit> f51lambda$1647210896 = ComposableLambdaKt.composableLambdaInstance(-1647210896, false, new Function2() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda0
        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(Object obj, Object obj2) {
            return ComposableSingletons$MainActivityKt.lambda__1647210896$lambda$0((Composer) obj, ((Integer) obj2).intValue());
        }
    });

    /* JADX INFO: renamed from: lambda$-247347457, reason: not valid java name */
    private static Function3<RowScope, Composer, Integer, Unit> f52lambda$247347457 = ComposableLambdaKt.composableLambdaInstance(-247347457, false, new Function3() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda1
        @Override // kotlin.jvm.functions.Function3
        public final Object invoke(Object obj, Object obj2, Object obj3) {
            return ComposableSingletons$MainActivityKt.lambda__247347457$lambda$0((RowScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
        }
    });

    /* JADX INFO: renamed from: lambda$-871720306, reason: not valid java name */
    private static Function3<RowScope, Composer, Integer, Unit> f54lambda$871720306 = ComposableLambdaKt.composableLambdaInstance(-871720306, false, new Function3() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda2
        @Override // kotlin.jvm.functions.Function3
        public final Object invoke(Object obj, Object obj2, Object obj3) {
            return ComposableSingletons$MainActivityKt.lambda__871720306$lambda$0((RowScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
        }
    });
    private static Function2<Composer, Integer, Unit> lambda$964692755 = ComposableLambdaKt.composableLambdaInstance(964692755, false, new Function2() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda3
        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(Object obj, Object obj2) {
            return ComposableSingletons$MainActivityKt.lambda_964692755$lambda$0((Composer) obj, ((Integer) obj2).intValue());
        }
    });

    /* JADX INFO: renamed from: lambda$-722578777, reason: not valid java name */
    private static Function3<TextFieldLabelScope, Composer, Integer, Unit> f53lambda$722578777 = ComposableLambdaKt.composableLambdaInstance(-722578777, false, new Function3() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda4
        @Override // kotlin.jvm.functions.Function3
        public final Object invoke(Object obj, Object obj2, Object obj3) {
            return ComposableSingletons$MainActivityKt.lambda__722578777$lambda$0((TextFieldLabelScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
        }
    });
    private static Function3<TextFieldLabelScope, Composer, Integer, Unit> lambda$1977537297 = ComposableLambdaKt.composableLambdaInstance(1977537297, false, new Function3() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda5
        @Override // kotlin.jvm.functions.Function3
        public final Object invoke(Object obj, Object obj2, Object obj3) {
            return ComposableSingletons$MainActivityKt.lambda_1977537297$lambda$0((TextFieldLabelScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
        }
    });
    private static Function3<TextFieldLabelScope, Composer, Integer, Unit> lambda$1828242654 = ComposableLambdaKt.composableLambdaInstance(1828242654, false, new Function3() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda6
        @Override // kotlin.jvm.functions.Function3
        public final Object invoke(Object obj, Object obj2, Object obj3) {
            return ComposableSingletons$MainActivityKt.lambda_1828242654$lambda$0((TextFieldLabelScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
        }
    });
    private static Function2<Composer, Integer, Unit> lambda$1765228308 = ComposableLambdaKt.composableLambdaInstance(1765228308, false, new Function2() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda7
        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(Object obj, Object obj2) {
            return ComposableSingletons$MainActivityKt.lambda_1765228308$lambda$0((Composer) obj, ((Integer) obj2).intValue());
        }
    });

    /* JADX INFO: renamed from: getLambda$-1647210896$app, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m10574getLambda$1647210896$app() {
        return f51lambda$1647210896;
    }

    /* JADX INFO: renamed from: getLambda$-247347457$app, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m10575getLambda$247347457$app() {
        return f52lambda$247347457;
    }

    /* JADX INFO: renamed from: getLambda$-722578777$app, reason: not valid java name */
    public final Function3<TextFieldLabelScope, Composer, Integer, Unit> m10576getLambda$722578777$app() {
        return f53lambda$722578777;
    }

    /* JADX INFO: renamed from: getLambda$-871720306$app, reason: not valid java name */
    public final Function3<RowScope, Composer, Integer, Unit> m10577getLambda$871720306$app() {
        return f54lambda$871720306;
    }

    public final Function2<Composer, Integer, Unit> getLambda$1765228308$app() {
        return lambda$1765228308;
    }

    public final Function3<TextFieldLabelScope, Composer, Integer, Unit> getLambda$1828242654$app() {
        return lambda$1828242654;
    }

    public final Function3<TextFieldLabelScope, Composer, Integer, Unit> getLambda$1977537297$app() {
        return lambda$1977537297;
    }

    public final Function2<Composer, Integer, Unit> getLambda$964692755$app() {
        return lambda$964692755;
    }

    static final Unit lambda__1647210896$lambda$0(Composer $composer, int $changed) {
        ComposerKt.sourceInformation($composer, "C49@2137L12:MainActivity.kt#vyvp3i");
        if (!$composer.shouldExecute(($changed & 3) != 2, $changed & 1)) {
            $composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1647210896, $changed, -1, "org.owasp.mastestapp.ComposableSingletons$MainActivityKt.lambda$-1647210896.<anonymous> (MainActivity.kt:49)");
            }
            MainActivityKt.MainScreen($composer, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit lambda_964692755$lambda$0(Composer $composer, int $changed) {
        ComposerKt.sourceInformation($composer, "C137@5289L15:MainActivity.kt#vyvp3i");
        if ($composer.shouldExecute(($changed & 3) != 2, $changed & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(964692755, $changed, -1, "org.owasp.mastestapp.ComposableSingletons$MainActivityKt.lambda$964692755.<anonymous> (MainActivity.kt:137)");
            }
            TextKt.m3325TextNvy7gAk("Payment", null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, $composer, 6, 0, 262142);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    static final Unit lambda_1765228308$lambda$0(Composer $composer, int $changed) {
        Function0<ComposeUiNode> function0;
        ComposerKt.sourceInformation($composer, "C139@5337L1561:MainActivity.kt#vyvp3i");
        if ($composer.shouldExecute(($changed & 3) != 2, $changed & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1765228308, $changed, -1, "org.owasp.mastestapp.ComposableSingletons$MainActivityKt.lambda$1765228308.<anonymous> (MainActivity.kt:139)");
            }
            ComposerKt.sourceInformationMarkerStart($composer, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            Modifier modifier = Modifier.INSTANCE;
            MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), $composer, ((0 >> 3) & 14) | ((0 >> 3) & StylePropertiesKt.TextDirectionMask));
            int i = (0 << 3) & StylePropertiesKt.TextDirectionMask;
            ComposerKt.sourceInformationMarkerStart($composer, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode($composer, 0));
            CompositionLocalMap currentCompositionLocalMap = $composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier($composer, modifier);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            int i2 = ((i << 6) & 896) | 6;
            ComposerKt.sourceInformationMarkerStart($composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16216L9:Composables.kt#9igjgp");
            if (!($composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            $composer.startReusableNode();
            if ($composer.getInserting()) {
                function0 = constructor;
                $composer.createNode(function0);
            } else {
                function0 = constructor;
                $composer.useNode();
            }
            Composer composerM4601constructorimpl = Updater.m4601constructorimpl($composer);
            Updater.m4609setimpl(composerM4601constructorimpl, measurePolicyColumnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4609setimpl(composerM4601constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4609setimpl(composerM4601constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4607reconcileimpl(composerM4601constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4609setimpl(composerM4601constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            int i3 = (i2 >> 6) & 14;
            ComposerKt.sourceInformationMarkerStart($composer, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            int i4 = ((0 >> 6) & StylePropertiesKt.TextDirectionMask) | 6;
            ComposerKt.sourceInformationMarkerStart($composer, -1624210176, "C142@5522L24,146@5723L27,141@5483L660,154@6325L24,153@6280L203,160@6636L24,164@6840L26,159@6597L287:MainActivity.kt#vyvp3i");
            TextFieldState textFieldStateM1832rememberTextFieldStateLepunE = TextFieldStateKt.m1832rememberTextFieldStateLepunE(null, 0L, $composer, 0, 3);
            Modifier modifierM1091paddingVpY3zN4$default = PaddingKt.m1091paddingVpY3zN4$default(Modifier.INSTANCE, 0.0f, Dp.m8397constructorimpl(4), 1, null);
            ComposerKt.sourceInformationMarkerStart($composer, -329482235, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue = $composer.rememberedValue();
            if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                Object obj = new Function1() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda8
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj2) {
                        return ComposableSingletons$MainActivityKt.lambda_1765228308$lambda$0$0$0$0((SemanticsPropertyReceiver) obj2);
                    }
                };
                $composer.updateRememberedValue(obj);
                objRememberedValue = obj;
            }
            ComposerKt.sourceInformationMarkerEnd($composer);
            TextFieldKt.TextField(textFieldStateM1832rememberTextFieldStateLepunE, SensitiveContentKt.sensitiveContent(SemanticsModifierKt.semantics$default(modifierM1091paddingVpY3zN4$default, false, (Function1) objRememberedValue, 1, null), true), false, false, null, null, f53lambda$722578777, null, null, null, null, null, null, false, null, null, null, null, null, null, null, null, null, null, null, $composer, 1572864, 0, 0, 33554364);
            SecureTextFieldKt.m3052SecureTextFieldXvU6IwQ(TextFieldStateKt.m1832rememberTextFieldStateLepunE(null, 0L, $composer, 0, 3), PaddingKt.m1091paddingVpY3zN4$default(Modifier.INSTANCE, 0.0f, Dp.m8397constructorimpl(4), 1, null), false, null, null, lambda$1977537297, null, null, null, null, null, null, false, null, 0, (char) 0, null, null, null, null, null, null, null, $composer, 196656, 0, 0, 8388572);
            TextFieldState textFieldStateM1832rememberTextFieldStateLepunE2 = TextFieldStateKt.m1832rememberTextFieldStateLepunE(null, 0L, $composer, 0, 3);
            Modifier modifierM1091paddingVpY3zN4$default2 = PaddingKt.m1091paddingVpY3zN4$default(Modifier.INSTANCE, 0.0f, Dp.m8397constructorimpl(4), 1, null);
            ComposerKt.sourceInformationMarkerStart($composer, -329446492, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue2 = $composer.rememberedValue();
            if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                Object obj2 = new Function1() { // from class: org.owasp.mastestapp.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda9
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj3) {
                        return ComposableSingletons$MainActivityKt.lambda_1765228308$lambda$0$0$1$0((SemanticsPropertyReceiver) obj3);
                    }
                };
                $composer.updateRememberedValue(obj2);
                objRememberedValue2 = obj2;
            }
            ComposerKt.sourceInformationMarkerEnd($composer);
            TextFieldKt.TextField(textFieldStateM1832rememberTextFieldStateLepunE2, SemanticsModifierKt.semantics$default(modifierM1091paddingVpY3zN4$default2, false, (Function1) objRememberedValue2, 1, null), false, false, null, null, lambda$1828242654, null, null, null, null, null, null, false, null, null, null, null, null, null, null, null, null, null, null, $composer, 1572864, 0, 0, 33554364);
            ComposerKt.sourceInformationMarkerEnd($composer);
            ComposerKt.sourceInformationMarkerEnd($composer);
            $composer.endNode();
            ComposerKt.sourceInformationMarkerEnd($composer);
            ComposerKt.sourceInformationMarkerEnd($composer);
            ComposerKt.sourceInformationMarkerEnd($composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    static final Unit lambda__722578777$lambda$0(TextFieldLabelScope TextField, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter(TextField, "$this$TextField");
        ComposerKt.sourceInformation($composer, "C143@5578L16:MainActivity.kt#vyvp3i");
        if ($composer.shouldExecute(($changed & 17) != 16, $changed & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-722578777, $changed, -1, "org.owasp.mastestapp.ComposableSingletons$MainActivityKt.lambda$-722578777.<anonymous> (MainActivity.kt:143)");
            }
            TextKt.m3325TextNvy7gAk("Username", null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, $composer, 6, 0, 262142);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit lambda_1765228308$lambda$0$0$0$0(SemanticsPropertyReceiver semantics) {
        Intrinsics.checkNotNullParameter(semantics, "$this$semantics");
        SemanticsPropertiesKt.setSensitiveData(semantics, false);
        return Unit.INSTANCE;
    }

    static final Unit lambda_1977537297$lambda$0(TextFieldLabelScope SecureTextField, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter(SecureTextField, "$this$SecureTextField");
        ComposerKt.sourceInformation($composer, "C155@6381L16:MainActivity.kt#vyvp3i");
        if ($composer.shouldExecute(($changed & 17) != 16, $changed & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1977537297, $changed, -1, "org.owasp.mastestapp.ComposableSingletons$MainActivityKt.lambda$1977537297.<anonymous> (MainActivity.kt:155)");
            }
            TextKt.m3325TextNvy7gAk("Password", null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, $composer, 6, 0, 262142);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    static final Unit lambda_1828242654$lambda$0(TextFieldLabelScope TextField, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter(TextField, "$this$TextField");
        ComposerKt.sourceInformation($composer, "C161@6692L19:MainActivity.kt#vyvp3i");
        if ($composer.shouldExecute(($changed & 17) != 16, $changed & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1828242654, $changed, -1, "org.owasp.mastestapp.ComposableSingletons$MainActivityKt.lambda$1828242654.<anonymous> (MainActivity.kt:161)");
            }
            TextKt.m3325TextNvy7gAk("Card number", null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, $composer, 6, 0, 262142);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            $composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit lambda_1765228308$lambda$0$0$1$0(SemanticsPropertyReceiver semantics) {
        Intrinsics.checkNotNullParameter(semantics, "$this$semantics");
        SemanticsPropertiesKt.setSensitiveData(semantics, true);
        return Unit.INSTANCE;
    }

    static final Unit lambda__247347457$lambda$0(RowScope Button, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter(Button, "$this$Button");
        ComposerKt.sourceInformation($composer, "C171@7157L23:MainActivity.kt#vyvp3i");
        if (!$composer.shouldExecute(($changed & 17) != 16, $changed & 1)) {
            $composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-247347457, $changed, -1, "org.owasp.mastestapp.ComposableSingletons$MainActivityKt.lambda$-247347457.<anonymous> (MainActivity.kt:171)");
            }
            TextKt.m3325TextNvy7gAk("Confirm Payment", null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, $composer, 6, 0, 262142);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit lambda__871720306$lambda$0(RowScope TextButton, Composer $composer, int $changed) {
        Intrinsics.checkNotNullParameter(TextButton, "$this$TextButton");
        ComposerKt.sourceInformation($composer, "C177@7424L14:MainActivity.kt#vyvp3i");
        if (!$composer.shouldExecute(($changed & 17) != 16, $changed & 1)) {
            $composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-871720306, $changed, -1, "org.owasp.mastestapp.ComposableSingletons$MainActivityKt.lambda$-871720306.<anonymous> (MainActivity.kt:177)");
            }
            TextKt.m3325TextNvy7gAk("Cancel", null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, $composer, 6, 0, 262142);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }
}
