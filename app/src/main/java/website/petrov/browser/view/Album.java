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

package website.petrov.browser.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

import website.petrov.browser.R;
import website.petrov.browser.browser.AlbumController;
import website.petrov.browser.browser.BrowserController;
import website.petrov.browser.unit.LayoutUnit;

class Album {
    @NonNull
    private final View albumView;
    @NonNull
    private final AppCompatImageView albumCover;
    @NonNull
    private final AppCompatTextView albumTitle;

    @SuppressLint("InflateParams")
    Album(@NonNull Context context, @NonNull AlbumController albumController,
          @Nullable BrowserController browserController) {

        albumView = LayoutInflater.from(context).inflate(R.layout.album, null, false);

        albumView.setOnClickListener(v -> {
            if (browserController != null) {
                browserController.showAlbum(albumController);
                browserController.hideOverview();
            }
        });

        AppCompatImageView albumClose = ViewCompat.requireViewById(albumView, R.id.album_close);
        albumCover = ViewCompat.requireViewById(albumView, R.id.album_cover);
        albumTitle = ViewCompat.requireViewById(albumView, R.id.album_title);
        albumTitle.setText(context.getString(R.string.album_untitled));

        if (browserController != null) {
            albumClose.setOnClickListener(v -> browserController.removeAlbum(albumController));
        }
    }

    @NonNull
    View getAlbumView() {
        return albumView;
    }

    @NonNull
    String getAlbumTitle() {
        return LayoutUnit.getText(albumTitle);
    }

    void setAlbumTitle(@Nullable String title) {
        albumTitle.setText(title);
    }

    void setAlbumCover(@Nullable Bitmap bitmap) {
        albumCover.setImageBitmap(bitmap);
    }

    void activate() {
        albumView.setBackgroundResource(R.drawable.album_shape_accent);
    }

    void deactivate() {
        albumView.setBackgroundResource(R.drawable.album_shape_transparent);
    }
}
