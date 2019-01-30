package website.petrov.browser.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import website.petrov.browser.R;
import website.petrov.browser.unit.BrowserUnit;

public class ClearService extends Service {
    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        clear();
        stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        System.exit(0); // For remove all WebView thread
    }

    @Override
    @Nullable
    public IBinder onBind(@NonNull Intent intent) {
        return null;
    }

    private void clear() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean clearCache = sp.getBoolean(getString(R.string.sp_clear_cache), false);
        boolean clearCookie = sp.getBoolean(getString(R.string.sp_clear_cookie), false);
        boolean clearHistory = sp.getBoolean(getString(R.string.sp_clear_history), false);
        boolean clearIndexedDB = sp.getBoolean(("sp_clearIndexedDB"), false);

        if (clearCache) {
            BrowserUnit.clearCache(this);
        }
        if (clearCookie) {
            BrowserUnit.clearCookie();
        }
        if (clearHistory) {
            BrowserUnit.clearHistory(this);
        }
        if (clearIndexedDB) {
            BrowserUnit.clearIndexedDB(this);
        }
    }
}
