package com.jwxt.exception;

import com.jwxt.bean.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;


/**
 * @author nitmali
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("参数解析失败", e);
        return new Response().failure("could_not_read_json", HttpStatus.BAD_REQUEST.value());
    }

    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("不支持当前请求方法", e);
        return new Response().failure("request_method_not_supported", HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    /**
     * 415 - Unsupported Media Type
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Response handleHttpMediaTypeNotSupportedException(Exception e) {
        log.error("不支持当前媒体类型", e);
        return new Response().failure("content_type_not_supported",HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    /**
     * 406 - Not Acceptable
     */
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public Response handleNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        log.error("无法使用请求的内容特性来响应请求的网页", e);
        return new Response().failure(e.toString(),HttpStatus.NOT_ACCEPTABLE.value());
    }

    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Response handleException(Exception e) {
        return new Response().failure(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * 200 - OK
     * 上传文件过大消息扑捉
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MultipartException.class)
    public Response handleMultipartException(Exception e) {
        log.info(e.getMessage());
        return new Response().failure(e.getMessage(), HttpStatus.OK.value());
    }
    /**
     * 200 - OK
     * 传递服务端验证消息用
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(SysRuntimeException.class)
    public Response handleRuntimeException(Exception e) {
        return new Response().failure(e.getMessage(), HttpStatus.OK.value());
    }
}