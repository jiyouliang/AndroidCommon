package com.jyl.filedownload;

import java.util.Set;

/**
 * 下载任务被观察者抽象接口
 */
public interface ITaskSubject {
    /**
     * 添加/注册观察者
     * @param o
     */
    void addObserver(ITaskObserver o);

    /**
     * 删除/注销观察者
     * @param o
     */
    void removeObserver(ITaskObserver o);

    /**
     * 通知所有观察者
     *
     * @param url
     */
    void notifyObservers(Set<String> url);
}
