package com.share.co.kcl.dtp.monitor.controller.console;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.share.co.kcl.dtp.common.utils.FunctionUtils;
import com.share.co.kcl.dtp.monitor.processor.annotation.Lock;
import com.share.co.kcl.dtp.monitor.factory.SpringDomainFactory;
import com.share.co.kcl.dtp.monitor.model.domain.ServerMonitorDo;
import com.share.co.kcl.dtp.monitor.model.dto.*;
import com.share.co.kcl.dtp.monitor.model.po.DtpServer;
import com.share.co.kcl.dtp.monitor.security.annotation.Auth;
import com.share.co.kcl.dtp.monitor.service.DtpServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@Api(tags = "服务控制路由")
@RequestMapping(value = "/console/server")
public class ServerConsoleController {

    @Autowired
    private DtpServerService dtpServerService;
    @Autowired
    private SpringDomainFactory springDomainFactory;

    @Auth
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

    @Auth
    @ApiOperation(value = "添加服务实例")
    @PostMapping(value = "/v1/insertServer")
    public void insertServer(@RequestBody @Validated ServerInsertRequest request) {
        DtpServer server = new DtpServer();
        server.setServerCode(request.getServerCode());
        server.setServerName(request.getServerName());
        server.setServerSecret(request.getServerSecret());
        dtpServerService.save(server);
    }

    @Auth
    @ApiOperation(value = "更新服务实例")
    @PostMapping(value = "/v1/updateServer")
    @Lock(key = "'server-operate:' + #request.serverId", prefix = false, timeout = 3)
    public void updateServer(@RequestBody @Validated ServerUpdateRequest request) {
        dtpServerService.update(dtpServerService.getUpdateWrapper()
                .set(DtpServer::getServerCode, request.getServerCode())
                .set(DtpServer::getServerName, request.getServerName())
                .set(DtpServer::getServerSecret, request.getServerSecret())
                .eq(DtpServer::getId, request.getServerId()));
    }

    @Auth
    @ApiOperation(value = "删除服务实例")
    @PostMapping(value = "/v1/deleteServer")
    @Lock(key = "'server-operate:' + #request.serverId", prefix = false, timeout = 3)
    public void deleteServer(@RequestBody @Validated ServerDeleteRequest request) {
        dtpServerService.removeById(request.getServerId());
    }
}
