package com.share.co.kcl.threadpool.spring.meta;

import lombok.Data;

@Data
public class DynamicPoolConfig {

    /**
     * the server code
     */
    private String serverCode;

    /**
     * the server monitor domain
     */
    private String serverMonitor;
}
