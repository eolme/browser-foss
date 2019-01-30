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
