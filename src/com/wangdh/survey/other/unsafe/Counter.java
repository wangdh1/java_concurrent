package com.wangdh.survey.other.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * JUC下面大量使用了CAS操作，它们的底层是调用的Unsafe的CompareAndSwapXXX()方法。
 * 这种方式广泛运用于无锁算法，与java中标准的悲观锁机制相比，它可以利用CAS处理器指令提供极大的加速。
 * 比如，我们可以基于Unsafe的compareAndSwapInt()方法构建线程安全的计数器。
 * 参考：
 * 1.https://mp.weixin.qq.com/s?__biz=Mzg2ODA0ODM0Nw==&mid=2247483866&idx=1&sn=941ef04260f8afea009761f108726211&scene=21#wechat_redirect
 * 2.https://blog.csdn.net/sherld/article/details/42492259
 */
public class Counter {
    private volatile int count = 0;
    private static long offset;
    private static Unsafe unsafe;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe)f.get(null);
            offset = unsafe.objectFieldOffset(Counter.class.getDeclaredField("count"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void increment(){
        int before = count;
        // 失败了就重试直到成功为止
        while (!unsafe.compareAndSwapInt(this,offset,before,before + 1)){
            before = count;
        }
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        // 启100个线程，每个线程自增10000次
        IntStream.range(0, 100).forEach(i -> threadPool.submit(() ->
            IntStream.range(0, 10000).forEach(j -> counter.increment())
        ));

        threadPool.shutdown();
        Thread.sleep(2000);
        // 打印1000000
        System.out. println(counter.getCount());
    }
}
