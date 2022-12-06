package com.share.co.kcl.dtp.monitor.model.vo.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdministratorSignInResponse {

    @ApiModelProperty(value = "授权TOKEN")
    private String authToken;
}
