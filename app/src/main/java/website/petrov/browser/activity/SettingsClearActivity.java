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

package website.petrov.browser.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import website.petrov.browser.R;
import website.petrov.browser.fragment.FragmentClear;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.unit.HelperUnit;
import website.petrov.browser.view.NinjaToast;

public class SettingsClearActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        HelperUnit.setTheme(this);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentClear()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clear, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.clear_menu_done_all:
                final BottomSheetDialog dialog = new BottomSheetDialog(SettingsClearActivity.this);
                View dialogView = View.inflate(SettingsClearActivity.this, R.layout.dialog_action, null);
                AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.toast_clear);
                MaterialButton actionOk = dialogView.findViewById(R.id.action_ok);
                actionOk.setOnClickListener(view -> {
                    clear();
                    dialog.cancel();
                });
                MaterialButton actionCancel = dialogView.findViewById(R.id.action_cancel);
                actionCancel.setOnClickListener(view -> dialog.cancel());
                dialog.setContentView(dialogView);
                dialog.show();
                break;
            default:
                break;
        }
        return true;
    }

    private void clear() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean clearCache = sp.getBoolean(getString(R.string.sp_clear_cache), false);
        boolean clearCookie = sp.getBoolean(getString(R.string.sp_clear_cookie), false);
        boolean clearHistory = sp.getBoolean(getString(R.string.sp_clear_history), false);
        boolean clearIndexedDB = sp.getBoolean(("sp_clearIndexedDB"), false);

        BottomSheetDialog dialog = new BottomSheetDialog(this);

        View dialogView = View.inflate(this, R.layout.dialog_progress, null);
        AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
        textView.setText(this.getString(R.string.toast_wait_a_minute));
        dialog.setContentView(dialogView);
        dialog.show();

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

        dialog.hide();
        dialog.dismiss();
        NinjaToast.show(this, R.string.toast_delete_successful);
    }
}
