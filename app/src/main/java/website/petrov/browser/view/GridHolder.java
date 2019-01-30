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

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

import com.google.android.material.card.MaterialCardView;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import website.petrov.browser.R;

@SuppressWarnings("WeakerAccess")
public class GridHolder extends AbstractDraggableItemViewHolder {
    public final AppCompatTextView title;
    public final AppCompatImageView cover;
    public final MaterialCardView container;

    public GridHolder(@NonNull View v) {
        super(v);
        container = ViewCompat.requireViewById(v, R.id.grid_item_container);
        title = ViewCompat.requireViewById(container, R.id.grid_item_title);
        cover = ViewCompat.requireViewById(container, R.id.grid_item_cover);
    }
}