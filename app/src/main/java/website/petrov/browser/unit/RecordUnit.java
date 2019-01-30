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

package website.petrov.browser.unit;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import website.petrov.browser.database.Record;

public class RecordUnit {
    public static final String TABLE_BOOKMARKS = "BOOKMARKS";
    public static final String TABLE_HISTORY = "HISTORY";
    public static final String TABLE_WHITELIST = "WHITELIST";
    public static final String TABLE_JAVASCRIPT = "JAVASCRIPT";
    public static final String TABLE_COOKIE = "COOKIE";
    public static final String TABLE_GRID = "GRID";

    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_URL = "URL";
    public static final String COLUMN_TIME = "TIME";
    public static final String COLUMN_DOMAIN = "DOMAIN";
    public static final String COLUMN_FILENAME = "FILENAME";
    public static final String COLUMN_ORDINAL = "ORDINAL";

    public static final String CREATE_HISTORY = "CREATE TABLE "
            + TABLE_HISTORY
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")";

    public static final String CREATE_BOOKMARKS = "CREATE TABLE "
            + TABLE_BOOKMARKS
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")";

    public static final String CREATE_WHITELIST = "CREATE TABLE "
            + TABLE_WHITELIST
            + " ("
            + " " + COLUMN_DOMAIN + " text"
            + ")";

    public static final String CREATE_JAVASCRIPT = "CREATE TABLE "
            + TABLE_JAVASCRIPT
            + " ("
            + " " + COLUMN_DOMAIN + " text"
            + ")";

    public static final String CREATE_COOKIE = "CREATE TABLE "
            + TABLE_COOKIE
            + " ("
            + " " + COLUMN_DOMAIN + " text"
            + ")";

    public static final String CREATE_GRID = "CREATE TABLE "
            + TABLE_GRID
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_FILENAME + " text,"
            + " " + COLUMN_ORDINAL + " integer"
            + ")";

    @Nullable
    private static Record holder;

    @Contract(pure = true)
    @Nullable
    public static Record getHolder() {
        return holder;
    }

    public synchronized static void setHolder(@Nullable Record record) {
        holder = record;
    }
}
