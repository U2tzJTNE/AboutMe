package com.u2tzjtne.aboutme.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.util.Const;
import com.u2tzjtne.aboutme.util.SPUtil;


/**
 * Created by JK on 2017/11/14.
 * Activity基类
 */

public class BaseActivity extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initTheme();
    }

    //初始化主题
    public void initTheme() {
        int themeType = SPUtil.getInt(Const.THEME_TYPE, 100);
        switch (themeType) {
            case 100:
                setTheme(R.style.AppTheme);
                break;
            case 101:
                setTheme(R.style.AppTheme_Color1);
                break;
            case 102:
                setTheme(R.style.AppTheme_Color2);
                break;
            case 103:
                setTheme(R.style.AppTheme_Color3);
                break;
            case 104:
                setTheme(R.style.AppTheme_Color4);
                break;
            case 105:
                setTheme(R.style.AppTheme_Color5);
                break;
        }
    }

    /**
     * 启动指定Activity
     *
     * @param target 目标activity
     * @param bundle 携带bundle
     * @param finish 是否结束当前activity
     */
    public void startActivity(Class<? extends Activity> target, Bundle bundle, boolean finish) {
        Intent intent = new Intent(this, target);
        if (bundle != null) {
            intent.putExtra(getPackageName(), bundle);
        }
        startActivity(intent);
        if (finish) {
            finish();
        }
    }

    //获取bundle
    public Bundle getBundle() {
        if (getIntent() != null && getIntent().hasExtra(getPackageName())) {
            return getIntent().getBundleExtra(getPackageName());
        } else {
            return null;
        }
    }

}
