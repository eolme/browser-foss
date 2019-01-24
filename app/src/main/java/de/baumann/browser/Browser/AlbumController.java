package de.baumann.browser.Browser;

import android.graphics.Bitmap;
import android.view.View;

public interface AlbumController {

    View getAlbumView();

    void setAlbumCover(Bitmap bitmap);

    String getAlbumTitle();

    void setAlbumTitle(String title);

    void activate();

    void deactivate();
}
