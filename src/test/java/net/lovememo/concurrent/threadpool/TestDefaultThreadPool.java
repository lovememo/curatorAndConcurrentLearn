package net.lovememo.concurrent.threadpool;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * Author: lovememo
 * Date: 18-6-8
 */
public class TestDefaultThreadPool {
    public static void main(String[] args) throws Throwable {
        DefaultThreadPool<Runnable> threadPool = new DefaultThreadPool(5);
        for(int i=0; i<20; i++) {
            threadPool.execute(()-> {
                try {
                    TimeUnit.SECONDS.sleep(new SecureRandom().nextInt(6));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
