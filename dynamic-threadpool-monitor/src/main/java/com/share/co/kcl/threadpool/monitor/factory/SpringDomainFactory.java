package com.share.co.kcl.threadpool.monitor.factory;

import com.share.co.kcl.threadpool.monitor.model.Domain;
import com.share.co.kcl.threadpool.monitor.model.domain.ExecutorMonitorDo;
import com.share.co.kcl.threadpool.monitor.model.domain.ServerMonitorDo;
import org.springframework.context.ApplicationContext;

public class SpringDomainFactory extends Domain {

    public SpringDomainFactory(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public ServerMonitorDo newServerMonitor(Long serverId) {
        return new ServerMonitorDo(serverId, this.applicationContext);
    }

    public ExecutorMonitorDo newExecutorMonitor(Long serverId, String serverIp) {
        return new ExecutorMonitorDo(serverId, serverIp, this.applicationContext);
    }
}
