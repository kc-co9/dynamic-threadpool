package com.share.co.kcl.dtp.monitor.model.vo.executor;

import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import com.share.co.kcl.dtp.common.model.bo.ExecutorStatisticsBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorDetailGetResponse {

    @ApiModelProperty(value = "线程池配置信息")
    private ExecutorConfigBo executorConfig;

    @ApiModelProperty(value = "线程池统计信息")
    private ExecutorStatisticsBo executorStatistics;
}
