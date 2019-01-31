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

package website.petrov.browser.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.h6ah4i.android.widget.advrecyclerview.adapter.SimpleWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.mobapphome.simpleencryptorlib.SimpleEncryptor;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import website.petrov.browser.BuildConfig;
import website.petrov.browser.R;
import website.petrov.browser.browser.AdBlock;
import website.petrov.browser.browser.AlbumController;
import website.petrov.browser.browser.BrowserContainer;
import website.petrov.browser.browser.BrowserController;
import website.petrov.browser.browser.Cookie;
import website.petrov.browser.browser.Javascript;
import website.petrov.browser.database.BookmarkList;
import website.petrov.browser.database.Record;
import website.petrov.browser.database.RecordAction;
import website.petrov.browser.service.ClearService;
import website.petrov.browser.service.HolderService;
import website.petrov.browser.task.ScreenshotTask;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.unit.DatabaseUnit;
import website.petrov.browser.unit.HelperUnit;
import website.petrov.browser.unit.IntentUnit;
import website.petrov.browser.unit.LayoutUnit;
import website.petrov.browser.unit.ViewUnit;
import website.petrov.browser.view.AdapterRecord;
import website.petrov.browser.view.CompleteAdapter;
import website.petrov.browser.view.FullscreenHolder;
import website.petrov.browser.view.GridAdapter;
import website.petrov.browser.view.GridItem;
import website.petrov.browser.view.GridProvider;
import website.petrov.browser.view.NinjaToast;
import website.petrov.browser.view.NinjaWebView;
import website.petrov.browser.view.SwipeTouchListener;

@SuppressWarnings({"IfCanBeSwitch", "ResultOfMethodCallIgnored"})
public class BrowserActivity extends AppCompatActivity implements BrowserController, View.OnClickListener {

    private static final String TAG = "BrowserActivity";
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final float[] NEGATIVE_COLOR = {
            -1.0f, 0, 0, 0, 255, // Red
            0, -1.0f, 0, 0, 255, // Green
            0, 0, -1.0f, 0, 255, // Blue
            0, 0, 0, 1.0f, 0     // Alpha
    };
    @NonNull
    private final BroadcastReceiver downloadReceiver;
    @NonNull
    private final BroadcastReceiver pwCopy;
    @NonNull
    private final BroadcastReceiver unCopy;
    private final int shortAnimTime = 200; // config_shortAnimTime
    private final int mediumAnimTime = 400; // config_mediumAnimTime
    @Nullable
    private ClipboardManager clipboard;
    private String decryptedUserPW;
    private String decryptedUserName;
    // Menus
    private RelativeLayout menuTabPreview;
    private LinearLayoutCompat menuNewTabOpen;
    private LinearLayoutCompat menuCloseTab;
    private LinearLayoutCompat menuQuit;
    private LinearLayoutCompat menuShareScreenshot;
    private LinearLayoutCompat menuShareLink;
    private LinearLayoutCompat menuSharePDF;
    private LinearLayoutCompat menuOpenWith;
    private LinearLayoutCompat menuSearchSite;
    private LinearLayoutCompat menuSettings;
    private LinearLayoutCompat menuDownload;
    private LinearLayoutCompat menuSaveScreenshot;
    private LinearLayoutCompat menuSaveBookmark;
    private LinearLayoutCompat menuSavePDF;
    private LinearLayoutCompat menuSaveStart;
    private LinearLayoutCompat menuHelp;
    private LinearLayoutCompat contextListNewTab;
    private LinearLayoutCompat contextListNewTabOpen;
    private LinearLayoutCompat contextListEdit;
    private LinearLayoutCompat contextListDelete;
    private LinearLayoutCompat contextListFav;
    private View floatButtonTabView;
    private View floatButtonSaveView;
    private View floatButtonShareView;
    private View floatButtonMoreView;
    private AppCompatImageButton tabNext;
    private AppCompatImageButton tabPrev;
    private AppCompatImageButton omniboxRefresh;
    private AppCompatImageButton openStartPage;
    private AppCompatImageButton openBookmark;
    private AppCompatImageButton openHistory;
    private FloatingActionButton fabImageButtonNav;
    private AppCompatAutoCompleteTextView inputBox;
    private ContentLoadingProgressBar progressBar;
    private AppCompatEditText searchBox;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog bottomSheetDialogOverView;
    private NinjaWebView ninjaWebView;
    private ListView listView;
    private AppCompatTextView omniboxTitle;
    private AppCompatTextView dialogTitle;
    private View customView;
    private VideoView videoView;
    private HorizontalScrollView tabScrollView;
    private AppCompatImageButton tabToggle;
    // Layouts
    private RelativeLayout appBar;
    private RelativeLayout omnibox;
    private RelativeLayout searchPanel;
    private FrameLayout contentFrame;
    private LinearLayoutCompat tabContainer;
    private FrameLayout fullscreenHolder;
    @Nullable
    private String url;
    private String overViewTab;
    private int originalOrientation;
    @Dimension
    private int dimen156dp;
    @Dimension
    private int dimen144dp;
    @Dimension
    private int dimen117dp;
    @Dimension
    private int dimen108dp;
    @Nullable
    private WebChromeClient.CustomViewCallback customViewCallback;
    @Nullable
    private ValueCallback<Uri[]> filePathCallback;
    private ValueCallback<Uri[]> mFilePathCallback;
    @Nullable
    private String mCameraPhotoPath;
    @Nullable
    private AlbumController currentAlbumController;
    private SharedPreferences sp;
    private SimpleEncryptor mahEncryptor;
    private Javascript javaHosts;
    private Cookie cookieHosts;
    private AdBlock adBlock;

    public BrowserActivity() {
        super();

        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(@NonNull Context context, @NonNull Intent intent) {
                bottomSheetDialog = new BottomSheetDialog(BrowserActivity.this);
                View dialogView = View.inflate(BrowserActivity.this, R.layout.dialog_action, null);
                AppCompatTextView textView = ViewCompat.requireViewById(dialogView, R.id.dialog_text);
                textView.setText(R.string.toast_downloadComplete);
                MaterialButton actionOk = ViewCompat.requireViewById(dialogView, R.id.action_ok);
                actionOk.setOnClickListener(view -> {
                    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    hideBottomSheetDialog();
                });
                MaterialButton actionCancel = ViewCompat.requireViewById(dialogView, R.id.action_cancel);
                actionCancel.setOnClickListener(view -> hideBottomSheetDialog());
                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
            }
        };

