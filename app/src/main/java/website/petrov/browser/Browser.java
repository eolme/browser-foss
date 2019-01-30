package website.petrov.browser;

import android.app.Application;

import website.petrov.browser.unit.DebugUnit;

public class Browser extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        DebugUnit.initialize(this);
    }
}
