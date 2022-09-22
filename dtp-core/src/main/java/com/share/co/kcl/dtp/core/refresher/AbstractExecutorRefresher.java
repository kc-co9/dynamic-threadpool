package com.share.co.kcl.dtp.core.refresher;

import com.share.co.kcl.dtp.common.enums.RejectedStrategy;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.utils.AddressUtils;
import com.share.co.kcl.dtp.core.DynamicThreadPoolExecutor;
import com.share.co.kcl.dtp.core.monitor.ExecutorMonitor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractExecutorRefresher implements Refresher {

    private final AtomicBoolean shouldSyncFromRemote = new AtomicBoolean(true);

    private final BlockingQueue<ExecutorConfigBo> refreshQueue = new LinkedBlockingQueue<>();

    @Getter
    @Setter
    protected String serverCode;
    @Getter
    @Setter
    protected String serverIp;

    protected AbstractExecutorRefresher(String serverCode) {
        this.serverCode = serverCode;
        this.serverIp = AddressUtils.getLocalIpList().get(0);
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
                    AbstractExecutorRefresher.this.doRefresh();
                    shouldSyncFromRemote.set(false);
                } catch (Exception ignore) {
                }
            }
        }, 3000, 3000);

        new Thread(() -> {
            for (; ; ) {
                try {
                    ExecutorConfigBo body = refreshQueue.take();
                    AbstractExecutorRefresher.this.doUpdate(body);
                } catch (InterruptedException ignore) {
                } catch (Exception ignore) {
                } catch (Throwable ignore) {
                } finally {
                    shouldSyncFromRemote.set(true);
                }
            }
        }).start();
    }

    protected void doRefresh() {
        boolean isShouldSyncExecutor = AbstractExecutorRefresher.this.checkExecutorSync(serverCode, serverIp);
        if (!isShouldSyncExecutor) {
            return;
        }

        List<ExecutorConfigBo> executorConfigList = AbstractExecutorRefresher.this.pullExecutorSync(serverCode, serverIp);
        if (CollectionUtils.isEmpty(executorConfigList)) {
            return;
        }
        for (ExecutorConfigBo executorConfigBo : executorConfigList) {
            // retry 3 times if offering refresh body is failure
            for (int i = 0; i < 3 && !refreshQueue.offer(executorConfigBo); i++) {
            }
        }
    }

    protected void doUpdate(ExecutorConfigBo body) {
        ThreadPoolExecutor threadPool = ExecutorMonitor.watch().get(body.getExecutorId());
        if (Objects.nonNull(threadPool)) {
            threadPool.setCorePoolSize(body.getCorePoolSize());
            threadPool.setMaximumPoolSize(body.getMaximumPoolSize());
            threadPool.setKeepAliveTime(body.getKeepAliveTime(), TimeUnit.SECONDS);
            threadPool.setRejectedExecutionHandler(
                    RejectedStrategy.parse(body.getRejectedStrategy()).orElse(RejectedStrategy.defaultRejectedHandler()));
            if (threadPool instanceof DynamicThreadPoolExecutor) {
                DynamicThreadPoolExecutor dynamicThreadPool = ((DynamicThreadPoolExecutor) threadPool);
                dynamicThreadPool.setExecutorName(body.getExecutorName());
            }
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
