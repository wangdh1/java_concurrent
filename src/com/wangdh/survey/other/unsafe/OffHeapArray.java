package com.wangdh.survey.other.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 如果进程在运行过程中JVM上的内存不足了，会导致频繁的进行GC。理想情况下，我们可以考虑使用堆外内存，这是一块不受JVM管理的内存。
 * 使用堆外内存
 * 使用Unsafe的allocateMemory()我们可以直接在堆外分配内存，这可能非常有用，但我们要记住，这个内存不受JVM管理，因此我们要调用freeMemory()方法手动释放它。
 * 假设我们要在堆外创建一个巨大的int数组，我们可以使用allocateMemory()方法来实现
 */
public class OffHeapArray {
    // 一个int等于4个字节
    private static final int INT = 4;
    private long size;
    private long address;

    private static Unsafe unsafe;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public OffHeapArray(long size){
        this.size = size;
        //参数字节数
        this.address = unsafe.allocateMemory(size * INT);
    }

    // 获取指定索引处的元素
    public int get(long i) {
        return unsafe.getInt(address + i *  INT);
    }

    // 设置指定索引处的元素
    public void set(long i,int value){
        unsafe.putInt(address + i * INT,value);
    }

    // 元素个数
    public long getSize(){
        return this.size;
    }

    // 释放堆外内存
    public void freeMemory(){
        unsafe.freeMemory(address);
    }

    public static void main(String[] args) {
        OffHeapArray offHeapArray = new OffHeapArray(4);
        offHeapArray.set(0,1);
        offHeapArray.set(1,2);
        offHeapArray.set(2,3);
        offHeapArray.set(3,4);
        offHeapArray.set(2,5);// 在索引2的位置重复放入元素
        int sum = 0;
        for (int i = 0; i < offHeapArray.getSize(); i++) {
            sum += offHeapArray.get(i);
        }
        System.out.println(sum);
        //最后，一定要记得调用freeMemory()将内存释放回操作系统。
        offHeapArray.freeMemory();
    }
}
