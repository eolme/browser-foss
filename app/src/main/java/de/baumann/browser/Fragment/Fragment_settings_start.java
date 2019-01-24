package de.baumann.browser.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import de.baumann.browser.Activity.Whitelist_AdBlock;
import de.baumann.browser.Activity.Whitelist_Cookie;
import de.baumann.browser.Activity.Whitelist_Javascript;
import de.baumann.browser.Ninja.R;
import de.baumann.browser.Task.ExportWhitelistAdBlockTask;
import de.baumann.browser.Task.ExportWhitelistCookieTask;
import de.baumann.browser.Task.ExportWhitelistJSTask;
import de.baumann.browser.Task.ImportWhitelistAdBlockTask;
import de.baumann.browser.Task.ImportWhitelistCookieTask;
import de.baumann.browser.Task.ImportWhitelistJSTask;

public class Fragment_settings_start extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private boolean spChange = false;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preference_start);
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
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            CharSequence i = preference.getTitle();
            if (getString(R.string.setting_title_whitelist).contentEquals(i)) {
                Intent toWhitelist = new Intent(getActivity(), Whitelist_AdBlock.class);
                getActivity().startActivity(toWhitelist);
            } else if (getString(R.string.setting_title_whitelistJS).contentEquals(i)) {
                Intent toJavascript = new Intent(getActivity(), Whitelist_Javascript.class);
                getActivity().startActivity(toJavascript);
            } else if (getString(R.string.setting_title_whitelistCookie).contentEquals(i)) {
                Intent toCookie = new Intent(getActivity(), Whitelist_Cookie.class);
                getActivity().startActivity(toCookie);
            } else if (getString(R.string.setting_title_export_whitelist).contentEquals(i)) {
                new ExportWhitelistAdBlockTask(getActivity()).execute();
            } else if (getString(R.string.setting_title_import_whitelist).contentEquals(i)) {
                new ImportWhitelistAdBlockTask(getActivity()).execute();
            } else if (getString(R.string.setting_title_export_whitelistJS).contentEquals(i)) {
                new ExportWhitelistJSTask(getActivity()).execute();
            } else if (getString(R.string.setting_title_import_whitelistJS).contentEquals(i)) {
                new ImportWhitelistJSTask(getActivity()).execute();
            } else if (getString(R.string.setting_title_export_whitelistCookie).contentEquals(i)) {
                new ExportWhitelistCookieTask(getActivity()).execute();
            } else if (getString(R.string.setting_title_import_whitelistCookie).contentEquals(i)) {
                new ImportWhitelistCookieTask(getActivity()).execute();
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        spChange = true;
    }
}
