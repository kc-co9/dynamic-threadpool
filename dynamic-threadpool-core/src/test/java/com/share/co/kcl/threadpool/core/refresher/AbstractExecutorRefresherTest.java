package com.share.co.kcl.threadpool.core.refresher;

import com.share.co.kcl.common.enums.RejectedStrategy;
import com.share.co.kcl.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.threadpool.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.threadpool.core.monitor.ExecutorMonitor;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AbstractExecutorRefresherTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractExecutorRefresherTest.class);

    private static TestExecutorRefresher executorRefresher;

    @BeforeClass
    public static void startRefreshThread() {
        executorRefresher = new TestExecutorRefresher("ignore");
        executorRefresher.refresh();
    }

    @Test
    public void jdkThreadPoolExecutorRefreshTest() throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        ExecutorMonitor.register(threadPoolExecutor);
        threadExecutorRefreshTest(threadPoolExecutor);
        ExecutorMonitor.clear();
    }

    @Test
    public void dynamicThreadPoolExecutorRefreshTest() throws InterruptedException {
        DynamicThreadPoolExecutor dynamicThreadPoolExecutor =
                new DynamicThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        threadExecutorRefreshTest(dynamicThreadPoolExecutor);
        ExecutorMonitor.clear();
    }

    private void threadExecutorRefreshTest(ThreadPoolExecutor threadPoolExecutor) throws InterruptedException {
        Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
        Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
        Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());

        Assert.assertFalse("canRefresh status is not correction", executorRefresher.getCanRefresh());
        Assert.assertFalse("canSync status is not correction", executorRefresher.getCanSync());
        Assert.assertFalse("canPull status is not correction", executorRefresher.getCanPull());
        Assert.assertFalse("isPull status is not correction", executorRefresher.getIsPull());
        Assert.assertFalse("isRefresh status is not correction", executorRefresher.getIsRefresh());
        Assert.assertFalse("canUpdate status is not correction", executorRefresher.getCanUpdate());
        Assert.assertFalse("isUpdate status is not correction", executorRefresher.getIsUpdate());

        executorRefresher.setCanRefresh(true);

        // allow refreshing
        Assert.assertTrue("canRefresh status is error", executorRefresher.getCanRefresh());
        while (executorRefresher.getCanRefresh()
                && !executorRefresher.getCanSync()
                && !executorRefresher.getCanPull()
                && !executorRefresher.getIsPull()
                && !executorRefresher.getIsRefresh()
                && !executorRefresher.getCanUpdate()
                && !executorRefresher.getIsUpdate()) {
            LOG.debug("executor start refresh");
            Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
            Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
            Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
            Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());
            executorRefresher.setCanSync(true);
        }

        // allow syncing
        Assert.assertTrue("canSync status is error", executorRefresher.getCanRefresh());
        while (executorRefresher.getCanRefresh()
                && executorRefresher.getCanSync()
                && !executorRefresher.getCanPull()
                && !executorRefresher.getIsPull()
                && !executorRefresher.getIsRefresh()
                && !executorRefresher.getCanUpdate()
                && !executorRefresher.getIsUpdate()) {
            LOG.debug("executor start pull");
            Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
            Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
            Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
            Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());
            executorRefresher.setCanPull(true);
        }

        // allow pulling
        Assert.assertTrue("canPull status is error", executorRefresher.getCanPull());
        while (executorRefresher.getCanRefresh()
                && executorRefresher.getCanSync()
                && (executorRefresher.getCanPull() || executorRefresher.getIsPull() || executorRefresher.getIsRefresh())
                && !executorRefresher.getCanUpdate()
                && !executorRefresher.getIsUpdate()) {
            LOG.debug("executor is pulling/refreshing");
            if (executorRefresher.getIsPull()) {
                LOG.debug("executor pull success");
                Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
                Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
                Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
                Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());
            }
            if (executorRefresher.getIsRefresh()) {
                LOG.debug("executor refresh success");
                Assert.assertEquals("core pool size is error", 1, threadPoolExecutor.getCorePoolSize());
                Assert.assertEquals("max pool size is error", 10, threadPoolExecutor.getMaximumPoolSize());
                Assert.assertEquals("keep alive time size is error", 10, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
                Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.AbortPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());
                executorRefresher.setCanUpdate(true);
            }
            Thread.sleep(500);
        }

        // allow updating
        Assert.assertTrue("isPull status is error", executorRefresher.getIsPull());
        Assert.assertTrue("canUpdate status is error", executorRefresher.getCanUpdate());
        while (executorRefresher.getCanRefresh()
                && executorRefresher.getCanSync()
                && executorRefresher.getCanPull()
                && executorRefresher.getIsPull()
                && executorRefresher.getIsRefresh()
                && (executorRefresher.getCanUpdate() || executorRefresher.getIsUpdate())) {
            LOG.debug("executor is updating");
            if (executorRefresher.getIsUpdate()) {
                LOG.debug("executor update success");
                Assert.assertEquals("core pool size is error", 10, threadPoolExecutor.getCorePoolSize());
                Assert.assertEquals("max pool size is error", 100, threadPoolExecutor.getMaximumPoolSize());
                Assert.assertEquals("keep alive time size is error", 1000, threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
                Assert.assertEquals("reject handler size is error", ThreadPoolExecutor.CallerRunsPolicy.class, threadPoolExecutor.getRejectedExecutionHandler().getClass());
                if (threadPoolExecutor instanceof DynamicThreadPoolExecutor) {
                    DynamicThreadPoolExecutor dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) threadPoolExecutor;
                    Assert.assertEquals("executor name is error", "test-executor-name-new", dynamicThreadPoolExecutor.getExecutorName());
                }
                break;
            }
            Thread.sleep(500);
        }

        Assert.assertTrue("isUpdate status is error", executorRefresher.getIsUpdate());
        executorRefresher.reset();
    }

    @Getter
    @Setter
    private static class TestExecutorRefresher extends AbstractExecutorRefresher {

        private Boolean canSync = false;

        private Boolean canPull = false;
        private Boolean isPull = false;

        private Boolean canRefresh = false;
        private Boolean isRefresh = false;

        private Boolean canUpdate = false;
        private Boolean isUpdate = false;

        protected TestExecutorRefresher(String serverCode) {
            super(serverCode);
        }

        @Override
        protected boolean checkExecutorSync(String serverCode, String serverIp) {
            return this.canSync;
        }

        @Override
        protected List<ExecutorConfigBo> pullExecutorSync(String serverCode, String serverIp) {
            List<ExecutorConfigBo> result = new ArrayList<>();
            if (this.canPull) {
                try {
                    ExecutorConfigBo executorConfigBo = new ExecutorConfigBo();
                    executorConfigBo.setExecutorId(ExecutorMonitor.watch().keySet().stream().findFirst().orElse(""));
                    executorConfigBo.setExecutorName("test-executor-name-new");
                    executorConfigBo.setCorePoolSize(10);
                    executorConfigBo.setMaximumPoolSize(100);
                    executorConfigBo.setKeepAliveTime(1000);
                    executorConfigBo.setRejectedStrategy(RejectedStrategy.CALLER_RUNS_POLICY);
                    result.add(executorConfigBo);
                } finally {
                    this.isPull = true;
                }
            }
            return result;
        }

        @Override
        protected void doRefresh() {
            if (this.canRefresh) {
                try {
                    super.doRefresh();
                } finally {
                    this.isRefresh = true;
                }
            }
        }

        @Override
        protected void doUpdate(ExecutorConfigBo body) {
            if (this.canUpdate) {
                try {
                    super.doUpdate(body);
                } finally {
                    this.isUpdate = true;
                }
            }
        }

        public void reset() {
            this.canSync = false;
            this.canPull = false;
            this.isPull = false;
            this.canRefresh = false;
            this.isRefresh = false;
            this.canUpdate = false;
            this.isUpdate = false;
            LOG.debug("executor is reset");
        }
    }
}
