package com.jwxt.exception;

/**
 * 系统运行时异常类
 * 主要保存运行时异常消息
 * PS：无堆栈信息
 *
 * @author mason nitmali
 */
public class SysRuntimeException extends RuntimeException {

    private String message = null;

    private Throwable cause = null;

    private String type = null;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SysRuntimeException(String message) {
        this.message = message;
        this.type = MessageEnum.WARNING.getValue();
    }

    public SysRuntimeException(String message, MessageEnum messageEnum) {
        super(message);
        this.type = messageEnum.getValue();
    }
}

enum MessageEnum {
    /**
     * 成功类型消息
     */
    SUCCESS("success"),
    /**
     * 警告类型
     */
    WARNING("warning"),
    /**
     * 错误类型
     */
    ERROR("error");

    private String value;

    MessageEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}