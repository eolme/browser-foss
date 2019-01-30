package website.petrov.browser.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

import com.google.android.material.card.MaterialCardView;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import website.petrov.browser.R;

@SuppressWarnings("WeakerAccess")
public class GridHolder extends AbstractDraggableItemViewHolder {
    public final AppCompatTextView title;
    public final AppCompatImageView cover;
    public final MaterialCardView container;

    public GridHolder(@NonNull View v) {
        super(v);
        container = ViewCompat.requireViewById(v, R.id.grid_item_container);
        title = ViewCompat.requireViewById(container, R.id.grid_item_title);
        cover = ViewCompat.requireViewById(container, R.id.grid_item_cover);
    }
}