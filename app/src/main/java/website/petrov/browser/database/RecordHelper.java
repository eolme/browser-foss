package website.petrov.browser.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import website.petrov.browser.BuildConfig;
import website.petrov.browser.unit.RecordUnit;

public class RecordHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "suze.db";

    private static final int DATABASE_VERSION = BuildConfig.VERSION_CODE;

    RecordHelper(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase database) {
        database.execSQL(RecordUnit.CREATE_BOOKMARKS);
        database.execSQL(RecordUnit.CREATE_HISTORY);
        database.execSQL(RecordUnit.CREATE_WHITELIST);
        database.execSQL(RecordUnit.CREATE_JAVASCRIPT);
        database.execSQL(RecordUnit.CREATE_COOKIE);
        database.execSQL(RecordUnit.CREATE_GRID);
    }

    // UPGRADE ATTENTION!!!
    @Override
    public void onUpgrade(@NonNull SQLiteDatabase database, int oldVersion, int newVersion) {
    }
}
