package com.wangdh.survey.thread;
import java.util.concurrent.*;
import	java.util.stream.DoubleStream;
import	java.util.stream.IntStream;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * MyRecursiveAction 将一个虚构的 workLoad 作为参数传给自己的构造方法。
 * 如果 wrokLoad 高于一个特定的阈值，该工作将分割为几个子工作，子工作继续分割。
 * 如果 workLoad 高于一个特定阈值，该工作将被分割为几个子工作，子工作继续分割。
 * 如果 workLoad 低于特定阈值，该工作将有 MyRecursiveAction 自己执行。
 */
class MyRecursiveAction extends RecursiveAction {
    private long workLoad = 0;

    public MyRecursiveAction(long workLoad) {
        this.workLoad = workLoad;
    }

    @Override
    protected void compute() {
        //if work is above threshold, break tasks up into smaller tasks
        if (this.workLoad > 16) {
            System.out.println(Thread.currentThread().getName() + ",Splitting workLoad : " + this.workLoad);
            List<MyRecursiveAction> subtasks = new ArrayList<>();
            subtasks.addAll(createSubtasks());
            for (RecursiveAction subtask : subtasks) {
                subtask.fork();
            }
        } else {
            System.out.println(Thread.currentThread().getName() + ",Doing workLoad myself: " + this.workLoad);
        }
    }

    private List<MyRecursiveAction> createSubtasks() {
        List<MyRecursiveAction> subtasks = new ArrayList<>();
        MyRecursiveAction subtask1 = new MyRecursiveAction(this.workLoad / 2);
        MyRecursiveAction subtask2 = new MyRecursiveAction(this.workLoad / 2);
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        return subtasks;
    }
}

/**
 * RecursiveTask 是一种会返回结果的任务。
 * 它可以将自己的工作分割为若干更小任务，并将这些子任务的执行结果合并到一个集体结果。
 * 可以有几个水平的分割和合并。
 */
class MyRecursiveTask extends RecursiveTask<Long> {

    private long workLoad = 0;

    public MyRecursiveTask(long workLoad) {
        this.workLoad = workLoad;
    }

    protected Long compute() {

        //if work is above threshold, break tasks up into smaller tasks
        if (this.workLoad > 2) {
            System.out.println("Splitting workLoad : " + this.workLoad);

            List<MyRecursiveTask> subtasks =
                    new ArrayList<>();
            subtasks.addAll(createSubtasks());

            long result = 0;
            for (MyRecursiveTask subtask : subtasks) {
                subtask.fork();
                result += subtask.join();
            }
            return result;
        } else {
            System.out.println("Doing workLoad myself: " + this.workLoad);
            return workLoad * 3;
        }
    }

    private List<MyRecursiveTask> createSubtasks() {
        List<MyRecursiveTask> subtasks =
                new ArrayList<>();

        MyRecursiveTask subtask1 = new MyRecursiveTask(this.workLoad / 2);
        MyRecursiveTask subtask2 = new MyRecursiveTask(this.workLoad / 2);

        subtasks.add(subtask1);
        subtasks.add(subtask2);

        return subtasks;
    }
}

/**
 * ForkJoinPool 在 Java7中被引入。
 * 它和 ExecutorService 很相似，除了一点不同。
 * ForkJoinPool 让我们可以很方便把任务分裂成几个更小的任务，这些分裂出来的任务也将会提交给 ForkJoinPool。
 * 任务可以继续分割成更小的子任务，只要它还能分割。
 * 可能听起来有点抽象，因此本节中我们将会解释 ForkJoinPool 是如何工作的，还有任务分割是如何进行的。
 */
public class ForkJoinPoolTest {
    @Test
    public void test1() throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(40);
        MyRecursiveAction myRecursiveAction = new MyRecursiveAction(240);
        forkJoinPool.invoke(myRecursiveAction);
        forkJoinPool.awaitTermination(2,TimeUnit.SECONDS);
        forkJoinPool.shutdown();
    }

    @Test
    public void test2() throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(40);
        forkJoinPool = ForkJoinPool.commonPool();
        MyRecursiveTask myRecursiveTask = new MyRecursiveTask(32);
        Object invoke = forkJoinPool.invoke(myRecursiveTask);
        System.out.println("mergedResult = " + invoke);
        forkJoinPool.awaitTermination(2,TimeUnit.SECONDS);
        forkJoinPool.shutdown();
    }

    @Test
    public void test3() throws InterruptedException {
        Executors.newWorkStealingPool();
                System.out.println(Runtime.getRuntime().availableProcessors());
    }

}
