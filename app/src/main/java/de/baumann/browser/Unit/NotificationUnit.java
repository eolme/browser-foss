package de.baumann.browser.Unit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.content.ContextCompat;

import de.baumann.browser.Activity.BrowserActivity;
import de.baumann.browser.Browser.BrowserContainer;
import de.baumann.browser.Ninja.R;
import de.baumann.browser.Service.HolderService;

public class NotificationUnit {

    public static final int HOLDER_ID = 0x65536;

    @NonNull
    public static Builder getHBuilder(@NonNull Context context) {
        Builder builder;

        final BroadcastReceiver stopNotificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(@NonNull Context context, @NonNull Intent intent) {
                Intent toHolderService = new Intent(context, HolderService.class);
                toHolderService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                IntentUnit.setClear(false);
                context.stopService(toHolderService);
                BrowserContainer.clear();
            }
        };

        IntentFilter intentFilter = new IntentFilter("stopNotification");
        context.registerReceiver(stopNotificationReceiver, intentFilter);
        Intent stopNotification = new Intent("stopNotification");
        PendingIntent stopNotificationPI = PendingIntent.getBroadcast(context, 0, stopNotification, PendingIntent.FLAG_CANCEL_CURRENT);

        Action action_UN = new Action.Builder(R.drawable.icon_earth, context.getString(R.string.toast_closeNotification), stopNotificationPI).build();

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "browser_not";// The id of the channel.
        CharSequence name = context.getString(R.string.app_name);// The user-visible name of the channel.
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        builder = new Builder(context, CHANNEL_ID);

        builder.setCategory(Notification.CATEGORY_MESSAGE);
        builder.setSmallIcon(R.drawable.ic_notification_ninja);
        builder.setContentTitle(context.getString(R.string.notification_content_holderTitle));
        builder.setContentText(context.getString(R.string.notification_content_holder));
        builder.setColor(ContextCompat.getColor(context,R.color.colorAccent));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setVibrate(new long[0]);
        builder.addAction(action_UN);

        Intent toActivity = new Intent(context, BrowserActivity.class);
        toActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pin = PendingIntent.getActivity(context, 0, toActivity, 0);
        builder.setContentIntent(pin);

        return builder;
    }
}
