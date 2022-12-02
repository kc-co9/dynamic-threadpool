package com.share.co.kcl.dtp.core.refresher;

import com.share.co.kcl.dtp.common.enums.RejectedStrategy;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import org.junit.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.anyString;

public class AbstractExecutorRefresherTests {

    @After
    public void afterRefresh() {
        ExecutorMonitor.clear();
    }

    @Test
    public void testJdkExecutorRefreshWithCheckNeedntUpdate() {
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        ExecutorMonitor.register(threadPoolExecutor);

        testExecutorRefreshWithCheckNeedntUpdate(threadPoolExecutor);
    }

    @Test
    public void testDynamicExecutorRefreshWithCheckNeedntUpdate() {
        ThreadPoolExecutor threadPoolExecutor =
                new DynamicThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        testExecutorRefreshWithCheckNeedntUpdate(threadPoolExecutor);
    }

    @Test
    public void testJdkExecutorRefreshWithCheckNeedUpdate() {
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        ExecutorMonitor.register(threadPoolExecutor);

        testExecutorRefreshWithCheckNeedUpdate(threadPoolExecutor);
    }

    @Test
    public void testDynamicExecutorRefreshWithCheckNeedUpdate() {
        ThreadPoolExecutor threadPoolExecutor =
                new DynamicThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        testExecutorRefreshWithCheckNeedUpdate(threadPoolExecutor);
    }

    @Test
    public void testJdkExecutorRefreshWithNotRepeatedUpdate() {
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        ExecutorMonitor.register(threadPoolExecutor);

        testExecutorRefreshWithNotRepeatedUpdate(threadPoolExecutor);
    }

    @Test
    public void testDynamicExecutorRefreshWithNotRepeatedUpdate() {
        ThreadPoolExecutor threadPoolExecutor =
                new DynamicThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        testExecutorRefreshWithNotRepeatedUpdate(threadPoolExecutor);
    }

    @Test
    public void testJdkExecutorRefreshWithRepeatedUpdate() {
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        ExecutorMonitor.register(threadPoolExecutor);

        testExecutorRefreshWithRepeatedUpdate(threadPoolExecutor);
    }

    @Test
    public void testDynamicExecutorRefreshWithRepeatedUpdate() {
        ThreadPoolExecutor threadPoolExecutor =
                new DynamicThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        testExecutorRefreshWithRepeatedUpdate(threadPoolExecutor);
    }

    private void testExecutorRefreshWithCheckNeedntUpdate(ThreadPoolExecutor threadPoolExecutor) {

        AbstractExecutorRefresher executorRefresher = new NeedntUpdateExecutorRefresher();

        Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());

        executorRefresher.produce();
        executorRefresher.consume();

        Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());

    }

    private void testExecutorRefreshWithCheckNeedUpdate(ThreadPoolExecutor threadPoolExecutor) {

        AbstractExecutorRefresher executorRefresher = new NeedUpdateExecutorRefresher();

        Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());

        executorRefresher.produce();
        executorRefresher.consume();

        Assert.assertEquals("core pool size is error", 10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 100, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 1000, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.CallerRunsPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());
    }

    private void testExecutorRefreshWithNotRepeatedUpdate(ThreadPoolExecutor threadPoolExecutor) {
        AbstractExecutorRefresher executorRefresher = new NeedUpdateExecutorRefresher();

        Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());

        executorRefresher.produce();
        executorRefresher.consume();

        Assert.assertEquals("core pool size is error", 10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 100, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 1000, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.CallerRunsPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());


        threadPoolExecutor.setCorePoolSize(1);
        threadPoolExecutor.setMaximumPoolSize(10);
        threadPoolExecutor.setKeepAliveTime(10, TimeUnit.SECONDS);
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());

        executorRefresher.produce();
        executorRefresher.consume();

        Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());
    }

    private void testExecutorRefreshWithRepeatedUpdate(ThreadPoolExecutor threadPoolExecutor) {
        AbstractExecutorRefresher executorRefresher = new NeedRepeatedUpdateExecutorRefresherForReportFailure();

        Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());

        executorRefresher.produce();
        executorRefresher.consume();

        Assert.assertEquals("core pool size is error", 10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 100, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 1000, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.CallerRunsPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());

        threadPoolExecutor.setCorePoolSize(1);
        threadPoolExecutor.setMaximumPoolSize(10);
        threadPoolExecutor.setKeepAliveTime(10, TimeUnit.SECONDS);
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());

        executorRefresher.produce();
        executorRefresher.consume();

        Assert.assertEquals("core pool size is error", 10, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 100, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 1000, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.CallerRunsPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());
    }

    private static class NeedntUpdateExecutorRefresher extends AbstractExecutorRefresher {
        protected NeedntUpdateExecutorRefresher() {
            super(anyString(), anyString());
        }

        @Override
        protected boolean checkUpdate(String serverCode, String serverIp) {
            return false;
        }

        @Override
        protected List<ExecutorConfigBo> fetchUpdate(String serverCode, String serverIp) {
            return Collections.emptyList();
        }
    }

    private static class NeedUpdateExecutorRefresher extends AbstractExecutorRefresher {

        private final AtomicBoolean executorUpdateResult = new AtomicBoolean(Boolean.FALSE);

        protected NeedUpdateExecutorRefresher() {
            super(anyString(), anyString());
        }

        @Override
        protected boolean checkUpdate(String serverCode, String serverIp) {
            return executorUpdateResult.compareAndSet(Boolean.FALSE, Boolean.TRUE);
        }

        @Override
        protected List<ExecutorConfigBo> fetchUpdate(String serverCode, String serverIp) {
            ExecutorConfigBo executorConfigBo = new ExecutorConfigBo();
            executorConfigBo.setExecutorId(ExecutorMonitor.watch().keySet().stream().findFirst().orElse(""));
            executorConfigBo.setExecutorName("test-executor-name-new");
            executorConfigBo.setCorePoolSize(10);
            executorConfigBo.setMaximumPoolSize(100);
            executorConfigBo.setKeepAliveTime(1000);
            executorConfigBo.setRejectedStrategy(RejectedStrategy.CALLER_RUNS_POLICY);
            return Collections.singletonList(executorConfigBo);
        }
    }

    private static class NeedRepeatedUpdateExecutorRefresherForReportFailure extends AbstractExecutorRefresher {
        protected NeedRepeatedUpdateExecutorRefresherForReportFailure() {
            super(anyString(), anyString());
        }

        @Override
        protected boolean checkUpdate(String serverCode, String serverIp) {
            return true;
        }

        @Override
        protected List<ExecutorConfigBo> fetchUpdate(String serverCode, String serverIp) {
            ExecutorConfigBo executorConfigBo = new ExecutorConfigBo();
            executorConfigBo.setExecutorId(ExecutorMonitor.watch().keySet().stream().findFirst().orElse(""));
            executorConfigBo.setExecutorName("test-executor-name-new");
            executorConfigBo.setCorePoolSize(10);
            executorConfigBo.setMaximumPoolSize(100);
            executorConfigBo.setKeepAliveTime(1000);
            executorConfigBo.setRejectedStrategy(RejectedStrategy.CALLER_RUNS_POLICY);
            return Collections.singletonList(executorConfigBo);
        }
    }
}
