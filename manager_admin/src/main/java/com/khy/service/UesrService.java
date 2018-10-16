package com.khy.service;

import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.khy.entity.User;

public interface UesrService {

	User login(User user);

	JSONObject register(User user,HttpSession session);

	PageInfo<User> page(User user);

	JSONObject resetPassword(User user);

	JSONObject setUserStatus(User user);

	String getUserInfo(User user);


}
