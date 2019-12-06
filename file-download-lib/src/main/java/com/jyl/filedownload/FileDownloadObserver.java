package com.jyl.filedownload;

import android.util.Log;

import com.liulishuo.okdownload.DownloadContext;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.listener.DownloadListener1;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件下载任务 <br/>
 * 该类依赖：com.liulishuo.okdownload:okdownload:1.0.6
 *
 */
public class FileDownloadObserver implements ITaskObserver {

    private static FileDownloadObserver instance;
    private DownloadListener1 mListener;
    private DownloadContext.QueueSet mQueueSet;
    private static final String TAG = "MyFileDownloadTask";
    /**
     * 存储所有请求url
     */
    private final Set<String> mTotalTaskUrls = new HashSet<>();
    private DownloadContext.Builder builder;
    private DownloadContext mDownloadContext;

    private FileDownloadObserver() {}

    public static FileDownloadObserver getInstance() {
        if(instance == null){
            synchronized (FileDownloadObserver.class){
                instance = new FileDownloadObserver();
            }
        }
        return instance;
    }

    public void init(String downloadDir){
        mQueueSet = new DownloadContext.QueueSet();
        File f = new File(downloadDir);
        if (!f.exists()) {
            boolean mkdirs = f.mkdirs();
        }
        mQueueSet.setParentPath(f.getAbsolutePath());
        mQueueSet.setMinIntervalMillisCallbackProcess(200);


    }


    /**
     * 接收到新任务通知
     * @param urls 新的下载任务url
     */
    @Override
    public void notify(Set<String> urls) {
        showLog("notify:url="+urls);

        showLog("开启下载任务");
        builder = mQueueSet.commit();
        for(String url : urls){
            mTotalTaskUrls.add(url);
            DownloadTask task = builder.bind(url);
            task.setTag(url);
        }

        mDownloadContext = builder.build();
        if(mListener != null){
            mDownloadContext.startOnParallel(mListener);
        }

    }

    /**
     * 停止下载
     */
    public void stop(){
        mDownloadContext.stop();
    }

    /**
     * 设置下载回调监听
     * @param listener
     */
    public void setListener(DownloadListener1 listener){
        this.mListener = listener;
    }

    private void showLog(String msg){
        Log.d(TAG, msg);
    }
}
