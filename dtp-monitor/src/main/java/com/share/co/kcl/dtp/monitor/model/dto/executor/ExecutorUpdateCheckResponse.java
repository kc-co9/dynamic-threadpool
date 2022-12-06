package com.share.co.kcl.dtp.monitor.model.dto.executor;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorUpdateCheckResponse {

    @ApiModelProperty(value = "线程池是否需要同步")
    private Boolean isNeedToSync;
}
