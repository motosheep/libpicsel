package com.north.light.libpicselect.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.north.light.libpicselect.R;

import java.util.Map;

/**
 * create by lzt
 * data 2019/12/8
 */
public abstract class LibPicBaseActivity extends FragmentActivity {
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //竖屏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //顶部状态栏
                window.setStatusBarColor(getResources().getColor(R.color.lib_pic_color_000000));
                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * map转换bundle
     */
    protected Bundle getBundle(Map<String, Object> source) {
        if (source == null || source.size() == 0) {
            return new Bundle();
        }
        Bundle bundle = new Bundle();
        for (Map.Entry<String, Object> arg : source.entrySet()) {
            String key = arg.getKey();
            Object value = arg.getValue();
            if (!TextUtils.isEmpty(key) && value != null) {
                if (value instanceof Integer) {
                    bundle.putInt(key, (Integer) value);
                } else if (value instanceof String) {
                    bundle.putString(key, (String) value);
                } else if (value instanceof Boolean) {
                    bundle.putBoolean(key, (Boolean) value);
                } else if (value instanceof Float) {
                    bundle.putFloat(key, (Float) value);
                } else if (value instanceof Long) {
                    bundle.putLong(key, (Long) value);
                }
            }
        }
        return bundle;
    }
}
