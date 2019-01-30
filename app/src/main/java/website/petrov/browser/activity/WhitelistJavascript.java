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

package website.petrov.browser.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import website.petrov.browser.R;
import website.petrov.browser.browser.Javascript;
import website.petrov.browser.database.RecordAction;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.unit.HelperUnit;
import website.petrov.browser.unit.LayoutUnit;
import website.petrov.browser.view.AdapterJavascript;
import website.petrov.browser.view.NinjaToast;

public class WhitelistJavascript extends AppCompatActivity {
    private AdapterJavascript adapter;
    private List<String> list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        HelperUnit.setTheme(this);
        setContentView(R.layout.whitelist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        list = action.listDomainsJS();
        action.close();

        ListView listView = findViewById(R.id.whitelist);
        listView.setEmptyView(findViewById(R.id.whitelist_empty));

        adapter = new AdapterJavascript(this, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        MaterialButton button = findViewById(R.id.whitelist_add);
        button.setOnClickListener(v -> {
            AppCompatEditText editText = findViewById(R.id.whitelist_edit);
            String domain = LayoutUnit.getText(editText);
            if (domain.isEmpty()) {
                NinjaToast.show(WhitelistJavascript.this, R.string.toast_input_empty);
            } else if (!BrowserUnit.isURL(domain)) {
                NinjaToast.show(WhitelistJavascript.this, R.string.toast_invalid_domain);
            } else {
                RecordAction action1 = new RecordAction(WhitelistJavascript.this);
                action1.open(true);
                if (action1.checkDomainJS(domain)) {
                    NinjaToast.show(WhitelistJavascript.this, R.string.toast_domain_already_exists);
                } else {
                    Javascript adBlock = new Javascript(WhitelistJavascript.this);
                    adBlock.addDomain(domain.trim());
                    list.add(0, domain.trim());
                    adapter.notifyDataSetChanged();
                    NinjaToast.show(WhitelistJavascript.this, R.string.toast_add_whitelist_successful);
                }
                action1.close();
            }
        });
    }

    @Override
    public void onPause() {
        hideSoftInput(findViewById(R.id.whitelist_edit));
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_whitelist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.whitelist_menu_clear:
                final BottomSheetDialog dialog = new BottomSheetDialog(WhitelistJavascript.this);
                View dialogView = View.inflate(WhitelistJavascript.this, R.layout.dialog_action, null);
                AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.toast_clear);
                MaterialButton actionOk = dialogView.findViewById(R.id.action_ok);
                actionOk.setOnClickListener(view -> {
                    Javascript javaScript = new Javascript(WhitelistJavascript.this);
                    javaScript.clearDomains();
                    list.clear();
                    adapter.notifyDataSetChanged();
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

    private void hideSoftInput(@NonNull View view) {
        view.clearFocus();
        InputMethodManager imm = ContextCompat.getSystemService(this, InputMethodManager.class);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
