package de.baumann.browser.Browser;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.os.MessageCompat;

import de.baumann.browser.View.NinjaWebView;

public class NinjaClickHandler extends Handler {
    @NonNull
    private final NinjaWebView webView;

    public NinjaClickHandler(@NonNull NinjaWebView webView) {
        super();
        this.webView = webView;
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        MessageCompat.setAsynchronous(message, true);
        super.handleMessage(message);
        webView.getBrowserController().onLongPress(message.getData().getString("url"));
    }
}
