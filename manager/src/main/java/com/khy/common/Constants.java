package com.khy.common;

public class Constants {
	public static final String PREFIX = "manager.";


	/**oss服务器上配置的 endpoint*/
    public static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    public static String accessKeyId = "LTAIBHCNeY6xhOlS";
    public static String accessKeySecret = "00cd2etgTUe6rD4bQXGQv3uZzUJzAA";
    public static String bucketName = "nongke";
    public static final String BASE_URL = "http://nongke.oss-cn-beijing.aliyuncs.com/";
    public static final String INVITE_USER_REGISTER="https://www.nongke365.com:8081/register.html?uid=";

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
    
    public static final String LOCK_NOTIFY_ORDER = PREFIX.concat("lock:notify:order:");
    
    public static final String USER_PHONE_RECHARGE= PREFIX.concat("user:phone:recharge:");
    
    public static final String LOCK = "lock";
    public static final int FIVE_MINUTE = 60*5;
    public static final int TWO_MINUTE = 60*2;
    public static final int ONE_DAY = 60*60*24;
    public static final int SEVEN_DAY = 60*60*24*7;
    
    
    ///常用的变量值
    /**vip用户内容*/
    public static final int VIP_USER = 1;
    public static final int GENERAL_UER = 0;
    
    /**点卡购物的折扣内容*/
    public static final String CARD_DISCOUNT = "card_discount";
    /**vip的价格内容*/
    public static final String VIP_PRICE = "vip_price";
    /**vip转成余额 的价格*/
    public static final String VIP_TO_USER_MONEY = "vip_to_user_money";
    
    /**vip购买商品的折扣内容*/
    public static final String VIP_DISCOUNT = "vip_discount";
    
    /**vip充值话费的折扣内容*/
    public static final String VIP_PHONE_DISCOUNT = "vip_phone_discount";
    
	/**邮费*/
    public static final String POSTAGE = "postage";
    
    /**vip会员每个自然月话费充值的额度*/
    public static final String VIP_PHONE_RECHARGE = "vip_phone_recharge";
    
    /**话费充值1级邀请人佣金提成 */
    public static final String PAY_PHONE_BILL_EXTRACT = "pay_phone_bill_extract_";
    public static final String PAY_PHONE_BILL_EXTRACT_1 = "pay_phone_bill_extract_1";
    /**话费充值2级邀请人佣金提成 */
    public static final String PAY_PHONE_BILL_EXTRACT_2 = "pay_phone_bill_extract_2";
    /**话费充值3级邀请人佣金提成 */
    public static final String PAY_PHONE_BILL_EXTRACT_3 = "pay_phone_bill_extract_3";
    /**商品购买所有级别的佣金提成*/
    public static final String PAY_PRODUCT_BILL_EXTRACT = "pay_product_bill_extract";
    /**充值vip 1级邀请人佣金提成 单位元*/
    public static final String PAY_VIP_BILL_EXTRACT = "pay_vip_bill_extract_";
    public static final String PAY_VIP_BILL_EXTRACT_1 = "pay_vip_bill_extract_1";
    /**充值vip 2级邀请人佣金提成5元*/
    public static final String PAY_VIP_BILL_EXTRACT_2 = "pay_vip_bill_extract_2";
    /**充值vip 3级邀请人佣金提成15元*/
    public static final String PAY_VIP_BILL_EXTRACT_3 = "pay_vip_bill_extract_3";
    
    /**注册之后的跳转页面*/
    public static final String REGISTER_REDIRECT_URL = "register_redirect_url";
	
    
    
    
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
    
    // order_status
    public static final int ORDER_STATUS_WWC = 1;
    public static final int ORDER_STATUS_WC = 2;
    
    //order_payStatus
    public static final int ORDER_PAYSTATUS_WFK = 1;
    public static final int ORDER_PAYSTATUS_YFK = 2;
    public static final int ORDER_PAYSTATUS_YQX = 3;
    public static final int ORDER_PAYSTATUS_YFF = 4;
    
    //isBill
    public static final int ORDER_ISBILL_WCZ = 1;
    public static final int ORDER_ISBILL_YCZ = 2;
    
    
    //product_status
    public static final int PRODUCT_STATUS_WSJ = 0;
    public static final int PRODUCT_STATUS_SJ = 1;
    public static final int PRODUCT_STATUS_YXJ = 2;
    

    //流水内容
    //收入/支出 payType
    public static final int RECORD_INCOME = 1;
    public static final int RECORD_PAY = 2;
    
