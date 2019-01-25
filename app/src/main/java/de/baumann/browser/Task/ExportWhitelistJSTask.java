package de.baumann.browser.Task;

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

import de.baumann.browser.Ninja.R;
import de.baumann.browser.Unit.BrowserUnit;
import de.baumann.browser.View.NinjaToast;

public class ExportWhitelistJSTask extends AsyncTask<Void, Void, Boolean> {
    @NonNull
    private final WeakReference<Activity> referenceContext;
    @Nullable
    private BottomSheetDialog dialog;
    @Nullable
    private String path;

    public ExportWhitelistJSTask(@NonNull Activity context) {
        this.referenceContext = new WeakReference<>(context);
        this.dialog = null;
        this.path = null;
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
    @NonNull
    protected Boolean doInBackground(Void... params) {
        Activity context = referenceContext.get();
        if (context == null || context.isFinishing()) {
            return false;
        }

        path = BrowserUnit.exportWhitelist(context, 1);
        return !isCancelled() && path != null && !path.isEmpty();
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
