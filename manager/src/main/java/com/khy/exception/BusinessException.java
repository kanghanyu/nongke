package com.khy.exception;

/***
 *自定义的异常内容
 * @author kanghanyu
 *
 */
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String errorMsg;

	public BusinessException(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public BusinessException(String exceptionCode, Throwable cause) {
		super(cause);
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
