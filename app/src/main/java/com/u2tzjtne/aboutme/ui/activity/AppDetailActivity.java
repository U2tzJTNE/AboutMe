package com.u2tzjtne.aboutme.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.AppBean;
import com.u2tzjtne.aboutme.http.protocol.AppDetailProtocol;
import com.u2tzjtne.aboutme.ui.holder.DetailAppDesHolder;
import com.u2tzjtne.aboutme.ui.holder.DetailAppInfoHolder;
import com.u2tzjtne.aboutme.ui.holder.DetailAppOtherHolder;
import com.u2tzjtne.aboutme.ui.holder.DetailAppPicsHolder;
import com.u2tzjtne.aboutme.ui.holder.DetailAppUpdateHolder;
import com.u2tzjtne.aboutme.ui.holder.DetailDownloadHolder;
import com.u2tzjtne.aboutme.ui.view.LoadingPage;

public class AppDetailActivity extends BaseActivity {
    private LoadingPage mLoadingPage;
    private String mPackageName;
    private AppBean mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLoadingPage = new LoadingPage(this) {
            @Override
            public View onCreateSuccessView() {
                return AppDetailActivity.this.onCreateSuccessView();
            }

            @Override
            public ResultState onLoad() {
                return AppDetailActivity.this.onLoad();
            }
        };
        setContentView(mLoadingPage);
        mPackageName = getIntent().getStringExtra("package");
        // 开始加载数据
        mLoadingPage.loadData();
    }


    /**
     * 访问网络数据成功后初始化成功页面
     *
     * @return
     */
    public View onCreateSuccessView() {
        View view = View.inflate(this, R.layout.activity_app_detail, null);
        // 初始化应用详情信息
        FrameLayout flDetailAppInfo = view
                .findViewById(R.id.fl_detail_appinfo);
        DetailAppInfoHolder appInfoHolder = new DetailAppInfoHolder();
        flDetailAppInfo.addView(appInfoHolder.getRootView());
        appInfoHolder.setData(mData);
        // 初始化图片信息
        HorizontalScrollView hsvDetailPics = view
                .findViewById(R.id.hsv_detail_pics);
        DetailAppPicsHolder appPicsHolder = new DetailAppPicsHolder();
        hsvDetailPics.addView(appPicsHolder.getRootView());
        appPicsHolder.setData(mData);
        // 初始化应用更新信息
        FrameLayout flDetailUpdateInfo = view
                .findViewById(R.id.fl_detail_update_info);
        DetailAppUpdateHolder appUpdateHolder = new DetailAppUpdateHolder();
        flDetailUpdateInfo.addView(appUpdateHolder.getRootView());
        appUpdateHolder.setData(mData);
        // 初始化描述信息
        FrameLayout flDetailDesInfo = view
                .findViewById(R.id.fl_detail_des);
        DetailAppDesHolder appDesHolder = new DetailAppDesHolder();
        flDetailDesInfo.addView(appDesHolder.getRootView());
        appDesHolder.setData(mData);
        // 初始化应用其它信息
        FrameLayout flDetailAppOtherInfo = view
                .findViewById(R.id.fl_detail_other_info);
        DetailAppOtherHolder appOtherInfoHolder = new DetailAppOtherHolder();
        flDetailAppOtherInfo.addView(appOtherInfoHolder.getRootView());
        appOtherInfoHolder.setData(mData);
        // 应用权限信息
        RelativeLayout rl_permission = view.findViewById(R.id.rl_app_permission_info);
        rl_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppDetailActivity.this, AppPermissionActivity.class);
                //TODO 传过去Bundle
                startActivity(intent);
            }
        });
        // 初始化下载布局
        FrameLayout flDetailDownload = view
                .findViewById(R.id.fl_detail_download);
        DetailDownloadHolder downloadHolder = new DetailDownloadHolder();
        flDetailDownload.addView(downloadHolder.getRootView());
        downloadHolder.setData(mData);

        return view;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 加载网络数据
     *
     * @return
     */
    public LoadingPage.ResultState onLoad() {
        AppDetailProtocol protocol = new AppDetailProtocol(mPackageName);
        mData = protocol.getData(0,true);
        if (mData != null) {
            return LoadingPage.ResultState.STATE_SUCCESS;
        } else {
            return LoadingPage.ResultState.STATE_ERROR;
        }
    }

}