<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../admin/common.jsp" />
<script type="text/javascript" src="/js/bill.js"></script>
</head>
<body>


	<!-- Content Wrapper. Contains page content -->
	<div>
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>
				账单列表 
			</h1>
			<ol class="breadcrumb">
				<li><a href="#">账单列表 </a></li>
			</ol>
		</section>
	<br>
	<br>
	<ul id="myTab" class="nav nav-tabs">
		<li class="active">
			<a href="#jizhang" data-toggle="tab">
				进账
			</a>
		</li>
		<li>
			<a href="#chuzhang" data-toggle="tab">
				出账
			</a>
		</li>
	</ul>

		<!-- Main content -->
		<div id="myTabContent" class="tab-content">
			<div class="tab-pane fade in active" id="jizhang">
				<section class="content">
					<div class="row">
						<div class="col-xs-12">
							<div class="box">
								<div class="box-body">
									<div class="row">
											手机号: <input type="text" maxlength="11"  id="phone">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											订单号: <input type="text" maxlength="11" id="orderId">&nbsp;&nbsp;
											开始时间:<input id="startDate" style="width: 100px;height: 26px" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,maxDate:'#F{$dp.$D(\'endDate\');}'})" />&nbsp;&nbsp;
											结束时间:<input id="endDate" style="width: 100px;height: 26px" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,minDate:'#F{$dp.$D(\'startDate\');}'})" />
											账单类型:
											<select id="billType">
												<option value="">-请选择-</option>
												<option value=1>VIP账单</option>
												<option value="2">点卡</option>
												<option value="3">话费充值</option>
												<option value="4">购物消费</option>
											</select>
											<button type="button" name="search" onclick="search()" class="btn btn-primary">查询</button>
									</div>
								</div>
								<!-- /.box-header -->
								<div class="box-body"  style="overflow:scroll;">
									<input type="hidden" id="pageNum" value="${page.pageNum}">
									<input type="hidden" id="pageSize" value="${page.pageSize}">
									<input type="hidden" id="pages" value="${page.pages}">
									<table id="table" class="table table-bordered  table-striped table-hover" style="min-width:1300px;">
										<thead>
											<tr>
												<th>uid</th>
												<th>账户名</th>
												<th>账单类型</th>
												<th>订单标识</th>
												<th>账单金额</th>
												<th>账单描述</th>
												<th>时间</th>
												<th>其他</th>
											</tr>
										</thead>
										<tbody id="tbody">
											<c:forEach var="bill" items="${page.list}">
												<tr>
													<td width="4%">${bill.uid}</td>
													<td width="5%">${bill.phone}</td>
													<td width="5%">${bill.billType==1?"VIP账单":(bill.billType==2?"点卡":(bill.billType==3?"话费充值":"购物消费"))}</td>
													<td width="5%">${bill.orderId}</td>
													<td width="5%">${bill.amount==null?0:bill.amount}</td>
													<td width="7%">${bill.description}</td>
													<td width="8%"><fmt:formatDate value="${bill.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
													<td width="20%">
														<button class="btn btn-primary btn-sm"
															onclick="detailBill('${bill.id }')">详情</button>
													</td>
												</tr>
											</c:forEach>
										</tbody>
												<tr>
													<td colspan="4">统计</td>
													<td id="totalAmount">${amount}(元)</td>
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
			</div>
			
			<div class="tab-pane fade" id="chuzhang">
				<section class="content">
					<div class="row">
						<div class="col-xs-12">
							<div class="box">
								<div class="box-body">
									<div class="row">
											手机号: <input type="text" maxlength="11"  id="phonec">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											订单号: <input type="text" maxlength="11" id="orderIdc">&nbsp;&nbsp;
											开始时间:<input id="startDatec" style="width: 100px;height: 26px" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,maxDate:'#F{$dp.$D(\'endDate\');}'})" />&nbsp;&nbsp;
											结束时间:<input id="endDatec" style="width: 100px;height: 26px" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,minDate:'#F{$dp.$D(\'startDate\');}'})" />
											账单类型:
											<select id="billTypec">
												<option value="">-请选择-</option>
												<option value="3">话费充值</option>
												<option value="4">商品进价</option>
												<option value="5">提现账单</option>
											</select>
											<button type="button" name="search" onclick="searchc()" class="btn btn-primary">查询</button>
									</div>
								</div>
								<!-- /.box-header -->
								<div class="box-body"  style="overflow:scroll;">
									<input type="hidden" id="pageNum" value="${page.pageNum}">
									<input type="hidden" id="pageSize" value="${page.pageSize}">
									<input type="hidden" id="pages" value="${page.pages}">
									<table id="table" class="table table-bordered  table-striped table-hover" style="min-width:1300px;">
										<thead>
											<tr>
												<th>uid</th>
												<th>账户名</th>
												<th>账单类型</th>
												<th>订单标识</th>
												<th>账单金额</th>
												<th>账单描述</th>
												<th>时间</th>
												<th>其他</th>
											</tr>
										</thead>
										<tbody id="tbody">
											<c:forEach var="bill" items="${page.list}">
												<tr>
													<td width="4%">${bill.uid}</td>
													<td width="5%">${bill.phone}</td>
													<td width="5%">${bill.billType==1?"VIP账单":(bill.billType==2?"点卡":(bill.billType==3?"话费充值":"购物消费"))}</td>
													<td width="5%">${bill.orderId}</td>
													<td width="5%">${bill.amount==null?0:bill.amount}</td>
													<td width="7%">${bill.description}</td>
													<td width="8%"><fmt:formatDate value="${bill.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
													<td width="20%">
														<button class="btn btn-primary btn-sm"
															onclick="detailBill('${bill.id }')">详情</button>
													</td>
												</tr>
											</c:forEach>
										</tbody>
												<tr>
													<td colspan="4">统计</td>
													<td id="totalAmount">${amount}(元)</td>
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
			</div>
		</div>
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

	
	
	
</body>
<!-- /.content-wrapper -->
</html>