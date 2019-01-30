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
