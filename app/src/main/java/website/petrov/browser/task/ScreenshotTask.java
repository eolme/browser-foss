package website.petrov.browser.task;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.lang.ref.WeakReference;

import website.petrov.browser.R;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.unit.HelperUnit;
import website.petrov.browser.unit.ViewUnit;
import website.petrov.browser.view.NinjaToast;
import website.petrov.browser.view.NinjaWebView;

public class ScreenshotTask extends AsyncTask<Void, Boolean, Boolean> {
    private static final String TAG = "ScreenshotTask";

    private final WeakReference<AppCompatActivity> referenceContext;
    private final WeakReference<NinjaWebView> referenceWebView;
    private int windowWidth;
    private float contentHeight;
    private String title;
    private String path;
    private BottomSheetDialog dialog;

    public ScreenshotTask(@NonNull AppCompatActivity context, @NonNull NinjaWebView webView) {
        this.referenceContext = new WeakReference<>(context);
        this.referenceWebView = new WeakReference<>(webView);
        this.windowWidth = 0;
        this.contentHeight = 0f;
        this.title = null;
        this.path = null;
    }

    @Override
    @NonNull
    protected Boolean doInBackground(Void... params) {
        AppCompatActivity context = referenceContext.get();
        if (context == null || context.isFinishing()) {
            return false;
        }

        NinjaWebView webView = referenceWebView.get();
        if (webView == null) {
            return false;
        }

        int HAS_WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        publishProgress(HAS_WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED);

        try {
            Bitmap bitmap = ViewUnit.capture(webView, windowWidth, contentHeight, Bitmap.Config.ARGB_8888);
            path = BrowserUnit.screenshot(context, bitmap, title);
        } catch (Throwable e) {
            Log.e(TAG, "doInBackground", e);
            path = null;
        }

        return path != null && !path.isEmpty();
    }

    @Override
    protected void onPreExecute() {
        AppCompatActivity context = referenceContext.get();
        if (context == null || context.isFinishing()) {
            return;
        }

        NinjaWebView webView = referenceWebView.get();
        if (webView == null) {
            return;
        }

        NinjaToast.show(context, context.getString(R.string.toast_wait_a_minute));
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

        try {
            windowWidth = ViewUnit.getWindowWidth(context);
            contentHeight = webView.getContentHeight() * ViewUnit.getDensity(context);
            title = HelperUnit.fileName(webView.getUrl());
        } catch (Throwable e) {
            Log.e(TAG, "onPreExecute", e);
            NinjaToast.show(context, context.getString(R.string.toast_error));
        }
    }

    @Override
    protected void onPostExecute(@NonNull Boolean result) {
        AppCompatActivity context = referenceContext.get();
        if (context == null || context.isFinishing()) {
            return;
        }

        if (dialog.isShowing()) {
            dialog.cancel();
        }

        if (result) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            if (sp.getInt("screenshot", 0) == 1) {
                final File pathFile = new File(HelperUnit.safeGetString(sp, "screenshot_path", ""));

                if (pathFile.exists()) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("image/*");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, path);
                    Uri bmpUri = Uri.fromFile(pathFile);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.menu_share)));
                    sp.edit().putBoolean("delete_screenshot", true).apply();
                }
            } else {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                View dialogView = View.inflate(context, R.layout.dialog_action, null);
                AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.toast_downloadComplete);
                MaterialButton actionOk = dialogView.findViewById(R.id.action_ok);
                actionOk.setOnClickListener(view -> {
                    context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    bottomSheetDialog.cancel();
                });
                MaterialButton actionCancel = dialogView.findViewById(R.id.action_cancel);
                actionCancel.setOnClickListener(view -> bottomSheetDialog.cancel());
                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
            }
        } else {
            NinjaToast.show(context, context.getString(R.string.toast_error));
        }
    }

    @Override
    protected void onProgressUpdate(Boolean... needRequestPermission) {
        if (needRequestPermission[0]) {
            AppCompatActivity context = referenceContext.get();
            if (context == null || context.isFinishing()) {
                return;
            }

            NinjaToast.show(context, R.string.toast_permission_sdCard_sec);
            HelperUnit.grantPermissionsStorage(context);
        }
    }
}
