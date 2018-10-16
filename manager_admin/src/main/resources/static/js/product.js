$(function(){
	var pages = $("#pages").val();
	var pageNum = $("#pageNum").val();
	var pageSize = $("#pageSize").val();
	var options={
            bootstrapMajorVersion:1,    //版本
            currentPage:pageNum,    //当前页数
            numberOfPages:5,    //最多显示Page页
            totalPages:pages,    //所有数据可以显示的页数
            onPageClicked:function(e,originalEvent,type,page){
            	addHtml(page);
            }
        }
        $("#page").bootstrapPaginator(options);
})


	function addHtml(pageNum){
		var pageSize = $("#pageSize").val();
		var productName = $("#productName").val();
    	var status = $("#status").val();
    	var data = {
    		 "pageNum":pageNum,"pageSize":pageSize,"productName":productName,"status":status
    	}
		$.ajax({
    		type : "post",
    		data : JSON.stringify(data),
    		url : "/product/dataList",
    		dataType : "json",
    		contentType : 'application/json',
    		success : function(data) {
    			if(data != null){
    				$("#tbody").html("");
    				var htmlStr="";
    				 $.each(data.list, function (index, item) {
    					 var productName = item.productName!=null?item.productName:"";
    					 if(productName.length>8){
    						 productName = productName.substring(0,8)+"...";
    					 }
    					 var productPriceStr = item.productPriceStr!=null?item.productPriceStr:"0.00";
    					 var detail = item.detail!=null?item.detail:"";
    					 if(detail.length>15){
    						 detail = detail.substring(0,15)+"...";
    					 }
    					 
    					 var status = item.status==0?"未上架":(item.status==1?"已上架":"已下架");
    					 htmlStr += "<tr>";
    					 htmlStr += '<td width="5%">'+item.productId+'</td>';
    					 htmlStr += '<td width="8%">'+productName+'</td>';
    					 htmlStr += '<td width="9%">'+productPriceStr+'</td>';
    					 htmlStr += '<td width="5%">'+item.stockAmount+'</td>';
    				     htmlStr += '<td width="8%">'+item.salesAmount+'</td>';
    				     htmlStr += '<td width="8%">'+item.initAmount+'</td>';
    				     htmlStr += '<td width="9%">'+detail+'</td>';
    				     htmlStr += '<td width="7%">'+status+'</td>';
    				     htmlStr += '<td width="7%">'+item.createTimeStr+'</td>';
    					 htmlStr += '<td width="30%">';
    					 htmlStr += '<button class="btn btn-primary btn-sm" onclick="detailProduct('+item.productId+')"> <i class="glyphicon glyphicon-edit">详情</i></button> '
    					 htmlStr += '<button class="btn btn-primary btn-sm" onclick="editProduct('+item.productId+')"> <i class="glyphicon glyphicon-edit">修改</i></button> '
    					 htmlStr += '<button class="btn btn-info btn-sm" onclick="setProductStatus('+item.productId+',1)">商品上架</button>'
    					 htmlStr += '<button class="btn btn-warning btn-sm" onclick="setProductStatus('+item.productId+',2)">商品下架</button>'
    					 htmlStr += '</td>';
    					 htmlStr += '</tr>';
    				 });
    				 $("#tbody").html(htmlStr);
    			}
    		}
    	});
	}

	
