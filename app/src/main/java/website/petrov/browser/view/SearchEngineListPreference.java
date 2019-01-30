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

package website.petrov.browser.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import website.petrov.browser.R;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.unit.HelperUnit;
import website.petrov.browser.unit.LayoutUnit;

@SuppressWarnings("unused")
public class SearchEngineListPreference extends ListPreference {

    private static final String CUSTOM_SEARCH_ENGINE_ID = "5";

    public SearchEngineListPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SearchEngineListPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SearchEngineListPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchEngineListPreference(@NonNull Context context) {
        super(context);
    }

    private void showEditDialog() {
        final Context context = getContext();

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View dialogView = View.inflate(context, R.layout.dialog_edit_title, null);

        final TextInputEditText editText = dialogView.findViewById(R.id.dialog_edit);
        String custom = HelperUnit.safeGetString(sp, context.getString(R.string.sp_search_engine_custom), "");

        editText.setHint(R.string.dialog_title_hint);
        editText.setText(custom);

        MaterialButton actionOk = dialogView.findViewById(R.id.action_ok);
        actionOk.setOnClickListener(view -> {
            String domain = LayoutUnit.getText(editText);
            if (domain.isEmpty()) {
                NinjaToast.show(context, R.string.toast_input_empty);
            } else if (!BrowserUnit.isURL(domain)) {
                NinjaToast.show(context, R.string.toast_invalid_domain);
            } else {
                sp.edit().putString(context.getString(R.string.sp_search_engine), "8")
                        .putString(context.getString(R.string.sp_search_engine_custom), domain)
                        .apply();
                hideSoftInput(editText);
                bottomSheetDialog.cancel();
            }
        });
        MaterialButton actionCancel = dialogView.findViewById(R.id.action_cancel);
        actionCancel.setOnClickListener(view -> {
            hideSoftInput(editText);
            bottomSheetDialog.cancel();
        });
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void hideSoftInput(@NonNull View view) {
        view.clearFocus();
        InputMethodManager imm = ContextCompat.getSystemService(getContext(), InputMethodManager.class);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean callChangeListener(@NonNull Object newValue) {
        String selected = (String) newValue;
        if (selected.equals(CUSTOM_SEARCH_ENGINE_ID)) {
            showEditDialog();
        }
        return super.callChangeListener(newValue);
    }
}
