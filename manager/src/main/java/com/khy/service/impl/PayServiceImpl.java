package com.khy.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.khy.common.Constants;
import com.khy.common.JsonResponse;
import com.khy.entity.OrderInfo;
import com.khy.entity.Product;
import com.khy.entity.User;
import com.khy.exception.BusinessException;
import com.khy.mapper.CartMapper;
import com.khy.mapper.OrderInfoMapper;
import com.khy.mapper.ProductMapper;
import com.khy.mapper.UserMapper;
import com.khy.mapper.dto.CartDTO;
import com.khy.mapper.dto.PayProductDetailDTO;
import com.khy.mapper.dto.PreOrderDTO;
import com.khy.mapper.dto.PreOrderResultDTO;
import com.khy.mapper.dto.RechargeResultDTO;
import com.khy.mapper.dto.RechargeSubmitDTO;
import com.khy.mapper.dto.SubmitOrderDTO;
import com.khy.mapper.dto.SubmitOrderResultDTO;
import com.khy.service.PayService;
import com.khy.utils.BeanUtils;
import com.khy.utils.PayUtil;
import com.khy.utils.PhoneUtils;
import com.khy.utils.SessionHolder;
import com.khy.utils.Utils;
@Transactional
@Service
public class PayServiceImpl extends BaseService implements PayService {
	public final static Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);
	private final static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	private final static BigDecimal ZERO = new BigDecimal("0.00");
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private OrderInfoMapper orderInfoMapper;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private PhoneUtils PhoneUtils;
	
	@Override
	public JsonResponse<PreOrderResultDTO> createPreOrder(PreOrderDTO dto) {
		logger.info("提交生成前置订单的请求参数{}",JSON.toJSON(dto));
		JsonResponse<PreOrderResultDTO>jsonResponse = new JsonResponse<>();
		if(null == dto){
			jsonResponse.setRetDesc("请求参数不能为空");
			return jsonResponse;
		}
		if(CollectionUtils.isEmpty(dto.getList())){
			jsonResponse.setRetDesc("购买商品列表为空");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		User userDb  = userMapper.getUserByUid(uid);
		if(null == userDb){
			jsonResponse.setRetDesc("当前用户状态异常");
			return jsonResponse;
		}
		Integer isVip = userDb.getIsVip();
		Integer orderType = Constants.PAY_PRODUCT;
		//针对生成前置订单的用户加锁防止生成多个前置订单;
		boolean lock = cacheService.lock(Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)),Constants.LOCK ,Constants.FIVE_MINUTE);
		logger.info("提交生成前置订单的加锁操作key = {},结果 = {}",Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)),lock);
		if(lock){
			jsonResponse.setRetDesc("订单生成失败请稍后再试");
			return jsonResponse;
		}
		try {
			//开始校验商品的信息;
			PreOrderResultDTO ret = new PreOrderResultDTO();
			ret.setUid(uid);
			JSONObject json = checkProduct(dto.getList(),ret);
			logger.info("提交生成前置订单的校验商品内容的结果list={}",JSON.toJSON(dto));
			if(json.getIntValue("code") == 2000){
				logger.error("提交生成前置订单的校验商品内容的失败ret = "+json.toString());
				jsonResponse.setRetDesc(json.getString("msg"));
				return jsonResponse;
			}
			if(CollectionUtils.isEmpty(ret.getList())){
				jsonResponse.setRetDesc("生成的订单详情内容为空");
				return jsonResponse;
			}
			//生成前置订单的id;
			ret.setOrderId(Utils.getOrderId());
			Double totalMoney = ret.getList().stream().collect(Collectors.summingDouble(PayProductDetailDTO::getTotal));
			BigDecimal b1 = new BigDecimal(totalMoney.toString());
			ret.setTotalMoney(b1);
			ret.setOrderType(orderType);
			Map<String, String> online = getOnline();
			if(null ==online || online.size() == 0){
				jsonResponse.setRetDesc("获取在线参数异常,请联系管理员");
				return jsonResponse;
			}
			logger.info("获取在线参数的map={}",JSON.toJSON(online));
			if(null != isVip && isVip.intValue()==Constants.VIP_USER){
				String discountRet = online.get(Constants.VIP_DISCOUNT);
				if(StringUtils.isBlank(discountRet)){
					jsonResponse.setRetDesc("会员折扣异常请您联系管理员稍后再试");
					return jsonResponse;
				}
				float discount = Integer.valueOf(discountRet).intValue()/100F;
				ret.setDiscount(discount);//去在线表里面获取内容
				ret.setDiscountDetail("VIP会员购买商品"+discountRet+"折");
				ret.setDiscountMoney(b1.multiply(new BigDecimal(discountRet)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP));//获取两位以内容的
			}
			BigDecimal totalCardMoney ;
			BigDecimal totalPay ;
			String cartDiscount = online.get(Constants.CARD_DISCOUNT);
			if(StringUtils.isBlank(cartDiscount)){
				jsonResponse.setRetDesc("点卡折扣异常请您联系管理员稍后再试");
				return jsonResponse;
			}
			String postage = online.get(Constants.POSTAGE);
			if(StringUtils.isBlank(postage)){
				jsonResponse.setRetDesc("获取商品的运费异常请您联系管理员稍后再试");
				return jsonResponse;
			}
			ret.setPostage(new BigDecimal(postage));
			if(null != ret.getDiscountMoney()){
				totalPay = ret.getDiscountMoney();
				totalCardMoney = ret.getDiscountMoney().multiply(new BigDecimal(cartDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
			}else{
				totalCardMoney =ret.getTotalMoney().multiply(new BigDecimal(cartDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
				totalPay = ret.getTotalMoney();
			}
			ret.setTotalCardMoney(totalCardMoney);
			ret.setTotalCardPay(totalCardMoney.add(ret.getPostage()));
			ret.setTotalPay(totalPay.add(ret.getPostage()));
			Calendar calendar = Calendar.getInstance();
			Date now = calendar.getTime();
			calendar.add(Calendar.MINUTE, 30);
			Date invalidTime = calendar.getTime();
			ret.setCreateTime(now);
			ret.setProductDetail(JSONObject.toJSONString(ret.getList()));
			OrderInfo info = new OrderInfo();
			BeanUtils.copyProperties(ret, info);
			info.setStatus(Constants.ORDER_STATUS_WWC);
			info.setPayStatus(Constants.ORDER_PAYSTATUS_WFK);
			info.setIsBill(Constants.ORDER_ISBILL_WCZ);
			info.setInvalidTime(invalidTime);
			/**生成订单信息*/
			logger.info("插入订单信息内容orderInfo="+JSON.toJSON(info));
			int flag = orderInfoMapper.insert(info);
			if(flag>0){
				jsonResponse.success(ret, "生成前置订单成功");
			}else{
				jsonResponse.setRetDesc("生成前置订单失败");
			}
			logger.info("返回去的前置订单信息"+JSON.toJSON(ret));
			//还可以设置一些额外的参数内容;//TODO 比如用户的余额信息
			Map<String, Object> extra = new HashMap<>();
			extra.put("money", userDb.getMoney());
			extra.put("cardMoney", userDb.getCardMoney());
			extra.put("isVip", userDb.getIsVip());
			extra.put("cartDiscount",cartDiscount);
			jsonResponse.setExtra(extra);
		} catch (Exception e) {
			jsonResponse.setRetDesc("生成前置订单异常");
			e.printStackTrace();
			logger.error("提交订单生成前置订单异常"+e.getMessage());
			throw new BusinessException("提交订单生成前置订单异常"+e.getMessage());
		}finally{
			cacheService.releaseLock(Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)));
		}
		return jsonResponse;
	}


	@Override
	public JsonResponse<SubmitOrderResultDTO> payForProductOnline(SubmitOrderDTO dto) {
		logger.info("用户在线支付商品订单的请求参数SubmitOrderDTO = "+JSON.toJSONString(dto));
		JsonResponse<SubmitOrderResultDTO>jsonResponse = new JsonResponse<>();
		if(null == dto){
			jsonResponse.setRetDesc("请求参数不能为空");
			return jsonResponse;
		}
		if(StringUtils.isBlank(dto.getOrderId())){
			jsonResponse.setRetDesc("订单的id不能为空");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		Date now = new Date();
		String orderId = dto.getOrderId();
		OrderInfo info  = new OrderInfo();
		info.setUid(uid);
		info.setOrderId(orderId);
		info.setStatus(Constants.ORDER_STATUS_WWC);
		info.setPayStatus(Constants.ORDER_PAYSTATUS_WFK);
		OrderInfo orderInfo = orderInfoMapper.getPayOrder(info);
		logger.info("用户在线支付订单的获取数据库订单信息orderInfoDb={},查询的参数内容info={}",JSON.toJSONString(orderInfo),JSON.toJSONString(info));
		if(null == orderInfo){
			jsonResponse.setRetDesc("当前前置订单状态异常");
			return jsonResponse;
		}
		if(orderInfo.getInvalidTime().before(now)){
			//说明该订单已经过了支付的时间了订单失效
			//更新订单的状态内容;
			OrderInfo updateInfo = new OrderInfo();
			updateInfo.setPayStatus(Constants.ORDER_PAYSTATUS_YQX);
			updateInfo.setStatus(Constants.ORDER_STATUS_WC);
			updateInfo.setUid(uid);
			updateInfo.setOrderId(orderId);
			logger.info("用户提交支付的订单orderId={},当前订单已经失效,去更新数据库该订单的信息内容updateInfo={}",orderId,JSON.toJSON(updateInfo));
			orderInfoMapper.update(orderInfo);
			setUserMoney(orderInfo);
			jsonResponse.setRetDesc("当前订单30分钟内未付款,已失效");
			return jsonResponse;
		}
		
		if(orderInfo.getOrderType() != Constants.PAY_PRODUCT){
			jsonResponse.setRetDesc("商品购买支付的专用付款方式");
			return jsonResponse;
		}
		Map<String, String> online = getOnline();
		if(null ==online || online.size() == 0){
			jsonResponse.setRetDesc("获取在线参数异常,请联系管理员");
			return jsonResponse;
		}
		String key = null;
		List<Long> productIds = null;
		SubmitOrderResultDTO ret = new SubmitOrderResultDTO();
		try {
			JSONObject userRet = getUserByUidAndLock(uid);
			if(userRet.getIntValue("code") == 1000){
				jsonResponse.setRetDesc(userRet.getString("msg"));
				return jsonResponse;
			}
			User userDb = userRet.getObject("user",User.class);
			if(null == userDb){
				jsonResponse.setRetDesc("当前用户状态异常");
				return jsonResponse;
			}
			//为了防止重复付款 先对用户的付款订单进行加锁
			key = Constants.USER_ONLINE_PAY_LOCK.concat(uid).concat(orderId);
			boolean lock = cacheService.lock(key,Constants.LOCK ,Constants.FIVE_MINUTE);
			logger.info("用户在线支付订单的加锁操作key = {},结果 = {}",key,lock);
			if(lock){
				jsonResponse.setRetDesc("订单支付中,请勿重复支付");
				return jsonResponse;
			}
			List<Product>listProduct = new ArrayList<>();
			JSONObject json = checkProductOrderInfo(orderInfo,dto,userDb,online,listProduct);
			if(CollectionUtils.isNotEmpty(listProduct)){
				productIds = listProduct.stream().map(Product::getProductId).collect(Collectors.toList());
			}
			logger.info("在线支付校验订单内容结果json={}",json.toString());
			
			if(json.getIntValue("code") == 2000){
				logger.error("在线支付校验订单内容结果失败json={}",json.toString());
				jsonResponse.setRetDesc(json.getString("msg"));
				return jsonResponse;
			}
			Integer payType = dto.getPayType();
			orderInfo.setPayType(payType);
			BeanUtils.copyProperties(orderInfo, ret);
			if (payType == Constants.CARD_PAY) {
				// 包含更新订单状态内容
				orderInfo.setPayTime(now);
				orderInfo.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
				orderInfoMapper.update(orderInfo);
				
				// 更新用户的账户信息(点卡)
				BigDecimal cardMoney = userDb.getCardMoney() != null ? userDb.getCardMoney() : ZERO;
				userDb.setCardMoney(cardMoney.subtract(dto.getTotalPay()));
				userMapper.updateUser(userDb);

				// 更新用户的账务流水
				String descr = "购买商品花费点卡" + dto.getTotalPay() + "元";
				saveUserRecord(uid, Constants.RECORD_PAY, Constants.RECORD_CARD_MONEY, dto.getTotalPay(), cardMoney,
						orderId, descr, now);
				// 更新商品的数量内容
				batchUpdateProduct(listProduct);
				ret.setFlag(1);		
			} else if (payType == Constants.ALIPAY || payType == Constants.WEIXIN_PAY) {
				if (dto.getRmb().compareTo(ZERO) == 0 && dto.getCornMoney().compareTo(dto.getTotalPay()) == 0) {
					// 说明全部是余额抵扣的和点卡购买的路径一样
					// 包含更新订单状态内容
					orderInfo.setPayTime(now);
					orderInfo.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
					orderInfoMapper.update(orderInfo);

					// 更新用户的账户信息(余额)
					BigDecimal money = userDb.getMoney() != null ? userDb.getMoney() : ZERO;
					userDb.setMoney(money.subtract(dto.getCornMoney()));
					userMapper.updateUser(userDb);

					// 更新用户的账务流水
					String descr = "购买订单余额抵扣" + dto.getCornMoney() + "元";
					saveUserRecord(uid, Constants.RECORD_PAY, Constants.RECORD_MONEY, dto.getCornMoney(), money,
							orderId, descr, now);
					// 更新商品的数量内容
					batchUpdateProduct(listProduct);
					ret.setFlag(1);
				} else {
					// 需要在线支付的拼装验签内容;
					// 根据支付方式选择验签的内容;
					logger.info("在线支付拼装网关验签内容的接口orderInfo{}", JSON.toJSON(orderInfo));
					String sign = null;
					if (payType == Constants.ALIPAY) {
						sign = PayUtil.setProductSign(dto);
					} else if (payType == Constants.WEIXIN_PAY) {
						// 这个是微信的签名内容
					}
					if(StringUtils.isBlank(sign)){
						jsonResponse.setRetDesc("获取APP支付验签失败");
						return jsonResponse;
					}
					orderInfoMapper.update(orderInfo);
					ret.setPaySign(sign);
					ret.setFlag(2);
				}
			}
		} catch (Exception e) {
			logger.error("用户在线支付商品订单--->接口异常" + e.getMessage());
			throw new BusinessException("用户在线支付商品订单--->接口异常" + e.getMessage());
		} finally {
			if(StringUtils.isNotBlank(key)){
				cacheService.releaseLock(key);
			}
			// 清除用户锁和商品锁
			cacheService.releaseLock(Constants.LOCK_USER + uid);
			// 批量清除商品锁内容
			if (CollectionUtils.isNotEmpty(productIds)) {
				for (Long productId : productIds) {
					cacheService.releaseLock(Constants.LOCK_PRODUCT + productId);
				}
			}
		}
		jsonResponse.success(ret);
		return jsonResponse;
	}

	@Override
	public JsonResponse<RechargeResultDTO> recharge(RechargeSubmitDTO dto) {
		JsonResponse<RechargeResultDTO>jsonResponse = new JsonResponse<>();
		if(null == dto){
			jsonResponse.setRetDesc("请求参数不能为空");
			return jsonResponse;
		}
		User user = SessionHolder.currentUser();
		if(null == user){
			jsonResponse.setRetDesc("请重新登录");
			return jsonResponse;
		}
		String uid = user.getUid();
		Map<String, String> online = getOnline();
		logger.info("获取在线参数的map={}",JSON.toJSON(online));
		if(null ==online || online.size() == 0){
			jsonResponse.setRetDesc("获取在线参数异常,请联系管理员");
			return jsonResponse;
		}
		Integer orderType = dto.getOrderType();
		RechargeResultDTO ret = new RechargeResultDTO();
		try {
			JSONObject userRet = getUserByUidAndLock(uid);
			if(userRet.getIntValue("code") == 1000){
				jsonResponse.setRetDesc(userRet.getString("msg"));
				return jsonResponse;
			}
			user = userRet.getObject("user",User.class);
			if(null == user){
				jsonResponse.setRetDesc("当前用户状态异常");
				return jsonResponse;
			}
			//针对生成前置订单的用户加锁防止生成多个前置订单;
			boolean lock = cacheService.lock(Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)),Constants.LOCK ,Constants.FIVE_MINUTE);
			logger.info("充值加锁操作key = {},结果 = {}",Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)),lock);
			if(lock){
				jsonResponse.setRetDesc("VIP购买提交订单生成失败请稍后再试");
				return jsonResponse;
			}
			JSONObject json = check(dto,user,online);
			if(json.getIntValue("code") == 2000){
				logger.error("提交充值订单的校验内容的失败ret = "+json.toString());
				jsonResponse.setRetDesc(json.getString("msg"));
				return jsonResponse;
			}
			OrderInfo info = json.getObject("order", OrderInfo.class);
			Date now = new Date();
			//校验通过开始处理扣费
			info.setCreateTime(now);
			Integer payType = dto.getPayType();
			BeanUtils.copyProperties(info, ret);
			// TODO 充值订单的设置内容(未完成待续)
			String sign = null;
			if(payType == Constants.MONEY_PAY){//等于余额抵扣的 那么余额和点卡当时更新完毕生效
				//余额抵扣只能购买点卡
				info.setStatus(Constants.ORDER_STATUS_WC);
				info.setPayStatus(Constants.ORDER_PAYSTATUS_YFF);
				info.setPayTime(now);
				info.setIsBill(Constants.ORDER_ISBILL_YCZ);
				orderInfoMapper.insert(info);

				//更新用户的余额内容
				BigDecimal totalPay = dto.getTotalPay();
				BigDecimal money = user.getMoney();
				BigDecimal cardMoney = user.getCardMoney();
				user.setMoney(money.subtract(totalPay));
				user.setCardMoney(cardMoney.add(totalPay));
				userMapper.updateUser(user);
				//余额 流水
				String desc ="余额充值点卡";
				saveUserRecord(uid, Constants.RECORD_PAY, Constants.RECORD_MONEY, totalPay, money, info.getOrderId(), desc, now);
				desc = "点卡充值";
				saveUserRecord(uid, Constants.RECORD_INCOME, Constants.RECORD_CARD_MONEY, totalPay, cardMoney, info.getOrderId(), desc, now);
				ret.setFlag(1);
				//充值点卡 这个订单就算完事了;
			}else if(payType == Constants.ALIPAY || payType == Constants.WEIXIN_PAY){
				if(payType == Constants.ALIPAY){
					sign = PayUtil.setRechargeSign(info,ret);
				}else if(payType == Constants.WEIXIN_PAY){
					
				}
				//微信支付的sign
				if(StringUtils.isBlank(sign)){
					jsonResponse.setRetDesc("获取APP支付验签失败");
					return jsonResponse;
				}
				//保存订单内容
				info.setStatus(Constants.ORDER_STATUS_WWC);
				info.setPayStatus(Constants.ORDER_PAYSTATUS_WFK);
				info.setIsBill(Constants.ORDER_ISBILL_WCZ);
				orderInfoMapper.insert(info);
				ret.setPaySign(sign);
				ret.setFlag(2);
			}
		} catch (Exception e) {
			jsonResponse.setRetDesc("充值订单异常");
			logger.error("充值订单异常"+e.getMessage());
			throw new BusinessException("充值订单异常"+e.getMessage());
		}finally {
			//释放加锁内容;
			cacheService.releaseLock(Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)));
			// 清除用户锁和商品锁
			cacheService.releaseLock(Constants.LOCK_USER + uid);
		}
		jsonResponse.success(ret);
		return jsonResponse;
	}

	

	private JSONObject check(RechargeSubmitDTO dto, User user, Map<String, String> online) {
		JSONObject json = new JSONObject();
		json.put("code",2000);
		Integer orderType = dto.getOrderType();
		Integer payType = dto.getPayType();
		BigDecimal totalPay = dto.getTotalPay();
		BigDecimal totalMoney = dto.getTotalMoney();
		if(null == orderType){
			json.put("msg","充值类型不能为空");
			return json;
		}
		if(null == payType){
			json.put("msg","付款方式不能为空");
			return json;
		}
		if(null == totalPay){
			json.put("msg","付款总额不能为空");
			return json;
		}
		if(null == totalMoney){
			json.put("msg","当前充值金额不为空");
			return json;
		}
		OrderInfo info = new OrderInfo();
		if(orderType == Constants.PAY_VIP){
			if(payType != Constants.ALIPAY && payType != Constants.WEIXIN_PAY ){
				json.put("msg","VIP只能支付宝/微信支付");
				return json;
			}
			Integer isVip = user.getIsVip();
			if(null != isVip && isVip.intValue()==Constants.VIP_USER){
				json.put("msg","您已经是会员请勿重复购买");
				return json;
			}
			if(totalMoney.compareTo(totalPay)!=0){
				json.put("msg","VIP购买的总付不等于总金额");
				return json;
			}
			if(StringUtils.isBlank(online.get(Constants.VIP_PRICE))){
				json.put("msg","获取会员价格请您联系管理员稍后再试");
				return json;
			}
			BigDecimal vipPrice = new BigDecimal(online.get(Constants.VIP_PRICE).toString());
			if(totalPay.compareTo(vipPrice) != 0){
				json.put("msg","VIP价格和应付金额不一致请重新下单");
				return json;
			}
			String toMoney = online.get(Constants.VIP_TO_USER_MONEY);
			if(StringUtils.isBlank(toMoney)){
				json.put("msg","购买vip送的余额数值异常,请联系管理员");
				return json;
			}
			info.setProductDetail("VIP升级");
			info.setDescription(totalPay+"元(充到余额"+toMoney+"元)");
			info.setDiscountMoney(new BigDecimal(toMoney));
		}else if(orderType == Constants.PAY_CARD){
//			if(totalMoney.intValue() %100 != 0 ){
//				json.put("msg","点卡充值金额必须是100的整倍数");
//				return json;
//			}
			if(totalMoney.compareTo(ZERO)<0 ){
				json.put("msg","点卡充值金额必须大于零");
				return json;
			}
			if(totalMoney.compareTo(totalPay)!=0){
				json.put("msg","点卡充值金额和点卡总价不一致");
				return json;
			}
			if(payType != Constants.MONEY_PAY && payType != Constants.ALIPAY && payType != Constants.WEIXIN_PAY ){
				json.put("msg","点卡只支持支付宝/微信/余额购买");
				return json;
			}
			BigDecimal money = user.getMoney() != null ?user.getMoney() :ZERO;
			if(payType == Constants.MONEY_PAY && money.compareTo(totalPay) < 0){
				json.put("msg","用户余额不足");
				return json;
			}
			info.setProductDetail("点卡购买");
			info.setDescription("用户购买"+totalPay+"元的点卡");
		}else if(orderType == Constants.PAY_PHONE){//话费充值
			if(payType != Constants.ALIPAY && payType != Constants.WEIXIN_PAY ){
				json.put("msg","话充值只能支付宝/微信支付");
				return json;
			}
//			if(totalMoney.intValue() %50 != 0 ){
//				json.put("msg","当前充值金额必须是50的整倍数");
//				return json;
//			}
			if(totalMoney.compareTo(ZERO)<0 ){
				json.put("msg","当前充值金额必须大于零");
				return json;
			}
			String phone = user.getPhone();
			JSONObject checkRet = PhoneUtils.checkPhoneNum(phone, totalMoney.intValue());
			if(null == checkRet){
				json.put("msg","手机号校验失败,当前手机号充值异常");
				return json;
			}
			
			if(checkRet.getIntValue("error_code")!= 0){
				json.put("msg",checkRet.getString("reason"));
				return json;
			}
			//非会员不能充值话费
			//标识会员折扣充值
			Integer isVip = user.getIsVip();
			if(isVip == Constants.GENERAL_UER){
				json.put("msg","您不是会员不享受话费充值优惠");
				return json;
			}
			String vipDiscount = online.get(Constants.VIP_PHONE_DISCOUNT);
			if (StringUtils.isBlank(vipDiscount)) {
				json.put("msg", "获取vip充值话费折扣异常请您联系管理员稍后再试");
				return json;
			}
			BigDecimal dicountMoney = totalMoney.multiply(new BigDecimal(vipDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
			if(dicountMoney.compareTo(totalPay) != 0){ 
				json.put("msg","VIP充值话费应付金额不等于折扣后价格");
				return json;
			}
			//查询当月vip用户已经充值了多少钱的
			String key = getKey(user.getUid());
			String accumulate = cacheService.getString(key);//已经充值的额度incr自增内容
			BigDecimal accumulatePrice = StringUtils.isNotBlank(accumulate)? new BigDecimal(accumulate):ZERO;
			String phoneRecharge = online.get(Constants.VIP_PHONE_RECHARGE);
			if (StringUtils.isBlank(phoneRecharge)) {
				json.put("msg", "获取vip充值话费每月优惠额度数据折扣异常请您联系管理员稍后再试");
				return json;
			}
			if((accumulatePrice.add(totalMoney)).compareTo(new BigDecimal(phoneRecharge)) > 0){
				json.put("msg", "您当月VIP已经优惠充值了"+accumulate+"元,现在充值"+totalMoney+"元,已超出优惠额度");
				return json;
			}
			info.setDiscount(Float.valueOf(vipDiscount)/100);
			info.setDiscountDetail("会员话费充值折扣优惠内容");
			info.setDiscountMoney(totalPay);
			info.setProductDetail("手机话费充值");
			info.setDescription(user.getPhone());
		}else{
			json.put("msg","当前充值类型不匹配,充值失败");
			return json;
		}
		info.setOrderId(Utils.getOrderId());
		info.setOrderType(orderType);
		info.setPayType(payType);
		info.setUid(user.getUid());
		info.setTotalMoney(totalMoney);;
		info.setTotalPay(totalPay);
		if(payType == Constants.MONEY_PAY){
			info.setCornMoney(totalPay);
		}else{
			info.setRmb(totalPay);
		}
		json.put("code",1000);
		json.put("order",info);
		return json;
	}
	
	/***
	 * 状态TRADE_SUCCESS的通知触发条件是商户签约的产品支持退款功能的前提下，买家付款成功；
	 * 交易状态TRADE_FINISHED的通知触发条件是商户签约的产品不支持退款功能的前提下，买家付款成功；
	 * 或者，商户签约的产品支持退款功能的前提下，交易已经成功并且已经超过可退款期限。
	 */
	//异步回调
	@Override
	public JsonResponse<Boolean> payNotify(String payType, HttpServletRequest request) {
		JsonResponse<Boolean>jsonResponse = new JsonResponse<>();
		jsonResponse.setRspBody(false);
		OrderInfo orderInfo = null;
		String orderId = null;
		String trade_no = null;
		String key = null;
		String total_amount = null;
		long amount = 0;
		
		Date now = new Date();
		if(payType.equals("alipay")){
			if (!"TRADE_SUCCESS".equals(request.getParameter("trade_status"))) {
				jsonResponse.setRetDesc("异步回调接口验签失败--->付款状态trade_statu="+request.getParameter("trade_status"));
				return jsonResponse;
			}
			orderId = request.getParameter("out_trade_no");
			if(StringUtils.isBlank(orderId)){
				jsonResponse.setRetDesc("订单号为空");
				return jsonResponse;
			}
			try {
				//标识支付宝的验签
				Map<String,String> param = getParam(request);
				logger.info("订支付宝验签获取的响应参数内容param={}" + JSON.toJSONString(param)); 
				if(null == param){
					jsonResponse.setRetDesc("异步回调接口验签失败获取响应参数为空");
					return jsonResponse;
				}
				boolean signVerified = AlipaySignature.rsaCheckV1(param, Constants.ALI_PUBLIC_KEY,Constants.CHARSET_UTF8,Constants.SIGN_TYPE_RSA2); // 校验签名是否正确
				if (!signVerified) {
					logger.error("订支付宝验签没有通过signVerified=" + signVerified); 
					jsonResponse.setRetDesc("异步回调接口验签失败signVerified="+signVerified);
					return jsonResponse;
				} 
				// TODO 验签成功后
				logger.info("订单支付宝验签成功signVerified = "+signVerified);
				trade_no = request.getParameter("trade_no");
				total_amount = request.getParameter("total_amount");
				logger.info("订单支付宝验签成功trade_no = {} total_amount={}",trade_no,total_amount);
			} catch (Exception e) {
				logger.error("支付宝回调解签错误orderId={},e={}",orderId,e.getMessage());
				throw new BusinessException("支付宝回调解签错误"+e.getMessage());
			}
		}else{
			//微信的回调内容;
			
		}
		
		try {
			//先查询该订单是否已付款--->如果已付款说明已经更新过了
			OrderInfo info = new OrderInfo();
			info.setOrderId(orderId);
			orderInfo = orderInfoMapper.getPayOrder(info);
			logger.info("异步回调的订单的内容信息order={}",JSON.toJSON(orderInfo));
			if(null == orderInfo){
				jsonResponse.setRetDesc("未查询到当前订单信息orderId="+orderId);
				return jsonResponse;
			}
			if(orderInfo.getPayStatus() != Constants.ORDER_PAYSTATUS_WFK){
				jsonResponse.success(true);
				return jsonResponse;
			}
			if(new BigDecimal("0.88").compareTo(orderInfo.getRmb())!=0){
				//说明前后的钱不一致
				jsonResponse.setRetDesc("付款金额不对orderId="+orderId);
				return jsonResponse;
			}
			//针对该内容加锁处理--->防止多次回调都会执行内容
			boolean lock = cacheService.lock(Constants.LOCK_NOTIFY_ORDER.concat(orderId), Constants.LOCK, Constants.FIVE_MINUTE);
			if(lock){
				jsonResponse.setRetDesc("异步回调加锁失败orderId="+orderId);//可以直接返回成功;
				return jsonResponse;
			}
			Integer orderType = orderInfo.getOrderType();
			String uid = orderInfo.getUid();
			if(orderType == Constants.PAY_PRODUCT){
				//更新订单的状态
				OrderInfo updateOrder = new OrderInfo();
				updateOrder.setUid(uid);
				updateOrder.setOrderId(orderId);
				updateOrder.setStatus(Constants.ORDER_STATUS_WWC);
				updateOrder.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
				updateOrder.setPayTime(now);
				updateOrder.setTradeNo(trade_no);
				orderInfoMapper.update(updateOrder);
				//更新商品的库存/销量
				String productDetail = orderInfo.getProductDetail();
				batchUpdateProduct(productDetail,uid);
				//如果含有余额抵扣的扣除余额-->生成订单已经更新
				//设置账单内容-->定时处理
				//设置分佣内容-->定时/用户确认收款设置
			}else if(orderType == Constants.PAY_PHONE){ //充值订单内容
				OrderInfo updateOrder = new OrderInfo();
				updateOrder.setOrderId(orderId);
				updateOrder.setUid(uid);
				updateOrder.setStatus(Constants.ORDER_STATUS_WWC);
				updateOrder.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
				updateOrder.setPayTime(now);
				updateOrder.setTradeNo(trade_no);
				orderInfoMapper.update(updateOrder);
				
				key = getKey(uid);
				amount = orderInfo.getTotalMoney().longValue();
				logger.info("话费充值订单支付完成进行累计操作amount={},key={}",amount,key);
				cacheService.incr(key, amount, Constants.ONE_MONTH);	
				//订单更新之后全部开始定时器配置内容;
			}else if(orderType == Constants.PAY_CARD){
				OrderInfo updateOrder = new OrderInfo();
				updateOrder.setOrderId(orderId);
				updateOrder.setUid(uid);
				updateOrder.setStatus(Constants.ORDER_STATUS_WC);
				updateOrder.setPayStatus(Constants.ORDER_PAYSTATUS_YFF);
				updateOrder.setPayTime(now);
				updateOrder.setTradeNo(trade_no);
				orderInfoMapper.update(updateOrder);
				
				//更新用户的点卡内容
				BigDecimal totalMoney = orderInfo.getTotalMoney();
				User user = userMapper.getUserByUid(uid);
				BigDecimal cardMoney = user.getCardMoney();
				user.setCardMoney(cardMoney.add(totalMoney));
				userMapper.updateUser(user);
				String desc = "点卡充值";
				saveUserRecord(uid, Constants.RECORD_INCOME, Constants.RECORD_CARD_MONEY, totalMoney, cardMoney, info.getOrderId(), desc, now);
			}else if(orderType == Constants.PAY_VIP){
				OrderInfo updateOrder = new OrderInfo();
				updateOrder.setOrderId(orderId);
				updateOrder.setUid(uid);
				updateOrder.setStatus(Constants.ORDER_STATUS_WC);
				updateOrder.setPayStatus(Constants.ORDER_PAYSTATUS_YFK);
				updateOrder.setPayTime(now);
				updateOrder.setTradeNo(trade_no);
				orderInfoMapper.update(updateOrder);
				//然后查看vip升级是否还有转成余额的部分
				BigDecimal discountMoney = orderInfo.getDiscountMoney();
				User user = userMapper.getUserByUid(uid);
				if(null != discountMoney && discountMoney.compareTo(ZERO) !=0){
					//标识含有转换的余额内容
					BigDecimal money = user.getMoney();
					user.setMoney(money.add(discountMoney));
					saveUserRecord(uid, Constants.RECORD_INCOME, Constants.RECORD_MONEY, discountMoney, money, info.getOrderId(), "VIP升级转成余额", now);
				}
				user.setIsVip(Constants.VIP_USER);
				userMapper.updateUser(user);
			}
			jsonResponse.success(true);
		} catch (Exception e) {
			if(StringUtils.isNotBlank(key)){
				cacheService.decr(key,amount);	
			}
			logger.error("支付回调接口异常订单orderId={},e={}",orderId,e.getMessage());
			throw new BusinessException("支付回调接口异常");
		}finally {
			cacheService.releaseLock(Constants.LOCK_NOTIFY_ORDER.concat(orderId));
		}
		return jsonResponse;
	}


	private void batchUpdateProduct(String productDetail,String uid) {
		if(StringUtils.isNotBlank(productDetail)){
			List<PayProductDetailDTO> productList = JSONArray.parseArray(productDetail, PayProductDetailDTO.class);
			if(CollectionUtils.isNotEmpty(productList)){
				for (PayProductDetailDTO dto : productList) {
					Long productId = dto.getProductId();
					Integer amount = dto.getAmount();
					JSONObject json = getProductByProductIdAndLock(productId);
					if(json.getIntValue("code")==1000){
						logger.error("异步回调更新商品失败内容商品productId="+productId);
						throw new BusinessException("更新商品状态异常product = "+productId);
					}
					try {
						Product findProduct = json.getObject("product",Product.class);
						findProduct.setSalesAmount(findProduct.getSalesAmount()+amount);
						findProduct.setStockAmount(findProduct.getStockAmount()-amount);
						productMapper.updateProduct(findProduct);
						cartMapper.delete(productId, uid);//购物车暂时不删除商品
					} catch (Exception e) {
						logger.error("异步回调更新商品失败内容");
						throw new BusinessException(e.getMessage());
					}finally {
						cacheService.releaseLock(Constants.LOCK_PRODUCT+productId);
					}
					
				}
			}
			
		}
		
	}


	private Map<String, String> getParam(HttpServletRequest request) throws UnsupportedEncodingException {
		 Map<String,String> params = new HashMap<String,String>();
		 Map requestParams = request.getParameterMap();
		 for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			 String name = (String) iter.next();
			 String[] values = (String[]) requestParams.get(name);
			 String valueStr = "";
			 for (int i = 0; i < values.length; i++) {
				 valueStr = (i == values.length - 1) ? valueStr + values[i]:valueStr + values[i] + ",";
			 }
			 //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化//
//			 valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			 params.put(name, valueStr);
		}
		return params;
	}
	
	private JSONObject checkProductOrderInfo(OrderInfo orderInfo, SubmitOrderDTO dto, User user, Map<String, String> online,List<Product>listProduct) {
		JSONObject json = new JSONObject();
		json.put("code",2000);
		if(null == dto.getPayType()){
			json.put("msg","付款方式不能为空");
			return json;
		}
		if(StringUtils.isAnyBlank(dto.getUserName(),dto.getAddress(),dto.getPhone())){
			json.put("msg","收件信息不能为空");
			return json;
		}
		if(null == dto.getTotalPay()){
			json.put("msg","该订单的付款的总额不能为空");
			return json;
		}
		if(CollectionUtils.isEmpty(dto.getList())){
			json.put("msg","该订单的商品详情不能为空");
			return json;
		}
		String postage = online.get(Constants.POSTAGE);
		if(StringUtils.isBlank(postage)){
			json.put("msg","获取运费价格异常请您联系管理员稍后再试");
			return json;
		}
		String productDetail = orderInfo.getProductDetail();
		List<PayProductDetailDTO> listDb = JSONArray.parseArray(productDetail, PayProductDetailDTO.class);
		if(dto.getList().size() != listDb.size()){
			json.put("msg","该订单的商品种类和前置订单不一致");
			return json;
		}
		BigDecimal total = new BigDecimal("0.00");
		BigDecimal totalCost = new BigDecimal("0.00");
		BigDecimal payTotal  = new BigDecimal("0.00");
		BigDecimal discount  = new BigDecimal("1.00");
		BigDecimal discountMoney  = new BigDecimal("0.00");
		String discountDetail = "";
		Product updateProduct = null;
		for (PayProductDetailDTO dto1 : listDb) {
			boolean flag = true;
			for (PayProductDetailDTO dto2 : dto.getList()) {
				if(dto1.getProductId().equals(dto2.getProductId())){
					if(dto1.equals(dto2)){
						flag = false;
						break;
					}else{
						json.put("msg","商品id为"+dto1.getProductId()+"的信息前后不一致订单支付失败");
						return json;
					}
				}
			}
			if(flag){
				json.put("msg","商品id为"+dto1.getProductId()+"的信息提交的订单表里面没有该项内容");
				return json;
			}
			Long productId = dto1.getProductId();
			JSONObject ret = getProductByProductIdAndLock(productId);
			if(ret.getIntValue("code")==1000){
				json.put("msg",ret.getString("msg"));
				return json;
			}
			Product findProduct = ret.getObject("product",Product.class);
			if(dto1.getProductPrice().compareTo(findProduct.getProductPrice())!=0){
				json.put("msg","商品id为"+dto1.getProductId()+"的商品价格发生变化请重新下单");
				return json;
			}
			if(findProduct.getStockAmount().compareTo(dto1.getAmount())< 0){
				json.put("msg","商品id为"+dto1.getProductId()+"的商品不存在/库存不足");
				return json;
			}
			
			
			//设置更新数量的商品列表
			updateProduct = new Product();
			updateProduct.setProductId(productId);
			updateProduct.setSalesAmount(findProduct.getSalesAmount()+dto1.getAmount());
			updateProduct.setStockAmount(findProduct.getStockAmount()-dto1.getAmount());
			if(updateProduct.getStockAmount().intValue() == 0){
//				updateProduct.setStatus(Constants.PRODUCT_STATUS_WSJ);
			}
			listProduct.add(updateProduct);
			
			//已完成订单设置进价/成本内容;
			dto1.setCostPrice(findProduct.getCostPrice());
			BigDecimal cost = dto1.getCostPrice().multiply(new BigDecimal(dto1.getAmount()));
			dto1.setCost(cost.doubleValue());
			totalCost = totalCost.add(cost);
			total = total.add(new BigDecimal(dto1.getTotal().toString()));
		}
		
		Integer isVip = user.getIsVip();
		if(null != isVip && isVip == Constants.VIP_USER){//如果是vip用户
			String vipDiscount = online.get(Constants.VIP_DISCOUNT);
			if(StringUtils.isBlank(vipDiscount)){
				json.put("msg","获取会员折扣异常请您联系管理员稍后再试");
				return json;
			}
			discountDetail = "vip会员"+vipDiscount+"折    ";
			discount = discount.multiply(new BigDecimal(vipDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
			payTotal = total.multiply(new BigDecimal(vipDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
		}else{
			payTotal = total;
		}
		
		String cardDiscount = online.get(Constants.CARD_DISCOUNT);
		if(dto.getPayType() == Constants.CARD_PAY){//标识点卡支付
			if(StringUtils.isBlank(cardDiscount)){
				json.put("msg","获取点卡折扣异常请您联系管理员稍后再试");
				return json;
			}
			discountDetail = discountDetail+"点卡支付"+cardDiscount+"折";
			discount = discount.multiply(new BigDecimal(cardDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
			payTotal = payTotal.multiply(new BigDecimal(cardDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
		}
		if(payTotal.compareTo(ZERO) == 0){
			json.put("msg","该订单商品的总的应付款为0");
			return json;
		}
		discountMoney = payTotal;
		BigDecimal postageMoney = new BigDecimal(postage);
		if(postageMoney.compareTo(dto.getPostage()) != 0){
			json.put("msg","该订单的运费已经,请重新下单");
			return json;
		}
		payTotal = payTotal.add(postageMoney);//总的应付金额+运费
		BigDecimal totalPay = dto.getTotalPay();//总的付款金额
		if(totalPay.compareTo(payTotal) !=0){
			json.put("msg","付款总额和订单应付金额不一致付款失败");
			return json;
		}
		BigDecimal cornMoney = dto.getCornMoney() != null ?dto.getCornMoney():ZERO;
		BigDecimal rmb = dto.getRmb() != null ?dto.getRmb():ZERO;
		if(dto.getPayType() == Constants.CARD_PAY){//点卡付
			if(rmb.compareTo(totalPay) != 0){
				json.put("msg","支付的点卡金额不等于应付金额");
				return json;
			}
			BigDecimal cardMoney = user.getCardMoney() != null ? user.getCardMoney():ZERO;
			if(cardMoney.compareTo(rmb) < 0){
				json.put("msg","用户的点卡余额不足");
				return json;
			}
		}else if(dto.getPayType() == Constants.ALIPAY){
			if(totalPay.compareTo(cornMoney.add(rmb)) != 0){
				json.put("msg","支付的金额和余额之和不等于应付金额");
				return json;
			}
			BigDecimal money = user.getMoney() != null ? user.getMoney():ZERO;
			if(money.compareTo(cornMoney)< 0){
				json.put("msg","用户的余额不足");
				return json;
			}
		}
		orderInfo.setProductDetail(JSONObject.toJSONString(listDb));
		orderInfo.setTotalCost(totalCost);
		orderInfo.setTotalMoney(total);
		orderInfo.setDiscount(discount.floatValue());
		orderInfo.setDiscountDetail(discountDetail);
		orderInfo.setDiscountMoney(discountMoney);
		orderInfo.setTotalPay(totalPay);
		orderInfo.setRmb(rmb);
		orderInfo.setCornMoney(cornMoney);
		orderInfo.setPostage(postageMoney);
		orderInfo.setUserName(dto.getUserName());
		orderInfo.setAddress(dto.getAddress());
		orderInfo.setPostCode(dto.getPostCode());
		orderInfo.setPhone(dto.getPhone());
		orderInfo.setDescription("用户购买的商品内容订单id="+orderInfo.getOrderId());
		json.put("code",1000);
		json.put("msg","订单校验通过");
		return json;
	}
	
	private JSONObject checkProduct(List<CartDTO> list, PreOrderResultDTO ret) {
		JSONObject json = new JSONObject();
		json.put("code",2000);
		int num =1;
		PayProductDetailDTO detail = null;
		List<PayProductDetailDTO>dtos = new ArrayList<>();
		for (CartDTO dto : list) {
			if(null == dto){
				json.put("msg", "第"+num+"个商品参数不为空");
				break;
			}
			Long productId = dto.getProductId();
			Integer amount = dto.getAmount();
			if(null == productId){
				json.put("msg", "第"+num+"个商品id不为空");
				break;
			}
			if(null == amount){
				json.put("msg", "第"+num+"个商品amount不为空");
				break;
			}
			if(amount.intValue() <= 0 ){
				json.put("msg", "第"+num+"个商品amount必须大于0");
				break;
			}
			Product productDb = productMapper.findProduct(productId);
			if(null == productDb){
				json.put("msg", "第"+num+"个商品状态异常");
				break;
			}
			if(productDb.getStockAmount().intValue() < amount.intValue()){
				json.put("msg", "第"+num+"个商品库存不足");
				break;
			}
			detail = new PayProductDetailDTO();
			BeanUtils.copyProperties(productDb, detail);
			detail.setAmount(amount);
			detail.setTotal(productDb.getProductPrice().multiply(new BigDecimal(amount)).doubleValue());
			dtos.add(detail);
//			cartMapper.delete(productId, ret.getUid());//购物车暂时不删除商品
		}
		ret.setList(dtos);
		if(list.size() == ret.getList().size()){
			json.put("code",1000);
		}
		return json;
	}
	
	/**
	 * 批量更新商品的库存和销售数量
	 * @Description
	 * @author khy
	 * @date  2018年10月17日下午6:47:28
	 * @param listProduct
	 */
	private void batchUpdateProduct(List<Product> listProduct) {
		if(CollectionUtils.isNotEmpty(listProduct)){
			for (Product product : listProduct) {
				productMapper.updateProduct(product);	
			}
		}
	}
	
	
	public static void main(String[] args) {
		String discount="88";
		BigDecimal b1 = new BigDecimal("1");
		
		long longValue = b1.longValue();
		System.out.println(longValue);
		BigDecimal b2 = new BigDecimal("1.19");
		BigDecimal b3 = new BigDecimal("1.19");
		System.out.println(b1.add(b2).add(b3).multiply(new BigDecimal(discount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP));
		System.out.println(b1.multiply(new BigDecimal(discount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP));
		Integer payType=3;
		if(payType != Constants.ALIPAY){
			System.out.println("不想等");
		}
		if(false || true){
			System.out.println(11111111);
		}
	}
}
