package com.wangdh.survey.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 首先，ArrayBlockingQueueTest 类分别在两个独立的线程中启动了一个 Producer 和 Consumer 。
 * Producer 向一个共享的 BlockingQueue 中注入字符串，而 Consumer 则会从中把它们拿出来。
 */
public class ArrayBlockingQueueTest {
    public static void main(String[] args) {
        /**
         * ArrayBlockingQueue 是有界的阻塞队列，其内部实现是将对象放到一个数组里。
         * 有界也就意味着，它不能够存储无限多数量的元素。
         * 它有一个同一时间存储元素数量的上线。你可以在对其初始化的时候设定这个上限，但之后就无法对这个上限进行修改了。
         * ArrayBlockingQueue 内部以 FIFO(先进先出)的顺序对元素进行存储。
         * 队列中的头元素在所有元素之中是放入时间最久的那个，而尾元素则是最短的那个。
         */
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1024);
        Producer producer = new Producer(queue);
        Consumer consumer = new Consumer(queue);
        new Thread(producer).start();
        new Thread(consumer).start();
    }
}

class Producer implements Runnable {
    private BlockingQueue<String> queue = null;

    Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void run() {
        for (int i = 1;i<=3;i++){
            System.out.println(Thread.currentThread().getName() + ",i=" + i + ",start" );
            queue.add(i + "");
            System.out.println(Thread.currentThread().getName() + ",i=" + i + ",end" );
        }
    }
}

class Consumer implements Runnable {
    private BlockingQueue queue = null;

    Consumer(BlockingQueue queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            Thread.sleep(2000);
            while (!queue.isEmpty()){
                System.out.println(Thread.currentThread().getName() + ",start" );
                System.out.println(Thread.currentThread().getName() + ":" + queue.take());
                System.out.println(Thread.currentThread().getName() + ",end" );
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
