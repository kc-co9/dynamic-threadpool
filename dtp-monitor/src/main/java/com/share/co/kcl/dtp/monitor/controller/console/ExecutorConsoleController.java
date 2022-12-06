package com.share.co.kcl.dtp.monitor.controller.console;

import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import com.share.co.kcl.dtp.monitor.model.po.entity.DtpExecutorStatisticsHistory;
import com.share.co.kcl.dtp.monitor.model.vo.executor.*;
import com.share.co.kcl.dtp.monitor.security.annotation.Auth;
import com.share.co.kcl.dtp.monitor.service.DtpExecutorService;
import com.share.co.kcl.dtp.monitor.service.DtpExecutorStatisticsHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Api(tags = "线程池控制路由")
@RequestMapping(value = "/console/executor")
public class ExecutorConsoleController {

    @Autowired
    private DtpExecutorService dtpExecutorService;
    @Autowired
    private DtpExecutorStatisticsHistoryService dtpExecutorStatisticsHistoryService;

    @Auth
    @ApiOperation(value = "获取线程池配置列表")
    @GetMapping(value = "/v1/getExecutorList")
    public ExecutorSearchResponse getExecutorList(@ModelAttribute @Validated ExecutorSearchRequest request) {
        List<ExecutorConfigBo> executorConfigList = dtpExecutorService.lookupExecutorInfo(request.getServerId(), request.getServerIp());
        List<ExecutorConfigBo> configResult = executorConfigList.stream()
                .filter(item -> StringUtils.isBlank(request.getExecutorId()) || item.getExecutorId().equals(request.getExecutorId()))
                .filter(item -> StringUtils.isBlank(request.getExecutorName()) || item.getExecutorName().equals(request.getExecutorName()))
                .collect(Collectors.toList());
        return new ExecutorSearchResponse(configResult);
    }

    @Auth
    @ApiOperation(value = "获取线程池配置详情")
    @GetMapping(value = "/v1/getExecutorDetail")
    public ExecutorDetailGetResponse getExecutorDetail(@ModelAttribute @Validated ExecutorDetailGetRequest request) {
        List<ExecutorConfigBo> executorConfigList = dtpExecutorService.lookupExecutorInfo(request.getServerId(), request.getServerIp());
        List<ExecutorStatisticsBo> executorStatisticsList = dtpExecutorService.lookupExecutorStatistics(request.getServerId(), request.getServerIp());
        Optional<ExecutorConfigBo> executorConfigBo = executorConfigList.stream()
                .filter(item -> StringUtils.isBlank(request.getExecutorId()) || item.getExecutorId().equals(request.getExecutorId()))
                .findFirst();
        Optional<ExecutorStatisticsBo> executorStatisticsBo = executorStatisticsList.stream()
                .filter(item -> StringUtils.isBlank(request.getExecutorId()) || item.getExecutorId().equals(request.getExecutorId()))
                .findFirst();
        return new ExecutorDetailGetResponse(executorConfigBo.orElse(null), executorStatisticsBo.orElse(null));
    }

    @Auth
    @ApiOperation(value = "获取线程池数据统计-折线图")
    @GetMapping(value = "/v1/getExecutorStatisticsLineChart")
    public ExecutorStatisticsLineChartResponse getExecutorStatisticsLineChart(@ModelAttribute @Validated ExecutorStatisticsLineChartRequest request) {
        List<DtpExecutorStatisticsHistory> result =
                dtpExecutorStatisticsHistoryService.getLineChart(request.getServerId(), request.getServerIp(), request.getExecutorId(), request.getDuration());
        return new ExecutorStatisticsLineChartResponse(request.getDuration(), result);
    }

    @Auth
    @ApiOperation(value = "配置线程池")
    @PostMapping(value = "/v1/configureExecutor")
    public void configureExecutor(@RequestBody @Validated ExecutorConfigureRequest request) {
        dtpExecutorService.configureExecutor(request.getServerId(), request.getServerIp(), request.getExecutorId(), request.getExecutorConfig());
    }
}
