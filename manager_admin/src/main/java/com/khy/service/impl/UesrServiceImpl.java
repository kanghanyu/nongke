package com.khy.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.khy.config.RedisUtils;
import com.khy.entity.User;
import com.khy.entity.UserBank;
import com.khy.mapper.UserBankMapper;
import com.khy.mapper.UserMapper;
import com.khy.service.UesrService;
import com.khy.utils.Constants;
import com.khy.utils.SessionHolder;
import com.khy.utils.Utils;
@Service
@Transactional
public class UesrServiceImpl implements UesrService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserBankMapper userBankMapper;
	
	@Autowired
	private RedisUtils RedisUtils;
	@Override
	public User login(User user) {
		if(null == user){
			return null;
		}
		String phone = user.getPhone();
		String password = user.getPassword();
		if(StringUtils.isBlank(phone) || StringUtils.isBlank(password)){
			return null;
		}
		User userDb = userMapper.getUserByPhone(phone);
		if(null == userDb || !userDb.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
			return null;
		}
		userDb.hide(userDb);
		return userDb;
	}

	@Override
	public JSONObject register(User user, HttpSession session) {
 		JSONObject json = new JSONObject();
		json.put("code", 2000);
		String msg = null;
		if(null == user){
			msg = "参数为空";
			json.put("msg",msg);
			return json;
		}
		String key = Constants.ADMIN_SMS_REGISTER+user.getPhone();
		String redisCode = RedisUtils.STRINGS.get(key);
		if(StringUtils.isBlank(redisCode)){
			json.put("msg","验证码已失效");
			return json;
		}
		if(!user.getCode().equals(redisCode)){
			json.put("msg","验证码错误");
			return json;
		}
		
		if(StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getConfirmPassword())){
			msg = "密码不能为空";
			json.put("msg",msg);
			return json;
		}
		if(!user.getPassword().equals(user.getConfirmPassword())){
			msg = "两次密码不一致";
			json.put("msg",msg);
			return json;
		}
		User userDb = userMapper.getUserByPhone(user.getPhone());
		if(null != userDb){
			msg = "手机号已注册";
			json.put("msg",msg);
			return json;
		}
		if(StringUtils.isBlank(msg)){
			user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
			user.setIsManager(1);
			user.setIsVip(0);
			user.setUid(Utils.getUid());
			user.setMoney(new BigDecimal("0.00"));
			int flag = userMapper.insert(user);
			if(flag >= 1){
				userDb = userMapper.getUserByPhone(user.getPhone());
				userDb.hide(userDb);
				session.setAttribute("loginUser", userDb);
				SessionHolder.setUser(userDb);
				json.put("code",1000);
				json.put("msg","恭喜您注册成功啦");
				RedisUtils.KEYS.del(key);
			}
		}
		return json;
	}

	/**
	 * 分页查询用户信息
	 */
	@Override
	public PageInfo<User> page(User user) {
		PageHelper.startPage(user.getPageNum(), user.getPageSize());
		List<User> list = userMapper.list(user);
		PageInfo <User>pageInfo = new PageInfo<User>(list);
		return pageInfo;
	}

	@Override
	public JSONObject resetPassword(User user) {
		JSONObject json = new JSONObject();
		json.put("code",2000);
		json.put("msg","操作失败");
		if(null == user){
			json.put("msg","参数不为空");
			return json;
		}
		if(StringUtils.isAnyBlank(user.getPhone(),user.getPassword(),user.getConfirmPassword(),user.getCode())){
			json.put("msg","参数不为空");
			return json;
		}
		if(!user.getPassword().equals(user.getConfirmPassword())){
			json.put("msg","两次密码不一致");
			return json;
		}
		String key = Constants.ADMIN_SMS_FIND_PASSWORD+user.getPhone();
		String redisCode = RedisUtils.STRINGS.get(key);
		if(StringUtils.isBlank(redisCode)){
			json.put("msg","验证码已过期,重新获取");
			return json;
		}
		if(!user.getCode().equals(redisCode)){
			json.put("msg","验证码错误,请重新输入");
			return json;
		}
		User userDb = userMapper.getUserByPhone(user.getPhone());
		if(null == userDb){
			json.put("msg","当前用户不存在");
			return json;
		}
		userDb.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		int num = userMapper.updateUser(userDb);
		if(num>0){
			json.put("code",1000);
			json.put("msg","修改密码成功");
			RedisUtils.KEYS.del(key);
		}
		return json;
	}
	@Override
	public JSONObject setUserStatus(User user) {
		JSONObject json = new JSONObject();
		json.put("code",2000);
		json.put("msg","操作失败");
		if(null == user){
			json.put("msg","用户不为空");
			return json;
		}
		int num = userMapper.updateUser(user);
		if(num>0){
			json.put("code",1000);
			json.put("msg","成功");
		}
		return json;
	}

	@Override
	public String getUserInfo(User user) {
		JSONObject json = new JSONObject();
		json.put("code",2000);
		if(null == user || null == user.getUid()){
			json.put("msg","请求参数不能为空");
			return json.toJSONString();
		}
		String uid = user.getUid();
		User userDb = userMapper.getUserByUid(uid);
		if(null == userDb){
			json.put("msg","当前用户状态异常");
			return json.toJSONString();
		}
		
		UserBank bank = userBankMapper.getByUid(uid);
		json.put("user", userDb);
		json.put("bank", bank);
		json.put("code",1000);
		return json.toJSONString();
	}

}



