package com.forever.whatsappstatussaver.Adapters;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public SpacingItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        // Set spacing for all sides
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first row to avoid double spacing
        if (position < ((StaggeredGridLayoutManager) parent.getLayoutManager()).getSpanCount()) {
            outRect.top = space;
        }
    }
}