        pwCopy = new BroadcastReceiver() {
            @Override
            public void onReceive(@NonNull Context context, @NonNull Intent intent) {
                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText("text", decryptedUserPW);
                    clipboard.setPrimaryClip(clip);
                    NinjaToast.show(BrowserActivity.this, R.string.toast_copy_successful);
                } else {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_copy_failed);
                }
            }
        };

        unCopy = new BroadcastReceiver() {
            @Override
            public void onReceive(@NonNull Context context, @NonNull Intent intent) {
                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText("text", decryptedUserName);
                    clipboard.setPrimaryClip(clip);
                    NinjaToast.show(BrowserActivity.this, R.string.toast_copy_successful);
                } else {
                    NinjaToast.show(BrowserActivity.this, R.string.toast_copy_failed);
                }
            }
        };
    }

    @SuppressWarnings("SameReturnValue")
    private boolean onKeyCodeBack() {
        hideSoftInput(inputBox);
        hideOverview();

        if (omnibox.getVisibility() == View.GONE) {
            showOmnibox();
        } else {
            if (ninjaWebView.canGoBack()) {
                ninjaWebView.goBack();
            } else {
                removeAlbum(currentAlbumController);
            }
        }
        return true;
    }

    private boolean prepareRecord() {
        NinjaWebView currentWebView = (NinjaWebView) currentAlbumController;
        if (currentWebView == null) {
            return true;
        }
        String title = currentWebView.getTitle();
        String url = currentWebView.getUrl();
        return (title == null
                || title.isEmpty()
                || url == null
                || url.isEmpty()
                || url.startsWith(BrowserUnit.URL_SCHEME_ABOUT)
                || url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)
                || url.startsWith(BrowserUnit.URL_SCHEME_INTENT));
    }

    // Overrides
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .permitDiskReads()
                    .permitDiskWrites()
                    .permitNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putInt("restart_changed", 0).apply();

        HelperUnit.grantPermissionsStorage(this);
        HelperUnit.setTheme(this);

        setContentView(R.layout.activity_main);

        String saved = HelperUnit.safeGetString(sp, "saved_key_ok", "no");
        if (saved.equals("no")) {
            char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                    's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                    'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6',
                    '7', '8', '9', '0', '!', '§', '$', '%', '&', '/', '(', ')', '=', '?', ';', ':', '_', '-', '.', ',',
                    '+', '#', '*', '<', '>'};
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 25; ++i) {
                char c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }

            sp.edit().putBoolean(getString(R.string.sp_location), false)
                    .putString("saved_key", sb.toString())
                    .putString("saved_key_ok", "yes")
                    .putString("setting_gesture_tb_up", "08")
                    .putString("setting_gesture_tb_down", "01")
                    .putString("setting_gesture_tb_left", "07")
                    .putString("setting_gesture_tb_right", "06")
                    .putString("setting_gesture_nav_up", "04")
                    .putString("setting_gesture_nav_down", "05")
                    .putString("setting_gesture_nav_left", "03")
                    .putString("setting_gesture_nav_right", "02")
                    .apply();
        }

        String key = HelperUnit.safeGetString(sp, "saved_key", "");
        try {
            mahEncryptor = SimpleEncryptor.newInstance(key);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException |
                InvalidKeyException | UnsupportedEncodingException e) {
            Log.e(TAG, "Encrypt", e);
        }

        contentFrame = findViewById(R.id.main_content);
        appBar = findViewById(R.id.appBar);

        final RelativeLayout rootView = findViewById(R.id.rootView);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

            if (heightDiff > 100) {
                omniboxTitle.setVisibility(View.GONE);
            } else {
                omniboxTitle.setVisibility(View.VISIBLE);
            }
        });

        Resources resources = getResources();

        dimen156dp = resources.getDimensionPixelSize(R.dimen.layout_width_156dp);
        dimen144dp = resources.getDimensionPixelSize(R.dimen.layout_width_144dp);
        dimen117dp = resources.getDimensionPixelSize(R.dimen.layout_height_117dp);
        dimen108dp = resources.getDimensionPixelSize(R.dimen.layout_height_108dp);

        ninjaWebView = (NinjaWebView) currentAlbumController;

        initOmnibox();
        initSearchPanel();
        initOverview();

        if (sp.getBoolean("start_tabStart", true)) {
            showOverview();
        }

        javaHosts = new Javascript(this);
        cookieHosts = new Cookie(this);
        adBlock = new AdBlock(this);

        dispatchIntent(getIntent());

        // show changelog
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            final String versionName = pInfo.versionName;
            String oldVersionName = HelperUnit.safeGetString(sp, "oldVersionName", "0.0");

            if (!oldVersionName.equals(versionName)) {
                bottomSheetDialog = new BottomSheetDialog(this);
                View dialogView = View.inflate(this, R.layout.dialog_text, null);

                AppCompatTextView dialogTitle = ViewCompat.requireViewById(dialogView, R.id.dialog_title);
                dialogTitle.setText(R.string.changelog_title);

                AppCompatTextView dialogText = ViewCompat.requireViewById(dialogView, R.id.dialog_text);
                dialogText.setText(HelperUnit.textSpannable(getString(R.string.changelog_dialog)));
                dialogText.setMovementMethod(LinkMovementMethod.getInstance());

                AppCompatImageButton fab = ViewCompat.requireViewById(dialogView, R.id.floatButton_ok);
                fab.setOnClickListener(v -> {
                    sp.edit().putString("oldVersionName", versionName).apply();
                    hideBottomSheetDialog();
                });

                AppCompatImageButton fabHelp = ViewCompat.requireViewById(dialogView, R.id.floatButton_help);
                fabHelp.setOnClickListener(v -> showHelpDialog());

                AppCompatImageButton fabSettings = ViewCompat.requireViewById(dialogView, R.id.floatButton_settings);
                fabSettings.setOnClickListener(v -> {
                    Intent intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    hideBottomSheetDialog();
                });

                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Changelog", e);
        }

        clipboard = ContextCompat.getSystemService(this, ClipboardManager.class);
    }

    @Override
    public void onDestroy() {
        boolean clearIndexedDB = sp.getBoolean(("sp_clearIndexedDB"), false);
        if (clearIndexedDB) {
            BrowserUnit.clearIndexedDB(this);
        }

        Intent toHolderService = new Intent(this, HolderService.class);
        IntentUnit.setClear(true);
        stopService(toHolderService);

        if (sp.getBoolean(getString(R.string.sp_clear_quit), false)) {
            Intent toClearService = new Intent(this, ClearService.class);
            startService(toClearService);
        }

        BrowserContainer.clear();
        IntentUnit.setContext(null);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                return showOverflow();
            case KeyEvent.KEYCODE_BACK:
                // When video fullscreen, first close it
                if (fullscreenHolder != null || customView != null || videoView != null) {
                    return onHideCustomView();
                }
                return onKeyCodeBack();
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        Intent toHolderService = new Intent(this, HolderService.class);
        IntentUnit.setClear(false);
        stopService(toHolderService);
        inputBox.clearFocus();

        IntentUnit.setContext(this);

        unregisterReceiver(downloadReceiver);
        unregisterReceiver(unCopy);
        unregisterReceiver(pwCopy);
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentUnit.setContext(this);
        dispatchIntent(getIntent());

        if (sp.getInt("restart_changed", 1) == 1) {
            sp.edit().putInt("restart_changed", 0).apply();
            finish();
        }

        if (sp.getBoolean("pdf_create", false)) {
            bottomSheetDialog = new BottomSheetDialog(this);
            View dialogView = View.inflate(this, R.layout.dialog_action, null);
            AppCompatTextView textView = ViewCompat.requireViewById(dialogView, R.id.dialog_text);

            MaterialButton actionOk = ViewCompat.requireViewById(dialogView, R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                hideBottomSheetDialog();
            });
            MaterialButton actionCancel = ViewCompat.requireViewById(dialogView, R.id.action_cancel);
            actionCancel.setOnClickListener(view -> hideBottomSheetDialog());
            bottomSheetDialog.setContentView(dialogView);

            final File pathFile = new File(HelperUnit.safeGetString(sp, "pdf_path", ""));

            if (sp.getBoolean("pdf_share", false)) {
                if (pathFile.exists() && !sp.getBoolean("pdf_delete", false)) {
                    sp.edit().putBoolean("pdf_delete", true).apply();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, pathFile.getName());
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, pathFile.getName());
                    sharingIntent.setType("*/pdf");
                    Uri bmpUri = Uri.fromFile(pathFile);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    startActivity(Intent.createChooser(sharingIntent, getString(R.string.menu_share)));
                } else if (pathFile.exists() && sp.getBoolean("pdf_delete", false)) {
                    pathFile.delete();
                    sp.edit().putBoolean("pdf_create", false)
                            .putBoolean("pdf_share", false)
                            .putBoolean("pdf_delete", false).apply();
                } else {
                    sp.edit().putBoolean("pdf_create", false)
                            .putBoolean("pdf_share", false)
                            .putBoolean("pdf_delete", false).apply();
                    textView.setText(R.string.menu_share_pdfToast);
                    bottomSheetDialog.show();
                }
            } else {
                textView.setText(R.string.toast_downloadComplete);
                bottomSheetDialog.show();
                sp.edit().putBoolean("pdf_share", false)
                        .putBoolean("pdf_create", false)
                        .putBoolean("pdf_delete", false).apply();
            }
        }

        if (sp.getBoolean("delete_screenshot", false)) {
            File pathFile = new File(HelperUnit.safeGetString(sp, "screenshot_path", ""));

            if (pathFile.exists()) {
                pathFile.delete();
                sp.edit().putBoolean("delete_screenshot", false).apply();
            }
        }

        IntentFilter downloadFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, downloadFilter);

        IntentFilter intentFilterUnCopy = new IntentFilter("unCopy");
        registerReceiver(unCopy, intentFilterUnCopy);

        IntentFilter intentFilterPwCopy = new IntentFilter("pwCopy");
        registerReceiver(pwCopy, intentFilterPwCopy);
    }

    @Override
    public void updateAutoComplete() {
        RecordAction action = new RecordAction(this);
        action.open(false);
        List<Record> list = action.listBookmarks();
        list.addAll(action.listHistory());
        action.close();

        CompleteAdapter adapter = new CompleteAdapter(this, list);
        inputBox.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        inputBox.setDropDownWidth(ViewUnit.getWindowWidth(this));
        inputBox.setDropDownHorizontalOffset(16);
        inputBox.setOnItemClickListener((parent, view, position, id) -> {
            String url = LayoutUnit.getText(ViewCompat.requireViewById(view, R.id.record_item_url));
            inputBox.setText(url);
            updateAlbum(url);
            hideSoftInput(inputBox);
        });
    }

    @Override
    public void updateBookmarks() {
        RecordAction action = new RecordAction(this);
        action.open(false);
        action.close();
    }

    @Override
    public void updateInputBox(@Nullable String query) {
        inputBox.setText(query);
        inputBox.clearFocus();
    }

    @Override
    public synchronized void updateProgress(int progress) {
        progressBar.setProgress(progress);

        updateBookmarks();
        if (progress < BrowserUnit.PROGRESS_MAX) {
            updateRefresh(true);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            updateRefresh(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public synchronized void showAlbum(@NonNull AlbumController controller) {
        if (currentAlbumController != null) {
            currentAlbumController.deactivate();
        }
        contentFrame.removeAllViews();
        contentFrame.addView((View) controller);
        currentAlbumController = controller;
        currentAlbumController.activate();
        updateOmnibox();
    }

    @Override
    public synchronized void removeAlbum(@Nullable AlbumController controller) {
        if (BrowserContainer.size() <= 1) {
            if (!sp.getBoolean("sp_reopenLastTab", false)) {
                doubleTapsQuit();
            } else {
                updateAlbum(HelperUnit.safeGetString(sp, "favoriteURL", "https://github.com/eolme/browser-suze"));
                hideOverview();
            }
        } else if (controller != null) {
            closeTabConfirmation(() -> {
                tabContainer.removeView(controller.getAlbumView());
                int index = BrowserContainer.indexOf(controller);
                BrowserContainer.remove(controller);
                if (index >= BrowserContainer.size()) {
                    index = BrowserContainer.size() - 1;
                }
                showAlbum(BrowserContainer.get(index));
            });
        }
    }

    @Override
    public void showFileChooser(@NonNull ValueCallback<Uri[]> filePathCallback) {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
        }
        mFilePathCallback = filePathCallback;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Unable to create Image File", ex);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
    }

    @Override
    public void onShowCustomView(@Nullable View view, @Nullable WebChromeClient.CustomViewCallback callback) {
        if (view == null || currentAlbumController == null) {
            return;
        }

        if (customView != null && callback != null) {
            callback.onCustomViewHidden();
            return;
        }

        customView = view;
        originalOrientation = getRequestedOrientation();

        fullscreenHolder = new FullscreenHolder(this);
        fullscreenHolder.addView(
                customView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        decorView.addView(
                fullscreenHolder,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        customView.setKeepScreenOn(true);
        ((View) currentAlbumController).setVisibility(View.GONE);
        setCustomFullscreen(true);

        if (view instanceof FrameLayout) {
            if (((FrameLayout) view).getFocusedChild() instanceof VideoView) {
                videoView = (VideoView) ((FrameLayout) view).getFocusedChild();
                videoView.setOnErrorListener(new VideoCompletionListener());
                videoView.setOnCompletionListener(new VideoCompletionListener());
            }
        }
        customViewCallback = callback;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public boolean onHideCustomView() {
        if (customView == null || customViewCallback == null || currentAlbumController == null) {
            return false;
        }

        FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        decorView.removeView(fullscreenHolder);

        customView.setKeepScreenOn(false);
        ((View) currentAlbumController).setVisibility(View.VISIBLE);
        setCustomFullscreen(false);

        fullscreenHolder = null;
        customView = null;
        if (videoView != null) {
            videoView.setOnErrorListener(null);
            videoView.setOnCompletionListener(null);
            videoView = null;
        }
        setRequestedOrientation(originalOrientation);
        ninjaWebView.reload();

        return true;
    }

    // Methods

    @Override
    public void onLongPress(@Nullable String url) {
        if (url != null) {
            showLongPressMenu(url);
        } else {
            WebView.HitTestResult result = ninjaWebView.getHitTestResult();
            final int type = result.getType();

            if (type == WebView.HitTestResult.IMAGE_TYPE ||
                    type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE ||
                    type == WebView.HitTestResult.SRC_ANCHOR_TYPE) {

                String parsed = result.getExtra();
                if (parsed == null) {
                    showLongPressMenu(ninjaWebView.getUrl());
                } else {
                    showLongPressMenu(parsed);
                }
            }
        }
    }

    public void hideOverview() {
        if (bottomSheetDialogOverView != null) {
            bottomSheetDialogOverView.cancel();
        }
    }

    private void showOverview() {
        bottomSheetDialogOverView.show();
        new Handler().postDelayed(() -> {
            if (currentAlbumController != null) {
                tabScrollView.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
            }
        }, shortAnimTime);
    }

    private void hideBottomSheetDialog() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.cancel();
        }
    }

    @Override
    public void onClick(@NonNull View v) {
        RecordAction action = new RecordAction(this);
        ninjaWebView = (NinjaWebView) currentAlbumController;

        String title;
        if (ninjaWebView != null) {
            title = ninjaWebView.getTitle().trim();
            url = ninjaWebView.getUrl().trim();
        } else {
            title = "";
            url = "";
        }

        switch (v.getId()) {
            // Menu overflow

            case R.id.tab_prev:
                AlbumController controller = nextAlbumController(false);
                showAlbum(controller);
                updateOverflow();
                break;

            case R.id.tab_next:
                AlbumController controller2 = nextAlbumController(true);
                showAlbum(controller2);
                updateOverflow();
                break;

            case R.id.tab_plus:
                hideBottomSheetDialog();
                hideOverview();
                addAlbum(getString(R.string.album_untitled), HelperUnit.safeGetString(sp, "favoriteURL", "https://github.com/eolme/browser-suze"), true);
                break;

            case R.id.menu_newTabOpen:
                hideBottomSheetDialog();
                hideOverview();
                addAlbum(getString(R.string.album_untitled), HelperUnit.safeGetString(sp, "favoriteURL", "https://github.com/eolme/browser-suze"), true);
                break;

            case R.id.menu_closeTab:
                hideBottomSheetDialog();
                removeAlbum(currentAlbumController);
                break;

            case R.id.menu_tabPreview:
                hideBottomSheetDialog();
                showTabPreview();
                showOverview();
                break;

            case R.id.menu_quit:
                hideBottomSheetDialog();
                doubleTapsQuit();
                break;

            case R.id.menu_shareScreenshot:
                hideBottomSheetDialog();
                sp.edit().putInt("screenshot", 1).apply();
                new ScreenshotTask(this, ninjaWebView).execute();
                break;

            case R.id.menu_shareLink:
                hideBottomSheetDialog();
                if (prepareRecord()) {
                    NinjaToast.show(this, getString(R.string.toast_share_failed));
                } else {
                    IntentUnit.share(this, title, url);
                }
                break;

            case R.id.menu_sharePDF:
                hideBottomSheetDialog();
                printPDF(true);
                break;

            case R.id.menu_openWith:
                hideBottomSheetDialog();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                Intent chooser = Intent.createChooser(intent, getString(R.string.menu_open_with));
                startActivity(chooser);
                break;

            case R.id.menu_saveScreenshot:
                hideBottomSheetDialog();
                sp.edit().putInt("screenshot", 0).apply();
                new ScreenshotTask(this, ninjaWebView).execute();
                break;

            case R.id.menu_saveBookmark:
                hideBottomSheetDialog();

                String key = HelperUnit.safeGetString(sp, "saved_key", "");
                String encryptedUserName;
                String encryptedUserPW;

                try {
                    SimpleEncryptor mahEncryptor = SimpleEncryptor.newInstance(key);
                    encryptedUserName = mahEncryptor.encode("");
                    encryptedUserPW = mahEncryptor.encode("");
                } catch (Throwable e) {
                    Log.e(TAG, "Bookmark", e);
                    NinjaToast.show(this, R.string.toast_error);
                    return;
                }

                BookmarkList db = new BookmarkList(this);
                db.open();
                if (db.isExist(url)) {
                    NinjaToast.show(this, R.string.toast_newTitle);
                } else {
                    db.insert(HelperUnit.secString(ninjaWebView.getTitle()), url, encryptedUserName, encryptedUserPW, "01");
                    NinjaToast.show(this, R.string.toast_edit_successful);
                    initBookmarkList();
                }
                db.close();
                break;

            case R.id.menu_saveStart:
                hideBottomSheetDialog();
                action.open(true);
                if (action.checkGridItem(url)) {
                    NinjaToast.show(this, getString(R.string.toast_already_exist_in_home));
                } else {
                    Bitmap bitmap = ViewUnit.capture(ninjaWebView, dimen156dp, dimen117dp, Bitmap.Config.ARGB_8888);
                    String filename = System.currentTimeMillis() + BrowserUnit.SUFFIX_PNG;
                    int ordinal = action.listGrid().size();
                    GridItem itemAlbum = new GridItem(title, url, filename, ordinal);

                    if (BrowserUnit.bitmap2File(this, bitmap, filename) && action.addGridItem(itemAlbum)) {
                        NinjaToast.show(this, getString(R.string.toast_add_to_home_successful));
                    } else {
                        NinjaToast.show(this, getString(R.string.toast_add_to_home_failed));
                    }
                }
                action.close();
                break;

            // Omnibox

            case R.id.menu_searchSite:
                hideBottomSheetDialog();
                hideSoftInput(inputBox);
                showSearchPanel();
                break;

            case R.id.contextLink_saveAs:
                hideBottomSheetDialog();
                printPDF(false);
                break;

            case R.id.menu_settings:
                hideBottomSheetDialog();
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;

            case R.id.menu_help:
                showHelpDialog();
                break;

            case R.id.menu_download:
                hideBottomSheetDialog();
                startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                break;

            case R.id.floatButton_tab:
                menuNewTabOpen.setVisibility(View.VISIBLE);
                menuCloseTab.setVisibility(View.VISIBLE);
                menuTabPreview.setVisibility(View.VISIBLE);
                menuQuit.setVisibility(View.VISIBLE);

                menuShareScreenshot.setVisibility(View.GONE);
                menuShareLink.setVisibility(View.GONE);
                menuSharePDF.setVisibility(View.GONE);
                menuOpenWith.setVisibility(View.GONE);

                menuSaveScreenshot.setVisibility(View.GONE);
                menuSaveBookmark.setVisibility(View.GONE);
                menuSavePDF.setVisibility(View.GONE);
                menuSaveStart.setVisibility(View.GONE);

                floatButtonTabView.setVisibility(View.VISIBLE);
                floatButtonSaveView.setVisibility(View.INVISIBLE);
                floatButtonShareView.setVisibility(View.INVISIBLE);
                floatButtonMoreView.setVisibility(View.INVISIBLE);

                menuSearchSite.setVisibility(View.GONE);
                menuHelp.setVisibility(View.GONE);
                menuSettings.setVisibility(View.GONE);
                menuDownload.setVisibility(View.GONE);
                break;

            case R.id.floatButton_share:
                menuNewTabOpen.setVisibility(View.GONE);
                menuCloseTab.setVisibility(View.GONE);
                menuTabPreview.setVisibility(View.GONE);
                menuQuit.setVisibility(View.GONE);

                menuShareScreenshot.setVisibility(View.VISIBLE);
                menuShareLink.setVisibility(View.VISIBLE);
                menuSharePDF.setVisibility(View.VISIBLE);
                menuOpenWith.setVisibility(View.VISIBLE);

                menuSaveScreenshot.setVisibility(View.GONE);
                menuSaveBookmark.setVisibility(View.GONE);
                menuSavePDF.setVisibility(View.GONE);
                menuSaveStart.setVisibility(View.GONE);

                floatButtonTabView.setVisibility(View.INVISIBLE);
                floatButtonSaveView.setVisibility(View.INVISIBLE);
                floatButtonShareView.setVisibility(View.VISIBLE);
                floatButtonMoreView.setVisibility(View.INVISIBLE);

                menuSearchSite.setVisibility(View.GONE);
                menuHelp.setVisibility(View.GONE);
                menuSettings.setVisibility(View.GONE);
                menuDownload.setVisibility(View.GONE);
                break;

            case R.id.floatButton_save:
                menuNewTabOpen.setVisibility(View.GONE);
                menuCloseTab.setVisibility(View.GONE);
                menuTabPreview.setVisibility(View.GONE);
                menuQuit.setVisibility(View.GONE);

                menuShareScreenshot.setVisibility(View.GONE);
                menuShareLink.setVisibility(View.GONE);
                menuSharePDF.setVisibility(View.GONE);
                menuOpenWith.setVisibility(View.GONE);

                menuSaveScreenshot.setVisibility(View.VISIBLE);
                menuSaveBookmark.setVisibility(View.VISIBLE);
                menuSavePDF.setVisibility(View.VISIBLE);
                menuSaveStart.setVisibility(View.VISIBLE);

                menuSearchSite.setVisibility(View.GONE);
                menuHelp.setVisibility(View.GONE);

                floatButtonTabView.setVisibility(View.INVISIBLE);
                floatButtonSaveView.setVisibility(View.VISIBLE);
                floatButtonShareView.setVisibility(View.INVISIBLE);
                floatButtonMoreView.setVisibility(View.INVISIBLE);

                menuSettings.setVisibility(View.GONE);
                menuDownload.setVisibility(View.GONE);
                break;

            case R.id.floatButton_more:
                menuNewTabOpen.setVisibility(View.GONE);
                menuCloseTab.setVisibility(View.GONE);
                menuTabPreview.setVisibility(View.GONE);
                menuQuit.setVisibility(View.GONE);

                menuShareScreenshot.setVisibility(View.GONE);
                menuShareLink.setVisibility(View.GONE);
                menuSharePDF.setVisibility(View.GONE);
                menuOpenWith.setVisibility(View.GONE);

                menuSaveScreenshot.setVisibility(View.GONE);
                menuSaveBookmark.setVisibility(View.GONE);
                menuSavePDF.setVisibility(View.GONE);
                menuSaveStart.setVisibility(View.GONE);

                floatButtonTabView.setVisibility(View.INVISIBLE);
                floatButtonSaveView.setVisibility(View.INVISIBLE);
                floatButtonShareView.setVisibility(View.INVISIBLE);
                floatButtonMoreView.setVisibility(View.VISIBLE);

                menuSettings.setVisibility(View.VISIBLE);
                menuSearchSite.setVisibility(View.VISIBLE);
                menuHelp.setVisibility(View.VISIBLE);
                menuDownload.setVisibility(View.VISIBLE);

                break;

            // Buttons

            case R.id.omnibox_overview:
                showOverview();
                break;

            case R.id.omnibox_refresh:
                if (ninjaWebView.isLoadFinish()) {
                    if (!url.startsWith("https://")) {
                        bottomSheetDialog = new BottomSheetDialog(this);
                        View dialogView = View.inflate(this, R.layout.dialog_action, null);
                        AppCompatTextView textView = ViewCompat.requireViewById(dialogView, R.id.dialog_text);
                        textView.setText(R.string.toast_unsecured);
                        MaterialButton actionOk = ViewCompat.requireViewById(dialogView, R.id.action_ok);
                        actionOk.setOnClickListener(view -> {
                            hideBottomSheetDialog();
                            ninjaWebView.loadUrl(url.replace("http://", "https://"));
                        });
                        MaterialButton actionCancel2 = ViewCompat.requireViewById(dialogView, R.id.action_cancel);
                        actionCancel2.setOnClickListener(view -> {
                            hideBottomSheetDialog();
                            ninjaWebView.reload();
                        });
                        bottomSheetDialog.setContentView(dialogView);
                        bottomSheetDialog.show();
                    } else {
                        ninjaWebView.reload();
                    }
                } else {
                    ninjaWebView.stopLoading();
                }
                break;

            default:
                break;
        }
    }

    private void printPDF(boolean share) {
        boolean success = true;
        try {
            sp.edit().putBoolean("pdf_share", share).apply();

            String title = HelperUnit.fileName(ninjaWebView.getUrl());
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            File file = new File(dir, title + ".pdf");
            sp.edit().putString("pdf_path", file.getPath()).apply();

            String pdfTitle = file.getName().replace(".pdf", "");

            PrintManager printManager = ContextCompat.getSystemService(this, PrintManager.class);
            if (printManager != null) {
                PrintDocumentAdapter printAdapter = ninjaWebView.createPrintDocumentAdapter(title);
                printManager.print(pdfTitle, printAdapter,
                        new PrintAttributes.Builder().setColorMode(PrintAttributes.COLOR_MODE_COLOR).build());
            } else {
                success = false;
            }
        } catch (Throwable e) {
            Log.e(TAG, "PDF", e);
            success = false;
        } finally {
            sp.edit().putBoolean("pdf_create", success).apply();
        }
    }

    private void dispatchIntent(@NonNull Intent intent) {
        Intent toHolderService = new Intent(this, HolderService.class);
        IntentUnit.setClear(false);
        stopService(toHolderService);

        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_WEB_SEARCH)) {
            // From ActionMode and some others
            pinAlbums(intent.getStringExtra(SearchManager.QUERY));
        } else if (filePathCallback != null) {
            filePathCallback = null;
        } else if ("sc_history".equals(action)) {
            pinAlbums(HelperUnit.safeGetString(sp, "favoriteURL", "https://github.com/eolme/browser-suze"));
            showOverview();
            new Handler().postDelayed(() -> openHistory.performClick(), mediumAnimTime);
        } else if ("sc_bookmark".equals(action)) {
            pinAlbums(HelperUnit.safeGetString(sp, "favoriteURL", "https://github.com/eolme/browser-suze"));
            showOverview();
            new Handler().postDelayed(() -> openBookmark.performClick(), mediumAnimTime);
        } else if ("sc_start_page".equals(action)) {
            pinAlbums(HelperUnit.safeGetString(sp, "favoriteURL", "https://github.com/eolme/browser-suze"));
            showOverview();
            new Handler().postDelayed(() -> openStartPage.performClick(), mediumAnimTime);
        } else if (Intent.ACTION_SEND.equals(action)) {
            pinAlbums(intent.getStringExtra(Intent.EXTRA_TEXT));
        } else {
            pinAlbums(null);
        }
        getIntent().setAction("");
    }

    private void initRendering(@NonNull View view) {
        if (sp.getBoolean("sp_invert", false)) {
            Paint paint = new Paint();
            ColorMatrix matrix = new ColorMatrix();
            matrix.set(NEGATIVE_COLOR);
            ColorMatrix gcm = new ColorMatrix();
            gcm.setSaturation(0);
            ColorMatrix concat = new ColorMatrix();
            concat.setConcat(matrix, gcm);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(concat);
            paint.setColorFilter(filter);
            // maybe sometime LAYER_TYPE_NONE would better?
            view.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
        } else {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOmnibox() {
        omnibox = findViewById(R.id.main_omnibox);
        inputBox = findViewById(R.id.main_omnibox_input);
        omniboxRefresh = findViewById(R.id.omnibox_refresh);
        AppCompatImageButton omniboxOverview = findViewById(R.id.omnibox_overview);
        AppCompatImageButton omniboxOverflow = findViewById(R.id.omnibox_overflow);
        omniboxTitle = findViewById(R.id.omnibox_title);
        progressBar = findViewById(R.id.main_progress_bar);

        String nav = HelperUnit.safeGetString(sp, "nav_position", "0");
        int fabPosition = Integer.parseInt(nav);

        switch (fabPosition) {
            case 0:
                fabImageButtonNav = findViewById(R.id.fab_imageButtonNav_right);
                break;
            case 1:
                fabImageButtonNav = findViewById(R.id.fab_imageButtonNav_left);
                break;
            case 2:
                fabImageButtonNav = findViewById(R.id.fab_imageButtonNav_center);
                break;
            default:
                fabImageButtonNav = findViewById(R.id.fab_imageButtonNav_right);
                break;
        }

        fabImageButtonNav.setOnLongClickListener(v -> {
            showFastToggle();
            return false;
        });

        omniboxOverflow.setOnLongClickListener(v -> {
            showFastToggle();
            return false;
        });

        fabImageButtonNav.setOnClickListener(v -> showOverflow());

        omniboxOverflow.setOnClickListener(v -> showOverflow());

        if (sp.getBoolean("sp_gestures_use", true)) {
            fabImageButtonNav.setOnTouchListener(new SwipeTouchListener(this) {
                public void onSwipeRight() {
                    performGesture("setting_gesture_nav_right");
                }

                public void onSwipeLeft() {
                    performGesture("setting_gesture_nav_left");
                }

                public void onSwipeTop() {
                    performGesture("setting_gesture_nav_up");
                }

                public void onSwipeBottom() {
                    performGesture("setting_gesture_nav_down");
                }
            });

            inputBox.setOnTouchListener(new SwipeTouchListener(this) {
                public void onSwipeRight() {
                    performGesture("setting_gesture_tb_right");
                }

                public void onSwipeLeft() {
                    performGesture("setting_gesture_tb_left");
                }

                public void onSwipeTop() {
                    performGesture("setting_gesture_tb_up");
                }

                public void onSwipeBottom() {
                    performGesture("setting_gesture_tb_down");
                }
            });
        }

        inputBox.setOnEditorActionListener((v, actionId, event) -> {
            if (ninjaWebView == null || actionId != EditorInfo.IME_ACTION_GO) {
                return false;
            }

            String query = LayoutUnit.getText(inputBox);
            if (query.isEmpty()) {
                NinjaToast.show(this, getString(R.string.toast_input_empty));
                return true;
            }

            updateAlbum(query);
            hideSoftInput(inputBox);
            return false;
        });

        inputBox.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                omniboxTitle.setVisibility(View.GONE);
            } else {
                omniboxTitle.setVisibility(View.VISIBLE);
            }
        });

        updateBookmarks();
        updateAutoComplete();

        omniboxRefresh.setOnClickListener(this);
        omniboxOverview.setOnClickListener(this);
    }

    private void performGesture(@NonNull String gesture) {
        ninjaWebView = (NinjaWebView) currentAlbumController;
        String fabPosition = HelperUnit.safeGetString(sp, gesture, "0");

        switch (fabPosition) {
            case "02":
                if (ninjaWebView.canGoForward()) {
                    ninjaWebView.goForward();
                } else {
                    NinjaToast.show(this, R.string.toast_webview_forward);
                }
                break;
            case "03":
                if (ninjaWebView.canGoBack()) {
                    ninjaWebView.goBack();
                } else {
                    removeAlbum(currentAlbumController);
                }
                break;
            case "04":
                ninjaWebView.pageUp(true);
                break;
            case "05":
                ninjaWebView.pageDown(true);
                break;
            case "06":
                AlbumController controller = nextAlbumController(false);
                showAlbum(controller);
                break;
            case "07":
                AlbumController controller2 = nextAlbumController(true);
                showAlbum(controller2);
                break;
            case "08":
                showOverview();
                break;
            case "09":
                addAlbum(getString(R.string.album_untitled), HelperUnit.safeGetString(sp, "favoriteURL", "https://github.com/eolme/browser-suze"), true);
                break;
            case "10":
                removeAlbum(currentAlbumController);
                break;
            default:
                break;
        }
    }

    private void hideTabPreview() {
        tabScrollView.setVisibility(View.GONE);
        tabToggle.setVisibility(View.VISIBLE);
    }

    private void showTabPreview() {
        tabScrollView.setVisibility(View.VISIBLE);
        if (currentAlbumController != null) {
            tabScrollView.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
        }
        tabToggle.setVisibility(View.GONE);
    }

    private void initOverview() {
        bottomSheetDialogOverView = new BottomSheetDialog(this);
        View dialogView = View.inflate(this, R.layout.dialog_overiew, null);

        openStartPage = ViewCompat.requireViewById(dialogView, R.id.open_newTab_2);
        openBookmark = ViewCompat.requireViewById(dialogView, R.id.open_bookmark_2);
        openHistory = ViewCompat.requireViewById(dialogView, R.id.open_history_2);
        AppCompatImageButton openMenu = ViewCompat.requireViewById(dialogView, R.id.open_menu);
        tabContainer = ViewCompat.requireViewById(dialogView, R.id.tab_container);
        AppCompatImageButton tabPlus = ViewCompat.requireViewById(dialogView, R.id.tab_plus);
        tabScrollView = ViewCompat.requireViewById(dialogView, R.id.tab_ScrollView);
        tabToggle = ViewCompat.requireViewById(dialogView, R.id.tab_toggle);
        tabPlus.setOnClickListener(this);
        listView = ViewCompat.requireViewById(dialogView, R.id.home_list_2);

        final MaterialButton relayoutOK = ViewCompat.requireViewById(dialogView, R.id.relayout_ok);
        final RecyclerView gridView = ViewCompat.requireViewById(dialogView, R.id.home_grid_2);
        final View openStartPageView = ViewCompat.requireViewById(dialogView, R.id.open_newTabView);
        final View openBookmarkView = ViewCompat.requireViewById(dialogView, R.id.open_bookmarkView);
        final View openHistoryView = ViewCompat.requireViewById(dialogView, R.id.open_historyView);
        final AppCompatTextView overviewTitle = ViewCompat.requireViewById(dialogView, R.id.overview_title);

        final AppCompatImageButton overviewPrev = ViewCompat.requireViewById(dialogView, R.id.overview_prev);
        final AppCompatImageButton overviewNext = ViewCompat.requireViewById(dialogView, R.id.overview_next);

        gridView.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);

        tabToggle.setOnClickListener(v -> showTabPreview());

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(@NonNull AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                hideTabPreview();
            }

            public void onScroll(@NonNull AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }
        });

        gridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                hideTabPreview();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        openStartPage.setOnClickListener(v -> {
            gridView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            openStartPageView.setVisibility(View.VISIBLE);
            openBookmarkView.setVisibility(View.INVISIBLE);
            openHistoryView.setVisibility(View.INVISIBLE);
            overviewTitle.setText(getString(R.string.album_title_home));
            overviewNext.setImageResource(R.drawable.icon_bookmark);
            overviewPrev.setImageResource(R.drawable.icon_history);
            overViewTab = getString(R.string.album_title_home);

            GridLayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);

            // drag & drop manager
            RecyclerViewDragDropManager recyclearManager = new RecyclerViewDragDropManager();
//            recyclearManager.setDraggingItemShadowDrawable(
//                    (NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z3));
            // Start dragging after long press
            recyclearManager.setInitiateOnLongPress(true);
            recyclearManager.setInitiateOnMove(false);
            recyclearManager.setLongPressTimeout(750);

            // setup dragging item effects (NOTE: DraggableItemAnimator is required)
            recyclearManager.setDragStartItemAnimationDuration(250);
            recyclearManager.setDraggingItemAlpha(0.8f);
            recyclearManager.setDraggingItemScale(1.3f);
            recyclearManager.setDraggingItemRotation(15.0f);

            gridView.setLayoutManager(layoutManager);

            RecordAction action = new RecordAction(this);
            action.open(false);
            GridAdapter gridAdapter = new GridAdapter(
                    new GridProvider(
                            action.listGrid(),
                            new GridProvider.OnItemClickListener() {
                                @Override
                                public void onClick(@NonNull GridItem item) {
                                    updateAlbum(item.getURL());
                                    hideOverview();
                                }

                                @Override
                                public void onLongClick(@NonNull GridItem item) {
                                    showGridMenu(item);
                                }
                            }
                    )
            );
            gridView.setAdapter(recyclearManager.createWrappedAdapter(gridAdapter));
            action.close();

            recyclearManager.attachRecyclerView(gridView);
        });

        openBookmark.setOnClickListener(v -> {
            gridView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            openStartPageView.setVisibility(View.INVISIBLE);
            openBookmarkView.setVisibility(View.VISIBLE);
            openHistoryView.setVisibility(View.INVISIBLE);
            overviewTitle.setText(getString(R.string.album_title_bookmarks));
            overviewNext.setImageResource(R.drawable.icon_history);
            overviewPrev.setImageResource(R.drawable.icon_earth);
            overViewTab = getString(R.string.album_title_bookmarks);
            sp.edit().putString("filter_passBY", "00").apply();
            initBookmarkList();
        });

        openBookmark.setOnLongClickListener(v -> {
            showFilterDialog();
            return false;
        });

        openHistory.setOnClickListener(v -> {
            gridView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            openStartPageView.setVisibility(View.INVISIBLE);
            openBookmarkView.setVisibility(View.INVISIBLE);
            openHistoryView.setVisibility(View.VISIBLE);
            overviewTitle.setText(getString(R.string.album_title_history));
            overviewNext.setImageResource(R.drawable.icon_earth);
            overviewPrev.setImageResource(R.drawable.icon_bookmark);
            overViewTab = getString(R.string.album_title_history);

            RecordAction action = new RecordAction(this);
            action.open(false);
            final List<Record> list = action.listHistory();
            action.close();

            final AdapterRecord adapter = new AdapterRecord(this, list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener((parent, view, position, id) -> {
                updateAlbum(list.get(position).getURL());
                hideOverview();
            });

            listView.setOnItemLongClickListener((parent, view, position, id) -> {
                showListMenu(adapter, list, position);
                return true;
            });
        });

        openMenu.setOnClickListener(v -> {
            bottomSheetDialog = new BottomSheetDialog(this);
            View dialogView1 = View.inflate(this, R.layout.dialog_menu_overview, null);

            menuSettings = ViewCompat.requireViewById(dialogView1, R.id.menu_settings);
            menuSettings.setOnClickListener(this);

            menuQuit = ViewCompat.requireViewById(dialogView1, R.id.menu_quit);
            menuQuit.setOnClickListener(this);

            LinearLayoutCompat tvRelayout = ViewCompat.requireViewById(dialogView1, R.id.tv_relayout);
            LinearLayoutCompat bookmarkSort = ViewCompat.requireViewById(dialogView1, R.id.bookmark_sort);
            LinearLayoutCompat bookmarkFilter = ViewCompat.requireViewById(dialogView1, R.id.bookmark_filter);
            LinearLayoutCompat bookmarkBlank = ViewCompat.requireViewById(dialogView1, R.id.bookmark_blank);

            if (overViewTab.equals(getString(R.string.album_title_home))) {
                tvRelayout.setVisibility(View.VISIBLE);
            } else {
                tvRelayout.setVisibility(View.GONE);
            }

            if (overViewTab.equals(getString(R.string.album_title_bookmarks))) {
                bookmarkFilter.setVisibility(View.VISIBLE);
                bookmarkSort.setVisibility(View.VISIBLE);
                bookmarkBlank.setVisibility(View.VISIBLE);
            } else {
                bookmarkFilter.setVisibility(View.GONE);
                bookmarkSort.setVisibility(View.GONE);
                bookmarkBlank.setVisibility(View.GONE);
            }

            bookmarkFilter.setOnClickListener(v1 -> showFilterDialog());

            bookmarkBlank.setOnClickListener(v12 -> {
                hideBottomSheetDialog();
                sp.edit().putString("favoriteURL", "about:blank").apply();
                NinjaToast.show(this, R.string.toast_fav);
            });

            bookmarkSort.setOnClickListener(v13 -> {
                hideBottomSheetDialog();
                bottomSheetDialog = new BottomSheetDialog(this);
                View dialogView11 = View.inflate(this, R.layout.dialog_bookmark_sort, null);
                LinearLayoutCompat dialogSortName = ViewCompat.requireViewById(dialogView11, R.id.dialog_sortName);
                dialogSortName.setOnClickListener(v131 -> {
                    sp.edit().putString("sortDBB", "title").apply();
                    initBookmarkList();
                    hideBottomSheetDialog();
                });
                LinearLayoutCompat dialogSortIcon = ViewCompat.requireViewById(dialogView11, R.id.dialog_sortIcon);
                dialogSortIcon.setOnClickListener(v1312 -> {
                    sp.edit().putString("sortDBB", "icon").apply();
                    initBookmarkList();
                    hideBottomSheetDialog();
                });
                bottomSheetDialog.setContentView(dialogView11);
                bottomSheetDialog.show();
            });

            tvRelayout.setOnClickListener(v14 -> {
                SimpleWrapperAdapter wrapperAdapter = ((SimpleWrapperAdapter) gridView.getAdapter());
                if (wrapperAdapter == null) {
                    return;
                }
                GridAdapter gridAdapter = (GridAdapter) wrapperAdapter.getWrappedAdapter();
                if (gridAdapter == null) {
                    return;
                }

                hideBottomSheetDialog();
                omnibox.setVisibility(View.GONE);
                appBar.setVisibility(View.GONE);
                omniboxTitle.setVisibility(View.GONE);
                relayoutOK.setVisibility(View.VISIBLE);

                relayoutOK.setOnClickListener(v141 -> {
                    relayoutOK.setVisibility(View.GONE);

                    List<GridItem> gridList = gridAdapter.getProvider().getItems();

                    RecordAction action = new RecordAction(this);
                    action.open(true);
                    action.clearGrid();
                    for (@Nullable GridItem item : gridList) {
                        action.addGridItem(item);
                    }
                    action.close();
                    gridAdapter.stopEditMode();
                    NinjaToast.show(this, getString(R.string.toast_relayout_successful));
                });

                gridAdapter.startEditMode();
            });

            menuHelp = ViewCompat.requireViewById(dialogView1, R.id.menu_help);
            menuHelp.setOnClickListener(v15 -> showHelpDialog());

            LinearLayoutCompat tvDelete = ViewCompat.requireViewById(dialogView1, R.id.tv_delete);
            tvDelete.setOnClickListener(v16 -> {
                hideBottomSheetDialog();
                bottomSheetDialog = new BottomSheetDialog(this);
                View dialogView3 = View.inflate(this, R.layout.dialog_action, null);
                AppCompatTextView textView = ViewCompat.requireViewById(dialogView3, R.id.dialog_text);
                textView.setText(R.string.hint_database);
                MaterialButton actionOk = ViewCompat.requireViewById(dialogView3, R.id.action_ok);
                actionOk.setOnClickListener(view -> {
                    if (overViewTab.equals(getString(R.string.album_title_home))) {
                        BrowserUnit.clearHome(this);
                        openStartPage.performClick();
                    } else if (overViewTab.equals(getString(R.string.album_title_bookmarks))) {
                        File data = Environment.getDataDirectory();
                        String bookmarksPath_app = "//data//" + getPackageName() + "//databases//" + BookmarkList.DATABASE_NAME;
                        final File bookmarkFile_app = new File(data, bookmarksPath_app);
                        BrowserUnit.deleteDir(bookmarkFile_app);
                        openBookmark.performClick();
                    } else if (overViewTab.equals(getString(R.string.album_title_history))) {
                        BrowserUnit.clearHistory(this);
                        openHistory.performClick();
                    }
                    hideBottomSheetDialog();
                    omniboxRefresh.performClick();
                });
                MaterialButton actionCancel = ViewCompat.requireViewById(dialogView3, R.id.action_cancel);
                actionCancel.setOnClickListener(view -> hideBottomSheetDialog());
                bottomSheetDialog.setContentView(dialogView3);
                bottomSheetDialog.show();
            });

            bottomSheetDialog.setContentView(dialogView1);
            bottomSheetDialog.show();
        });

        bottomSheetDialogOverView.setContentView(dialogView);

        overviewPrev.setOnClickListener(v -> {
            if (overViewTab.equals(getString(R.string.album_title_home))) {
                openHistory.performClick();
            } else if (overViewTab.equals(getString(R.string.album_title_bookmarks))) {
                openStartPage.performClick();
            } else if (overViewTab.equals(getString(R.string.album_title_history))) {
                openBookmark.performClick();
            }
        });

        overviewNext.setOnClickListener(v -> {
            if (overViewTab.equals(getString(R.string.album_title_home))) {
                openBookmark.performClick();
            } else if (overViewTab.equals(getString(R.string.album_title_bookmarks))) {
                openHistory.performClick();
            } else if (overViewTab.equals(getString(R.string.album_title_history))) {
                openStartPage.performClick();
            }
        });

        String tab = HelperUnit.safeGetString(sp, "start_tab", "0");
        switch (tab) {
            case "0":
                openStartPage.performClick();
                break;
            case "3":
                openBookmark.performClick();
                break;
            case "4":
                openHistory.performClick();
                break;
            default:
                openStartPage.performClick();
                break;
        }
    }

    private void initSearchPanel() {
        searchPanel = findViewById(R.id.main_search_panel);
        searchBox = findViewById(R.id.main_search_box);
        // Views
        AppCompatImageButton searchUp = findViewById(R.id.main_search_up);
        AppCompatImageButton searchDown = findViewById(R.id.main_search_down);
        AppCompatImageButton searchCancel = findViewById(R.id.main_search_cancel);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(@NonNull Editable s) {
                if (currentAlbumController != null) {
                    ((NinjaWebView) currentAlbumController).findAllAsync(s.toString());
                }
            }
        });

        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE) {
                return false;
            }

            if (LayoutUnit.isEmpty(searchBox)) {
                NinjaToast.show(this, getString(R.string.toast_input_empty));
                return true;
            }
            return false;
        });

        searchUp.setOnClickListener(v -> {
            if (LayoutUnit.isEmpty(searchBox)) {
                NinjaToast.show(this, getString(R.string.toast_input_empty));
                return;
            }

            hideSoftInput(searchBox);

            if (currentAlbumController != null) {
                ((NinjaWebView) currentAlbumController).findNext(false);
            }
        });

        searchDown.setOnClickListener(v -> {
            if (LayoutUnit.isEmpty(searchBox)) {
                NinjaToast.show(this, getString(R.string.toast_input_empty));
                return;
            }

            hideSoftInput(searchBox);

            if (currentAlbumController != null) {
                ((NinjaWebView) currentAlbumController).findNext(true);
            }
        });

        searchCancel.setOnClickListener(v -> hideSearchPanel());
    }

    private void initBookmarkList() {
        final BookmarkList db = new BookmarkList(this);
        db.open();

        final int layoutStyle = R.layout.list_item_bookmark;
        int[] xmlId = new int[]{
                R.id.record_item_title
        };
        String[] column = new String[]{
                "pass_title",
        };

        String search = HelperUnit.safeGetString(sp, "filter_passBY", "00");
        Cursor bookmarkCursor;
        if (search.equals("00")) {
            bookmarkCursor = db.fetchAllData(this);
        } else {
            bookmarkCursor = db.fetchDataByFilter(search, "pass_creation");
        }

        if (bookmarkCursor == null || bookmarkCursor.isClosed()) {
            Log.e(TAG, "Cursor is unavailable");
            db.close();
            return;
        }

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, layoutStyle, bookmarkCursor, column, xmlId, 0) {
            @Override
            public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
                Cursor row = (Cursor) listView.getItemAtPosition(position);
                final String bookmarksIcon = DatabaseUnit.getSafeString(row, "pass_creation");

                View v = super.getView(position, convertView, parent);
                AppCompatImageView ivIcon = ViewCompat.requireViewById(v, R.id.ib_icon);
                HelperUnit.switchIcon(BrowserActivity.this, bookmarksIcon, "pass_creation", ivIcon);

                return v;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            db.open();

            Cursor passCursor;
            if (search.equals("00")) {
                passCursor = db.fetchAllData(this);
            } else {
                passCursor = db.fetchDataByFilter(search, "pass_creation");
            }

            if (passCursor == null) {
                Log.e(TAG, "Cursor is null");
                db.close();
                return;
            }

            final String passContent = DatabaseUnit.getSafeString(passCursor, "pass_content");
            final String passIcon = DatabaseUnit.getSafeString(passCursor, "pass_icon");
            final String passAttachment = DatabaseUnit.getSafeString(passCursor, "pass_attachment");

            db.close();

            updateAlbum(passContent);
            toastLogin(passIcon, passAttachment);
            hideOverview();
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Cursor listRow = (Cursor) listView.getItemAtPosition(position);
            String _id = DatabaseUnit.getSafeString(listRow, "_id");
            String passTitle = DatabaseUnit.getSafeString(listRow, "pass_title");
            String passContent = DatabaseUnit.getSafeString(listRow, "pass_content");
            String passIcon = DatabaseUnit.getSafeString(listRow, "pass_icon");
            String passAttachment = DatabaseUnit.getSafeString(listRow, "pass_attachment");
            String passCreation = DatabaseUnit.getSafeString(listRow, "pass_creation");

            bottomSheetDialog = new BottomSheetDialog(this);
            View dialogView = View.inflate(this, R.layout.dialog_menu_context_list, null);

            contextListNewTab = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_newTab);
            contextListNewTab.setVisibility(View.VISIBLE);
            contextListNewTab.setOnClickListener(v -> {
                addAlbum(getString(R.string.album_untitled), passContent, false);
                NinjaToast.show(this, getString(R.string.toast_new_tab_successful));
                toastLogin(passIcon, passAttachment);
                hideBottomSheetDialog();
            });

            contextListNewTabOpen = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_newTabOpen);
            contextListNewTabOpen.setVisibility(View.VISIBLE);
            contextListNewTabOpen.setOnClickListener(v -> {
                addAlbum(getString(R.string.album_untitled), passContent, true);
                toastLogin(passIcon, passAttachment);
                hideBottomSheetDialog();
                hideOverview();
            });

            contextListFav = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_fav);
            contextListFav.setVisibility(View.VISIBLE);
            contextListFav.setOnClickListener(v -> {
                hideBottomSheetDialog();
                sp.edit().putString("favoriteURL", passContent).apply();
                NinjaToast.show(this, R.string.toast_fav);
            });

            contextListEdit = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_edit);
            contextListEdit.setVisibility(View.VISIBLE);
            contextListEdit.setOnClickListener(v -> {
                hideBottomSheetDialog();

                bottomSheetDialog = new BottomSheetDialog(this);

                View dialogView1 = View.inflate(this, R.layout.dialog_edit_bookmark, null);

                final AppCompatEditText passTitleET = ViewCompat.requireViewById(dialogView1, R.id.pass_title);
                final AppCompatEditText passUserNameET = ViewCompat.requireViewById(dialogView1, R.id.pass_userName);
                final AppCompatEditText passUserPWET = ViewCompat.requireViewById(dialogView1, R.id.pass_userPW);
                final AppCompatEditText passURLET = ViewCompat.requireViewById(dialogView1, R.id.pass_url);
                final AppCompatImageView ibIcon = ViewCompat.requireViewById(dialogView1, R.id.ib_icon);

                try {
                    decryptedUserName = mahEncryptor.decode(passIcon);
                    decryptedUserPW = mahEncryptor.decode(passAttachment);
                } catch (Throwable e) {
                    Log.e(TAG, "Encrypt", e);
                    NinjaToast.show(this, R.string.toast_error);
                }

                passTitleET.setText(passTitle);
                passUserNameET.setText(decryptedUserName);
                passUserPWET.setText(decryptedUserPW);
                passURLET.setText(passContent);

                MaterialButton actionOk = ViewCompat.requireViewById(dialogView1, R.id.action_ok);
                actionOk.setOnClickListener(view1 -> {
                    String inputPass_title = LayoutUnit.getText(passTitleET);
                    String inputPass_url = LayoutUnit.getText(passURLET);

                    String encryptedUserName;
                    String encryptedUserPW;

                    try {
                        encryptedUserName = mahEncryptor.encode(LayoutUnit.getText(passUserNameET));
                        encryptedUserPW = mahEncryptor.encode(LayoutUnit.getText(passUserPWET));
                    } catch (Throwable e) {
                        Log.e(TAG, "Encrypt", e);
                        NinjaToast.show(this, R.string.toast_error);
                        hideBottomSheetDialog();
                        return;
                    }

                    db.open();
                    db.update(Integer.parseInt(_id),
                            HelperUnit.secString(inputPass_title),
                            HelperUnit.secString(inputPass_url),
                            HelperUnit.secString(encryptedUserName),
                            HelperUnit.secString(encryptedUserPW), passCreation);
                    db.close();
                    initBookmarkList();
                    hideSoftInput(passTitleET);
                    NinjaToast.show(this, R.string.toast_edit_successful);
                    hideBottomSheetDialog();
                });
                MaterialButton actionCancel = ViewCompat.requireViewById(dialogView1, R.id.action_cancel);
                actionCancel.setOnClickListener(view1 -> {
                    hideSoftInput(passTitleET);
                    hideBottomSheetDialog();
                });
                HelperUnit.switchIcon(this, passCreation, "pass_creation", ibIcon);
                bottomSheetDialog.setContentView(dialogView1);
                bottomSheetDialog.show();

                ibIcon.setOnClickListener(v1 -> {
                    final String inputPass_title = LayoutUnit.getText(passTitleET);
                    final String inputPass_url = LayoutUnit.getText(passURLET);
                    String encryptedUserName;
                    String encryptedUserPW;

                    try {
                        encryptedUserName = mahEncryptor.encode(LayoutUnit.getText(passUserNameET));
                        encryptedUserPW = mahEncryptor.encode(LayoutUnit.getText(passUserPWET));
                    } catch (Throwable e) {
                        Log.e(TAG, "Save", e);
                        hideBottomSheetDialog();
                        NinjaToast.show(this, R.string.toast_error);
                        return;
                    }

                    hideBottomSheetDialog();
                    hideSoftInput(passTitleET);

                    bottomSheetDialog = new BottomSheetDialog(this);
                    View dialogView11 = View.inflate(this, R.layout.dialog_edit_icon, null);

                    LinearLayoutCompat icon01 = ViewCompat.requireViewById(dialogView11, R.id.icon_01);
                    icon01.setOnClickListener(v11 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "01");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    LinearLayoutCompat icon02 = ViewCompat.requireViewById(dialogView11, R.id.icon_02);
                    icon02.setOnClickListener(v112 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "02");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    LinearLayoutCompat icon03 = ViewCompat.requireViewById(dialogView11, R.id.icon_03);
                    icon03.setOnClickListener(v113 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "03");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    LinearLayoutCompat icon04 = ViewCompat.requireViewById(dialogView11, R.id.icon_04);
                    icon04.setOnClickListener(v114 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "04");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    LinearLayoutCompat icon05 = ViewCompat.requireViewById(dialogView11, R.id.icon_05);
                    icon05.setOnClickListener(v1111 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "05");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    LinearLayoutCompat icon06 = ViewCompat.requireViewById(dialogView11, R.id.icon_06);
                    icon06.setOnClickListener(v115 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "06");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    LinearLayoutCompat icon07 = ViewCompat.requireViewById(dialogView11, R.id.icon_07);
                    icon07.setOnClickListener(v116 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "07");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    LinearLayoutCompat icon08 = ViewCompat.requireViewById(dialogView11, R.id.icon_08);
                    icon08.setOnClickListener(v117 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "08");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    LinearLayoutCompat icon09 = ViewCompat.requireViewById(dialogView11, R.id.icon_09);
                    icon09.setOnClickListener(v118 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "09");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    LinearLayoutCompat icon10 = ViewCompat.requireViewById(dialogView11, R.id.icon_10);
                    icon10.setOnClickListener(v119 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "10");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    LinearLayoutCompat icon11 = ViewCompat.requireViewById(dialogView11, R.id.icon_11);
                    icon11.setOnClickListener(v1110 -> {
                        db.open();
                        db.update(Integer.parseInt(_id), HelperUnit.secString(inputPass_title), HelperUnit.secString(inputPass_url), HelperUnit.secString(encryptedUserName), HelperUnit.secString(encryptedUserPW), "11");
                        db.close();
                        initBookmarkList();
                        hideBottomSheetDialog();
                    });

                    bottomSheetDialog.setContentView(dialogView11);
                    bottomSheetDialog.show();
                    NinjaToast.show(this, R.string.toast_edit_successful);
                });
            });

            contextListDelete = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_delete);
            contextListDelete.setVisibility(View.VISIBLE);
            contextListDelete.setOnClickListener(v -> {
                hideBottomSheetDialog();
                bottomSheetDialog = new BottomSheetDialog(this);
                View dialogView12 = View.inflate(this, R.layout.dialog_action, null);
                AppCompatTextView textView = ViewCompat.requireViewById(dialogView12, R.id.dialog_text);
                textView.setText(R.string.toast_titleConfirm_delete);
                MaterialButton actionOk = ViewCompat.requireViewById(dialogView12, R.id.action_ok);
                actionOk.setOnClickListener(view12 -> {
                    db.open();
                    db.delete(Integer.parseInt(_id));
                    db.close();
                    initBookmarkList();
                    hideBottomSheetDialog();
                });
                MaterialButton actionCancel = ViewCompat.requireViewById(dialogView12, R.id.action_cancel);
                actionCancel.setOnClickListener(view12 -> hideBottomSheetDialog());
                bottomSheetDialog.setContentView(dialogView12);
                bottomSheetDialog.show();
            });

            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();
            return true;
        });

        db.close();
    }

    private void showFastToggle() {
        bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = View.inflate(this, R.layout.dialog_toggle, null);

        MaterialCheckBox swJava = ViewCompat.requireViewById(dialogView, R.id.switch_js);
        AppCompatImageButton whiteListJS = ViewCompat.requireViewById(dialogView, R.id.imageButton_js);
        MaterialCheckBox swAdBlock = ViewCompat.requireViewById(dialogView, R.id.switch_adBlock);
        AppCompatImageButton whiteList_ab = ViewCompat.requireViewById(dialogView, R.id.imageButton_ab);
        MaterialCheckBox swImage = ViewCompat.requireViewById(dialogView, R.id.switch_images);
        MaterialCheckBox swRemote = ViewCompat.requireViewById(dialogView, R.id.switch_remote);
        MaterialCheckBox swCookie = ViewCompat.requireViewById(dialogView, R.id.switch_cookie);
        AppCompatImageButton whitelistCookie = ViewCompat.requireViewById(dialogView, R.id.imageButton_cookie);
        MaterialCheckBox swLocation = ViewCompat.requireViewById(dialogView, R.id.switch_location);
        MaterialCheckBox swInvert = ViewCompat.requireViewById(dialogView, R.id.switch_invert);
        MaterialCheckBox swHistory = ViewCompat.requireViewById(dialogView, R.id.switch_history);

        ninjaWebView = ((NinjaWebView) currentAlbumController);
        if (ninjaWebView == null) {
            return;
        }

        final String url = ninjaWebView.getUrl();

        if (javaHosts.isWhite(url)) {
            whiteListJS.setImageResource(R.drawable.check_green);
        } else {
            whiteListJS.setImageResource(R.drawable.ic_action_close_red);
        }

        if (cookieHosts.isWhite(url)) {
            whitelistCookie.setImageResource(R.drawable.check_green);
        } else {
            whitelistCookie.setImageResource(R.drawable.ic_action_close_red);
        }

        if (sp.getBoolean(getString(R.string.sp_javascript), true)) {
            swJava.setChecked(true);
        } else {
            swJava.setChecked(false);
        }

        whiteListJS.setOnClickListener(view -> {
            if (javaHosts.isWhite(ninjaWebView.getUrl())) {
                whiteListJS.setImageResource(R.drawable.ic_action_close_red);
                javaHosts.removeDomain(BrowserUnit.safeGetHost(url));
            } else {
                whiteListJS.setImageResource(R.drawable.check_green);
                javaHosts.addDomain(BrowserUnit.safeGetHost(url));
            }
        });

        whitelistCookie.setOnClickListener(view -> {
            if (cookieHosts.isWhite(ninjaWebView.getUrl())) {
                whitelistCookie.setImageResource(R.drawable.ic_action_close_red);
                cookieHosts.removeDomain(BrowserUnit.safeGetHost(url));
            } else {
                whitelistCookie.setImageResource(R.drawable.check_green);
                cookieHosts.addDomain(BrowserUnit.safeGetHost(url));
            }
        });

        swJava.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sp.edit().putBoolean(getString(R.string.sp_javascript), true).apply();
            } else {
                sp.edit().putBoolean(getString(R.string.sp_javascript), false).apply();
            }
        });

        if (adBlock.isWhite(url)) {
            whiteList_ab.setImageResource(R.drawable.check_green);
        } else {
            whiteList_ab.setImageResource(R.drawable.ic_action_close_red);
        }

        if (sp.getBoolean(getString(R.string.sp_ad_block), true)) {
            swAdBlock.setChecked(true);
        } else {
            swAdBlock.setChecked(false);
        }

        whiteList_ab.setOnClickListener(view -> {
            if (adBlock.isWhite(ninjaWebView.getUrl())) {
                whiteList_ab.setImageResource(R.drawable.ic_action_close_red);
                adBlock.removeDomain(BrowserUnit.safeGetHost(url));
            } else {
                whiteList_ab.setImageResource(R.drawable.check_green);
                adBlock.addDomain(BrowserUnit.safeGetHost(url));
            }
        });

        swAdBlock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sp.edit().putBoolean(getString(R.string.sp_ad_block), true).apply();
            } else {
                sp.edit().putBoolean(getString(R.string.sp_ad_block), false).apply();
            }
        });

        if (sp.getBoolean(getString(R.string.sp_images), true)) {
            swImage.setChecked(true);
        } else {
            swImage.setChecked(false);
        }

        swImage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sp.edit().putBoolean(getString(R.string.sp_images), true).apply();
            } else {
                sp.edit().putBoolean(getString(R.string.sp_images), false).apply();
            }
        });

        if (sp.getBoolean(("sp_remote"), true)) {
            swRemote.setChecked(true);
        } else {
            swRemote.setChecked(false);
        }

        swRemote.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sp.edit().putBoolean(("sp_remote"), true).apply();
            } else {
                sp.edit().putBoolean(("sp_remote"), false).apply();
            }
        });

        if (sp.getBoolean(getString(R.string.sp_cookies), true)) {
            swCookie.setChecked(true);
        } else {
            swCookie.setChecked(false);
        }

        swCookie.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sp.edit().putBoolean(getString(R.string.sp_cookies), true).apply();
            } else {
                sp.edit().putBoolean(getString(R.string.sp_cookies), false).apply();
            }
        });

        if (sp.getBoolean("saveHistory", true)) {
            swHistory.setChecked(true);
        } else {
            swHistory.setChecked(false);
        }

        swHistory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sp.edit().putBoolean("saveHistory", true).apply();
            } else {
                sp.edit().putBoolean("saveHistory", false).apply();
            }
        });

        if (!sp.getBoolean(getString(R.string.sp_location), true)) {
            swLocation.setChecked(false);
        } else {
            swLocation.setChecked(true);
        }

        swLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sp.edit().putBoolean(getString(R.string.sp_location), true).apply();
            } else {
                sp.edit().putBoolean(getString(R.string.sp_location), false).apply();
            }
        });

        if (!sp.getBoolean("sp_invert", false)) {
            swInvert.setChecked(false);
        } else {
            swInvert.setChecked(true);
        }

        swInvert.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sp.edit().putBoolean("sp_invert", true).apply();
                initRendering(contentFrame);
            } else {
                sp.edit().putBoolean("sp_invert", false).apply();
                initRendering(contentFrame);
            }
        });

        final AppCompatTextView fontText = ViewCompat.requireViewById(dialogView, R.id.font_text);
        fontText.setText(HelperUnit.safeGetString(sp, "sp_fontSize", "100"));

        AppCompatImageButton fontMinus = ViewCompat.requireViewById(dialogView, R.id.font_minus);
        fontMinus.setImageResource(R.drawable.icon_minus);
        fontMinus.setOnClickListener(view -> {
            switch (HelperUnit.safeGetString(sp, "sp_fontSize", "100")) {
                case "75":
                    Log.i(TAG, "Can not change font size");
                    break;
                case "100":
                    sp.edit().putString("sp_fontSize", "75").apply();
                    break;
                case "125":
                    sp.edit().putString("sp_fontSize", "100").apply();
                    break;
                case "150":
                    sp.edit().putString("sp_fontSize", "125").apply();
                    break;
            }
            fontText.setText(HelperUnit.safeGetString(sp, "sp_fontSize", "100"));
        });

        AppCompatImageButton fontPlus = ViewCompat.requireViewById(dialogView, R.id.font_plus);
        fontPlus.setImageResource(R.drawable.icon_plus);
        fontPlus.setOnClickListener(view -> {
            String fontSize = HelperUnit.safeGetString(sp, "sp_fontSize", "100");
            switch (fontSize) {
                case "75":
                    sp.edit().putString("sp_fontSize", "100").apply();
                    break;
                case "100":
                    sp.edit().putString("sp_fontSize", "125").apply();
                    break;
                case "125":
                    sp.edit().putString("sp_fontSize", "150").apply();
                    break;
                case "150":
                    Log.i(TAG, "Can not change font size");
                    break;
                default:
                    break;
            }
            fontText.setText(HelperUnit.safeGetString(sp, "sp_fontSize", "100"));
        });

        MaterialButton butOK = ViewCompat.requireViewById(dialogView, R.id.action_ok);
        butOK.setOnClickListener(view -> {
            if (ninjaWebView != null) {
                hideBottomSheetDialog();
                ninjaWebView.initPreferences();
                ninjaWebView.reload();
            }
        });

        MaterialButton actionCancel = ViewCompat.requireViewById(dialogView, R.id.action_cancel);
        actionCancel.setOnClickListener(view -> hideBottomSheetDialog());

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void toastLogin(@NonNull String userName, @NonNull String passWord) {
        try {
            decryptedUserName = mahEncryptor.decode(userName);
            decryptedUserPW = mahEncryptor.decode(passWord);
        } catch (Throwable e) {
            Log.e(TAG, "Login", e);
            NinjaToast.show(this, R.string.toast_error);
            return;
        }

        Intent copy = new Intent("unCopy");
        PendingIntent copyUN = PendingIntent.getBroadcast(this, 0, copy, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent copy2 = new Intent("pwCopy");
        PendingIntent copyPW = PendingIntent.getBroadcast(this, 1, copy2, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder;

        NotificationManager mNotificationManager = ContextCompat.getSystemService(this, NotificationManager.class);
        if (mNotificationManager == null) {
            NinjaToast.show(this, R.string.toast_error);
            return;
        }

        String CHANNEL_ID = "browser_not"; // The id of the channel.
        CharSequence name = getString(R.string.app_name); // The user-visible name of the channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        NotificationCompat.Action actionUN = new NotificationCompat.Action.Builder(R.drawable.icon_earth, getString(R.string.toast_titleConfirm_pasteUN), copyUN).build();
        NotificationCompat.Action actionPW = new NotificationCompat.Action.Builder(R.drawable.icon_earth, getString(R.string.toast_titleConfirm_pastePW), copyPW).build();

        Notification n = builder
                .setSmallIcon(R.drawable.ic_notification_ninja)
                .setContentTitle(this.getString(R.string.app_name))
                .setContentText(this.getString(R.string.toast_titleConfirm_paste))
                .setColor(ContextCompat.getColor(this, R.color.secondaryColor))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0])
                .addAction(actionUN)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .addAction(actionPW)
                .build();

        if (!decryptedUserName.isEmpty() || !decryptedUserPW.isEmpty()) {
            mNotificationManager.notify(0, n);
        }
    }

    private synchronized void addAlbum(@NonNull String title, @Nullable String url, boolean foreground) {
        showOmnibox();
        ninjaWebView = new NinjaWebView(this);
        ninjaWebView.setBrowserController(this);
        ninjaWebView.setAlbumTitle(title);
        ViewUnit.bound(this, ninjaWebView);

        final View albumView = ninjaWebView.getAlbumView();
        if (currentAlbumController != null) {
            int index = BrowserContainer.indexOf(currentAlbumController) + 1;
            BrowserContainer.add(ninjaWebView, index);
            tabContainer.addView(albumView, index, new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        } else {
            BrowserContainer.add(ninjaWebView);
            tabContainer.addView(albumView, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        }
        if (!foreground) {
            ViewUnit.bound(this, ninjaWebView);
            ninjaWebView.loadUrl(url);
            ninjaWebView.deactivate();
            return;
        }

        showAlbum(ninjaWebView);

        if (url != null && !url.isEmpty()) {
            ninjaWebView.loadUrl(url);
        }
    }

    private synchronized void pinAlbums(@Nullable String url) {
        showOmnibox();
        hideSoftInput(inputBox);
        hideSearchPanel();
        tabContainer.removeAllViews();

        if (ninjaWebView == null) {
            ninjaWebView = new NinjaWebView(this);
        }

        for (@NonNull AlbumController controller : BrowserContainer.list()) {
            if (controller instanceof NinjaWebView) {
                ((NinjaWebView) controller).setBrowserController(this);
            }
            tabContainer.addView(controller.getAlbumView(), LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            controller.getAlbumView().setVisibility(View.VISIBLE);
            controller.deactivate();
        }

        if (url == null) {
            if (BrowserContainer.size() < 1) {
                addAlbum("", sp.getString("favoriteURL", "https://github.com/eolme/browser-suze"), true);
            } else {
                if (currentAlbumController != null) {
                    currentAlbumController.activate();
                    return;
                }

                int index = BrowserContainer.size() - 1;
                currentAlbumController = BrowserContainer.get(index);
                contentFrame.removeAllViews();
                contentFrame.addView((View) currentAlbumController);
                currentAlbumController.activate();

                updateOmnibox();
            }
        } else { // When url != null
            ninjaWebView.setBrowserController(this);
            ninjaWebView.setAlbumTitle(getString(R.string.album_untitled));
            ViewUnit.bound(this, ninjaWebView);
            ninjaWebView.loadUrl(url);

            BrowserContainer.add(ninjaWebView);
            final View albumView = ninjaWebView.getAlbumView();
            tabContainer.addView(albumView, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            contentFrame.removeAllViews();
            contentFrame.addView(ninjaWebView);

            currentAlbumController = ninjaWebView;
            currentAlbumController.activate();

            updateOmnibox();
        }
    }

    private synchronized void updateAlbum(@Nullable String url) {
        if (currentAlbumController != null) {
            ((NinjaWebView) currentAlbumController).loadUrl(url);
        }
        updateOmnibox();
    }

    private void closeTabConfirmation(@NonNull Runnable okAction) {
        if (!sp.getBoolean("sp_close_tab_confirm", true)) {
            okAction.run();
        } else {
            bottomSheetDialog = new BottomSheetDialog(this);
            View dialogView = View.inflate(this, R.layout.dialog_action, null);
            AppCompatTextView textView = ViewCompat.requireViewById(dialogView, R.id.dialog_text);
            textView.setText(R.string.toast_close_tab);
            MaterialButton actionOk = ViewCompat.requireViewById(dialogView, R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                okAction.run();
                hideBottomSheetDialog();
            });
            MaterialButton actionCancel = ViewCompat.requireViewById(dialogView, R.id.action_cancel);
            actionCancel.setOnClickListener(view -> hideBottomSheetDialog());
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();
        }
    }

    private void updateOmnibox() {
        if (currentAlbumController == null) {
            currentAlbumController = new NinjaWebView(this);
        }

        initRendering(contentFrame);

        omniboxTitle.setText(currentAlbumController.getAlbumTitle());
        ninjaWebView = (NinjaWebView) currentAlbumController;
        updateProgress(ninjaWebView.getProgress());
        updateBookmarks();
        scrollChange();

        if (ninjaWebView.getUrl() == null && ninjaWebView.getOriginalUrl() == null) {
            updateInputBox(null);
        } else if (ninjaWebView.getUrl() != null) {
            updateInputBox(ninjaWebView.getUrl());
        } else {
            updateInputBox(ninjaWebView.getOriginalUrl());
        }

        contentFrame.postDelayed(() -> currentAlbumController.setAlbumCover(ViewUnit.capture(((View) currentAlbumController), dimen144dp, dimen108dp, Bitmap.Config.RGB_565)), shortAnimTime);
    }

    private void scrollChange() {
        String hideToolbar = HelperUnit.safeGetString(sp, "sp_hideToolbar", "0");
        if (hideToolbar.equals("0") || hideToolbar.equals("1")) {
            ninjaWebView.setOnScrollChangeListener((scrollY, oldScrollY) -> {
                if (hideToolbar.equals("0")) {
                    if (scrollY > oldScrollY) {
                        hideOmnibox();
                    } else if (scrollY < oldScrollY) {
                        showOmnibox();
                    }
                } else {
                    hideOmnibox();
                }
            });
        }
    }

    private void updateRefresh(boolean running) {
        if (running) {
            omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(this, R.drawable.ic_action_close));
        } else {
            try {
                if (ninjaWebView.getUrl().contains("https://")) {
                    omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(this, R.drawable.ic_action_refresh));
                } else {
                    omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(this, R.drawable.icon_alert));
                }
            } catch (Throwable e) {
                omniboxRefresh.setImageDrawable(ViewUnit.getDrawable(this, R.drawable.ic_action_refresh));
            }
        }
    }

    @Nullable
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void showLongPressMenu(@NonNull String url) {
        bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = View.inflate(this, R.layout.dialog_menu_context_link, null);
        dialogTitle = ViewCompat.requireViewById(dialogView, R.id.dialog_title);
        dialogTitle.setText(url);

        LinearLayoutCompat contextLink_newTab = ViewCompat.requireViewById(dialogView, R.id.contextLink_newTab);
        contextLink_newTab.setOnClickListener(v -> {
            addAlbum(getString(R.string.album_untitled), url, false);
            NinjaToast.show(this, getString(R.string.toast_new_tab_successful));
            hideBottomSheetDialog();
        });

        LinearLayoutCompat contextLink__shareLink = ViewCompat.requireViewById(dialogView, R.id.contextLink__shareLink);
        contextLink__shareLink.setOnClickListener(v -> {
            if (prepareRecord()) {
                NinjaToast.show(this, getString(R.string.toast_share_failed));
            } else {
                IntentUnit.share(this, "", url);
            }
            hideBottomSheetDialog();
        });

        LinearLayoutCompat contextLink_openWith = ViewCompat.requireViewById(dialogView, R.id.contextLink_openWith);
        contextLink_openWith.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            Intent chooser = Intent.createChooser(intent, getString(R.string.menu_open_with));
            startActivity(chooser);
            hideBottomSheetDialog();
        });

        LinearLayoutCompat contextLink_newTabOpen = ViewCompat.requireViewById(dialogView, R.id.contextLink_newTabOpen);
        contextLink_newTabOpen.setOnClickListener(v -> {
            addAlbum(getString(R.string.album_untitled), url, true);
            hideBottomSheetDialog();
        });

        LinearLayoutCompat contextLink_saveAs = ViewCompat.requireViewById(dialogView, R.id.contextLink_saveAs);
        contextLink_saveAs.setOnClickListener(v -> {
            try {
                hideBottomSheetDialog();

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_AlertDialog);
                View dialogView1 = View.inflate(this, R.layout.dialog_edit_extension, null);

                final AppCompatEditText editTitle = ViewCompat.requireViewById(dialogView1, R.id.dialog_edit);
                final AppCompatEditText editExtension = ViewCompat.requireViewById(dialogView1, R.id.dialog_edit_extension);

                editTitle.setHint(R.string.dialog_title_hint);
                editTitle.setText(HelperUnit.fileName(ninjaWebView.getUrl()));

                String urlExtension = url.substring(url.lastIndexOf("."));
                if (urlExtension.length() <= 8) {
                    editExtension.setText(urlExtension);
                }

                builder.setView(dialogView1);
                builder.setTitle(R.string.menu_edit);
                builder.setPositiveButton(R.string.app_ok, (dialog, whichButton) -> {
                    hideSoftInput(editTitle);

                    String title = LayoutUnit.getText(editTitle);
                    String extension = LayoutUnit.getText(editExtension);
                    String filename = title + extension;

                    if (title.isEmpty() || extension.isEmpty() || !extension.startsWith(".")) {
                        NinjaToast.show(this, getString(R.string.toast_input_empty));
                    } else {
                        int HAS_WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (HAS_WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                            NinjaToast.show(this, R.string.toast_permission_sdCard_sec);
                        } else {
                            Uri source = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(source);
                            request.addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url));
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                            DownloadManager dm = ContextCompat.getSystemService(this, DownloadManager.class);
                            if (dm != null) {
                                dm.enqueue(request);
                            } else {
                                NinjaToast.show(this, R.string.toast_error);
                            }
                        }
                    }
                });
                builder.setNegativeButton(R.string.app_cancel, (dialog, whichButton) -> {
                    dialog.cancel();
                    hideSoftInput(editTitle);
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                showSoftInput(editTitle);
            } catch (Throwable e) {
                Log.e(TAG, "SaveAs", e);
            }
        });
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void doubleTapsQuit() {
        if (!sp.getBoolean("sp_close_browser_confirm", true)) {
            finish();
        } else {
            bottomSheetDialog = new BottomSheetDialog(this);
            View dialogView = View.inflate(this, R.layout.dialog_action, null);
            AppCompatTextView textView = ViewCompat.requireViewById(dialogView, R.id.dialog_text);
            textView.setText(R.string.toast_quit);
            MaterialButton actionOk = ViewCompat.requireViewById(dialogView, R.id.action_ok);
            actionOk.setOnClickListener(view -> finish());
            MaterialButton actionCancel = ViewCompat.requireViewById(dialogView, R.id.action_cancel);
            actionCancel.setOnClickListener(view -> hideBottomSheetDialog());
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();
        }
    }

    private void hideSoftInput(@NonNull View view) {
        view.clearFocus();
        InputMethodManager imm = ContextCompat.getSystemService(this, InputMethodManager.class);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showSoftInput(@NonNull View view) {
        new Handler().postDelayed(() -> {
            view.requestFocus();
            InputMethodManager imm = ContextCompat.getSystemService(this, InputMethodManager.class);
            if (imm != null) {
                imm.showSoftInput(view, 0);
                // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }, mediumAnimTime);
    }

    private void showOmnibox() {
        if (omnibox.getVisibility() == View.GONE && searchPanel.getVisibility() == View.GONE) {
            searchPanel.setVisibility(View.GONE);
            omnibox.setVisibility(View.VISIBLE);
            appBar.setVisibility(View.VISIBLE);

            if (HelperUnit.safeGetString(sp, "sp_hideNav", "0").equals("0")) {
                fabImageButtonNav.setVisibility(View.GONE);
            }
        }
    }

    private void hideOmnibox() {
        if (omnibox.getVisibility() == View.VISIBLE) {
            omnibox.setVisibility(View.GONE);
            searchPanel.setVisibility(View.GONE);
            appBar.setVisibility(View.GONE);

            String hideNav = HelperUnit.safeGetString(sp, "sp_hideNav", "0");
            if (hideNav.equals("0") || hideNav.equals("2")) {
                fabImageButtonNav.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideSearchPanel() {
        hideSoftInput(searchBox);
        omniboxTitle.setVisibility(View.VISIBLE);
        searchBox.setText("");
        searchPanel.setVisibility(View.GONE);
        omnibox.setVisibility(View.VISIBLE);
    }

    private void showSearchPanel() {
        showOmnibox();
        omnibox.setVisibility(View.GONE);
        omniboxTitle.setVisibility(View.GONE);
        searchPanel.setVisibility(View.VISIBLE);
        showSoftInput(searchBox);
    }

    private void updateOverflow() {
        new Handler().postDelayed(() -> dialogTitle.setText(ninjaWebView.getTitle()), shortAnimTime);

        if (BrowserContainer.size() <= 1) {
            tabNext.setVisibility(View.GONE);
            tabPrev.setVisibility(View.GONE);
        } else {
            tabNext.setVisibility(View.VISIBLE);
            tabPrev.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("SameReturnValue")
    private boolean showOverflow() {
        bottomSheetDialog = new BottomSheetDialog(this);

        View dialogView = View.inflate(this, R.layout.dialog_menu, null);

        AppCompatImageButton fabTab = ViewCompat.requireViewById(dialogView, R.id.floatButton_tab);
        fabTab.setOnClickListener(this);
        AppCompatImageButton fabShare = ViewCompat.requireViewById(dialogView, R.id.floatButton_share);
        fabShare.setOnClickListener(this);
        AppCompatImageButton fabSave = ViewCompat.requireViewById(dialogView, R.id.floatButton_save);
        fabSave.setOnClickListener(this);
        AppCompatImageButton fabMore = ViewCompat.requireViewById(dialogView, R.id.floatButton_more);
        fabMore.setOnClickListener(this);

        tabPrev = ViewCompat.requireViewById(dialogView, R.id.tab_prev);
        tabPrev.setOnClickListener(this);
        tabNext = ViewCompat.requireViewById(dialogView, R.id.tab_next);
        tabNext.setOnClickListener(this);

        floatButtonTabView = ViewCompat.requireViewById(dialogView, R.id.floatButton_tabView);
        floatButtonSaveView = ViewCompat.requireViewById(dialogView, R.id.floatButton_saveView);
        floatButtonShareView = ViewCompat.requireViewById(dialogView, R.id.floatButton_shareView);
        floatButtonMoreView = ViewCompat.requireViewById(dialogView, R.id.floatButton_moreView);

        dialogTitle = ViewCompat.requireViewById(dialogView, R.id.dialog_title);

        menuNewTabOpen = ViewCompat.requireViewById(dialogView, R.id.menu_newTabOpen);
        menuNewTabOpen.setOnClickListener(this);
        menuCloseTab = ViewCompat.requireViewById(dialogView, R.id.menu_closeTab);
        menuCloseTab.setOnClickListener(this);
        menuTabPreview = ViewCompat.requireViewById(dialogView, R.id.menu_tabPreview);
        menuTabPreview.setOnClickListener(this);
        menuQuit = ViewCompat.requireViewById(dialogView, R.id.menu_quit);
        menuQuit.setOnClickListener(this);

        menuShareScreenshot = ViewCompat.requireViewById(dialogView, R.id.menu_shareScreenshot);
        menuShareScreenshot.setOnClickListener(this);
        menuShareLink = ViewCompat.requireViewById(dialogView, R.id.menu_shareLink);
        menuShareLink.setOnClickListener(this);
        menuSharePDF = ViewCompat.requireViewById(dialogView, R.id.menu_sharePDF);
        menuSharePDF.setOnClickListener(this);
        menuOpenWith = ViewCompat.requireViewById(dialogView, R.id.menu_openWith);
        menuOpenWith.setOnClickListener(this);

        menuSaveScreenshot = ViewCompat.requireViewById(dialogView, R.id.menu_saveScreenshot);
        menuSaveScreenshot.setOnClickListener(this);
        menuSaveBookmark = ViewCompat.requireViewById(dialogView, R.id.menu_saveBookmark);
        menuSaveBookmark.setOnClickListener(this);
        menuSavePDF = ViewCompat.requireViewById(dialogView, R.id.contextLink_saveAs);
        menuSavePDF.setOnClickListener(this);
        menuSaveStart = ViewCompat.requireViewById(dialogView, R.id.menu_saveStart);
        menuSaveStart.setOnClickListener(this);

        menuSearchSite = ViewCompat.requireViewById(dialogView, R.id.menu_searchSite);
        menuSearchSite.setOnClickListener(this);
        menuSettings = ViewCompat.requireViewById(dialogView, R.id.menu_settings);
        menuSettings.setOnClickListener(this);
        menuDownload = ViewCompat.requireViewById(dialogView, R.id.menu_download);
        menuDownload.setOnClickListener(this);
        menuHelp = ViewCompat.requireViewById(dialogView, R.id.menu_help);
        menuHelp.setOnClickListener(this);

        updateOverflow();

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
        return true;
    }

    private void showGridMenu(@NonNull GridItem gridItem) {
        bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = View.inflate(this, R.layout.dialog_menu_context_list, null);

        contextListNewTab = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_newTab);
        contextListNewTab.setVisibility(View.VISIBLE);
        contextListNewTab.setOnClickListener(v -> {
            addAlbum(getString(R.string.album_untitled), gridItem.getURL(), false);
            NinjaToast.show(this, getString(R.string.toast_new_tab_successful));
            hideBottomSheetDialog();
        });

        contextListNewTabOpen = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_newTabOpen);
        contextListNewTabOpen.setVisibility(View.VISIBLE);
        contextListNewTabOpen.setOnClickListener(v -> {
            addAlbum(getString(R.string.album_untitled), gridItem.getURL(), true);
            hideBottomSheetDialog();
            hideOverview();
        });

        contextListDelete = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_delete);
        contextListDelete.setVisibility(View.VISIBLE);
        contextListDelete.setOnClickListener(v -> {
            hideBottomSheetDialog();
            bottomSheetDialog = new BottomSheetDialog(this);
            View dialogView1 = View.inflate(this, R.layout.dialog_action, null);
            AppCompatTextView textView = ViewCompat.requireViewById(dialogView1, R.id.dialog_text);
            textView.setText(R.string.toast_titleConfirm_delete);
            MaterialButton actionOk = ViewCompat.requireViewById(dialogView1, R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                RecordAction action = new RecordAction(this);
                action.open(true);
                action.deleteGridItem(gridItem);
                action.close();
                deleteFile(gridItem.getFilename());
                openStartPage.performClick();
                hideBottomSheetDialog();
            });
            MaterialButton actionCancel = ViewCompat.requireViewById(dialogView1, R.id.action_cancel);
            actionCancel.setOnClickListener(view -> hideBottomSheetDialog());
            bottomSheetDialog.setContentView(dialogView1);
            bottomSheetDialog.show();
        });

        contextListEdit = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_edit);
        contextListEdit.setVisibility(View.VISIBLE);
        contextListEdit.setOnClickListener(v -> {
            hideBottomSheetDialog();

            bottomSheetDialog = new BottomSheetDialog(this);
            View dialogView12 = View.inflate(this, R.layout.dialog_edit_title, null);

            final AppCompatEditText editText = ViewCompat.requireViewById(dialogView12, R.id.dialog_edit);

            editText.setHint(R.string.dialog_title_hint);
            editText.setText(gridItem.getTitle());

            MaterialButton actionOk = ViewCompat.requireViewById(dialogView12, R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                String text = LayoutUnit.getText(editText);
                if (text.isEmpty()) {
                    NinjaToast.show(this, getString(R.string.toast_input_empty));
                } else {
                    RecordAction action = new RecordAction(this);
                    action.open(true);
                    gridItem.setTitle(text);
                    action.updateGridItem(gridItem);
                    action.close();
                    hideSoftInput(editText);
                    openStartPage.performClick();
                }
                hideBottomSheetDialog();
            });
            MaterialButton actionCancel = ViewCompat.requireViewById(dialogView12, R.id.action_cancel);
            actionCancel.setOnClickListener(view -> {
                hideSoftInput(editText);
                hideBottomSheetDialog();
            });
            bottomSheetDialog.setContentView(dialogView12);
            bottomSheetDialog.show();
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void showListMenu(@NonNull AdapterRecord adapterRecord, @NonNull List<Record> recordList, int location) {
        final Record record = recordList.get(location);

        bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = View.inflate(this, R.layout.dialog_menu_context_list, null);

        contextListNewTab = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_newTab);
        contextListNewTab.setOnClickListener(v -> {
            addAlbum(getString(R.string.album_untitled), record.getURL(), false);
            NinjaToast.show(this, getString(R.string.toast_new_tab_successful));
            hideBottomSheetDialog();
        });

        contextListNewTabOpen = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_newTabOpen);
        contextListNewTabOpen.setOnClickListener(v -> {
            addAlbum(getString(R.string.album_untitled), record.getURL(), true);
            hideBottomSheetDialog();
            hideOverview();
        });

        contextListDelete = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_delete);
        contextListDelete.setOnClickListener(v -> {
            hideBottomSheetDialog();
            bottomSheetDialog = new BottomSheetDialog(this);
            View dialogView1 = View.inflate(this, R.layout.dialog_action, null);
            AppCompatTextView textView = ViewCompat.requireViewById(dialogView1, R.id.dialog_text);
            textView.setText(R.string.toast_titleConfirm_delete);
            MaterialButton actionOk = ViewCompat.requireViewById(dialogView1, R.id.action_ok);
            actionOk.setOnClickListener(view -> {
                RecordAction action = new RecordAction(this);
                action.open(true);
                action.deleteHistory(record);
                action.close();
                recordList.remove(location);
                adapterRecord.notifyDataSetChanged();
                updateBookmarks();
                updateAutoComplete();
                hideBottomSheetDialog();
                NinjaToast.show(this, getString(R.string.toast_delete_successful));
            });
            MaterialButton actionCancel = ViewCompat.requireViewById(dialogView1, R.id.action_cancel);
            actionCancel.setOnClickListener(view -> hideBottomSheetDialog());
            bottomSheetDialog.setContentView(dialogView1);
            bottomSheetDialog.show();
        });

        contextListEdit = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_edit);
        contextListFav = ViewCompat.requireViewById(dialogView, R.id.menu_contextList_fav);

        if (overViewTab.equals(getString(R.string.album_title_bookmarks))) {
            contextListFav.setVisibility(View.VISIBLE);
            contextListEdit.setVisibility(View.VISIBLE);
        } else {
            contextListFav.setVisibility(View.GONE);
            contextListEdit.setVisibility(View.GONE);
        }

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void showFilterDialog() {
        hideBottomSheetDialog();

        bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = View.inflate(this, R.layout.dialog_edit_icon, null);

        LinearLayoutCompat icon01 = ViewCompat.requireViewById(dialogView, R.id.icon_01);
        icon01.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "01").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });
        LinearLayoutCompat icon02 = ViewCompat.requireViewById(dialogView, R.id.icon_02);
        icon02.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "02").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });
        LinearLayoutCompat icon03 = ViewCompat.requireViewById(dialogView, R.id.icon_03);
        icon03.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "03").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });
        LinearLayoutCompat icon04 = ViewCompat.requireViewById(dialogView, R.id.icon_04);
        icon04.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "04").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });
        LinearLayoutCompat icon05 = ViewCompat.requireViewById(dialogView, R.id.icon_05);
        icon05.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "05").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });
        LinearLayoutCompat icon06 = ViewCompat.requireViewById(dialogView, R.id.icon_06);
        icon06.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "06").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });
        LinearLayoutCompat icon07 = ViewCompat.requireViewById(dialogView, R.id.icon_07);
        icon07.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "07").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });
        LinearLayoutCompat icon08 = ViewCompat.requireViewById(dialogView, R.id.icon_08);
        icon08.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "08").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });
        LinearLayoutCompat icon09 = ViewCompat.requireViewById(dialogView, R.id.icon_09);
        icon09.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "09").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });
        LinearLayoutCompat icon10 = ViewCompat.requireViewById(dialogView, R.id.icon_10);
        icon10.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "10").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });
        LinearLayoutCompat icon11 = ViewCompat.requireViewById(dialogView, R.id.icon_11);
        icon11.setOnClickListener(v -> {
            sp.edit().putString("filter_passBY", "11").apply();
            initBookmarkList();
            hideBottomSheetDialog();
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void showHelpDialog() {
        hideBottomSheetDialog();
        bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = View.inflate(this, R.layout.dialog_help, null);

        AppCompatImageButton fab = ViewCompat.requireViewById(dialogView, R.id.floatButton_ok);
        fab.setOnClickListener(v -> hideBottomSheetDialog());

        AppCompatImageButton fabSettings = ViewCompat.requireViewById(dialogView, R.id.floatButton_settings);
        fabSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            hideBottomSheetDialog();
        });

        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void setCustomFullscreen(boolean fullscreen) {
        View decorView = getWindow().getDecorView();
        if (fullscreen) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    @NonNull
    private AlbumController nextAlbumController(boolean next) {
        if (BrowserContainer.size() <= 1) {
            if (currentAlbumController != null) {
                return currentAlbumController;
            } else {
                return new NinjaWebView(this);
            }
        }

        List<AlbumController> list = BrowserContainer.list();
        int index = list.indexOf(currentAlbumController);
        if (next) {
            if (++index >= list.size()) {
                index = 0;
            }
        } else {
            if (--index < 0) {
                index = list.size() - 1;
            }
        }

        return list.get(index);
    }

    // Classes
    private class VideoCompletionListener implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        private static final String TAG = "VideoCompletionListener";

        @Override
        public boolean onError(@NonNull MediaPlayer mp, int what, int extra) {
            Log.e(TAG, "onError called with " + what + " param");
            return false;
        }

        @Override
        public void onCompletion(@NonNull MediaPlayer mp) {
            onHideCustomView();
        }
    }
}