function search(){
	var pages = 0;
	var pageNum = 1
	var pageSize = $("#pageSize").val();
	var productName = $("#productName").val();
	var status = $("#status").val();
	var data = {
		 "pageNum":pageNum,"pageSize":pageSize,"productName":productName,"status":status
	}
	$.ajax({
		type : "post",
		data : JSON.stringify(data),
		url : "/product/dataList",
		dataType : "json",
		contentType : 'application/json',
		success : function(data) {
			if(data != null){
				pageNum = data.pageNum
				pages = data.pages;
				$("#tbody").html("");
				var htmlStr="";
				 $.each(data.list, function (index, item) {
					 var productName = item.productName!=null?item.productName:"";
					 if(productName.length>8){
						 productName = productName.substring(0,8)+"...";
					 }
					 var productPriceStr = item.productPriceStr!=null?item.productPriceStr:"0.00";
					 var detail = item.detail!=null?item.detail:"";
					 if(detail.length>15){
						 detail = detail.substring(0,15)+"...";
					 }
					 var status = item.status==0?"未上架":(item.status==1?"已上架":"已下架");
					 htmlStr += "<tr>";
					 htmlStr += '<td width="5%">'+item.productId+'</td>';
					 htmlStr += '<td width="8%">'+productName+'</td>';
					 htmlStr += '<td width="9%">'+productPriceStr+'</td>';
					 htmlStr += '<td width="5%">'+item.stockAmount+'</td>';
				     htmlStr += '<td width="8%">'+item.salesAmount+'</td>';
				     htmlStr += '<td width="8%">'+item.initAmount+'</td>';
				     htmlStr += '<td width="9%">'+detail+'</td>';
				     htmlStr += '<td width="7%">'+status+'</td>';
				     htmlStr += '<td width="7%">'+item.createTimeStr+'</td>';
					 htmlStr += '<td width="30%">';
					 htmlStr += '<button class="btn btn-primary btn-sm" onclick="detailProduct('+item.productId+')"> <i class="glyphicon glyphicon-edit">详情</i></button> '
					 htmlStr += '<button class="btn btn-primary btn-sm" onclick="editProduct('+item.productId+')"> <i class="glyphicon glyphicon-edit">修改</i></button> '
					 htmlStr += '<button class="btn btn-info btn-sm" onclick="setProductStatus('+item.productId+',1)">商品上架</button>'
					 htmlStr += '<button class="btn btn-warning btn-sm" onclick="setProductStatus('+item.productId+',2)">商品下架</button>'
					 htmlStr += '</td>';
					 htmlStr += '</tr>';
				 });
				 $("#tbody").html(htmlStr);
				 
			}
			var options={
		            bootstrapMajorVersion:1,    //版本
		            currentPage:pageNum,    //当前页数
		            numberOfPages:5,    //最多显示Page页
			        totalPages:pages,    //所有数据可以显示的页数
		            onPageClicked:function(e,originalEvent,type,page){
		            	addHtml(page);
		            }
		        }
			if(pages >0){
				$("#page").bootstrapPaginator(options);
				$("#page").show();
			}else{
				$("#page").hide();
			}
		}
	});
}

 

function imgsUpload(form,src){
	var formData = new FormData($("#"+form)[0]);
	$.ajax({
		type : 'post',
		url : "/product/uploadCoverImg",
		data : formData,
		cache : false,
		processData : false,
		contentType : false,
		dataType : "json",
	}).success(function(data) {
		if(null != data && data.code == 1000){
			$("#"+src).attr("src",data.url);
			return ;
		}
	}).error(function() {
		alert("上传失败");
		return ;
	});

}


function coverImgsUpload(num){
	var formData = new FormData($("#coverImgForm"+num)[0]);
	$.ajax({
		type : 'post',
		url : "/product/uploadCoverImg",
		data : formData,
		cache : false,
		processData : false,
		contentType : false,
		dataType : "json",
	}).success(function(data) {
		if(null != data && data.code == 1000){
			$("#coverImgSrc"+num).attr("src",data.url);
			return ;
		}
	}).error(function() {
		alert("上传失败");
		return ;
	});

}

function addProduct(){
	$('#addProduct').modal('toggle');
}

function addCoverImg(){
	var divs = $("#coverImgs div");
	var length = divs.length+1;
	if(length > 3){
		alert("封面图片最多3张")
		return ;
	}
	var parent = $("#coverImgs");
	var htmlStr='<div id="coverImgsDiv'+length+'">';
	htmlStr+='<form id="coverImgForm'+length+'"  class="coverImgForm" enctype="multipart/form-data">';
	htmlStr+='<img id="coverImgSrc'+length+'" onclick="javascript:show(this);" class="coverImgSrc" src="" height="50px" width="50px">';
	htmlStr+='<input type="file" name="coverImg" multiple="multiple" onchange="coverImgsUpload('+length+')"/>';
	htmlStr+='</form>';
	htmlStr+='</div>';
	parent.append(htmlStr);
}

function delCoverImg(){
	var divs = $("#coverImgs div");
	var length = divs.length -1;
	if(length < 1){
		alert("封面图片最少1张")
		return ;
	}
	$("#coverImgs div:last").remove();
}

function addDetailImg(){
	var divs = $("#detailImgs div");
	var length = divs.length+1;
	if(length > 10){
		alert("封面图片最多10张")
		return ;
	}
	var parent = $("#detailImgs");
	var htmlStr='<div id="detailImgDiv'+length+'">';
	htmlStr+='<form id="detailImgForm'+length+'"  class="detailImgForm" enctype="multipart/form-data">';
	htmlStr+='<img id="detailImgSrc'+length+'" onclick="javascript:show(this);" src="" class="detailImgSrc" height="50px" width="50px">';
	htmlStr+='<input type="file" name="detailImg" multiple="multiple" onchange="detailImgsUpload('+length+')"/>';
	htmlStr+='</form>';
	htmlStr+='</div>';
	parent.append(htmlStr);
}

