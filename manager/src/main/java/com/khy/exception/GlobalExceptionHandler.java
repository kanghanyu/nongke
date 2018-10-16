package com.khy.exception;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.khy.common.JsonResponse;
import com.khy.interceptor.LoginInterceptor;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	private static String BUSINESSEXCEPTION ="业务操作异常"; 
	private static String EXCEPTION ="非业务操作异常"; 
	@ExceptionHandler(value = BusinessException.class)
	@ResponseBody
	public Object baseErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		boolean instance = BusinessException.class.isInstance(e);
		if (instance) {
			JSONObject json = new JSONObject();
			json.put("url", req.getRequestURI());
			json.put("msg",e.toString());
			json.put("time", sdf.format(new Date()));
			json.put("code", 2000);
			logger.error(json.toString());
			BusinessException bex = (BusinessException) e;
			JsonResponse jsonResponse = JsonResponse.fail(BUSINESSEXCEPTION,bex.getErrorMsg());
			return JSON.toJSON(jsonResponse);
		}
		return "---BusinessException Handler---:" + e.getMessage();
	}


	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public Object exception(HttpServletRequest req, Exception e) throws Exception {
		JSONObject json = new JSONObject();
		json.put("url", req.getRequestURI());
		json.put("msg",e.toString());
		json.put("time", sdf.format(new Date()));
		json.put("code", 2000);
		logger.error(json.toString());
		JsonResponse jsonResponse = JsonResponse.fail(EXCEPTION,e.getMessage());
		return JSON.toJSON(jsonResponse);
	}
}
