package com.wangdh.survey.lock;

import org.junit.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {
    @Test
    public void test() {
        Lock lock = new ReentrantLock();
        lock.lock();
        //同步代码
        lock.unlock();
    }
}
