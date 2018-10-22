package com.khy.common;

public class Constants {
	public static final String PREFIX = "manager.";


	/**oss服务器上配置的 endpoint*/
    public static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    public static String accessKeyId = "LTAIBHCNeY6xhOlS";
    public static String accessKeySecret = "00cd2etgTUe6rD4bQXGQv3uZzUJzAA";
    public static String bucketName = "nongke";
    public static final String BASE_URL = "http://nongke.oss-cn-beijing.aliyuncs.com/";
    public static final String INVITE_USER_REGISTER="http://www.nongke365.com:8081/register.html?uid=";

 // 初始化ascClient需要的几个参数
    public static final String product = "Dysmsapi";// 短信API产品名称（短信产品名固定，无需修改）
    public static final String domain = "dysmsapi.aliyuncs.com";// 短信API产品域名（接口地址固定，无需修改）

    
    //缓存的key值
    public static final String USER_SMS_REGISTER = PREFIX.concat("user:sms:register:");
    public static final String USER_SMS_FIND_PASSWORD = PREFIX.concat("user:sms:find:password:");
    public static final String USER_SMS_UPDATE_BANKINFO = PREFIX.concat("user:sms:update:bankinfo:");
    public static final String USER_LOGIN = Constants.PREFIX.concat("user.login:");
    public static final String USER_CREATE_PRE_ORDER_LOCK = PREFIX.concat("user:create:pre:order:lock:");
    public static final String USER_ONLINE_PAY_LOCK = PREFIX.concat("user:online:pay:lock:");
    public static final String ONLINE_PARARME = "online_parame:";
    public static final String LOCK_USER = PREFIX.concat("lock:user:");
    public static final String LOCK_PRODUCT = PREFIX.concat("lock:product:");
    
    public static final String USER_PHONE_RECHARGE= PREFIX.concat("user:phone:recharge");
    
    public static final String LOCK = "lock";
    public static final int FIVE_MINUTE = 60*5;
    public static final int TWO_MINUTE = 60*2;
    public static final int ONE_DAY = 60*60*24;
    
    
    ///常用的变量值
    /**vip用户内容*/
    public static final int VIP_USER = 1;
    public static final int GENERAL_UER = 0;
    
    /**点卡购物的折扣内容*/
    public static final String CARD_DISCOUNT = "card_discount";
    /**vip的价格内容*/
    public static final String VIP_PRICE = "vip_price";
    public static final String VIP_PRICE_FORMONEY = "vip_price_formoney";
    
    /**vip购买商品的折扣内容*/
    public static final String VIP_DISCOUNT = "vip_discount";
	/**邮费*/
    public static final String POSTAGE = "postage";
    
    /**vip会员每个自然月话费充值的额度*/
    public static final String VIP_PHONE_RECHARGE = "vip_phone_recharge";
    
    /**话费充值1级邀请人佣金提成 */
    public static final String PAY_PHONE_BILL_EXTRACT_1 = "pay_phone_bill_extract_1";
    /**话费充值2级邀请人佣金提成 */
    public static final String PAY_PHONE_BILL_EXTRACT_2 = "pay_phone_bill_extract_2";
    /**话费充值3级邀请人佣金提成 */
    public static final String PAY_PHONE_BILL_EXTRACT_3 = "pay_phone_bill_extract_3";
    /**商品购买所有级别的佣金提成*/
    public static final String PAY_PRODUCT_BILL_EXTRACT = "pay_product_bill_extract";
    /**充值vip 1级邀请人佣金提成 单位元*/
    public static final String PAY_VIP_BILL_EXTRACT_1 = "pay_vip_bill_extract_1";
    /**充值vip 2级邀请人佣金提成5元*/
    public static final String PAY_VIP_BILL_EXTRACT_2 = "pay_vip_bill_extract_2";
    /**充值vip 3级邀请人佣金提成15元*/
    public static final String PAY_VIP_BILL_EXTRACT_3 = "pay_vip_bill_extract_3";
	
    
    
    
    //orderType
    public static final int PAY_VIP = 1;
    public static final int PAY_CARD = 2;
    public static final int PAY_PHONE = 3;
    public static final int PAY_PRODUCT = 4;
    
    //payType
    public static final int CARD_PAY = 1;
    public static final int MONEY_PAY = 2;
    public static final int ALIPAY = 3;
    public static final int WEIXIN_PAY = 4;
    
    

    //流水内容
    //收入/支出
    public static final int RECORD_INCOME = 1;
    public static final int RECORD_PAY = 2;
    
