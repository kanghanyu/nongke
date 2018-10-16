package com.khy.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.DigestUtils;

public class Utils {

//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat orderSdf = new SimpleDateFormat("yyMMddHHmmss");

	public static String getUid(){
		String uid = sdf.format(new Date());
		return uid;
	}
	
	public static String getOrderId(){
		String orderId = orderSdf.format(new Date());
		return orderId;
	}
	
	public static String getToken(){
		String uid = sdf.format(new Date());
		String token = DigestUtils.md5DigestAsHex(uid.getBytes());
		return token;
	}
	

	public static String getFileName(){
		String fileName = sdf1.format(new Date());
		return fileName;
	}
	
}
