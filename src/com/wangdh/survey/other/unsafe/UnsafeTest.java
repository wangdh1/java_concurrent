package com.wangdh.survey.other.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeTest {
    public static void main(String[] args) throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        User user1 = new User();
        // 打印10
        System.out.println(user1.getAge());

        User user2 = (User) unsafe.allocateInstance(User.class);
        // 打印0(age将返回0，因为 Unsafe.allocateInstance()只会给对象分配内存，并不会调用构造方法，所以这里只会返回int类型的默认值0。)
        System.out.println(user2.getAge());

        unsafe.putInt(user2,unsafe.objectFieldOffset(User.class.getDeclaredField("age")),20);
        System.out.println(user2.getAge());
    }
}

class User {
    private int age;

    public User() {
        this.age = 10;
    }

    public int getAge() {
        return age;
    }
}