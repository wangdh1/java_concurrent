package com.wangdh.survey.other;

import java.util.concurrent.Semaphore;

class ThreadSemaphore implements Runnable{

    private final Semaphore semaphore;

    ThreadSemaphore(Semaphore semaphore){
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName()+"获取到锁进来");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        semaphore.release();
    }
}

public class SemaphoreTest {
    public static void main(String[]args){
        Semaphore semaphore = new Semaphore(2,true);
        for (int i = 0; i < 5; i++) {
            new Thread(new ThreadSemaphore(semaphore)).start();
        }
    }
}
