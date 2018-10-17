package com.khy.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.khy.common.JsonResponse;
import com.khy.mapper.dto.CartMoneyDTO;
import com.khy.mapper.dto.PreOrderDTO;
import com.khy.mapper.dto.PreOrderResultDTO;
import com.khy.mapper.dto.SubmitOrderDTO;
import com.khy.mapper.dto.SubmitOrderResultDTO;
import com.khy.service.PayService;
import com.khy.service.UesrService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

//@RestController
//@RequestMapping("/pay")
//@Api(value="付款相关")
public class PayController{

	@Autowired
	private PayService payService;
	@Autowired
	private UesrService uesrService;
	
	@RequestMapping(value = "/buyProduct",method = RequestMethod.POST)
	@ApiOperation(value = "购买商品提交订单信息")
    @ApiImplicitParam(name = "dto", value = "购买商品提交订单信息", required = true, paramType = "body", dataType = "PreOrderDTO")
	public JsonResponse<PreOrderResultDTO> buyProduct(@RequestBody PreOrderDTO dto){
		JsonResponse<PreOrderResultDTO> jsonResponse = payService.createPreOrder(dto);
		return jsonResponse;
	}
	
	@RequestMapping(value = "/buyVip",method = RequestMethod.POST)
	@ApiOperation(value = "购买vip提交的信息内容")
	public JsonResponse<PreOrderResultDTO> buyVip(){
		JsonResponse<PreOrderResultDTO> jsonResponse = payService.buyVip();
		return jsonResponse;
	}
	
	///还有话费充值和点卡购买的
	@RequestMapping(value = "/payOnline",method = RequestMethod.POST)
	@ApiOperation(value = "在线支付订单内容")
    @ApiImplicitParam(name = "dto", value = "购买商品提交订单信息", required = true, paramType = "body", dataType = "SubmitOrderDTO")
	public JsonResponse<SubmitOrderResultDTO> payOnline(@RequestBody SubmitOrderDTO dto){
		JsonResponse<SubmitOrderResultDTO> jsonResponse = payService.payOnline(dto);
		return jsonResponse;
	}
	
	
	
	
	///////////////////////////////////////////
	
	@RequestMapping(value = "/commissionToMoney",method = RequestMethod.POST)
	@ApiOperation(value = "佣金转成余额")
	@ApiImplicitParam(paramType = "query", dataType = "BigDecimal", name = "amount", value = "佣金转成余额的数量", required = true)
	public JsonResponse<Boolean> commissionToMoney(BigDecimal amount){
		JsonResponse<Boolean> jsonResponse = uesrService.commissionToMoney(amount);
		return jsonResponse;
	}
	
	
	@RequestMapping(value = "/cardMoneyToUser",method = RequestMethod.POST)
	@ApiOperation(value = "点卡转账给别人")
	@ApiImplicitParam(name = "dto", value = "点卡转账给别人", required = true, paramType = "body", dataType = "CartMoneyDTO")
	public JsonResponse<Boolean> cardMoneyToUser(@RequestBody CartMoneyDTO dto){
		JsonResponse<Boolean> jsonResponse = uesrService.cardMoneyToUser(dto);
		return jsonResponse;
	}
}

