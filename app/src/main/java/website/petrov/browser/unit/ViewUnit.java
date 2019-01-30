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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class ViewUnit {

    public static void bound(@NonNull Context context, @NonNull View view) {
        int windowWidth = getWindowWidth(context);
        int windowHeight = getWindowHeight(context);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(windowHeight, View.MeasureSpec.EXACTLY);

        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    @NonNull
    public static Bitmap capture(@NonNull View view, @Dimension float width,
                                 @Dimension float height, @NonNull Bitmap.Config config) {
        if (!view.isDrawingCacheEnabled()) {
            view.setDrawingCacheEnabled(true);
        }

        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, config);
        bitmap.eraseColor(Color.WHITE);

        Canvas canvas = new Canvas(bitmap);
        int left = view.getLeft();
        int top = view.getTop();
        int status = canvas.save();
        canvas.translate(-left, -top);

        float scale = width / view.getWidth();
        canvas.scale(scale, scale, left, top);

        view.draw(canvas);
        canvas.restoreToCount(status);

        Paint alphaPaint = new Paint();
        alphaPaint.setColor(Color.TRANSPARENT);

        canvas.drawRect(0f, 0f, 1f, height, alphaPaint);
        canvas.drawRect(width - 1f, 0f, width, height, alphaPaint);
        canvas.drawRect(0f, 0f, width, 1f, alphaPaint);
        canvas.drawRect(0f, height - 1f, width, height, alphaPaint);
        canvas.setBitmap(null);

        return bitmap;
    }

    public static float getDensity(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    @Nullable
    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        return ContextCompat.getDrawable(context, id);
    }

    private static int getWindowHeight(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getWindowWidth(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}