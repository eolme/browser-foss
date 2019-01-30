/*
 * Open source android application.
 *
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

package website.petrov.browser.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import website.petrov.browser.R;
import website.petrov.browser.browser.Cookie;

public class AdapterCookie extends ArrayAdapter<String> {
    @NonNull
    private final Context context;
    @LayoutRes
    private final int layoutResId;
    @NonNull
    private final List<String> list;

    public AdapterCookie(@NonNull Context context, @NonNull List<String> list) {
        super(context, R.layout.whitelist_item, list);
        this.context = context;
        this.layoutResId = R.layout.whitelist_item;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UniversalHolder holder;
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
            holder = new UniversalHolder();
            holder.text = view.findViewById(R.id.whitelist_item_domain);
            holder.button = view.findViewById(R.id.whitelist_item_cancel);
            view.setTag(holder);
        } else {
            holder = (UniversalHolder) view.getTag();
        }

        holder.text.setText(list.get(position));
        holder.button.setOnClickListener(v -> {
            Cookie cookie = new Cookie(context);
            cookie.removeDomain(list.get(position));
            list.remove(position);
            notifyDataSetChanged();
            NinjaToast.show(context, R.string.toast_delete_successful);
        });

        return view;
    }
}
