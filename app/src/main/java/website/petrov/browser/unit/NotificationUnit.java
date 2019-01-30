/*
 * Open source android application.
 *
 * Copyright (c) 2015 Matthew Lee
 * Copyright (c) 2017 Gaukler Faun
 * Copyright (c) 2019 Petrov Anton
 *
 * This file is part of Suze Browser.
 *
 * Suze Browser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Suze Browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Suze Browser. If not, see <https://www.gnu.org/licenses/>.
 */

package website.petrov.browser.unit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import website.petrov.browser.R;
import website.petrov.browser.activity.BrowserActivity;
import website.petrov.browser.browser.BrowserContainer;
import website.petrov.browser.service.HolderService;

public class NotificationUnit {

    public static final int HOLDER_ID = 0x65536;
    private static final int NOTIFICATION_REQUEST = 22578; // NOTIFICATION = 695848315896 % 65535 / 2

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

        String CHANNEL_ID = "browser_not"; // The id of the channel.
        CharSequence name = context.getString(R.string.app_name); // The user-visible name of the channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManagerCompat.from(context).createNotificationChannel(mChannel);
        }

        builder = new Builder(context, CHANNEL_ID)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(R.drawable.ic_notification_ninja)
                .setContentTitle(context.getString(R.string.notification_content_holderTitle))
                .setContentText(context.getString(R.string.notification_content_holder))
                .setColor(ContextCompat.getColor(context, R.color.secondaryColor))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0])
                .addAction(action_UN);

        Intent toActivity = new Intent(context, BrowserActivity.class);
        toActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pin = PendingIntent.getActivity(context, NOTIFICATION_REQUEST, toActivity, 0);
        builder.setContentIntent(pin);

        return builder;
    }
}
