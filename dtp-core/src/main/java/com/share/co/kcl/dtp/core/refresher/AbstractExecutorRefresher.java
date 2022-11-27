package com.share.co.kcl.dtp.core.refresher;

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
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractExecutorRefresher implements Refresher {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractExecutorRefresher.class);

    private final AtomicBoolean shouldSyncFromRemote = new AtomicBoolean(true);

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
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!shouldSyncFromRemote.get()) {
                        return;
                    }
                    List<ExecutorConfigBo> refreshList = AbstractExecutorRefresher.this.doPull();
                    for (ExecutorConfigBo refreshContent : refreshList) {
                        // retry 3 times if offering refresh body is failure
                        for (int i = 0; i < 3 && !refreshQueue.offer(refreshContent); i++) {
                        }
                    }
                    shouldSyncFromRemote.set(false);
                } catch (Exception ignore) {
                    // ignore any exception
                }
            }
        }, 3000, 3000);

        new Thread(() -> {
            for (; ; ) {
                try {
                    ExecutorConfigBo refreshContent = refreshQueue.take();
                    AbstractExecutorRefresher.this.doUpdate(refreshContent);
                } catch (Exception ignore) {
                    // ignore any exception
                } finally {
                    shouldSyncFromRemote.set(true);
                }
            }
        }).start();
    }

    protected List<ExecutorConfigBo> doPull() {
        boolean isShouldSyncExecutor = AbstractExecutorRefresher.this.checkExecutorSync(serverCode, serverIp);
        if (!isShouldSyncExecutor) {
            return Collections.emptyList();
        }

        List<ExecutorConfigBo> executorConfigList = AbstractExecutorRefresher.this.pullExecutorSync(serverCode, serverIp);
        if (CollectionUtils.isEmpty(executorConfigList)) {
            return Collections.emptyList();
        }

        LOG.info("succeed pulling refresh data, executor content: {}", executorConfigList);
        return executorConfigList;
    }

    protected void doUpdate(ExecutorConfigBo refreshContent) {
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

            LOG.info("succeed updating refresh data to thread pool , update content: {}", refreshContent);
        }
    }

    /**
     * look up which should pull refresh config
     */
    protected abstract boolean checkExecutorSync(String serverCode, String serverIp);

    /**
     * pull refresh config from remote
     *
     * @return json data
     */
    protected abstract List<ExecutorConfigBo> pullExecutorSync(String serverCode, String serverIp);


}
