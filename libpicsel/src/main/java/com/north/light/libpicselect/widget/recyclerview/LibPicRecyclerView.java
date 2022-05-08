package com.north.light.libpicselect.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.north.light.libpicselect.model.LibPicSelConfig;

import org.jetbrains.annotations.NotNull;

/**
 * FileName: LibPicRecyclerView
 * Author: lzt
 * Date: 2022/4/29 17:37
 */
public class LibPicRecyclerView extends RecyclerView {

    public LibPicRecyclerView(@NonNull @NotNull Context context) {
        super(context);
        init();
    }

    public LibPicRecyclerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LibPicRecyclerView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addOnScrollListener(new AutoLoadScrollListener());
    }

    public static class AutoLoadScrollListener extends OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case SCROLL_STATE_IDLE:
                    LibPicSelConfig.getInstance().getBindListener().BindResume();
                    break;
                case SCROLL_STATE_DRAGGING:
                    LibPicSelConfig.getInstance().getBindListener().BindPause();
                    break;
                case SCROLL_STATE_SETTLING:
                    LibPicSelConfig.getInstance().getBindListener().BindPause();
                    break;
            }
        }
    }
}
