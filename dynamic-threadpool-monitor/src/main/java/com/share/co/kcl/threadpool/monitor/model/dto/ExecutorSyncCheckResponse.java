package com.share.co.kcl.threadpool.monitor.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorSyncCheckResponse {

    @ApiModelProperty(value = "线程池是否已同步")
    private Boolean isSync;
}
