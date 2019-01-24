package de.baumann.browser.Unit;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.baumann.browser.Ninja.R;

public class HelperUnit {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_1 = 1234;

    public static void grantPermissionsStorage(@NonNull AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.toast_permission_title)
                            .setMessage(R.string.toast_permission_sdCard)
                            .setPositiveButton(activity.getString(R.string.app_ok), (dialog, which) ->
                                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            REQUEST_CODE_ASK_PERMISSIONS))
                            .setNegativeButton(activity.getString(R.string.app_cancel), (dialog, which) -> dialog.cancel())
                            .show();
                }
            }
        }
    }

    public static void grantPermissionsLoc(@NonNull AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasACCESS_FINE_LOCATION = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.toast_permission_title)
                            .setMessage(R.string.toast_permission_loc)
                            .setPositiveButton(activity.getString(R.string.app_ok), (dialog, which) ->
                                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQUEST_CODE_ASK_PERMISSIONS_1))
                            .setNegativeButton(activity.getString(R.string.app_cancel), (dialog, which) -> dialog.cancel())
                            .show();
                }
            }
        }
    }

    public static void setTheme(@NonNull Context activity) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);

        if (sp.getBoolean("sp_darkUI", false)){
            activity.setTheme(R.style.AppTheme);
        } else {
            activity.setTheme(R.style.AppTheme_dark);
        }
    }

    @NonNull
    public static SpannableString textSpannable(@NonNull String text) {
        SpannableString s = new SpannableString(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY));
        Linkify.addLinks(s, Linkify.WEB_URLS);
        return s;
    }

    @NonNull
    public static String fileName(@NonNull String url) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        String domain = Objects.requireNonNull(Uri.parse(url).getHost()).replace("www.", "").trim();

        return domain.replace(".", "_").trim() + "_" + currentTime.trim();
    }

    public static void switchIcon(@NonNull AppCompatActivity activity, @NonNull String string,
                                  @NonNull String fieldDB, @NonNull AppCompatImageView be) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        switch (string) {
            case "01":be.setImageResource(R.drawable.circle_red);
                sharedPref.edit().putString(fieldDB, "01").apply();break;
            case "02":be.setImageResource(R.drawable.circle_pink);
                sharedPref.edit().putString(fieldDB, "02").apply();break;
            case "03":be.setImageResource(R.drawable.circle_purple);
                sharedPref.edit().putString(fieldDB, "03").apply();break;
            case "04":be.setImageResource(R.drawable.circle_blue);
                sharedPref.edit().putString(fieldDB, "04").apply();break;
            case "05":be.setImageResource(R.drawable.circle_teal);
                sharedPref.edit().putString(fieldDB, "05").apply();break;
            case "06":be.setImageResource(R.drawable.circle_green);
                sharedPref.edit().putString(fieldDB, "06").apply();break;
            case "07":be.setImageResource(R.drawable.circle_lime);
                sharedPref.edit().putString(fieldDB, "07").apply();break;
            case "08":be.setImageResource(R.drawable.circle_yellow);
                sharedPref.edit().putString(fieldDB, "08").apply();break;
            case "09":be.setImageResource(R.drawable.circle_orange);
                sharedPref.edit().putString(fieldDB, "09").apply();break;
            case "10":be.setImageResource(R.drawable.circle_brown);
                sharedPref.edit().putString(fieldDB, "10").apply();break;
            case "11":
                be.setImageResource(R.drawable.circle_grey);
                sharedPref.edit().putString(fieldDB, "11").apply();break;
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
}