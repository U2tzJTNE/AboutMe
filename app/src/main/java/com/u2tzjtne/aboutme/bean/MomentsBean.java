package com.u2tzjtne.aboutme.bean;

import java.io.Serializable;

/**
 * Created by JK on 2017/12/25.
 */

public class MomentsBean implements Serializable {
    private String nickname;
    private String date;
    private String message;
    private String avatarURL;
    private int fontSize;
    private int bgColor;
    private String userID;
    private String email;
    private boolean isFooter;

    public boolean isFooter() {
        return isFooter;
    }

    public void setFooter(boolean footer) {
        isFooter = footer;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String name) {
        this.nickname = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String imageURL) {
        this.avatarURL = imageURL;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int textSize) {
        this.fontSize = textSize;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }
}
