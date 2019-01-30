/*
 * Open source android application.
 *
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
