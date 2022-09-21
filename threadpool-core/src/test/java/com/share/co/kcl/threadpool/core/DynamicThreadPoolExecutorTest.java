package com.share.co.kcl.threadpool.core;

import com.share.co.kcl.threadpool.core.monitor.ExecutorMonitor;
import com.share.co.kcl.threadpool.core.refresher.DefaultExecutorRefresher;
import com.share.co.kcl.threadpool.core.refresher.Refresher;
import com.share.co.kcl.threadpool.core.reporter.DefaultExecutorReporter;
import com.share.co.kcl.threadpool.core.reporter.DefaultServerHealthReporter;
import com.share.co.kcl.threadpool.core.reporter.Reporter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class DynamicThreadPoolExecutorTest {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicThreadPoolExecutorTest.class);

    /**
     * Rigorous Test :-)
     */
    @Test
    public void newDynamicThreadPoolExecutorTest() {
        new DynamicThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        Assert.assertEquals("executor register failure", 1, ExecutorMonitor.watch().size());

        new DynamicThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory());
        Assert.assertEquals("executor register failure", 2, ExecutorMonitor.watch().size());

        new DynamicThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());
        Assert.assertEquals("executor register failure", 3, ExecutorMonitor.watch().size());

        new DynamicThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        Assert.assertEquals("executor register failure", 4, ExecutorMonitor.watch().size());
    }

    @Test
    public void terminateDynamicThreadPoolExecutorTest() {
        DynamicThreadPoolExecutor executor1 = new DynamicThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        DynamicThreadPoolExecutor executor2 = new DynamicThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory());
        DynamicThreadPoolExecutor executor3 = new DynamicThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());
        DynamicThreadPoolExecutor executor4 = new DynamicThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        Assert.assertEquals("executor register failure", 4, ExecutorMonitor.watch().size());

        executor1.shutdownNow();
        Assert.assertEquals("executor1 shutdown failure", 3, ExecutorMonitor.watch().size());

        executor2.shutdownNow();
        Assert.assertEquals("executor2 shutdown failure", 2, ExecutorMonitor.watch().size());

        executor3.shutdownNow();
        Assert.assertEquals("executor3 shutdown failure", 1, ExecutorMonitor.watch().size());

        executor4.shutdownNow();
        Assert.assertEquals("executor4 shutdown failure", 0, ExecutorMonitor.watch().size());
    }

    public static void main(String[] args) throws InterruptedException {
        String serverCode = "test_server";
        String serverHealthReportLink = "http://localhost:8080/monitor/v1/reportServerHealth";
        String executorInfoReportLink = "http://localhost:8080/monitor/v1/reportExecutorInfo";

        String checkExecutorSyncLink = "http://localhost:8080/monitor/v1/checkExecutorSync";
        String pullExecutorSyncLink = "http://localhost:8080/monitor/v1/lookupExecutorSync";

        new Thread(() -> {
            Reporter serverReporter = new DefaultServerHealthReporter(serverCode, serverHealthReportLink);
            serverReporter.report();
        }).start();

        new Thread(() -> {
            Refresher executorRefresher = new DefaultExecutorRefresher(serverCode, checkExecutorSyncLink, pullExecutorSyncLink);
            executorRefresher.refresh();
            executorRefresher.update();
        }).start();

        new Thread(() -> {
            Reporter executorReporter = new DefaultExecutorReporter(serverCode, executorInfoReportLink);
            executorReporter.report();
        }).start();

        new DynamicThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        new DynamicThreadPoolExecutor(2, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        new DynamicThreadPoolExecutor(3, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        new DynamicThreadPoolExecutor(4, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        new DynamicThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        new DynamicThreadPoolExecutor(6, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        while (true) {
            Thread.sleep(3000L);
            LOG.debug("running:" + System.currentTimeMillis());
        }
    }
}
