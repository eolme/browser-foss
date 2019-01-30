package website.petrov.browser.browser;

import android.net.Uri;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import website.petrov.browser.view.NinjaWebView;

public class NinjaWebChromeClient extends WebChromeClient {
    @NonNull
    private final NinjaWebView ninjaWebView;

    public NinjaWebChromeClient(@NonNull NinjaWebView ninjaWebView) {
        super();
        this.ninjaWebView = ninjaWebView;
    }

    @Override
    public void onProgressChanged(@NonNull WebView view, int progress) {
        super.onProgressChanged(view, progress);
        ninjaWebView.update(progress);
    }

    @Override
    public void onReceivedTitle(@NonNull WebView view, @NonNull String title) {
        super.onReceivedTitle(view, title);
        ninjaWebView.update(title, view.getUrl());
    }

    @Override
    public void onShowCustomView(@NonNull View view, @NonNull WebChromeClient.CustomViewCallback callback) {
        BrowserController controller = ninjaWebView.getBrowserController();
        if (controller != null) {
            controller.onShowCustomView(view, callback);
        }
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        BrowserController controller = ninjaWebView.getBrowserController();
        if (controller != null) {
            controller.onHideCustomView();
        }
        super.onHideCustomView();
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(@NonNull String origin,
                                                   @NonNull GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, false);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public boolean onShowFileChooser(@NonNull WebView webView,
                                     @NonNull ValueCallback<Uri[]> filePathCallback,
                                     @NonNull WebChromeClient.FileChooserParams fileChooserParams) {
        BrowserController controller = ninjaWebView.getBrowserController();
        if (controller != null) {
            controller.showFileChooser(filePathCallback);
        }
        return true;
    }
}
