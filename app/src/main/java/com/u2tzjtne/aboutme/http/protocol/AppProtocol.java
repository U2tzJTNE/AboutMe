package com.u2tzjtne.aboutme.http.protocol;

import java.lang.reflect.Type;
import java.util.ArrayList;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.u2tzjtne.aboutme.bean.AppBean;


/**
 * 应用页访问网络
 *
 * @author Kevin
 */
public class AppProtocol extends BaseProtocol<ArrayList<AppBean>> {
    @Override
    public String getKey() {
        return "app";
    }

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public ArrayList<AppBean> parseJson(String result) {
        try {
            Type type = new TypeToken<ArrayList<AppBean>>() {
            }.getType();
            return new Gson().fromJson(result, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
