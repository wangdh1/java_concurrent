package com.wangdh.survey.concurrentmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap 和 HashTable 类很相似，但 ConcurrentHashMap 能提供比 HashTable 更好的并发性能。
 * 在你从中读取对象的时候，ConcurrentHashMap 并不会把整个 Map 锁住。
 * 此外，在你向其写入对象的时候，ConcurrentHashMap 也不会锁住整个 Map，它的内部只是把 Map 中正在被写入的部分锁定。
 * 其实就是把 synchronized 同步整个方法改为了同步方法里面的部分代码。
 * 另外一个不同点是，在被遍历的时候，即使是 ConcurrentHashMap 被改动，它也不会抛 ConcurrentModificationException。
 * 尽管 Iterator 的设计不是为多个线程同时使用。
 */
class Thread1 extends Thread {

    private final Map map;

    Thread1(Map map) {
        this.map = map;
    }

    @Override
    public void run() {
        super.run();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map.remove("6");
        map.put("name","wangdh");
    }
}

class Thread2 extends Thread {

    private final Map map;

    Thread2(Map map) {
        this.map = map;
    }

    @Override
    public void run() {
        super.run();
        Set set = map.keySet();
        for (Object next : set) {
            System.out.println(next + ":" + map.get(next));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class ConcurrentHashMapTest {
    public static void main(String[] args) {
        Map map = null;
//        map = new HashMap<>();
        map = new ConcurrentHashMap<>();
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "c");
        map.put("4", "d");
        map.put("5", "e");
        map.put("6", "f");
        map.put("7", "g");
        map.put("8", "h");
        new Thread1(map).start();
        new Thread2(map).start();
    }
}
