package com.u2tzjtne.aboutme.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.AppBean;
import com.u2tzjtne.aboutme.bean.DownloadBean;
import com.u2tzjtne.aboutme.http.HttpHelper;
import com.u2tzjtne.aboutme.manager.DownloadManager;
import com.u2tzjtne.aboutme.ui.activity.AppDetailActivity;
import com.u2tzjtne.aboutme.ui.view.CProgressButton;
import com.u2tzjtne.aboutme.util.UIUtil;

import java.util.ArrayList;

/**
 * Created by JK on 2017/12/9.
 */

public class AppPagerAdapter extends RecyclerView.Adapter<AppPagerAdapter.AppHolder> implements View.OnClickListener, DownloadManager.DownloadObserver {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<AppBean> mItems;
    private DownloadManager manager;
    private float mProgress;// 当前下载进度
    private int mCurrentState;// 当前下载状态
    private AppBean mAppInfo;
    private AppHolder mHolder;

    public AppPagerAdapter(Context context) {
        this.context = context;
        mItems = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public ArrayList<AppBean> getList() {
        return mItems;
    }

    public void setItems(ArrayList<AppBean> data) {
        this.mItems.clear();
        this.mItems.addAll(data);
    }


    public void addItems(final ArrayList<AppBean> data) {
        mItems.addAll(data);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public AppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppHolder(inflater.inflate(R.layout.list_item_app, parent, false));
    }

    @Override
    public void onBindViewHolder(final AppHolder holder, int position) {

        mHolder = holder;

        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.anim_recycler_item_show);
        holder.mView.startAnimation(animation);

        AlphaAnimation aa1 = new AlphaAnimation(1.0f, 0.1f);
        aa1.setDuration(400);
        holder.ivIcon.startAnimation(aa1);

        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        aa.setDuration(400);

        // 设置数据
        mAppInfo = mItems.get(position);
        if (mAppInfo != null) {
            CProgressButton.initStatusString(new String[]{"下载", "等待", "暂停", "重试", "安装"});
            holder.tvName.setText(mAppInfo.getName());
            holder.tvDesc.setText(mAppInfo.getDes());
            holder.tvPackageName.setText(mAppInfo.getPackageName());
            holder.tvSize.setText(Formatter.formatFileSize(UIUtil.getContext(), mAppInfo.getSize()));
            holder.rbStar.setRating(mAppInfo.getStars());
            holder.btnDownload.setOnClickListener(this);
            Glide.with(context).load(HttpHelper.URL + "image?name=" + mAppInfo.getIconUrl()).into(holder.ivIcon);
            // 监听下载进度
            manager = DownloadManager.getInstance();
            manager.registerObserver(this);

            DownloadBean downloadInfo = manager.getDownloadInfo(mAppInfo);
            if (downloadInfo == null) {
                // 没有下载过
                mCurrentState = DownloadManager.STATE_NONE;
                mProgress = 0;
            } else {
                // 之前下载过, 以内存中的对象的状态为准
                mCurrentState = downloadInfo.getCurrentState();
                mProgress = downloadInfo.getProgress();
            }

            refreshUI(mProgress, mCurrentState);
        }

        holder.ivIcon.startAnimation(aa);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到首页详情页面
                TextView view1 = view.findViewById(R.id.tv_package);
                Intent intent = new Intent(context, AppDetailActivity.class);
                //将包名传递到详情页
                intent.putExtra("package", view1.getText().toString());
                context.startActivity(intent);
            }
        });
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
                mHolder.btnDownload.normal(0);
                break;
            case DownloadManager.STATE_WAITING:
                mHolder.btnDownload.normal(1);
                break;
            case DownloadManager.STATE_DOWNLOAD:
                mHolder.btnDownload.startDownLoad();
                int pos = (int) (mProgress * 100);
                mHolder.btnDownload.download(pos);
                break;
            case DownloadManager.STATE_PAUSE:
                mHolder.btnDownload.normal(2);
                break;
            case DownloadManager.STATE_ERROR:
                mHolder.btnDownload.normal(3);
                break;
            case DownloadManager.STATE_SUCCESS:
                mHolder.btnDownload.normal(4);
                break;
            default:
                break;
        }
    }

    // 主线程刷新ui
    private void refreshOnMainThread(final DownloadBean info) {
        // 判断要刷新的下载对象是否是当前的应用
        if (info.getId() == mAppInfo.getId()) {
            UIUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshUI(info.getProgress(), info.getCurrentState());
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                // 根据当前状态来决定相关操作
                if (mCurrentState == DownloadManager.STATE_NONE
                        || mCurrentState == DownloadManager.STATE_PAUSE
                        || mCurrentState == DownloadManager.STATE_ERROR) {
                    // 开始下载
                    manager.download(mAppInfo);
                } else if (mCurrentState == DownloadManager.STATE_DOWNLOAD
                        || mCurrentState == DownloadManager.STATE_WAITING) {
                    // 暂停下载
                    manager.pause(mAppInfo);
                } else if (mCurrentState == DownloadManager.STATE_SUCCESS) {
                    // 开始安装
                    manager.install(mAppInfo);
                }
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

    class AppHolder extends RecyclerView.ViewHolder {
        private CardView mView;
        private ImageView ivIcon;
        private TextView tvName;
        private RatingBar rbStar;
        private TextView tvSize;
        private CProgressButton btnDownload;
        private TextView tvDesc;
        private TextView tvPackageName;

        private AppHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.app_card_view);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            rbStar = itemView.findViewById(R.id.rb_star);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            btnDownload = itemView.findViewById(R.id.btn_download);
            tvPackageName = itemView.findViewById(R.id.tv_package);
        }
    }
}
