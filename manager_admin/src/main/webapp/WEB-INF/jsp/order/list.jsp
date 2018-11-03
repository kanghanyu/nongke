<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../admin/common.jsp" />
<script type="text/javascript" src="/js/order.js"></script>
</head>
<body>
	<div>
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>
				订单列表 
			</h1>
			<ol class="breadcrumb">
				<li><a href="#">订单列表 </a></li>
			</ol>
		</section>

		<!-- Main content -->
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
						<div class="box-body">
							<div class="row">
									手机号: <input type="text" maxlength="11"  id="phone">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									订单号: <input type="text" id="orderId">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									开始时间:<input id="startDate" style="width: 100px;height: 26px" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,maxDate:'#F{$dp.$D(\'endDate\');}'})" />&nbsp;&nbsp;
									结束时间:<input id="endDate" style="width: 100px;height: 26px" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,minDate:'#F{$dp.$D(\'startDate\');}'})" />
									订单状态:
									<select id="status">
										<option value="">-请选择-</option>
										<option value="1">未付款</option>
										<option value="2">已付款</option>
										<option value="3">已取消</option>
									</select>
									<button type="button" name="search" onclick="search()" class="btn btn-primary">查询</button>
							</div>
						</div>
						<!-- /.box-header -->
						<div class="box-body"  style="overflow:scroll;">
							<input type="hidden" id="pageNum" value="${page.pageNum}">
							<input type="hidden" id="pageSize" value="${page.pageSize}">
							<input type="hidden" id="pages" value="${page.pages}">
							<table id="table1" class="table table-bordered  table-striped table-hover" style="min-width:4000px;">
								<thead>
									<tr >
										<th>订单号</th>
										<th>订单状态</th>
										<th>创建时间</th>
										<th>账户名</th>
										<th>商品总金额</th>
										<th>邮费</th>
										<th>订单应付金额</th>
										<th>折扣</th>
										<th>商品折扣价</th>
										<th>订单实付金额</th>
										<th>付款方式</th>
										<th>RMB金额</th>
										<th>余额抵扣</th>
										<th>付款时间</th>
										<th>收件人姓名</th>
										<th>收件人电话</th>
										<th>收件人地址</th>
										<th>商品名称</th>
										<th>缩略图</th>
										<th>价格</th>
										<th>数量</th>
										<th>总金额</th>
										<th>其他</th>
									</tr>
								</thead>
								<tbody id="tbody">
									<c:forEach var="order" items="${page.list}" >
										<tr>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.orderId}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.statusStr}</td>
											<td width="2%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}"><fmt:formatDate value="${order.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.amountPhone}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.totalMoney}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.postage}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.totalPayable}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.discount==null?"暂无":order.discount}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.discountMoney!=null?order.discountMoney:order.totalMoney}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.totalPay}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.payType==1?"点卡":(order.payType==3?"支付宝":"微信")}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.rmb!=null?order.rmb:0}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.cornMoney!=null?order.cornMoney:0}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}"><fmt:formatDate value="${order.payTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.userName}</td>
											<td width="1%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.phone}</td>
											<td width="3%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">${order.address}</td>
											<c:forEach var="product" items="${order.products}" varStatus="p">
												<c:choose>
													<c:when test="${p.index ==0 }">
															<td style="vertical-align: middle;" width="3%" >${product.productName}</td>
															<td style="vertical-align: middle;" width="1%" ><img src="${product.img}" height="40px" width="40px"></td>
															<td style="vertical-align: middle;" width="1%" >${product.productPrice}</td>
															<td style="vertical-align: middle;" width="1%" >${product.amount}</td>
															<td style="vertical-align: middle;" width="1%" >${product.total}</td>
														<td width="3%" style="vertical-align: middle;" rowspan="${fn:length(order.products)}">测试列</td>
													</c:when>
													<c:otherwise>
														<tr>
															<td style="vertical-align: middle;" width="3%" >${product.productName}</td>
															<td style="vertical-align: middle;" width="1%" ><img src="${product.img}" height="40px" width="40px"></td>
															<td style="vertical-align: middle;" width="1%" >${product.productPrice}</td>
															<td style="vertical-align: middle;" width="1%" >${product.amount}</td>
															<td style="vertical-align: middle;" width="1%" >${product.total}</td>
														</tr>
													</c:otherwise>
												</c:choose>
											</c:forEach>
										</tr>
									</c:forEach>
								</tbody>
										<tr>
											<td colspan="6">统计</td>
											<td id="countTotalMoney">${count.totalMoney}(元)</td>
											<td ></td>
											<td ></td>
											<td id="countTotalPay">${count.totalPay}(元)</td>
										</tr>
							</table>
							<tr><div id="page"></div> </tr>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
			</div>
		</section>
		<!-- /.content -->
	</div>


	<div class='modal' id='detailModal'>
		<div class='modal-dialog'>
			<div class='modal-content'>
				<div class='modal-header'>
					<button type='button' class='close' data-dismiss='modal'>
						<span aria-hidden='true'>×</span><span class='sr-only'>Close</span>
					</button>
					<h4 class='modal-title'>用户详情内容</h4>
				</div>
				<div class='modal-body'>
						<input type="hidden" id="id" name="id" value="">
					<div>
						<h3 align="center">基础信息</h3>
						<div class='form-group'>
							<label for='url'>uid：&nbsp;</label> 
								<input type="text" id="uidD" class="form-control flat"  disabled="disabled" style="width:160px;display:inline;"> 
							<label for='url'>手机号：</label> 
								<input type="text" id="phoneD" class="form-control flat" disabled="disabled" style="width:160px;display:inline;"> 
						</div>
						<div class='form-group'>
							<label for='url'>邀请人uid：</label> 
								<input type="text" id="inviterUidD" class="form-control flat"  disabled="disabled" style="width:160px;display:inline;"> 
							<label for='url'>邀请人手机号：</label> 
								<input type="text" id="inviterPhoneD" class="form-control flat"  disabled="disabled" style="width:160px;display:inline;"> 
						</div>
						<div class='form-group'>
							<label for='url'>余额：</label> 
								<input type="text" id="moneyD" class="form-control flat"  disabled="disabled" style="width:100px;display:inline;"> 
							<label for='url'>点卡：</label> 
								<input type="text" id="cardMoneyD" class="form-control flat"  disabled="disabled" style="width:100px;display:inline;"> 
							<label for='url'>佣金：</label> 
								<input type="text" id="commissionD" class="form-control flat"  disabled="disabled" style="width:100px;display:inline;"> 
						</div>
						
						<div class='form-group'>
							<label for='url'>头像：</label> 
									<img src="" id="imgD" height="50px" width="50px">
							<label for='url'>角色：</label> 
								<input type="text" id="isManagerD" class="form-control flat"  disabled="disabled" style="width:100px;display:inline;"> 
							<label for='url'>用户类型：</label> 
								<input type="text" id="isVipD" class="form-control flat"  disabled="disabled" style="width:100px;display:inline;"> 
						</div>
						<div class='form-group'>
							<label for='url'>邀请二维码地址：</label> 
								<input type="text" id="imgUrlD" class="form-control flat"  disabled="disabled" style="width:300px;display:inline;"> 
						</div>
					</div>
					<div>
						<h3 align="center">银行卡信息</h3>
						<div class='form-group'>
							<label for='url'>银行名：</label> 
								<input type="text" id="bankNameD" class="form-control flat"  disabled="disabled" style="width:160px;display:inline;"> 
							<label for='url'>银行卡号：</label> 
								<input type="text" id="bankNumD" class="form-control flat" disabled="disabled" style="width:160px;display:inline;"> 
						</div>
						<div class='form-group'>
							<label for='url'>户主：</label> 
								<input type="text" id="userNameD" class="form-control flat"  disabled="disabled" style="width:180px;display:inline;"> 
							<label for='url'>联系号码：</label> 
								<input type="text" id="phoneD1" class="form-control flat"  disabled="disabled" style="width:160px;display:inline;"> 
						</div>
						<div class='form-group'>
							<label for='url'>开户地址：</label> 
								<input type="text" id="bankAdressD" class="form-control flat"  disabled="disabled" style="width:320px;display:inline;"> 
						</div>
					</div>
					<div>
						<h3 align="center">收货地址信息</h3>
						<div class='form-group'>
							<label for='url'>收件人姓名：</label> 
								<input type="text" id="userNameDD" class="form-control flat"  disabled="disabled" style="width:160px;display:inline;"> 
							<label for='url'>收件人电话：</label> 
								<input type="text" id="phoneDD" class="form-control flat" disabled="disabled" style="width:160px;display:inline;"> 
						</div>
						<div class='form-group'>
							<label for='url'>邮编：</label> 
								<input type="text" id="postCodeDD" class="form-control flat"  disabled="disabled" style="width:180px;display:inline;"> 
						</div>
						<div class='form-group'>
							<label for='url'>收货地址：</label> 
								<input type="text" id="addressDD" class="form-control flat"  disabled="disabled" style="width:320px;display:inline;"> 
						</div>
					</div>
					<div class='modal-footer'>
						<button type='button' class='btn btn-default'
							data-dismiss='modal'>关闭</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 转账记录 /佣金记录 -->
	<div class='modal' id='zzjl_Modal'>
		<div class='modal-dialog'>
			<div class='modal-content'>
				<div class='modal-header'>
					<button type='button' class='close' data-dismiss='modal'>
						<span aria-hidden='true'>×</span><span class='sr-only'>Close</span>
					</button>
					<h4 align="center" class='modal-title-zz-yj'></h4>
				</div>
				<div class='modal-body' style="min-width:700;max-height:500px; overflow:scroll;">
					<table id="zzjl_table" class="table table-bordered  table-striped table-hover">
						<thead>
							<tr>
								<th>类型</th>
								<th>描述</th>
								<th class="daoli">稻粒</th>
								<th>时间</th>
							</tr>
						</thead>
						<tbody id="zzjl_tbody">
						</tbody>
					</table>
					<div class='modal-footer'>
						<button type='button' class='btn btn-default'
							data-dismiss='modal'>关闭</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!--提现记录  -->
	<div class='modal' id='txjl_Modal'>
		<div class='modal-dialog'>
			<div class='modal-content'>
				<div class='modal-header'>
					<button type='button' class='close' data-dismiss='modal'>
						<span aria-hidden='true'>×</span><span class='sr-only'>Close</span>
					</button>
					<h4 align="center" class='modal-title'>提现记录</h4>
				</div>
				<div class='modal-body' style="min-width:700;max-height:500px; overflow:scroll;">
					<table id="txjl_table" class="table table-bordered  table-striped table-hover">
						<thead>
							<tr>
								<th>时间</th>
								<th>金额</th>
								<th>状态</th>
							</tr>
						</thead>
						<tbody id="txjl_tbody">
						</tbody>
					</table>
					<div class='modal-footer'>
						<button type='button' class='btn btn-default'
							data-dismiss='modal'>关闭</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!--我的通讯录  -->
	<div class='modal' id='txl_Modal'>
		<div class='modal-dialog'>
			<div class='modal-content'>
				<div class='modal-header'>
					<button type='button' class='close' data-dismiss='modal'>
						<span aria-hidden='true'>×</span><span class='sr-only'>Close</span>
					</button>
					<h4 align="center" class='modal-title'>我的通讯录</h4>
				</div>
				<div class='modal-body' style="min-width:700;max-height:500px; overflow:scroll;">
				
					<h5 align="center" class='modal-title_fs'>粉丝:0人数</h4>
					<table id="txl_table" class="table table-bordered  table-striped table-hover">
						<thead>
							<tr>
								<th>头像</th>
								<th>用户信息</th>
								<th>身份类别</th>
							</tr>
						</thead>
						<tbody id="txl_tbody">
						</tbody>
					</table>
					<div class='modal-footer'>
						<button type='button' class='btn btn-default'
							data-dismiss='modal'>关闭</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!--我的账单内容  -->
	<div class='modal' id='zd_Modal'>
		<div class='modal-dialog'>
			<div class='modal-content'>
				<div class='modal-header'>
					<button type='button' class='close' data-dismiss='modal'>
						<span aria-hidden='true'>×</span><span class='sr-only'>Close</span>
					</button>
					<h4 align="center" class='modal-title'>我的账单</h4>
				</div>
				<div class='modal-body' style="min-width:700;max-height:500px; overflow:scroll;">
					<table id="zd_table" class="table table-bordered  table-striped table-hover">
						<thead>
							<tr>
								<th>时间</th>
								<th>金额</th>
								<th>类型</th>
							</tr>
						</thead>
						<tbody id="zd_tbody">
						</tbody>
					</table>
					<div class='modal-footer'>
						<button type='button' class='btn btn-default'
							data-dismiss='modal'>关闭</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
	<!--话费充值记录  -->
	<div class='modal' id='hf_Modal'>
		<div class='modal-dialog'>
			<div class='modal-content'>
				<div class='modal-header'>
					<button type='button' class='close' data-dismiss='modal'>
						<span aria-hidden='true'>×</span><span class='sr-only'>Close</span>
					</button>
					<h4 align="center" class='modal-title'>话费充值记录</h4>
				</div>
				<div class='modal-body' style="min-width:700;max-height:500px; overflow:scroll;">
					<table id="hf_table" class="table table-bordered  table-striped table-hover">
						<thead>
							<tr>
								<th>提交时间</th>
								<th>充值号码</th>
								<th>充值金额</th>
								<th>折扣金额</th>
								<th>状态</th>
							</tr>
						</thead>
						<tbody id="hf_tbody">
						</tbody>
					</table>
					<div class='modal-footer'>
						<button type='button' class='btn btn-default'
							data-dismiss='modal'>关闭</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!--我的订单  -->
	<div class='modal' id='dd_Modal'>
		<div class='modal-dialog'>
			<div class='modal-content'>
				<div class='modal-header'>
					<button type='button' class='close' data-dismiss='modal'>
						<span aria-hidden='true'>×</span><span class='sr-only'>Close</span>
					</button>
					<h4 align="center" class='modal-title'>我的订单</h4>
				</div>
				<div class='modal-body' style="min-width:700;max-height:500px; overflow:scroll;">
					<table id="dd_table" class="table table-bordered  table-striped table-hover">
						<thead>
							<!-- <tr>
								<th>提交时间</th>
								<th>充值号码</th>
								<th>充值金额</th>
								<th>折扣金额</th>
								<th>状态</th>
							</tr> -->
						</thead>
						<tbody id="dd_tbody">
						</tbody>
					</table>
					<div class='modal-footer'>
						<button type='button' class='btn btn-default'
							data-dismiss='modal'>关闭</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
</body>
<!-- /.content-wrapper -->
</html>