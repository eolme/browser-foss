package website.petrov.browser.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatTextView;

import website.petrov.browser.R;

@UiThread
public class NinjaToast {

    public static void show(@NonNull Context context, int stringResId) {
        show(context, context.getString(stringResId));
    }

    public static void show(@NonNull Context context, @NonNull String text) {
        Activity activity = (Activity) context;

        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_bottom, activity.findViewById(R.id.dialog_toast));

        AppCompatTextView dialogText = layout.findViewById(R.id.dialog_text);
        dialogText.setText(text);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
