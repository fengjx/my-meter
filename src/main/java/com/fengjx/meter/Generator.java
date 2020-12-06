package com.fengjx.meter;


import java.util.concurrent.BlockingQueue;

/**
 * 生成请求
 * @author fengjianxin
 */
public class Generator implements Runnable {

    private final String url;
    private int total;
    private final BlockingQueue<String> queue;

    public Generator(String url, int total, BlockingQueue<String> queue) {
        this.url = url;
        this.total = total;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (total > 0) {
            try {
                System.out.println(String.format("%d gen => %s", total, url));
                queue.put(url);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                total--;
            }
        }
    }
}
