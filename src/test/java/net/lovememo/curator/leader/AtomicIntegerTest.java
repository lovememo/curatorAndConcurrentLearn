package net.lovememo.curator.leader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: lovememo
 * Date: 18-6-5
 */
public class AtomicIntegerTest {
    public static final AtomicInteger atomicInteger = new AtomicInteger(0);
    public static int value = 0;

    public static void main(String[] args) throws InterruptedException {
        long s = System.currentTimeMillis();
        atomicIntegerTest();
        long e = System.currentTimeMillis();
        Thread.sleep(3000);
        System.out.println("automicInteger最终结果是" + atomicInteger.get());
        System.out.println("automicInteger使用时间为：" + (e - s) + " ms");

        s = System.currentTimeMillis();
        normalIntegerTest();
        e = System.currentTimeMillis();
        Thread.sleep(3000);
        System.out.println("normalInteger最终结果是" + value);
        System.out.println("normalInteger使用时间为：" + (e - s) + " ms");
    }

    private static void atomicIntegerTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(10000);
        for (int i = 0; i < 100000; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 4; j++) {
                    atomicInteger.getAndIncrement();
                }
            });
        }
        executorService.shutdown();
    }

    private static void normalIntegerTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(10000);
        for (int i = 0; i < 100000; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 4; j++) {
                    value ++;
                }
            });
        }
        executorService.shutdown();
    }
}
