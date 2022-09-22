package com.share.co.kcl.dtp.monitor.model;

import com.share.co.kcl.dtp.common.constants.ResultCode;
import com.share.co.kcl.dtp.common.exception.BaseException;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * result code
     */
    @ApiModelProperty(value = "result code")
    private Integer code;
    /**
     * result additional info
     */
    @ApiModelProperty(value = "result additional info")
    private String msg;
    /**
     * result content
     */
    @ApiModelProperty(value = "result content")
    private T data;

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg(), null);
    }

    public static <T> Result<T> error(ResultCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMsg(), null);
    }

    public static <T> Result<T> error(ResultCode errorCode, BaseException ex) {
        return new Result<>(errorCode.getCode(), ex.getMsg(), null);
    }

    public static <T> Result<T> error(T data) {
        return new Result<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg(), data);
    }

    public static <T> Result<T> error(ResultCode errorCode, T data) {
        return new Result<>(errorCode.getCode(), errorCode.getMsg(), data);
    }

}
