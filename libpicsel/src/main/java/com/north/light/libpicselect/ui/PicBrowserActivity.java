package com.north.light.libpicselect.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.north.light.libpicselect.PicSelMain;
import com.north.light.libpicselect.R;
import com.north.light.libpicselect.bean.PicSelIntentInfo;
import com.north.light.libpicselect.constant.IntentCode;
import com.north.light.libpicselect.constant.PicConstant;
import com.north.light.libpicselect.model.PicSelConfig;
import com.north.light.libpicselect.widget.photoview.PhotoView;
import com.north.light.libpicselect.widget.viewpager.LibPicSelViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片浏览activity
 * change by lzt 20200823 增加视频数据的适配
 * change by lzt 20201013 增加选择确定按钮
 * change by lzt 20201020 修改图片数据重内存获取
 */
public class PicBrowserActivity extends PicBaseActivity {
    private int mBrowserPos = 0;//浏览位置
    private int mVideoPlayWay = 1;//视频播放方式：1系统自带 2开发者自定义
    private static final String TAG = PicBrowserActivity.class.getName();
    private LibPicSelViewPager mViewPager;
    private List<PhotoView> mViewList = new ArrayList<>();
    private List<String> mDataList = new ArrayList<>();
    private ImageView mBack;
    //是否开启选择模式--默认不开启
    private volatile boolean isShowSelMode = false;
    private volatile int selLimit = 9;
    //图片选中框
    private CheckBox mCheckBox;
    //选择提示
    private TextView mSelTips;
    //播放按钮监听__播放功能，暂时只做本地视频播放适配
    private ImageView mPlayBt;
    //选择图片确定控件
    private TextView mConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_pic_activity_pic_browser);
        initView();
    }

    private void initView() {
        //是否开启选择模式
        isShowSelMode = getIntent().getBooleanExtra(IntentCode.BROWSER_SHOW_SEL_MODE, false);
        selLimit = getIntent().getIntExtra(IntentCode.BROWSER_SEL_LIMIT, 9);
        mDataList = PicConstant.getInstance().getPicList();
        mBrowserPos = getIntent().getIntExtra(IntentCode.BROWSER_POSITION, 0);
        mVideoPlayWay = getIntent().getIntExtra(IntentCode.BROWSER_VIDEO_WAY, 1);
        mViewPager = findViewById(R.id.lib_pic_activity_pic_browser_viewpager);
        mBack = findViewById(R.id.lib_pic_activity_pic_browser_back);
        mConfirm = findViewById(R.id.lib_pic_activity_pic_browser_confirm);
        mCheckBox = findViewById(R.id.lib_pic_activity_pic_browser_checkbox);
        mSelTips = findViewById(R.id.lib_pic_activity_pic_browser_tips);
        mPlayBt = findViewById(R.id.lib_pic_activity_pic_browser_play_video);
        mSelTips.setText(String.format(getResources().getString(R.string.lib_pic_pic_select_activity_checkbox_count), 0, selLimit));
        if (isShowSelMode) {
            mCheckBox.setVisibility(View.VISIBLE);
            mSelTips.setVisibility(View.VISIBLE);
            mConfirm.setVisibility(View.VISIBLE);
        } else {
            mCheckBox.setVisibility(View.INVISIBLE);
            mSelTips.setVisibility(View.INVISIBLE);
            mConfirm.setVisibility(View.INVISIBLE);
        }
        //监听事件
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确定
                finishPage(2);
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishPage(1);
            }
        });
        for (String result : mDataList) {
//            if (!TextUtils.isEmpty(result)) {
            PhotoView photoView = new PhotoView(this);
            mViewList.add(photoView);
//            }
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (isShowSelMode) {
                    //选择模式，更新该图片选择状态
                    String path = mDataList.get(i);
                    final boolean isSel = PicSelIntentInfo.getInstance().isSel(path);
                    isVideo(path);
                    mCheckBox.setChecked(isSel);
                    updateSelCount();
                } else {
                    //非选择模式--只更新播放按钮视觉
                    String path = mDataList.get(i);
                    isVideo(path);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = mViewPager.getCurrentItem();
                PicSelIntentInfo.getInstance().setSelPic(mDataList.get(pos), pos, mCheckBox.isChecked(), selLimit,
                        new PicSelIntentInfo.SelCountListener() {
                            @Override
                            public void selCount(int count) {
                                //设置提示
                                updateSelCount();
                            }

                            @Override
                            public void limit() {
                                mCheckBox.setChecked(false);
                                Toast.makeText(PicBrowserActivity.this.getApplicationContext()
                                        , "最多只能选择" + selLimit + "张图片", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        //播放按钮监听
        mPlayBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放视频
                try {
                    String path = mDataList.get(mViewPager.getCurrentItem());
                    if (mVideoPlayWay == 1) {
                        //系统默认
                        if (path.contains("http")) {
                            PicSelMain.getInstance().playNetVideo(mDataList.get(mViewPager.getCurrentItem())
                                    , PicBrowserActivity.this);
                        } else {
                            PicSelMain.getInstance().playLocalVideo(mDataList.get(mViewPager.getCurrentItem())
                                    , PicBrowserActivity.this);
                        }
                    } else if (mVideoPlayWay == 2) {
                        //自定义播放--广播
                        PicSelMain.getInstance().sendPlayUrlIntent(PicBrowserActivity.this, path);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "mPlayBt e: " + e.getMessage());
                }
            }
        });
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(mBrowserPos);
        //如果浏览的数据为第0个，则会出现无法触发viewpager滑动监听
        if (isShowSelMode && mBrowserPos == 0) {
            try {
                String path = mDataList.get(0);
                final boolean isSel = PicSelIntentInfo.getInstance().isSel(path);
                isVideo(path);
                mCheckBox.setChecked(isSel);
                updateSelCount();
            } catch (Exception e) {
                Log.d(TAG, "设置默认选中逻辑error: " + e.getMessage());
            }
        } else if (!isShowSelMode && mBrowserPos == 0) {
            try {
                //非选择模式下，检查第一个资源
                String path = mDataList.get(0);
                isVideo(path);
            } catch (Exception e) {
                Log.d(TAG, "设置默认选中逻辑error: " + e.getMessage());
            }
        }
    }

    //更新选中个数
    private void updateSelCount() {
        int count = PicSelIntentInfo.getInstance().selCount();
        mSelTips.setText(String.format(getResources().getString(R.string.lib_pic_pic_browser_activity_checkbox_count), count, selLimit));
    }


    private PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "title";
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            try {
                if (PicSelConfig.getInstance().getbindListener() != null)
                    PicSelConfig.getInstance().getbindListener().BindImageView(mDataList.get(position), mViewList.get(position));
            } catch (Exception e) {
                Log.d(TAG, "instantiateItem error: " + e.getMessage());
            }
            return mViewList.get(position);
        }

    };

    /**
     * 结束页面
     *
     * @param mode 1普通浏览，2选择浏览
     */
    private void finishPage(int mode) {
        if (isShowSelMode) {
            if (mode == 1) {
                setResult(IntentCode.BROWSER_CODE_RESULT);
            } else if (mode == 2) {
                setResult(IntentCode.BROWSER_CODE_RESULT_CONFIRM);
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        finishPage(1);
    }

    /**
     * 指定图片位置的方法--内部
     */
    public static void launch(Activity activity, int position, int selLimit) {
        Intent intent = new Intent(activity, PicBrowserActivity.class);
        intent.putExtra(IntentCode.BROWSER_POSITION, position);
        intent.putExtra(IntentCode.BROWSER_SHOW_SEL_MODE, true);
        intent.putExtra(IntentCode.BROWSER_SEL_LIMIT, selLimit);
        activity.startActivityForResult(intent, IntentCode.BROWSER_CODE_REQUEST);
    }

    /**
     * 判断数据是否为视频
     */
    public void isVideo(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (path.toLowerCase().contains(".mp4") || path.toLowerCase().contains(".3gp")
                || path.toLowerCase().contains(".rmvb") || path.toLowerCase().contains(".flv")) {
            //是视频
            mPlayBt.setVisibility(View.VISIBLE);
        } else {
            mPlayBt.setVisibility(View.GONE);
        }
    }
}
