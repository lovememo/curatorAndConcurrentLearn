package net.lovememo.concurrent.threadpool;

/**
 * Author: lovememo
 * Date: 18-6-8
 */
public interface ThreadPool<Job extends Runnable> {
    void execute(Job job) throws InterruptedException;
    void shutdown() throws InterruptedException;
    int getWaitedJobSize();
    void addWorkers(int num);
    void removeWorkers(int num);

}