function delDetailImg(){
	var divs = $("#detailImgs div");
	var length = divs.length -1;
	if(length < 1){
		alert("封面图片最少1张")
		return ;
	}
	$("#detailImgs div:last").remove();
}

function detailImgsUpload(num){
	var formData = new FormData($("#detailImgForm"+num)[0]);
	$.ajax({
		type : 'post',
		url : "/product/uploadCoverImg",
		data : formData,
		cache : false,
		processData : false,
		contentType : false,
		dataType : "json",
	}).success(function(data) {
		if(null != data && data.code == 1000){
			$("#detailImgSrc"+num).attr("src",data.url);
			return ;
		}
	}).error(function(data) {
		alert("上传失败");
		return ;
	});

}


function saveProduct(){
	var productName = $("#productNameSave").val();
	var productPrice = $("#productPrice").val();
	var initAmount = $("#initAmount").val();
	var coverImg ="";
	$(".coverImgSrc").each(function (index,item){
		var ret = $(item).attr("src");
		if(null != ret && ret != ''){
			coverImg += ret
			coverImg += ",";
		}
	});
	coverImg = coverImg.substring(0, coverImg.length-1);
	var detailImgs ="";
	$(".detailImgSrc").each(function (index,item){
		var ret = $(item).attr("src");
		if(null != ret && ret !=''){
			detailImgs += ret
			detailImgs += ",";
		}
	});
	detailImgs = detailImgs.substring(0, detailImgs.length-1);
	var img = $("#imgSrc").attr("src");
	var detail = $("#detail").val();
	if(null == productName || productName == ''){
		alert("商品名不能为空");
		return false;
	}
	if(null == productPrice || productPrice == ''){
		alert("商品价格不能为空");
		return false;
	}
	if(null == initAmount || initAmount == ''){
		alert("商品库存不能为空");
		return false;
	}
	if(null == coverImg || coverImg == ''){
		alert("商品封面图片不能为空");
		return false;
	}
	if(null == detailImgs || detailImgs == ''){
		alert("商品详情图片不能为空");
		return false;
	}
	if(null == detail || detail == ''){
		alert("商品详情不能为空");
		return false;
	}
	if(null == img || img == ''){
		alert("商品缩略图不能为空");
		return false;
	}
	
	var data = {
			"productName":productName,"productPrice":productPrice,"initAmount":initAmount,"coverImg":coverImg,"detailImgs":detailImgs,"detail":detail,"img":img
	}
	$.ajax({
		type : "post",
		data : JSON.stringify(data),
		url : "/product/saveProduct",
		dataType : "json",
		contentType : 'application/json',
		success : function(data) {
			if(null != data && data.code == 1000){
				 window.location.href = "/product/toProductList?pageSize=2&pageNum=1"; 
			}
		}
	});
	
}



function detailProduct(id) {
	
	var data = {
			"productId":id
	}
	
	$.ajax({
		type : "post",
		data : JSON.stringify(data),
		url : "/product/findByProductId",
		dataType : "json",
		contentType : 'application/json',
		success : function(data) {
			if(null != data && data.code == 1000){
				$("#productNameDetail").val(data.product.productName);
				$("#productPriceDetail").val(data.product.productPrice);
				$("#stockAmountDetail").val(data.product.stockAmount);
				$("#salesAmountDetail").val(data.product.salesAmount);
				$("#initAmountDetail").val(data.product.initAmount);
				$("#detailDetail").val(data.product.detail);
				$("#imgDetail").attr("src",data.product.img)
				$('#detailProduct').modal('toggle');
				var coverImg = data.product.coverImg;
				var coverImgArr = coverImg.split(",")
				var html = "";
				$.each(coverImgArr,function(index,j){
					html += '<input type="image" height="50px" width="50px" src="'+j+'">';
				})
				$("#coverImgsDivDetail").html(html);
				
				var detailImgs = data.product.detailImgs;
				var detailImgsArr = detailImgs.split(",")
				html = "";
				$.each(detailImgsArr,function(index,j){
					html += '<input type="image" height="50px" width="50px" src="'+j+'">';
				})
				$("#detailImgDivDetail").html(html);
			}
		}
	});
}



