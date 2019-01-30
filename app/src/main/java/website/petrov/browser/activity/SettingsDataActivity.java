package website.petrov.browser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import website.petrov.browser.R;
import website.petrov.browser.fragment.FragmentSettingsData;
import website.petrov.browser.unit.HelperUnit;

public class SettingsDataActivity extends AppCompatActivity {
    private static final String DB_CHANGE = "DB_CHANGE";
    private final boolean dbChange = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        HelperUnit.setTheme(this);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentSettingsData()).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent keyEvent) {
        Intent intent = new Intent();
        intent.putExtra(DB_CHANGE, dbChange);
        setResult(AppCompatActivity.RESULT_OK, intent);
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra(DB_CHANGE, dbChange);
            setResult(AppCompatActivity.RESULT_OK, intent);
            finish();
        }
        return true;
    }
}
