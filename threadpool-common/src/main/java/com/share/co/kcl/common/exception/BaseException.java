package com.share.co.kcl.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    protected final String msg;

    public BaseException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
