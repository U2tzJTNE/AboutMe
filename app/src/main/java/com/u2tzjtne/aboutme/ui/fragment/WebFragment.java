package com.u2tzjtne.aboutme.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.u2tzjtne.aboutme.MyApplication;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.ui.view.LoadingPage;
import com.u2tzjtne.aboutme.util.CommonUtil;
import com.u2tzjtne.aboutme.constant.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by JK on 2017/11/21.
 */

public class WebFragment extends BaseFragment {

    @BindView(R.id.loading_progress)
    ProgressBar loadingProgress;
    @BindView(R.id.loading_layout)
    FrameLayout loadingLayout;
    @BindView(R.id.web_view)
    WebView webView;
    Unbinder unbinder;


    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//支持JS
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setSupportZoom(false); //禁用缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        if (CommonUtil.isNetworkConnected(MyApplication.getContext())) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
        }
        //默认加载博客页面
        String url = getArguments() != null ? getArguments().getString(Constant.WEB_VIEW_URL) : Constant.BLOG_URL;
        webView.loadUrl(url);
        //在本WebView中显示
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                loadingLayout.setVisibility(View.VISIBLE);
                loadingProgress.setProgress(newProgress);
                if (newProgress == 100) {
                    loadingLayout.setVisibility(View.GONE);
                }
            }
        });
        return rootView;
    }

    @Override
    public View onCreateSuccessView() {
        return null;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        //当页面被失去焦点被切换到后台不可见状态，需要执行onPause
        //通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行
        //webView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //销毁Webview
        //在关闭了Activity时，如果Webview的音乐或视频，还在播放。就必须销毁Webview
        //但是注意：webview调用destory时,webview仍绑定在Activity上
        //这是由于自定义webview构建时传入了该Activity的context对象
        //因此需要先从父容器中移除webview,然后再销毁webview
        //在 Activity 销毁（ WebView ）的时候，先让 WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
