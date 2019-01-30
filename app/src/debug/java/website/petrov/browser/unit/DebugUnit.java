package website.petrov.browser.unit;

import android.app.Application;

import androidx.annotation.NonNull;

import com.squareup.leakcanary.LeakCanary;

public final class DebugUnit {
    public static void initialize(@NonNull Application app) {
        if (LeakCanary.isInAnalyzerProcess(app)) {
            return;
        }
        LeakCanary.install(app);
    }
}