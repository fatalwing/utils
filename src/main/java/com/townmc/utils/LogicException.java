package com.townmc.utils;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 系统逻辑异常
 * @author meng
 */
public class LogicException extends RuntimeException implements Supplier<LogicException> {

    private String errorCode;

    private String errorInfo;

    private boolean i18 = false;

    private LogicException() {

    }

    public LogicException(String message) {
        super(message);
    }

    public LogicException(Throwable cause) {
        super(cause);
    }

    public LogicException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogicException(String errorCode, String errorInfo, boolean i18) {
        super("errorCode: " + errorCode + ", errorInfo: " + errorInfo);
        this.errorCode = errorCode;
        this.i18 = i18;

        this.errorInfo = this.errorInfo(errorInfo);
    }

    public LogicException(String errorCode, String errorInfo) {
        super("errorCode: " + errorCode + ", errorInfo: " + errorInfo);
        this.errorCode = errorCode;

        this.errorInfo = this.errorInfo(errorInfo);
    }

    public LogicException(String errorCode, String errorInfo, Throwable cause) {
        super("errorCode: " + errorCode + ", errorInfo: " + errorInfo, cause);
        this.errorCode = errorCode;
        this.errorInfo = this.errorInfo(errorInfo);
    }

    private String errorInfo(String info) {
        if (this.i18) {
            // TODO: 国际化解析返回
            return info;
        } else {
            return info;
        }
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public LogicException get() {
        return new LogicException(Optional.of(this.getErrorCode()).orElse("system_error"),
                Optional.of(this.getErrorInfo()).orElse("系统异常。by_exception"));
    }
}
