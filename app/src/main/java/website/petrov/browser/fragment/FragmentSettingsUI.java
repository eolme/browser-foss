package website.petrov.browser.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import website.petrov.browser.R;

public class FragmentSettingsUI extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preference_ui);
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
    public void onSharedPreferenceChanged(@NonNull SharedPreferences sp, @NonNull String key) {
        FragmentActivity activity = requireActivity();
        if (key.equals("sp_exit") || key.equals("sp_toggle") || key.equals("sp_add") ||
                key.equals("sp_darkUI") || key.equals("nav_position") ||
                key.equals("sp_hideOmni") || key.equals("start_tab") || key.equals("sp_hideSB")) {

            final BottomSheetDialog dialog = new BottomSheetDialog(activity);
            View dialogView = View.inflate(activity, R.layout.dialog_action, null);
            AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.toast_restart);
            MaterialButton actionOk = dialogView.findViewById(R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                sp.edit().putInt("restart_changed", 1).apply();
                activity.finish();
            });
            MaterialButton actionCancel = dialogView.findViewById(R.id.action_cancel);
            actionCancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        }
    }
}
