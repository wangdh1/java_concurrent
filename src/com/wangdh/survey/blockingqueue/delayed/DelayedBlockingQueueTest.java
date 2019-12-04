package com.wangdh.survey.blockingqueue.delayed;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * DelayQueue 将会在每个元素的 getDelay()方法返回的值的时间段之后才释放掉该元素。
 * 如果返回的是 0 或者负值，延迟将被认为过期，该元素将会在 DelayQueue 的下一次 take 被调用的时候被释放掉。
 * 可以看到 Delayed 接口继承了 Comparable 接口，这也就意味着 Delayed 对象之间可以进行对比。
 * 这个可能在对 DelayeQueue 队列中的元素进行排序时有用，因此它们可以根据过期时间进行有序释放。
 */
public class DelayedBlockingQueueTest {
    public static void main(String[] args) throws InterruptedException {
        DelayQueue<DelayedElement> queue = new DelayQueue<>();
        DelayedElement element1 = new DelayedElement(50);
        DelayedElement element2 = new DelayedElement(0);
        DelayedElement element3 = new DelayedElement(100);
        queue.put(element1);
        queue.put(element2);
        queue.put(element3);
        DelayedElement e = queue.take();
        System.out.println("e1:" + e.delayTime);
        DelayedElement e2 = queue.take();
        System.out.println("e2:" + e2.delayTime);
        DelayedElement e3 = queue.take();
        System.out.println("e3:" + e3.delayTime);
        System.out.println("end...");
    }
}

/**
 * DelayQueue 对元素进行持有知道一个特定的延迟到期。注入其中的元素必须实现 concurrent.Delay 接口
 */
class DelayedElement implements Delayed {
    long delayTime;
    long tamp;

    DelayedElement(long delay) {
        delayTime = delay;
        tamp = delay + System.currentTimeMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return tamp - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        return tamp - ((DelayedElement) o).tamp > 0 ? 1 : -1;
    }
}