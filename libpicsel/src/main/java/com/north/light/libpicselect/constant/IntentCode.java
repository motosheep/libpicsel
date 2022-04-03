package com.north.light.libpicselect.constant;

/**
 * @Author: lzt
 * @CreateDate: 2021/8/9 15:23
 * @Version: 1.0
 * @Description:activity intent 跳转code
 */
public class IntentCode {
    //默认请求req code
    public static final int DEFAULT_REQ = 0x1116;
    //视频录制code
    public static final int VIDEO_REQ = 0x0013;
    public static final int VIDEO_RES = 0x0014;
    public static final int VIDEO_RECORD_RESULT_CODE = 0x0005;
    public static final String VIDEO_RECODE_SECOND = "VIDEO_RECODE_SECOND";
    public static final String VIDEO_RECODE_PATH = "VIDEO_RECODE_PATH";
    //图片选择code
    public static final int PIC_SEL_REQ = 0x0011;
    public static final int PIC_SEL_RES = 0x0012;
    public static final String PIC_SEL_DATA_LIMIT = "PIC_SEL_DATA_LIMIT";
    public static final String PIC_SEL_DATA_NEED_CAMERA = "PIC_SEL_DATA_NEED_CAMERA";
    public static final String PIC_SEL_DATA_SHOW_GIF = "PIC_SEL_DATA_SHOW_GIF";
    public static final String PIC_SEL_DATA_SHOW_VIDEO = "PIC_SEL_DATA_SHOW_VIDEO";
    public static final String PIC_SEL_DATA_SELECT = "PIC_SEL_DATA_SELECT";
    public static final String PIC_SEL_DATA_CUS_CAMERA = "PIC_SEL_DATA_CUS_CAMERA";
    //图片浏览
    public static final int BROWSER_CODE_REQUEST = 0x1101;
    public static final int BROWSER_CODE_RESULT = 0x1102;
    public static final int BROWSER_CODE_RESULT_CONFIRM = 0x1103;
    public static final String BROWSER_POSITION = "BROWSER_POSITION";
    public static final String BROWSER_SHOW_SEL_MODE = "BROWSER_SHOW_SEL_MODE";
    public static final String BROWSER_SEL_LIMIT = "BROWSER_SEL_LIMIT";
    //浏览视频方式：1系统自带 2自定义视频播放页面
    public static final String BROWSER_VIDEO_WAY = "BROWSER_VIDEO_WAY";
    //系统拍摄图片
    public static final int PIC_MAIN_TAKE_PIC_REQUEST = 0x1111;
    //系统剪裁图片
    public static final int PIC_MAIN_CROP_PIC_REQUEST = 0x1113;
    //图片剪裁--自定义
    public static final int PIC_CROP_CODE_REQ = 0x1114;
    public static final int PIC_CROP_CODE_RES = 0x1115;
    //原图路径
    public static final String PIC_CROP_DATA_ORG_PATH = "PIC_CROP_DATA_ORG_PATH";
    //目标图片路径
    public static final String PIC_CROP_DATA_TAR_PATH = "PIC_CROP_DATA_TAR_PATH";
    //宽高比率
    public static final String PIC_CROP_PIC_RATE_WIDTH = "PIC_CROP_PIC_RATE_WIDTH";
    public static final String PIC_CROP_PIC_RATE_HEIGHT = "PIC_CROP_PIC_RATE_HEIGHT";
    //剪裁路径
    public static final String PIC_CROP_DATA_CLIP_DATA = "PIC_CROP_DATA_CLIP_DATA";
    //图片中间件页面
    public static final String PIC_SEL_MID_PARAMS = "PIC_SEL_MID_PARAMS";
    //中间页面code
    public static final int PIC_SEL_MID_REQ_CODE = 0x1117;
    public static final int PIC_SEL_MID_RES_CODE = 0x1118;
}
