package website.petrov.browser.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import website.petrov.browser.R;
import website.petrov.browser.database.Record;
import website.petrov.browser.service.HolderService;
import website.petrov.browser.unit.RecordUnit;

public class HolderActivity extends AppCompatActivity {
    private static final int TIMER_SCHEDULE_DEFAULT = 512;

    @Nullable
    private Record first;
    @Nullable
    private Timer timer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || getIntent().getData() == null) {
            finish();
            return;
        }

        first = new Record();
        first.setTitle(getString(R.string.album_untitled));
        first.setURL(getIntent().getData().toString());
        first.setTime(System.currentTimeMillis());

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (first != null) {
                    Intent toService = new Intent(HolderActivity.this, HolderService.class);
                    toService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    RecordUnit.setHolder(first);
                    startService(toService);
                }
                HolderActivity.this.finish();
            }
        };
        timer = new Timer();
        timer.schedule(task, TIMER_SCHEDULE_DEFAULT);
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.purge();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
        }
    }
}
