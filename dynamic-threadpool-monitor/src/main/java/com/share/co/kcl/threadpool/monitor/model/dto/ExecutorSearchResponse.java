package com.share.co.kcl.threadpool.monitor.model.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.share.co.kcl.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.common.model.bo.ExecutorStatisticsBo;
import com.share.co.kcl.common.utils.FunctionUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class ExecutorSearchResponse {

    private List<ServerExecutor> executorList;

    public ExecutorSearchResponse() {
    }

    public ExecutorSearchResponse(List<ExecutorConfigBo> configResult, List<ExecutorStatisticsBo> statisticsResult) {
        Map<String, ExecutorStatisticsBo> statisticsMap = FunctionUtils.mappingMap(statisticsResult, ExecutorStatisticsBo::getExecutorId, Function.identity());
        this.executorList = configResult.stream().map(executorConfigBo -> {
            ServerExecutor serverExecutor = new ServerExecutor();
            serverExecutor.setExecutorId(executorConfigBo.getExecutorId());
            serverExecutor.setExecutorName(executorConfigBo.getExecutorName());
            serverExecutor.setConfigBody(executorConfigBo);
            serverExecutor.setStatisticsBody(statisticsMap.get(executorConfigBo.getExecutorId()));
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
        private ExecutorConfigBo configBody;

        @ApiModelProperty(value = "线程池数据统计")
        private ExecutorStatisticsBo statisticsBody;
    }
}
