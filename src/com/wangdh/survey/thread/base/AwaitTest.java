package com.wangdh.survey.thread.base;

import java.util.concurrent.CountDownLatch;

/**
 * wait方法和Notify方法写在实体类对象中
 * wait代表退出当前线程对某实体对象的操作，释放锁暂时退出当前线程的操作，由其他线程调用该对象的notify方法来释放前一个线程，继续对该对象进行操作
 */
public class AwaitTest {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        NumberHolder numberHolder = new NumberHolder();

        Thread t1 = new IncreaseThread(numberHolder, countDownLatch);
        Thread t2 = new DecreaseThread(numberHolder, countDownLatch);

        Thread t3 = new IncreaseThread(numberHolder, countDownLatch);
        Thread t4 = new DecreaseThread(numberHolder, countDownLatch);

        Thread t5 = new IncreaseThread(numberHolder, countDownLatch);
        Thread t6 = new DecreaseThread(numberHolder, countDownLatch);


        t1.start();
        t2.start();

        t3.start();
        t4.start();

        t5.start();
        t6.start();

        try {
            countDownLatch.await();
            System.out.println("主线程执行结束!");
        } catch (InterruptedException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

class NumberHolder { //**********非常重要
    private int number; //这里number执行increase的次数和执行decrease的次数要相同，
    //否则可能造成线程一直处于wait状态，除非将下面的while改成if
    private int i; //标记执行的次数，测试CountDownLatch类

    public synchronized void increase() {
        while (20 == number) //当前的number为10的时候，对象当前的线程进入等待的状态
        {
            try {
                this.wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

// 能执行到这里说明已经被唤醒
// 并且number为0
        number++;
        System.out.println("执行次数" + (++i));
        System.out.println(number);

// 通知在等待的线程
        this.notifyAll();
    }

    public synchronized void decrease() {
        while (0 == number) //当前对象的number等于0时，进入等待的状态
        {
            try {
                this.wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

// 能执行到这里说明已经被唤醒
// 并且number不为0
        number--;
        System.out.println("执行次数" + (++i));
        System.out.println(number);
        this.notifyAll();
    }

}

//线程类：用于控制实体类下面的数据相加
class IncreaseThread extends Thread {
    private NumberHolder numberHolder;
    private CountDownLatch countDownLatch;

    public IncreaseThread(NumberHolder numberHolder, CountDownLatch countDownLatch) {
        this.numberHolder = numberHolder;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; ++i) {
// 进行一定的延时
            try {
                Thread.sleep((long) Math.random() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

// 进行增加操作

            numberHolder.increase();
        }
        countDownLatch.countDown();
    }

}

//线程类，完成对实体类的操作
class DecreaseThread extends Thread {
    private NumberHolder numberHolder;
    private CountDownLatch countDownLatch;

    public DecreaseThread(NumberHolder numberHolder, CountDownLatch countDownLatch) {
        this.numberHolder = numberHolder;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; ++i) {
// 进行一定的延时
            try {
                Thread.sleep((long) Math.random() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

// 进行减少操作
            numberHolder.decrease();
        }
        countDownLatch.countDown();
    }

}