package com.khy.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.khy.common.Constants;
import com.khy.entity.OrderInfo;
import com.khy.exception.BusinessException;
import com.khy.mapper.dto.RechargeResultDTO;
import com.khy.mapper.dto.SubmitOrderDTO;
import com.khy.mapper.dto.SubmitOrderResultDTO;

public class PayUtil {
	public final static Logger logger = LoggerFactory.getLogger(PayUtil.class);
	public static String setProductSign(SubmitOrderDTO dto) {
		logger.info("支付宝商品支付验签开始");		
		Map<String, String> param = new HashMap<>();
		param.put("app_id", Constants.ALIPAY_APPID);
		param.put("method", Constants.METHOD);
		param.put("format", Constants.FORMAT_JSON);
		param.put("charset", Constants.CHARSET_UTF8);
		param.put("sign_type", Constants.SIGN_TYPE_RSA2);
		param.put("version", Constants.VERSION);
		param.put("notify_url", Constants.NOTIFY_URL);
		param.put("timestamp", Utils.formatDateTime(new Date()));
		
		Map<String, Object> orderDescr = new HashMap<>();
		String productName = dto.getList().get(0).getProductName();
		Integer orderType = dto.getOrderType();
		orderDescr.put("body",productName);
		orderDescr.put("subject", dto.getSubject());
		orderDescr.put("out_trade_no", dto.getOrderId());
		orderDescr.put("timeout_express", Constants.TIMEOUT_EXPRESS);
		orderDescr.put("total_amount", dto.getRmb());
		orderDescr.put("product_code","QUICK_MSECURITY_PAY");
		orderDescr.put("goods_type",orderType == 4 ? "1" : "0");
		
		//测试生成sign 的内容
//		orderDescr.put("body","我是测试数据");
//		orderDescr.put("subject", "App支付测试Java");
//		orderDescr.put("out_trade_no", "1111");
//		orderDescr.put("timeout_express", "30m");
//		orderDescr.put("total_amount", "0.01");
//		orderDescr.put("product_code","QUICK_MSECURITY_PAY");
		param.put("biz_content", JSON.toJSONString(orderDescr));
		String sign = null;
		try {
			sign = 	getSign(param,Constants.CHARSET_UTF8, Constants.SIGN_TYPE_RSA2);
			logger.info("支付宝验签生成的sign={}",sign);		
		} catch (AlipayApiException e) {
			logger.error("支付宝生成验签失败失败"+e.getMessage());
			throw new BusinessException("支付宝生成验签失败失败"+e.getMessage());
		}
		String result = "";
		try {
			if (StringUtils.isNotBlank(sign)) {
				result = getSignEncodeUrl(param, sign, Constants.CHARSET_UTF8);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("支付宝验签转化异常" + e.getMessage());
			throw new BusinessException("支付宝验签转化异常" + e.getMessage());
		}
		logger.info("支付宝验签生成的全部信息结果result={}", result);
		return result;
	}
	
	
	public static String setRechargeSign(OrderInfo info, RechargeResultDTO ret) throws UnsupportedEncodingException {
		logger.info("支付宝支付验签开始");		
		Map<String, String> param = new HashMap<>();
		param.put("app_id", Constants.ALIPAY_APPID);
		param.put("method", Constants.METHOD);
		param.put("format", Constants.FORMAT_JSON);
		param.put("charset", Constants.CHARSET_UTF8);
		param.put("sign_type", Constants.SIGN_TYPE_RSA2);
		param.put("version", Constants.VERSION);
		param.put("notify_url", Constants.NOTIFY_URL);
		param.put("timestamp", Utils.formatDateTime(new Date()));
		
	
		Map<String, Object> orderDescr = new HashMap<>();
		Integer orderType = info.getOrderType();
		orderDescr.put("body",info.getDescription());
		orderDescr.put("subject", ret.getSubject());
		orderDescr.put("out_trade_no", info.getOrderId());
		orderDescr.put("timeout_express", Constants.TIMEOUT_EXPRESS);
		orderDescr.put("total_amount", info.getRmb());
		orderDescr.put("product_code","QUICK_MSECURITY_PAY");
		orderDescr.put("goods_type",orderType == 4 ? "1" : "0");
		param.put("biz_content", JSON.toJSONString(orderDescr));
		String sign = null;
		try {
			sign = 	getSign(param,Constants.CHARSET_UTF8, Constants.SIGN_TYPE_RSA2);
			logger.info("支付宝验签生成的sign={}",sign);		
		} catch (AlipayApiException e) {
			logger.error("支付宝生成验签失败失败"+e.getMessage());
			throw new BusinessException("支付宝生成验签失败失败"+e.getMessage());
		}
		String result = "";
		if(StringUtils.isNotBlank(sign)){
			result = getSignEncodeUrl(param,sign,Constants.CHARSET_UTF8);
			logger.info("支付宝验签生成的全部信息结果ret={}",ret);		
		}
		return result;
	}
	
	private static String getSign(Map<String, String> param, String charset, String signType) throws AlipayApiException {
		String content = AlipaySignature.getSignContent(param);
		String sign = AlipaySignature.rsaSign(content, Constants.PRIVATE_KEY, charset,signType);
		return sign;
	}

	private static String getSignEncodeUrl(Map<String, String> param, String sign, String charset) throws UnsupportedEncodingException {
		String encodedSign = "";
		if(null != param && param.size() != 0){
			StringBuilder ret = new StringBuilder();
			List<String> keys = new ArrayList<String>(param.keySet());
			Collections.sort(keys);
			boolean first = true;// 是否第一个
			for (String key : keys) {
				if(StringUtils.isNotBlank(param.get(key))){
					if(first){
						first = false;
					}else{
						ret.append("&");
					}
					ret.append(key).append("=");
					ret.append(URLEncoder.encode(param.get(key), charset));
				}
			}
			ret.append("&sign=").append(URLEncoder.encode(sign, charset));
			encodedSign = ret.toString();
		}
		return encodedSign;
	}

	
	public static void main(String[] args) throws UnsupportedEncodingException {
		SubmitOrderDTO dto =  new SubmitOrderDTO();
	}

}
