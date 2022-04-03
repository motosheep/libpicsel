package com.north.light.libpicselect.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.north.light.libpicselect.model.PicSelConfig;

/**
 * author:li
 * date:2022/4/3
 * desc:权限检查Utils
 */
public class PicPermissionCheck {

    private static final class SingleHolder {
        static final PicPermissionCheck mInstance = new PicPermissionCheck();
    }

    public static PicPermissionCheck getInstance() {
        return PicPermissionCheck.SingleHolder.mInstance;
    }

    /**
     * 检查权限
     */
    public boolean check(int type) {
        Context context = PicSelConfig.getInstance().getContext();
        switch (type) {
            case PicPermissionType.TYPE_CAMERA:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case PicPermissionType.TYPE_RECORD:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case PicPermissionType.TYPE_EXTERNAL:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case PicPermissionType.TYPE_CAMERA_RECORD:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case PicPermissionType.TYPE_CAMERA_EXTERNAL:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case PicPermissionType.TYPE_CAMERA_RECORD_EXTERNAL:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            default:
                return false;
        }
        return true;
    }


}
