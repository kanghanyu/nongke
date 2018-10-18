package com.khy.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import com.khy.mapper.dto.SubmitOrderDTO;
import com.khy.mapper.dto.SubmitOrderResultDTO;
import com.khy.service.PayService;
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
		String cartDiscount = online.get(Constants.CARD_DISCOUNT);
		if(StringUtils.isBlank(cartDiscount)){
			jsonResponse.setRetDesc("点卡折扣异常请您联系管理员稍后再试");
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
				infoDb.setProducDetail("购买vip内容");
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
			BigDecimal totalCardMoney =total.multiply(new BigDecimal(cartDiscount)).divide(ONE_HUNDRED,2, BigDecimal.ROUND_HALF_UP);
			ret.setTotalCardMoney(totalCardMoney);
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
	public JsonResponse<SubmitOrderResultDTO> payOnline(SubmitOrderDTO dto) {
		logger.info("用户在线支付订单的请求参数SubmitOrderDTO = "+JSON.toJSONString(dto));
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
		Map<String, String> online = getOnline();
		if(null ==online || online.size() == 0){
			jsonResponse.setRetDesc("获取在线参数异常,请联系管理员");
			return jsonResponse;
		}
		JSONObject json = null;
		List<Product>listProduct = new ArrayList<>();
		if(orderInfo.getOrderType().intValue() == Constants.PAY_PRODUCT){
			json = checkProductOrderInfo(orderInfo,dto,userDb,online,listProduct);
		}else if (orderInfo.getOrderType().intValue() == Constants.PAY_VIP){
			if(dto.getPayType() != Constants.ALIPAY){
				jsonResponse.setRetDesc("VIP只能通过支付宝购买");
				return jsonResponse;
			}
			json = checkVipOrderInfo(orderInfo,dto,userDb,online);
		}
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
		orderInfo.setPayType(dto.getPayType());
		SubmitOrderResultDTO ret = new SubmitOrderResultDTO();
		//如果支付方式是点卡
		Date now = new Date();
		if(dto.getPayType() == Constants.CARD_PAY){
			try {
				//通过点卡支付  所有的业务内容
				orderInfo.setPayTime(now);
				orderInfo.setStatus(3);
				//包含更新订单状态内容
				orderInfoMapper.update(orderInfo);
				//更新用户的账户信息(点卡)
				BigDecimal cardMoney = userDb.getCardMoney() != null ? userDb.getCardMoney():ZERO;
				userDb.setCardMoney(cardMoney.subtract(dto.getTotalPay()));
				userMapper.updateUser(userDb);
				//更新用户的账务流水
				String descr="购买商品花费"+dto.getTotalPay()+":元";
				saveUserRecord(uid,Constants.RECORD_PAY,Constants.RECORD_CARD_MONEY,dto.getTotalPay(),cardMoney,orderId,descr,now);
				//更新商品的数量内容
				batchUpdateProduct(listProduct);
			} catch (Exception e) {
				logger.error("用户在线支付订单--->点卡支付异常"+e.getMessage());
				throw new BusinessException("用户在线支付订单--->点卡支付异常"+e.getMessage());
			}finally {
				//清除用户锁和商品锁
				cacheService.releaseLock(Constants.LOCK_USER+uid);
				
			}
		}else{
			//需要在线支付的拼装验签内容;
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


	private JSONObject checkVipOrderInfo(OrderInfo orderInfo, SubmitOrderDTO dto, User user,Map<String, String> online) {
		JSONObject json = new JSONObject();
		json.put("code",2000);
		if(null == orderInfo || null == dto){
			json.put("msg","参数不能为空");
			return json;
		}
		if(null == dto.getPayType()){
			json.put("msg","付款方式不能为空");
			return json;
		}
		if(null == dto.getTotalPay()){
			json.put("msg","该订单的付款的总额不能为空");
			return json;
		}
		Integer isVip = user.getIsVip();
		if(null != isVip && isVip == Constants.VIP_USER){//如果是vip用户
			json.put("msg","你已经是VIP用户");
			return json;
		}
		String vipPrice = online.get(Constants.VIP_PRICE);
		if(StringUtils.isBlank(vipPrice)){
			json.put("msg","获取VIP价格异常请您联系管理员稍后再试");
			return json;
		}
		if(new BigDecimal(vipPrice).compareTo(orderInfo.getTotalMoney()) != 0){
			json.put("msg","VIP价格发生变动请重新添加订单");
			return json;
		}
		BigDecimal totalPay = dto.getTotalPay();
		if(new BigDecimal(vipPrice).compareTo(totalPay) != 0){
			json.put("msg","VIP订单价格和应付款金额不一致");
			return json;
		}
		if(new BigDecimal(vipPrice).compareTo(ZERO) == 0){
			json.put("msg","VIP价格异常,请联系管理员");
			return json;
		}
		BigDecimal rmb = dto.getRmb() !=null ? dto.getRmb() : ZERO;
		if(totalPay.compareTo(rmb) != 0){
			json.put("msg","支付的金额不等于应付金额");
			return json;
		}
		orderInfo.setTotalMoney(totalPay);
		orderInfo.setTotalPay(totalPay);
		
		
		json.put("code",1000);
		json.put("msg","订单校验通过");
		return json;
	}


	private JSONObject checkProductOrderInfo(OrderInfo orderInfo, SubmitOrderDTO dto, User user, Map<String, String> online,List<Product>listProduct) {
		JSONObject json = new JSONObject();
		json.put("code",2000);
		if(null == orderInfo || null == dto){
			json.put("msg","参数不能为空");
			return json;
		}
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
		if(dto.getList().size()!=listDb.size()){
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
		orderInfo.setTotalMoney(totalPay);
		orderInfo.setRmb(rmb);
		orderInfo.setCornMoney(cornMoney);
		orderInfo.setPostage(postageMoney);
		orderInfo.setUserName(dto.getUserName());
		orderInfo.setAddress(dto.getAddress());
		orderInfo.setPostCode(dto.getPostCode());
		orderInfo.setPhone(dto.getPhone());
		orderInfo.setDescription("用户购买的商品内容");
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
			if(productDb.getStockAmount().intValue() < amount){
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
	public static void main(String[] args) {
		BigDecimal b1 = new BigDecimal("8.8");
		BigDecimal b2 = new BigDecimal("8.66");
		BigDecimal multiply = b1.multiply(b2);
		System.out.println(multiply.doubleValue());
		System.out.println(multiply.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		BigDecimal divide = b1.multiply(b2).multiply(new BigDecimal(100)).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_UP);
		System.out.println(divide.doubleValue());
		
		
	}
}
