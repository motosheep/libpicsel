package com.north.light.libpicselect.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;


/**
 * @author zhy http://blog.csdn.net/lmj623565791/article/details/39761281
 * 绘制阴影截图视图
 */
public class LibPicClipImageBorderView extends View {
    private final String TAG = LibPicClipImageBorderView.class.getSimpleName();
    /**
     * 水平方向与View的边距
     */
    private int mHorizontalPadding;
    /**
     * 垂直方向与View的边距
     */
    private int mVerticalPadding;
    /**
     * 绘制的矩形的宽度
     */
    private int mWidth;
    /**
     * 边框的颜色，默认为白色
     */
    private int mBorderColor = Color.parseColor("#FFFFFF");
    /**
     * 边框的宽度 单位dp
     */
    private int mBorderWidth = 1;
    /**
     * 图片的比例 宽度
     */
    private int widthProportion = 10;
    /**
     * 图片的比例高度
     */
    private int heightProportion = 7;

    /**
     * 设置宽高比例
     *
     * @param widthProportion
     * @param heightProportion
     */
    public void setProportion(int widthProportion, int heightProportion) {
        this.widthProportion = widthProportion;
        this.heightProportion = heightProportion;
    }


    private Paint mPaint;

    public LibPicClipImageBorderView(Context context) {
        this(context, null);
    }

    public LibPicClipImageBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LibPicClipImageBorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 计算矩形区域的宽度
        mWidth = getWidth() - 2 * mHorizontalPadding;
        Log.d(TAG, " 计算矩形区域的宽度	getWidth()  = " + getWidth()
                + "  mHorizontalPadding" + mHorizontalPadding + "	mWidth"
                + mWidth);
        // 计算距离屏幕垂直边界 的边距
        int height = getHeight(widthProportion, heightProportion, getWidth());
        Log.d(TAG, " 计算距离屏幕垂直边界 的边距	height Utils.getHeight(16,9,getWidth())  = "
                + height);
        Log.d(TAG, " getHeight()  = " + getHeight());
        mVerticalPadding = (getHeight() - height) / 2;
        mPaint.setColor(Color.parseColor("#aa000000"));
        mPaint.setStyle(Style.FILL);
        // 绘制左边1
        canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
        // 绘制右边2
        canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(),
                getHeight(), mPaint);
        // 绘制上边3
        canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding, mVerticalPadding, mPaint);
        // 绘制下边4
        canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding, getWidth() - mHorizontalPadding, getHeight(), mPaint);
        // 绘制外边框
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Style.STROKE);
        canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth()
                - mHorizontalPadding, getHeight() - mVerticalPadding, mPaint);

    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;

    }

    public void setVerticalPadding(int mVerticalPadding) {
        this.mVerticalPadding = mVerticalPadding;
    }

    public static int getHeight(int widthProportion, int heightProportion, int width) {
        int temp = width / widthProportion;
        return temp * heightProportion;
    }

}
