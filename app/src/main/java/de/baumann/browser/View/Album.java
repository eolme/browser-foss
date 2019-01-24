package de.baumann.browser.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

import de.baumann.browser.Browser.AlbumController;
import de.baumann.browser.Browser.BrowserController;
import de.baumann.browser.Ninja.R;

class Album {
    @NonNull
    private final Context context;

    private View albumView;
    public View getAlbumView() {
        return albumView;
    }

    @NonNull
    private final AlbumController albumController;
    private AppCompatImageView albumCover;
    private AppCompatTextView albumTitle;
    public String getAlbumTitle() {
        return albumTitle.getText().toString();
    }

    @NonNull
    private BrowserController browserController;

    public Album(@NonNull Context context, @NonNull AlbumController albumController,
                 @NonNull BrowserController browserController) {
        this.context = context;
        this.albumController = albumController;
        this.browserController = browserController;
        initUI();
    }

    public void setAlbumCover(@Nullable Bitmap bitmap) {
        albumCover.setImageBitmap(bitmap);
    }

    public void setAlbumTitle(@NonNull String title) {
        albumTitle.setText(title);
    }

    public void setBrowserController(@NonNull BrowserController browserController) {
        this.browserController = browserController;
    }

    private void initUI() {
        albumView = LayoutInflater.from(context).inflate(R.layout.album, null, false);

        albumView.setOnClickListener(v -> {
            browserController.showAlbum(albumController);
            browserController.hideOverview();
        });

        AppCompatImageView albumClose = ViewCompat.requireViewById(albumView, R.id.album_close);
        albumCover = ViewCompat.requireViewById(albumView, R.id.album_cover);
        albumTitle = ViewCompat.requireViewById(albumView, R.id.album_title);
        albumTitle.setText(context.getString(R.string.album_untitled));

        albumClose.setOnClickListener(v -> browserController.removeAlbum(albumController));
    }

    public void activate() {
        albumView.setBackgroundResource(R.drawable.album_shape_accent);
    }

    public void deactivate() {
        albumView.setBackgroundResource(R.drawable.album_shape_transparent);
    }
}
