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

package website.petrov.browser.view;

import androidx.annotation.Nullable;

public class GridItem {
    @Nullable
    private String title;
    @Nullable
    private String url;
    @Nullable
    private String filename;
    private int ordinal;

    public GridItem() {
        this.title = null;
        this.url = null;
        this.filename = null;
        this.ordinal = -1;
    }

    public GridItem(@Nullable String title, @Nullable String url, @Nullable String filename, int ordinal) {
        this.title = title;
        this.url = url;
        this.filename = filename;
        this.ordinal = ordinal;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nullable
    public String getURL() {
        return url;
    }

    public void setURL(@Nullable String url) {
        this.url = url;
    }

    @Nullable
    public String getFilename() {
        return filename;
    }

    public void setFilename(@Nullable String filename) {
        this.filename = filename;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }
}
