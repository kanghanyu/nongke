package com.khy.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.khy.common.JsonResponse;
import com.khy.mapper.dto.PreOrderDTO;
import com.khy.mapper.dto.PreOrderResultDTO;
import com.khy.mapper.dto.RechargeResultDTO;
import com.khy.mapper.dto.RechargeSubmitDTO;
import com.khy.mapper.dto.SubmitOrderDTO;
import com.khy.mapper.dto.SubmitOrderResultDTO;
import com.khy.service.PayService;
import com.khy.service.impl.PayServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/pay")
@Api(value="付款相关")
public class PayController{
	public final static Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);
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
	
	
	@RequestMapping(value = "/{payType}/notify")
	@ApiOperation(value = "在线购物支付订单内容")
	@ApiImplicitParam(name = "memberId", value = "供应商的id", required = true, dataType = "Long", paramType = "path")
	public String async(@PathVariable(value = "payType", required = true)String payType,HttpServletRequest request){
		JsonResponse<Boolean> jsonResponse = payService.payNotify(payType,request);
		if(jsonResponse.getRspBody()){
			logger.info("支付异步回调成功"+JSON.toJSONString(jsonResponse));
			return "success";
		}
		logger.error("支付异步回调失败"+JSON.toJSONString(jsonResponse));
		return "failure";
	}
	
}

