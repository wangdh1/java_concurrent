package com.wangdh.survey.thread;
import java.util.*;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * 执行器服务
 * ExecutorService 接口表示一个异步执行机制，使我们能够在后台执行任务。因此一个 ExecutorService 很类似一个线程池。
 * 实际上，存在于 concurrent 包里的 ExecutorService 实现就是一个线程池实现。
 */
public class ExecutorServiceTest {

    @Test
    public void test1() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(()-> {
            System.out.println("ha ha,this is asynchronous task...0");
        });
        executorService.execute(()-> {
            System.out.println("ha ha,this is asynchronous task...1");
        });
        executorService.execute(()-> {
            System.out.println("ha ha,this is asynchronous task...2");
        });
        executorService.shutdown();

        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        ExecutorService executorService2 = Executors.newScheduledThreadPool(10);
    }

    /**
     * submit(Callable)方法类似于 submit(Runnable)方法，除了它所要求的参数类型之外。
     * Callable 实例除了它的 call()方法能够返回一个结果之外和一个 Runnable 很像。
     * Runnable.run()不能返回一个结果。
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test2() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(() -> {
            Thread.sleep(1000);
            System.out.println("asynchronous task...");
            return "success";
        });
        System.out.println("main task...");
        /**
         * future.get()会阻塞线程直到 Callable 执行结束。你可以把这个当成是一个有返回值的线程。
         */
        System.out.println("result is = " + future.get());
        executorService.shutdown();
    }

    @Test
    public void test3() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Set set = new HashSet<>();
        set.add(new Callable() {
            @Override
            public Object call() throws Exception {
                return "test 001";
            }
        });
        set.add(new Callable() {
            @Override
            public Object call() throws Exception {
                Map map = new ConcurrentHashMap<>();
                map.put("name","wangdinghua");
                return map;
            }
        });
        set.add(new Callable() {
            @Override
            public Object call() throws Exception {
                Map map = new ConcurrentHashMap<>();
                map.put("name","huahua");
                return map;
            }
        });
        Object result = executorService.invokeAny(set);
        System.out.println("result = " + result);
        System.out.println("----------------------------------------------------------------");
        List<Future> list = executorService.invokeAll(set);
        list.stream().forEach(e->{
            try {
                System.out.println(e.get());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        });
        executorService.shutdown();
    }
}
