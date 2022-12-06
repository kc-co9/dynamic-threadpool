package com.share.co.kcl.dtp.monitor.model.vo.executor;

import com.share.co.kcl.dtp.common.utils.FunctionUtils;
import com.share.co.kcl.dtp.monitor.model.enums.LineChartDuration;
import com.share.co.kcl.dtp.monitor.model.po.entity.DtpExecutorStatisticsHistory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Data
public class ExecutorStatisticsLineChartResponse {

    private List<Map.Entry<String, LineChartPoint>> points;

    public ExecutorStatisticsLineChartResponse(LineChartDuration duration, List<DtpExecutorStatisticsHistory> points) {
        this.points = FunctionUtils.mappingList(points, point -> {
            String key = "";
            switch (duration) {
                case IN_15_MINUTES:
                case IN_1_HOURS:
                case IN_1_DAYS:
                    key = point.getCreateTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);
                    break;
                case IN_7_DAYS:
                case IN_14_DAYS:
                case IN_30_DAYS:
                    key = point.getCreateTime().toLocalDate().format(DateTimeFormatter.ISO_DATE);
                    break;
            }
            return new AbstractMap.SimpleEntry<>(key, new LineChartPoint(point));
        });
    }

    @Data
    public static class LineChartPoint {

        @ApiModelProperty(value = "线程池队列节点数量")
        private Integer executorQueueNodeCount;

        @ApiModelProperty(value = "线程池队列剩余容量")
        private Integer executorQueueRemainCapacity;

        @ApiModelProperty(value = "线程池当前线程数量")
        private Integer executorPoolSize;

        @ApiModelProperty(value = "线程池曾经出现过的最大线程池数量")
        private Integer executorLargestPoolSize;

        @ApiModelProperty(value = "线程池正在执行任务的线程数量")
        private Integer executorActiveCount;

        @ApiModelProperty(value = "线程池已被调度执行的任务数量")
        private Long executorTaskCount;

        @ApiModelProperty(value = "线程池已被执行完成的任务数量")
        private Long executorCompletedTaskCount;

        public LineChartPoint() {
        }

        public LineChartPoint(DtpExecutorStatisticsHistory statistics) {
            this.setExecutorQueueNodeCount(statistics.getExecutorQueueNodeCount());
            this.setExecutorQueueRemainCapacity(statistics.getExecutorQueueRemainCapacity());
            this.setExecutorPoolSize(statistics.getExecutorPoolSize());
            this.setExecutorLargestPoolSize(statistics.getExecutorLargestPoolSize());
            this.setExecutorActiveCount(statistics.getExecutorActiveCount());
            this.setExecutorTaskCount(statistics.getExecutorTaskCount());
            this.setExecutorCompletedTaskCount(statistics.getExecutorCompletedTaskCount());
        }
    }
}
