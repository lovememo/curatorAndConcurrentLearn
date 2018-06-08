package net.lovememo.concurrent.threadpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 总体目标是需要一个 worker的pool，每个worker在各自的线程中工作
 * 该pool可以扩大缩小
 * 每个worker就是监控job队列，没工作就在job队列上等待。有工作则获取工作，工作完毕后，再次轮询，是否有工作。
 *
 * 每往job队列上增加工作，就通知在job队列上等待的其他worker线程，让他们停止休息，马上干活。
 *
 */

/**
 * Author: lovememo
 * Date: 18-6-8
 */
public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    private static final int MAX_POOL_SIZE = 10;
    private static final int MIN_POOL_SIZE = 2;
    private static final int DEFAULT_POOL_SIZE = 5;

    private final LinkedList<Job> jobs = new LinkedList<>(new ArrayList());
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList());

    private AtomicLong threadNum = new AtomicLong();
    private int workerCount = 0;

    public DefaultThreadPool() {
        this(DEFAULT_POOL_SIZE);
    }

    public DefaultThreadPool(int size) {
        size  = size < MIN_POOL_SIZE ? MIN_POOL_SIZE : size > MAX_POOL_SIZE ? MAX_POOL_SIZE : size;
        initWorkerThread(size);
    }

    private void initWorkerThread(int size) {
        for(int i=0; i<size; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            new Thread(worker,"ThreadPool worker-" + this.threadNum.incrementAndGet())
                    .start();
            workerCount ++;
        }
    }

    @Override
    public void execute(Job job) throws InterruptedException {
        if(null == job) {
            throw new NullPointerException("job can not be null");
        }
        synchronized (jobs) {
            jobs.addLast(job);
            jobs.notifyAll();
        }
    }

    public void shutdown() throws InterruptedException {
        for(Worker worker : workers) {
            worker.shutdown();
            workerCount --;
        }
    }

    @Override
    public int getWaitedJobSize() {
        return jobs.size();//工作的数量本来就是动态变化的，不需要加锁
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs) { //大家新工作先停一停，下面宣布下重大人事调整
            if (workers.size() + num > MAX_POOL_SIZE) {
                num = MAX_POOL_SIZE - workers.size();
            }
            initWorkerThread(num);
        }
    }

    @Override
    public void removeWorkers(int num) {
        synchronized (jobs) {//大家新工作先停一停，下面宣布下重大人事调整
            int maxNumCanBeRemoved = workers.size() - MIN_POOL_SIZE;
            num = num > maxNumCanBeRemoved ? maxNumCanBeRemoved : num;
            for (int i = 0; i < num; i++) {
                Worker worker = workers.get(i);
                if(workers.remove(worker)) {
                    worker.shutdown();
                }
                workerCount--;
            }
        }
    }

    class Worker implements Runnable {
        private volatile boolean isRunning = true;
        public void shutdown() {
            this.isRunning = false;
        }

        @Override
        public void run() {
            while(isRunning) {
                Job job = null;
                synchronized (jobs) {
                    if(jobs.size() < 1) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            isRunning = false;//被中断了
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                            return;
                        }
                    } else {
                        job = jobs.removeFirst();
                    }
                }
                if(null != job) {
                    try {
                        System.out.println(Thread.currentThread().getName() + ":  job start. ");
                        job.run();
                        System.out.println(Thread.currentThread().getName() + ":  job done. ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
