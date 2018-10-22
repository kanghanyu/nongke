package com.khy.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.khy.common.Constants;
import com.khy.common.JsonResponse;
import com.khy.entity.Msg;
import com.khy.entity.User;
import com.khy.mapper.UserMapper;
import com.khy.service.impl.CacheService;
@Component
public class SmsUtils {
	public final static Logger logger = LoggerFactory.getLogger(SmsUtils.class);
	private static IAcsClient acsClient=null;
	@Autowired
	private CacheService cacheService;
	@Autowired 
	private UserMapper userMapper;
	static{
		 //可自助调整超时时间
       System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
       System.setProperty("sun.net.client.defaultReadTimeout", "10000");

       //初始化acsClient,暂不支持region化
       IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", Constants.accessKeyId, Constants.accessKeySecret);
       try {
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", Constants.product, Constants.domain);
			acsClient = new DefaultAcsClient(profile);
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
	
	public static String sendSms(String phone,String code){
		 //组装请求对象-具体描述见控制台-文档部分内容
       SendSmsRequest request = new SendSmsRequest();
       //必填:待发送手机号
       request.setPhoneNumbers(phone);
       //必填:短信签名-可在短信控制台中找到
       request.setSignName("拓荒农科");
       //必填:短信模板-可在短信控制台中找到
       request.setTemplateCode("SMS_147200076");
       //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
       request.setTemplateParam("{\"code\":"+code+"}");
       //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
       request.setOutId("yourOutId");
       //hint 此处可能会抛出异常，注意catch
       SendSmsResponse response;
       String ret=null;
		try {
			response = acsClient.getAcsResponse(request);
			if(null!= response){
				ret=response.getCode();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
       return ret;
	}
	
	public static String getSix(){  
		int num=(int)((Math.random()*9+1)*100000);
		String ret=num+"";
        return ret;  
    }  
	
	public JsonResponse<Boolean> sendMessage(Msg msg){
		JsonResponse<Boolean> jsonResponse = new JsonResponse<>();
		if(null == msg || null == msg.getPhone()){
			jsonResponse.setRetDesc("参数不合法");
			return jsonResponse;
		}
		String key = null;
		String phone = msg.getPhone();
		User userDb = userMapper.getUserByPhone(phone);
		if(msg.getType() == 1){//标识注册
			key  = Constants.USER_SMS_REGISTER+phone;
			if(null != userDb){
				jsonResponse.setRetDesc("该手机号已注册");
				return jsonResponse;
			}
		}else if(msg.getType() == 2){
			key  = Constants.USER_SMS_FIND_PASSWORD+phone;
			if(null == userDb){
				jsonResponse.setRetDesc("当前账户不存在");
				return jsonResponse;
			}
		}else if(msg.getType() == 3){//修改用户信息的
			key  = Constants.USER_SMS_UPDATE_BANKINFO+phone;
		}
		String code = getSix();
		String ret = sendSms(msg.getPhone(), code);
		if(StringUtils.isNotBlank(ret)&&ret.equals("OK")){//表示发送验证码成
			cacheService.setString(key, code, Constants.FIVE_MINUTE);
			jsonResponse.success(true);
			jsonResponse.setRetDesc("获取短信验证码成功");
		}else if(StringUtils.isNotBlank(ret)&&ret.equals("isv.MOBILE_COUNT_OVER_LIMIT")){
			logger.error("获取短信发送过于频繁");
			jsonResponse.setRetDesc("获取短信发送过于频繁");
		}
		return jsonResponse;
	}
	
	
	
}
