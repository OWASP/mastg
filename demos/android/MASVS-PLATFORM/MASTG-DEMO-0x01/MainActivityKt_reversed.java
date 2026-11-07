package org.owasp.mastestapp;

import android.content.Context;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.material3.AndroidAlertDialog_androidKt;
import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.ProvidableCompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt__SnapshotStateKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.Shadow;
import androidx.compose.ui.graphics.drawscope.DrawStyle;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.PlatformSpanStyle;
import androidx.compose.ui.text.SpanStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontSynthesis;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.intl.LocaleList;
import androidx.compose.ui.text.style.BaselineShift;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.text.style.TextGeometricTransform;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KCallable;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonArray;
import kotlinx.serialization.json.JsonElement;

/* JADX INFO: compiled from: MainActivity.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u00002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\u001a\u001e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0001\u001a#\u0010\u0007\u001a\u00020\bH\u0007b\u0002\b\nb\u0002\b\u000bb\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e¢\u0006\u0002\u0010\t\u001a3\u0010\u000f\u001a\u00020\b2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\b0\u0011H\u0007b\u0002\b\u000bb\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e¢\u0006\u0002\u0010\u0012\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0013²\u0006\n\u0010\u0005\u001a\u00020\u0003X\u008a\u008e\u0002²\u0006\n\u0010\u0014\u001a\u00020\u0015X\u008a\u008e\u0002"}, d2 = {"MASTG_TEXT_TAG", "", "UpdateDisplayString", "Landroidx/compose/ui/text/AnnotatedString;", "defaultMessage", "displayString", "result", "MainScreen", "", "(Landroidx/compose/runtime/Composer;I)V", "Landroidx/compose/ui/tooling/preview/Preview;", "Landroidx/compose/runtime/Composable;", "Landroidx/compose/runtime/ComposableTarget;", "applier", "androidx.compose.ui.UiComposable", "PaymentDialog", "onResult", "Lkotlin/Function1;", "(Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V", "app", "showPaymentForm", ""}, k = 2, mv = {2, 4, 0}, xi = 48)
public final class MainActivityKt {
    public static final String MASTG_TEXT_TAG = "mastgTestText";

    /* JADX INFO: compiled from: MainActivity.kt */
    @Metadata(k = 3, mv = {2, 4, 0}, xi = 1328)
    public static final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[Status.values().length];
            try {
                iArr[Status.PASS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Status.FAIL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Status.ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    static final Unit MainScreen$lambda$10(int i, Composer composer, int i2) {
        MainScreen(composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    static final Unit PaymentDialog$lambda$3(Function1 function1, int i, Composer composer, int i2) {
        PaymentDialog(function1, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    public static final AnnotatedString UpdateDisplayString(String defaultMessage, AnnotatedString displayString, String result) throws Throwable {
        Intrinsics.checkNotNullParameter(defaultMessage, "defaultMessage");
        Intrinsics.checkNotNullParameter(displayString, "displayString");
        Intrinsics.checkNotNullParameter(result, "result");
        AnnotatedString.Builder builder = new AnnotatedString.Builder(0, 1, null);
        builder.append(defaultMessage);
        try {
            JsonElement toJsonElement = Json.INSTANCE.parseToJsonElement(result);
            Intrinsics.checkNotNull(toJsonElement, "null cannot be cast to non-null type kotlinx.serialization.json.JsonArray");
            Iterable<JsonElement> iterable = (JsonArray) toJsonElement;
            Collection arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10));
            for (JsonElement jsonElement : iterable) {
                Json json = Json.INSTANCE;
                json.getSerializersModule();
                arrayList.add((DemoResult) json.decodeFromJsonElement(DemoResult.INSTANCE.serializer(), jsonElement));
                iterable = iterable;
            }
            List list = (List) arrayList;
            Iterator it = list.iterator();
            while (it.hasNext()) {
                DemoResult demoResult = (DemoResult) it.next();
                switch (WhenMappings.$EnumSwitchMapping$0[demoResult.getStatus().ordinal()]) {
                    case 1:
                        Iterator it2 = it;
                        List list2 = list;
                        int iPushStyle = builder.pushStyle(new SpanStyle(Color.INSTANCE.m5509getGreen0d7_KjU(), 0L, (FontWeight) null, (FontStyle) null, (FontSynthesis) null, (FontFamily) null, (String) null, 0L, (BaselineShift) null, (TextGeometricTransform) null, (LocaleList) null, 0L, (TextDecoration) null, (Shadow) null, (PlatformSpanStyle) null, (DrawStyle) null, 65534, (DefaultConstructorMarker) null));
                        try {
                            try {
                                builder.append("MASTG-DEMO-" + demoResult.getDemoId() + " demonstrated a successful test:\n" + demoResult.getMessage() + "\n\n");
                                Unit unit = Unit.INSTANCE;
                                builder.pop(iPushStyle);
                                it = it2;
                                list = list2;
                            } catch (Throwable th) {
                                th = th;
                                builder.pop(iPushStyle);
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                        }
                        break;
                    case 2:
                        Iterator it3 = it;
                        List list3 = list;
                        int iPushStyle2 = builder.pushStyle(new SpanStyle(ColorKt.Color(4294940672L), 0L, (FontWeight) null, (FontStyle) null, (FontSynthesis) null, (FontFamily) null, (String) null, 0L, (BaselineShift) null, (TextGeometricTransform) null, (LocaleList) null, 0L, (TextDecoration) null, (Shadow) null, (PlatformSpanStyle) null, (DrawStyle) null, 65534, (DefaultConstructorMarker) null));
                        try {
                            try {
                                builder.append("MASTG-DEMO-" + demoResult.getDemoId() + " demonstrated a failed test:\n" + demoResult.getMessage() + "\n\n");
                                Unit unit2 = Unit.INSTANCE;
                                builder.pop(iPushStyle2);
                                it = it3;
                                list = list3;
                            } catch (Throwable th3) {
                                th = th3;
                                builder.pop(iPushStyle2);
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                        }
                        break;
                    case 3:
                        int iPushStyle3 = builder.pushStyle(new SpanStyle(Color.INSTANCE.m5512getRed0d7_KjU(), 0L, (FontWeight) null, (FontStyle) null, (FontSynthesis) null, (FontFamily) null, (String) null, 0L, (BaselineShift) null, (TextGeometricTransform) null, (LocaleList) null, 0L, (TextDecoration) null, (Shadow) null, (PlatformSpanStyle) null, (DrawStyle) null, 65534, (DefaultConstructorMarker) null));
                        Iterator it4 = it;
                        try {
                            List list4 = list;
                            try {
                                try {
                                    builder.append("MASTG-DEMO-" + demoResult.getDemoId() + " failed:\n" + demoResult.getMessage() + "\n\n");
                                    Unit unit3 = Unit.INSTANCE;
                                    builder.pop(iPushStyle3);
                                    it = it4;
                                    list = list4;
                                } catch (Throwable th5) {
                                    th = th5;
                                    builder.pop(iPushStyle3);
                                    throw th;
                                }
                            } catch (Throwable th6) {
                                th = th6;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                        }
                        break;
                    default:
                        throw new NoWhenBranchMatchedException();
                }
            }
        } catch (Exception e) {
            builder.append(result);
        }
        return builder.toAnnotatedString();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static final void MainScreen(Composer composer, final int i) {
        Object next;
        Composer composerStartRestartGroup = composer.startRestartGroup(2010408835);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(MainScreen)96@3825L76,97@3929L34,98@3995L7,108@4545L46,111@4598L280,107@4510L368:MainActivity.kt#vyvp3i");
        int i2 = 1;
        int i3 = 0;
        if (composerStartRestartGroup.shouldExecute(i != 0, i & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(2010408835, i, -1, "org.owasp.mastestapp.MainScreen (MainActivity.kt:94)");
            }
            final String str = "Click \"Start\" to run the test.\n\n";
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -732389233, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue = composerStartRestartGroup.rememberedValue();
            Object[] objArr = 0;
            if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                AnnotatedString.Builder builder = new AnnotatedString.Builder(i3, i2, objArr == true ? 1 : 0);
                builder.append("Click \"Start\" to run the test.\n\n");
                MutableState mutableStateMutableStateOf$default = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(builder.toAnnotatedString(), null, 2, null);
                composerStartRestartGroup.updateRememberedValue(mutableStateMutableStateOf$default);
                objRememberedValue = mutableStateMutableStateOf$default;
            }
            final MutableState mutableState = (MutableState) objRememberedValue;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -732385947, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue2 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                MutableState mutableStateMutableStateOf$default2 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(Boolean.valueOf((boolean) i3), null, 2, null);
                composerStartRestartGroup.updateRememberedValue(mutableStateMutableStateOf$default2);
                objRememberedValue2 = mutableStateMutableStateOf$default2;
            }
            final MutableState mutableState2 = (MutableState) objRememberedValue2;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ProvidableCompositionLocal<Context> localContext = AndroidCompositionLocals_androidKt.getLocalContext();
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object objConsume = composerStartRestartGroup.consume(localContext);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            MastgTest mastgTest = new MastgTest((Context) objConsume);
            Iterator<T> it = Reflection.getOrCreateKotlinClass(MastgTest.class).getMembers().iterator();
            do {
                if (!it.hasNext()) {
                    next = null;
                    break;
                }
                next = it.next();
            } while (!Intrinsics.areEqual(((KCallable) next).getName(), "shouldRunInMainThread"));
            KCallable kCallable = (KCallable) next;
            Object objCall = kCallable != null ? kCallable.call(mastgTest) : null;
            Boolean bool = objCall instanceof Boolean ? (Boolean) objCall : null;
            if (bool != null) {
                bool.booleanValue();
            }
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -732366223, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue3 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                Function0 function0 = new Function0() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda2
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.MainScreen$lambda$7$0(mutableState2);
                    }
                };
                composerStartRestartGroup.updateRememberedValue(function0);
                objRememberedValue3 = function0;
            }
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            BaseScreenKt.BaseScreen((Function0) objRememberedValue3, ComposableLambdaKt.rememberComposableLambda(2027959928, true, new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda3
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.MainScreen$lambda$8(mutableState, (Composer) obj, ((Integer) obj2).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 54, 0);
            if (MainScreen$lambda$4(mutableState2)) {
                composerStartRestartGroup.startReplaceGroup(-1228153855);
                ComposerKt.sourceInformation(composerStartRestartGroup, "125@4953L158,124@4915L206");
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -732353055, "CC(remember):MainActivity.kt#9igjgp");
                Object objRememberedValue4 = composerStartRestartGroup.rememberedValue();
                if (objRememberedValue4 == Composer.INSTANCE.getEmpty()) {
                    Function1 function1 = new Function1() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda4
                        @Override // kotlin.jvm.functions.Function1
                        public final Object invoke(Object obj) {
                            return MainActivityKt.MainScreen$lambda$9$0(str, mutableState2, mutableState, (String) obj);
                        }
                    };
                    composerStartRestartGroup.updateRememberedValue(function1);
                    objRememberedValue4 = function1;
                }
                ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
                PaymentDialog((Function1) objRememberedValue4, composerStartRestartGroup, 6);
                composerStartRestartGroup.endReplaceGroup();
            } else {
                composerStartRestartGroup.startReplaceGroup(-1227940513);
                composerStartRestartGroup.endReplaceGroup();
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            composerStartRestartGroup.skipToGroupEnd();
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda5
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.MainScreen$lambda$10(i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    private static final AnnotatedString MainScreen$lambda$1(MutableState<AnnotatedString> mutableState) {
        return mutableState.getValue();
    }

    private static final boolean MainScreen$lambda$4(MutableState<Boolean> mutableState) {
        return mutableState.getValue().booleanValue();
    }

    private static final void MainScreen$lambda$5(MutableState<Boolean> mutableState, boolean z) {
        mutableState.setValue(Boolean.valueOf(z));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$7$0(MutableState $showPaymentForm$delegate) {
        MainScreen$lambda$5($showPaymentForm$delegate, true);
        return Unit.INSTANCE;
    }

    static final Unit MainScreen$lambda$8(MutableState $displayString$delegate, Composer $composer, int $changed) {
        ComposerKt.sourceInformation($composer, "C112@4608L264:MainActivity.kt#vyvp3i");
        if (!$composer.shouldExecute(($changed & 3) != 2, $changed & 1)) {
            $composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(2027959928, $changed, -1, "org.owasp.mastestapp.MainScreen.<anonymous> (MainActivity.kt:112)");
            }
            TextKt.m3326TextZ58ophY(MainScreen$lambda$1($displayString$delegate), TestTagKt.testTag(PaddingKt.m1089padding3ABfNKs(Modifier.INSTANCE, Dp.m8397constructorimpl(16)), MASTG_TEXT_TAG), Color.INSTANCE.m5515getWhite0d7_KjU(), null, TextUnitKt.getSp(16), null, null, FontFamily.INSTANCE.getMonospace(), 0L, null, null, 0L, 0, false, 0, 0, null, null, null, $composer, 25008, 0, 524136);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$9$0(String $defaultMessage, MutableState $showPaymentForm$delegate, MutableState $displayString$delegate, String message) {
        Intrinsics.checkNotNullParameter(message, "message");
        MainScreen$lambda$5($showPaymentForm$delegate, false);
        $displayString$delegate.setValue(UpdateDisplayString($defaultMessage, MainScreen$lambda$1($displayString$delegate), message));
        return Unit.INSTANCE;
    }

    public static final void PaymentDialog(final Function1<? super String, Unit> onResult, Composer $composer, final int $changed) {
        Composer $composer2;
        Intrinsics.checkNotNullParameter(onResult, "onResult");
        Composer $composer3 = $composer.startRestartGroup(-172528441);
        ComposerKt.sourceInformation($composer3, "C(PaymentDialog)N(onResult)136@5235L34,168@6934L270,174@7230L232,135@5195L2273:MainActivity.kt#vyvp3i");
        int $dirty = $changed;
        if (($changed & 6) == 0) {
            $dirty |= $composer3.changedInstance(onResult) ? 4 : 2;
        }
        if (!$composer3.shouldExecute(($dirty & 3) != 2, $dirty & 1)) {
            $composer2 = $composer3;
            $composer2.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-172528441, $dirty, -1, "org.owasp.mastestapp.PaymentDialog (MainActivity.kt:134)");
            }
            ComposerKt.sourceInformationMarkerStart($composer3, 1703895721, "CC(remember):MainActivity.kt#9igjgp");
            boolean z = ($dirty & 14) == 4;
            Object objRememberedValue = $composer3.rememberedValue();
            if (z || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                Object obj = new Function0() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda6
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.PaymentDialog$lambda$0$0(onResult);
                    }
                };
                $composer3.updateRememberedValue(obj);
                objRememberedValue = obj;
            }
            ComposerKt.sourceInformationMarkerEnd($composer3);
            $composer2 = $composer3;
            AndroidAlertDialog_androidKt.m2318AlertDialogOix01E0((Function0) objRememberedValue, ComposableLambdaKt.rememberComposableLambda(2057517839, true, new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda7
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj2, Object obj3) {
                    return MainActivityKt.PaymentDialog$lambda$1(onResult, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, $composer3, 54), null, ComposableLambdaKt.rememberComposableLambda(-636378351, true, new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda8
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj2, Object obj3) {
                    return MainActivityKt.PaymentDialog$lambda$2(onResult, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, $composer3, 54), null, ComposableSingletons$MainActivityKt.INSTANCE.getLambda$964692755$app(), ComposableSingletons$MainActivityKt.INSTANCE.getLambda$1765228308$app(), null, 0L, 0L, 0L, 0L, 0.0f, null, $composer2, 1772592, 0, 16276);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = $composer2.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda9
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj2, Object obj3) {
                    return MainActivityKt.PaymentDialog$lambda$3(onResult, $changed, (Composer) obj2, ((Integer) obj3).intValue());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PaymentDialog$lambda$0$0(Function1 $onResult) {
        $onResult.invoke("Payment cancelled.");
        return Unit.INSTANCE;
    }

    static final Unit PaymentDialog$lambda$1(final Function1 $onResult, Composer $composer, int $changed) {
        ComposerKt.sourceInformation($composer, "C170@7103L34,170@7086L108:MainActivity.kt#vyvp3i");
        if (!$composer.shouldExecute(($changed & 3) != 2, $changed & 1)) {
            $composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(2057517839, $changed, -1, "org.owasp.mastestapp.PaymentDialog.<anonymous> (MainActivity.kt:170)");
            }
            ComposerKt.sourceInformationMarkerStart($composer, 1589692561, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChanged = $composer.changed($onResult);
            Object objRememberedValue = $composer.rememberedValue();
            if (zChanged || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                Object obj = new Function0() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda0
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.PaymentDialog$lambda$1$0$0($onResult);
                    }
                };
                $composer.updateRememberedValue(obj);
                objRememberedValue = obj;
            }
            ComposerKt.sourceInformationMarkerEnd($composer);
            ButtonKt.Button((Function0) objRememberedValue, null, false, null, null, null, null, null, null, ComposableSingletons$MainActivityKt.INSTANCE.m10575getLambda$247347457$app(), $composer, 805306368, 510);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PaymentDialog$lambda$1$0$0(Function1 $onResult) {
        $onResult.invoke("Payment confirmed.");
        return Unit.INSTANCE;
    }

    static final Unit PaymentDialog$lambda$2(final Function1 $onResult, Composer $composer, int $changed) {
        ComposerKt.sourceInformation($composer, "C176@7370L34,176@7349L103:MainActivity.kt#vyvp3i");
        if (!$composer.shouldExecute(($changed & 3) != 2, $changed & 1)) {
            $composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-636378351, $changed, -1, "org.owasp.mastestapp.PaymentDialog.<anonymous> (MainActivity.kt:176)");
            }
            ComposerKt.sourceInformationMarkerStart($composer, -1674234765, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChanged = $composer.changed($onResult);
            Object objRememberedValue = $composer.rememberedValue();
            if (zChanged || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                Object obj = new Function0() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda1
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.PaymentDialog$lambda$2$0$0($onResult);
                    }
                };
                $composer.updateRememberedValue(obj);
                objRememberedValue = obj;
            }
            ComposerKt.sourceInformationMarkerEnd($composer);
            ButtonKt.TextButton((Function0) objRememberedValue, null, false, null, null, null, null, null, null, ComposableSingletons$MainActivityKt.INSTANCE.m10577getLambda$871720306$app(), $composer, 805306368, 510);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PaymentDialog$lambda$2$0$0(Function1 $onResult) {
        $onResult.invoke("Payment cancelled.");
        return Unit.INSTANCE;
    }
}
