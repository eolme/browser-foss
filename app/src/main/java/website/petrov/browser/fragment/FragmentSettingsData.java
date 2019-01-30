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

package website.petrov.browser.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import website.petrov.browser.R;
import website.petrov.browser.activity.WhitelistAdBlock;
import website.petrov.browser.activity.WhitelistJavascript;
import website.petrov.browser.database.BookmarkList;
import website.petrov.browser.database.RecordHelper;
import website.petrov.browser.task.ExportWhitelistAdBlockTask;
import website.petrov.browser.task.ExportWhitelistCookieTask;
import website.petrov.browser.task.ExportWhitelistJSTask;
import website.petrov.browser.task.ImportWhitelistAdBlockTask;
import website.petrov.browser.task.ImportWhitelistCookieTask;
import website.petrov.browser.task.ImportWhitelistJSTask;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.unit.IntentUnit;
import website.petrov.browser.view.NinjaToast;

public class FragmentSettingsData extends PreferenceFragmentCompat {
    private static final String TAG = "FragmentSettingsData";

    private BottomSheetDialog dialog;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preference_data);
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        FragmentActivity activity = requireActivity();
        View dialogView;
        AppCompatTextView textView;
        MaterialButton actionOk;
        MaterialButton actionCancel;

        File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File data = Environment.getDataDirectory();

        String previewsPath_app = "//data//" + activity.getPackageName() + "//files//";
        String previewsPath_backup = BrowserUnit.APP_NAME + "//previews//";
        final File previewsFolder_app = new File(data, previewsPath_app);
        final File previewsFolder_backup = new File(sd, previewsPath_backup);

        String databasePath_app = "//data//" + activity.getPackageName() + "//databases//" + RecordHelper.DATABASE_NAME;
        String databasePath_backup = BrowserUnit.APP_NAME + "//databases//browser_database.db";
        String bookmarksPath_app = "//data//" + activity.getPackageName() + "//databases//" + BookmarkList.DATABASE_NAME;
        String bookmarksPath_backup = BrowserUnit.APP_NAME + "//databases//browser_bookmarks.db";

        final File databaseFile_app = new File(data, databasePath_app);
        final File databaseFile_backup = new File(sd, databasePath_backup);
        final File bookmarkFile_app = new File(data, bookmarksPath_app);
        final File bookmarkFile_backup = new File(sd, bookmarksPath_backup);

        CharSequence i = preference.getTitle();
        if (getString(R.string.setting_title_whitelist).contentEquals(i)) {
            Intent toWhitelist = new Intent(activity, WhitelistAdBlock.class);
            activity.startActivity(toWhitelist);
        } else if (getString(R.string.setting_title_whitelistJS).contentEquals(i)) {
            Intent toJavascript = new Intent(activity, WhitelistJavascript.class);
            activity.startActivity(toJavascript);
        } else if (getString(R.string.setting_title_export_whitelist).contentEquals(i)) {
            dialog = new BottomSheetDialog(activity);
            dialogView = View.inflate(activity, R.layout.dialog_action, null);
            textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.toast_backup);
            actionOk = dialogView.findViewById(R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                dialog.cancel();
                makeBackupDir();
                new ExportWhitelistAdBlockTask(activity).execute();
            });
            actionCancel = dialogView.findViewById(R.id.action_cancel);
            actionCancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        } else if (getString(R.string.setting_title_import_whitelist).contentEquals(i)) {
            dialog = new BottomSheetDialog(activity);
            dialogView = View.inflate(activity, R.layout.dialog_action, null);
            textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.hint_database);
            actionOk = dialogView.findViewById(R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                dialog.cancel();
                new ImportWhitelistAdBlockTask(activity).execute();
            });
            actionCancel = dialogView.findViewById(R.id.action_cancel);
            actionCancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        } else if (getString(R.string.setting_title_export_whitelistJS).contentEquals(i)) {
            dialog = new BottomSheetDialog(activity);
            dialogView = View.inflate(activity, R.layout.dialog_action, null);
            textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.toast_backup);
            actionOk = dialogView.findViewById(R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                dialog.cancel();
                makeBackupDir();
                new ExportWhitelistJSTask(activity).execute();
            });
            actionCancel = dialogView.findViewById(R.id.action_cancel);
            actionCancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        } else if (getString(R.string.setting_title_import_whitelistJS).contentEquals(i)) {
            dialog = new BottomSheetDialog(activity);
            dialogView = View.inflate(activity, R.layout.dialog_action, null);
            textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.hint_database);
            actionOk = dialogView.findViewById(R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                dialog.cancel();
                new ImportWhitelistJSTask(activity).execute();
            });
            actionCancel = dialogView.findViewById(R.id.action_cancel);
            actionCancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        } else if (getString(R.string.setting_title_export_whitelistCookie).contentEquals(i)) {
            dialog = new BottomSheetDialog(activity);
            dialogView = View.inflate(activity, R.layout.dialog_action, null);
            textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.toast_backup);
            actionOk = dialogView.findViewById(R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                dialog.cancel();
                makeBackupDir();
                new ExportWhitelistCookieTask(activity).execute();
            });
            actionCancel = dialogView.findViewById(R.id.action_cancel);
            actionCancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        } else if (getString(R.string.setting_title_import_whitelistCookie).contentEquals(i)) {
            dialog = new BottomSheetDialog(activity);
            dialogView = View.inflate(activity, R.layout.dialog_action, null);
            textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.hint_database);
            actionOk = dialogView.findViewById(R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                dialog.cancel();
                new ImportWhitelistCookieTask(activity).execute();
            });
            actionCancel = dialogView.findViewById(R.id.action_cancel);
            actionCancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        } else if (getString(R.string.setting_title_export_database).contentEquals(i)) {
            dialog = new BottomSheetDialog(activity);
            dialogView = View.inflate(activity, R.layout.dialog_action, null);
            textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.toast_backup);
            actionOk = dialogView.findViewById(R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                dialog.cancel();
                makeBackupDir();
                try {
                    int HAS_WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (HAS_WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                        NinjaToast.show(activity, R.string.toast_permission_sdCard_sec);
                    } else {
                        BrowserUnit.deleteDir(previewsFolder_backup);
                        BrowserUnit.deleteDir(databaseFile_backup);
                        BrowserUnit.deleteDir(bookmarkFile_backup);
                        BrowserUnit.exportBookmarks(activity);
                        copyDirectory(previewsFolder_app, previewsFolder_backup);
                        copyDirectory(databaseFile_app, databaseFile_backup);
                        copyDirectory(bookmarkFile_app, bookmarkFile_backup);
                        NinjaToast.show(activity, getString(R.string.toast_export_successful) + BrowserUnit.APP_NAME);
                    }
                } catch (Throwable e) {
                    Log.e(TAG, "Export", e);
                }
            });
            actionCancel = dialogView.findViewById(R.id.action_cancel);
            actionCancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        } else if (getString(R.string.setting_title_import_database).contentEquals(i)) {
            dialog = new BottomSheetDialog(activity);
            dialogView = View.inflate(activity, R.layout.dialog_action, null);
            textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.hint_database);
            actionOk = dialogView.findViewById(R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                dialog.cancel();
                try {
                    int HAS_WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (HAS_WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                        NinjaToast.show(activity, R.string.toast_permission_sdCard_sec);
                    } else {
                        BrowserUnit.importBookmarks(activity);
                        copyDirectory(previewsFolder_backup, previewsFolder_app);
                        copyDirectory(databaseFile_backup, databaseFile_app);
                        copyDirectory(bookmarkFile_backup, bookmarkFile_app);
                        dialogRestart();
                    }
                } catch (Throwable e) {
                    Log.e(TAG, "Import", e);
                }
            });
            actionCancel = dialogView.findViewById(R.id.action_cancel);
            actionCancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        }
        return super.onPreferenceTreeClick(preference);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void makeBackupDir() {
        FragmentActivity activity = requireActivity();

        File backupDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BrowserUnit.APP_NAME + "//");
        File noMedia = new File(backupDir, "//.nomedia");

        int HAS_WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (HAS_WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            NinjaToast.show(activity, R.string.toast_permission_sdCard_sec);
        } else {
            if (!backupDir.exists()) {
                try {
                    backupDir.mkdirs();
                    noMedia.createNewFile();
                } catch (Throwable e) {
                    Log.e(TAG, "Make dir", e);
                }
            }
        }
    }

    private void dialogRestart() {
        FragmentActivity activity = requireActivity();
        final SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        final BottomSheetDialog dialog = new BottomSheetDialog(activity);
        View dialogView = View.inflate(activity, R.layout.dialog_action, null);
        AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
        textView.setText(R.string.toast_restart);
        MaterialButton actionOk = dialogView.findViewById(R.id.action_ok);
        actionOk.setOnClickListener(view -> {
            sp.edit().putInt("restart_changed", 1).apply();
            IntentUnit.restartApp(activity.getApplicationContext());
        });
        MaterialButton actionCancel = dialogView.findViewById(R.id.action_cancel);
        actionCancel.setOnClickListener(view -> dialog.cancel());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    // If targetLocation does not exist, it will be created.
    private void copyDirectory(@NonNull File sourceLocation, @NonNull File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }
            String[] children = sourceLocation.list();
            for (String aChildren : children) {
                copyDirectory(new File(sourceLocation, aChildren),
                        new File(targetLocation, aChildren));
            }
        } else {
            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            // Copy the bits from InputStream to OutputStream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
}
