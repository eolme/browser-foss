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

package website.petrov.browser.browser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.util.LinkedList;
import java.util.List;

import website.petrov.browser.view.NinjaWebView;

public class BrowserContainer {
    private static final List<AlbumController> list = new LinkedList<>();

    @Contract(pure = true)
    @NonNull
    public static AlbumController get(int index) {
        return list.get(index);
    }

    public synchronized static void add(@Nullable AlbumController controller) {
        list.add(controller);
    }

    public synchronized static void add(@Nullable AlbumController controller, int index) {
        list.add(index, controller);
    }

    public synchronized static void remove(@Nullable AlbumController controller) {
        if (controller instanceof NinjaWebView) {
            ((NinjaWebView) controller).destroy();
        }
        list.remove(controller);
    }

    public static int indexOf(@Nullable AlbumController controller) {
        return list.indexOf(controller);
    }

    @Contract(pure = true)
    @NonNull
    public static List<AlbumController> list() {
        return list;
    }

    public static int size() {
        return list.size();
    }

    public synchronized static void clear() {
        for (AlbumController albumController : list) {
            if (albumController instanceof NinjaWebView) {
                ((NinjaWebView) albumController).destroy();
            }
        }
        list.clear();
    }
}
