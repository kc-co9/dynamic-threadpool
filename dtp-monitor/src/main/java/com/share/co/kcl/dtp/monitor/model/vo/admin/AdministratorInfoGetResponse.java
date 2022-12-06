package com.share.co.kcl.dtp.monitor.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdministratorInfoGetResponse {

    @ApiModelProperty(value = "管理者ID")
    private String administratorId;

    @ApiModelProperty(value = "管理者名字")
    private String administratorName;
}
