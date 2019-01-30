package website.petrov.browser.view;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import website.petrov.browser.unit.HelperUnit;

public class NinjaContextWrapper extends ContextWrapper {
    @NonNull
    private final Context context;

    public NinjaContextWrapper(@NonNull Context context) {
        super(context);
        this.context = context;
        HelperUnit.setTheme(context);
    }

    @Override
    @NonNull
    public Resources.Theme getTheme() {
        return context.getTheme();
    }
}