    //余额.点卡.佣金.人民币 type
    public static final int RECORD_MONEY = 1;
    public static final int RECORD_CARD_MONEY = 2;
    public static final int RECORD_COMMISSION = 3;
    public static final int RECORD_RMB = 4;
    
    
    //user_bill -->type
    public static final int BILL_INCOME = 1;
    public static final int BILL_PAY = 2;
    
    
    //user_bill -->billType
    public static final int BILL_VIP = 1;
    public static final int BILL_CARD = 2;
    public static final int BILL_PHONE = 3;
    public static final int BILL_PRODUCT = 4;
    
    
    //alipay
    public static final String ALIPAY_APPID = "2018100161603116"; 
    public static final String PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCzzHc4ZvnteUpvJ5xx8x1ZwmytGnAQeP24ujsUWZqcdV0d3yRDWruMtw18Ka3YZpXZwyMz1RG4MeZICkHqnKmY3seKdMqD5Tu9s10IGyvRRg7d6kx+lofxnuTumlY36lR8dwbMv3mOTtqdZf7LkWcI9Bn85Kp+gpGZ9H7ciHjjjsp1OsVoUASE33b2wx3EvtkyPOusZA7IsxgGyf7w8Rum7qJ8S7dUsy4nolnV2PEsf28ZsVLgkQ3WEh/HC9Msp86zZvBnNl5E3O0TSu4JzRWtRHoFZEHxWJxbS6ePH2akreOjaDNXwZfhdP8wtz/ewRvsA+UyBUV+wqegr6SJYAddAgMBAAECggEARRKCpodg8msLPmKQRnJoTyhTCblDXgCtMD9THQFDVWH9ENG9o6aCsI9OPenhIGZgoRTHPE1ZjlKVLg3Kvu/Kc7D6JWWyuzLgXYM/dTLDBZWNdpFhK6x2s0sglc1y2+5I4Lv1P8k7UIFYmOkrFldYpubn6PCL0ygj+WolJmHDV1/IylYQFdIMVMFHlhOPabYwCYH+e/nqKUfEv1Vv9BA0yISiRYpQzTVfze1BiCGPHe+YXgt8yx8ojUPMwWc+t+1vV9FZpb/nIp3h3YnXnJqwpmAxqJzIz5v2c4QlMtzCZy6foKe+lfotlPDbjlgzfEsa5iBYvwq1w9gHL/12O7Ai/QKBgQDxSsc1f8ML8rk6KvEX3huLue82sabxe+yZ7EkhOjcDoo29uowCKQ0lZY4Vjth4NBnH/443ChVQEeGpeGQNQ8NXlHkSnX14PNECxpi4ckc1gPacP7pkKqGB0cowea3Mce++5socgE0p0Rx1P9TLMqTQPQx2pFifdJfQBXTIBzgJZwKBgQC+wh5znKz1P3orwtnJU8upeSMSk4eEeQRf9E96JL5BDD6JuyTTOMiqlDXtpvI+tnXgt7uMmElAEKtkp0zRqYYaej2zareJhxsXVvAzwdGdVost1kYIUK/x3DdnfwIrHQDWYRdr8MaKhdz+EVfl41jqVSPL0hSES++nZk8ahYc6mwKBgD9Sn4rzt404KtIQGOtpl5MIvbV909EF86bFcr+slml2zHnhLgMXIOMSjsIrlbWd/rIU6A6br9Iujyk5dhBhGy+twERZXmcilruCwOuO2g85g/6nXtQQuAfotYYZOLoxoHhZ+uqV5So3PZ+rm72j0crwaofsQ3G9WGX4OJiSBGu7AoGAa0JXQQaNsfCZT1Xmg2+cipRUhjNx9ajeLt3+el2CpKyv1V3nHOVG37Mqh5BhkaWirlJhIHLYbS9yT8xKQ3Ggt+6YFvQgczIGp574vQF0TZXF+GYNRsunwsTiWk1t+LP8Lo61Tro5yNXwJuO1msu0yT5KPGbJpoHUJTo/rFf8a5UCgYAafe3qUQ9O18R6jQosYy2j6yb3ZMqCu/RFHwKUccYstaOt2mPoXlnfOFTvEhK4zuCONuqJYt0BPnU1hLJwG2QEQSWo+4Eld38lGkPP+ihk2fI+laJ0714l0FXoxbublV3rX4ozgIul4aezKR3LhZhLajaXsJpSCceFjbEPFEJKvg==";
    public static final String METHOD = "alipay.trade.app.pay";
    public static final String FORMAT_JSON = "json";
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String ALI_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiuJuogLsSCok2NVCvqhmqlrNbYLbGtJngeJ4pKS7EYxsXFbXGtl3QMWTWLFsdFkZCJ9rU9beQsJi/e8rjVojjSxX7Mq4CYZVu+/soXFde3MEguSlk32bAjVCgr2fBGL0ivY/qOW0DzRYJBa/b72z/8ZYCX+Idw0H5DOqDoAwzpqxvWc9M16VI9T26nOfzYsmt/KkOCoDJnCvpVJPf3so1iVK/rqANLMBU/lJbIVN1lJpC8f3S0H8eKhAicVcWMinWTYkbH65jtoARzP/CWvh40CKpHrEVfbOQatrKIW8uTHnozGsBguQh1dg0w00FDsOW+mn3qc227bvMd2JUJ27IQIDAQAB";
    public static final String VERSION = "1.0";
    public static final String SIGN_TYPE_RSA2 = "RSA2";
    public static final String NOTIFY_URL = "https://www.nongke365.com:8081/pay/alipay/notify";
    public static final String TIMEOUT_EXPRESS = "20m";
    public static final String AES_KEY = "qNbuDK656ziShag3287ujA==";
    public static final String AES_FULLALG = "AES/CBC/PKCS5Padding";
    
}
