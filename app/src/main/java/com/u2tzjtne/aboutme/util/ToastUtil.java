package com.u2tzjtne.aboutme.util;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.u2tzjtne.aboutme.MyApplication;


/**
 * ToastUtil 利用单例模式，解决重命名toast重复弹出的问题
 */
public class ToastUtil {
    private static Toast mToast;

    public static void s(@NonNull String mString) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApplication.getInstance(), mString, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(mString);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void s(int messageID) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApplication.getInstance(), messageID, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(messageID);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void l(@NonNull String mString) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApplication.getInstance(), mString, Toast.LENGTH_LONG);
        } else {
            mToast.setText(mString);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    public static void l(int messageID) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApplication.getInstance(), messageID, Toast.LENGTH_LONG);
        } else {
            mToast.setText(messageID);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

}
