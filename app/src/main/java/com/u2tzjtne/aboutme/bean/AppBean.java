package com.u2tzjtne.aboutme.bean;

import java.util.ArrayList;

/**
 * Created by JK on 2017/11/17.
 */

public class AppBean {

    /**
     * id : 1580615
     * name : 人人
     * packageName : com.renren.mobile.android
     * iconUrl : app/com.renren.mobile.android/icon.jpg
     * stars : 2
     * size : 21803987
     * downloadUrl : app/com.renren.mobile.android/com.renren.mobile.android.apk
     * des : 2005-2014 你的校园一直在这儿。中国最大的实名制SNS网络平台，大学生
     */

    private int id;
    private String name;
    private String packageName;
    private String iconUrl;
    private float stars;
    private int size;
    private String downloadUrl;
    private String des;

    //// 以下字段共应用详情页使用
    private String downloadNum;
    private String version;
    private String date;
    private String author;
    private ArrayList<String> screen;
    private ArrayList<SafeBean> safe;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }


    public String getDownloadNum() {
        return downloadNum;
    }

    public void setDownloadNum(String downloadNum) {
        this.downloadNum = downloadNum;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<String> getScreen() {
        return screen;
    }

    public void setScreen(ArrayList<String> screen) {
        this.screen = screen;
    }

    public ArrayList<SafeBean> getSafe() {
        return safe;
    }

    public void setSafe(ArrayList<SafeBean> safe) {
        this.safe = safe;
    }


    public static class SafeBean {
        /**
         * safeUrl : app/com.baidu.input/safeIcon0.jpg
         * safeDesUrl : app/com.baidu.input/safeDesUrl0.jpg
         * safeDes : 已通过安智市场官方认证，是正版软件
         * safeDesColor : 0
         */

        private String safeUrl;
        private String safeDesUrl;
        private String safeDes;
        private int safeDesColor;

        public String getSafeUrl() {
            return safeUrl;
        }

        public void setSafeUrl(String safeUrl) {
            this.safeUrl = safeUrl;
        }

        public String getSafeDesUrl() {
            return safeDesUrl;
        }

        public void setSafeDesUrl(String safeDesUrl) {
            this.safeDesUrl = safeDesUrl;
        }

        public String getSafeDes() {
            return safeDes;
        }

        public void setSafeDes(String safeDes) {
            this.safeDes = safeDes;
        }

        public int getSafeDesColor() {
            return safeDesColor;
        }

        public void setSafeDesColor(int safeDesColor) {
            this.safeDesColor = safeDesColor;
        }
    }
}
