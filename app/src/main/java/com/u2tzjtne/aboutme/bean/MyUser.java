package com.u2tzjtne.aboutme.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by JK on 2017/11/30.
 */

public class MyUser extends BmobUser {
    private String avatarURL;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String nickname;

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
}
