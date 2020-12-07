package com.fengjx.meter;

import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 执行 http 请求
 * @author fengjianxin
 */
public class Exector implements Runnable {

    private int total;
    private final BlockingQueue<String> queue;
    private final Reportor reportor;
    private final CountDownLatch latch;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .build();

    /**
     * 信号量控制并发数
     */
    private final Semaphore semaphore;

    public Exector(int total, int parall, BlockingQueue<String> queue, CountDownLatch latch, Reportor reportor) {
        semaphore = new Semaphore(parall);
        this.total = total;
        this.queue = queue;
        this.latch = latch;
        this.reportor = reportor;
    }

    @Override
    public void run() {
        while (total > 0) {
            try {
                semaphore.acquire();
                exec(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                total--;
            }
        }
    }

    private void exec(String url) {
        System.out.println(String.format("%d exec => %s", total, url));
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.67 Safari/537.36")
                .build();

        Instant start = Instant.now();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println(e.getMessage());
                addReport(start, false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                addReport(start, response.isSuccessful());
            }
        });
    }

    private void addReport(Instant start, boolean ok) {
        long duraMillis = Duration.between(start, Instant.now()).toMillis();
        reportor.add(new Reportor.Result(duraMillis, ok));
        latch.countDown();
        semaphore.release();
    }

}
