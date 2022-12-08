package com.share.co.kcl.dtp.core.refresher;

import com.google.common.annotations.VisibleForTesting;
import com.share.co.kcl.dtp.common.enums.RejectedStrategy;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.utils.NetworkUtils;
import com.share.co.kcl.dtp.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public abstract class AbstractExecutorRefresher implements Refresher {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractExecutorRefresher.class);

    private final ScheduledExecutorService consumerThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private final ScheduledExecutorService producerThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    private final BlockingQueue<ExecutorConfigBo> refreshQueue = new LinkedBlockingQueue<>();

    @Getter
    @Setter
    protected String serverCode;
    @Getter
    @Setter
    protected String serverSecret;
    @Getter
    @Setter
    protected String serverIp;

    protected AbstractExecutorRefresher(String serverCode, String serverSecret) {
        this.serverCode = serverCode;
        this.serverSecret = serverSecret;
        this.serverIp = NetworkUtils.getLocalIpList().get(0);
    }

    @Override
    public void refresh() {
        producerThreadPoolExecutor.scheduleWithFixedDelay(this::produce, 3000L, 3000L, TimeUnit.MILLISECONDS);
        consumerThreadPoolExecutor.scheduleWithFixedDelay(this::consume, 0L, 1L, TimeUnit.MILLISECONDS);
    }

    @VisibleForTesting
    void produce() {
        try {
            drainToQueue(fetchUpdate());
        } catch (Exception ex) {
            // ignore any exception
            LOG.debug("produce config throw exception", ex);
        }
    }

    @VisibleForTesting
    void consume() {
        try {
            executeUpdate(drainFromQueue());
        } catch (Exception ex) {
            // ignore any exception
            LOG.debug("consume config throw exception", ex);
        }
    }

    protected List<ExecutorConfigBo> fetchUpdate() {
        boolean isNeedToSyncExecutor = this.checkUpdate(serverCode, serverIp);
        if (!isNeedToSyncExecutor) {
            return Collections.emptyList();
        }

        List<ExecutorConfigBo> executorConfigList = this.fetchUpdate(serverCode, serverIp);
        if (CollectionUtils.isEmpty(executorConfigList)) {
            return Collections.emptyList();
        }

        LOG.info("succeed pulling refresh data, executor content: {}", executorConfigList);
        return executorConfigList;
    }

    protected void executeUpdate(ExecutorConfigBo refreshContent) {
        if (Objects.isNull(refreshContent)) {
            return;
        }
        ThreadPoolExecutor executor = ExecutorMonitor.watch().get(refreshContent.getExecutorId());
        if (Objects.nonNull(executor)) {
            executor.setCorePoolSize(refreshContent.getCorePoolSize());
            executor.setMaximumPoolSize(refreshContent.getMaximumPoolSize());
            executor.setKeepAliveTime(refreshContent.getKeepAliveTime(), TimeUnit.SECONDS);
            executor.setRejectedExecutionHandler(
                    RejectedStrategy.parse(refreshContent.getRejectedStrategy()).orElse(RejectedStrategy.defaultRejectedHandler()));
            if (executor instanceof DynamicThreadPoolExecutor) {
                DynamicThreadPoolExecutor dynamicExecutor = ((DynamicThreadPoolExecutor) executor);
                dynamicExecutor.setExecutorName(refreshContent.getExecutorName());
            }
        }
    }

    private ExecutorConfigBo drainFromQueue() throws InterruptedException {
        @SuppressWarnings("UnnecessaryLocalVariable")
        ExecutorConfigBo refreshContent = refreshQueue.poll(10, TimeUnit.SECONDS);
        return refreshContent;
    }

    private void drainToQueue(List<ExecutorConfigBo> refreshList) {
        if (CollectionUtils.isEmpty(refreshList)) {
            return;
        }
        for (ExecutorConfigBo refreshContent : refreshList) {
            // noinspection StatementWithEmptyBody
            for (int i = 0; i < 3 && !refreshQueue.offer(refreshContent); i++) {
                // retry 3 times if offering refresh body is failure
            }
        }
    }

    /**
     * look up which should pull refresh config
     */
    protected abstract boolean checkUpdate(String serverCode, String serverIp);

    /**
     * fetch refresh config from remote
     *
     * @return json data
     */
    protected abstract List<ExecutorConfigBo> fetchUpdate(String serverCode, String serverIp);
}
