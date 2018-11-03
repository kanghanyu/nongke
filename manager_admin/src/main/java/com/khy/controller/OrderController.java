package com.khy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.khy.entity.OnlineParame;
import com.khy.mapper.dto.UserCommonDTO;
import com.khy.mapper.dto.UserOrderInfoDTO;
import com.khy.service.OrderService;

@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	@RequestMapping("/toOrderList")
	public ModelAndView toOnline(UserCommonDTO dto){
		ModelAndView model = new ModelAndView("order/list");
		PageInfo<UserOrderInfoDTO> page =  orderService.page(dto);
		model.addObject("page", page);
		return model;
	}
	
	@RequestMapping("/dataList")
	@ResponseBody
	public String dateList(@RequestBody UserCommonDTO dto){
		PageInfo<UserOrderInfoDTO> page =  orderService.page(dto);
		return JSON.toJSONString(page);
	}
	
}

