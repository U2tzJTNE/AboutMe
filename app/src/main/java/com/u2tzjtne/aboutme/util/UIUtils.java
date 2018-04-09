package com.u2tzjtne.aboutme.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

import com.u2tzjtne.aboutme.MyApplication;

/**
 * Created by JK on 2017/5/25.
 * <p>
 * UI工具类
 */

public class UIUtils {
    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static int getMainThreadId() {
        return MyApplication.getMainThreadId();
    }

    public static Handler getHandler() {
        return MyApplication.getHandler();
    }

    //根据id获取字符串
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    //根据id获取图片
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    //根据id获取颜色值
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    //获取颜色状态集合
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    //根据id获取尺寸(像素)
    public static int getDimension(int id) {
        return getContext().getResources().getDimensionPixelSize(id);
    }

    //根据id获取字符串数组
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    //dp转px
    public static int dp2px(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5);
    }

    //px转dp
    public static int px2dp(float px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);
    }

    //加载布局文件
    public static View inflate(int layoutId) {
        return View.inflate(getContext(), layoutId, null);
    }

    /**
     * 判断当前是否运行在主线程
     *
     * @return
     */
    public static boolean isRunOnUiThread() {
        return getMainThreadId() == android.os.Process.myTid();
    }

    /**
     * 保证当前的操作运行在UI主线程
     *
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        if (isRunOnUiThread()) {
            runnable.run();
        } else {
            getHandler().post(runnable);
        }
    }
}