function setProductStatus(productId,type){
	var msg = "";
	var data ="";
	if(type == 1){
		msg = "您真的确定要上架该商品吗？\n\n请确认！";
		data = {
	    		 "productId":productId,"status":1
	    	}
	}else if(type == 2){
		msg = "您真的确定要下架该商品吗？\n\n请确认！";			
		data = {
				"productId":productId,"status":2
		}
	}
	if (confirm(msg) == true) {
		$.ajax({
			type : "post",
			data : JSON.stringify(data),
			url : "/product/setProductStatus",
			dataType : "json",
			contentType : 'application/json',
			success : function(data) {
				if(null != data && data.code == 1000){
					alert(data.msg);
				}
			}
		});
		return true;
	} else {
		return false;
	}
}

function show(obj){
    var _this = $(obj);//将当前的pimg元素作为_this传入函数  
    imgShow("#outerdiv", "#innerdiv", "#bigimg", _this);  
}

function imgShow(outerdiv, innerdiv, bigimg, _this){
    var src = _this.attr("src");//获取当前点击的pimg元素中的src属性  
    $(outerdiv).attr('display','block');
    $(bigimg).attr("src", src);//设置#bigimg元素的src属性  
     $(outerdiv).fadeIn("fast");
  
	$(outerdiv).click(function(){//再次点击淡出消失弹出层  
	    $(this).fadeOut("fast");  
	});  
}


//商品修改内容;
function editProduct(productId) {
	var data = {
			"productId":productId
	}
	
	$.ajax({
		type : "post",
		data : JSON.stringify(data),
		url : "/product/findByProductId",
		dataType : "json",
		contentType : 'application/json',
		success : function(data) {
			if(null != data && data.code == 1000){
				$('#editProduct').modal('toggle');
				$("#productIdE").val(data.product.productId);
				$("#productNameE").val(data.product.productName);
				$("#productPriceE").val(data.product.productPrice);
				$("#stockAmountE").val(data.product.stockAmount);
				$("#salesAmountE").val(data.product.salesAmount);
				$("#initAmountE").val(data.product.initAmount);
				$("#detailE").val(data.product.detail);
				$("#imgE").attr("src",data.product.img);
				var coverImg = data.product.coverImg;
				var coverImgArr = coverImg.split(",")
				var html = "";
				var num = 1;
				$.each(coverImgArr,function(index,j){
					html += '<div id="coverImgsDivE'+num+'">';
					html += '<form id="coverImgFormE'+num+'"  class="coverImgFormE" enctype="multipart/form-data">';
					html += '<img id="coverImgSrcE'+num+'" onclick="javascript:showE(this);" class="coverImgSrcE" src="'+j+'" height="50px" width="50px">';
					html += '<input type="file" name="coverImgE" multiple="multiple" onchange="coverImgsUploadE('+num+')"/>';
					html += '</form>';
					html += '</div>';
					num++;
				})
				$("#coverImgsE").html(html);
				
				var detailImgs = data.product.detailImgs;
				var detailImgsArr = detailImgs.split(",")
				html = "";
				num = 1;
				$.each(detailImgsArr,function(index,j){
					html += '<div id="detailImgDivE'+num+'">';
					html += '<form id="detailImgFormE'+num+'"  class="detailImgFormE" enctype="multipart/form-data">';
					html += '<img id="detailImgSrcE'+num+'" onclick="javascript:showE(this);" class="detailImgSrcE" src="'+j+'" height="50px" width="50px">';
					html += '<input type="file" name="detailImgE" multiple="multiple" onchange="detailImgsUploadE('+num+')"/>';
					html += '</form>';
					html += '</div>';
					num++;
				})
				$("#detailImgsE").html(html);
			}
		}
	});
	
}
	
function addCoverImgE(){
	var divs = $("#coverImgsE div");
	var length = divs.length+1;
	if(length > 3){
		alert("封面图片最多3张")
		return ;
	}
	var parent = $("#coverImgsE");
	var htmlStr='<div id="coverImgsDivE'+length+'">';
	htmlStr+='<form id="coverImgFormE'+length+'"  class="coverImgFormE" enctype="multipart/form-data">';
	htmlStr+='<img id="coverImgSrcE'+length+'" onclick="javascript:showE(this);" class="coverImgSrcE" src="" height="50px" width="50px">';
	htmlStr+='<input type="file" name="coverImgE" multiple="multiple" onchange="coverImgsUploadE('+length+')"/>';
	htmlStr+='</form>';
	htmlStr+='</div>';
	parent.append(htmlStr);
}

function delCoverImgE(){
	var divs = $("#coverImgsE div");
	var length = divs.length -1;
	if(length < 1){
		alert("封面图片最少1张")
		return ;
	}
	$("#coverImgsE div:last").remove();
}

