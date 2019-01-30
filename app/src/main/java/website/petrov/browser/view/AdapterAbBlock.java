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
import website.petrov.browser.browser.AdBlock;

public class AdapterAbBlock extends ArrayAdapter<String> {
    @NonNull
    private final Context context;
    @LayoutRes
    private final int layoutResId;
    @NonNull
    private final List<String> list;

    public AdapterAbBlock(@NonNull Context context, @NonNull List<String> list) {
        super(context, R.layout.whitelist_item, list);
        this.context = context;
        this.layoutResId = R.layout.whitelist_item;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
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
            AdBlock adBlock = new AdBlock(context);
            adBlock.removeDomain(list.get(position));
            list.remove(position);
            notifyDataSetChanged();
            NinjaToast.show(context, R.string.toast_delete_successful);
        });

        return view;
    }
}
