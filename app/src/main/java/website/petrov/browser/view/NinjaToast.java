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