function addDetailImgE(){
	var divs = $("#detailImgsE div");
	var length = divs.length+1;
	if(length > 10){
		alert("封面图片最多10张")
		return ;
	}
	var parent = $("#detailImgsE");
	var htmlStr='<div id="detailImgDivE'+length+'">';
	htmlStr+='<form id="detailImgFormE'+length+'"  class="detailImgFormE" enctype="multipart/form-data">';
	htmlStr+='<img id="detailImgSrcE'+length+'" onclick="javascript:showE(this);" src="" class="detailImgSrcE" height="50px" width="50px">';
	htmlStr+='<input type="file" name="detailImgE" multiple="multiple" onchange="detailImgsUploadE('+length+')"/>';
	htmlStr+='</form>';
	htmlStr+='</div>';
	parent.append(htmlStr);
}

function delDetailImgE(){
	var divs = $("#detailImgsE div");
	var length = divs.length -1;
	if(length < 1){
		alert("封面图片最少1张")
		return ;
	}
	$("#detailImgsE div:last").remove();
}


function coverImgsUploadE(num){
	var formData = new FormData($("#coverImgFormE"+num)[0]);
	$.ajax({
		type : 'post',
		url : "/product/uploadCoverImg",
		data : formData,
		cache : false,
		processData : false,
		contentType : false,
		dataType : "json",
	}).success(function(data) {
		if(null != data && data.code == 1000){
			$("#coverImgSrcE"+num).attr("src",data.url);
			return ;
		}
	}).error(function() {
		alert("上传失败");
		return ;
	});
}

function detailImgsUploadE(num){
	var formData = new FormData($("#detailImgFormE"+num)[0]);
	$.ajax({
		type : 'post',
		url : "/product/uploadCoverImg",
		data : formData,
		cache : false,
		processData : false,
		contentType : false,
		dataType : "json",
	}).success(function(data) {
		if(null != data && data.code == 1000){
			$("#detailImgSrcE"+num).attr("src",data.url);
			return ;
		}
	}).error(function(data) {
		alert("上传失败");
		return ;
	});
}

function showE(obj){
    var _this = $(obj);//将当前的pimg元素作为_this传入函数  
    imgShow("#outerdivE", "#innerdivE", "#bigimgE", _this);  
}

function editDbProduct(){
	var productId = $("#productIdE").val();
	var productName = $("#productNameE").val();
	var productPrice = $("#productPriceE").val();
	var initAmount = $("#initAmountE").val();
	var salesAmount = $("#salesAmountE").val();
	
	var coverImg ="";
	$(".coverImgSrcE").each(function (index,item){
		var ret = $(item).attr("src");
		if(null != ret && ret != ''){
			coverImg += ret
			coverImg += ",";
		}
	});
	coverImg = coverImg.substring(0, coverImg.length-1);
	var detailImgs ="";
	$(".detailImgSrcE").each(function (index,item){
		var ret = $(item).attr("src");
		if(null != ret && ret != ''){
			detailImgs += ret
			detailImgs += ",";
		}
	});
	detailImgs = detailImgs.substring(0, detailImgs.length-1);
	var img = $("#imgE").attr("src");
	var detail = $("#detailE").val();
	if(null == productName || productName == ''){
		alert("商品名不能为空");
		return false;
	}
	if(null == productPrice || productPrice == ''){
		alert("商品价格不能为空");
		return false;
	}
	if(null == initAmount || initAmount == ''){
		alert("商品总数不能为空");
		return false;
	}
	if(null != salesAmount && salesAmount > initAmount){
		alert("商品总数不能小于销售数量");
		return false;
	}
	
	if(null == coverImg || coverImg == ''){
		alert("商品封面图片不能为空");
		return false;
	}
	if(null == detailImgs || detailImgs == ''){
		alert("商品详情图片不能为空");
		return false;
	}
	if(null == img || img == ''){
		alert("商品缩略图片不能为空");
		return false;
	}
	if(null == detail || detail == ''){
		alert("商品详情不能为空");
		return false;
	}
	
	var data = {
			"productId":productId,"productName":productName,"productPrice":productPrice,"initAmount":initAmount,"coverImg":coverImg,"detailImgs":detailImgs,"detail":detail,"img":img
	}
	$.ajax({
		type : "post",
		data : JSON.stringify(data),
		url : "/product/updateProduct",
		dataType : "json",
		contentType : 'application/json',
		success : function(data) {
			if(null != data && data.code == 1000){
				 window.location.href = "/product/toProductList?pageSize=2&pageNum=1"; 
			}
		}
	});
	
}

