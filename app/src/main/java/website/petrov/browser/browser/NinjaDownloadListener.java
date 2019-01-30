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

import android.content.Context;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import website.petrov.browser.R;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.unit.IntentUnit;

public class NinjaDownloadListener implements DownloadListener {
    @NonNull
    private final Context context;

    public NinjaDownloadListener(@NonNull Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onDownloadStart(@NonNull String url, @NonNull String userAgent,
                                @NonNull String contentDisposition,
                                @NonNull String mimeType, long contentLength) {
        final Context holder = IntentUnit.getContext();
        if (!(holder instanceof AppCompatActivity)) {
            BrowserUnit.download(context, url, contentDisposition, mimeType);
            return;
        }

        String text = holder.getString(R.string.dialog_title_download) + " - " + URLUtil.guessFileName(url, contentDisposition, mimeType);
        final BottomSheetDialog dialog = new BottomSheetDialog(holder);
        View dialogView = View.inflate(holder, R.layout.dialog_action, null);
        AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
        textView.setText(text);
        MaterialButton actionOk = dialogView.findViewById(R.id.action_ok);
        actionOk.setOnClickListener(view -> {
            BrowserUnit.download(holder, url, contentDisposition, mimeType);
            dialog.cancel();
        });
        MaterialButton actionCancel = dialogView.findViewById(R.id.action_cancel);
        actionCancel.setOnClickListener(view -> dialog.cancel());
        dialog.setContentView(dialogView);
        dialog.show();
    }
}
