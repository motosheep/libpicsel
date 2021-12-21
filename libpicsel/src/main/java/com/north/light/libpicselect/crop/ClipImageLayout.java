package com.north.light.libpicselect.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

/**
 * 剪切视图
 */
public class ClipImageLayout extends RelativeLayout {

    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;
    /**
     * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
     */
//    private int mHorizontalPadding = 1;
//    private int mVerticalPadding = (getHeight() - (getWidth() - 2 * mHorizontalPadding)) / 2;
    /**
     * 传入的图片路径
     */
    private String mLocalPicPath = "";
    /**
     * 当前旋转角度
     */
    private int mCurrentDegree = 0;
    /**
     * zoom view根布局
     */
    private RelativeLayout mZoomViewRoot;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mZoomViewRoot = new RelativeLayout(context);
        mClipImageView = new ClipImageBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mZoomViewRoot, lp);
        this.addView(mClipImageView, lp);
        // 计算padding的px
//        mHorizontalPadding = (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
//                        .getDisplayMetrics());
        mClipImageView.setHorizontalPadding(0);
        mClipImageView.setVerticalPadding(0);
        //初始化zoom view
        initZoomView();
    }

//    public void setImageDrawable(Drawable drawable) {
//        mZoomImageView.setImageDrawable(drawable);
//    }


    /**
     * 设置图片
     */
    public void setImageDrawable(String path) {
        mLocalPicPath = path;
        //显示本地图片
        ClipBitmapUtils bitmapUtils = new ClipBitmapUtils();
        Bitmap bitmap = bitmapUtils.decodeFile(path);
        if (mZoomImageView != null) {
            mZoomImageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 初始化缩放控件
     */
    private void initZoomView() {
        try {
            if (mZoomViewRoot != null) {
                mZoomViewRoot.removeAllViews();
                mZoomImageView = new ClipZoomImageView(getContext());
                android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT);
                mZoomViewRoot.addView(mZoomImageView, lp);
                mZoomImageView.setHorizontalPadding(0);
                mZoomImageView.setVerticalPadding(0);
            }
        } catch (Exception e) {
            mZoomImageView = null;
        }
    }

    /**
     * 旋转图片
     */
    public void setRightRotaImage() {
        rotaImage(getNextDegree(1));
    }

    /**
     * 旋转图片
     */
    public void setLeftRotaImage() {
        rotaImage(getNextDegree(2));
    }

    /**
     * 获取下次旋转角度
     *
     * @param type 1右 2左
     */
    private int getNextDegree(int type) {
        if (type == 1) {
            //右方向90度旋转
            if (mCurrentDegree > 360) {
                mCurrentDegree = 0;
            } else {
                mCurrentDegree = mCurrentDegree + 90;
            }
        } else if (type == 2) {
            //左方向90度旋转
            if (mCurrentDegree <= 0) {
                mCurrentDegree = 360;
            } else {
                mCurrentDegree = mCurrentDegree - 90;
            }
        }
        return mCurrentDegree;
    }

    /**
     * 旋转图片
     */
    private void rotaImage(int degree) {
        if (TextUtils.isEmpty(mLocalPicPath)) {
            return;
        }
        initZoomView();
        ClipBitmapUtils bitmapUtils = new ClipBitmapUtils();
        Bitmap bitmap = bitmapUtils.decodeFile(mLocalPicPath);
        Bitmap rotaBitmap = bitmapUtils.rotaingImageView(degree, bitmap);
        if (mZoomImageView != null) {
            mZoomImageView.setImageBitmap(rotaBitmap);
        }
    }

//    /**
//     * 对外公布设置边距的方法,单位为dp
//     *
//     * @param mHorizontalPadding
//     */
//    public void setHorizontalPadding(int mHorizontalPadding) {
//        this.mHorizontalPadding = mHorizontalPadding;
//    }

    /**
     * 设置宽高比例
     *
     * @param widthProportion
     * @param heightProportion
     */
    public void setProportion(int widthProportion, int heightProportion) {
        mClipImageView.setProportion(widthProportion, heightProportion);
        if (mZoomImageView != null) {
            mZoomImageView.setProportion(widthProportion, heightProportion);
        }
    }

//    public void setVerticalPadding(int mVerticalPadding) {
//        this.mVerticalPadding = mVerticalPadding;
//    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() throws Exception {
        return mZoomImageView.clip();
    }

}
