package com.u2tzjtne.aboutme.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Intent;
import android.net.Uri;

import com.u2tzjtne.aboutme.bean.AppBean;
import com.u2tzjtne.aboutme.bean.DownloadBean;
import com.u2tzjtne.aboutme.http.HttpHelper;
import com.u2tzjtne.aboutme.util.IOUtil;
import com.u2tzjtne.aboutme.util.UIUtil;


/**
 * 下载管理器-单例
 *
 * @author Kevin
 */
public class DownloadManager {

    public static final int STATE_NONE = 0;// 下载未开始
    public static final int STATE_WAITING = 1;// 等待下载
    public static final int STATE_DOWNLOAD = 2;// 正在下载
    public static final int STATE_PAUSE = 3;// 下载暂停
    public static final int STATE_ERROR = 4;// 下载失败
    public static final int STATE_SUCCESS = 5;// 下载成功

    // 所有观察者的集合
    private ArrayList<DownloadObserver> mObservers = new ArrayList<>();

    // 下载对象的集合, ConcurrentHashMap是线程安全的HashMap
    private ConcurrentHashMap<Integer, DownloadBean> mDownloadInfoMap = new ConcurrentHashMap<>();
    // 下载任务集合, ConcurrentHashMap是线程安全的HashMap
    private ConcurrentHashMap<Integer, DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<>();

    private static DownloadManager sInstance = new DownloadManager();

    private DownloadManager() {
    }

    public static DownloadManager getInstance() {
        return sInstance;
    }

