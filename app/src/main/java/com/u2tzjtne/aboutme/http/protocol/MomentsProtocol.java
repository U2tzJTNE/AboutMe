package com.u2tzjtne.aboutme.http.protocol;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.u2tzjtne.aboutme.bean.MomentsBean;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by JK on 2018/1/1.
 */

public class MomentsProtocol extends BaseProtocol<ArrayList<MomentsBean>> {
    @Override
    public String getKey() {
        return "moments";
    }

    @Override
    public String getParams() {
        return "";
    }

    @Override
    public ArrayList<MomentsBean> parseJson(String result) {
        try {
            Type type = new TypeToken<ArrayList<MomentsBean>>() {
            }.getType();
            return new Gson().fromJson(result, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
