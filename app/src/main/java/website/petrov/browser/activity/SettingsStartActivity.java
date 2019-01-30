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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import website.petrov.browser.R;
import website.petrov.browser.fragment.FragmentSettingsStart;
import website.petrov.browser.unit.HelperUnit;

public class SettingsStartActivity extends AppCompatActivity {
    private static final String DB_CHANGE = "DB_CHANGE";
    private final boolean dbChange = false;

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

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentSettingsStart()).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent keyEvent) {
        Intent intent = new Intent();
        intent.putExtra(DB_CHANGE, dbChange);
        setResult(Activity.RESULT_OK, intent);
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra(DB_CHANGE, dbChange);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        return true;
    }
}
