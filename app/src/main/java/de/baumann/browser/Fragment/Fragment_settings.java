package de.baumann.browser.Fragment;

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

import de.baumann.browser.Activity.Settings_ClearActivity;
import de.baumann.browser.Activity.Settings_DataActivity;
import de.baumann.browser.Activity.Settings_GestureActivity;
import de.baumann.browser.Activity.Settings_StartActivity;
import de.baumann.browser.Activity.Settings_UIActivity;
import de.baumann.browser.Ninja.R;
import de.baumann.browser.Unit.HelperUnit;

public class Fragment_settings extends PreferenceFragmentCompat {
    
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preference_setting);
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            CharSequence i = preference.getTitle();
            if (getString(R.string.setting_title_data).contentEquals(i)) {
                Intent dataControl = new Intent(activity, Settings_DataActivity.class);
                activity.startActivity(dataControl);
            } else if (getString(R.string.setting_title_ui).contentEquals(i)) {
                Intent uiControl = new Intent(activity, Settings_UIActivity.class);
                activity.startActivity(uiControl);
            } else if (getString(R.string.setting_gestures).contentEquals(i)) {
                Intent gestureControl = new Intent(activity, Settings_GestureActivity.class);
                activity.startActivity(gestureControl);
            } else if (getString(R.string.setting_title_start_control).contentEquals(i)) {
                Intent startControl = new Intent(activity, Settings_StartActivity.class);
                activity.startActivity(startControl);
            } else if (getString(R.string.setting_title_clear_control).contentEquals(i)) {
                Intent clearControl = new Intent(activity, Settings_ClearActivity.class);
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
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void showLicenseDialog(String title, String text) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        View dialogView = View.inflate(activity, R.layout.dialog_text, null);

        TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        dialog_title.setText(title);

        TextView dialog_text = dialogView.findViewById(R.id.dialog_text);
        dialog_text.setText(HelperUnit.textSpannable(text));
        dialog_text.setMovementMethod(LinkMovementMethod.getInstance());

        ImageButton fab = dialogView.findViewById(R.id.floatButton_ok);
        fab.setOnClickListener(v -> dialog.cancel());

        ImageButton fab_help = dialogView.findViewById(R.id.floatButton_help);
        fab_help.setVisibility(View.GONE);

        ImageButton fab_settings = dialogView.findViewById(R.id.floatButton_settings);
        fab_settings.setVisibility(View.GONE);

        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void showChangelogDialog() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }

        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        View dialogView = View.inflate(activity, R.layout.dialog_text, null);

        TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
        dialog_title.setText(R.string.changelog_title);

        TextView dialog_text = dialogView.findViewById(R.id.dialog_text);
        dialog_text.setText(HelperUnit.textSpannable(getString(R.string.changelog_dialog)));
        dialog_text.setMovementMethod(LinkMovementMethod.getInstance());

        ImageButton fab = dialogView.findViewById(R.id.floatButton_ok);
        fab.setOnClickListener(v -> dialog.cancel());

        ImageButton fab_help = dialogView.findViewById(R.id.floatButton_help);
        fab_help.setVisibility(View.GONE);

        ImageButton fab_settings = dialogView.findViewById(R.id.floatButton_settings);
        fab_settings.setVisibility(View.GONE);

        dialog.setContentView(dialogView);
        dialog.show();
    }
}
