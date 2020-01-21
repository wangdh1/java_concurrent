package com.wangdh.survey.lock;

import java.util.concurrent.locks.StampedLock;

public class StampedLock_Point {
    private double x, y;

    private final StampedLock stampedLock = new StampedLock();

    //写锁的使用
    void move(double deltaX, double deltaY) {

        long stamp = stampedLock.writeLock(); //获取写锁
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            stampedLock.unlockWrite(stamp); //释放写锁
        }
    }

    //乐观读锁的使用
    double distanceFromOrigin() {

        long stamp = stampedLock.tryOptimisticRead(); //获得一个乐观读锁
        double currentX = x;
        double currentY = y;
        if (!stampedLock.validate(stamp)) { //检查乐观读锁后是否有其他写锁发生，有则返回false

            stamp = stampedLock.readLock(); //获取一个悲观读锁

            try {
                currentX = x;
            } finally {
                stampedLock.unlockRead(stamp); //释放悲观读锁
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    //悲观读锁以及读锁升级写锁的使用
    void moveIfAtOrigin(double newX, double newY) {

        long stamp = stampedLock.readLock(); //悲观读锁
        try {
            while (x == 0.0 && y == 0.0) {
                long ws = stampedLock.tryConvertToWriteLock(stamp); //读锁转换为写锁
                if (ws != 0L) { //转换成功

                    stamp = ws; //票据更新
                    x = newX;
                    y = newY;
                    break;
                } else {
                    stampedLock.unlockRead(stamp); //转换失败释放读锁
                    stamp = stampedLock.writeLock(); //强制获取写锁
                }
            }
        } finally {
            stampedLock.unlock(stamp); //释放所有锁
        }
    }
}
