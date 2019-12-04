package com.wangdh.survey.thread;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * ScheduleExecutorService 是一个 ExecutorService，它能够将任务延后执行，或者间隔固定时间多次执行。
 * 任务由一个工作者线程异步执行，而不是由提交任务给 ScheduleExecutorService 的那个线程执行。
 */
public class ScheduleExecutorServiceTest {
    @Test
    public void test1() throws ExecutionException, InterruptedException {
//    public static void main(String[] args) throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ScheduledExecutorService  scheduledExecutorService  = Executors.newScheduledThreadPool(5);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("begin to do sth...");
            }
        },1, TimeUnit.SECONDS);

        /**
         * period 被解释为前一个执行的开始和下一个执行的开始之间的间隔时间,
         * 前提是需要前一个执行完毕才会开始下一个的执行。
         */
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
//                System.out.println(Thread.currentThread().getName() + ",scheduleAtFixedRate begin..." + formatter.format(LocalDateTime.now()));
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                System.err.println(Thread.currentThread().getName() + ",scheduleAtFixedRate done..." + formatter.format(LocalDateTime.now()));
            }
        },1,3,TimeUnit.SECONDS);

        /**
         * period 则被解释为前一个执行的结束和下一个执行开始之间的间隔
         */
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + ",scheduleWithFixedDelay begin..." + formatter.format(LocalDateTime.now()));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println(Thread.currentThread().getName() + ",scheduleWithFixedDelay done..." + formatter.format(LocalDateTime.now()));
            }
        },3,3,TimeUnit.SECONDS);


        ScheduledFuture<String> schedule = scheduledExecutorService.schedule(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "done";
            }
        }, 10, TimeUnit.SECONDS);
//        System.out.println(schedule.get());



        Thread.sleep(50000);
//        scheduledExecutorService.shutdownNow();
    }
}
