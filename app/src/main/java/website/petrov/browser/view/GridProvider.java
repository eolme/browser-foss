/*
 * Open source android application.
 *
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GridProvider {
    @NonNull
    private final List<GridItem> items;
    @Nullable
    private final OnItemClickListener clickItemListener;

    public GridProvider(@NonNull List<GridItem> items,
                        @Nullable OnItemClickListener clickItemListener) {
        this.items = items;
        this.clickItemListener = clickItemListener;
    }

    public List<GridItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void callOnClick(int position) {
        if (clickItemListener != null) {
            clickItemListener.onClick(getItem(position));
        }
    }

    public void callOnLongClick(int position) {
        if (clickItemListener != null) {
            clickItemListener.onLongClick(getItem(position));
        }
    }

    public int getCount() {
        return items.size();
    }

    @NonNull
    public GridItem getItem(int index) {
        return items.get(index);
    }

    public void removeItem(int position) {
        Cache.savedItem = items.get(position);
        Cache.savedPosition = position;

        items.remove(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        if (fromPosition > toPosition) {
            for (int i = fromPosition; i > toPosition; ) {
                swapItem(i, --i);
            }
        } else {
            for (int i = fromPosition; i < toPosition; ) {
                swapItem(i, ++i);
            }
        }
    }

    public void swapItem(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
    }

    public int undoLastRemoval() {
        items.add(Cache.savedPosition, Cache.savedItem);
        return Cache.savedPosition;
    }

    public interface OnItemClickListener {
        void onClick(@NonNull GridItem item);

        void onLongClick(@NonNull GridItem item);
    }

    private static class Cache {
        @Nullable
        static GridItem savedItem;
        static int savedPosition;
    }
}
