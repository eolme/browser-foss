package de.baumann.browser.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import de.baumann.browser.Ninja.R;

public class Fragment_clear extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preference_clear);
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
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {}

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        FragmentActivity activity = getActivity();
        if (activity != null && preference.getTitle() == getString(R.string.clear_title_deleteDatabase)) {
            final SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            final BottomSheetDialog dialog = new BottomSheetDialog(activity);
            View dialogView = View.inflate(getActivity(), R.layout.dialog_action, null);
            AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.hint_database);
            MaterialButton action_ok = dialogView.findViewById(R.id.action_ok);
            action_ok.setOnClickListener(view -> {
                dialog.cancel();
                getActivity().deleteDatabase("Ninja4.db");
                getActivity().deleteDatabase("pass_DB_v01.db");
                sp.edit().putInt("restart_changed", 1).apply();
                getActivity().finish();
            });
            MaterialButton action_cancel = dialogView.findViewById(R.id.action_cancel);
            action_cancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        }
        return super.onPreferenceTreeClick(preference);
    }
}
