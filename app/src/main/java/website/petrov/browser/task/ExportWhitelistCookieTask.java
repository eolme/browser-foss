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

package website.petrov.browser.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.ref.WeakReference;

import website.petrov.browser.R;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.view.NinjaToast;

public class ExportWhitelistCookieTask extends AsyncTask<Void, Void, Boolean> {
    @NonNull
    private final WeakReference<Activity> referenceContext;
    @Nullable
    private BottomSheetDialog dialog;
    @Nullable
    private String path;

    public ExportWhitelistCookieTask(@NonNull Activity context) {
        this.referenceContext = new WeakReference<>(context);
        this.dialog = null;
        this.path = null;
    }

    @Override
    @NonNull
    protected Boolean doInBackground(Void... params) {
        Activity context = referenceContext.get();
        if (context == null || context.isFinishing()) {
            return false;
        }

        path = BrowserUnit.exportWhitelist(context, 2);
        return !isCancelled() && path != null && !path.isEmpty();
    }

    @Override
    protected void onPreExecute() {
        Activity context = referenceContext.get();
        if (context == null || context.isFinishing()) {
            return;
        }

        dialog = new BottomSheetDialog(context);
        View dialogView = View.inflate(context, R.layout.dialog_progress, null);
        AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
        textView.setText(context.getString(R.string.toast_wait_a_minute));
        dialog.setContentView(dialogView);
        Window window = dialog.getWindow();
        if (window != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        dialog.show();
    }

    @Override
    protected void onPostExecute(@NonNull Boolean result) {
        if (dialog != null && dialog.isShowing()) {
            dialog.hide();
            dialog.dismiss();
        }

        Activity context = referenceContext.get();
        if (context == null || context.isFinishing()) {
            return;
        }

        if (result) {
            NinjaToast.show(context, context.getString(R.string.toast_export_successful) + path);
        } else {
            NinjaToast.show(context, R.string.toast_export_failed);
        }
    }
}
