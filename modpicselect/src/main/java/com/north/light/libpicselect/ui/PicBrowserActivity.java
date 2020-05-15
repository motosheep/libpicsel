package com.north.light.libpicselect.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.chrisbanes.photoview.PhotoView;
import com.north.light.libpicselect.R;
import com.north.light.libpicselect.bean.PicInfo;
import com.north.light.libpicselect.bean.PicSelIntentInfo;
import com.north.light.libpicselect.model.PicSelConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PicBrowserActivity extends PicBaseActivity {
    public static final int CODE_REQUEST = 0x1101;
    public static final int CODE_RESULT = 0x1102;

    public static final String CODE_BROWSERLIST = "CODE_BROWSERLIST";
    public static final String CODE_BROWSERPOS = "CODE_BROWSERPOS";
    public static final String CODE_SHOWSELMODE = "CODE_SHOWSELMODE";
    public static final String CODE_SELLIMIT = "CODE_SELLIMIT";
    private int mBrowserPos = 0;//浏览位置
    private static final String TAG = PicBrowserActivity.class.getName();
    private ViewPager mViewPager;
    private List<PhotoView> mViewList = new ArrayList<>();
    private List<String> mDataList = new ArrayList<>();
    private ImageView mBack;
    //是否开启选择模式
    private volatile boolean isShowSelMode = false;//默认不开启
    private volatile int selLimit = 9;
    //图片选中框
    private CheckBox mCheckBox;
    //选择提示
    private TextView mSelTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_browser);
        initView();
    }

    private void initView() {
        //是否开启选择模式
        isShowSelMode = getIntent().getBooleanExtra(CODE_SHOWSELMODE, false);
        selLimit = getIntent().getIntExtra(CODE_SELLIMIT, 9);
        mDataList = getIntent().getStringArrayListExtra(CODE_BROWSERLIST);
        mBrowserPos = getIntent().getIntExtra(CODE_BROWSERPOS, 0);
        mViewPager = findViewById(R.id.activity_pic_browser_viewpager);
        mBack = findViewById(R.id.activity_pic_browser_back);
        mCheckBox = findViewById(R.id.activity_pic_browser_checkbox);
        mSelTips = findViewById(R.id.activity_pic_browser_tips);
        mSelTips.setText(String.format(getResources().getString(R.string.pic_select_activity_checkbox_count), 0, selLimit));
        if (isShowSelMode) {
            mCheckBox.setVisibility(View.VISIBLE);
        } else {
            mCheckBox.setVisibility(View.INVISIBLE);
        }
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        for (String result : mDataList) {
            if (!TextUtils.isEmpty(result)) {
                Log.d(TAG, "数据: " + result);
                PhotoView photoView = new PhotoView(this);
                mViewList.add(photoView);
            }
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (isShowSelMode) {
                    //选择模式，更新该图片选择状态
                    final boolean isSel = PicSelIntentInfo.getInstance().isSel(mDataList.get(i));
                    mCheckBox.setChecked(isSel);
                    updateSelCount();
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
                                Log.d(TAG, "sel count: " + count);
                                //设置提示
                                updateSelCount();
                            }

                            @Override
                            public void limit() {
                                Log.d(TAG, "limit");
                                mCheckBox.setChecked(false);
                                Toast.makeText(PicBrowserActivity.this.getApplicationContext()
                                        , "最多只能选择" + selLimit + "张图片", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(mBrowserPos);
    }

    //更新选中个数
    private void updateSelCount() {
        int count = PicSelIntentInfo.getInstance().selCount();
        mSelTips.setText(String.format(getResources().getString(R.string.pic_browser_activity_checkbox_count), count, selLimit));
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
            if (PicSelConfig.getInstance().getbindListener() != null)
                PicSelConfig.getInstance().getbindListener().BindImageView(mDataList.get(position), mViewList.get(position));
            return mViewList.get(position);
        }

    };

    @Override
    public void finish() {
        if(isShowSelMode){
            setResult(CODE_RESULT);
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 指定图片位置的方法
     */
    public static void launch(Activity activity, List<String> data, int position, int selLimit) {
        if (data != null && data.size() != 0) {
            Intent intent = new Intent(activity, PicBrowserActivity.class);
            intent.putExtra(PicBrowserActivity.CODE_BROWSERLIST, (Serializable) data);
            intent.putExtra(PicBrowserActivity.CODE_BROWSERPOS, position);
            intent.putExtra(PicBrowserActivity.CODE_SHOWSELMODE, true);
            intent.putExtra(PicBrowserActivity.CODE_SELLIMIT, selLimit);
            activity.startActivityForResult(intent, CODE_REQUEST);
        }
    }
}
