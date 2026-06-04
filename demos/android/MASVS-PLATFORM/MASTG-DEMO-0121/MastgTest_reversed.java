package org.owasp.mastestapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import androidx.autofill.HintConstants;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: MastgTest.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001:\u0002\b\tB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "AppointmentDbHelper", "AppointmentProvider", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        DemoResults r = new DemoResults("0121");
        try {
            this.context.openOrCreateDatabase(AppointmentDbHelper.DB_NAME, 0, null).close();
            r.add(Status.FAIL, "Appointment database initialized. Any app can read patient records via: content://org.owasp.mastestapp.appointments/appointments");
            return r.toJson();
        } catch (Exception e) {
            r.add(Status.ERROR, "Initialization error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return r.toJson();
        }
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0007\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J \u0010\n\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\u0016J@\u0010\u000e\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u0010H\u0002¨\u0006\u0017"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$AppointmentDbHelper;", "Landroid/database/sqlite/SQLiteOpenHelper;", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "onCreate", "", "db", "Landroid/database/sqlite/SQLiteDatabase;", "onUpgrade", "oldVersion", "", "newVersion", "insert", HintConstants.AUTOFILL_HINT_NAME, "", "dob", "date", "doctor", "diagnosis", "notes", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class AppointmentDbHelper extends SQLiteOpenHelper {
        public static final int $stable = 0;
        public static final String DB_NAME = "appointments.db";
        public static final int DB_VERSION = 1;
        public static final String TABLE_APPOINTMENTS = "appointments";

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AppointmentDbHelper(Context context) {
            super(context, DB_NAME, (SQLiteDatabase.CursorFactory) null, 1);
            Intrinsics.checkNotNullParameter(context, "context");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            Intrinsics.checkNotNullParameter(db, "db");
            db.execSQL("CREATE TABLE appointments (\n  _id INTEGER PRIMARY KEY AUTOINCREMENT,\n  patient_name TEXT NOT NULL,\n  dob TEXT NOT NULL,\n  appointment_date TEXT NOT NULL,\n  doctor TEXT NOT NULL,\n  diagnosis TEXT,\n  notes TEXT\n)");
            insert(db, "Maria Garcia", "1988-03-14", "2026-04-10", "Dr. Chen", "Type 2 Diabetes", "Adjust metformin dosage to 1000mg twice daily");
            insert(db, "James Wilson", "1975-09-27", "2026-04-11", "Dr. Patel", "Essential Hypertension", "Refer to cardiology; increase lisinopril to 20mg");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Intrinsics.checkNotNullParameter(db, "db");
            db.execSQL("DROP TABLE IF EXISTS appointments");
            onCreate(db);
        }

        private final void insert(SQLiteDatabase db, String name, String dob, String date, String doctor, String diagnosis, String notes) {
            ContentValues cv = new ContentValues();
            cv.put("patient_name", name);
            cv.put("dob", dob);
            cv.put("appointment_date", date);
            cv.put("doctor", doctor);
            cv.put("diagnosis", diagnosis);
            cv.put("notes", notes);
            db.insert(TABLE_APPOINTMENTS, null, cv);
        }
    }

    /* JADX INFO: compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0007\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0006\u001a\u00020\u0007H\u0016JO\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0010\u0010\f\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\r2\b\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u0010\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\r2\b\u0010\u0011\u001a\u0004\u0018\u00010\u000eH\u0016¢\u0006\u0002\u0010\u0012J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0016J1\u0010\u0017\u001a\u00020\u00182\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u0010\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\rH\u0016¢\u0006\u0002\u0010\u0019J;\u0010\u001a\u001a\u00020\u00182\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u0010\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\rH\u0016¢\u0006\u0002\u0010\u001bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000¨\u0006\u001d"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$AppointmentProvider;", "Landroid/content/ContentProvider;", "<init>", "()V", "db", "Lorg/owasp/mastestapp/MastgTest$AppointmentDbHelper;", "onCreate", "", "query", "Landroid/database/Cursor;", "uri", "Landroid/net/Uri;", "projection", "", "", "selection", "selectionArgs", "sortOrder", "(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;", "getType", "insert", "values", "Landroid/content/ContentValues;", "delete", "", "(Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I", "update", "(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class AppointmentProvider extends ContentProvider {
        private static final String AUTH = "org.owasp.mastestapp.appointments";
        private static final UriMatcher MATCHER;
        private static final int MATCH_APPOINTMENTS = 1;
        private static final int MATCH_APPOINTMENT_BY_ID = 2;
        private AppointmentDbHelper db;
        public static final int $stable = 8;

        @Override // android.content.ContentProvider
        public boolean onCreate() {
            Context context = getContext();
            if (context != null) {
                this.db = new AppointmentDbHelper(context);
                return true;
            }
            throw new IllegalArgumentException("Required value was null.".toString());
        }

        @Override // android.content.ContentProvider
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            AppointmentDbHelper appointmentDbHelper = this.db;
            if (appointmentDbHelper == null) {
                Intrinsics.throwUninitializedPropertyAccessException("db");
                appointmentDbHelper = null;
            }
            SQLiteDatabase readableDb = appointmentDbHelper.getReadableDatabase();
            switch (MATCHER.match(uri)) {
                case 1:
                    return readableDb.query(AppointmentDbHelper.TABLE_APPOINTMENTS, null, null, null, null, null, "_id ASC");
                case 2:
                    long id = ContentUris.parseId(uri);
                    return readableDb.query(AppointmentDbHelper.TABLE_APPOINTMENTS, null, "_id=?", new String[]{String.valueOf(id)}, null, null, "_id ASC");
                default:
                    return null;
            }
        }

        @Override // android.content.ContentProvider
        public String getType(Uri uri) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            switch (MATCHER.match(uri)) {
                case 1:
                    return "vnd.android.cursor.dir/vnd.mastestapp.appointment";
                case 2:
                    return "vnd.android.cursor.item/vnd.mastestapp.appointment";
                default:
                    return null;
            }
        }

        @Override // android.content.ContentProvider
        public Uri insert(Uri uri, ContentValues values) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            return null;
        }

        @Override // android.content.ContentProvider
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            return 0;
        }

        @Override // android.content.ContentProvider
        public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            return 0;
        }

        static {
            UriMatcher $this$MATCHER_u24lambda_u240 = new UriMatcher(-1);
            $this$MATCHER_u24lambda_u240.addURI(AUTH, AppointmentDbHelper.TABLE_APPOINTMENTS, 1);
            $this$MATCHER_u24lambda_u240.addURI(AUTH, "appointments/#", 2);
            MATCHER = $this$MATCHER_u24lambda_u240;
        }
    }
}