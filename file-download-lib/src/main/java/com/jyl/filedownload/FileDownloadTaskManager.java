package com.jyl.filedownload;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 文件下载任务管理器
 */
public class FileDownloadTaskManager extends DownloadListener1 implements ITaskSubject {

    private static final String TAG = "FileDownloadTaskManager";

    /**
     * 观察者列表
     */
    private final Set<ITaskObserver> mObserverSet = new HashSet<>();

    private static FileDownloadTaskManager instance;

    /**
     * 最大并行执行任务数
     */
    private static final int MAX_PARALLEL_TASK_SIZE = 5;

    /**
     * 存储所有任务url
     */
    private final Set<String> mTasksSet = new HashSet<>();

    /**
     * 总进度
     */
    private long mTotalProgress;

    /**
     * 当前进度
     */
    private long mCurrProgress;


    /**
     * 已经链接的队列，用于计算总进度
     */
    private final Map<String, Long> mConnectedMap = new HashMap<>();

    /**
     * 下载的任务队列
     */
    private final Map<String, Long> mDownloadingMap = new HashMap<>();

    private FileDownloadObserver mFileDownloadObserver;

    private OnDownloadListener mDownloadListener;


    private FileDownloadTaskManager() {
    }

    public static FileDownloadTaskManager getInstance() {
        if (instance == null) {
            synchronized (FileDownloadTaskManager.class) {
                instance = new FileDownloadTaskManager();
            }
        }
        return instance;
    }

    @Override
    public void addObserver(ITaskObserver o) {
        if (mObserverSet == null) {
            return;
        }
        mObserverSet.add(o);
    }

    /**
     * 初始化
     *
     * @param downloadDir 下载缓存路径
     */
    public void init(String downloadDir) {
        showLog("init:downloadDir=" + downloadDir);
        mFileDownloadObserver = FileDownloadObserver.getInstance();
        mFileDownloadObserver.init(downloadDir);
        mFileDownloadObserver.setListener(this);

        // 添加观察者
        addObserver(mFileDownloadObserver);
        // 设置最大并行下载数
        DownloadDispatcher.setMaxParallelRunningCount(MAX_PARALLEL_TASK_SIZE);
    }

    /**
     * 添加新任务
     *
     * @param url
     */
    public void addTask(String url) {
        // 通知任务执行观察者（也就是具体下载任务执行者）
        if (mTasksSet.contains(url)) {
            return;
        }
        Set<String> set = new HashSet<>();
        set.add(url);
        mTasksSet.add(url);
        notifyObservers(set);
    }

    /**
     * 添加并开始执行任务
     *
     * @param urls
     */
    public void addAllTasks(Set<String> urls) {

        Set<String> set = filterTasks(urls);
        if (set == null) return;
        mTasksSet.addAll(set);
        notifyObservers(set);
    }

    /**
     * 过滤重复任务
     *
     * @param urls
     * @return
     */
    private Set<String> filterTasks(Set<String> urls) {
        Set<String> set = new HashSet<>();
        for (String url : urls) {
            if (mTasksSet.contains(url)) {
                showLog("mAllTasks存在url,不重复添加:" + url);
                continue;
            }
            set.add(url);
        }
        if (set.size() == 0) {
            return null;
        }
        return set;
    }

    /**
     * 停止任务
     */
    public void stop() {
        mFileDownloadObserver.stop();
    }

    @Override
    public void removeObserver(ITaskObserver o) {
        if (mObserverSet == null) {
            return;
        }
        mObserverSet.remove(o);
    }

    @Override
    public void notifyObservers(Set<String> url) {
        if (mObserverSet == null) {
            return;
        }
        for (ITaskObserver o : mObserverSet) {
            o.notify(url);
        }
    }

    public void setOnDownloadListener(OnDownloadListener listener) {
        this.mDownloadListener = listener;
    }


