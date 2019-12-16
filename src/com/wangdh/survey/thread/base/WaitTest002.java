package com.wangdh.survey.thread.base;

public class WaitTest002 implements Runnable {
    private int num;
    private Object lock;

    public WaitTest002(int num, Object lock) {
        super();
        this.num = num;
        this.lock = lock;
    }

    public void run() {
        try {
            while(true){
                synchronized(lock){
                    System.out.println(Thread.currentThread().getName() + ":" + num);
                    lock.notify();
                    lock.notifyAll();
                    lock.wait();
                    Thread.sleep(500);
                }
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        final Object lock = new Object();

        Thread thread1 = new Thread(new WaitTest002(1,lock));
        Thread thread2 = new Thread(new WaitTest002(2, lock));

        thread1.start();
        thread2.start();
//        Thread thread3 = new Thread(new WaitTest002(3, lock));
//        thread3.start();
    }
}
