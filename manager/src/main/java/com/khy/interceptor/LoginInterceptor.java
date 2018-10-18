package com.khy.interceptor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.khy.common.Constants;
import com.khy.common.JsonResponse;
import com.khy.entity.User;
import com.khy.service.impl.CacheService;
import com.khy.utils.SessionHolder;
@Configuration
public class LoginInterceptor extends WebMvcConfigurerAdapter{
	public final static Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

	@Autowired
	private CacheService cacheService;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static List<String> WHITE_LIST = new ArrayList<String>(){{
		add("/error");
		add("/swagger-ui.html");
		add("/swagger-resources.*"); 
		add("/webjars/springfox-swagger-ui.*"); 
		add("/v2/api-docs");
		add("/configuration/ui");
		add("/configuration/security");
		add("/user/login");
		add("/user/register");
		add("/user/resetPassword");
		add("/user/getMessageCode");
		add("/user/getOnlineParame");
		add("/user/validate/img");
		add("/product/pageProduct");
		add("/product/getProduct");
	}};
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new HandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {
				String requestURI = request.getRequestURI();
		        for (String whiteListRegex : WHITE_LIST) {
		            if (request.getRequestURI() != null && requestURI.matches(whiteListRegex)) {
		                return true;
		            }
		        }
				String token = request.getHeader("token");
				if(StringUtils.isBlank(token)){
					sendMessage(response,"token不能为空");
					return false;
				}
				String ret = cacheService.getString(Constants.USER_LOGIN.concat(token));
				if(StringUtils.isBlank(ret)){
					sendMessage(response,"当前用户尚未登录");
					return false;
				}
				User user = JSON.parseObject(ret, User.class);
				user.setToken(token);
				SessionHolder.setUser(user);
				return true;
			}
			
			@Override
			public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
					ModelAndView modelAndView) throws Exception {
				
			}
			
			@Override
			public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
					throws Exception {
				if(ex != null){
					JSONObject json = new JSONObject();
					json.put("url", request.getRequestURI());
					json.put("msg",ex.toString());
					json.put("time", sdf.format(new Date()));
					json.put("code", 2000);
					logger.error(json.toString());
				}
			}
		}).addPathPatterns("/**");
	}

	public void sendMessage(HttpServletResponse response,String resBody) throws IOException{
		JsonResponse<String>jsonResponse = new JsonResponse<>();
		jsonResponse.setRspBody(resBody);
		jsonResponse.setRetDesc("用户尚未登录,请登录");
		response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(jsonResponse));
	}
	
}
