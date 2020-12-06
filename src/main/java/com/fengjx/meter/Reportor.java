package com.fengjx.meter;


import java.util.ArrayList;
import java.util.List;

/**
 * 生成测试报告
 * @author fengjianxin
 */
public class Reportor {

    private final List<Result> results = new ArrayList<>();

    public void add(Result result) {
        synchronized (results) {
            System.out.println(results.size() + " add report: " + result);
            results.add(result);
        }
    }

    public void report() {
        int size = results.size();
        long totalDuraMillis = 0;
        int success = 0, fail = 0;
        results.sort((o1, o2) -> Math.toIntExact(o1.getDuraMillis() - o2.getDuraMillis()));
        int index = size * 95 / 100 - 1;
        for (Result result : results) {
            totalDuraMillis += result.getDuraMillis();
            if (result.isOk()) {
                success++;
            } else {
                fail++;
            }
        }
        long avg = totalDuraMillis / results.size();

        StringBuilder report = new StringBuilder("测试结果\r\n");
        report.append("平均响应时间：").append(avg).append(" ms\r\n")
                .append("95%响应时间：").append(results.get(index).duraMillis).append(" ms\r\n")
                .append("成功次数：").append(success).append("\r\n")
                .append("失败次数：").append(fail).append("\r\n");

        System.out.println(report.toString());
    }

    public static class Result {
        private long duraMillis;
        private boolean ok;

        public Result(long duraMillis, boolean ok) {
            this.duraMillis = duraMillis;
            this.ok = ok;
        }

        public long getDuraMillis() {
            return duraMillis;
        }

        public void setDuraMillis(long duraMillis) {
            this.duraMillis = duraMillis;
        }

        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "duraMillis=" + duraMillis +
                    ", ok=" + ok +
                    '}';
        }
    }

}
