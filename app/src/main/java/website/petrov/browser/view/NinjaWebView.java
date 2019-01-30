package website.petrov.browser.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.os.MessageCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import java.net.URISyntaxException;
import java.util.HashMap;

import website.petrov.browser.R;
import website.petrov.browser.browser.AdBlock;
import website.petrov.browser.browser.AlbumController;
import website.petrov.browser.browser.BrowserController;
import website.petrov.browser.browser.Cookie;
import website.petrov.browser.browser.Javascript;
import website.petrov.browser.browser.NinjaClickHandler;
import website.petrov.browser.browser.NinjaDownloadListener;
import website.petrov.browser.browser.NinjaGestureListener;
import website.petrov.browser.browser.NinjaWebChromeClient;
import website.petrov.browser.browser.NinjaWebViewClient;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.unit.HelperUnit;
import website.petrov.browser.unit.IntentUnit;
import website.petrov.browser.unit.ViewUnit;

public class NinjaWebView extends WebView implements AlbumController {
    private static final String TAG = "NinjaWebView";

    @NonNull
    private final Context context;
    @NonNull
    private final GestureDetectorCompat gestureDetector;
    private final int dimen144dp;
    private final int dimen108dp;
    private final int animTime;

    @NonNull
    private final Album album;
    @NonNull
    private final NinjaWebViewClient webViewClient;
    @NonNull
    private final NinjaWebChromeClient webChromeClient;
    @NonNull
    private final NinjaDownloadListener downloadListener;
    @NonNull
    private final NinjaClickHandler clickHandler;

    @NonNull
    private final AdBlock adBlock;
    @NonNull
    private final Javascript javaHosts;
    @NonNull
    private final Cookie cookieHosts;
    @NonNull
    private final SharedPreferences sp;
    @NonNull
    private WebSettings webSettings;

    private boolean foreground;

    @Nullable
    private OnScrollChangeListener onScrollChangeListener;
    @Nullable
    private BrowserController browserController = null;

    public NinjaWebView(@NonNull Context context) {
        super(context); // Cannot create a dialog, the WebView context is not an Activity

        this.context = context;
        this.dimen144dp = getResources().getDimensionPixelSize(R.dimen.layout_width_144dp);
        this.dimen108dp = getResources().getDimensionPixelSize(R.dimen.layout_height_108dp);
        this.animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        this.foreground = false;

        this.adBlock = new AdBlock(this.context);
        this.javaHosts = new Javascript(this.context);
        this.cookieHosts = new Cookie(this.context);
        this.webViewClient = new NinjaWebViewClient(this);
        this.webChromeClient = new NinjaWebChromeClient(this);
        this.downloadListener = new NinjaDownloadListener(this.context);
        this.clickHandler = new NinjaClickHandler(this);
        this.gestureDetector = new GestureDetectorCompat(context, new NinjaGestureListener(this));
        this.album = new Album(this.context, this,
                context instanceof BrowserController ? (BrowserController) context : null);
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.webSettings = getSettings();

        initWebView();
        initWebSettings();
        initPreferences();
        initAlbum();
    }

    @NonNull
    public AdBlock getAdBlock() {
        return adBlock;
    }

    @NonNull
    public Cookie getCookieHosts() {
        return cookieHosts;
    }

    public boolean isForeground() {
        return foreground;
    }

    @Nullable
    public BrowserController getBrowserController() {
        return browserController;
    }

    public void setBrowserController(@NonNull BrowserController browserController) {
        this.browserController = browserController;
    }

    public void setOnScrollChangeListener(@Nullable OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    private synchronized void initWebView() {
        setWebViewClient(webViewClient);
        setWebChromeClient(webChromeClient);
        setDownloadListener(downloadListener);
        setOnTouchListener((view, motionEvent) -> {
            gestureDetector.onTouchEvent(motionEvent);
            return false;
        });
    }

    private synchronized void initWebSettings() {
        webSettings.setAppCachePath(context.getCacheDir().getAbsolutePath());

        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);

        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);

