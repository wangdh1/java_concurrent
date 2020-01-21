package com.wangdh.survey.lock.tongge;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class MyLockMySelf {
    private volatile int state = 0;
    private static long stateOffset;
    private static long tailOffset;
    private static Unsafe unsafe;
    static final Node EMPTY = new Node();
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe)f.get(null);
            stateOffset = unsafe.objectFieldOffset(MyLockMySelf.class.getDeclaredField("state"));
            tailOffset = unsafe.objectFieldOffset(MyLockMySelf.class.getDeclaredField("tail"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MyLockMySelf() {
        head = tail = EMPTY;
    }

    public void lock(){
        if(compareAndSetState(0,1)){
            return;
        }
        Node node = enqueue();
        Node prev = node.prev;
        while(prev != head || !compareAndSetState(0,1)){
            System.out.println(Thread.currentThread().getName());
            unsafe.park(false,0L);
        }
        head = node;
        node.thread = null;
        node.prev = null;
        prev.next = null;
    }

    /**
     * 入队
     */
    private Node enqueue() {
        while (true){
            Node t = tail;
            Node node = new Node(Thread.currentThread(), t);
            if(compareAndSetTail(t,node)){
                t.next = node;
                return node;
            }
        }
    }

    public void unlock(){
        state = 0;
        Node next = head.next;
        if(Objects.nonNull(next)){
            unsafe.unpark(next.thread);
        }
    }

    /**
     * 修改state的状态
     * @param current
     * @param expected
     * @return
     */
    private boolean compareAndSetState(int current, int expected) {
        return unsafe.compareAndSwapInt(this, stateOffset, current, expected);
    }

    private boolean compareAndSetTail(Node current, Node expected) {
        return unsafe.compareAndSwapObject(this, tailOffset, current, expected);
    }

    static class Node {
        private Thread thread;
        private Node prev;
        private Node next;

        public Node() {
        }

        public Node(Thread thread, Node prev) {
            this.thread = thread;
            this.prev = prev;
        }
    }
    private volatile Node head;
    private volatile Node tail;


    /////////////////////测试///////////////////////
    private static int count = 0;
    public static void main(String[] args) throws InterruptedException {
        MyLockMySelf myLock = new MyLockMySelf();
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        IntStream.range(0,1000).forEach(i->new Thread(()->{
            myLock.lock();
            IntStream.range(0,10000).forEach(j->count++);
            myLock.unlock();
            countDownLatch.countDown();
        },"thread--" + i).start());
        countDownLatch.await();
        System.out.println(count);
    }
}