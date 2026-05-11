package org.owasp.mastestapp;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0002J\u0006\u0010\b\u001a\u00020\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "writeSensitiveDataToUnencryptedRoom", "", "mastgTest", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    private final void writeSensitiveDataToUnencryptedRoom() {
        try {
            try {
                AppDatabase db = AppDatabase.INSTANCE.getInstance(this.context);
                UserDao dao = db.userDao();
                Log.i("MASTG-ROOM", "Clearing existing users...");
                dao.clear();
                Log.i("MASTG-ROOM", "Inserting new user...");
                dao.insert(new UserEntity(0, "demoUser_room", "john.doe@maswe.com", "ghp_1234567890abcdefghijklmnopqrstuvABCD", 1, null));
                List allUsers = dao.getAll();
                Log.i("MASTG-ROOM", "Users in DB after insert: " + allUsers.size());
                List $this$forEach$iv = allUsers;
                int $i$f$forEach = 0;
                for (Iterator it = $this$forEach$iv.iterator(); it.hasNext(); it = it) {
                    Object element$iv = it.next();
                    UserEntity it2 = (UserEntity) element$iv;
                    List allUsers2 = allUsers;
                    Log.i("MASTG-ROOM", "User: " + it2.getUsername() + ", " + it2.getEmail() + ", " + it2.getToken());
                    allUsers = allUsers2;
                    $this$forEach$iv = $this$forEach$iv;
                    $i$f$forEach = $i$f$forEach;
                }
                Log.i("MASTG-ROOM", "Room DB written successfully: PrivateUnencryptedRoomDB");
            } catch (Exception e) {
                e = e;
                Log.e("MASTG-ROOM", "Error writing Room DB: " + e.getMessage());
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public final String mastgTest() {
        Log.i("MASTG-ROOM", "Starting MastgTest RoomDB demo...");
        writeSensitiveDataToUnencryptedRoom();
        File dbPath = this.context.getDatabasePath("PrivateUnencryptedRoomDB");
        Log.i("MASTG-ROOM", "Database final path: " + dbPath.getAbsolutePath());
        return "RoomDB Demo Complete. Plaintext PII + Token stored in unencrypted Room database.";
    }
}
