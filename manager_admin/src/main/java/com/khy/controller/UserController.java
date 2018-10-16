package com.khy.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.khy.entity.Msg;
import com.khy.entity.User;
import com.khy.service.UesrService;
import com.khy.utils.SmsUtils;
import com.khy.utils.ValidateCode;

@Controller
@RequestMapping("/user")
public class UserController {
	

	private static String msg ="手机号/密码不正确";
	@Autowired
	private UesrService uesrService;
	
	@Autowired
	private SmsUtils SmsUtils;
	
	
	@RequestMapping(value = "getMessageCode", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String getMessageCode(@RequestBody Msg msg){
		System.out.println("发送短信验证码的");
		JSONObject json = SmsUtils.sendMessage(msg);
		return json.toString();
	}
	
	/**
	 * 登录接口
	 * @Description
	 * @author khy
	 * @date  2018年9月30日下午1:27:17
	 * @param user
	 * @param session
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/login")
	public String loginForm(User user,HttpSession session) throws UnsupportedEncodingException{
		user=uesrService.login(user);
		if(user!=null){
			session.setAttribute("loginUser", user);
			return "redirect:/index";
		}else{
			msg = URLEncoder.encode(msg,"UTF-8");
			return "redirect:/login?msg="+msg;
		}
	}
	
	/**
	 * 注册接口
	 * @Description
	 * @author khy
	 * @date  2018年9月30日下午1:27:26
	 * @param user
	 * @param session
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/register")
	public String register(User user,HttpSession session) throws UnsupportedEncodingException{
		String ret = null;
		JSONObject json = uesrService.register(user,session);
		if(json.getInteger("code").intValue() == 1000){
			ret = "redirect:/index";
		}else{
			ret = "redirect:/register?msg="+URLEncoder.encode(json.getString("msg"),"UTF-8");
		}
		return ret;
	}
	
	@RequestMapping("/resetPassword")
	public String resetPassword(User user) throws UnsupportedEncodingException{
		String ret = null;
		JSONObject json = uesrService.resetPassword(user);
		if(json.getInteger("code").intValue() == 1000){
			ret = "redirect:/login";
		}else{
			ret = "redirect:/resetPassword?msg="+URLEncoder.encode(json.getString("msg"),"UTF-8");
		}
		return ret;
	}
	
	/***
	 * 设置用户的状态/管理员/vip/
	 * @Description
	 * @author khy
	 * @date  2018年10月6日下午1:12:55
	 * @param user
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/setUserStatus")
	@ResponseBody
	public String setUserStatus(@RequestBody User user) throws UnsupportedEncodingException{
		JSONObject json = uesrService.setUserStatus(user);
		return json.toString();
	}
	
	/**
	 * 退出登录接口
	 * @Description
	 * @author khy
	 * @date  2018年9月30日下午1:27:32
	 * @param session
	 * @return
	 */
	@RequestMapping("/loginOut")
	public String loginOut(HttpSession session){
		session.setAttribute("loginUser", null);
		return "redirect:/index";
	}
	
	
	/***
	 * 获取用户
	 * @Description
	 * @author khy
	 * @date  2018年9月30日下午1:28:17
	 * @param request
	 * @return
	 */
	@RequestMapping("/toUserList")
	public ModelAndView toUserList(User user){
		ModelAndView model = new ModelAndView("user/list");
		PageInfo<User> page =  uesrService.page(user);
		model.addObject("page", page);
		return model;
	}
	
	@RequestMapping("/dataList")
	@ResponseBody
	public String dateList(@RequestBody User user){
		PageInfo<User> page =  uesrService.page(user);
		return JSON.toJSONString(page);
	}
	
	
	@RequestMapping("/getUserInfo")
	@ResponseBody
	public String getUserInfo(@RequestBody User user){
		String ret =  uesrService.getUserInfo(user);
		return ret;
	}
	
	
	/***
	 * 获取验证码的
	 * @Description
	 * @author khy
	 * @date  2018年9月29日下午2:15:49
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/validate/img")
	public void img(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 // 设置响应的类型格式为图片格式  
        response.setContentType("image/jpeg");  
        //禁止图像缓存。  
        response.setHeader("Pragma", "no-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
          
        HttpSession session = request.getSession();  
        ValidateCode vCode = new ValidateCode(120,40,4,100);  
        session.setAttribute("code", vCode.getCode());  
        vCode.write(response.getOutputStream());  
	}
	
	
	
	
}

