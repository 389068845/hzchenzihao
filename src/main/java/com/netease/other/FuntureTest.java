package com.netease.other;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by chenzihao on 2017/1/7.
 */
public class FuntureTest
{
    final static ExecutorService service = Executors.newCachedThreadPool();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Long t1 = System.currentTimeMillis();

        // 任务1
        Future<Boolean> booleanTask = service.submit(new Callable<Boolean>() {

            public Boolean call() throws Exception {
                Thread.sleep(500);
                return true;
            }
        });

        // 任务2
        Future<String> stringTask = service.submit(new Callable<String>() {

            public String call() throws Exception {
                Thread.sleep(100);
                return "Hello World";
            }
        });

        // 任务3
        Future<Integer> integerTask = service.submit(new Callable<Integer>() {

            public Integer call() throws Exception {
                Thread.sleep(1500);
                return new Random().nextInt(100);
            }
        });

        System.out.println("BooleanTask" + booleanTask.get());
        System.out.println("StringTask" + stringTask.get());
        System.out.println("integerTask" + integerTask.get());
//        try {
//            System.out.println("integerTask" + integerTask.get(100,TimeUnit.MILLISECONDS));
//        } catch (TimeoutException e) {
//            System.err.println("timeOut: " + (System.currentTimeMillis() - t1));
//        }

        // 执行时间
        System.err.println("time: " + (System.currentTimeMillis() - t1));
    }
}

