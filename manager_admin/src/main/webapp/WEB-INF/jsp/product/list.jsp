<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../admin/common.jsp" />
<script type="text/javascript" src="/js/product.js"></script>
</head>
<body>
	<div>
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>
				商品列表 
			</h1>
			<ol class="breadcrumb">
				<li><a href="#">商品列表 </a></li>
			</ol>
		</section>

		<!-- Main content -->
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
						<div class="box-body">
							<div class="row">
									商品名称: <input placeholder="商品名称" type="text" maxlength="50"  id="productName">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									状态:
									<select id="status">
										<option value="">-请选择-</option>
										<option value=0>未上架</option>
										<option value="1">已上架</option>
										<option value="2">已下架</option>
									</select>
									<button type="button" name="search" onclick="search()" class="btn btn-primary">查询</button>
									<button type="button" id="add" onclick="addProduct()"
													class="btn btn-primary">新增商品</button>
							</div>
						</div>
						<!-- /.box-header -->
						<div class="box-body">
							<input type="hidden" id="pageNum" value="${page.pageNum}">
							<input type="hidden" id="pageSize" value="${page.pageSize}">
							<input type="hidden" id="pages" value="${page.pages}">
							<table id="table1"
								class="table table-bordered  table-striped table-hover">
								<thead>
									<tr>
										<th>商品标识</th>
										<th>商品名称</th>
										<th>价格</th>
										<th>库存数量</th>
										<th>销售数量</th>
										<th>总数量</th>
										<th>详情</th>
										<th>状态</th>
										<th>创建时间</th>
										<th>其他操作</th>
									</tr>
								</thead>
								<tbody id="tbody">
									<c:forEach var="entity" items="${page.list}">
										<tr>
											<td width="5%">${entity.productId}</td>
											<td width="8%">
												<c:if test="${fn:length(entity.productName)>8 }">                         
													${fn:substring(entity.productName, 0, 8)}...                   
												</c:if>                  
												<c:if test="${fn:length(entity.productName)<=8 }"> 
												    ${entity.productName }                   
												</c:if>
											</td>
											<td width="9%">${entity.productPriceStr}</td>
											<td width="5%">${entity.stockAmount}</td>
											<td width="8%">${entity.salesAmount}</td>
											<td width="8%">${entity.initAmount}</td>
											<td width="9%">
												<c:if test="${fn:length(entity.detail)>15 }">                         
													${fn:substring(entity.detail, 0, 15)}...                   
												</c:if>                  
												<c:if test="${fn:length(entity.detail)<=15 }"> 
												    ${entity.detail }                   
												</c:if>
											</td>
											<td width="7%">${entity.status==0?"未上架":(entity.status==1?"已上架":"已下架")}</td>
											<td width="7%">${entity.createTimeStr}</td>
											<td width="30%">
												<button class="btn btn-primary btn-sm"
													onclick="detailProduct('${entity.productId }')">
													<i class="glyphicon glyphicon-edit">详情</i>
												</button> 
												<button class="btn btn-primary btn-sm"
													onclick="editProduct('${entity.productId }')">
													<i class="glyphicon glyphicon-edit">修改</i>
												</button> 
												<button class="btn btn-info btn-sm"
													onclick='setProductStatus("${entity.productId}","1")'>商品上架</button>
												<button class="btn btn-warning btn-sm"
													onclick='setProductStatus("${entity.productId}","2")'>商品下架</button>
											</td>
										</tr>
									</c:forEach>
								</tbody>
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


	<div class='modal' id='addProduct'>
		<div class='modal-dialog'>
			<div class='modal-content'>
				<div class='modal-header'>
					<button type='button' class='close' data-dismiss='modal'>
						<span aria-hidden='true'>×</span><span class='sr-only'>Close</span>
					</button>
					<h4 class='modal-title' align="center" >添加商品</h4>
				</div>
				<div class='modal-body'>
						<input type="hidden" id="id" name="id" value="">
						<div class='form-group'>
							<label for='url'>商品名称</label> 
							<input type="text" class='form-control' name="productName" id="productNameSave" placeholder='商品名称'>
						</div>
						<div >
							<label>商品单价:<input type="text" class='form-control' name="productPrice" id='productPrice' placeholder='商品单价'></label> 
							<label>商品库存:<input type="text" class='form-control' name="initAmount" id='initAmount' placeholder='商品库存'></label> 
						</div>
						
						<div >
							<label for='username'>缩略图</label> 
							<form id="imgForm" enctype="multipart/form-data">
								<img id="imgSrc" onclick="javascript:show(this);" src="" height="50px" width="50px">
								<input type="file" name="img" multiple="multiple" onchange="imgsUpload('imgForm','imgSrc')"/>
							</form>
						</div>
						<div>
							<div class='form-group' id="coverImgs">
								<label for='username'>封面图片</label> 
								<div id="coverImgsDiv1">
									<form id="coverImgForm1"  class="coverImgForm" enctype="multipart/form-data">
										<img id="coverImgSrc1" onclick="javascript:show(this);" class="coverImgSrc" src="" height="50px" width="50px">
										<input type="file" name="coverImg" multiple="multiple" onchange="coverImgsUpload(1)"/>
									</form>
								</div>
							</div>
						 	<div id="outerdiv" style="position:absolute;top:30%;left:280px;z-index:2000;width:200%;height:200%;display:none;">
						       	<div id="innerdiv" style="position:fixed;width: 200%;height:200%;">
						       		<img id="bigimg" style="border:5px solid #fff;width: 520px;;height:350px;" src="" />
						       	</div>
						    </div>  
							<div>
								<input type="button" value="增加封面图片" onclick="addCoverImg()"><input value="删除封面图片" type="button" onclick="delCoverImg()">
							</div>
							
							<div class='form-group' id="detailImgs">
								<label for='username'>详情图片</label> 
								<div id="detailImgDiv1">
									<form id="detailImgForm1"  class="detailImgForm" enctype="multipart/form-data">
										<img id="detailImgSrc1" onclick="javascript:show(this);" class="detailImgSrc" src="" height="50px" width="50px">
										<input type="file" name="detailImg" multiple="multiple" onchange="detailImgsUpload(1)"/>
									</form>
								</div>
							</div>
							<div>
								<input type="button" value="增加详情图片" onclick="addDetailImg()"><input value="删除详情图片" type="button" onclick="delDetailImg()">
							</div>
						</div>
						
						<div class='form-group'>
							<label for='driver'>详情内容:</label> 
							<textarea rows="5" id="detail"  cols=27   onpropertychange= "this.style.posHeight=this.scrollHeight "></textarea>
						</div>
						
						<div class='modal-footer'>
							<button type='button' class='btn btn-default'
								data-dismiss='modal'>关闭</button>
							<button type="button" onclick="saveProduct()" class='btn btn-primary' >保存</button>
						</div>
				</div>

			</div>
		</div>
	</div>
	
	
	<div class='modal' id='detailProduct'>
		<div class='modal-dialog'>
			<div class='modal-content'>
				<div class='modal-header'>
					<button type='button' class='close' data-dismiss='modal'>
						<span aria-hidden='true'>×</span><span class='sr-only'>Close</span>
					</button>
					<h4 class='modal-title'>查看商品</h4>
				</div>
				<div class='modal-body'>
						<input type="hidden" id="id" name="id" value="">
						<div class='form-group'>
							<label for='url'>商品名称</label> 
							<input type="text" class='form-control' name="productName" id="productNameDetail" placeholder='商品名称'>
						</div>
						<div >
							<label style="width:100px;" >商品单价:<input type="text" class='form-control' name="productPrice" id='productPriceDetail' placeholder='商品单价' ></label> 
							<label>商品库存:<input type="text" class='form-control' name="stockAmount" id='stockAmountDetail'  ></label> 
							<label>商品销量:<input type="text" class='form-control' name="salesAmount" id='salesAmountDetail' ></label> 
							<label>商品总量:<input type="text" class='form-control' name="initAmount" id='initAmountDetail' ></label> 
						</div>
						<div>
							<div class='form-group'>
								<label for='username'>缩略图</label> 
								<img id="imgDetail" src="" height="50px" width="50px">
							</div>
							<div class='form-group'>
								<label for='username'>封面图片</label> 
								<div id="coverImgsDivDetail">
								</div>
							</div>
							
							<div class='form-group'>
								<label for='username'>详情图片</label> 
								<div id="detailImgDivDetail">
								</div>
							</div>
						</div>
						<div class='form-group'>
							<label for='driver'>详情内容:</label> 
							<textarea rows="5" id="detailDetail"  cols=27   onpropertychange= "this.style.posHeight=this.scrollHeight "></textarea>
						</div>
						<div class='modal-footer'>
							<button type='button' class='btn btn-default'
								data-dismiss='modal'>关闭</button>
						</div>
				</div>

			</div>
		</div>
	</div>
	
	<div class='modal' id='editProduct'>
		<div class='modal-dialog'>
			<div class='modal-content'>
				<div class='modal-header'>
					<button type='button' class='close' data-dismiss='modal'>
						<span aria-hidden='true'>×</span><span class='sr-only'>Close</span>
					</button>
					<h4 class='modal-title'>修改商品</h4>
				</div>
				<div class='modal-body'>
					<input type="hidden" id="">
						<div class='form-group'>
							<label for='url'>商品id</label> 
							<input type="text" class='form-control' disabled="disabled" id="productIdE" placeholder='商品名称'>
							<label for='url'>商品名称</label> 
							<input type="text" class='form-control' id="productNameE" placeholder='商品名称'>
						</div>
						
						<div class='form-group'>
							<label for='url'>商品单价:</label> 
								<input type="text" class='form-control' id='productPriceE' style="width:50px;display:inline;">
							<label for='url'>商品库存:</label> 
								<input type="text" class='form-control' id='stockAmountE' disabled="disabled" style="width:50px;display:inline;">
							<label for='url'>商品销量:</label> 
								<input type="text" class='form-control' id='salesAmountE' disabled="disabled" style="width:50px;display:inline;">
							<label for='url'>商品总数:</label> 
								<input type="text" class='form-control' id='initAmountE' style="width:50px;display:inline;">
						</div>
						
						<div class='form-group' >
							<label for='username'>缩略图</label> 
							<form id="imgFormE" enctype="multipart/form-data">
								<img id="imgE" src="" height="50px" width="50px" onclick="javascript:showE(this);">
								<input type="file" name="img" multiple="multiple" onchange="imgsUpload('imgFormE','imgE')"/>
							</form>
						</div>
						<div>
							<div class='form-group' id="coverImgsE">
								<label for='username'>封面图片</label> 
							</div>
						 	<div id="outerdivE" style="position:absolute;top:30%;left:280px;z-index:2000;width:200%;height:200%;display:none;">
						       	<div id="innerdivE" style="position:fixed;width: 200%;height:200%;">
						       		<img id="bigimgE" style="border:5px solid #fff;width: 520px;;height:350px;" src="" />
						       	</div>
						    </div>  
							<div>
								<input type="button" value="增加封面图片" onclick="addCoverImgE()"><input value="删除封面图片" type="button" onclick="delCoverImgE()">
							</div>
							
							<div class='form-group' id="detailImgsE">
								<label for='username'>详情图片</label> 
							</div>
							<div>
								<input type="button" value="增加详情图片" onclick="addDetailImgE()"><input value="删除详情图片" type="button" onclick="delDetailImgE()">
							</div>
						</div>
						<div class='form-group'>
							<label for='driver'>详情内容:</label> 
							<textarea rows="5" id="detailE"  cols=27   onpropertychange= "this.style.posHeight=this.scrollHeight "></textarea>
						</div>
						
						<div class='modal-footer'>
							<button type='button' class='btn btn-default'
								data-dismiss='modal'>关闭</button>
							<button type="button" onclick="editDbProduct()" class='btn btn-primary' >保存</button>
						</div>
				</div>

			</div>
		</div>
	</div>

</body>
 
<!-- /.content-wrapper -->
</html>