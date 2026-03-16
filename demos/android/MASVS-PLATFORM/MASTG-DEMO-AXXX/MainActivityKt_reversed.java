package org.owasp.mastestapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.compose.foundation.layout.PaddingKt;
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
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KCallable;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonArray;
import kotlinx.serialization.json.JsonElement;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000\u0018\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\u001a\u001e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0001\u001a\r\u0010\u0007\u001a\u00020\bH\u0007¢\u0006\u0002\u0010\t\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n\u0000¨\u0006\n²\u0006\n\u0010\u0005\u001a\u00020\u0003X\u008a\u008e\u0002"}, d2 = {"MASTG_TEXT_TAG", "", "UpdateDisplayString", "Landroidx/compose/ui/text/AnnotatedString;", "defaultMessage", "displayString", "result", "MainScreen", "", "(Landroidx/compose/runtime/Composer;I)V", "app_debug"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MainActivityKt {
    public static final String MASTG_TEXT_TAG = "mastgTestText";

    /* compiled from: MainActivity.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    public /* synthetic */ class WhenMappings {
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

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$13(int i, Composer composer, int i2) {
        MainScreen(composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    public static final AnnotatedString UpdateDisplayString(String defaultMessage, AnnotatedString displayString, String result) throws Throwable {
        Intrinsics.checkNotNullParameter(defaultMessage, "defaultMessage");
        Intrinsics.checkNotNullParameter(displayString, "displayString");
        Intrinsics.checkNotNullParameter(result, "result");
        int $i$f$buildAnnotatedString = 0;
        AnnotatedString.Builder $this$UpdateDisplayString_u24lambda_u244 = new AnnotatedString.Builder(0, 1, null);
        $this$UpdateDisplayString_u24lambda_u244.append(defaultMessage);
        try {
            JsonElement toJsonElement = Json.INSTANCE.parseToJsonElement(result);
            Intrinsics.checkNotNull(toJsonElement, "null cannot be cast to non-null type kotlinx.serialization.json.JsonArray");
            Iterable jsonArrayFromString = (JsonArray) toJsonElement;
            Iterable $this$map$iv = jsonArrayFromString;
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
            for (Object item$iv$iv : $this$map$iv) {
                try {
                    JsonElement it = (JsonElement) item$iv$iv;
                    Json $this$decodeFromJsonElement$iv = Json.INSTANCE;
                    $this$decodeFromJsonElement$iv.getSerializersModule();
                    Iterable $this$map$iv2 = $this$map$iv;
                    destination$iv$iv.add((DemoResult) $this$decodeFromJsonElement$iv.decodeFromJsonElement(DemoResult.INSTANCE.serializer(), it));
                    $this$map$iv = $this$map$iv2;
                } catch (Exception e) {
                    $this$UpdateDisplayString_u24lambda_u244.append(result);
                    return $this$UpdateDisplayString_u24lambda_u244.toAnnotatedString();
                }
            }
            List demoResults = (List) destination$iv$iv;
            Iterator it2 = demoResults.iterator();
            while (it2.hasNext()) {
                DemoResult demoResult = (DemoResult) it2.next();
                switch (WhenMappings.$EnumSwitchMapping$0[demoResult.getStatus().ordinal()]) {
                    case 1:
                        Iterator it3 = it2;
                        List demoResults2 = demoResults;
                        int $i$f$buildAnnotatedString2 = $i$f$buildAnnotatedString;
                        SpanStyle style$iv = new SpanStyle(Color.INSTANCE.m5681getGreen0d7_KjU(), 0L, (FontWeight) null, (FontStyle) null, (FontSynthesis) null, (FontFamily) null, (String) null, 0L, (BaselineShift) null, (TextGeometricTransform) null, (LocaleList) null, 0L, (TextDecoration) null, (Shadow) null, (PlatformSpanStyle) null, (DrawStyle) null, 65534, (DefaultConstructorMarker) null);
                        int index$iv = $this$UpdateDisplayString_u24lambda_u244.pushStyle(style$iv);
                        try {
                            try {
                                $this$UpdateDisplayString_u24lambda_u244.append("MASTG-DEMO-" + demoResult.getDemoId() + " demonstrated a successful test:\n" + demoResult.getMessage() + "\n\n");
                                Unit unit = Unit.INSTANCE;
                                $this$UpdateDisplayString_u24lambda_u244.pop(index$iv);
                                $i$f$buildAnnotatedString = $i$f$buildAnnotatedString2;
                                it2 = it3;
                                demoResults = demoResults2;
                            } catch (Throwable th) {
                                th = th;
                                $this$UpdateDisplayString_u24lambda_u244.pop(index$iv);
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    case 2:
                        Iterator it4 = it2;
                        List demoResults3 = demoResults;
                        SpanStyle style$iv2 = new SpanStyle(ColorKt.Color(4294940672L), 0L, (FontWeight) null, (FontStyle) null, (FontSynthesis) null, (FontFamily) null, (String) null, 0L, (BaselineShift) null, (TextGeometricTransform) null, (LocaleList) null, 0L, (TextDecoration) null, (Shadow) null, (PlatformSpanStyle) null, (DrawStyle) null, 65534, (DefaultConstructorMarker) null);
                        int index$iv2 = $this$UpdateDisplayString_u24lambda_u244.pushStyle(style$iv2);
                        try {
                            try {
                                int $i$f$buildAnnotatedString3 = $i$f$buildAnnotatedString;
                                try {
                                    $this$UpdateDisplayString_u24lambda_u244.append("MASTG-DEMO-" + demoResult.getDemoId() + " demonstrated a failed test:\n" + demoResult.getMessage() + "\n\n");
                                    Unit unit2 = Unit.INSTANCE;
                                    try {
                                        $this$UpdateDisplayString_u24lambda_u244.pop(index$iv2);
                                        $i$f$buildAnnotatedString = $i$f$buildAnnotatedString3;
                                        it2 = it4;
                                        demoResults = demoResults3;
                                    } catch (Exception e2) {
                                        $this$UpdateDisplayString_u24lambda_u244.append(result);
                                        return $this$UpdateDisplayString_u24lambda_u244.toAnnotatedString();
                                    }
                                } catch (Throwable th3) {
                                    th = th3;
                                    $this$UpdateDisplayString_u24lambda_u244.pop(index$iv2);
                                    throw th;
                                }
                            } catch (Throwable th4) {
                                th = th4;
                            }
                        } catch (Throwable th5) {
                            th = th5;
                        }
                    case 3:
                        SpanStyle style$iv3 = new SpanStyle(Color.INSTANCE.m5684getRed0d7_KjU(), 0L, (FontWeight) null, (FontStyle) null, (FontSynthesis) null, (FontFamily) null, (String) null, 0L, (BaselineShift) null, (TextGeometricTransform) null, (LocaleList) null, 0L, (TextDecoration) null, (Shadow) null, (PlatformSpanStyle) null, (DrawStyle) null, 65534, (DefaultConstructorMarker) null);
                        int index$iv3 = $this$UpdateDisplayString_u24lambda_u244.pushStyle(style$iv3);
                        Iterator it5 = it2;
                        try {
                            List demoResults4 = demoResults;
                            try {
                                $this$UpdateDisplayString_u24lambda_u244.append("MASTG-DEMO-" + demoResult.getDemoId() + " failed:\n" + demoResult.getMessage() + "\n\n");
                                Unit unit3 = Unit.INSTANCE;
                                $this$UpdateDisplayString_u24lambda_u244.pop(index$iv3);
                                it2 = it5;
                                demoResults = demoResults4;
                            } catch (Throwable th6) {
                                th = th6;
                                $this$UpdateDisplayString_u24lambda_u244.pop(index$iv3);
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                        }
                    default:
                        throw new NoWhenBranchMatchedException();
                }
            }
        } catch (Exception e3) {
        }
        return $this$UpdateDisplayString_u24lambda_u244.toAnnotatedString();
    }

    public static final void MainScreen(Composer composer, final int i) {
        Object next;
        Composer composerStartRestartGroup = composer.startRestartGroup(2010408835);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(MainScreen)83@3086L76,84@3194L7,107@4292L280,93@3709L863:MainActivity.kt#vyvp3i");
        if (i == 0 && composerStartRestartGroup.getSkipping()) {
            composerStartRestartGroup.skipToGroupEnd();
        } else {
            final String str = "Click \"Start\" to run the test.\n\n";
            composerStartRestartGroup.startReplaceGroup(1922877357);
            ComposerKt.sourceInformation(composerStartRestartGroup, "CC(remember):MainActivity.kt#9igjgp");
            int i2 = 0;
            Object objRememberedValue = composerStartRestartGroup.rememberedValue();
            int i3 = 1;
            boolean z = false;
            if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                AnnotatedString.Builder builder = new AnnotatedString.Builder(i2, i3, z ? 1 : 0);
                builder.append("Click \"Start\" to run the test.\n\n");
                MutableState mutableStateMutableStateOf$default = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(builder.toAnnotatedString(), null, 2, null);
                composerStartRestartGroup.updateRememberedValue(mutableStateMutableStateOf$default);
                objRememberedValue = mutableStateMutableStateOf$default;
            }
            final MutableState mutableState = (MutableState) objRememberedValue;
            composerStartRestartGroup.endReplaceGroup();
            ProvidableCompositionLocal<Context> localContext = AndroidCompositionLocals_androidKt.getLocalContext();
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object objConsume = composerStartRestartGroup.consume(localContext);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            final MastgTest mastgTest = new MastgTest((Context) objConsume);
            Iterator<T> it = Reflection.getOrCreateKotlinClass(MastgTest.class).getMembers().iterator();
            while (true) {
                if (!it.hasNext()) {
                    next = null;
                    break;
                } else {
                    next = it.next();
                    if (Intrinsics.areEqual(((KCallable) next).getName(), "shouldRunInMainThread")) {
                        break;
                    }
                }
            }
            KCallable kCallable = (KCallable) next;
            Object objCall = kCallable != null ? kCallable.call(mastgTest) : null;
            Boolean bool = objCall instanceof Boolean ? (Boolean) objCall : null;
            final boolean zBooleanValue = bool != null ? bool.booleanValue() : false;
            BaseScreenKt.BaseScreen(new Function0() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return MainActivityKt.MainScreen$lambda$12(zBooleanValue, mastgTest, str, mutableState);
                }
            }, ComposableLambdaKt.rememberComposableLambda(2027959928, true, new Function2<Composer, Integer, Unit>() { // from class: org.owasp.mastestapp.MainActivityKt.MainScreen.2
                @Override // kotlin.jvm.functions.Function2
                public /* bridge */ /* synthetic */ Unit invoke(Composer composer2, Integer num) {
                    invoke(composer2, num.intValue());
                    return Unit.INSTANCE;
                }

                public final void invoke(Composer $composer, int $changed) {
                    ComposerKt.sourceInformation($composer, "C108@4302L264:MainActivity.kt#vyvp3i");
                    if (($changed & 11) != 2 || !$composer.getSkipping()) {
                        TextKt.m3575TextZ58ophY(MainActivityKt.MainScreen$lambda$7(mutableState), TestTagKt.testTag(PaddingKt.m830padding3ABfNKs(Modifier.INSTANCE, Dp.m8394constructorimpl(16)), MainActivityKt.MASTG_TEXT_TAG), Color.INSTANCE.m5687getWhite0d7_KjU(), null, TextUnitKt.getSp(16), null, null, FontFamily.INSTANCE.getMonospace(), 0L, null, null, 0L, 0, false, 0, 0, null, null, null, $composer, 25008, 0, 524136);
                    } else {
                        $composer.skipToGroupEnd();
                    }
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 48, 0);
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda1
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.MainScreen$lambda$13(i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final AnnotatedString MainScreen$lambda$7(MutableState<AnnotatedString> mutableState) {
        MutableState<AnnotatedString> $this$getValue$iv = mutableState;
        return $this$getValue$iv.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$12(boolean $runInMainThread, final MastgTest mastgTestClass, final String defaultMessage, final MutableState displayString$delegate) {
        Intrinsics.checkNotNullParameter(mastgTestClass, "$mastgTestClass");
        Intrinsics.checkNotNullParameter(defaultMessage, "$defaultMessage");
        Intrinsics.checkNotNullParameter(displayString$delegate, "$displayString$delegate");
        if ($runInMainThread) {
            String result = mastgTestClass.mastgTest();
            displayString$delegate.setValue(UpdateDisplayString(defaultMessage, MainScreen$lambda$7(displayString$delegate), result));
        } else {
            new Thread(new Runnable() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivityKt.MainScreen$lambda$12$lambda$11(mastgTestClass, defaultMessage, displayString$delegate);
                }
            }).start();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void MainScreen$lambda$12$lambda$11(MastgTest mastgTestClass, final String defaultMessage, final MutableState displayString$delegate) {
        Intrinsics.checkNotNullParameter(mastgTestClass, "$mastgTestClass");
        Intrinsics.checkNotNullParameter(defaultMessage, "$defaultMessage");
        Intrinsics.checkNotNullParameter(displayString$delegate, "$displayString$delegate");
        final String result = mastgTestClass.mastgTest();
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: org.owasp.mastestapp.MainActivityKt$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MainActivityKt.MainScreen$lambda$12$lambda$11$lambda$10(defaultMessage, result, displayString$delegate);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void MainScreen$lambda$12$lambda$11$lambda$10(String defaultMessage, String result, MutableState displayString$delegate) {
        Intrinsics.checkNotNullParameter(defaultMessage, "$defaultMessage");
        Intrinsics.checkNotNullParameter(result, "$result");
        Intrinsics.checkNotNullParameter(displayString$delegate, "$displayString$delegate");
        displayString$delegate.setValue(UpdateDisplayString(defaultMessage, MainScreen$lambda$7(displayString$delegate), result));
    }
}
