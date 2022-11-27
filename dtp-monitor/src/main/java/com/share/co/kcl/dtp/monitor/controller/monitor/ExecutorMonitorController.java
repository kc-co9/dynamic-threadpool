package com.share.co.kcl.dtp.monitor.controller.monitor;

import com.share.co.kcl.dtp.common.exception.BusinessException;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.monitor.factory.SpringDomainFactory;
import com.share.co.kcl.dtp.monitor.model.dto.*;
import com.share.co.kcl.dtp.monitor.model.po.DtpServer;
import com.share.co.kcl.dtp.monitor.security.annotation.Sign;
import com.share.co.kcl.dtp.monitor.service.DtpExecutorService;
import com.share.co.kcl.dtp.monitor.service.DtpServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = "线程池监听路由")
@RequestMapping(value = "/monitor/executor")
public class ExecutorMonitorController {

    @Autowired
    private DtpServerService dtpServerService;
    @Autowired
    private DtpExecutorService dtpExecutorService;
    @Autowired
    private SpringDomainFactory springDomainFactory;

    @Sign
    @ApiOperation(value = "上报线程池信息")
    @PostMapping(value = "/v1/reportExecutorInfo")
    public void reportExecutorInfo(@RequestBody @Validated ExecutorReportRequest request) {
        DtpServer server = dtpServerService.getByCode(request.getServerCode())
                .orElseThrow(() -> new BusinessException("server is down or not exist"));
        dtpExecutorService.reportExecutorInfo(server.getId(), request.getServerIp(), request.getExecutorList());
    }

    @Sign
    @ApiOperation(value = "检测线程池同步")
    @GetMapping(value = "/v1/checkExecutorSync")
    public ExecutorSyncCheckResponse checkExecutorSync(@ModelAttribute @Validated ExecutorSyncCheckRequest request) {
        DtpServer server = dtpServerService.getByCode(request.getServerCode())
                .orElseThrow(() -> new BusinessException("server is down or not exist"));
        boolean isSync = dtpExecutorService.checkExecutorSync(server.getId(), request.getServerIp());
        return new ExecutorSyncCheckResponse(isSync);
    }

    @Sign
    @ApiOperation(value = "查询线程池同步数据")
    @GetMapping(value = "/v1/lookupExecutorSync")
    public ExecutorSyncLookupResponse lookupExecutorSync(@ModelAttribute @Validated ExecutorSyncLookupRequest request) {
        DtpServer server = dtpServerService.getByCode(request.getServerCode())
                .orElseThrow(() -> new BusinessException("server is down or not exist"));
        List<ExecutorConfigBo> executorConfigList = dtpExecutorService.lookupExecutorInfo(server.getId(), request.getServerIp());
        List<ExecutorConfigBo> configResult = executorConfigList.stream()
                .filter(item -> springDomainFactory.newExecutorMonitor(server.getId(), request.getServerIp()).isSync(item.getExecutorId()))
                .collect(Collectors.toList());
        return new ExecutorSyncLookupResponse(configResult);
    }


}
