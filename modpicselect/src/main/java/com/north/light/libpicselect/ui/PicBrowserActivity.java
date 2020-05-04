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
import android.widget.ImageView;


import com.github.chrisbanes.photoview.PhotoView;
import com.north.light.libpicselect.R;
import com.north.light.libpicselect.model.PicSelConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PicBrowserActivity extends PicBaseActivity {
    public static final String CODE_BROWSERLIST = "CODE_BROWSERLIST";
    public static final String CODE_BROWSERPOS = "CODE_BROWSERPOS";
    private int mBrowserPos = 0;
    private static final String TAG = PicBrowserActivity.class.getName();
    private ViewPager mViewPager;
    private List<PhotoView> mViewList = new ArrayList<>();
    private List<String> mDataList = new ArrayList<>();
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_browser);
        initView();
    }

    private void initView() {
        mDataList = getIntent().getStringArrayListExtra(CODE_BROWSERLIST);
        mBrowserPos = getIntent().getIntExtra(CODE_BROWSERPOS, 0);
        mViewPager = findViewById(R.id.activity_pic_browser_viewpager);
        mBack = findViewById(R.id.activity_pic_browser_back);

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
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(mBrowserPos);
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


    public static void launch(Activity activity, List<String> data) {
        if (data != null && data.size() != 0) {
            Intent intent = new Intent(activity, PicBrowserActivity.class);
            intent.putExtra(PicBrowserActivity.CODE_BROWSERLIST, (Serializable) data);
            activity.startActivity(intent);
        }
    }
}
