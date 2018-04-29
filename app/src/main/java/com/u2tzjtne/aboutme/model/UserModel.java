package com.u2tzjtne.aboutme.model;


import com.u2tzjtne.aboutme.bean.MyUser;
import com.u2tzjtne.aboutme.util.Const;
import com.u2tzjtne.aboutme.util.SPUtil;

import cn.bmob.v3.BmobUser;

/**
 * Created by JK on 2017/12/3.
 * 用户模型类
 */

public class UserModel {
    private static UserModel ourInstance;

    public static UserModel getInstance() {
        if (ourInstance == null) {
            ourInstance = new UserModel();
        }
        return ourInstance;
    }

    /**
     * 退出登录
     */
    public void logout() {
        BmobUser.logOut();
        SPUtil.putBoolean(Const.IS_LOGIN, false);
        SPUtil.putString(Const.USER_EMAIL, "");
        SPUtil.putString(Const.USER_PASSWORD, "");
    }

    //当前缓存的用户
    public MyUser getCurrentUser() {
        return BmobUser.getCurrentUser(MyUser.class);
    }

}
