package com.share.co.kcl.dtp.monitor.service;

import com.share.co.kcl.dtp.common.exception.BusinessException;
import com.share.co.kcl.dtp.monitor.annotation.Lock;
import com.share.co.kcl.dtp.monitor.dao.DtpServerDao;
import com.share.co.kcl.dtp.monitor.model.domain.ServerMonitorDo;
import com.share.co.kcl.dtp.monitor.factory.SpringDomainFactory;
import com.share.co.kcl.dtp.monitor.model.po.DtpServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DtpServerService extends DtpBaseService<DtpServerDao, DtpServer> {

    @Autowired
    private SpringDomainFactory springDomainFactory;

    @Lock(key = "#serverCode + ':' + #serverIp", timeout = 3L, waittime = 1L)
    public void reportHealth(String serverCode, String serverIp) {
        DtpServer dtpServer = this.getByCode(serverCode)
                .orElseThrow(() -> new BusinessException("server is not exist"));

        ServerMonitorDo serverMonitorDo = springDomainFactory.newServerMonitor(dtpServer.getId());

        boolean isRunning = serverMonitorDo.isRunning() || serverMonitorDo.start();
        if (!isRunning) {
            throw new BusinessException("report error, server is down");
        }

        boolean isReport = serverMonitorDo.report(serverIp);
        if (!isReport) {
            throw new BusinessException("server report failure");
        }
    }

    public Optional<DtpServer> getByCode(String serverCode) {
        if (StringUtils.isBlank(serverCode)) {
            return Optional.empty();
        }
        return this.getFirst(this.getQueryWrapper().eq(DtpServer::getServerCode, serverCode));
    }

}
