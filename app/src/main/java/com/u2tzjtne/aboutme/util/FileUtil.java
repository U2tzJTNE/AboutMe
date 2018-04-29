package com.u2tzjtne.aboutme.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {

    public static String APP_IMAGE_CACHE_PATH = "";

    public static String initPath() {
        if (checkSDCard()) {
            APP_IMAGE_CACHE_PATH = Environment.getExternalStorageDirectory() + "/AboutMe/";
        } else {
            APP_IMAGE_CACHE_PATH = Environment.getDataDirectory().getPath() + "/AboutMe/";
        }
        return APP_IMAGE_CACHE_PATH;
    }

    private static boolean checkSDCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * @param path 保存文件；
     */
    public static void saveBitmapWithPath(Bitmap bm, String path) {
        // 生成本来的文件名
        File file = new File(initPath());
        try {
            if (!file.exists())
                file.mkdir();
            File file1 = new File(path);
            if (!file1.exists()) {
                file1.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
