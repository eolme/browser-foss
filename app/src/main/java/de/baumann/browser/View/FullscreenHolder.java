package de.baumann.browser.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class FullscreenHolder extends FrameLayout {
    public FullscreenHolder(@NonNull Context context) {
        super(context);
        this.setBackgroundColor(ContextCompat.getColor(context,(android.R.color.black)));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return true;
    }
}
