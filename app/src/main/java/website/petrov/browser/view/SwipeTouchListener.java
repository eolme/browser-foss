package website.petrov.browser.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;

import org.jetbrains.annotations.Contract;

public class SwipeTouchListener implements OnTouchListener {
    private static final String TAG = "SwipeTouchListener";

    private final GestureDetectorCompat gestureDetector;

    protected SwipeTouchListener(@NonNull Context ctx) {
        gestureDetector = new GestureDetectorCompat(ctx, new GestureListener());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(@NonNull View v, @NonNull MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    // ↓ do not remove, needed for swipe listener of the "navigation button"
    protected void onSwipeRight() {
    }

    protected void onSwipeLeft() {
    }

    protected void onSwipeTop() {
    }

    protected void onSwipeBottom() {
    }

    private final class GestureListener implements GestureDetector.OnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Contract(pure = true)
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(@NonNull MotionEvent e) {
        }

        @Contract(pure = true)
        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            return false;
        }

        @Contract(pure = true)
        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2,
                                float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
        }

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2,
                               float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;
            } catch (Throwable e) {
                Log.e(TAG, "Fling", e);
            }
            return result;
        }
    }
}
