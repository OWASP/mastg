package org.owasp.mastestapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import androidx.autofill.HintConstants;
import java.io.File;
import kotlin.Metadata;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MastgTest.kt */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001:\u0003\b\t\nB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lorg/owasp/mastestapp/MastgTest;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "mastgTest", "", "CredentialDbHelper", "CredentialProvider", "FileLeakProvider", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class MastgTest_reversed {
    public static final int $stable = 8;
    private final Context context;

    public MastgTest_reversed(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    public final String mastgTest() {
        DemoResults r = new DemoResults("0x07IP");
        try {
            File secretFile = new File(this.context.getFilesDir(), "secret.txt");
            FilesKt.writeText$default(secretFile, "TOP_SECRET_TOKEN=tok_live_12345\nPIN=9876\n", null, 2, null);
            this.context.openOrCreateDatabase(CredentialDbHelper.DB_NAME, 0, null).close();
            r.add(Status.FAIL, "Initialized secret.txt and seeded creds. App exposes them via exported ContentProviders (IPC). Try: content://org.owasp.mastestapp.credentials/credentials and content://org.owasp.mastestapp.files/files/secret.txt");
            return r.toJson();
        } catch (Exception e) {
            r.add(Status.ERROR, "Initialization error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return r.toJson();
        }
    }

    /* compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0007\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J \u0010\n\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\u0016J(\u0010\u000e\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0010H\u0002¨\u0006\u0014"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$CredentialDbHelper;", "Landroid/database/sqlite/SQLiteOpenHelper;", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "onCreate", "", "db", "Landroid/database/sqlite/SQLiteDatabase;", "onUpgrade", "oldVersion", "", "newVersion", "insert", "user", "", "pass", "note", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class CredentialDbHelper extends SQLiteOpenHelper {
        public static final int $stable = 0;
        public static final String DB_NAME = "creds.db";
        public static final int DB_VERSION = 1;
        public static final String TABLE_CREDENTIALS = "credentials";

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public CredentialDbHelper(Context context) {
            super(context, DB_NAME, (SQLiteDatabase.CursorFactory) null, 1);
            Intrinsics.checkNotNullParameter(context, "context");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) throws SQLException {
            Intrinsics.checkNotNullParameter(db, "db");
            db.execSQL("CREATE TABLE credentials (\n  _id INTEGER PRIMARY KEY AUTOINCREMENT,\n  username TEXT NOT NULL,\n  password TEXT NOT NULL,\n  note TEXT\n)");
            insert(db, "admin", "StrongPwd!2026", "prod account");
            insert(db, "test", "test1234", "dev account");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLException {
            Intrinsics.checkNotNullParameter(db, "db");
            db.execSQL("DROP TABLE IF EXISTS credentials");
            onCreate(db);
        }

        private final void insert(SQLiteDatabase db, String user, String pass, String note) {
            ContentValues cv = new ContentValues();
            cv.put(HintConstants.AUTOFILL_HINT_USERNAME, user);
            cv.put(HintConstants.AUTOFILL_HINT_PASSWORD, pass);
            cv.put("note", note);
            db.insert(TABLE_CREDENTIALS, null, cv);
        }
    }

    /* compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0007\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0006\u001a\u00020\u0007H\u0016JO\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0010\u0010\f\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\r2\b\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u0010\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\r2\b\u0010\u0011\u001a\u0004\u0018\u00010\u000eH\u0016¢\u0006\u0002\u0010\u0012J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0016J1\u0010\u0017\u001a\u00020\u00182\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u0010\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\rH\u0016¢\u0006\u0002\u0010\u0019J;\u0010\u001a\u001a\u00020\u00182\u0006\u0010\n\u001a\u00020\u000b2\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u0010\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\rH\u0016¢\u0006\u0002\u0010\u001bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000¨\u0006\u001d"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$CredentialProvider;", "Landroid/content/ContentProvider;", "<init>", "()V", "db", "Lorg/owasp/mastestapp/MastgTest$CredentialDbHelper;", "onCreate", "", "query", "Landroid/database/Cursor;", "uri", "Landroid/net/Uri;", "projection", "", "", "selection", "selectionArgs", "sortOrder", "(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;", "getType", "insert", "values", "Landroid/content/ContentValues;", "delete", "", "(Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I", "update", "(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I", "Companion", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class CredentialProvider extends ContentProvider {
        private static final String AUTH = "org.owasp.mastestapp.credentials";
        private static final UriMatcher MATCHER;
        private static final int MATCH_CREDENTIALS = 1;
        private static final int MATCH_CREDENTIAL_BY_ID = 2;
        private CredentialDbHelper db;
        public static final int $stable = 8;

        @Override // android.content.ContentProvider
        public boolean onCreate() {
            Context context = getContext();
            if (context != null) {
                this.db = new CredentialDbHelper(context);
                return true;
            }
            throw new IllegalArgumentException("Required value was null.".toString());
        }

        @Override // android.content.ContentProvider
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            CredentialDbHelper credentialDbHelper = this.db;
            if (credentialDbHelper == null) {
                Intrinsics.throwUninitializedPropertyAccessException("db");
                credentialDbHelper = null;
            }
            SQLiteDatabase readableDb = credentialDbHelper.getReadableDatabase();
            switch (MATCHER.match(uri)) {
                case 1:
                    return readableDb.query(CredentialDbHelper.TABLE_CREDENTIALS, new String[]{"_id", HintConstants.AUTOFILL_HINT_USERNAME, HintConstants.AUTOFILL_HINT_PASSWORD, "note"}, null, null, null, null, "_id ASC");
                case 2:
                    long id = ContentUris.parseId(uri);
                    return readableDb.query(CredentialDbHelper.TABLE_CREDENTIALS, new String[]{"_id", HintConstants.AUTOFILL_HINT_USERNAME, HintConstants.AUTOFILL_HINT_PASSWORD, "note"}, "_id=?", new String[]{String.valueOf(id)}, null, null, "_id ASC");
                default:
                    return null;
            }
        }

        @Override // android.content.ContentProvider
        public String getType(Uri uri) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            switch (MATCHER.match(uri)) {
                case 1:
                    return "vnd.android.cursor.dir/vnd.mastestapp.credential";
                case 2:
                    return "vnd.android.cursor.item/vnd.mastestapp.credential";
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
            $this$MATCHER_u24lambda_u240.addURI(AUTH, CredentialDbHelper.TABLE_CREDENTIALS, 1);
            $this$MATCHER_u24lambda_u240.addURI(AUTH, "credentials/#", 2);
            MATCHER = $this$MATCHER_u24lambda_u240;
        }
    }

    /* compiled from: MastgTest.kt */
    @Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\u001a\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016JO\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\b\u001a\u00020\t2\u0010\u0010\u000e\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000b\u0018\u00010\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000b2\u0010\u0010\u0011\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000b\u0018\u00010\u000f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u000bH\u0016¢\u0006\u0002\u0010\u0013J\u0012\u0010\u0014\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\b\u001a\u00020\tH\u0016J\u001c\u0010\u0015\u001a\u0004\u0018\u00010\t2\u0006\u0010\b\u001a\u00020\t2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0016J1\u0010\u0018\u001a\u00020\u00192\u0006\u0010\b\u001a\u00020\t2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000b2\u0010\u0010\u0011\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000b\u0018\u00010\u000fH\u0016¢\u0006\u0002\u0010\u001aJ;\u0010\u001b\u001a\u00020\u00192\u0006\u0010\b\u001a\u00020\t2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\u0010\u0010\u001a\u0004\u0018\u00010\u000b2\u0010\u0010\u0011\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000b\u0018\u00010\u000fH\u0016¢\u0006\u0002\u0010\u001c¨\u0006\u001d"}, d2 = {"Lorg/owasp/mastestapp/MastgTest$FileLeakProvider;", "Landroid/content/ContentProvider;", "<init>", "()V", "onCreate", "", "openFile", "Landroid/os/ParcelFileDescriptor;", "uri", "Landroid/net/Uri;", "mode", "", "query", "Landroid/database/Cursor;", "projection", "", "selection", "selectionArgs", "sortOrder", "(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;", "getType", "insert", "values", "Landroid/content/ContentValues;", "delete", "", "(Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I", "update", "(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class FileLeakProvider extends ContentProvider {
        public static final int $stable = 0;

        @Override // android.content.ContentProvider
        public boolean onCreate() {
            return true;
        }

        @Override // android.content.ContentProvider
        public ParcelFileDescriptor openFile(Uri uri, String mode) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            Intrinsics.checkNotNullParameter(mode, "mode");
            String relative = uri.getLastPathSegment();
            if (relative == null) {
                return null;
            }
            Context context = getContext();
            if (context == null) {
                throw new IllegalArgumentException("Required value was null.".toString());
            }
            File base = context.getFilesDir();
            File target = new File(base, relative);
            return ParcelFileDescriptor.open(target, 268435456);
        }

        @Override // android.content.ContentProvider
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            return null;
        }

        @Override // android.content.ContentProvider
        public String getType(Uri uri) {
            Intrinsics.checkNotNullParameter(uri, "uri");
            return "application/octet-stream";
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
}