        if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {
            WebSettingsCompat.setSafeBrowsingEnabled(webSettings, true);
        }
    }

    public synchronized void initPreferences() {
        webViewClient.enableAdBlock(sp.getBoolean(context.getString(R.string.sp_ad_block), true));

        webSettings.setTextZoom(Integer.parseInt(HelperUnit.safeGetString(sp, "sp_fontSize", "100")));

        webSettings.setAllowFileAccessFromFileURLs(sp.getBoolean(("sp_remote"), true));
        webSettings.setAllowUniversalAccessFromFileURLs(sp.getBoolean(("sp_remote"), true));

        webSettings.setBlockNetworkImage(!sp.getBoolean(context.getString(R.string.sp_images), true));
        webSettings.setJavaScriptEnabled(sp.getBoolean(context.getString(R.string.sp_javascript), true));
        webSettings.setJavaScriptCanOpenWindowsAutomatically(sp.getBoolean(context.getString(R.string.sp_javascript), true));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (sp.getBoolean(("sp_remote"), true)) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            }
        }

        if (sp.getBoolean(context.getString(R.string.sp_location), true)) {
            int hasACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                AppCompatActivity activity = (AppCompatActivity) context;
                HelperUnit.grantPermissionsLoc(activity);
            } else {
                webSettings.setGeolocationEnabled(sp.getBoolean(context.getString(R.string.sp_location), true));
            }
        }

        CookieManager manager = CookieManager.getInstance();
        manager.setAcceptCookie(sp.getBoolean(context.getString(R.string.sp_cookies), true));
    }

    private synchronized void initAlbum() {
        album.setAlbumCover(null);
        album.setAlbumTitle(context.getString(R.string.album_untitled));
    }

    public synchronized HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("DNT", "1");
        if (sp.getBoolean(context.getString(R.string.sp_savedata), false)) {
            requestHeaders.put("Save-Data", "on");
        }
        return requestHeaders;
    }

    @Override
    @NonNull
    public View getAlbumView() {
        return album.getAlbumView();
    }

    @Override
    public void setAlbumCover(@Nullable Bitmap bitmap) {
        album.setAlbumCover(bitmap);
    }

    @Override
    @NonNull
    public String getAlbumTitle() {
        return album.getAlbumTitle();
    }

    public void setAlbumTitle(@Nullable String title) {
        album.setAlbumTitle(title);
    }

    @Override
    public synchronized void activate() {
        requestFocus();
        foreground = true;
        album.activate();
    }

    @Override
    public synchronized void deactivate() {
        clearFocus();
        foreground = false;
        album.deactivate();
    }

    public synchronized void update(int progress) {
        if (browserController == null) {
            return;
        }

        if (foreground) {
            browserController.updateProgress(progress);
        }

        setAlbumCover(ViewUnit.capture(this, dimen144dp, dimen108dp, Bitmap.Config.RGB_565));
        if (isLoadFinish()) {
            new Handler().postDelayed(() -> setAlbumCover(ViewUnit.capture(NinjaWebView.this,
                    dimen144dp, dimen108dp, Bitmap.Config.RGB_565)), animTime);

            if (prepareRecord()) {
                browserController.updateAutoComplete();
            }
        }
    }

    public synchronized void update(@NonNull String title, @NonNull String url) {
        if (browserController == null) {
            return;
        }

        album.setAlbumTitle(title);
        if (foreground) {
            browserController.updateBookmarks();
            browserController.updateInputBox(url);
        }

        try {
            AppCompatTextView omniTitle = this.getRootView().findViewById(R.id.omnibox_title);
            omniTitle.setText(NinjaWebView.this.getTitle());
        } catch (Throwable e) {
            Log.e(TAG, "Error updating");
        }
    }

    @Override
    public synchronized void destroy() {
        stopLoading();
        onPause();
        clearHistory();
        setVisibility(GONE);
        removeAllViews();
        super.destroy();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public synchronized void loadUrl(@Nullable String url) {
        if (url == null || url.trim().isEmpty()) {
            NinjaToast.show(context, R.string.toast_load_error);
            return;
        }

        if (!url.contains("://")) {
            url = BrowserUnit.queryWrapper(context, url.trim());
        }

        if (url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)) {
            Intent intent = IntentUnit.getEmailIntent(MailTo.parse(url));
            context.startActivity(intent);
            reload();
            return;
        } else if (url.startsWith(BrowserUnit.URL_SCHEME_INTENT)) {
            Intent intent;
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                context.startActivity(intent);
            } catch (URISyntaxException u) {
                Log.e(TAG, "Error parsing URL");
            }
            return;
        }

        if (!sp.getBoolean(context.getString(R.string.sp_javascript), true)) {
            webSettings = getSettings();
            if (javaHosts.isWhite(url)) {
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webSettings.setJavaScriptEnabled(true);
            } else {
                webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
                webSettings.setJavaScriptEnabled(false);
            }
        }

        if (!sp.getBoolean(context.getString(R.string.sp_cookies), true)) {
            CookieManager manager = CookieManager.getInstance();
            if (cookieHosts.isWhite(url)) {
                manager.getCookie(url);
                manager.setAcceptCookie(true);
            } else {
                manager.setAcceptCookie(false);
            }
        }

        webViewClient.updateWhite(adBlock.isWhite(url));
        super.loadUrl(url, getRequestHeaders());

        if (browserController != null && foreground) {
            browserController.updateBookmarks();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void reload() {
        webViewClient.updateWhite(adBlock.isWhite(getUrl()));

        if (!sp.getBoolean(context.getString(R.string.sp_javascript), true)) {
            webSettings = getSettings();
            if (javaHosts.isWhite(getUrl())) {
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webSettings.setJavaScriptEnabled(true);
            } else {
                webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
                webSettings.setJavaScriptEnabled(false);
            }
        }

        if (!sp.getBoolean(context.getString(R.string.sp_cookies), true)) {
            CookieManager manager = CookieManager.getInstance();
            if (cookieHosts.isWhite(getUrl())) {
                manager.getCookie(getUrl());
                manager.setAcceptCookie(true);
            } else {
                manager.setAcceptCookie(false);
            }
        }

        super.reload();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldL, int oldT) {
        super.onScrollChanged(l, t, oldL, oldT);
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChange(t, oldT);
        }
    }

    public boolean isLoadFinish() {
        return getProgress() >= BrowserUnit.PROGRESS_MAX;
    }

    public void onLongPress() {
        Message click = clickHandler.obtainMessage();
        if (click != null) {
            MessageCompat.setAsynchronous(click, true);
            click.setTarget(clickHandler);
        }
        requestFocusNodeHref(click);
    }

    private boolean prepareRecord() {
        String title = getTitle();
        String url = getUrl();

        return !(title == null
                || title.isEmpty()
                || url == null
                || url.isEmpty()
                || url.startsWith(BrowserUnit.URL_SCHEME_ABOUT)
                || url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)
                || url.startsWith(BrowserUnit.URL_SCHEME_INTENT));
    }

    public interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param scrollY    Current vertical scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        void onScrollChange(int scrollY, int oldScrollY);
    }
}
