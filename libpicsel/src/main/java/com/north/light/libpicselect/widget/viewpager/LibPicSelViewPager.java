package com.north.light.libpicselect.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * author:li
 * date:2021/11/21
 * desc:自定义图片库viewpager
 */
public class LibPicSelViewPager extends ViewPager {

    public LibPicSelViewPager(Context context) {
        super(context);
    }

    public LibPicSelViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }
}
