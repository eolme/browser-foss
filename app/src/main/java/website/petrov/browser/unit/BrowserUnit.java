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

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import website.petrov.browser.R;
import website.petrov.browser.browser.AdBlock;
import website.petrov.browser.browser.Cookie;
import website.petrov.browser.browser.Javascript;
import website.petrov.browser.database.RecordAction;
import website.petrov.browser.view.NinjaToast;

public class BrowserUnit {
    public static final String APP_NAME = "Suze";
    public static final int PROGRESS_MAX = 100;
    public static final String SUFFIX_PNG = ".png";
    public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    public static final String URL_ENCODING = "UTF-8";
    public static final String URL_SCHEME_ABOUT = "about:";
    public static final String URL_SCHEME_MAIL_TO = "mailto:";
    public static final String URL_SCHEME_INTENT = "intent://";
    private static final String TAG = "BrowserUnit";
    private static final String SUFFIX_TXT = ".txt";
    private static final String SEARCH_ENGINE_GOOGLE = "https://www.google.com/search?q=";
    private static final String SEARCH_ENGINE_DUCKDUCKGO = "https://duckduckgo.com/?q=";
    private static final String SEARCH_ENGINE_BAIDU = "https://www.baidu.com/s?wd=";
    private static final String SEARCH_ENGINE_YANDEX = "https://yandex.com/search/touch/?text=";
    private static final String SEARCH_ENGINE_MAILRU = "https://go.mail.ru/msearch?q=";
    private static final String URL_ABOUT_BLANK = "about:blank";
    private static final String URL_SCHEME_FILE = "file://";
    private static final String URL_SCHEME_HTTP = "https://";
    private static final String URL_PREFIX_GOOGLE_PLAY = "www.google.com/url?q=";
    private static final String URL_SUFFIX_GOOGLE_PLAY = "&sa";
    private static final String URL_PREFIX_GOOGLE_PLUS = "plus.url.google.com/url?q=";
    private static final String URL_SUFFIX_GOOGLE_PLUS = "&rct";

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^((ftp|http|https|intent)?://)"                             // support scheme
                    + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                    + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"                            // IP形式的URL -> 199.194.52.184
                    + "|"                                                        // 允许IP和DOMAIN（域名）
                    + "(.)*"                                                     // 域名 -> www.
                    + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."                    // 二级域名
                    + "[a-z]{2,24})"                                              // first level domain -> .com or .museum
                    + "(:[0-9]{1,4})?"                                           // 端口 -> :80
                    + "((/?)|"                                                   // a slash isn't required if there is no file name
                    + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$");

    @Contract("null -> false")
    public static boolean isURL(@Nullable String url) {
        if (url == null) {
            return false;
        }

        url = url.toLowerCase(Locale.getDefault());
        if (url.startsWith(URL_ABOUT_BLANK)
                || url.startsWith(URL_SCHEME_MAIL_TO)
                || url.startsWith(URL_SCHEME_FILE)) {
            return true;
        }

        return URL_PATTERN.matcher(url).matches();
    }

    @NonNull
    public static String queryWrapper(@NonNull Context context, @NotNull String query) {
        // Use prefix and suffix to process some special links
        String temp = query.toLowerCase(Locale.getDefault());
        if (temp.contains(URL_PREFIX_GOOGLE_PLAY) && temp.contains(URL_SUFFIX_GOOGLE_PLAY)) {
            int start = temp.indexOf(URL_PREFIX_GOOGLE_PLAY) + URL_PREFIX_GOOGLE_PLAY.length();
            int end = temp.indexOf(URL_SUFFIX_GOOGLE_PLAY);
            query = query.substring(start, end);
        } else if (temp.contains(URL_PREFIX_GOOGLE_PLUS) && temp.contains(URL_SUFFIX_GOOGLE_PLUS)) {
            int start = temp.indexOf(URL_PREFIX_GOOGLE_PLUS) + URL_PREFIX_GOOGLE_PLUS.length();
            int end = temp.indexOf(URL_SUFFIX_GOOGLE_PLUS);
            query = query.substring(start, end);
        }

        if (isURL(query)) {
            if (query.startsWith(URL_SCHEME_ABOUT) || query.startsWith(URL_SCHEME_MAIL_TO)) {
                return query;
            }

            if (!query.contains("://")) {
                query = URL_SCHEME_HTTP + query;
            }

            return query;
        }

        try {
            query = URLEncoder.encode(query, URL_ENCODING);
        } catch (UnsupportedEncodingException u) {
            Log.e(TAG, "Unsupported Encoding Exception", u);
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String custom = HelperUnit.safeGetString(sp, context.getString(R.string.sp_search_engine_custom), SEARCH_ENGINE_GOOGLE);
        final int i = Integer.valueOf(HelperUnit.safeGetString(sp, context.getString(R.string.sp_search_engine), "0"));
        switch (i) {
            case 0:
                return SEARCH_ENGINE_GOOGLE + query;
            case 1:
                return SEARCH_ENGINE_YANDEX + query;
            case 2:
                return SEARCH_ENGINE_BAIDU + query;
            case 3:
                return SEARCH_ENGINE_DUCKDUCKGO + query;
            case 4:
                return SEARCH_ENGINE_MAILRU + query;
            case 5:
                return custom + query;
            default:
                return SEARCH_ENGINE_GOOGLE + query;
        }
    }

    public static boolean bitmap2File(@NonNull Context context, @NonNull Bitmap bitmap,
                                      @NonNull String filename) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Contract("_, null -> null")
    @Nullable
    public static Bitmap file2Bitmap(@NonNull Context context, @Nullable String filename) {
        if (filename == null) {
            return null;
        }

        Bitmap result;
        try {
            FileInputStream fileInputStream = context.openFileInput(filename);
            result = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            result = null;
        }
        return result;
    }

    public static void download(@Nullable Context context, @NonNull String url,
                                @NonNull String contentDisposition, @NonNull String mimeType) {
        if (context == null) {
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String filename = URLUtil.guessFileName(url, contentDisposition, mimeType); // Maybe unexpected filename.

        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie(url);

        request.addRequestHeader("Cookie", cookie);
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(filename);
        request.setMimeType(mimeType);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        DownloadManager manager = ContextCompat.getSystemService(context, DownloadManager.class);
        if (manager == null) {
            NinjaToast.show(context, R.string.toast_error);
            return;
        }

        int HAS_WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (HAS_WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            NinjaToast.show(context, R.string.toast_permission_sdCard_sec);
        } else {
            manager.enqueue(request);
            NinjaToast.show(context, R.string.toast_start_download);
        }
    }

    @Contract("_, null, _ -> null")
    @Nullable
    public static String screenshot(@NonNull Context context, @Nullable Bitmap bitmap,
                                    @Nullable String name) {
        if (bitmap == null) {
            return null;
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (name == null || name.trim().isEmpty()) {
            name = String.valueOf(System.currentTimeMillis());
        }
        name = name.trim();

        int count = 0;
        File file = new File(dir, name + SUFFIX_PNG);
        while (file.exists()) {
            count++;
            file = new File(dir, name + "_" + count + SUFFIX_PNG);
        }

        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString("screenshot_path", file.getPath()).apply();

            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "screenshot", e);
            return null;
        }
    }

    public static void exportBookmarks(@NonNull Context context) {
        String filename = context.getString(R.string.export_bookmarks);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BrowserUnit.APP_NAME + "//" + filename + SUFFIX_TXT);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String savedKey = HelperUnit.safeGetString(sp, "saved_key", "no");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(savedKey);
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, "Error adding record", e);
        }
    }

    public static void importBookmarks(@NonNull Context context) {
        String filename = context.getString(R.string.export_bookmarks);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BrowserUnit.APP_NAME + "//" + filename + SUFFIX_TXT);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                sp.edit().putString("saved_key", line).apply();
            }
            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error adding record", e);
        }
    }

    @Nullable
    public static String exportWhitelist(@NonNull Context context, int i) {
        RecordAction action = new RecordAction(context);
        List<String> list;
        String filename;

        action.open(false);

        switch (i) {
            case 0:
                list = action.listDomains();
                filename = context.getString(R.string.export_whitelistAdBlock);
                break;
            case 1:
                list = action.listDomainsJS();
                filename = context.getString(R.string.export_whitelistJS);
                break;
            default:
                list = action.listDomainsCookie();
                filename = context.getString(R.string.export_whitelistCookie);
                break;
        }

        action.close();

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BrowserUnit.APP_NAME + "//" + filename + SUFFIX_TXT);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            for (String domain : list) {
                writer.write(domain);
                writer.newLine();
            }
            writer.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            return null;
        }
    }

    public static int importWhitelist(@NonNull Context context) {
        String filename = context.getString(R.string.export_whitelistAdBlock);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BrowserUnit.APP_NAME + "//" + filename + SUFFIX_TXT);

        AdBlock adBlock = new AdBlock(context);
        int count = 0;

        try {
            RecordAction action = new RecordAction(context);
            action.open(true);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!action.checkDomain(line)) {
                    adBlock.addDomain(line);
                    count++;
                }
            }
            reader.close();
            action.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading file", e);
        }

        return count;
    }

    public static int importWhitelistJS(@NonNull Context context) {
        String filename = context.getString(R.string.export_whitelistJS);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BrowserUnit.APP_NAME + "//" + filename + SUFFIX_TXT);

        Javascript js = new Javascript(context);
        int count = 0;

        try {
            RecordAction action = new RecordAction(context);
            action.open(true);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!action.checkDomainJS(line)) {
                    js.addDomain(line);
                    count++;
                }
            }
            reader.close();
            action.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading file", e);
        }

        return count;
    }

    public static int importWhitelistCookie(@NonNull Context context) {
        String filename = context.getString(R.string.export_whitelistCookie);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BrowserUnit.APP_NAME + "//" + filename + SUFFIX_TXT);

        Cookie cookie = new Cookie(context);
        int count = 0;

        try {
            RecordAction action = new RecordAction(context);
            action.open(true);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!action.checkDomainCookie(line)) {
                    cookie.addDomain(line);
                    count++;
                }
            }
            reader.close();
            action.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading file", e);
        }

        return count;
    }

    public static void clearHome(@NonNull Context context) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearHome();
        action.close();
    }

    public static void clearCache(@NonNull Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Throwable e) {
            Log.e(TAG, "Error clearing cache", e);
        }
    }

    // CookieManager.removeAllCookies() must be called on a thread with a running Looper.
    public static void clearCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.flush();
        cookieManager.removeAllCookies(value -> {
        });
    }

    public static void clearHistory(@NonNull Context context) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearHistory();
        action.close();
    }

    public static void clearIndexedDB(@NonNull Context context) {
        File data = Environment.getDataDirectory();

        String indexedDB = "//data//" + context.getPackageName() + "//app_webview//" + "//IndexedDB";
        String localStorage = "//data//" + context.getPackageName() + "//app_webview//" + "//Local Storage";

        final File indexedDB_dir = new File(data, indexedDB);
        final File localStorage_dir = new File(data, localStorage);

        BrowserUnit.deleteDir(indexedDB_dir);
        BrowserUnit.deleteDir(localStorage_dir);
    }

    @Contract("null -> false")
    public static boolean deleteDir(@Nullable File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    @NonNull
    public static String safeGetHost(@Nullable String url) {
        if (url == null) {
            return "";
        }

        String host = Uri.parse(url).getHost();

        if (host == null || host.isEmpty()) {
            Matcher urlParse = URL_PATTERN.matcher(host);
            if (urlParse.matches() && urlParse.groupCount() >= 5) {
                host = urlParse.group(5);
            } else {
                host = "";
            }
        }

        return host.replace("www.", "").trim();
    }
}
