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
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import website.petrov.browser.R;
import website.petrov.browser.activity.SettingsClearActivity;
import website.petrov.browser.activity.SettingsDataActivity;
import website.petrov.browser.activity.SettingsGestureActivity;
import website.petrov.browser.activity.SettingsStartActivity;
import website.petrov.browser.activity.SettingsUIActivity;
import website.petrov.browser.unit.HelperUnit;

public class FragmentSettings extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preference_setting);
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        FragmentActivity activity = requireActivity();
        CharSequence i = preference.getTitle();
        if (getString(R.string.setting_title_data).contentEquals(i)) {
            Intent dataControl = new Intent(activity, SettingsDataActivity.class);
            activity.startActivity(dataControl);
        } else if (getString(R.string.setting_title_ui).contentEquals(i)) {
            Intent uiControl = new Intent(activity, SettingsUIActivity.class);
            activity.startActivity(uiControl);
        } else if (getString(R.string.setting_gestures).contentEquals(i)) {
            Intent gestureControl = new Intent(activity, SettingsGestureActivity.class);
            activity.startActivity(gestureControl);
        } else if (getString(R.string.setting_title_start_control).contentEquals(i)) {
            Intent startControl = new Intent(activity, SettingsStartActivity.class);
            activity.startActivity(startControl);
        } else if (getString(R.string.setting_title_clear_control).contentEquals(i)) {
            Intent clearControl = new Intent(activity, SettingsClearActivity.class);
            activity.startActivity(clearControl);
        } else if (getString(R.string.setting_title_license).contentEquals(i)) {
            showLicenseDialog(getString(R.string.license_title), getString(R.string.license_dialog));
        } else if (getString(R.string.setting_title_community).contentEquals(i)) {
            showLicenseDialog(getString(R.string.setting_title_community), getString(R.string.cont_dialog));
        } else if (getString(R.string.changelog_title).contentEquals(i)) {
            showChangelogDialog();
        } else if (getString(R.string.setting_title_appSettings).contentEquals(i)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void showLicenseDialog(@NonNull String title, @NonNull String text) {
        FragmentActivity activity = requireActivity();

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        View dialogView = View.inflate(activity, R.layout.dialog_text, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText(title);

        TextView dialogText = dialogView.findViewById(R.id.dialog_text);
        dialogText.setText(HelperUnit.textSpannable(text));
        dialogText.setMovementMethod(LinkMovementMethod.getInstance());

        ImageButton fab = dialogView.findViewById(R.id.floatButton_ok);
        fab.setOnClickListener(v -> dialog.cancel());

        ImageButton fabHelp = dialogView.findViewById(R.id.floatButton_help);
        fabHelp.setVisibility(View.GONE);

        ImageButton fabSettings = dialogView.findViewById(R.id.floatButton_settings);
        fabSettings.setVisibility(View.GONE);

        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void showChangelogDialog() {
        FragmentActivity activity = requireActivity();

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        View dialogView = View.inflate(activity, R.layout.dialog_text, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText(R.string.changelog_title);

        TextView dialogText = dialogView.findViewById(R.id.dialog_text);
        dialogText.setText(HelperUnit.textSpannable(getString(R.string.changelog_dialog)));
        dialogText.setMovementMethod(LinkMovementMethod.getInstance());

        ImageButton fab = dialogView.findViewById(R.id.floatButton_ok);
        fab.setOnClickListener(v -> dialog.cancel());

        ImageButton fabHelp = dialogView.findViewById(R.id.floatButton_help);
        fabHelp.setVisibility(View.GONE);

        ImageButton fabSettings = dialogView.findViewById(R.id.floatButton_settings);
        fabSettings.setVisibility(View.GONE);

        dialog.setContentView(dialogView);
        dialog.show();
    }
}
