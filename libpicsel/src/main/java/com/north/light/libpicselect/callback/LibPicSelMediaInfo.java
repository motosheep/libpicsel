package com.north.light.libpicselect.callback;

/**
 * FileName: LibPicSelMediaInfo
 * Author: lzt
 * Date: 2022/4/29 16:42
 * 选择图片信息obj
 */
public class LibPicSelMediaInfo {
    //原图的路径
    private String path;
    //转换后的路径
    private String trainPath;

    public LibPicSelMediaInfo(String path, String trainPath) {
        this.path = path;
        this.trainPath = trainPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTrainPath() {
        return trainPath;
    }

    public void setTrainPath(String trainPath) {
        this.trainPath = trainPath;
    }
}
