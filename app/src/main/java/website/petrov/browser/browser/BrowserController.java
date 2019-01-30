package website.petrov.browser.browser;

import android.net.Uri;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface BrowserController {
    void updateAutoComplete();

    void updateBookmarks();

    void updateInputBox(@Nullable String query);

    void updateProgress(int progress);

    void showAlbum(@NonNull AlbumController albumController);

    void removeAlbum(@NonNull AlbumController albumController);

    void showFileChooser(@NonNull ValueCallback<Uri[]> filePathCallback);

    void onShowCustomView(@NonNull View view, @NonNull WebChromeClient.CustomViewCallback callback);

    boolean onHideCustomView();

    void onLongPress(@Nullable String url);

    void hideOverview();
}
