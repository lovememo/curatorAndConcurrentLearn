package net.lovememo.concurrent;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * Author: lovememo
 * Date: 18-6-7
 */
public class WaitTime {
    private static Boolean breadCooked = false;

    public static void main(String[] args) throws InterruptedException {
        HungryPerson peter = new HungryPerson(2000, "peter");
        Thread peterThread = new Thread(peter, "Peter");

        Thread cook = new Thread(new Cook(peter), "cookForPeter");
        peterThread.start();
        cook.start();
        peterThread.join();
        cook.join();
        breadCooked = false;

        System.out.println("peter is done....");
        System.out.println("\n\n\n\n-----------------\n\n\n");
        //--------------------------------------------
        HungryPerson linda = new HungryPerson(9992000, "linda");
        Thread lindaThread = new Thread(linda, "Linda");
        cook = new Thread(new Cook(linda), "cookForLinda");
        lindaThread.start();
        cook.start();

        lindaThread.join();
        cook.join();
        System.out.println("linda is done");
    }



    static class HungryPerson implements Runnable {
        private long waitMills = 3000L;
        private String name;

        public long getWaitMills() {
            return waitMills;
        }

        public void setWaitMills(long waitMills) {
            this.waitMills = waitMills;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public HungryPerson(long waitMills, String name) {
            this.waitMills = waitMills;
            this.name = name;
        }

        public Boolean waitBreadIsOk(long waitMills) throws InterruptedException {
            long remaining = waitMills;
            long future = System.currentTimeMillis() + waitMills;
            synchronized (this) {
                if (!breadCooked && remaining > 0) {
                    this.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                    if(remaining <= 0) {
                        System.out.println(this.getName() + " said i had waited for " + (waitMills / 1000) +" second, can not wait any more!!!");
                    }
                }
            }
            return breadCooked;
        }

        @Override
        public void run() {
            try {
                boolean notDead = waitBreadIsOk(waitMills);
                if(notDead) {
                    System.out.println(this.getName() + " said bread is delicious");
                } else {
                    System.out.println(this.getName() + " said i'm starved to death");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Cook implements Runnable {
        HungryPerson person;
        public Cook(HungryPerson person) {
            this.person = person;
        }
        @Override
        public void run() {
            try {
                System.out.println("cook start cook bread");
                TimeUnit.MILLISECONDS.sleep(5000);
                System.out.println("cook said bread is cooked");
                synchronized (person) {
                    breadCooked = true;
                    this.person.notify();
                    System.out.println("cook notified " + person.getName());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
