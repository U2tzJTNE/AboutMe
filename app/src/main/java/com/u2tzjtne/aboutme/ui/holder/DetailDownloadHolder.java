package com.u2tzjtne.aboutme.ui.holder;


import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.AppBean;
import com.u2tzjtne.aboutme.bean.DownloadBean;
import com.u2tzjtne.aboutme.manager.DownloadManager;
import com.u2tzjtne.aboutme.ui.view.ProgressHorizontal;
import com.u2tzjtne.aboutme.util.UIUtils;


/**
 * 详情页-下载
 */
public class DetailDownloadHolder extends BaseHolder<AppBean> implements DownloadManager.DownloadObserver, OnClickListener {

    private DownloadManager mDownloadManager;

    private Button btnDownload;
    //下载进度条容器
    private FrameLayout flDownload;
    //下载进度条
    private ProgressHorizontal pbProgress;

    private AppBean mAppDetailBean;

    private float mProgress;// 当前下载进度
    private int mCurrentState;// 当前下载状态

    @Override
    public View initView() {
        View view = View.inflate(UIUtils.getContext(),
                R.layout.layout_detail_download, null);
        btnDownload = view.findViewById(R.id.btn_detail_download);
        flDownload = view.findViewById(R.id.fl_download);
        btnDownload.setOnClickListener(this);
        flDownload.setOnClickListener(this);

        // 添加进度条布局
        pbProgress = new ProgressHorizontal(UIUtils.getContext());
        pbProgress.setProgressTextColor(Color.WHITE);// 文字颜色为白色
        pbProgress.setProgressTextSize(UIUtils.dp2px(18));// 文字大小
        pbProgress.setProgressResource(R.drawable.progress_normal);// 进度条颜色
        pbProgress.setProgressBackgroundResource(R.drawable.progress_bg);// 进度条背景色
        // 初始化布局参数
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        flDownload.addView(pbProgress, params);
        return view;
    }

    @Override
    public void refreshView(AppBean data) {
        mAppDetailBean = data;

        mDownloadManager = DownloadManager.getInstance();
        // 监听下载进度
        mDownloadManager.registerObserver(this);

        DownloadBean downloadInfo = mDownloadManager.getDownloadInfo(data);
        if (downloadInfo == null) {
            // 没有下载过
            mCurrentState = DownloadManager.STATE_NONE;
            mProgress = 0;
        } else {
            // 之前下载过, 以内存中的对象的状态为准
            mCurrentState = downloadInfo.getCurrentState();
            mProgress = downloadInfo.getProgress();
        }
        //根据对象状态刷新界面
        refreshUI(mProgress, mCurrentState);
    }

    /**
     * 刷新界面
     *
     * @param progress
     * @param state
     */
    private void refreshUI(float progress, int state) {
        mCurrentState = state;
        mProgress = progress;
        switch (state) {
            case DownloadManager.STATE_NONE:
                flDownload.setVisibility(View.GONE);
                btnDownload.setText(UIUtils.getString(R.string.app_state_download));
                break;
            case DownloadManager.STATE_WAITING:
                flDownload.setVisibility(View.GONE);
                btnDownload.setText(UIUtils.getString(R.string.app_state_waiting));
                break;
            case DownloadManager.STATE_DOWNLOAD:
                flDownload.setVisibility(View.VISIBLE);
                btnDownload.setVisibility(View.GONE);
                pbProgress.setProgress(progress);
                pbProgress.setCenterText("");
                break;
            case DownloadManager.STATE_PAUSE:
                btnDownload.setVisibility(View.GONE);
                flDownload.setVisibility(View.VISIBLE);
                pbProgress.setProgress(progress);
                pbProgress.setCenterText(UIUtils
                        .getString(R.string.app_state_paused));
                break;
            case DownloadManager.STATE_ERROR:
                flDownload.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText(UIUtils.getString(R.string.app_state_error));
                break;
            case DownloadManager.STATE_SUCCESS:
                flDownload.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText(UIUtils.getString(R.string.app_state_downloaded));
                break;
            default:
                break;
        }
    }

    @Override
    public void onDownloadStateChanged(DownloadBean info) {
        refreshOnMainThread(info);
    }

    @Override
    public void onDownloadProgressChanged(DownloadBean info) {
        refreshOnMainThread(info);
    }

    // 主线程刷新ui
    private void refreshOnMainThread(final DownloadBean info) {
        // 判断要刷新的下载对象是否是当前的应用
        if (info.getId() == mAppDetailBean.getId()) {
            UIUtils.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    refreshUI(info.getProgress(), info.getCurrentState());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_detail_download:
            case R.id.fl_download:
                // 根据当前状态来决定相关操作
                if (mCurrentState == DownloadManager.STATE_NONE
                        || mCurrentState == DownloadManager.STATE_PAUSE
                        || mCurrentState == DownloadManager.STATE_ERROR) {
                    // 开始下载
                    mDownloadManager.download(mAppDetailBean);
                } else if (mCurrentState == DownloadManager.STATE_DOWNLOAD
                        || mCurrentState == DownloadManager.STATE_WAITING) {
                    // 暂停下载
                    mDownloadManager.pause(mAppDetailBean);
                } else if (mCurrentState == DownloadManager.STATE_SUCCESS) {
                    // 开始安装
                    mDownloadManager.install(mAppDetailBean);
                }
                break;
            default:
                break;
        }
    }

}
