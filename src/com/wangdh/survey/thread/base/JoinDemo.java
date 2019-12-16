package com.wangdh.survey.thread.base;

import org.junit.Test;

/**
 * 创建了10个线程，每个线程都会等待前一个线程结束才会继续运行。
 * 可以通俗的理解成接力，前一个线程将接力棒传给下一个线程，然后又传给下一个线程......
 */
public class JoinDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread previousThread = Thread.currentThread();
        previousThread.wait();
        for (int i = 1; i <= 10; i++) {
            Thread curThread = new JoinThread(previousThread);
            curThread.start();
            previousThread = curThread;
        }
    }

    static class JoinThread extends Thread {
        private Thread thread;

        public JoinThread(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            try {
                thread.join();
                System.out.println(thread.getName() + " terminated." + "<<<===>>>currentThread is " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
