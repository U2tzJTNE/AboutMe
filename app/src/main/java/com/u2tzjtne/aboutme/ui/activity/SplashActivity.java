package com.u2tzjtne.aboutme.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.u2tzjtne.aboutme.bean.AppUpdateBean;
import com.u2tzjtne.aboutme.constant.Constant;
import com.u2tzjtne.aboutme.http.HttpHelper;
import com.u2tzjtne.aboutme.http.Interceptor.ProgressInterceptor;
import com.u2tzjtne.aboutme.http.ProgressListener;
import com.u2tzjtne.aboutme.util.LogUtil;
import com.u2tzjtne.aboutme.util.SPUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SplashActivity extends AppCompatActivity {

    private long startTimeMillis;// 记录开始访问网络的时间

    //下载进度
    private ProgressDialog progressDialog;
    private AppUpdateBean bean;

    private static final int LOAD_MAIN = 1;// 加载主界面
    private static final int SHOW_UPDATE_DIALOG = 2;// 显示是否更新的对话框
    protected static final int JSON_ERROR = 3;// 错误统一代号
    protected static final int GET_VERSION_ERROR = 4;// 错误统一代号
    protected static final int REQUEST_ERROR = 5;// 错误统一代号
    protected static final int DATA_ERROR = 6;// 错误统一代号

    private Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                // 处理消息
                switch (msg.what) {
                    case LOAD_MAIN:// 加载主界面
                        loadMain();
                        break;
                    case JSON_ERROR:// JSON异常
                        Toast.makeText(SplashActivity.this, "JSON解析异常", Toast.LENGTH_SHORT).show();
                        loadMain();// 进入主界面
                        break;
                    case GET_VERSION_ERROR:// 获取本地版本号异常
                        Toast.makeText(SplashActivity.this, "获取当前版本号异常", Toast.LENGTH_SHORT).show();
                        loadMain();// 进入主界面
                        break;
                    case REQUEST_ERROR:// JSON异常
                        Toast.makeText(SplashActivity.this, "网络请求异常", Toast.LENGTH_SHORT).show();
                        loadMain();// 进入主界面
                        break;
                    case DATA_ERROR:// JSON异常
                        Toast.makeText(SplashActivity.this, "返回数据异常", Toast.LENGTH_SHORT).show();
                        loadMain();// 进入主界面
                        break;
                    case SHOW_UPDATE_DIALOG:// 显示更新版本的对话框
                        showUpDialog(bean.isIsForcedUpdate());
                        break;
                    default:
                        break;
                }
            }
        };
        //是否检查更新 默认检查更新
        if (SPUtil.getBoolean(Constant.IS_CHECK_UPDATE, true)) {
            checkUpdate();//检查更新
        } else {
            loadMain();
        }

    }

    //获取当前版本号
    private int getCurrentVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    /**
     * 检查更新
     */
    protected void checkUpdate() {

        String url = HttpHelper.URL + "updateinfo.html";
        startTimeMillis = System.currentTimeMillis();//开始连接时间
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                handler.sendEmptyMessage(LOAD_MAIN);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                if (responseData != null) {
                    //解析JSON
                    bean = new Gson().fromJson(responseData, AppUpdateBean.class);
                    if (bean != null) {
                        int currentVersion = getCurrentVersion();
                        if (currentVersion != 0) {
                            // 时间不超过2秒，补足2秒
                            long endTime = System.currentTimeMillis();
                            if (endTime - startTimeMillis < 1000) {
                                SystemClock.sleep(1000 - (endTime - startTimeMillis));
                            }
                            if (bean.getVersionCode() > currentVersion) {
                                //显示更新dialog
                                handler.sendEmptyMessage(SHOW_UPDATE_DIALOG);
                            } else {
                                handler.sendEmptyMessage(LOAD_MAIN);
                            }
                        } else {
                            handler.sendEmptyMessage(GET_VERSION_ERROR);
                        }
                    } else {
                        handler.sendEmptyMessage(JSON_ERROR);
                    }
                } else {
                    handler.sendEmptyMessage(DATA_ERROR);
                }
            }
        });

    }

    //显示更新dialog
    protected void showUpDialog(boolean isForcedUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("发现新版本：" + bean.getVersionCode());
        builder.setMessage(bean.getDes());
        builder.setCancelable(false);
        //强制更新
        if (isForcedUpdate) {
            builder.setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                    System.exit(0);
                }
            });
            builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //直接在当前界面下载
                    downloadAPKWithProgressDialog();
                }
            });
        } else {
            builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    loadMain();
                }
            });
            builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SplashActivityPermissionsDispatcher.downloadAPKWithPermissionCheck(SplashActivity.this, bean.isIsForcedUpdate());
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void downloadAPK(boolean isForcedUpdate) {
        if (isForcedUpdate) {
            downloadAPKWithProgressDialog();
        } else {
            downloadAPkWithDownloadManager();
        }
    }

    /**
     * 向用户解释为什么需要调用该权限
     * <p>
     * 只有当第一次请求权限被用户拒绝，下次请求权限之前会调用
     */
    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationale(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("应用更新需要您提供存储权限")
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                }).show();
    }

    /* *
     *  当用户拒绝权限申请
     * */
    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onPermissionDenied() {
        if (bean.isIsForcedUpdate()) {
            finish();
        } else {
            loadMain();
        }
    }

    /*
     * 当用户选中了授权窗口中的不再询问复选框后并拒绝了权限请求时需要调用的方法
     * */
    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAsk() {
        showPermissionMessage("抱歉，您没有打开外部存储权限！", true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    /**
     * 没打开权限
     * 跳转到app设置页面
     */
    public void showPermissionMessage(String message, final boolean isFinish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("去打开", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                try {
                    startActivity(localIntent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alert = builder.create();
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isFinish) {
                    finish();
                }
            }
        });
        alert.show();
    }

    private void downloadAPKWithProgressDialog() {
        final String url = bean.getApkUrl();//下载地址
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载");
        progressDialog.show();
        ProgressInterceptor.addListener(url, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                progressDialog.setProgress(progress);
            }
        });

        final String path = Environment.getExternalStorageDirectory().getPath() + "/AboutMe.apk";
        LogUtil.d(path);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ProgressInterceptor()).build();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(e.toString());
                progressDialog.dismiss();
                ProgressInterceptor.removeListener(url);
                //出现异常  进入主页
                loadMain();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(new File(path));
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    LogUtil.i(e.toString());
                    e.printStackTrace();
                    progressDialog.dismiss();
                    ProgressInterceptor.removeListener(url);
                    //出现异常  进入主页
                    loadMain();
                }
                progressDialog.dismiss();
                ProgressInterceptor.removeListener(url);
                //安装apk
                installAPK(path);
            }
        });

    }

    //安装apk
    private void installAPK(String path) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void downloadAPkWithDownloadManager() {
        // uri 是你的下载地址，可以使用Uri.parse("http://")包装成Uri对象
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(bean.getApkUrl()));

        // 通过setAllowedNetworkTypes方法可以设置允许在何种网络下下载，
        // 也可以使用setAllowedOverRoaming方法，它更加灵活
        //req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        req.setAllowedOverRoaming(true);
        // 此方法表示在下载过程中通知栏会一直显示该下载，在下载完成后仍然会显示，
        // 直到用户点击该通知或者消除该通知。还有其他参数可供选择
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 设置下载文件存放的路径，同样你可以选择以下方法存放在你想要的位置。
        // setDestinationUri
        // setDestinationInExternalPublicDir
        req.setDestinationInExternalFilesDir(SplashActivity.this,
                Environment.getExternalStorageDirectory().getPath(), "AboutMe.apk");

        // 设置一些基本显示信息
        req.setTitle("AboutMe.apk");
        req.setDescription("下载完后请点击打开");
        req.setMimeType("application/vnd.android.package-archive");

        // Ok go!
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm != null) {
            dm.enqueue(req);
        }
        loadMain();
    }

    //屏蔽返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    /**
     * 跳转到主页面
     */
    private void loadMain() {
        Intent intent;
        if (SPUtil.getBoolean(Constant.FIRST_LAUNCH, true)) {
            intent = new Intent(SplashActivity.this, GuideActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
