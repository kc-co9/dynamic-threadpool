package com.share.co.kcl.dtp.monitor.controller.monitor;

import com.share.co.kcl.dtp.common.exception.BusinessException;
import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.monitor.factory.SpringDomainFactory;
import com.share.co.kcl.dtp.monitor.model.dto.executor.*;
import com.share.co.kcl.dtp.monitor.model.po.entity.DtpServer;
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

    @Sign
    @ApiOperation(value = "上报线程池配置信息")
    @PostMapping(value = "/v1/reportExecutorConfig")
    public void reportExecutorConfig(@RequestBody @Validated ExecutorConfigReportRequest request) {
        DtpServer server = dtpServerService.getByCode(request.getServerCode())
                .orElseThrow(() -> new BusinessException("server is down or not exist"));
        dtpExecutorService.reportExecutorConfig(server.getId(), request.getServerIp(), request.getExecutorConfigList());
    }

    @Sign
    @ApiOperation(value = "上报线程池统计信息")
    @PostMapping(value = "/v1/reportExecutorStatistics")
    public void reportExecutorStatistics(@RequestBody @Validated ExecutorStatisticsReportRequest request) {
        DtpServer server = dtpServerService.getByCode(request.getServerCode())
                .orElseThrow(() -> new BusinessException("server is down or not exist"));
        dtpExecutorService.reportExecutorStatistics(server.getId(), request.getServerIp(), request.getExecutorStatisticsList());
    }

    @Sign
    @ApiOperation(value = "检测线程池更新")
    @GetMapping(value = "/v1/checkExecutorUpdate")
    public ExecutorUpdateCheckResponse checkExecutorUpdate(@ModelAttribute @Validated ExecutorUpdateCheckRequest request) {
        DtpServer server = dtpServerService.getByCode(request.getServerCode())
                .orElseThrow(() -> new BusinessException("server is down or not exist"));
        boolean isNeedToSync = dtpExecutorService.checkExecutorUpdate(server.getId(), request.getServerIp());
        return new ExecutorUpdateCheckResponse(isNeedToSync);
    }

    @Sign
    @ApiOperation(value = "查询线程池更新数据")
    @GetMapping(value = "/v1/lookupExecutorUpdate")
    public ExecutorUpdateLookupResponse lookupExecutorUpdate(@ModelAttribute @Validated ExecutorUpdateLookupRequest request) {
        DtpServer server = dtpServerService.getByCode(request.getServerCode())
                .orElseThrow(() -> new BusinessException("server is down or not exist"));
        List<ExecutorConfigBo> result = dtpExecutorService.lookupExecutorUpdate(server.getId(), request.getServerIp());
        return new ExecutorUpdateLookupResponse(result);
    }


}
