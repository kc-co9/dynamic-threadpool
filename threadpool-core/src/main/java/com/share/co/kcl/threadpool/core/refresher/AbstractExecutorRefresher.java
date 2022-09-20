package com.share.co.kcl.threadpool.core.refresher;

import com.share.co.kcl.common.enums.RejectedStrategy;
import com.share.co.kcl.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.common.utils.AddressUtils;
import com.share.co.kcl.threadpool.core.monitor.ExecutorMonitor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractExecutorRefresher implements Refresher {

    private final AtomicBoolean shouldSyncFromRemote = new AtomicBoolean(false);

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
                    if (shouldSyncFromRemote.get()) {
                        shouldSyncFromRemote.set(false);
                        return;
                    }

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
                    shouldSyncFromRemote.set(true);
                } catch (Exception ignore) {
                }
            }
        }, 3000, 3000);
    }

    @Override
    public void update() {
        for (; ; ) {
            try {
                ExecutorConfigBo body = refreshQueue.take();

                ThreadPoolExecutor threadPool = ExecutorMonitor.watch().get(body.getExecutorId());
                threadPool.setCorePoolSize(body.getCorePoolSize());
                threadPool.setMaximumPoolSize(body.getMaximumPoolSize());
                threadPool.setKeepAliveTime(body.getKeepAliveTime(), TimeUnit.SECONDS);
                RejectedExecutionHandler rejectedExecutionHandler = RejectedStrategy.parse(body.getRejectedStrategy());
                if (Objects.nonNull(rejectedExecutionHandler)) {
                    threadPool.setRejectedExecutionHandler(rejectedExecutionHandler);
                }
            } catch (InterruptedException ignore) {
            } catch (Exception ignore) {
            } catch (Throwable ignore) {
            } finally {
                shouldSyncFromRemote.set(false);
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
