package com.u2tzjtne.aboutme.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.takwolf.android.hfrecyclerview.HeaderAndFooterRecyclerView;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.bean.MomentsBean;
import com.u2tzjtne.aboutme.http.HttpHelper;
import com.u2tzjtne.aboutme.ui.adapter.MomentsAdapter;
import com.u2tzjtne.aboutme.ui.view.LoadMoreFooter;
import com.u2tzjtne.aboutme.util.UIUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JK on 2017/11/30.
 * 动态 页面
 */

public class MomentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreFooter.OnLoadMoreListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private HeaderAndFooterRecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private MomentsAdapter adapter;
    private Context context;
    private LoadMoreFooter loadMoreFooter;

    public static final String TAG = "MomentsFragment:";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moments, null);
        initView(view);
        return view;
    }

    //初始化View
    private void initView(View view) {
        context = getActivity();
        mRecyclerView = view.findViewById(R.id.recycler_view_recycler_view);
        //宽屏时切换为卡片布局
        if (getScreenWidthDp() >= 1200) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else if (getScreenWidthDp() >= 800) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
        adapter = new MomentsAdapter(context);
        loadMoreFooter = new LoadMoreFooter(context, mRecyclerView, this); // 初始化加载更多
        mRecyclerView.setAdapter(adapter);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_recycler_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue, R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(this);
        onRefresh();
    }

    //刷新数据
    @Override
    public void onRefresh() {
        String index = String.valueOf(0);
        OkHttpClient client = new OkHttpClient();
        //get请求后面追加共同的参数
        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpHelper.URL + "moments").newBuilder();
        urlBuilder.addQueryParameter("index", index);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                UIUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreFooter.setState(LoadMoreFooter.STATE_FAILED);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final ArrayList<MomentsBean> data = parseJson(response.body().string());
                adapter.setItems(data);
                UIUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        if (data.size() < 20) {
                            loadMoreFooter.setState(LoadMoreFooter.STATE_NO_MORE);
                        } else {
                            loadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    //加载更多
    @Override
    public void onLoadMore() {
        String index = String.valueOf(adapter.getItemCount());
        OkHttpClient client = new OkHttpClient();
        //get请求后面追加共同的参数
        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpHelper.URL + "moments").newBuilder();
        urlBuilder.addQueryParameter("index", index);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                UIUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreFooter.setState(LoadMoreFooter.STATE_FAILED);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ArrayList<MomentsBean> data = parseJson(response.body().string());
                final int startPos = adapter.getItemCount();
                final int endPos = data.size();
                adapter.addItems(data);
                UIUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemRangeChanged(startPos, endPos);
                        if (endPos < 20) {
                            loadMoreFooter.setState(LoadMoreFooter.STATE_NO_MORE);
                        } else {
                            loadMoreFooter.setState(LoadMoreFooter.STATE_ENDLESS);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    //解析JSON
    private ArrayList<MomentsBean> parseJson(String json) {
        try {
            Type type = new TypeToken<ArrayList<MomentsBean>>() {
            }.getType();
            return new Gson().fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("res:", requestCode + "");
    }

    //获取屏幕宽度
    private int getScreenWidthDp() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }

}
