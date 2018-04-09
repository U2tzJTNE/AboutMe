package com.u2tzjtne.aboutme.bean;

import android.os.Environment;


import com.u2tzjtne.aboutme.manager.DownloadManager;

import java.io.File;

/**
 * Created by JK on 2017/12/11.
 */

public class DownloadBean {
    private int id;// apk唯一标识
    private long size;// apk大小
    private String downloadUrl;// 下载链接
    private String name;// apk名称
    private int currentState;// 当前下载状态
    private long currentPos;// 当前下载位置
    private String path;// apk下载在本地的路径

    private static final String GOOGLEMARKET = "googlemarket";// 文件夹名称
    private static final String DOWNLOAD = "download";// 子文件夹名称

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public long getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(long currentPos) {
        this.currentPos = currentPos;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 获取当前下载进度
    public float getProgress() {
        if (size == 0) {
            return 0;
        }

        return (float) currentPos / size;
    }

    /**
     * 根据应用信息,拷贝出一个下载对象
     */
    public static DownloadBean copy(AppBean appInfo) {
        DownloadBean info = new DownloadBean();
        info.id = appInfo.getId();
        info.size = appInfo.getSize();
        info.downloadUrl = appInfo.getDownloadUrl();
        info.name = appInfo.getName();
        info.currentState = DownloadManager.STATE_NONE;
        info.currentPos = 0;
        info.path = getFilePath(info.name);

        return info;
    }

    /**
     * 获取apk文件的本地下载路径
     */
    private static String getFilePath(String name) {
        StringBuffer sb = new StringBuffer();
        String sdcardPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        sb.append(sdcardPath);
        sb.append(File.separator);
        sb.append(GOOGLEMARKET);
        sb.append(File.separator);
        sb.append(DOWNLOAD);

        if (createDir(sb.toString())) {
            return sb.toString() + File.separator + name + ".apk";
        }

        return null;
    }

    // 创建文件夹
    private static boolean createDir(String dirPath) {
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            // 如果文件夹不存在,或者此文件不是一个文件夹,需要创建文件夹
            return dirFile.mkdirs();
        }

        return true;// 文件夹已经存在
    }

}
