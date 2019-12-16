package com.wangdh.survey.lock;

import org.junit.Test;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockTest {
    @Test
    public void test() {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readWriteLock.readLock().lock();
        // u can do sth...
        readWriteLock.readLock().unlock();

        readWriteLock.writeLock().lock();
        // u can do sth...
        readWriteLock.writeLock().unlock();
    }
}
