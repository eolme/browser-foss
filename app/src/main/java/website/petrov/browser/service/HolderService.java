package website.petrov.browser.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import website.petrov.browser.R;
import website.petrov.browser.activity.BrowserActivity;
import website.petrov.browser.browser.AlbumController;
import website.petrov.browser.browser.BrowserContainer;
import website.petrov.browser.browser.BrowserController;
import website.petrov.browser.database.Record;
import website.petrov.browser.unit.IntentUnit;
import website.petrov.browser.unit.NotificationUnit;
import website.petrov.browser.unit.RecordUnit;
import website.petrov.browser.unit.ViewUnit;
import website.petrov.browser.view.NinjaContextWrapper;
import website.petrov.browser.view.NinjaWebView;

public class HolderService extends Service implements BrowserController {

    private static final String TAG = "HolderService";

    @Override
    public void updateAutoComplete() {
    }

    @Override
    public void updateBookmarks() {
    }

    @Override
    public void updateInputBox(String query) {
    }

    @Override
    public void updateProgress(int progress) {
    }

    @Override
    public void showAlbum(@NonNull AlbumController albumController) {
    }

    @Override
    public void removeAlbum(@NonNull AlbumController albumController) {
    }

    @Override
    public void showFileChooser(@NonNull ValueCallback<Uri[]> filePathCallback) {
    }

    @Override
    public void onShowCustomView(@NonNull View view, @NonNull WebChromeClient.CustomViewCallback callback) {
    }

    @Override
    public boolean onHideCustomView() {
        return true;
    }

    @Override
    public void onLongPress(String url) {
    }

    @Override
    public void hideOverview() {
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Record record = RecordUnit.getHolder();

        if (record != null) {
            if (sp.getBoolean("sp_background", true)) {
                NinjaWebView webView = new NinjaWebView(new NinjaContextWrapper(this));

                webView.setBrowserController(this);
                webView.setAlbumCover(null);
                webView.setAlbumTitle(getString(R.string.album_untitled));
                ViewUnit.bound(this, webView);

                webView.loadUrl(record.getURL());
                webView.deactivate();

                BrowserContainer.add(webView);
                updateNotification();
            } else {
                Intent toActivity = new Intent(HolderService.this, BrowserActivity.class);
                toActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                toActivity.setAction(Intent.ACTION_SEND);
                toActivity.putExtra(Intent.EXTRA_TEXT, record.getURL());
                startActivity(toActivity);
            }
        } else {
            Log.e(TAG, "Holder is not set yet");
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (IntentUnit.isClear()) {
            BrowserContainer.clear();
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    @Nullable
    public IBinder onBind(@NonNull Intent intent) {
        return null;
    }

    private void updateNotification() {
        Notification notification = NotificationUnit.getHBuilder(this).build();
        startForeground(NotificationUnit.HOLDER_ID, notification);
        Toast.makeText(this, R.string.toast_load_in_background, Toast.LENGTH_LONG).show();
    }
}
