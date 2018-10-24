package com.khy.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartRequest;

import com.khy.common.JsonResponse;
import com.khy.entity.Msg;
import com.khy.entity.OnlineParame;
import com.khy.entity.User;
import com.khy.entity.UserAddress;
import com.khy.entity.UserBank;
import com.khy.entity.UserCash;
import com.khy.entity.UserInviter;
import com.khy.mapper.dto.CartMoneyDTO;
import com.khy.mapper.dto.UserInviterDTO;
import com.khy.mapper.dto.UserRecordDTO;
import com.khy.service.UesrService;
import com.khy.utils.SessionHolder;
import com.khy.utils.SmsUtils;
import com.khy.utils.ValidateCode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(value="用户操作的相关接口内容")
public class UserController {
	@Autowired
	private UesrService uesrService;
	@Autowired
	private SmsUtils SmsUtils;
	
	@RequestMapping(value = "getMessageCode", method = RequestMethod.POST)
	@ApiOperation(notes = "获取手机短信验证码的接口",value = "获取手机短信验证码的接口")
	@ApiImplicitParam(name = "msg", value = "获取手机短信验证码的接口参数", required = true, paramType = "body", dataType = "Msg")
	public JsonResponse<Boolean> getMessageCode(@RequestBody Msg msg){
		JsonResponse<Boolean> jsonResponse = SmsUtils.sendMessage(msg);
		return jsonResponse;
	}
	
