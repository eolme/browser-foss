package website.petrov.browser.fragment;

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

import website.petrov.browser.R;
import website.petrov.browser.database.BookmarkList;
import website.petrov.browser.database.RecordHelper;

public class FragmentClear extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preference_clear);
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        FragmentActivity activity = requireActivity();
        if (preference.getTitle() == getString(R.string.clear_title_deleteDatabase)) {
            final SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            final BottomSheetDialog dialog = new BottomSheetDialog(activity);
            View dialogView = View.inflate(activity, R.layout.dialog_action, null);
            AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.hint_database);
            MaterialButton actionOk = dialogView.findViewById(R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                dialog.cancel();
                activity.deleteDatabase(RecordHelper.DATABASE_NAME);
                activity.deleteDatabase(BookmarkList.DATABASE_NAME);
                sp.edit().putInt("restart_changed", 1).apply();
                activity.finish();
            });
            MaterialButton actionCancel = dialogView.findViewById(R.id.action_cancel);
            actionCancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
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
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
    }
}
