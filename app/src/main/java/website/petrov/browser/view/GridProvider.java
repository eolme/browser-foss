package website.petrov.browser.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GridProvider {
    @NonNull
    private final List<GridItem> items;
    @Nullable
    private final OnItemClickListener clickItemListener;

    public GridProvider(@NonNull List<GridItem> items,
                        @Nullable OnItemClickListener clickItemListener) {
        this.items = items;
        this.clickItemListener = clickItemListener;
    }

    public List<GridItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void callOnClick(int position) {
        if (clickItemListener != null) {
            clickItemListener.onClick(getItem(position));
        }
    }

    public void callOnLongClick(int position) {
        if (clickItemListener != null) {
            clickItemListener.onLongClick(getItem(position));
        }
    }

    public int getCount() {
        return items.size();
    }

    @NonNull
    public GridItem getItem(int index) {
        return items.get(index);
    }

    public void removeItem(int position) {
        Cache.savedItem = items.get(position);
        Cache.savedPosition = position;

        items.remove(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        if (fromPosition > toPosition) {
            for (int i = fromPosition; i > toPosition; ) {
                swapItem(i, --i);
            }
        } else {
            for (int i = fromPosition; i < toPosition; ) {
                swapItem(i, ++i);
            }
        }
    }

    public void swapItem(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
    }

    public int undoLastRemoval() {
        items.add(Cache.savedPosition, Cache.savedItem);
        return Cache.savedPosition;
    }

    public interface OnItemClickListener {
        void onClick(@NonNull GridItem item);

        void onLongClick(@NonNull GridItem item);
    }

    private static class Cache {
        @Nullable
        static GridItem savedItem;
        static int savedPosition;
    }
}