	@RequestMapping(value = "getMessageCodeForUpdate", method = RequestMethod.POST)
	@ApiOperation(value = "获取手机短信验证码的接口-->修改用户信息的")
	public JsonResponse<Boolean> getMessageCodeForUpdate(){
		JsonResponse<Boolean> jsonResponse = new JsonResponse<>();
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录接口内容");
			return jsonResponse;
		}
		Msg msg = new Msg();
		msg.setPhone(user.getPhone());
		msg.setType(3);
		jsonResponse = SmsUtils.sendMessage(msg);
		return jsonResponse;
	}

	@RequestMapping(value = "/login",method = RequestMethod.POST)
	@ApiOperation(notes = "登录的接口",value = "用户登录的接口内容")
    @ApiImplicitParam(name = "user", value = "用户登录的接口参数", required = true, paramType = "body", dataType = "User")
	public JsonResponse<User> loginForm(@RequestBody User user) {
		JsonResponse<User> jsonResponse = uesrService.login(user);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/getUserInfo",method = RequestMethod.POST)
	@ApiOperation(value = "获取用户信息的接口")
	public JsonResponse<User> getUserInfo() {
		JsonResponse<User> jsonResponse = uesrService.getUserInfo();
		return jsonResponse;
	}
	
	@RequestMapping(value = "/uploadImg" ,method = RequestMethod.POST )
	@ApiOperation(value = "上传用户头像的接口")
	@ResponseBody
	public JsonResponse<Boolean> uploadImg(MultipartRequest request) {
		JsonResponse<Boolean> jsonResponse = uesrService.uploadImg(request);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/register",method = RequestMethod.POST)
	@ApiOperation(notes = "注册的接口",value = "用户注册的接口内容")
    @ApiImplicitParam(name = "user", value = "用户注册的接口参数", required = true, paramType = "body", dataType = "User")
	public JsonResponse<Boolean> register(@RequestBody User user){
		JsonResponse<Boolean> jsonResponse = uesrService.register(user);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/loginOut",method = RequestMethod.POST)
	@ApiOperation(notes = "退出登录的的接口",value = "退出登录的的接口内容")
	public JsonResponse<Boolean> loginOut(){
		JsonResponse<Boolean> jsonResponse = uesrService.loginOut();
		return jsonResponse;
	}
	
	@RequestMapping(value = "/resetPassword",method = RequestMethod.POST)
	@ApiOperation(notes = "修改密码的接口",value = "修改密码的接口")
    @ApiImplicitParam(name = "user", value = "用户注册的接口参数", required = true, paramType = "body", dataType = "User")
	public JsonResponse<Boolean> resetPassword(@RequestBody User user){
		JsonResponse<Boolean> jsonResponse = uesrService.resetPassword(user);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/saveOrUpdateUserAddress",method = RequestMethod.POST)
	@ApiOperation(value = "新增/修改收货地址")
    @ApiImplicitParam(name = "userAddress", value = "新增/修改收货地址接口参数", required = true, paramType = "body", dataType = "UserAddress")
	public JsonResponse<Boolean> saveOrUpdateUserAddress(@RequestBody UserAddress userAddress){
		JsonResponse<Boolean> jsonResponse = uesrService.saveOrUpdateUserAddress(userAddress);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/getUserAddress",method = RequestMethod.POST)
	@ApiOperation(value = "获取当前用户的收货地址")
	public JsonResponse<UserAddress> listUserAddress(){
		JsonResponse<UserAddress> jsonResponse = uesrService.getUserAddress();
		return jsonResponse;
	}
	
	@RequestMapping(value = "/saveOrUpdateUserBank",method = RequestMethod.POST)
	@ApiOperation(value = "新增/修改银行卡信息")
    @ApiImplicitParam(name = "userBank", value = "新增/修改银行卡信息接口参数", required = true, paramType = "body", dataType = "UserBank")
	public JsonResponse<Boolean> saveUserBank(@RequestBody UserBank userBank){
		JsonResponse<Boolean> jsonResponse = uesrService.saveOrUpdateUserBank(userBank);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/getUserBank",method = RequestMethod.POST)
	@ApiOperation(value = "获取当前用户银行卡信息内容")
	public JsonResponse<UserBank> getUserBank(){
		JsonResponse<UserBank> jsonResponse = uesrService.getUserBank();
		return jsonResponse;
	}
	
	@RequestMapping(value = "/listUserInviter",method = RequestMethod.POST)
	@ApiOperation(value = "获取用户的邀请人列表内容")
	@ApiImplicitParam(name = "userInviter", value = "获取用户的邀请人列表内容接口参数", required = true, paramType = "body", dataType = "UserInviter")
	public JsonResponse<List<UserInviterDTO>> listUserInviter(@RequestBody UserInviter userInviter){
		JsonResponse<List<UserInviterDTO>> jsonResponse = uesrService.listUserInviter(userInviter);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/saveUserCash",method = RequestMethod.POST)
	@ApiOperation(value = "用户体现接口内容")
    @ApiImplicitParam(name = "userCash", value = "新增/修改银行卡信息接口参数", required = true, paramType = "body", dataType = "UserCash")
	public JsonResponse<Boolean> saveUserCash(@RequestBody UserCash userCash){
		JsonResponse<Boolean> jsonResponse = uesrService.saveUserCash(userCash);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/listUserCash",method = RequestMethod.POST)
	@ApiOperation(value = "获取用户体现记录内容")
	public JsonResponse<List<UserCash>> listUserCash(){
		JsonResponse<List<UserCash>> jsonResponse = uesrService.listUserCash();
		return jsonResponse;
	}
	
	@RequestMapping(value = "/getOnlineParame",method = RequestMethod.POST)
	@ApiOperation(value = "获取在线参数的内容")
	public JsonResponse<Map<String,OnlineParame>> getOnlineParame(){
		JsonResponse<Map<String,OnlineParame>> jsonResponse =uesrService.getOnlineParame();
		return jsonResponse;
	}
	
	
	@RequestMapping(value = "/commissionToMoney", method = RequestMethod.POST)
	@ApiOperation(value = "佣金转成余额")
	@ApiImplicitParam(paramType = "query", dataType = "BigDecimal", name = "amount", value = "佣金转成余额的数量", required = true)
	public JsonResponse<Boolean> commissionToMoney(BigDecimal amount) {
		JsonResponse<Boolean> jsonResponse = uesrService.commissionToMoney(amount);
		return jsonResponse;
	}

	@RequestMapping(value = "/cardMoneyToUser", method = RequestMethod.POST)
	@ApiOperation(value = "点卡转账给别人")
	@ApiImplicitParam(name = "dto", value = "点卡转账给别人", required = true, paramType = "body", dataType = "CartMoneyDTO")
	public JsonResponse<Boolean> cardMoneyToUser(@RequestBody CartMoneyDTO dto) {
		JsonResponse<Boolean> jsonResponse = uesrService.cardMoneyToUser(dto);
		return jsonResponse;
	}

	@RequestMapping(value = "/listUserRecord", method = RequestMethod.POST)
	@ApiOperation(value = "获取用户佣金记录/转账记录内容")
	@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "type", value = "2:表示转账记录/3标识佣金记录", required = true)
	public JsonResponse<List<UserRecordDTO>> listUserRecord(Integer type) {
		JsonResponse<List<UserRecordDTO>> jsonResponse = uesrService.listUserRecord(type);
		return jsonResponse;
	}
	
	
	@RequestMapping(value = "/validate/img",method = RequestMethod.POST)
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

