package com.khy.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.khy.utils.SessionHolder;
import com.khy.utils.Utils;
@Transactional
@Service
public class PayServiceImpl extends BaseService implements PayService {
	public final static Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);
	private final static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	private final static BigDecimal ZERO = new BigDecimal(0);
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
			BigDecimal b1 = new BigDecimal(totalMoney);
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
				totalCardMoney = ret.getDiscountMoney().multiply(new BigDecimal(cartDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
			}else{
				totalCardMoney =ret.getTotalMoney().multiply(new BigDecimal(cartDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
			}
			ret.setTotalCardMoney(totalCardMoney);
			Date now = new Date();
			ret.setCreateTime(now);
			ret.setProducDetail(JSONObject.toJSONString(ret.getList()));
			OrderInfo info = new OrderInfo();
			BeanUtils.copyProperties(ret, info);
			info.setStatus(1);
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
		String orderId = dto.getOrderId();
		OrderInfo info  = new OrderInfo();
		info.setUid(uid);
		info.setOrderId(orderId);
		info.setStatus(1);
		OrderInfo orderInfo = orderInfoMapper.getPayOrder(info);
		logger.info("用户在线支付订单的获取数据库订单信息orderInfoDb={},查询的参数内容info={}",JSON.toJSONString(orderInfo),JSON.toJSONString(info));
		if(null == orderInfo){
			jsonResponse.setRetDesc("当前前置订单状态异常");
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
		List<Product>listProduct = new ArrayList<>();
		JSONObject json = checkProductOrderInfo(orderInfo,dto,userDb,online,listProduct);
		logger.info("在线支付校验订单内容结果json={}",json.toString());
		
		if(json.getIntValue("code") == 2000){
			logger.error("在线支付校验订单内容结果失败json={}",json.toString());
			jsonResponse.setRetDesc(json.getString("msg"));
			return jsonResponse;
		}
		//为了防止重复付款 先对用户的付款订单进行加锁
		String key = Constants.USER_ONLINE_PAY_LOCK.concat(uid).concat(orderId);
		boolean lock = cacheService.lock(key,Constants.LOCK ,Constants.FIVE_MINUTE);
		logger.info("用户在线支付订单的加锁操作key = {},结果 = {}",key,lock);
		if(lock){
			jsonResponse.setRetDesc("订单支付中,请勿重复支付");
			return jsonResponse;
		}
		Integer payType = dto.getPayType();
		orderInfo.setPayType(payType);
		SubmitOrderResultDTO ret = new SubmitOrderResultDTO();
		BeanUtils.copyProperties(orderInfo, ret);
		Date now = new Date();
		List<Long> productIds = null;
		if(CollectionUtils.isNotEmpty(listProduct)){
			productIds = listProduct.stream().map(Product::getProductId).collect(Collectors.toList());
		}
		try {
			if (payType == Constants.CARD_PAY) {
				// 包含更新订单状态内容
				orderInfo.setPayTime(now);
				orderInfo.setStatus(3);
				orderInfoMapper.update(orderInfo);

				// 更新用户的账户信息(点卡)
				BigDecimal cardMoney = userDb.getCardMoney() != null ? userDb.getCardMoney() : ZERO;
				userDb.setCardMoney(cardMoney.subtract(dto.getTotalPay()));
				userMapper.updateUser(userDb);

				// 更新用户的账务流水
				String descr = "购买商品花费点卡" + dto.getTotalPay() + ":元";
				saveUserRecord(uid, Constants.RECORD_PAY, Constants.RECORD_CARD_MONEY, dto.getTotalPay(), cardMoney,
						orderId, descr, now);
				// 更新商品的数量内容
				batchUpdateProduct(listProduct);
			} else if (payType == Constants.ALIPAY) {
				if (dto.getRmb().compareTo(ZERO) == 0 && dto.getCornMoney().compareTo(dto.getTotalPay()) == 0) {
					// 说明全部是余额抵扣的和点卡购买的路径一样
					// 包含更新订单状态内容
					orderInfo.setPayTime(now);
					orderInfo.setStatus(3);
					orderInfoMapper.update(orderInfo);

					// 更新用户的账户信息(余额)
					BigDecimal money = userDb.getMoney() != userDb.getMoney() ? userDb.getMoney() : ZERO;
					userDb.setMoney(money.subtract(dto.getCornMoney()));
					userMapper.updateUser(userDb);

					// 更新用户的账务流水
					String descr = "购买商品花费余额" + dto.getTotalPay() + ":元";
					saveUserRecord(uid, Constants.RECORD_PAY, Constants.RECORD_MONEY, dto.getCornMoney(), money,
							orderId, descr, now);
					// 更新商品的数量内容
					batchUpdateProduct(listProduct);
				} else {
					// 需要在线支付的拼装验签内容;
					// 根据支付方式选择验签的内容;
					logger.info("在线支付拼装网关验签内容的接口orderInfo{}", JSON.toJSON(orderInfo));
					if (payType == Constants.ALIPAY) {
						PayUtil.setProductSign(ret, dto);
					} else if (payType == Constants.WEIXIN_PAY) {
						// 这个是微信的签名内容

					}
					
					orderInfo.setPayTime(now);
					orderInfo.setStatus(2);// 标识付款中-->如果真的付款完成回调中再次更新订单内容
					orderInfoMapper.update(orderInfo);

					// 扣除余额抵扣的部分内容;
					//如果不是回调里面余额抵扣和商品数量不应该去更新使用-->都放到回调里面去支付
					// 更新用户的账户信息(余额)
//					BigDecimal money = userDb.getMoney() != userDb.getMoney() ? userDb.getMoney() : ZERO;
//					userDb.setMoney(money.subtract(dto.getCornMoney()));
//					userMapper.updateUser(userDb);
//
//					// 更新用户的账务流水
//					String descr = "购买商品花费余额" + dto.getTotalPay() + ":元";
//					saveUserRecord(uid, Constants.RECORD_PAY, Constants.RECORD_MONEY, dto.getCornMoney(), money,
//							orderId, descr, now);
					
				}
			}
		} catch (Exception e) {
			logger.error("用户在线支付商品订单--->接口异常" + e.getMessage());
			throw new BusinessException("用户在线支付商品订单--->接口异常" + e.getMessage());
		} finally {
			cacheService.releaseLock(key);
			// 清除用户锁和商品锁
			cacheService.releaseLock(Constants.LOCK_USER + uid);
			// 批量清除商品锁内容
			if (CollectionUtils.isNotEmpty(productIds)) {
				for (Long productId : productIds) {
					cacheService.releaseLock(Constants.LOCK_PRODUCT + productId);
				}
			}
		}
		return null;
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
		String producDetail = orderInfo.getProducDetail();
		List<PayProductDetailDTO> listDb = JSONArray.parseArray(producDetail, PayProductDetailDTO.class);
		if(dto.getList().size() != listDb.size()){
			json.put("msg","该订单的商品种类和前置订单不一致");
			return json;
		}
		BigDecimal total = new BigDecimal("0.00");
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
			}
			Product findProduct = ret.getObject("product",Product.class);
			if(findProduct.getStockAmount().compareTo(dto1.getAmount())< 0){
				json.put("msg","商品id为"+dto1.getProductId()+"的商品不存在/库存不足");
				return json;
			}
			updateProduct = new Product();
			updateProduct.setProductId(productId);
			updateProduct.setSalesAmount(findProduct.getSalesAmount()+dto1.getAmount());
			updateProduct.setStockAmount(findProduct.getStockAmount()-dto1.getAmount());
			if(updateProduct.getStockAmount().intValue() == 0){
				updateProduct.setStatus(2);
			}
			listProduct.add(updateProduct);
			total = total.add(new BigDecimal(dto1.getTotal()));
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
		orderInfo.setDiscount(discount.floatValue());
		orderInfo.setDiscountDetail(discountDetail);
		orderInfo.setDiscountMoney(discountMoney);
		orderInfo.setTotalPay(totalPay);
		orderInfo.setTotalMoney(total);
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
			cartMapper.delete(productId, ret.getUid());
		}
		if(list.size() == ret.getList().size()){
			json.put("code",1000);
		}
		return json;
	}
	
	
	@Override
	public JsonResponse<PreOrderResultDTO> buyVip() {
		JsonResponse<PreOrderResultDTO>jsonResponse = new JsonResponse<>();
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
		if(null != isVip && isVip.intValue()==Constants.VIP_USER){
			jsonResponse.setRetDesc("当前已经是vip用户");
			return jsonResponse;
		}
		Map<String, String> online = getOnline();
		logger.info("获取在线参数的map={}",JSON.toJSON(online));
		if(null ==online || online.size() == 0){
			jsonResponse.setRetDesc("获取在线参数异常,请联系管理员");
			return jsonResponse;
		}
		String vipPrice = online.get(Constants.VIP_PRICE);
		if(StringUtils.isBlank(vipPrice)){
			jsonResponse.setRetDesc("获取VIP价格异常请您联系管理员稍后再试");
			return jsonResponse;
		}
		Integer orderType = Constants.PAY_VIP;
		//针对生成前置订单的用户加锁防止生成多个前置订单;
		boolean lock = cacheService.lock(Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)),Constants.LOCK ,Constants.FIVE_MINUTE);
		logger.info("VIP购买提交前置订单的加锁操作key = {},结果 = {}",Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)),lock);
		if(lock){
			jsonResponse.setRetDesc("VIP购买提交订单生成失败请稍后再试");
			return jsonResponse;
		}
		try {
			//先查询当前用户是否之前生成过未付款的vip购买的订单内容;
			OrderInfo info = new OrderInfo();
			info.setUid(uid);
			info.setOrderType(orderType);
			info.setStatus(1);
			OrderInfo infoDb = orderInfoMapper.getPayOrder(info);
			Date now = new Date();
			BigDecimal total = new BigDecimal(vipPrice);
			int flag = 0;
			if(infoDb == null){//如果不存在则新增
				infoDb = new OrderInfo();
				infoDb.setOrderId(Utils.getOrderId());
				infoDb.setOrderType(orderType);
				infoDb.setUid(uid);
				infoDb.setTotalMoney(total);
				infoDb.setStatus(1);
				infoDb.setCreateTime(now);
				infoDb.setProducDetail("购买成为VIP");
				logger.info("VIP购买提交前置订单的插入orderInfo="+JSON.toJSONString(info));
				flag = orderInfoMapper.insert(infoDb);
			}else{//存在则修改原来的
				infoDb.setOrderId(Utils.getOrderId());
				infoDb.setTotalMoney(total);
				infoDb.setStatus(1);
				infoDb.setCreateTime(now);
				infoDb.setProducDetail("购买vip内容");
				logger.info("VIP购买提交前置订单的修改orderInfo="+JSON.toJSONString(info));
				flag = orderInfoMapper.update(infoDb);
			}
			PreOrderResultDTO ret = new PreOrderResultDTO();
			BeanUtils.copyProperties(infoDb, ret);
			jsonResponse.success(ret, "获取购买vip的信息成功");
			logger.info("VIP购买提交前置订单的成功后返回的内容PreOrderResultDTO="+JSON.toJSONString(ret));
		} catch (Exception e) {
			jsonResponse.setRetDesc("vip购买生成前置订单异常");
			e.printStackTrace();
			logger.error("vip购买生成前置订单异常"+e.getMessage());
			throw new BusinessException("vip购买生成前置订单异常"+e.getMessage());
		}finally {
			cacheService.releaseLock(Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)));
		}
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
		user  = userMapper.getUserByUid(uid);
		if(null == user){
			jsonResponse.setRetDesc("当前用户状态异常");
			return jsonResponse;
		}
		Map<String, String> online = getOnline();
		logger.info("获取在线参数的map={}",JSON.toJSON(online));
		if(null ==online || online.size() == 0){
			jsonResponse.setRetDesc("获取在线参数异常,请联系管理员");
			return jsonResponse;
		}
		Integer orderType = dto.getOrderType();
		//针对生成前置订单的用户加锁防止生成多个前置订单;
		boolean lock = cacheService.lock(Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)),Constants.LOCK ,Constants.FIVE_MINUTE);
		logger.info("充值加锁操作key = {},结果 = {}",Constants.USER_CREATE_PRE_ORDER_LOCK.concat(uid).concat(String.valueOf(orderType)),lock);
		if(lock){
			jsonResponse.setRetDesc("VIP购买提交订单生成失败请稍后再试");
			return jsonResponse;
		}
		OrderInfo info = new OrderInfo();
		RechargeResultDTO ret = new RechargeResultDTO();
		JSONObject json = check(dto,user,online,info);
		if(json.getIntValue("code") == 2000){
			logger.error("提交充值订单的校验内容的失败ret = "+json.toString());
			jsonResponse.setRetDesc(json.getString("msg"));
			return jsonResponse;
		}
		Date now = new Date();
		//校验通过开始处理扣费
		info.setCreateTime(now);
		Integer payType = dto.getPayType();
		try {
			BeanUtils.copyProperties(info, ret);
			if(payType == Constants.MONEY_PAY){//等于余额抵扣的 那么余额和点卡当时更新完毕生效
				info.setStatus(3);
				info.setPayTime(now);
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
			}else if(payType == Constants.ALIPAY){
				PayUtil.setRechargeSign(info,ret);
				
			}
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			//释放加锁内容;
			
		}
		
		
		
		return null;
	}

	
	
	

	private JSONObject check(RechargeSubmitDTO dto, User user, Map<String, String> online, OrderInfo info) {
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
		if(orderType == Constants.PAY_VIP){
			if(payType != Constants.ALIPAY || payType != Constants.WEIXIN_PAY ){
				json.put("msg","VIP只能支付宝/微信支付");
				return json;
			}
			Integer isVip = user.getIsVip();
			if(null != isVip && isVip.intValue()==Constants.VIP_USER){
				json.put("msg","您已经是会员请勿重复购买");
				return json;
			}
			if(StringUtils.isBlank(online.get(Constants.VIP_PRICE))){
				json.put("msg","获取会员价格请您联系管理员稍后再试");
				return json;
			}
			BigDecimal vipPrice = new BigDecimal(online.get(Constants.VIP_PRICE));
			String forMoney = online.get(Constants.VIP_PRICE_FORMONEY);
			if(StringUtils.isBlank(forMoney)){
				json.put("msg","购买vip送的余额数值异常,请联系管理员");
				return json;
			}
			if(totalPay.compareTo(vipPrice) != 0){
				json.put("msg","VIP价格和应付金额不一致请重新下单");
				return json;
			}
			info.setProducDetail("用户购买成为VIP用户,花费"+totalPay+":元");
		}else if(orderType == Constants.PAY_CARD){
			if(payType != Constants.MONEY_PAY || payType != Constants.ALIPAY || payType != Constants.WEIXIN_PAY ){
				json.put("msg","点卡只支持支付宝/微信/余额购买");
				return json;
			}
			BigDecimal money = user.getMoney() != null ?user.getMoney() :ZERO;
			if(payType == Constants.MONEY_PAY && money.compareTo(totalPay) < 0){
				json.put("msg","用户余额不足");
				return json;
			}
			info.setProducDetail("用户购买"+totalPay+":元的点卡");
		}else if(orderType == Constants.PAY_PHONE){//话费充值
			if(payType != Constants.ALIPAY || payType != Constants.WEIXIN_PAY ){
				json.put("msg","话充值只能支付宝/微信支付");
				return json;
			}
			if(totalMoney.intValue() %50 != 0 ){
				json.put("msg","当前充值金额必须是50的整倍数");
				return json;
			}
			//充值话费总金额和付款总金额是否相同正常充值
			if(totalMoney.compareTo(totalPay) != 0){
				//标识会员折扣充值
				Integer isVip = user.getIsVip();
				if(isVip == Constants.VIP_USER){
					String vipDiscount = online.get(Constants.VIP_DISCOUNT);
					if (StringUtils.isBlank(vipDiscount)) {
						json.put("msg", "获取vip充值话费折扣异常请您联系管理员稍后再试");
						return json;
					}
					BigDecimal dicountMoney = totalMoney.multiply(new BigDecimal(vipDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
					if(dicountMoney.compareTo(totalPay) == 0){ 
						//查询当月vip用户已经充值了多少钱的
						String key = getKey(user.getUid());
						String accumulate = cacheService.getString(key);
						BigDecimal accumulatePrice = StringUtils.isNotBlank(accumulate)? new BigDecimal(accumulate):ZERO;
						String phoneRecharge = online.get(Constants.VIP_PHONE_RECHARGE);
						if (StringUtils.isBlank(phoneRecharge)) {
							json.put("msg", "获取vip充值话费每月优惠额度数据折扣异常请您联系管理员稍后再试");
							return json;
						}
						if(accumulatePrice.compareTo(new BigDecimal(phoneRecharge)) > 0){
							json.put("msg", "您当月VIP已经优惠充值了"+accumulate+":元,现在充值"+totalMoney+":元,已超出优惠额度");
							return json;
						}
						info.setDiscount(Float.valueOf(vipDiscount)/100);
						info.setDiscountDetail("会员话费充值折扣优惠内容");
						info.setDiscountMoney(dicountMoney);
					}else{
						json.put("msg","VIP充值话费应付金额不等于折扣后价格");
						return json;
					}
				}else{
					json.put("msg","您不是会员不享受话费充值优惠");
					return json;
				}
			}
			info.setProducDetail("用户手机充值话费"+totalMoney+":元");
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
		return json;
	}
	private String getKey(String uid) {
		String key = Constants.USER_PHONE_RECHARGE;
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		key = key.concat(month+"").concat(uid);
		return key;
	}
}
