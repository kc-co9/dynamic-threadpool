package com.share.co.kcl.dtp.monitor.factory;

import com.share.co.kcl.dtp.monitor.model.Domain;
import com.share.co.kcl.dtp.monitor.model.domain.ExecutorMonitorDo;
import com.share.co.kcl.dtp.monitor.model.domain.ServerMonitorDo;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringDomainFactory extends Domain {

    private static final Map<Long, ServerMonitorDo> serverMonitorRepository = new ConcurrentHashMap<>();
    private static final Map<String, ExecutorMonitorDo> executorMonitorRepository = new ConcurrentHashMap<>();

    public SpringDomainFactory(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public ServerMonitorDo newServerMonitor(Long serverId) {
        return serverMonitorRepository.computeIfAbsent(serverId,
                k -> new ServerMonitorDo(serverId, this.applicationContext));
    }

    public ExecutorMonitorDo newExecutorMonitor(Long serverId, String serverIp) {
        String key = serverId + ":" + serverIp;
        return executorMonitorRepository.computeIfAbsent(key,
                k -> new ExecutorMonitorDo(serverId, serverIp, this.applicationContext));
    }
}
