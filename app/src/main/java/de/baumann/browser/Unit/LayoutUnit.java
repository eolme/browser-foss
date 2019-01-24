package de.baumann.browser.Unit;

import android.widget.TextView;

import androidx.annotation.NonNull;

public class LayoutUnit {

    /**
     * Return the text that the view is displaying. If an text has not been set yet, this
     * will return empty string.
     *
     * @param textView the view to take the text from it
     * @return text from `textView`
     * @see TextView#getText()
     */
    @NonNull
    public static <V extends TextView> String getText(@NonNull V textView) {
        final CharSequence text = textView.getText();
        return text == null ? "" : text.toString().trim();
    }

    /**
     * Return empty state of TextView
     *
     * @param textView the view to take the text from it
     * @return state
     * @see #getText(V)
     */
    public static <V extends TextView> boolean isEmpty(@NonNull V textView) {
        return getText(textView).isEmpty();
    }
}
