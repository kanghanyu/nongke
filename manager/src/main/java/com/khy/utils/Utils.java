package com.khy.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.DigestUtils;

public class Utils {

//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
	private final static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
	private final static SimpleDateFormat orderSdf = new SimpleDateFormat("yyMMddHHmmss");
	public final static String STANDARD_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
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
	
	public static String formatDateTime(Date date) {
		if (date == null){
			return null;
		}
		DateFormat format = new SimpleDateFormat(STANDARD_DATETIME_PATTERN);
		return format.format(date);
	}
	
}