    // 2. 注册观察者
    public synchronized void registerObserver(DownloadObserver observer) {
        if (observer != null && !mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    // 3. 注销观察者
    public synchronized void unregisterObserver(DownloadObserver observer) {
        if (observer != null && mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    // 4. 通知下载状态变化
    private synchronized void notifyDownloadStateChanged(DownloadBean info) {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadStateChanged(info);
        }
    }

    // 5. 通知下载进度变化
    private synchronized void notifyDownloadProgressChanged(DownloadBean info) {
        for (DownloadObserver observer : mObservers) {
            observer.onDownloadProgressChanged(info);
        }
    }

    /**
     * 1. 定义下载观察者接口
     */
    public interface DownloadObserver {
        // 下载状态发生变化
        public void onDownloadStateChanged(DownloadBean info);

        // 下载进度发生变化
        public void onDownloadProgressChanged(DownloadBean info);
    }

    /**
     * 开始下载
     */
    public synchronized void download(AppBean appInfo) {
        if (appInfo != null) {
            DownloadBean downloadInfo = mDownloadInfoMap.get(appInfo.getId());
            // 如果downloadInfo不为空,表示之前下载过, 就无需创建新的对象, 要接着原来的下载位置继续下载,也就是断点续传
            if (downloadInfo == null) {// 如果为空,表示第一次下载, 需要创建下载对象, 从头开始下载
                downloadInfo = DownloadBean.copy(appInfo);
            }
            // 下载状态更为正在等待
            downloadInfo.setCurrentState(STATE_WAITING);
            // 通知状态发生变化,各观察者根据此通知更新主界面
            notifyDownloadStateChanged(downloadInfo);

            // 将下载对象保存在集合中
            mDownloadInfoMap.put(appInfo.getId(), downloadInfo);

            // 初始化下载任务
            DownloadTask task = new DownloadTask(downloadInfo);
            // 启动下载任务
            ThreadManager.getThreadPool().execute(task);
            // 将下载任务对象维护在集合当中
            mDownloadTaskMap.put(appInfo.getId(), task);
        }
    }

    /**
     * 下载任务
     */
    class DownloadTask implements Runnable {

        private DownloadBean downloadInfo;
        private HttpHelper.HttpResult httpResult;

        DownloadTask(DownloadBean downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void run() {
            // 更新下载状态
            downloadInfo.setCurrentState(STATE_DOWNLOAD);
            notifyDownloadStateChanged(downloadInfo);

            File file = new File(downloadInfo.getPath());
            if (!file.exists() || file.length() != downloadInfo.getCurrentPos()
                    || downloadInfo.getCurrentPos() == 0) {// 文件不存在, 或者文件长度和对象的长度不一致,
                // 或者对象当前下载位置是0
                file.delete();// 移除无效文件
                downloadInfo.setCurrentPos(0);// 文件当前位置归零
                httpResult = HttpHelper.download(HttpHelper.URL
                        + "download?name=" + downloadInfo.getDownloadUrl());// 从头开始下载文件
            } else {
                System.out.println("---------------------------断点续传--------------" + file.length());
                // 需要断点续传
                httpResult = HttpHelper.download(HttpHelper.URL
                        + "download?name=" + downloadInfo.getDownloadUrl()
                        + "&range=" + file.length());
            }

            InputStream in = null;
            FileOutputStream out = null;
            if (httpResult != null
                    && (in = httpResult.getInputStream()) != null) {
                try {
                    out = new FileOutputStream(file, true);//在源文件基础上追加

                    int len = 0;
                    byte[] buffer = new byte[1024];
                    while ((len = in.read(buffer)) != -1
                            && downloadInfo.getCurrentState() == STATE_DOWNLOAD) {// 只有在下载的状态才读取文件,如果状态变化,就立即停止读写文件
                        out.write(buffer, 0, len);
                        out.flush();

                        downloadInfo.setCurrentPos(downloadInfo.getCurrentPos() + len);// 更新当前文件下载位置
                        notifyDownloadProgressChanged(downloadInfo);// 通知进度更新
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtil.close(in);
                    IOUtil.close(out);
                }

                // 下载结束, 判断文件是否完整
                if (file.length() == downloadInfo.getSize()) {
                    // 下载完毕
                    downloadInfo.setCurrentState(STATE_SUCCESS);
                    notifyDownloadStateChanged(downloadInfo);
                } else if (downloadInfo.getCurrentState() == STATE_PAUSE) {
                    // 中途暂停
                    notifyDownloadStateChanged(downloadInfo);
                } else {
                    // 下载失败
                    downloadInfo.setCurrentState(STATE_ERROR);
                    downloadInfo.setCurrentPos(0);
                    notifyDownloadStateChanged(downloadInfo);
                    // 删除无效文件
                    file.delete();
                }

            } else {
                // 下载失败
                downloadInfo.setCurrentState(STATE_ERROR);
                downloadInfo.setCurrentPos(0);
                notifyDownloadStateChanged(downloadInfo);
                // 删除无效文件
                file.delete();
            }

            // 不管下载成功,失败还是暂停, 下载任务已经结束,都需要从当前任务集合中移除
            mDownloadTaskMap.remove(downloadInfo.getId());
        }

    }

    /**
     * 下载暂停
     */
    public synchronized void pause(AppBean appInfo) {
        if (appInfo != null) {
            DownloadBean downloadInfo = mDownloadInfoMap.get(appInfo.getId());
            if (downloadInfo != null) {
                int state = downloadInfo.getCurrentState();
                // 如果当前状态是等待下载或者正在下载, 需要暂停当前任务
                if (state == STATE_WAITING || state == STATE_DOWNLOAD) {
                    // 停止当前的下载任务
                    DownloadTask downloadTask = mDownloadTaskMap
                            .get(appInfo.getId());
                    if (downloadTask != null) {
                        ThreadManager.getThreadPool().cancel(downloadTask);
                    }
                    // 更新下载状态为暂停
                    downloadInfo.setCurrentState(STATE_PAUSE);
                    notifyDownloadStateChanged(downloadInfo);
                }
            }
        }
    }

    /**
     * 安装apk
     */
    public synchronized void install(AppBean appInfo) {
        DownloadBean downloadInfo = mDownloadInfoMap.get(appInfo.getId());
        if (downloadInfo != null) {
            // 跳到系统的安装页面进行安装
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + downloadInfo.getPath()),
                    "application/vnd.android.package-archive");
            UIUtil.getContext().startActivity(intent);
        }
    }

    // 根据应用对象,获取对应的下载对象
    public DownloadBean getDownloadInfo(AppBean data) {
        if (data != null) {
            return mDownloadInfoMap.get(data.getId());
        }
        return null;
    }
}
