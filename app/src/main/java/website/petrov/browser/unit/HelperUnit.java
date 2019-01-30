/*
 * Open source android application.
 *
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

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.util.Linkify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.text.HtmlCompat;

import org.jetbrains.annotations.Contract;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import website.petrov.browser.BuildConfig;
import website.petrov.browser.R;

public class HelperUnit {
    private static final int REQUEST_STORAGE = 31405; // STORAGE = 259415 % 65535 / 2
    private static final int REQUEST_LOCATION = 4963; // LOCATION = 9315896 % 65535 / 2

    private static final String DEFAULT_FILE_NAME = BuildConfig.APPLICATION_ID;
    private static final int[] DRAWABLE_EMPTY_STATE = new int[0];

    public static void grantPermissionsStorage(@NonNull AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int HAS_WRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (HAS_WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(activity, R.style.AppTheme_AlertDialog)
                            .setTitle(R.string.toast_permission_title)
                            .setMessage(R.string.toast_permission_sdCard)
                            .setPositiveButton(activity.getString(R.string.app_ok), (dialog, which) ->
                                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            REQUEST_STORAGE))
                            .setNegativeButton(activity.getString(R.string.app_cancel), (dialog, which) -> dialog.cancel())
                            .show();
                }
            }
        }
    }

    public static void grantPermissionsLoc(@NonNull AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int HAS_ACCESS_FINE_LOCATION = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (HAS_ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(activity, R.style.AppTheme_AlertDialog)
                            .setTitle(R.string.toast_permission_title)
                            .setMessage(R.string.toast_permission_loc)
                            .setPositiveButton(activity.getString(R.string.app_ok), (dialog, which) ->
                                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQUEST_LOCATION))
                            .setNegativeButton(activity.getString(R.string.app_cancel), (dialog, which) -> dialog.cancel())
                            .show();
                }
            }
        }
    }

    public static void setTheme(@NonNull Context activity) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);

//        if (sp.getBoolean("sp_darkUI", false)){
        activity.setTheme(R.style.AppTheme);
//        } else {
//            activity.setTheme(R.style.AppTheme_dark);
//        }
    }

    @NonNull
    public static SpannableString textSpannable(@NonNull String text) {
        SpannableString s = new SpannableString(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY));
        Linkify.addLinks(s, Linkify.WEB_URLS);
        return s;
    }

    @NonNull
    public static String fileName(@Nullable String url) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        String currentTime = sdf.format(new Date()).trim();

        if (url == null) {
            return DEFAULT_FILE_NAME + "_" + currentTime;
        }

        String host = BrowserUnit.safeGetHost(url);
        if (host.isEmpty()) {
            String altName;
            try {
                altName = URLEncoder.encode(url, Charset.defaultCharset().displayName());
            } catch (UnsupportedEncodingException e) {
                altName = DEFAULT_FILE_NAME;
            }
            return altName + "_" + currentTime;
        } else {
            return host.replace(".", "_")
                    .trim() + "_" + currentTime;
        }
    }

    public static void switchIcon(@NonNull AppCompatActivity activity, @NonNull String string,
                                  @NonNull String fieldDB, @NonNull AppCompatImageView be) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        switch (string) {
            case "01":
                be.setImageResource(R.drawable.circle_red);
                sharedPref.edit().putString(fieldDB, "01").apply();
                break;
            case "02":
                be.setImageResource(R.drawable.circle_pink);
                sharedPref.edit().putString(fieldDB, "02").apply();
                break;
            case "03":
                be.setImageResource(R.drawable.circle_purple);
                sharedPref.edit().putString(fieldDB, "03").apply();
                break;
            case "04":
                be.setImageResource(R.drawable.circle_blue);
                sharedPref.edit().putString(fieldDB, "04").apply();
                break;
            case "05":
                be.setImageResource(R.drawable.circle_teal);
                sharedPref.edit().putString(fieldDB, "05").apply();
                break;
            case "06":
                be.setImageResource(R.drawable.circle_green);
                sharedPref.edit().putString(fieldDB, "06").apply();
                break;
            case "07":
                be.setImageResource(R.drawable.circle_lime);
                sharedPref.edit().putString(fieldDB, "07").apply();
                break;
            case "08":
                be.setImageResource(R.drawable.circle_yellow);
                sharedPref.edit().putString(fieldDB, "08").apply();
                break;
            case "09":
                be.setImageResource(R.drawable.circle_orange);
                sharedPref.edit().putString(fieldDB, "09").apply();
                break;
            case "10":
                be.setImageResource(R.drawable.circle_brown);
                sharedPref.edit().putString(fieldDB, "10").apply();
                break;
            case "11":
                be.setImageResource(R.drawable.circle_grey);
                sharedPref.edit().putString(fieldDB, "11").apply();
                break;
            default:
                be.setImageResource(R.drawable.circle_red);
                sharedPref.edit().putString(fieldDB, "01").apply();
                break;
        }
    }

    @NonNull
    public static String secString(@Nullable String string) {
        return string == null ? "" : string.replaceAll("'", "\'\'");
    }

    @Contract("_, null, _ -> param3")
    @NonNull
    public static String safeGetString(@NonNull SharedPreferences sp, @Nullable String name, @NonNull String def) {
        if (name == null) {
            return def;
        }

        final String get = sp.getString(name, def);
        return get == null ? def : get;
    }

    public static void clearState(@Nullable Drawable drawable) {
        if (drawable != null) {
            drawable.setState(DRAWABLE_EMPTY_STATE);
        }
    }
}