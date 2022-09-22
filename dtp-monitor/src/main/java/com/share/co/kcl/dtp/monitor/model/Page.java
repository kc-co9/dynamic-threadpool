package com.share.co.kcl.dtp.monitor.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Page {

    /**
     * page no
     */
    @NotNull(message = "page no is null")
    private Integer pageNo;

    /**
     * page size
     */
    @NotNull(message = "page size is null")
    private Integer pageSize;
}
