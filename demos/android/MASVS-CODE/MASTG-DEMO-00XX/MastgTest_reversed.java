package org.owasp.mastestapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001:\u0002\b\tB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "StudentProvider", "DatabaseHelper", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        Log.d("MASTG-TEST", "Hello from the OWASP MASTG Test app.");
        return "Hello from the OWASP MASTG Test app.";
    }

    /* compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0007\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0006\u001a\u00020\u0007H\u0016JK\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u000e\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\r2\b\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u000e\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\r2\b\u0010\u0011\u001a\u0004\u0018\u00010\u000eH\u0016¢\u0006\u0002\u0010\u0012J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0016J1\u0010\u0017\u001a\u00020\u00182\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u0010\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\rH\u0016¢\u0006\u0002\u0010\u0019J;\u0010\u001a\u001a\u00020\u00182\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u0010\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\rH\u0016¢\u0006\u0002\u0010\u001bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000¨\u0006\u001d"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$StudentProvider;", "Landroid/content/ContentProvider;", "<init>", "()V", "dbHelper", "Lorg/owasp/mastestapp/MastgTest$DatabaseHelper;", "onCreate", "", "query", "Landroid/database/Cursor;", "uri", "Landroid/net/Uri;", "projection", "", "", "selection", "selectionArgs", "sortOrder", "(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;", "getType", "insert", "values", "Landroid/content/ContentValues;", "delete", "", "(Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I", "update", "(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class StudentProvider extends ContentProvider {
        public static final String AUTHORITY = "org.owasp.mastestapp.provider";
        public static final int STUDENTS = 1;
        public static final int STUDENT_ID = 2;
        private static final UriMatcher uriMatcher;
        private DatabaseHelper dbHelper;

        /* renamed from: Companion, reason: from kotlin metadata */
        public static final Companion INSTANCE = new Companion(null);
        public static final int $stable = 8;

        /* compiled from: MastgTest.kt */
        @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\r"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$StudentProvider$Companion;", "", "<init>", "()V", "AUTHORITY", "", "STUDENTS", "", "STUDENT_ID", "uriMatcher", "Landroid/content/UriMatcher;", "getUriMatcher", "()Landroid/content/UriMatcher;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            public final UriMatcher getUriMatcher() {
                return StudentProvider.uriMatcher;
            }
        }

        static {
            UriMatcher $this$uriMatcher_u24lambda_u240 = new UriMatcher(-1);
            $this$uriMatcher_u24lambda_u240.addURI(AUTHORITY, "students", 1);
            $this$uriMatcher_u24lambda_u240.addURI(AUTHORITY, "students/#", 2);
            uriMatcher = $this$uriMatcher_u24lambda_u240;
        }

        @Override // android.content.ContentProvider
        public boolean onCreate() {
            Context context = getContext();
            Intrinsics.checkNotNull(context);
            this.dbHelper = new DatabaseHelper(context);
            return true;
        }

        @Override // android.content.ContentProvider
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            DatabaseHelper databaseHelper = this.dbHelper;
            if (databaseHelper == null) {
                Intrinsics.throwUninitializedPropertyAccessException("dbHelper");
                databaseHelper = null;
            }
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables("students");
            switch (uriMatcher.match(uri)) {
                case 1:
                    break;
                case 2:
                    String id = uri.getPathSegments().get(1);
                    qb.appendWhere("id=" + id);
                    Log.e("SQLI", "Injected ID segment: " + id);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            Context context = getContext();
            Intrinsics.checkNotNull(context);
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        }

        @Override // android.content.ContentProvider
        public String getType(Uri uri) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            return null;
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
    }

    /* compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J \u0010\n\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\u0016¨\u0006\u000e"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$DatabaseHelper;", "Landroid/database/sqlite/SQLiteOpenHelper;", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "onCreate", "", "db", "Landroid/database/sqlite/SQLiteDatabase;", "onUpgrade", "oldVersion", "", "newVersion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class DatabaseHelper extends SQLiteOpenHelper {
        public static final int $stable = 0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DatabaseHelper(Context context) {
            super(context, "students.db", (SQLiteDatabase.CursorFactory) null, 1);
            Intrinsics.checkNotNullParameter(context, "context");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) throws SQLException {
            Intrinsics.checkNotNullParameter(db, "db");
            db.execSQL("CREATE TABLE students (id INTEGER PRIMARY KEY, name TEXT)");
            db.execSQL("INSERT INTO students (name) VALUES ('Alice')");
            db.execSQL("INSERT INTO students (name) VALUES ('Bob')");
            db.execSQL("INSERT INTO students (name) VALUES ('Charlie')");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLException {
            Intrinsics.checkNotNullParameter(db, "db");
            db.execSQL("DROP TABLE IF EXISTS students");
            onCreate(db);
        }
    }
}
