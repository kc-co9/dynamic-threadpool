package com.share.co.kcl.dtp.monitor.controller.monitor;

import com.share.co.kcl.dtp.monitor.model.dto.*;
import com.share.co.kcl.dtp.monitor.security.annotation.Sign;
import com.share.co.kcl.dtp.monitor.service.DtpServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "服务监听路由")
@RequestMapping(value = "/monitor/server")
public class ServerMonitorController {

    @Autowired
    private DtpServerService dtpServerService;

    @Sign
    @ApiOperation(value = "上报实例心跳")
    @GetMapping(value = "/v1/reportServerHealth")
    public void reportServerHealth(@ModelAttribute @Validated ServerHealthReportRequest request) {
        dtpServerService.reportHealth(request.getServerCode(), request.getServerIp());
    }

    @Sign
    @ApiOperation(value = "上报实例配置")
    @PostMapping(value = "/v1/reportServerConfig")
    public void reportServerConfig(@RequestBody @Validated ServerConfigReportRequest request) {
        dtpServerService.reportConfig(request.getServerCode(), request.getServerIp(), request.getCpuNum(), request.getMemorySize());
    }
}
