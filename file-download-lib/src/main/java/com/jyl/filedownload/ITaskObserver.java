package com.jyl.filedownload;

import java.util.Set;

/**
 * 观察者抽血接口
 */
public interface ITaskObserver {

    /**
     * 通知回调
     * @param url 新的下载任务队列
     */
    void notify(Set<String> url);
}
