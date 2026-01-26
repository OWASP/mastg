package org.owasp.mastestapp;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composer;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.viewinterop.AndroidViewKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Lambda;

public final class MainActivity extends ComponentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.enableEdgeToEdge();
        this.setContent(new Lambda(0) {
            public final void invoke(Composer $composer, int $changed) {
                MainScreenKt.MainScreen($composer, 0);
            }
        });
    }
}

public final class MainScreenKt {
    public static final void MainScreen(Composer $composer, int $changed) {
        Composer $composer2 = $composer.startRestartGroup(0);
        if ($changed == 0) {
            if (!$composer2.getSkipping()) {
                ColumnKt.m586Column(PaddingKt.m565padding(Modifier.Companion, Dp.m5307constructorimpl(16)), null, null, new Lambda(3) {
                    public final void invoke(ColumnScope $this$Column, Composer $composer, int $changed) {
                        Composer $composer2 = $composer;
                        ColumnScope columnScope = $this$Column;
                        
                        // FAIL: [MASTG-TEST-0035] Vulnerable button without overlay protection
                        ButtonKt.m1334Button(new Lambda(0) {
                            public final void invoke() {
                                // Sensitive action: confirming a payment
                            }
                        }, PaddingKt.m565padding(Modifier.Companion.fillMaxWidth(), Dp.m5307constructorimpl(8)), false, null, null, null, null, null, new Lambda(3) {
                            public final void invoke(RowScope $this$Button, Composer $composer, int $changed) {
                                TextKt.m3574Text("Vulnerable: Confirm Payment", null, 0L, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, null, null, $composer, 0, 0, 131070);
                            }
                        }, $composer2, 805306368, 508);
                        
                        // PASS: [MASTG-TEST-0035] Button with overlay protection
                        AndroidViewKt.m4555AndroidView(new Lambda(1) {
                            public final Button invoke(Context context) {
                                Button button = new Button(context);
                                button.setText("Protected: Confirm Payment");
                                button.setFilterTouchesWhenObscured(true);
                                button.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        Toast.makeText(context, "Payment confirmed", 0).show();
                                    }
                                });
                                return button;
                            }
                        }, PaddingKt.m565padding(Modifier.Companion.fillMaxWidth(), Dp.m5307constructorimpl(8)), null, null, $composer2, 3080, 12);
                        
                        // PASS: [MASTG-TEST-0035] Custom view with manual obscured check
                        AndroidViewKt.m4555AndroidView(new Lambda(1) {
                            public final Button invoke(Context context) {
                                return new Button(context) {
                                    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
                                        if ((event.getFlags() & MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0) {
                                            Toast.makeText(this.getContext(), "Touch blocked - window obscured", 0).show();
                                            return false;
                                        }
                                        return super.onFilterTouchEventForSecurity(event);
                                    }
                                    
                                    {
                                        this.setText("Custom Protection: Grant Permission");
                                        this.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View view) {
                                                Toast.makeText(Button.this.getContext(), "Permission granted", 0).show();
                                            }
                                        });
                                    }
                                };
                            }
                        }, PaddingKt.m565padding(Modifier.Companion.fillMaxWidth(), Dp.m5307constructorimpl(8)), null, null, $composer2, 3080, 12);
                    }
                }, $composer2, 438, 6);
            }
        }
    }
}
