package com.khy.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.khy.common.JsonResponse;
import com.khy.mapper.dto.PreOrderDTO;
import com.khy.mapper.dto.PreOrderResultDTO;
import com.khy.mapper.dto.RechargeResultDTO;
import com.khy.mapper.dto.RechargeSubmitDTO;
import com.khy.mapper.dto.SubmitOrderDTO;
import com.khy.mapper.dto.SubmitOrderResultDTO;
import com.khy.service.PayService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/pay")
@Api(value="付款相关")
public class PayController{

	@Autowired
	private PayService payService;
	
	@RequestMapping(value = "/buyProduct",method = RequestMethod.POST)
	@ApiOperation(value = "购买商品提交订单信息")
    @ApiImplicitParam(name = "dto", value = "购买商品提交订单信息", required = true, paramType = "body", dataType = "PreOrderDTO")
	public JsonResponse<PreOrderResultDTO> buyProduct(@RequestBody PreOrderDTO dto){
		JsonResponse<PreOrderResultDTO> jsonResponse = payService.createPreOrder(dto);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/payForProductOnline",method = RequestMethod.POST)
	@ApiOperation(value = "在线购物支付订单内容")
	@ApiImplicitParam(name = "dto", value = "在线购物支付订单内容", required = true, paramType = "body", dataType = "SubmitOrderDTO")
	public JsonResponse<SubmitOrderResultDTO> payOnline(@RequestBody SubmitOrderDTO dto){
		JsonResponse<SubmitOrderResultDTO> jsonResponse = payService.payForProductOnline(dto);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/recharge",method = RequestMethod.POST)
	@ApiOperation(value = "购买vip提交的信息内容")
	public JsonResponse<RechargeResultDTO> recharge(@RequestBody RechargeSubmitDTO dto){
		JsonResponse<RechargeResultDTO> jsonResponse = payService.recharge(dto);
		return jsonResponse;
	}
	
	
	@RequestMapping(value = "/{payType}/notify",method = RequestMethod.POST)
	@ApiOperation(value = "在线购物支付订单内容")
	@ApiImplicitParam(name = "memberId", value = "供应商的id", required = true, dataType = "Long", paramType = "path")
	public String async(@PathVariable(value = "payType", required = true)String payType,HttpServletRequest request){
		System.out.println(payType);
		JsonResponse<Boolean> jsonResponse = payService.payNotify(payType,request);
		return null;
	}
	
}

