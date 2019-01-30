package website.petrov.browser.browser;

import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface AlbumController {

    @NonNull
    View getAlbumView();

    void setAlbumCover(@Nullable Bitmap bitmap);

    @NonNull
    String getAlbumTitle();

    void activate();

    void deactivate();
}
