package com.hyt.progress.exception;

/**
 * 全局异常
 *
 * @author admin
 */
public class ProgressException extends RuntimeException
{

    private static final long serialVersionUID = 570288218815530666L;

    private int errCode;

    private String errMsg;

    private Object data;

    public ProgressException() {
    }

    public ProgressException(String errMsg) {
        super(errMsg);
        this.errMsg = errMsg;
    }

    public ProgressException(int errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public ProgressException(int errCode, String errMsg, Object data) {
        super(errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ErrCode:" + getErrCode() + ", ErrMsg:" + getErrMsg();
    }

}
