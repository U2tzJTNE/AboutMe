package com.u2tzjtne.aboutme.http.protocol;


import com.u2tzjtne.aboutme.bean.AppBean;

import cn.bmob.v3.helper.GsonUtil;

/**
 * Created by JK on 2017/12/9.
 */

public class AppDetailProtocol extends BaseProtocol<AppBean> {
    private String packageName;

    public AppDetailProtocol(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getKey() {
        return "detail";
    }

    @Override
    public String getParams() {
        return "&packageName=" + packageName;
    }

    @Override
    public AppBean parseJson(String result) {
        try {
            return (AppBean) GsonUtil.toObject(result, AppBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
