package com.north.light.libpicselect.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 目录信息类
 * 用于数据过于大，目前使用静态工具类作为数据传递的载体，每次传输，都会通过setData方法更新最新的数据值
 * */
public class LibPicDirecotryIntentInfo {
    private static  Map<String, List<LibPicInfo>> data = new HashMap<>();

    public static Map<String, List<LibPicInfo>> getData() {
        return data;
    }

    public static void setData(Map<String, List<LibPicInfo>> data) {
        LibPicDirecotryIntentInfo.data = data;
    }
}
