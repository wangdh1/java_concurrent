package com.wangdh.survey.lock;

/**
 * 首先线程会尝试去获取数据，需要获取读锁①，
 * 如果存在值，则直接读取并释放读锁②。如果不存在值，则首先释放已经获取的读锁③，然后尝试获取写锁④。获取到写锁之后，再次检查值，因为此时可能存在其他写锁已经更新值，这时只需要读取，然后释放写锁⑤。如果还是没有值，则通过其他途径获取值并更新然后获取读锁⑥，这一步锁降级操作是为了直接抢占读锁，避免释放写锁之后再次获取读锁时被其他写线程抢占，这样保证了这一次读取数据的原子性。之后再执行⑤释放写锁和②释放读锁。
 *
 * 作者：knock_小新
 * 链接：https://juejin.im/post/5b9df6015188255c8f06923a
 * 来源：掘金
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
public class MyReadWriteLock {

    private int state = 0; //1. 定义一个读写锁共享变量state

    //2. state高16位为读锁数量
    private int GetReadCount() {
        return state >>> 16;
    }

    //2. 低16位为写锁数量
    private int GetWriteCount() {
        return state & ((1 << 16) - 1);
    }

    //3. 获取读锁时先判断是否有写锁，有则等待，没有则将读锁数量加1
    public synchronized void lockRead() throws InterruptedException{

        while (GetWriteCount() > 0) {
            wait();
        }

        System.out.println("lockRead ---" + Thread.currentThread().getName());
        state = state + (1 << 16);
    }

    //4. 释放读锁数量减1，通知所有等待线程
    public synchronized void unlockRead() {
        state = state - ((1 << 16));
        notifyAll();
    }

    //5. 获取写锁时需要判断读锁和写锁是否都存在，有则等待，没有则将写锁数量加1
    public synchronized void lockWriters() throws InterruptedException{

        while (GetReadCount() > 0 || GetWriteCount() > 0) {
            wait();
        }
        System.out.println("lockWriters ---" + Thread.currentThread().getName());
        state++;
    }

    //6. 释放写锁数量减1，通知所有等待线程
    public synchronized void unlockWriters(){

        state--;
        notifyAll();
    }
}