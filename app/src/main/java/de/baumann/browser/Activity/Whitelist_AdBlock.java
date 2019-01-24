package de.baumann.browser.Activity;

import android.content.Context;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import de.baumann.browser.Browser.AdBlock;
import de.baumann.browser.Database.RecordAction;
import de.baumann.browser.Ninja.R;
import de.baumann.browser.Unit.BrowserUnit;
import de.baumann.browser.Unit.HelperUnit;
import de.baumann.browser.Unit.LayoutUnit;
import de.baumann.browser.View.Adapter_AbBlock;
import de.baumann.browser.View.NinjaToast;

public class Whitelist_AdBlock extends AppCompatActivity {
    private Adapter_AbBlock adapter;
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
        list = action.listDomains();
        action.close();

        ListView listView = findViewById(R.id.whitelist);
        listView.setEmptyView(findViewById(R.id.whitelist_empty));

        adapter = new Adapter_AbBlock(this, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        MaterialButton button = findViewById(R.id.whitelist_add);
        button.setOnClickListener(v -> {
            AppCompatEditText editText = findViewById(R.id.whitelist_edit);
            String domain = LayoutUnit.getText(editText);
            if (domain.isEmpty()) {
                NinjaToast.show(Whitelist_AdBlock.this, R.string.toast_input_empty);
            } else if (!BrowserUnit.isURL(domain)) {
                NinjaToast.show(Whitelist_AdBlock.this, R.string.toast_invalid_domain);
            } else {
                RecordAction action1 = new RecordAction(Whitelist_AdBlock.this);
                action1.open(true);
                if (action1.checkDomain(domain)) {
                    NinjaToast.show(Whitelist_AdBlock.this, R.string.toast_domain_already_exists);
                } else {
                    AdBlock adBlock = new AdBlock(Whitelist_AdBlock.this);
                    adBlock.addDomain(domain.trim());
                    list.add(0, domain.trim());
                    adapter.notifyDataSetChanged();
                    NinjaToast.show(Whitelist_AdBlock.this, R.string.toast_add_whitelist_successful);
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
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_whitelist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.whitelist_menu_clear:
                final BottomSheetDialog dialog = new BottomSheetDialog(Whitelist_AdBlock.this);
                View dialogView = View.inflate(Whitelist_AdBlock.this, R.layout.dialog_action, null);
                AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.toast_clear);
                MaterialButton action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(view -> {
                    AdBlock adBlock = new AdBlock(Whitelist_AdBlock.this);
                    adBlock.clearDomains();
                    list.clear();
                    adapter.notifyDataSetChanged();
                    dialog.cancel();
                });
                MaterialButton action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(view -> dialog.cancel());
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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
