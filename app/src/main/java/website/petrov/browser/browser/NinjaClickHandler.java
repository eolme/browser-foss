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

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.os.MessageCompat;

import website.petrov.browser.view.NinjaWebView;

public class NinjaClickHandler extends Handler {
    @NonNull
    private final NinjaWebView webView;

    public NinjaClickHandler(@NonNull NinjaWebView webView) {
        super();
        this.webView = webView;
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        MessageCompat.setAsynchronous(message, true);
        super.handleMessage(message);

        BrowserController controller = webView.getBrowserController();
        if (controller != null) {
            controller.onLongPress(message.getData().getString("url"));
        }
    }
}
