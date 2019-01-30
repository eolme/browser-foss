/*
 * Open source android application.
 *
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

package website.petrov.browser.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import website.petrov.browser.R;
import website.petrov.browser.activity.WhitelistAdBlock;
import website.petrov.browser.activity.WhitelistCookie;
import website.petrov.browser.activity.WhitelistJavascript;
import website.petrov.browser.task.ExportWhitelistAdBlockTask;
import website.petrov.browser.task.ExportWhitelistCookieTask;
import website.petrov.browser.task.ExportWhitelistJSTask;
import website.petrov.browser.task.ImportWhitelistAdBlockTask;
import website.petrov.browser.task.ImportWhitelistCookieTask;
import website.petrov.browser.task.ImportWhitelistJSTask;

public class FragmentSettingsStart extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preference_start);
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        FragmentActivity activity = requireActivity();
        CharSequence i = preference.getTitle();
        if (getString(R.string.setting_title_whitelist).contentEquals(i)) {
            Intent toWhitelist = new Intent(activity, WhitelistAdBlock.class);
            activity.startActivity(toWhitelist);
        } else if (getString(R.string.setting_title_whitelistJS).contentEquals(i)) {
            Intent toJavascript = new Intent(activity, WhitelistJavascript.class);
            activity.startActivity(toJavascript);
        } else if (getString(R.string.setting_title_whitelistCookie).contentEquals(i)) {
            Intent toCookie = new Intent(activity, WhitelistCookie.class);
            activity.startActivity(toCookie);
        } else if (getString(R.string.setting_title_export_whitelist).contentEquals(i)) {
            new ExportWhitelistAdBlockTask(activity).execute();
        } else if (getString(R.string.setting_title_import_whitelist).contentEquals(i)) {
            new ImportWhitelistAdBlockTask(activity).execute();
        } else if (getString(R.string.setting_title_export_whitelistJS).contentEquals(i)) {
            new ExportWhitelistJSTask(activity).execute();
        } else if (getString(R.string.setting_title_import_whitelistJS).contentEquals(i)) {
            new ImportWhitelistJSTask(activity).execute();
        } else if (getString(R.string.setting_title_export_whitelistCookie).contentEquals(i)) {
            new ExportWhitelistCookieTask(activity).execute();
        } else if (getString(R.string.setting_title_import_whitelistCookie).contentEquals(i)) {
            new ImportWhitelistCookieTask(activity).execute();
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(@NonNull SharedPreferences sharedPreferences, @NonNull String key) {

    }
}
