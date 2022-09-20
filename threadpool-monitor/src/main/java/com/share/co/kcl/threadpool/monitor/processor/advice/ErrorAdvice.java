package com.share.co.kcl.threadpool.monitor.processor.advice;

import com.share.co.kcl.common.constants.ResultCode;
import com.share.co.kcl.common.exception.BusinessException;
import com.share.co.kcl.common.exception.HttpException;
import com.share.co.kcl.common.exception.ToastException;
import com.share.co.kcl.threadpool.monitor.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class ErrorAdvice implements HandlerExceptionResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorAdvice.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        mv.addObject(Result.error());
        return mv;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        return Result.error(ResultCode.PARAMS_ERROR);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpException.class)
    public Result<Void> httpExceptionHandler(HttpException ex) {
        return Result.error(ResultCode.NETWORK_ERROR);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ToastException.class)
    public Result<Void> toastExceptionHandler(ToastException ex) {
        return Result.error(ResultCode.OPERATE_ERROR, ex);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BusinessException.class)
    public Result<Void> businessExceptionHandler(BusinessException ex) {
        return Result.error(ResultCode.BUSINESS_ERROR, ex);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public Result<Void> exceptionHandler(Exception ex) {
        LOG.error("unknown error,", ex);
        return Result.error(ResultCode.ERROR);
    }

}
