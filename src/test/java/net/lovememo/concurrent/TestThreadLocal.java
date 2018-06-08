package net.lovememo.concurrent;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: lovememo
 * Date: 18-6-7
 */
public class TestThreadLocal {
    public static void main(String[] args) throws InterruptedException {
        NoThreadLocalCount noThreadLocalCount = new NoThreadLocalCount();
        ThreadLocalCount threadLocalCount = new ThreadLocalCount();
        List<Thread> threadList = new ArrayList<Thread>();
        for(int i=0; i<100; i++) {
            Thread thread = new Thread(() -> {
                System.out.println(noThreadLocalCount.increaseCount());
            });
            threadList.add(thread);
            thread.start();
        }

        for(Thread thread : threadList) {
            thread.join();
        }
        System.out.println("-------------done------------");
        threadList.clear();
        for(int i=0; i<100; i++) {
            Thread thread = new Thread(() -> {
                System.out.println(threadLocalCount.increaseCount());
            });
            threadList.add(thread);
            thread.start();
        }

        for(Thread thread : threadList) {
            thread.join();
        }
    }

    interface Count {
        int increaseCount();
    }
    static class NoThreadLocalCount implements Count {
        private int count = 0;
        @Override
        public int increaseCount() {
            return count ++;
        }
    }
    static class ThreadLocalCount implements Count {
        private ThreadLocal<Integer> count = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return 0;
            }
        };

        @Override
        public int increaseCount() {
            int value = this.count.get();
            this.count.set(this.count.get() + 1);
            return value;
        }
    }
}
