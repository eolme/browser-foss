/*
 * Open source android application.
 *
 * Copyright (c) 2017 Gaukler Faun
 * Copyright (c) 2019 Petrov Anton
 *
 * This file is part of Suze Browser.
 *
 * Suze Browser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Suze Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Suze Browser. If not, see <https://www.gnu.org/licenses/>.
 */

package website.petrov.browser.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import website.petrov.browser.BuildConfig;
import website.petrov.browser.unit.HelperUnit;

public class BookmarkList {

    public static final String DATABASE_NAME = "suze_pass.db";
    private static final String TAG = "BookmarkList";
    //define static variable
    private static final int DATABASE_VERSION = BuildConfig.VERSION_CODE;
    private static final String dbTable = "pass";
    //establish connection with SQLiteDataBase
    @NonNull
    private final Context c;
    @Nullable
    private SQLiteDatabase sqlDb;

    public BookmarkList(@NonNull Context context) {
        this.c = context;
    }

    public void open() {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (sqlDb != null && sqlDb.isOpen()) {
            sqlDb.close();
        }
    }

    //insert data
    public void insert(@NonNull String passTitle, @NonNull String passContent,
                       @NonNull String passIcon, @NonNull String passAttachment,
                       @NonNull String passCreation) {
        if (sqlDb != null && sqlDb.isOpen()) {
            if (!isExist(passContent)) {
                sqlDb.execSQL("INSERT INTO pass (pass_title, pass_content, pass_icon, pass_attachment, pass_creation) VALUES('" + passTitle + "','" + passContent + "','" + passIcon + "','" + passAttachment + "','" + passCreation + "')");
            }
        } else {
            Log.e(TAG, "DB must be opened");
        }
    }

    //check entry already in database or not
    public boolean isExist(@NonNull String passContent) {
        if (sqlDb != null && sqlDb.isOpen()) {
            String query = "SELECT pass_title FROM pass WHERE pass_content='" + passContent + "' LIMIT 1";
            Cursor row = sqlDb.rawQuery(query, null);
            boolean exist = row.moveToFirst();
            row.close();
            return exist;
        } else {
            Log.e(TAG, "DB must be opened");
        }
        return false;
    }

    //edit data
    public void update(int id, @NonNull String passTitle, @NonNull String passContent,
                       @NonNull String passIcon, @NonNull String passAttachment,
                       @NonNull String passCreation) {
        if (sqlDb != null && sqlDb.isOpen()) {
            sqlDb.execSQL("UPDATE " + dbTable + " SET pass_title='" + passTitle + "', pass_content='" + passContent + "', pass_icon='" + passIcon + "', pass_attachment='" + passAttachment + "', pass_creation='" + passCreation + "'   WHERE _id=" + id);
        } else {
            Log.e(TAG, "DB must be opened");
        }
    }

    //delete data
    public void delete(int id) {
        if (sqlDb != null && sqlDb.isOpen()) {
            sqlDb.execSQL("DELETE FROM " + dbTable + " WHERE _id=" + id);
        } else {
            Log.e(TAG, "DB must be opened");
        }
    }

    //fetch data
    @Nullable
    public Cursor fetchAllData(@NonNull Context activity) {
        if (sqlDb != null && sqlDb.isOpen()) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);

            String[] columns = new String[]{"_id", "pass_title", "pass_content", "pass_icon", "pass_attachment", "pass_creation"};

            switch (HelperUnit.safeGetString(sp, "sortDBB", "title")) {
                case "title":
                    return sqlDb.query(dbTable, columns, null, null, null, null, "pass_title" + " COLLATE NOCASE ASC;");

                case "icon": {
                    String orderBy = "pass_creation" + "," + "pass_title" + " COLLATE NOCASE ASC;";
                    return sqlDb.query(dbTable, columns, null, null, null, null, orderBy);
                }
            }
        } else {
            Log.e(TAG, "DB must be opened");
        }

        return null;
    }

    //fetch data by filter
    @Nullable
    public Cursor fetchDataByFilter(@Nullable String inputText, @NonNull String filterColumn) {
        if (sqlDb != null && sqlDb.isOpen()) {
            Cursor row;
            String query = "SELECT * FROM " + dbTable;
            if (inputText == null || inputText.length() == 0) {
                row = sqlDb.rawQuery(query, null);
            } else {
                query = "SELECT * FROM " + dbTable + " WHERE " + filterColumn + " like '%" + inputText + "%'";
                row = sqlDb.rawQuery(query, null);
            }
            if (row != null) {
                row.moveToFirst();
            }
            return row;
        } else {
            Log.e(TAG, "DB must be opened");
            return null;
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(@NonNull Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(@NonNull SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + dbTable + " (_id INTEGER PRIMARY KEY autoincrement, pass_title, pass_content, pass_icon, pass_attachment, pass_creation, UNIQUE(pass_content))");
        }

        @Override
        public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + dbTable);
            onCreate(db);
        }
    }
}