/*
 * Open source android application.
 *
 * Copyright (c) 2015 Matthew Lee
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
