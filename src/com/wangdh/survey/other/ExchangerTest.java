package com.wangdh.survey.other;

import java.util.concurrent.Exchanger;

class ExchangerRunnable implements Runnable{

    Exchanger exchanger = null;
    Object    object    = null;

    ExchangerRunnable(Exchanger exchanger, Object object) {
        this.exchanger = exchanger;
        this.object = object;
    }

    public void run() {
        try {
            Object previous = this.object;

            this.object = exchanger.exchange(this.object);

            System.out.println(
                    Thread.currentThread().getName() +
                            " exchanged " + previous + " for " + this.object
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Exchanger 类表示一种两个线程可以进行互相交换对象的会和点
 * 示意图
 * https://user-gold-cdn.xitu.io/2017/11/13/15fb424c4c497e08?imageslim
 * 当一个线程到达exchange调用点时，如果它的伙伴线程此前已经调用了此方法，那么它的伙伴会被调度唤醒并与之进行对象交换，然后各自返回。
 * 在常见的 生产者-消费者 模型中用于同步数据。
 */
public class ExchangerTest {
    public static void main(String[]args){
        Exchanger exchanger = new Exchanger();

        ExchangerRunnable exchangerRunnable1 =
                new ExchangerRunnable(exchanger, "Thread-0数据");

        ExchangerRunnable exchangerRunnable2 =
                new ExchangerRunnable(exchanger, "Thread-1数据");

        new Thread(exchangerRunnable1).start();
        new Thread(exchangerRunnable2).start();

    }
}