    //余额.点卡.佣金.人民币
    public static final int RECORD_MONEY = 1;
    public static final int RECORD_CARD_MONEY = 2;
    public static final int RECORD_COMMISSION = 3;
    public static final int RECORD_RMB = 4;
    
    
    
    //alipay
    public static final String ALIPAY_APPID = "2018071960718514"; 
    public static final String PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCATYHeBHKiq07iELm6viJazjVRdIqZrxtlWkTUupxsM+4NtFfHP7zRHk2E28zjRgx+fG5bTX/Hp6VT/oeh7UpmJ5uYRpNfsfFjP5mmcR66T2D4fkRjXJWpa70Aj0TGJ4CJhFhbKhud+WEeUK/H+hiRTOTKJ+w6L4LtxLmeHbRhGwxBY61zwlnqjccjpaVY+5SsYoaHP0mucyuXhiRpgctwV5QVvzEr3GnBsGXAkf1lfuVWu2HxWuJ9YZBUT93zgVGPxLE42g1a6bkocSGvaxsfO5zKj8/qGOd4CEZ6i3LEYOc2ZaNskmGqmafty5Bki5xcPlG8y9bcBTfbU96b7MR3AgMBAAECggEAW39quvsvCWTjt4I4Ab3wYBOooHNdGGZxQiFS7uY6AUwzW2CeLMD+lZ9npvTdM8NTy6xPQxuMocSjubfN4COKKDnc04aZOOh8fcvwdms7f6B2RiA+AyIWtSDZQ5nocOvg1bElFbZ8xXb/1I+0HAf2F0Df9G7uHzycwZXGzcTfS1PnsAhwWSu1vm6DQlN+8AC9bhXBlIwmOX3VgW1wSbDmpOAj5w5/qKGFsBfKbm1XoY8xwWJCEI1UoGWPwIac384kLpnrjlvvGw90z6nhwmtqpial/ODZT9hhJVm3pMQVKHStEZMc1jvDmTfFvuy+8DPUJC1gYdQ+SypXHsIlXOaJkQKBgQDexoTo+yU51kryJmrREK/CANk5FnpSa8ylEvn4GXYmAeyKc57uRl0kwRyqiKhKTqFgNgApGOYms+WlSusYQPeG1GeKmmvsstHBk93AQF+NJmthCuKFIVh/QrfEzeytzExGnymcEfysuGNwQasJXuzPQeGyVKLudJWLo9YLh/VT+QKBgQCTcA6WzV8kkeQCBpYuS9YdjalIIC5Xq8ukB2S1pfbA27HRjdMJQw5/dE7EafNiJkIHmQb3pfwJaNxTeC3BJLMJibAwMom51toDJIOZBHKE/Fx2CA/743eDWb2GQZvLqkCUv0NEhjLRVJckLJ1Wak7pGoz5gSUAcT+UPctq5BgX7wKBgCBGlMUIdek+Qdd5d2pYx44M/2vE9O3yDlEwQaUPU03IY5PfknwlF6OWkuemKvOxFrJQwI8zu0yKLdBX3V3DM9mF9oZfPxmpArjQCoWrDezuTwpdQFpXbXHqoaNSzmKTwc1NjAK/nGMNx+JgsT4HUbnpaqCioIR5wZzcnMtI5GNJAoGAWUpB655RAorANWHbzgnrkFZcsOucZUgLujkoCsKKH95tYGWt6ywLObEsitNHrKdS0dshVxXU/7XbyUk9HH2nWB1k8KuKsVunCMQyVpdU66kSLgFP/NKTbJ8KoqZVPhDLr8ntaTCE0jWSmzvRxaX4SD/mJKzLxerQAG00JnYhCU0CgYA9uesU//XD5/H18ScI8oaOdB0iQycZB1oI0BB9l6VsnScdG9IyECWJuO+IEY9p43UtahIq/DImjh4yPJR1NzD2DLPrty0TodV3wOL6ubc3obIOJclpAsaulmDLB9/804nLKBwDiiu+jgT9oxfILTVjFCijoNjt32BtfQJq63qyUQ==";
    public static final String METHOD = "alipay.trade.app.pay";
    public static final String FORMAT_JSON = "json";
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String ALI_PUBLIC_KEY = "";
    public static final String VERSION = "1.0";
    public static final String SIGN_TYPE_RSA2 = "RSA2";
    public static final String NOTIFY_URL = "http://www.nongke365.com:8081/pay/async/notify";
    public static final String TIMEOUT_EXPRESS = "5m";
}
