package com.north.light.libpicselect.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * create by lzt
 * data 2019/12/8
 */
public class DialogItemDecoration extends RecyclerView.ItemDecoration {
    private int itemSpace;

    /**
     * @param itemSpace item间隔
     */
    public DialogItemDecoration(int itemSpace) {
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
