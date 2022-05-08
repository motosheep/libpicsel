package com.north.light.libpicselect.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Created by lzt
 * time 2020/10/23
 * 描述：问价工具类
 */
public class LibPicFileUtils implements Serializable {

    private static final String TAG = LibPicFileUtils.class.getSimpleName();

    /**
     * 获取文件的后缀
     */
    private static String getFileExtName(String name) {
        return name.substring(name.lastIndexOf("."));
    }

    /**
     * 复制文件
     */
    public static String copyFileUsingFileStreams(String s, String parentPath) {
        try {
            String resultPath = parentPath + System.currentTimeMillis() + getFileExtName(s);
            new File(parentPath).mkdirs();
            File source = new File(s);
            File dest = new File(resultPath);
            if (!dest.exists()) {
                createFile(dest, true);// 创建文件
            }
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(source);
                output = new FileOutputStream(dest);
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) != -1) {
                    output.write(buf, 0, bytesRead);
                }
            } finally {
                input.close();
                output.close();
            }
            return resultPath;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String delFile) {
        try {
            File file = new File(delFile);
            if (!file.exists()) {
                return false;
            } else {
                if (file.isFile())
                    return deleteSingleFile(delFile);
                else
                    return deleteDirectory(delFile);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        try {
            // 如果dir不以文件分隔符结尾，自动添加文件分隔符
            if (!filePath.endsWith(File.separator))
                filePath = filePath + File.separator;
            File dirFile = new File(filePath);
            // 如果dir对应的文件不存在，或者不是一个目录，则退出
            if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
                return false;
            }
            boolean flag = true;
            // 删除文件夹中的所有文件包括子目录
            File[] files = dirFile.listFiles();
            for (File file : files) {
                // 删除子文件
                if (file.isFile()) {
                    flag = deleteSingleFile(file.getAbsolutePath());
                    if (!flag)
                        break;
                }
                // 删除子目录
                else if (file.isDirectory()) {
                    flag = deleteDirectory(file
                            .getAbsolutePath());
                    if (!flag)
                        break;
                }
            }
            if (!flag) {
                return false;
            }
            // 删除当前目录
            if (dirFile.delete()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    public static void createFile(File file, boolean isFile) {// 创建文件
        try {
            if (!file.exists()) {// 如果文件不存在
                if (!file.getParentFile().exists()) {// 如果文件父目录不存在
                    createFile(file.getParentFile(), false);
                } else {// 存在文件父目录
                    if (isFile) {// 创建文件
                        try {
                            file.createNewFile();// 创建新文件
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        file.mkdir();// 创建目录
                    }
                }
            }
        } catch (Exception e) {

        }
    }

//    /**
//     * bitmap保存本地方法
//     * change by lzt 20201112 通知本地媒体类更新数据
//     */
//    public String saveBitmap(Bitmap bm, String path) {
//        Log.e(TAG, "保存图片");
//        File f = new File(path);
//        if (!f.exists()) {
//            FileUtils.createFile(f, true);
//        }
//        try {
//            FileOutputStream out = new FileOutputStream(f);
//            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
//            Log.i(TAG, "已经保存");
//            try {
////                MediaStore.Images.Media.insertImage(
////                        PicSelConfig.getInstance().getContext().getContentResolver(),
////                        f.getAbsolutePath(), f.getName(), null);
////                PicSelConfig.getInstance().getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
////                        Uri.parse("file://" + f.getAbsolutePath())));
//            } catch (Exception e) {
//
//            }
//            out.flush();
//            out.close();
//            return path;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

}
