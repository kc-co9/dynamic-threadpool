package com.share.co.kcl.dtp.monitor.model.dto;

import com.share.co.kcl.dtp.common.model.bo.ExecutorConfigBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorSyncLookupResponse {

    @ApiModelProperty(value = "线程池配置信息")
    private List<ExecutorConfigBo> executorConfigList;
}
