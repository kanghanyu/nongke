package com.khy.utils;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.khy.service.impl.PayServiceImpl;

@Component
public class PhoneUtils {
	public final static Logger logger = LoggerFactory.getLogger(PhoneUtils.class);

	@Autowired
	private RestTemplate temple;
	public static final String key ="04c81e3a0a14d6e32472903b2862923a";//申请的接口Appkey
	public static final String openId="JH438d875c49b597d21b97323e4ef87fcb";//在个人中心查询
	
	public static final String checkPhoneNum = "http://op.juhe.cn/ofpay/mobile/telcheck?cardnum=*&phoneno=!&key="+key;
	
	public static final String telQueryUrl="http://op.juhe.cn/ofpay/mobile/telquery?cardnum=*&phoneno=!&key="+key;
	public static final String onlineUrl="http://op.juhe.cn/ofpay/mobile/onlineorder?key="+key+"&phoneno=!&cardnum=*&orderid=@&sign=$";
	public static final String yueUrl="http://op.juhe.cn/ofpay/mobile/yue?key="+key+"&"+"timestamp=%&sign=$";
	public static final String orderstaUrl="http://op.juhe.cn/ofpay/mobile/ordersta?key="+key+"&orderid=!";
	private Map<String, String>map = new HashMap<>();
	
	@PostConstruct
	private void initTipMap(){
		map.put("0","允许充值的手机号码及金额");
		map.put("208501","不允许充值的手机号码及金额");
		map.put("208502","请求手机号和面值查询商品信息失败，请重试");
		map.put("208503","运营商地区维护，暂不能充值");
		map.put("208505","错误的手机号码");
		map.put("208506","错误的充值金额");
		map.put("208509","错误的订单号");
		map.put("208515","校验值sign错误");
		map.put("208517","当前账户可用余额不足");
	}
	/**
	 * 1.检测手机号码是否能充值接口
	 * 
	 * @param phone
	 *            手机号码
	 * @param cardnum
	 *            充值金额,目前可选：5、10、20、30、50、100、300
	 * @return 返回错码，0为允许充 值的手机号码及金额，其他为不可以或其他错误
	 * @throws Exception
	 */
	public JSONObject checkPhoneNum(String phone, int cardnum){
		String url = checkPhoneNum.replace("*", cardnum + "").replace("!", phone);
		JSONObject json = get(url, 2);
		return json;
	}

	/**
	 * 2.根据手机号和面值查询商品信息
	 * 
	 * @param phone
	 *            手机号码
	 * @param cardnum
	 *            充值金额,目前可选：5、10、20、30、50、100、300
	 * @return String类型结果
	 * @throws Exception
	 */
	public JSONObject telQuery(String phone, int cardnum){
		String url = telQueryUrl.replace("*", cardnum + "").replace("!", phone);
		JSONObject json = get(url, 2);
		return json;
	}

	/**
	 * 3.依据用户提供的请求为指定手机直接充值
	 * 
	 * @param phone
	 *            手机号码
	 * @param cardnum
	 *            充值金额,目前可选：5、10、20、30、50、100、300
	 * @param orderid
	 *            商家订单号，8-32位字母数字组合，自定义
	 * @return 返回String结果
	 * @throws Exception
	 */
	public JSONObject onlineOrder(String phone, int cardnum, String orderId){
		String ret = openId + key + phone + cardnum + orderId;
		String sign = DigestUtils.md5DigestAsHex(ret.getBytes());
		String url = onlineUrl.replace("*", cardnum + "").replace("!", phone).replace("@", orderId).replace("$", sign);
		JSONObject json = get(url, 2);
		return json;
	}

	/**
	 * 4.查询账户余额
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONObject yuE(){
		String timestamp = System.currentTimeMillis() / 1000 + "";
		String ret = openId + key + timestamp;
		String sign = DigestUtils.md5DigestAsHex(ret.getBytes());
		String url = yueUrl.replace("%", timestamp).replace("$", sign);
		JSONObject json = get(url, 2);
		return json;
	}

	/**
	 * 5.订单状态查询
	 * 
	 * @param orderid
	 *            商家订单号
	 * @return 订单结果
	 * @throws Exception
	 */
	public JSONObject orderSta(String orderId){
		String url = orderstaUrl.replace("!",orderId);
		JSONObject json = get(url, 2);
		return json;
	}
	
	public JSONObject get(String url){
		try {
			return temple.getForObject(url, JSONObject.class);
		} catch (RestClientException e) {
			logger.error("请求路径url={}异常",url);
			return null;
		}
	}
	
	public JSONObject get(String url,int num){
		JSONObject json = null;
		while(num > 0){
			json = get(url);
			if(null !=json){
				break;
			}else{
				num--;
			}
		}
		return json;
	}
}
