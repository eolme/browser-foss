package website.petrov.browser.browser;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.os.MessageCompat;

import website.petrov.browser.view.NinjaWebView;

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

        BrowserController controller = webView.getBrowserController();
        if (controller != null) {
            controller.onLongPress(message.getData().getString("url"));
        }
    }
}
