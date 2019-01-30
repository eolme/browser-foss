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

package website.petrov.browser.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import website.petrov.browser.R;
import website.petrov.browser.database.Record;

public class AdapterRecord extends ArrayAdapter<Record> {
    @NonNull
    private final Context context;
    @LayoutRes
    private final int layoutResId;
    @NonNull
    private final List<Record> list;

    public AdapterRecord(@NonNull Context context, @NonNull List<Record> list) {
        super(context, R.layout.list_item, list);
        this.context = context;
        this.layoutResId = R.layout.list_item;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RecordHolder holder;
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
            holder = new RecordHolder();
            holder.title = view.findViewById(R.id.record_item_title);
            holder.time = view.findViewById(R.id.record_item_time);
            holder.url = view.findViewById(R.id.record_item_url);
            view.setTag(holder);
        } else {
            holder = (RecordHolder) view.getTag();
        }

        Record record = list.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        holder.title.setText(record.getTitle());
        holder.time.setText(sdf.format(record.getTime()));
        holder.url.setText(record.getURL());

        return view;
    }

    private static class RecordHolder {
        AppCompatTextView title;
        AppCompatTextView time;
        AppCompatTextView url;
    }
}