    //***********************************下载回调***********************************
    @Override
    public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
        showLog("taskStart");
        if (mDownloadListener != null) {
            mDownloadListener.onStart(task.getUrl());
        }
    }

    @Override
    public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
        showLog("retry");

    }

    @Override
    public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
        showLog("connected:fileName=" + task.getFilename()+",total="+totalLength);
        processConnected(task, blockCount, currentOffset, totalLength);
    }

    @Override
    public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
        showLog("progress:fileName=" + task.getFilename() + ",current=" + currentOffset + ",total=" + totalLength);
        processProgress(task, currentOffset, totalLength);
    }

    @Override
    public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
        showLog("taskEnd:fileName=" + task.getFilename() + "，cause=" + cause.name());
        processTaskEnd(task, cause, realCause);
    }

    /**
     * 处理下载链接开始
     *
     * @param task
     * @param blockCount
     * @param currentOffset
     * @param totalLength
     */
    private void processConnected(DownloadTask task, int blockCount, long currentOffset, long totalLength) {
        String url = task.getUrl();

        mConnectedMap.put(url, totalLength);
        // 计算总进度
        mTotalProgress = 0;
        for(Map.Entry<String, Long> entry : mConnectedMap.entrySet()){
            mTotalProgress += entry.getValue();
        }
        showLog("processConnected:mTotalProgress="+mTotalProgress);
        if (mDownloadListener != null) {
            mDownloadListener.onTotalProgress(mConnectedMap.keySet(), mDownloadingMap.keySet(), mCurrProgress, mTotalProgress);
        }
    }

    /**
     * 处理进度
     * @param task
     * @param currentOffset
     * @param totalLength
     */
    private void processProgress(DownloadTask task, long currentOffset, long totalLength) {
        showLog("processProgress:fileName="+task.getFilename()+",current="+currentOffset+",total="+totalLength);
        String url = task.getUrl();
        mDownloadingMap.put(url, currentOffset);
        if(mDownloadListener == null){
            return;
        }
        // 计算单个进度
        mDownloadListener.onProgress(url, currentOffset, totalLength);

        mCurrProgress = 0;
        // 计算总进度的当前进度
        for (Map.Entry<String, Long> entry : mDownloadingMap.entrySet()) {
            mCurrProgress += entry.getValue();
        }
        mDownloadListener.onTotalProgress(mConnectedMap.keySet(), mDownloadingMap.keySet(), mCurrProgress, mTotalProgress);
    }


    /**
     * 处理下载任务结束回调
     *
     * @param task
     * @param cause
     * @param realCause
     */
    private void processTaskEnd(DownloadTask task, EndCause cause, Exception realCause) {
        if(mDownloadListener == null){
            return;
        }
        String url = task.getUrl();
        if(cause == EndCause.COMPLETED){
            if(mCurrProgress == mTotalProgress){
                mDownloadListener.onComplete(mConnectedMap.keySet(), mCurrProgress, mTotalProgress);
            }
        }else if(cause == EndCause.ERROR){
            mDownloadListener.onError(url, realCause, cause.name());
        }


    }
    //***********************************下载回调结束***********************************

    /**
     * 下载回调监听
     */
    public interface OnDownloadListener {
        /**
         * 开始下载，如果多个任务，则每个任务开始都回调该方法
         *
         * @param url
         */
        void onStart(String url);

        /**
         * 总进度回调
         *
         * @param allUrls     所有任务集合
         * @param currUrls 当前任务集合
         * @param progress 当前进度
         * @param total    总进度
         */
        void onTotalProgress(Set<String> allUrls, Set<String> currUrls, long progress, long total);

        /**
         * 单个任务进度回调
         *
         * @param url
         * @param progress
         * @param total
         */
        void onProgress(String url, long progress, long total);

        /**
         * 所有下载任务完成
         */
        void onComplete(Set<String> url, long progress, long total);

        /**
         * 下载发送错误
         *
         * @param url
         * @param e      异常
         * @param errMsg 错误信息
         */
        void onError(String url, Exception e, String errMsg);
    }

    private void showLog(String msg) {
        Log.d(TAG, msg);
    }


}
