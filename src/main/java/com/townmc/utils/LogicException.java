package com.townmc.utils;

/**
 * 系统逻辑错误
 * Created by meng on 2015-1-27.
 */
class LogicException extends RuntimeException {

    private static final long serialVersionUID = -7923892655400414209L;

    protected String errorCode;
    protected String errorMsg;

    public LogicException(String errorCode) {
        super(errorCode);

        this.errorCode = errorCode;
        this.errorMsg = "";
    }

    public LogicException(String errorCode, String errorMsg) {
        super(errorCode);

        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public LogicException(String errorCode, String errorMsg, Throwable throwable) {
        super(errorCode, throwable);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
