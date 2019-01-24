package de.baumann.browser.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import de.baumann.browser.Ninja.R;
import de.baumann.browser.Unit.BrowserUnit;
import de.baumann.browser.Unit.LayoutUnit;

@SuppressWarnings("unused")
public class SearchEngineListPreference extends ListPreference {

    public SearchEngineListPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SearchEngineListPreference(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SearchEngineListPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchEngineListPreference(@NonNull Context context) {
        super(context);
    }

    private void showEditDialog() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View dialogView = View.inflate(getContext(), R.layout.dialog_edit_title, null);

        final EditText editText = dialogView.findViewById(R.id.dialog_edit);
        String custom = sp.getString(getContext().getString(R.string.sp_search_engine_custom), "");

        editText.setHint(R.string.dialog_title_hint);
        editText.setText(custom);

        MaterialButton action_ok = dialogView.findViewById(R.id.action_ok);
        action_ok.setOnClickListener(view -> {
            String domain = LayoutUnit.getText(editText);
            if (domain.isEmpty()) {
                NinjaToast.show(getContext(), R.string.toast_input_empty);
            } else if (!BrowserUnit.isURL(domain)) {
                NinjaToast.show(getContext(), R.string.toast_invalid_domain);
            } else {
                sp.edit().putString(getContext().getString(R.string.sp_search_engine), "8")
                        .putString(getContext().getString(R.string.sp_search_engine_custom), domain)
                        .apply();
                hideSoftInput(editText);
                bottomSheetDialog.cancel();
            }
        });
        MaterialButton action_cancel = dialogView.findViewById(R.id.action_cancel);
        action_cancel.setOnClickListener(view -> {
            hideSoftInput(editText);
            bottomSheetDialog.cancel();
        });
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void hideSoftInput(@NonNull View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean callChangeListener(@NonNull Object newValue) {
        String selected = (String) newValue;
        if (selected.equals("8")) {
            showEditDialog();
        }
        return super.callChangeListener(newValue);
    }
}
