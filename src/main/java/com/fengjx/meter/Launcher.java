package com.fengjx.meter;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 启动类
 * @author fengjianxin
 */
public class Launcher {

    public static void main(String[] args) throws InterruptedException {
        String url = args[0];
        int total = Integer.parseInt(args[1]);
        int parall = Integer.parseInt(args[2]);

        String info = String.format("MyMeter Running - %s, 总次数: %d, 并发数: %d, ", url, total, parall);
        System.out.println(info);
        CountDownLatch latch = new CountDownLatch(total);
        BlockingQueue<String> queue = new LinkedBlockingDeque<>(total);

        Generator gen = new Generator(url, total, queue);
        Reportor reportor = new Reportor();
        Exector exector = new Exector(total, parall, queue, latch, reportor);
        new Thread(gen).start();
        new Thread(exector).start();
        latch.await();
        reportor.report();
        System.exit(0);
    }

}
