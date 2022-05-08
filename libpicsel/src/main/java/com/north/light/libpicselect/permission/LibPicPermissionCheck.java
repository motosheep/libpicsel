package com.north.light.libpicselect.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.north.light.libpicselect.model.LibPicSelConfig;

/**
 * author:li
 * date:2022/4/3
 * desc:权限检查Utils
 */
public class LibPicPermissionCheck {

    private static final class SingleHolder {
        static final LibPicPermissionCheck mInstance = new LibPicPermissionCheck();
    }

    public static LibPicPermissionCheck getInstance() {
        return LibPicPermissionCheck.SingleHolder.mInstance;
    }

    /**
     * 检查权限
     */
    public boolean check(int type) {
        Context context = LibPicSelConfig.getInstance().getContext();
        switch (type) {
            case LibPicPermissionType.TYPE_CAMERA:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case LibPicPermissionType.TYPE_RECORD:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case LibPicPermissionType.TYPE_EXTERNAL:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case LibPicPermissionType.TYPE_CAMERA_RECORD:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case LibPicPermissionType.TYPE_CAMERA_EXTERNAL:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                break;
            case LibPicPermissionType.TYPE_CAMERA_RECORD_EXTERNAL:
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
