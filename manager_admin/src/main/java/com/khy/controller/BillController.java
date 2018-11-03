package com.khy.controller;

import java.math.BigDecimal;

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
import com.khy.entity.UserBill;
import com.khy.mapper.dto.UserCommonDTO;
import com.khy.service.BillService;

@Controller
@RequestMapping("/bill")
public class BillController {
	
	@Autowired
	private BillService billService;
	@RequestMapping("/toBill")
	public ModelAndView toBill(UserCommonDTO dto){
		ModelAndView model = new ModelAndView("bill/list");
		PageInfo<UserBill> page =  billService.page(dto);
		BigDecimal amount = billService.sumAmount(dto);
		model.addObject("page", page);
		model.addObject("amount", amount);
		return model;
	}
	
	@RequestMapping("/dataList")
	@ResponseBody
	public String dateList(@RequestBody UserCommonDTO dto){
		PageInfo<UserBill> page =  billService.page(dto);
		BigDecimal amount = billService.sumAmount(dto);
		JSONObject json = new JSONObject();
		json.put("page",page);
		json.put("amount",amount);
		return json.toString();
	}
	
}

