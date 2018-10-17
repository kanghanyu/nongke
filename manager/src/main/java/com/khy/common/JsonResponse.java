package com.khy.common;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="通用的响应内容",description="通用的响应内容")
public class JsonResponse<T> implements Serializable {

	private static final long serialVersionUID = 5671703245683870897L;
	static final String PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String SUCCESS = "10000";
	public static final String FAIL = "20000";
	public static final String EXCEPTION = "40000";
	@ApiModelProperty(value="响应code,10000 标识成功 20000表示失败 40000 标识异常" )
	private String retCode;
	@ApiModelProperty(value="响应的描述内容")
	private String retDesc;
	private String timestamp;
	@ApiModelProperty(value="响应的内容主题")
	private T rspBody;
	@ApiModelProperty(value="额外的一些参数内容")
	private Map<String,Object>extra;

	public JsonResponse() {
		this(null);
	}

	public JsonResponse(T value) {
		this(FAIL, "操作失败!", value);
	}

	public JsonResponse(String retCode, String retDesc, T rspBody) {
		this(retCode, retDesc, rspBody, new SimpleDateFormat(PATTERN).format(new Date()));
	}

	public JsonResponse(String retCode, String retDesc) {
		this(retCode, retDesc, null);
	}
	
	public static JsonResponse fail(String retDesc ,Object obj){
		return new JsonResponse(EXCEPTION, retDesc, obj);
	}
	public void success(T rspBody){
		this.setRetCode(SUCCESS);
		this.setRetDesc("操作成功");
		this.setRspBody(rspBody);
	}
	public void success(T rspBody,String retDesc){
		this.setRetCode(SUCCESS);
		this.setRetDesc(StringUtils.isNotBlank(retDesc)?retDesc:"操作成功");
		this.setRspBody(rspBody);
	}
	public JsonResponse(String retCode, String retDesc, T rspBody, String timestamp) {
		this.retCode = retCode;
		this.retDesc = retDesc;
		this.rspBody = rspBody;
		this.timestamp = timestamp;
	}
	
	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetDesc() {
		return retDesc;
	}

	public void setRetDesc(String retDesc) {
		this.retDesc = retDesc;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public T getRspBody() {
		return rspBody;
	}
	public Map<String, Object> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, Object> extra) {
		this.extra = extra;
	}

	public void setRspBody(T rspBody) {
		this.rspBody = rspBody;
	}
}
