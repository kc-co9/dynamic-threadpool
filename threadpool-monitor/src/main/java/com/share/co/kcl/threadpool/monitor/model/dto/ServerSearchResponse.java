package com.share.co.kcl.threadpool.monitor.model.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.share.co.kcl.common.utils.FunctionUtils;
import com.share.co.kcl.threadpool.monitor.model.domain.ServerMonitorDo;
import com.share.co.kcl.threadpool.monitor.model.po.DtpServer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Data
public class ServerSearchResponse {

    private IPage<Server> serverList;

    public ServerSearchResponse() {
    }

    public ServerSearchResponse(IPage<DtpServer> serverList, List<ServerMonitorDo> serverMonitorDos) {
        Map<Long, ServerMonitorDo> serverMonitorMap = FunctionUtils.mappingMap(serverMonitorDos, ServerMonitorDo::getServerId, Function.identity());
        this.serverList = serverList.convert(dtpServer -> {
            Server server = new Server();
            server.setServerId(dtpServer.getId());
            server.setServerCode(dtpServer.getServerCode());
            server.setServerName(dtpServer.getServerName());
            server.setServerIpList(
                    Optional.ofNullable(serverMonitorMap.get(dtpServer.getId()))
                            .map(ServerMonitorDo::lookup)
                            .orElse(Collections.emptyList()));
            server.setIsRunning(
                    Optional.ofNullable(serverMonitorMap.get(dtpServer.getId()))
                            .map(ServerMonitorDo::isRunning)
                            .orElse(false));
            return server;
        });
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Server {
        @ApiModelProperty(value = "服务ID")
        private Long serverId;

        @ApiModelProperty(name = "服务代码")
        private String serverCode;

        @ApiModelProperty(name = "服务名称")
        private String serverName;

        @ApiModelProperty(name = "服务正在运行IP")
        private List<String> serverIpList;

        @ApiModelProperty(name = "服务是否正在运行")
        private Boolean isRunning;

    }

}
