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

package website.petrov.browser.browser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.os.MessageCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;

import website.petrov.browser.BuildConfig;
import website.petrov.browser.R;
import website.petrov.browser.database.Record;
import website.petrov.browser.database.RecordAction;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.unit.IntentUnit;
import website.petrov.browser.unit.LayoutUnit;
import website.petrov.browser.view.NinjaToast;
import website.petrov.browser.view.NinjaWebView;

import static android.content.ContentValues.TAG;

public class NinjaWebViewClient extends WebViewClient {
    @NonNull
    private final NinjaWebView ninjaWebView;
    @NonNull
    private final Context context;
    @NonNull
    private final SharedPreferences sp;

    @NonNull
    private final AdBlock adBlock;
    @NonNull
    private final Cookie cookie;

    private boolean white;
    private boolean enable;

    public NinjaWebViewClient(@NonNull NinjaWebView ninjaWebView) {
        super();
        this.ninjaWebView = ninjaWebView;
        this.context = ninjaWebView.getContext();
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.adBlock = ninjaWebView.getAdBlock();
        this.cookie = ninjaWebView.getCookieHosts();
        this.white = false;
        this.enable = true;
    }

    public void updateWhite(boolean white) {
        this.white = white;
    }

    public void enableAdBlock(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull String url) {
        final Uri uri = Uri.parse(url);
        return handleUri(view, uri);
    }

    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
        final Uri uri = request.getUrl();
        return handleUri(view, uri);
    }

    @Override
    public void onPageStarted(@NonNull WebView view, @NonNull String url, @NonNull Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        String title = view.getTitle();
        if (title == null || title.isEmpty()) {
            ninjaWebView.update(context.getString(R.string.album_untitled), url);
        } else {
            ninjaWebView.update(title, url);
        }
    }

    @Override
    public void onPageFinished(@NonNull WebView view, @NonNull String url) {
        super.onPageFinished(view, url);

        if (!ninjaWebView.getSettings().getLoadsImagesAutomatically()) {
            ninjaWebView.getSettings().setLoadsImagesAutomatically(true);
        }

        String title = view.getTitle();
        if (title == null || title.isEmpty()) {
            ninjaWebView.update(context.getString(R.string.album_untitled), url);
        } else {
            ninjaWebView.update(title, url);
        }

        if (sp.getBoolean("saveHistory", true)) {
            RecordAction action = new RecordAction(context);

            action.open(true);

            if (action.checkHistory(url)) {
                action.deleteHistoryOld(url);
                action.addHistory(new Record(ninjaWebView.getTitle(), ninjaWebView.getUrl(), System.currentTimeMillis()));
            } else {
                action.addHistory(new Record(ninjaWebView.getTitle(), ninjaWebView.getUrl(), System.currentTimeMillis()));
            }


            action.close();
        }

        if (ninjaWebView.isForeground()) {
            ninjaWebView.invalidate();
        } else {
            ninjaWebView.postInvalidate();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public WebResourceResponse shouldInterceptRequest(@NonNull WebView view, @NonNull String url) {
        if (enable && !white && adBlock.isAd(url)) {
            return new WebResourceResponse(
                    BrowserUnit.MIME_TYPE_TEXT_PLAIN,
                    BrowserUnit.URL_ENCODING,
                    new ByteArrayInputStream(new byte[0])
            );
        }

        if (!sp.getBoolean(context.getString(R.string.sp_cookies), true)) {
            if (cookie.isWhite(url)) {
                CookieManager manager = CookieManager.getInstance();
                manager.getCookie(url);
                manager.setAcceptCookie(true);
            } else {
                CookieManager manager = CookieManager.getInstance();
                manager.setAcceptCookie(false);
            }
        }

        return super.shouldInterceptRequest(view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(@NonNull WebView view, @NonNull WebResourceRequest request) {
        if (enable && !white && adBlock.isAd(request.getUrl().toString())) {
            return new WebResourceResponse(
                    BrowserUnit.MIME_TYPE_TEXT_PLAIN,
                    BrowserUnit.URL_ENCODING,
                    new ByteArrayInputStream("".getBytes())
            );
        }

        if (!sp.getBoolean(context.getString(R.string.sp_cookies), true)) {
            if (cookie.isWhite(request.getUrl().toString())) {
                CookieManager manager = CookieManager.getInstance();
                manager.getCookie(request.getUrl().toString());
                manager.setAcceptCookie(true);
            } else {
                CookieManager manager = CookieManager.getInstance();
                manager.setAcceptCookie(false);
            }
        }

        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onFormResubmission(@NonNull WebView view, @NonNull Message doNotResend, @NonNull Message resend) {
        Context holder = IntentUnit.getContext();
        if (!(holder instanceof AppCompatActivity)) {
            return;
        }

        MessageCompat.setAsynchronous(doNotResend, true);
        MessageCompat.setAsynchronous(resend, true);

        final BottomSheetDialog dialog = new BottomSheetDialog(holder);
        View dialogView = View.inflate(holder, R.layout.dialog_action, null);
        AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
        textView.setText(R.string.dialog_content_resubmission);
        MaterialButton actionOk = dialogView.findViewById(R.id.action_ok);
        actionOk.setOnClickListener(view1 -> {
            resend.sendToTarget();
            dialog.cancel();
        });
        MaterialButton actionCancel = dialogView.findViewById(R.id.action_cancel);
        actionCancel.setOnClickListener(view12 -> {
            doNotResend.sendToTarget();
            dialog.cancel();
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    @Override
    public void onReceivedSslError(@NonNull WebView view, @NonNull SslErrorHandler handler, @NonNull SslError error) {
        Context holder = IntentUnit.getContext();
        if (!(holder instanceof AppCompatActivity)) {
            return;
        }

        final BottomSheetDialog dialog = new BottomSheetDialog(holder);
        View dialogView = View.inflate(holder, R.layout.dialog_action, null);
        AppCompatTextView textView = dialogView.findViewById(R.id.dialog_text);
        textView.setText(R.string.dialog_content_ssl_error);
        MaterialButton actionOk = dialogView.findViewById(R.id.action_ok);
        actionOk.setOnClickListener(view1 -> {
            handler.proceed();
            dialog.cancel();
        });
        MaterialButton actionCancel = dialogView.findViewById(R.id.action_cancel);
        actionCancel.setOnClickListener(view12 -> {
            handler.cancel();
            dialog.cancel();
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    @Override
    public void onReceivedHttpAuthRequest(@NonNull WebView view, @NonNull HttpAuthHandler handler,
                                          @NonNull String host, @NonNull String realm) {
        Context holder = IntentUnit.getContext();
        if (!(holder instanceof AppCompatActivity)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(holder, R.style.AppTheme_AlertDialog);
        View dialogView = View.inflate(holder, R.layout.dialog_edit_bookmark, null);

        final AppCompatEditText passUserNameET = dialogView.findViewById(R.id.pass_userName);
        final AppCompatEditText passUserPWET = dialogView.findViewById(R.id.pass_userPW);
        TextInputLayout loginTitle = dialogView.findViewById(R.id.login_title);
        loginTitle.setVisibility(View.GONE);

        builder.setView(dialogView);
        builder.setTitle(R.string.dialog_title_sign_in);
        builder.setPositiveButton(R.string.app_ok, (dialog, whichButton) -> {
            String user = LayoutUnit.getText(passUserNameET);
            String pass = LayoutUnit.getText(passUserPWET);
            handler.proceed(user, pass);
            dialog.cancel();
        });
        builder.setNegativeButton(R.string.app_cancel, (dialog, whichButton) -> {
            handler.cancel();
            dialog.cancel();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean handleUri(@NonNull WebView webView, @NonNull Uri uri) {
        boolean result = true;

        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Uri =" + uri);
        }

        final String url = uri.toString();

        // Based on some condition you need to determine if you are going to load the url
        // in your web view itself or in a browser.
        // You can use `host` or `scheme` or any part of the `uri` to decide.
        // open web links as usual
        if (url.startsWith("http")) {
            webView.loadUrl(url, ninjaWebView.getRequestHeaders());
            return true;
        }

        //try to find browse activity to handle uri
        Uri parsedUri = Uri.parse(url);
        PackageManager packageManager = context.getPackageManager();
        Intent browseIntent = new Intent(Intent.ACTION_VIEW).setData(parsedUri);
        if (browseIntent.resolveActivity(packageManager) != null) {
            context.startActivity(browseIntent);
            return true;
        }

        //if not activity found, try to parse intent://
        if (url.startsWith("intent:")) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    try {
                        context.startActivity(intent);
                    } catch (Throwable e) {
                        NinjaToast.show(context, R.string.toast_load_error);
                        result = false;
                    }
                    return result;
                }

                //try to find fallback url
                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                if (fallbackUrl != null) {
                    webView.loadUrl(fallbackUrl);
                    return true;
                }

                //invite to install
                Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + intent.getPackage()));
                if (marketIntent.resolveActivity(packageManager) != null) {
                    context.startActivity(marketIntent);
                    return true;
                } else {
                    result = false;
                }
            } catch (URISyntaxException e) {
                result = false;
            }
        }
        white = adBlock.isWhite(url);
        return result;//do nothing in other cases
    }
}
