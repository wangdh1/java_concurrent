package com.wangdh.survey.other;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class Waiter implements Runnable {

    CountDownLatch latch = null;

    public Waiter(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Waiter Released");
    }
}

class Decrementer implements Runnable {

    CountDownLatch latch = null;

    Decrementer(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {

        try {
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() +",count===>>>" + latch.getCount());
            this.latch.countDown();
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() +",count===>>>" + latch.getCount());
            this.latch.countDown();
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() +",count===>>>" + latch.getCount());
            this.latch.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 闭锁
 * java.util.concurrent.CountDownLatch 是一个并发构造，它允许一个或多个线程等待一系列指定操作的完成。
 * CountDownLatch 以一个给定的数量初始化。countDown()每被调用一次，这一数量就建议。通过调用 await()方法之一，线程可以阻塞等待这一数量到达零。
 * 下面是一个简单的示例，Decrementer 三次调用 countDown()之后，等待中的 Waiter 才会从 await()调用中释放出来。
 * 有时候会有这样的需求，多个线程同时工作，然后其中几个可以随意并发执行，但有一个线程需要等其他线程工作结束后，才能开始。
 * 举个例子，开启多个线程分块下载一个大文件，每个线程只下载固定的一截，最后由另外一个线程来拼接所有的分段，那么这时候我们可以考虑使用CountDownLatch来控制并发。
 */
public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        Waiter waiter = new Waiter(latch);
        Decrementer decrementer = new Decrementer(latch);

        new Thread(waiter).start();
        new Thread(decrementer).start();
        boolean await = latch.await(10, TimeUnit.SECONDS);
        System.out.println("main method end...,await=" + await);
    }
}
