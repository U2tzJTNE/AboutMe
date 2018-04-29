package com.u2tzjtne.aboutme.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.u2tzjtne.aboutme.BuildConfig;

import java.io.File;


public class ImageUtil {
    /**
     * 裁剪图片方法实现
     *
     * @param mTmpFile
     */
    public static void startPhotoZoom(Context mContext, File mTmpFile) {
        Uri uri = null;
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.i("TAG", "Android7.0以上版本的手机");
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileprovider", mTmpFile);
        } else {
            uri = Uri.fromFile(mTmpFile);
        }
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", true);
        ((Activity) mContext).startActivityForResult(intent, Const.REQUEST_CODE_EDITPIC);
    }
}
