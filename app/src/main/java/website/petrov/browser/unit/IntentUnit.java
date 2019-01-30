package website.petrov.browser.unit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.AlarmManagerCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.Contract;

import java.lang.ref.WeakReference;

import website.petrov.browser.R;

public class IntentUnit {
    private static final String TAG = "IntentUnit";
    private static final String INTENT_TYPE_MESSAGE_RFC822 = "message/rfc822";
    private static final int RESTART_INTENT = 32685; // RESTART = 4325145 % 65535 / 2
    // Activity holder
    private static WeakReference<Context> context;
    private static boolean clear = false;

    public static Intent getEmailIntent(@NonNull MailTo mailTo) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailTo.getTo()});
        intent.putExtra(Intent.EXTRA_TEXT, mailTo.getBody());
        intent.putExtra(Intent.EXTRA_SUBJECT, mailTo.getSubject());
        intent.putExtra(Intent.EXTRA_CC, mailTo.getCc());
        intent.setType(INTENT_TYPE_MESSAGE_RFC822);

        return intent;
    }

    public static void share(@NonNull Context context, @NonNull String title, @NonNull String url) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.menu_share_link)));
    }

    @Contract(pure = true)
    @Nullable
    public synchronized static Context getContext() {
        return context != null ? context.get() : null;
    }

    public synchronized static void setContext(@Nullable Context holder) {
        if (context != null) {
            context.clear();
        }
        context = new WeakReference<>(holder);
    }

    @Contract(pure = true)
    public static boolean isClear() {
        return clear;
    }

    public synchronized static void setClear(boolean b) {
        clear = b;
    }

    public static void restartApp(@Nullable Context c) {
        if (c == null) {
            Log.e(TAG, "Was not able to restart application, Context is null");
            return;
        }

        Intent restartIntent = new Intent();
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        restartIntent.setPackage(c.getPackageName());

        AlarmManager mgr = ContextCompat.getSystemService(c, AlarmManager.class);
        if (mgr == null) {
            c.startActivity(restartIntent);
            if (c instanceof Activity) {
                ((Activity) c).finish();
            }
        } else {
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(c, RESTART_INTENT, restartIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManagerCompat.setExactAndAllowWhileIdle(mgr, AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
            Runtime.getRuntime().exit(0);
        }
    }
}