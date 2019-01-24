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
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;

import java.util.Objects;

import de.baumann.browser.Ninja.R;
import de.baumann.browser.Unit.BrowserUnit;
import de.baumann.browser.Unit.LayoutUnit;

public class SearchEngineListPreference extends ListPreference {

    public SearchEngineListPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public SearchEngineListPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SearchEngineListPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchEngineListPreference(@NonNull Context context) {
        this(context, null);
    }

    private void init() {
        setPositiveButtonText(R.string.dialog_button_custom);
    }

    private void showEditDialog() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = View.inflate(getContext(), R.layout.dialog_edit_title, null);

        final EditText editText = dialogView.findViewById(R.id.dialog_edit);

        editText.setHint(R.string.dialog_se_hint);
        String custom = sp.getString(getContext().getString(R.string.sp_search_engine_custom), "");
        editText.setText(custom);
        editText.setSelection(Objects.requireNonNull(custom).length());

        builder.setView(dialogView);
        builder.setTitle(R.string.menu_edit);
        builder.setPositiveButton(R.string.app_ok, (dialog, whichButton) -> {
            String domain = LayoutUnit.getText(editText);
            if (domain.isEmpty()) {
                NinjaToast.show(getContext(), R.string.toast_input_empty);
            } else if (!BrowserUnit.isURL(domain)) {
                NinjaToast.show(getContext(), R.string.toast_invalid_domain);
            } else {
                sp.edit().putString(getContext().getString(R.string.sp_search_engine), "8")
                        .putString(getContext().getString(R.string.sp_search_engine_custom), domain).apply();

                hideSoftInput(editText);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.app_cancel, (dialog, whichButton) -> {
            dialog.cancel();
            hideSoftInput(editText);
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        showSoftInput(editText);
    }

    private void hideSoftInput(@NonNull View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftInput(@NonNull View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
