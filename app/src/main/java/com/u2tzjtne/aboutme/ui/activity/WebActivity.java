package com.u2tzjtne.aboutme.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.u2tzjtne.aboutme.MyApplication;
import com.u2tzjtne.aboutme.R;
import com.u2tzjtne.aboutme.constant.Constant;
import com.u2tzjtne.aboutme.util.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.loading_progress)
    ProgressBar loadingProgress;
    @BindView(R.id.loading_layout)
    FrameLayout loadingLayout;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        refresh.setColorSchemeResources(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow);
        refresh.setOnRefreshListener(this);
        initWebView();
    }

    private void initWebView() {
        WebSettings webSettings = webview.getSettings();
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
        Intent intent = getIntent();
        String url,title;
        if (intent != null) {
            url = intent.getStringExtra(Constant.WEB_VIEW_URL);
            title = intent.getStringExtra("title");
        } else {
            url = Constant.BLOG_URL;
            title = "AboutMe";
        }
        setTitle(title);
        webview.loadUrl(url);
        //在本WebView中显示
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                loadingLayout.setVisibility(View.VISIBLE);
                loadingProgress.setProgress(newProgress);
                if (newProgress == 100) {
                    refresh.setRefreshing(false);
                    loadingLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_web_close_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (webview != null && webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.action_close:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (webview != null) {
            webview.reload();
        }
    }
}
