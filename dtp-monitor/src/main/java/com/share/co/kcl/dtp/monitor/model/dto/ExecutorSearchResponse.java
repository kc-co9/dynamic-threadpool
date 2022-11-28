package com.share.co.kcl.dtp.monitor.model.dto;

import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ExecutorSearchResponse {

    private List<ServerExecutor> executorList;

    public ExecutorSearchResponse() {
    }

    public ExecutorSearchResponse(List<ExecutorConfigBo> configResult) {
        this.executorList = configResult.stream().map(executorConfigBo -> {
            ServerExecutor serverExecutor = new ServerExecutor();
            serverExecutor.setExecutorId(executorConfigBo.getExecutorId());
            serverExecutor.setExecutorName(executorConfigBo.getExecutorName());
            serverExecutor.setExecutorConfig(executorConfigBo);
            return serverExecutor;
        }).collect(Collectors.toList());
    }

    @Data
    public static class ServerExecutor {

        @ApiModelProperty(value = "线程池ID")
        private String executorId;

        @ApiModelProperty(value = "线程池名称")
        private String executorName;

        @ApiModelProperty(value = "线程池配置信息")
        private ExecutorConfigBo executorConfig;
    }
}
