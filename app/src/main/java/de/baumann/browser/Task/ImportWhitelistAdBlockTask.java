package de.baumann.browser.Task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import de.baumann.browser.Ninja.R;
import de.baumann.browser.Unit.BrowserUnit;
import de.baumann.browser.View.NinjaToast;

@SuppressLint("StaticFieldLeak")
public class ImportWhitelistAdBlockTask extends AsyncTask<Void, Void, Boolean> {
    private final Context context;
    private BottomSheetDialog dialog;
    private int count;

    public ImportWhitelistAdBlockTask(@NonNull Activity activity) {
        this.context = activity;
        this.dialog = null;
        this.count = 0;
    }

    @Override
    protected void onPreExecute() {
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
    protected Boolean doInBackground(Void... params) {
        count = BrowserUnit.importWhitelist(context);
        return !isCancelled() && count >= 0;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.hide();
        dialog.dismiss();

        if (result) {
            NinjaToast.show(context, context.getString(R.string.toast_import_successful) + count);
        } else {
            NinjaToast.show(context, R.string.toast_import_failed);
        }
    }
}
