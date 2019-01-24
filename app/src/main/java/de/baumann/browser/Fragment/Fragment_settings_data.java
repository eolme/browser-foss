package de.baumann.browser.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
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

import de.baumann.browser.Activity.Whitelist_AdBlock;
import de.baumann.browser.Activity.Whitelist_Javascript;
import de.baumann.browser.Ninja.R;
import de.baumann.browser.Task.ExportWhitelistAdBlockTask;
import de.baumann.browser.Task.ExportWhitelistCookieTask;
import de.baumann.browser.Task.ExportWhitelistJSTask;
import de.baumann.browser.Task.ImportWhitelistAdBlockTask;
import de.baumann.browser.Task.ImportWhitelistCookieTask;
import de.baumann.browser.Task.ImportWhitelistJSTask;
import de.baumann.browser.Unit.BrowserUnit;
import de.baumann.browser.View.NinjaToast;

@SuppressWarnings("ALL")
public class Fragment_settings_data extends PreferenceFragmentCompat {

    private BottomSheetDialog dialog;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.preference_data);
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            View dialogView;
            AppCompatTextView textView;
            MaterialButton action_ok;
            MaterialButton action_cancel;

            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File data = Environment.getDataDirectory();

            String previewsPath_app = "//data//" + activity.getPackageName() + "//files";
            String previewsPath_backup = "browser_backup//previews";
            final File previewsFolder_app = new File(data, previewsPath_app);
            final File previewsFolder_backup = new File(sd, previewsPath_backup);

            String databasePath_app = "//data//" + activity.getPackageName() + "//databases//Ninja4.db";
            String databasePath_backup = "browser_backup//databases//browser_database.db";
            String bookmarksPath_app = "//data//" + activity.getPackageName() + "//databases//pass_DB_v01.db";
            String bookmarksPath_backup = "browser_backup//databases//browser_bookmarks.db";

            final File databaseFile_app = new File(data, databasePath_app);
            final File databaseFile_backup = new File(sd, databasePath_backup);
            final File bookmarkFile_app = new File(data, bookmarksPath_app);
            final File bookmarkFile_backup = new File(sd, bookmarksPath_backup);

            CharSequence i = preference.getTitle();
            if (getString(R.string.setting_title_whitelist).contentEquals(i)) {
                Intent toWhitelist = new Intent(activity, Whitelist_AdBlock.class);
                activity.startActivity(toWhitelist);
            } else if (getString(R.string.setting_title_whitelistJS).contentEquals(i)) {
                Intent toJavascript = new Intent(activity, Whitelist_Javascript.class);
                activity.startActivity(toJavascript);
            } else if (getString(R.string.setting_title_export_whitelist).contentEquals(i)) {
                dialog = new BottomSheetDialog(activity);
                dialogView = View.inflate(activity, R.layout.dialog_action, null);
                textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.toast_backup);
                action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(view -> {
                    dialog.cancel();
                    makeBackupDir();
                    new ExportWhitelistAdBlockTask(activity).execute();
                });
                action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(view -> dialog.cancel());
                dialog.setContentView(dialogView);
                dialog.show();
            } else if (getString(R.string.setting_title_import_whitelist).contentEquals(i)) {
                dialog = new BottomSheetDialog(activity);
                dialogView = View.inflate(activity, R.layout.dialog_action, null);
                textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.hint_database);
                action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(view -> {
                    dialog.cancel();
                    new ImportWhitelistAdBlockTask(activity).execute();
                });
                action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(view -> dialog.cancel());
                dialog.setContentView(dialogView);
                dialog.show();
            } else if (getString(R.string.setting_title_export_whitelistJS).contentEquals(i)) {
                dialog = new BottomSheetDialog(activity);
                dialogView = View.inflate(activity, R.layout.dialog_action, null);
                textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.toast_backup);
                action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(view -> {
                    dialog.cancel();
                    makeBackupDir();
                    new ExportWhitelistJSTask(activity).execute();
                });
                action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(view -> dialog.cancel());
                dialog.setContentView(dialogView);
                dialog.show();
            } else if (getString(R.string.setting_title_import_whitelistJS).contentEquals(i)) {
                dialog = new BottomSheetDialog(activity);
                dialogView = View.inflate(activity, R.layout.dialog_action, null);
                textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.hint_database);
                action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(view -> {
                    dialog.cancel();
                    new ImportWhitelistJSTask(activity).execute();
                });
                action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(view -> dialog.cancel());
                dialog.setContentView(dialogView);
                dialog.show();
            } else if (getString(R.string.setting_title_export_whitelistCookie).contentEquals(i)) {
                dialog = new BottomSheetDialog(activity);
                dialogView = View.inflate(activity, R.layout.dialog_action, null);
                textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.toast_backup);
                action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(view -> {
                    dialog.cancel();
                    makeBackupDir();
                    new ExportWhitelistCookieTask(activity).execute();
                });
                action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(view -> dialog.cancel());
                dialog.setContentView(dialogView);
                dialog.show();
            } else if (getString(R.string.setting_title_import_whitelistCookie).contentEquals(i)) {
                dialog = new BottomSheetDialog(activity);
                dialogView = View.inflate(activity, R.layout.dialog_action, null);
                textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.hint_database);
                action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(view -> {
                    dialog.cancel();
                    new ImportWhitelistCookieTask(activity).execute();
                });
                action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(view -> dialog.cancel());
                dialog.setContentView(dialogView);
                dialog.show();
            } else if (getString(R.string.setting_title_export_database).contentEquals(i)) {
                dialog = new BottomSheetDialog(activity);
                dialogView = View.inflate(activity, R.layout.dialog_action, null);
                textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.toast_backup);
                action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(view -> {
                    dialog.cancel();
                    makeBackupDir();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                                NinjaToast.show(activity, R.string.toast_permission_sdCard_sec);
                            } else {
                                BrowserUnit.deleteDir(previewsFolder_backup);
                                BrowserUnit.deleteDir(databaseFile_backup);
                                BrowserUnit.deleteDir(bookmarkFile_backup);
                                BrowserUnit.exportBookmarks(activity);
                                copyDirectory(previewsFolder_app, previewsFolder_backup);
                                copyDirectory(databaseFile_app, databaseFile_backup);
                                copyDirectory(bookmarkFile_app, bookmarkFile_backup);
                                NinjaToast.show(activity, getString(R.string.toast_export_successful) + "browser_backup");
                            }
                        } else {
                            BrowserUnit.deleteDir(previewsFolder_backup);
                            BrowserUnit.deleteDir(databaseFile_backup);
                            BrowserUnit.deleteDir(bookmarkFile_backup);
                            BrowserUnit.exportBookmarks(activity);
                            copyDirectory(previewsFolder_app, previewsFolder_backup);
                            copyDirectory(databaseFile_app, databaseFile_backup);
                            copyDirectory(bookmarkFile_app, bookmarkFile_backup);
                            NinjaToast.show(activity, getString(R.string.toast_export_successful) + "browser_backup");
                        }
                    } catch (Throwable e) {
                        Log.w("Settings", e);
                    }
                });
                action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(view -> dialog.cancel());
                dialog.setContentView(dialogView);
                dialog.show();
            } else if (getString(R.string.setting_title_import_database).contentEquals(i)) {
                dialog = new BottomSheetDialog(activity);
                dialogView = View.inflate(activity, R.layout.dialog_action, null);
                textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.hint_database);
                action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(view -> {
                    dialog.cancel();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                                NinjaToast.show(activity, R.string.toast_permission_sdCard_sec);
                            } else {
                                BrowserUnit.importBookmarks(activity);
                                copyDirectory(previewsFolder_backup, previewsFolder_app);
                                copyDirectory(databaseFile_backup, databaseFile_app);
                                copyDirectory(bookmarkFile_backup, bookmarkFile_app);
                                dialogRestart();
                            }
                        } else {
                            BrowserUnit.importBookmarks(activity);
                            copyDirectory(previewsFolder_backup, previewsFolder_app);
                            copyDirectory(databaseFile_backup, databaseFile_app);
                            copyDirectory(bookmarkFile_backup, bookmarkFile_app);
                            dialogRestart();
                        }

                    } catch (Throwable e) {
                        Log.w("Settings", e);
                    }
                });
                action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setOnClickListener(view -> dialog.cancel());
                dialog.setContentView(dialogView);
                dialog.show();
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void makeBackupDir() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }

        File backupDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "browser_backup//");
        File noMedia = new File(backupDir, "//.nomedia");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                NinjaToast.show(activity, R.string.toast_permission_sdCard_sec);
            } else {
                if(!backupDir.exists()) {
                    try {
                        backupDir.mkdirs();
                        noMedia.createNewFile();
                    } catch (Throwable e) {
                        Log.w("Settings", e);
                    }
                }
            }
        } else {
            if(!backupDir.exists()) {
                try {
                    backupDir.mkdirs();
                    noMedia.createNewFile();
                } catch (Throwable e) {
                    Log.w("Settings", e);
                }
            }
        }
    }

    private void dialogRestart () {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            final SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            final BottomSheetDialog dialog = new BottomSheetDialog(activity);
            View dialogView = View.inflate(activity, R.layout.dialog_action, null);
            AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.toast_restart);
            MaterialButton action_ok = dialogView.findViewById(R.id.action_ok);
            action_ok.setOnClickListener(view -> {
                sp.edit().putInt("restart_changed", 1).apply();
                activity.finish();
            });
            MaterialButton action_cancel = dialogView.findViewById(R.id.action_cancel);
            action_cancel.setOnClickListener(view -> dialog.cancel());
            dialog.setContentView(dialogView);
            dialog.show();
        }
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
