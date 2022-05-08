package com.north.light.libpicselect.widget.ItemDecoration;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;


/**
 * create by lzt
 * data 2019/12/8
 */
public class LibPicDialogItemDecoration extends RecyclerView.ItemDecoration {
    private int itemSpace;

    /**
     * @param itemSpace item间隔
     */
    public LibPicDialogItemDecoration(int itemSpace) {
        this.itemSpace = itemSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildLayoutPosition(view) != parent.getChildCount()) {
            outRect.bottom = itemSpace;
        }
    }
}
