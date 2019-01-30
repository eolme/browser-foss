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
