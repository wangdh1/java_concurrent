package com.wangdh.survey.other.false_sharing;

import java.util.stream.IntStream;

/**
 * 伪共享测试
 */
public class FalseSharingTest {
    public static void main(String[] args) throws InterruptedException {
        test1(new Destination());
        test2(new Pointer());
    }

    private static void test1(Destination destination) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(() -> {
            IntStream.range(0, 100000000).forEach(i -> destination.x++);
        });
        Thread t2 = new Thread(() -> {
            IntStream.range(0, 100000000).forEach(i -> destination.y++);
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("t1 cost time:" + (System.currentTimeMillis() - start));
    }

    private static void test2(Pointer pointer) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(() -> {
            IntStream.range(0, 100000000).forEach(i -> pointer.x.value++);
        });
        Thread t2 = new Thread(() -> {
            IntStream.range(0, 100000000).forEach(i -> pointer.y.value++);
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("t2 cost time:" + (System.currentTimeMillis() - start));
    }
}
class Destination{
    volatile long x;
    long p1,p2,p3,p4,p5,p6,p7;
    volatile long y;
}

class Pointer{
    MyLong x = new MyLong();
    MyLong y = new MyLong();
}

/**
 * 默认使用@sun.misc.Contended这个注解是无效的，需要在JVM启动参数加上 -XX:-RestrictContended才会生效
 * ConcurrentHashMap 中的CounterCell使用了这注解
 */
@sun.misc.Contended
class MyLong{
    volatile long value;
}


