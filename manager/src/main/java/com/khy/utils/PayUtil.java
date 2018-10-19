package com.khy.utils;

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
import com.khy.exception.BusinessException;
import com.khy.mapper.dto.SubmitOrderDTO;
import com.khy.mapper.dto.SubmitOrderResultDTO;

public class PayUtil {
	public final static Logger logger = LoggerFactory.getLogger(PayUtil.class);
	public static void setSign(SubmitOrderResultDTO ret, SubmitOrderDTO dto) {
		logger.info("支付宝验签开始");		
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
		param.put("biz_content", JSON.toJSONString(orderDescr));
		String content = AlipaySignature.getSignContent(param);
		String sign = null;
		try {
			sign = AlipaySignature.rsa256Sign(content, Constants.PRIVATE_KEY,Constants.CHARSET_UTF8);
		} catch (AlipayApiException e) {
			logger.error("支付宝生成验签失败失败"+e.getMessage());
			throw new BusinessException("支付宝生成验签失败失败"+e.getMessage());
		}
		if(StringUtils.isNotBlank(sign)){
//			AlipaySignature.en
			
			
		}
		
		
	}

}
