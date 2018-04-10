package com.u2tzjtne.aboutme.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.u2tzjtne.aboutme.ui.view.LoadingPage;
import com.u2tzjtne.aboutme.util.UIUtil;

import java.util.List;

/**
 * Created by JK on 2017/11/15.
 */

public abstract class BaseFragment extends Fragment {
    public abstract boolean onBackPressed();

    public BackHandledInterface mBackHandledInterface;
    private LoadingPage mLoadingPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandledInterface)) {
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        } else {
            this.mBackHandledInterface = (BackHandledInterface) getActivity();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLoadingPage = new LoadingPage(UIUtil.getContext()) {

            @Override
            public View onCreateSuccessView() {
                return BaseFragment.this.onCreateSuccessView();
            }

            @Override
            public ResultState onLoad() {
                return BaseFragment.this.onLoad();
            }

        };
        return mLoadingPage;
    }


    // 由子类实现创建布局的方法
    public abstract View onCreateSuccessView();

    // 由子类实现加载网络数据的逻辑, 该方法运行在子线程
    public abstract LoadingPage.ResultState onLoad();

    // 开始加载网络数据
    public void loadData() {
        if (mLoadingPage != null) {
            mLoadingPage.loadData();
        }
    }

    /**
     * 校验数据的合法性,返回相应的状态
     *
     * @param data
     * @return
     */
    public LoadingPage.ResultState check(Object data) {
        if (data != null) {
            if (data instanceof List) {//判断当前对象是否是一个集合
                List list = (List) data;
                if (!list.isEmpty()) {//数据不为空,访问成功
                    return LoadingPage.ResultState.STATE_SUCCESS;
                } else {//空集合
                    return LoadingPage.ResultState.STATE_EMPTY;
                }
            }
        }
        return LoadingPage.ResultState.STATE_ERROR;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBackHandledInterface.setSelectedFragment(this);
    }

    //TODO  返回键
    public interface BackHandledInterface {
        void setSelectedFragment(BaseFragment selectedFragment);
    }
}