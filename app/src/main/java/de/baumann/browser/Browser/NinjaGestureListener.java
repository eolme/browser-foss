package de.baumann.browser.Browser;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import de.baumann.browser.View.NinjaWebView;

public class NinjaGestureListener extends GestureDetector.SimpleOnGestureListener {
    @NonNull
    private final NinjaWebView webView;
    private boolean longPress = true;

    public NinjaGestureListener(@NonNull NinjaWebView webView) {
        super();
        this.webView = webView;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        if (longPress) {
            webView.onLongPress();
        }
    }

    @Override
    public boolean onDoubleTapEvent(@NonNull MotionEvent e) {
        longPress = false;
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {
        longPress = true;
    }
}
