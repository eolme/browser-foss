package website.petrov.browser.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemState;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;

import website.petrov.browser.R;
import website.petrov.browser.unit.BrowserUnit;
import website.petrov.browser.unit.HelperUnit;

public class GridAdapter extends RecyclerView.Adapter<GridHolder>
        implements DraggableItemAdapter<GridHolder> {

    private final GridProvider mProvider;
    private boolean mCanEdit = false;

    public GridAdapter(@NonNull GridProvider dataProvider) {
        mProvider = dataProvider;
        setHasStableIds(true);
    }

    @NonNull
    public GridProvider getProvider() {
        return mProvider;
    }

    @NonNull
    @Override
    public GridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.grid_item, parent, false);
        return new GridHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GridHolder holder, int position) {
        final GridItem item = mProvider.getItem(position);

        // set text
        holder.title.setText(item.getTitle());
        holder.cover.setImageBitmap(BrowserUnit.file2Bitmap(holder.container.getContext(), item.getFilename()));
        holder.container.setOnClickListener(v -> {
            if (!mCanEdit) {
                mProvider.callOnClick(position);
            }
        });
        holder.container.setOnLongClickListener(v -> {
            if (!mCanEdit) {
                mProvider.callOnLongClick(position);
            }
            return true;
        });

        // set background resource (target view ID: container)
        final DraggableItemState dragState = holder.getDragState();

//        int bgResId;

        if (dragState.isUpdated()) {
//            bgResId = R.drawable.bg_item_dragging_active_state;

            // need to clear drawable state here to get correct appearance of the dragging item.
            HelperUnit.clearState(holder.container.getForeground());
        }
//        else if (dragState.isDragging()) {
//            bgResId = R.drawable.bg_item_dragging_state;
//        } else {
//            bgResId = R.drawable.bg_item_normal_state;
//        }
//
//        holder.container.setBackgroundResource(bgResId);
    }

    @Override
    @LayoutRes
    public int getItemViewType(int position) {
        return R.layout.grid_item;
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getOrdinal();
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    public void startEditMode() {
        mCanEdit = true;
    }

    public void stopEditMode() {
        mCanEdit = false;
    }

    @Override
    public boolean onCheckCanStartDrag(@NonNull GridHolder holder, int position, int x, int y) {
        return mCanEdit;
    }

    @Nullable
    @Override
    public ItemDraggableRange onGetItemDraggableRange(@NonNull GridHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        mProvider.moveItem(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }
}
