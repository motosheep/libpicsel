package com.north.light.libpicselect.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirecotryIntentInfo {
    private static  Map<String, List<PicInfo>> data = new HashMap<>();

    public static Map<String, List<PicInfo>> getData() {
        return data;
    }

    public static void setData(Map<String, List<PicInfo>> data) {
        DirecotryIntentInfo.data = data;
    }
}
