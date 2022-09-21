package com.share.co.kcl.threadpool.monitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.share.co.kcl.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.common.model.bo.ExecutorStatisticsBo;
import com.share.co.kcl.common.utils.FunctionUtils;
import com.share.co.kcl.threadpool.monitor.annotation.Lock;
import com.share.co.kcl.threadpool.monitor.factory.SpringDomainFactory;
import com.share.co.kcl.threadpool.monitor.model.domain.ServerMonitorDo;
import com.share.co.kcl.threadpool.monitor.model.po.DtpServer;
import com.share.co.kcl.threadpool.monitor.model.dto.*;
import com.share.co.kcl.threadpool.monitor.service.DtpExecutorService;
import com.share.co.kcl.threadpool.monitor.service.DtpServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Api(tags = "线程池控制路由")
@RequestMapping(value = "/console")
public class ConsoleController {

    @Autowired
    private DtpServerService dtpServerService;
    @Autowired
    private DtpExecutorService dtpExecutorService;
    @Autowired
    private SpringDomainFactory springDomainFactory;

    @ApiOperation(value = "获取服务实例列表")
    @GetMapping(value = "/v1/getServerList")
    public ServerSearchResponse getServerList(@ModelAttribute @Validated ServerSearchRequest request) {
        IPage<DtpServer> serverList = dtpServerService.page(new Page<>(request.getPageNo(), request.getPageSize()), dtpServerService.getQueryWrapper()
                .eq(Objects.nonNull(request.getServerId()), DtpServer::getId, request.getServerId())
                .eq(StringUtils.isNotBlank(request.getServerCode()), DtpServer::getServerCode, request.getServerCode())
                .eq(StringUtils.isNotBlank(request.getServerName()), DtpServer::getServerName, request.getServerName()));
        List<ServerMonitorDo> serverMonitorDos = FunctionUtils.mappingList(serverList.getRecords(), o -> springDomainFactory.newServerMonitor(o.getId()));
        return new ServerSearchResponse(serverList, serverMonitorDos);
    }

    @ApiOperation(value = "添加服务实例")
    @PostMapping(value = "/v1/insertServer")
    public void insertServer(@RequestBody @Validated ServerInsertRequest request) {
        DtpServer server = new DtpServer();
        server.setServerCode(request.getServerCode());
        server.setServerName(request.getServerName());
        dtpServerService.save(server);
    }

    @ApiOperation(value = "更新服务实例")
    @PostMapping(value = "/v1/updateServer")
    @Lock(key = "'server-operate:' + #request.serverId", prefix = false, timeout = 3)
    public void updateServer(@RequestBody @Validated ServerUpdateRequest request) {
        dtpServerService.update(dtpServerService.getUpdateWrapper()
                .set(DtpServer::getServerCode, request.getServerCode())
                .set(DtpServer::getServerName, request.getServerName())
                .eq(DtpServer::getId, request.getServerId()));
    }

    @ApiOperation(value = "删除服务实例")
    @PostMapping(value = "/v1/removeServer")
    @Lock(key = "'server-operate:' + #request.serverId", prefix = false, timeout = 3)
    public void removeServer(@RequestBody @Validated ServerRemoveRequest request) {
        dtpServerService.removeById(request.getServerId());
    }

    @ApiOperation(value = "获取线程池配置")
    @GetMapping(value = "/v1/getExecutorList")
    public ExecutorSearchResponse getExecutorList(@ModelAttribute @Validated ExecutorSearchRequest request) {
        List<ExecutorConfigBo> executorConfigList = dtpExecutorService.lookupExecutorInfo(request.getServerId(), request.getServerIp());
        List<ExecutorStatisticsBo> executorStatisticsList = dtpExecutorService.lookupExecutorStatistics(request.getServerId(), request.getServerIp());
        List<ExecutorConfigBo> configResult = executorConfigList.stream()
                .filter(item -> StringUtils.isBlank(request.getExecutorId()) || item.getExecutorId().equals(request.getExecutorId()))
                .filter(item -> StringUtils.isBlank(request.getExecutorName()) || item.getExecutorName().equals(request.getExecutorName()))
                .collect(Collectors.toList());
        List<ExecutorStatisticsBo> statisticsResult = executorStatisticsList.stream()
                .filter(item -> StringUtils.isBlank(request.getExecutorId()) || item.getExecutorId().equals(request.getExecutorId()))
                .filter(item -> StringUtils.isBlank(request.getExecutorName()) || item.getExecutorName().equals(request.getExecutorName()))
                .collect(Collectors.toList());
        return new ExecutorSearchResponse(configResult, statisticsResult);
    }

    @ApiOperation(value = "配置线程池")
    @PostMapping(value = "/v1/configureExecutor")
    public void configureExecutor(@RequestBody @Validated ExecutorConfigureRequest request) {
        dtpExecutorService.configureExecutor(request.getServerId(), request.getServerIp(), request.getExecutorId(), request.getConfigBody());
    }
}
