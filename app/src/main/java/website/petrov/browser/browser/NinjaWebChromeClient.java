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
