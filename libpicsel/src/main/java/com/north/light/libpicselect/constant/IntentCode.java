package com.north.light.libpicselect.constant;

/**
 * @Author: lzt
 * @CreateDate: 2021/8/9 15:23
 * @Version: 1.0
 * @Description:activity intent 跳转code
 */
public class IntentCode {
    //视频录制code
    public static final int VIDEO_REQ = 0x0003;
    public static final int VIDEO_RES = 0x0004;
    public static final int VIDEO_RECORD_RESULT_CODE = 0x0005;
    public static final String VIDEO_RECODE_SECOND = "VIDEO_RECODE_SECOND";
    public static final String VIDEO_RECODE_PATH = "VIDEO_RECODE_PATH";

    //图片选择code
    public static final int PIC_SEL_REQ = 0x0001;
    public static final int PIC_SEL_RES = 0x0002;
    public static final String PIC_SEL_DATA_LIMIT = "PIC_SEL_DATA_LIMIT";
    public static final String PIC_SEL_DATA_NEEDCAMERA = "PIC_SEL_DATA_NEEDCAMERA";
    public static final String PIC_SEL_DATA_SHOWGIF = "PIC_SEL_DATA_SHOWGIF";
    public static final String PIC_SEL_DATA_SHOWVIDEO = "PIC_SEL_DATA_SHOWVIDEO";
    public static final String PIC_SEL_DATA_SELECT = "PIC_SEL_DATA_SELECT";

    //图片浏览
    public static final int BROWSER_CODE_REQUEST = 0x1101;
    public static final int BROWSER_CODE_RESULT = 0x1102;
    public static final int BROWSER_CODE_RESULT_CONFIRM = 0x1103;
    public static final String BROWSER_BROWSERPOS = "BROWSER_BROWSERPOS";
    public static final String BROWSER_SHOWSELMODE = "BROWSER_SHOWSELMODE";
    public static final String BROWSER_SELLIMIT = "BROWSER_SELLIMIT";

    //main
    public static final int TAKEPIC_RESULT = 0x1111;
    public static final int CROPPIC_REQUEST = 0x1113;
}
