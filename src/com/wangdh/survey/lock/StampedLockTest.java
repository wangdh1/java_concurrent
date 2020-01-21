package com.wangdh.survey.lock;

import org.junit.Test;

import java.util.concurrent.locks.StampedLock;

/**
 * create at 2019-12-25
 * study from
 * https://juejin.im/post/5bacf523f265da0a951ee418
 */
public class StampedLockTest {
    private final StampedLock stampedLock = new StampedLock();

    @Test
    public void test(){
        long stamp = stampedLock.writeLock(); //获取写锁
        stampedLock.unlockWrite(stamp);
    }
}
